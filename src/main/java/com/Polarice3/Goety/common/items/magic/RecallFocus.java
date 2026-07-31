package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ArcaBlock;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.events.ArcaTeleporter;
import com.Polarice3.Goety.common.magic.spells.void_spells.RecallSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;

public class RecallFocus extends MagicFocus{
    public static final String TAG_POS = "RecallPos";
    public static final String TAG_DIMENSION = "RecallDimension";

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    public RecallFocus() {
        super(new RecallSpell());
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        ItemStack stack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        if (player != null) {
            if (!stack.isEmpty() && !hasRecall(stack)) {
                // ItemStack root NBT was removed in 1.21; keep recall target data in CUSTOM_DATA.
                CompoundTag compoundTag = tag(stack);
                if (stack.getItem() instanceof RecallFocus) {
                    BlockPos blockpos = pContext.getClickedPos();
                    BlockEntity tileEntity = world.getBlockEntity(blockpos);
                    if (tileEntity instanceof ArcaBlockEntity arcaTile) {
                        if (pContext.getPlayer() == arcaTile.getPlayer() && arcaTile.getLevel() != null) {
                            this.addRecallTags(arcaTile.getLevel().dimension(), arcaTile.getBlockPos(), compoundTag);
                            setTag(stack, compoundTag);
                            player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                            return InteractionResult.sidedSuccess(world.isClientSide);
                        }
                    }
                    BlockState blockstate = world.getBlockState(blockpos);
                    if (blockstate.is(ModTags.Blocks.RECALL_BLOCKS)) {
                        this.addRecallTags(world.dimension(), blockpos, compoundTag);
                        setTag(stack, compoundTag);
                        player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                        return InteractionResult.sidedSuccess(world.isClientSide);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof RecallFocus){
                if (hasRecall(itemstack)){
                    CustomData.update(DataComponents.CUSTOM_DATA, itemstack, compound -> {
                        compound.remove(TAG_DIMENSION);
                        compound.remove(TAG_POS);
                    });
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static boolean recall(LivingEntity livingEntity, ItemStack stack){
        CompoundTag compoundTag = tag(stack);
        if (hasRecall(stack)) {
            if (getDimension(compoundTag).isPresent() && getRecallBlockPos(compoundTag) != null) {
                BlockPos blockPos = getRecallBlockPos(compoundTag);
                if (blockPos != null) {
                    if (getDimension(compoundTag).get() == livingEntity.level().dimension()) {
                        Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, livingEntity.level(), blockPos);
                        if (optional.isPresent()) {
                            net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(livingEntity, optional.get().x, optional.get().y, optional.get().z);
                            if (event.isCanceled()) {
                                return false;
                            }
                            livingEntity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                            ModNetwork.sendToALL(new SPlayWorldSoundPacket(BlockPos.containing(livingEntity.xo, livingEntity.yo, livingEntity.zo), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                            ModNetwork.sendToALL(new SPlayWorldSoundPacket(BlockPos.containing(optional.get()), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                            return true;
                        }
                    } else {
                        if (livingEntity.getServer() != null) {
                            ServerLevel serverWorld = livingEntity.getServer().getLevel(getDimension(compoundTag).get());
                            if (serverWorld != null) {
                                Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, serverWorld, blockPos);
                                if (optional.isPresent()) {
                                    net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(livingEntity, optional.get().x, optional.get().y, optional.get().z);
                                    if (event.isCanceled()) {
                                        return false;
                                    }
                                    livingEntity.changeDimension(ArcaTeleporter.transition(serverWorld, livingEntity, optional.get()));
                                    livingEntity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasRecall(ItemStack p_40737_) {
        CompoundTag compoundtag = tag(p_40737_);
        return compoundtag.contains(TAG_DIMENSION) || compoundtag.contains(TAG_POS);
    }

    @Nullable
    public static BlockPos getRecallBlockPos(ItemStack itemStack) {
        return getRecallBlockPos(tag(itemStack));
    }

    public static BlockPos getRecallBlockPos(CompoundTag compoundTag){
        boolean flag = compoundTag.contains(TAG_POS);
        boolean flag1 = compoundTag.contains(TAG_DIMENSION);
        if (flag && flag1) {
            Optional<ResourceKey<Level>> optional = getDimension(compoundTag);
            if (optional.isPresent()) {
                return NbtUtils.readBlockPos(compoundTag, TAG_POS).orElse(null);
            }
        }
        return null;
    }

    public static Optional<ResourceKey<Level>> getDimension(CompoundTag p_40728_) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get(TAG_DIMENSION)).result();
    }

    public void addRecallTags(ResourceKey<Level> p_40733_, BlockPos p_40734_, CompoundTag p_40735_) {
        p_40735_.put(TAG_POS, NbtUtils.writeBlockPos(p_40734_));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, p_40733_).resultOrPartial(Goety.LOGGER::error).ifPresent((p_40731_) -> {
            p_40735_.put(TAG_DIMENSION, p_40731_);
        });
    }

    public static boolean isValid(ServerLevel serverLevel, ItemStack stack){
        CompoundTag compoundTag = tag(stack);
        if (hasRecall(stack)) {
            if (getDimension(compoundTag).isPresent()) {
                ServerLevel serverLevel1 = serverLevel.getServer().getLevel(getDimension(compoundTag).get());
                if (serverLevel1 != null) {
                    if (getRecallBlockPos(compoundTag) != null) {
                        BlockPos blockPos = getRecallBlockPos(compoundTag);
                        if (blockPos != null) {
                            BlockState blockState = serverLevel1.getBlockState(blockPos);
                            Block block = blockState.getBlock();
                            return block instanceof ArcaBlock || blockState.is(ModTags.Blocks.RECALL_BLOCKS);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        addRecallText(stack, tooltip);
    }

    public static void addRecallText(ItemStack stack, List<Component> tooltip){
        CompoundTag compoundTag = tag(stack);
        if (!compoundTag.isEmpty()) {
            if (!hasRecall(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noPos"));
            } else {
                BlockPos blockPos = getRecallBlockPos(compoundTag);
                if (blockPos != null && getDimension(compoundTag).isPresent()) {
                    tooltip.add(Component.translatable("info.goety.focus.Pos").append(" ").append(Component.translatable("info.goety.focus.PosNum", blockPos.getX(), blockPos.getY(), blockPos.getZ())));
                    tooltip.add(Component.translatable("info.goety.focus.PosDim", getDimension(compoundTag).get().location().toString()));
                }
            }
        }
    }

}
