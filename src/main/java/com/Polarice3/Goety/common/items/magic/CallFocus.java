package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.events.ArcaTeleporter;
import com.Polarice3.Goety.common.magic.spells.void_spells.CallSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class CallFocus extends MagicFocus{
    public static final String TAG_ENTITY = "Summoned";

    public CallFocus() {
        super(new CallSpell());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            if (hasSummon(stack)){
                LivingEntity livingEntity = getSummon(stack);
                if (livingEntity == null || livingEntity.isDeadOrDying()){
                    clearSummon(stack);
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            if (entity instanceof LivingEntity target) {
                if (stack.getItem() instanceof CallFocus) {
                    if (entity instanceof IOwned owned) {
                        if (owned.getTrueOwner() == player) {
                            if (!hasSummon(stack)) {
                                setSummon(stack, target);
                                player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof CallFocus){
                if (hasSummon(itemstack)){
                    clearSummon(itemstack);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static void call(ServerPlayer player, ItemStack stack){
        if (hasSummon(stack)) {
            CompoundTag compoundTag = tag(stack);
            LivingEntity livingEntity = getSummon(compoundTag);
            if (player.level() instanceof ServerLevel serverLevel) {
                if (livingEntity != null) {
                    LivingEntity original = null;
                    if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity vehicle) {
                        original = livingEntity;
                        livingEntity = vehicle;
                    }
                    if (!livingEntity.isDeadOrDying()) {
                        BlockPos blockPos = BlockFinder.SummonRadius(player.blockPosition(), livingEntity, serverLevel);
                        if (livingEntity.level().dimension() == player.level().dimension()) {
                            net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(livingEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            if (event.isCanceled()) {
                                return;
                            }
                            livingEntity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                            MobUtil.moveDownToGround(livingEntity);
                            ModNetwork.sendToALL(new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                            ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockPos, SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                            if (original instanceof IServant servant){
                                servant.setFollowing();
                            }
                            if (livingEntity instanceof IServant servant){
                                servant.setFollowing();
                            }
                        } else if (player.getServer() != null) {
                            ServerLevel serverWorld = player.getServer().getLevel(player.level().dimension());
                            if (serverWorld != null) {
                                blockPos = BlockFinder.SummonRadius(player.blockPosition(), livingEntity, serverWorld);
                                Vec3 vec3 = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(livingEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                if (event.isCanceled()) {
                                    return;
                                }
                                livingEntity.changeDimension(ArcaTeleporter.transition(serverWorld, livingEntity, vec3));
                                livingEntity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                                MobUtil.moveDownToGround(livingEntity);
                                ModNetwork.sendToALL(new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                                if (original instanceof IServant servant){
                                    servant.setFollowing();
                                }
                                if (livingEntity instanceof IServant servant){
                                    servant.setFollowing();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean hasSummon(ItemStack stack) {
        return tag(stack).contains(TAG_ENTITY);
    }

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void clearSummon(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, compoundTag -> compoundTag.remove(TAG_ENTITY));
    }

    private static void setSummon(ItemStack stack, LivingEntity livingEntity) {
        // ItemStack root NBT was removed in 1.21; keep the stored summon UUID in CUSTOM_DATA.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, compoundTag -> setSummon(compoundTag, livingEntity));
    }

    public static void setSummon(CompoundTag compoundTag, LivingEntity livingEntity){
        if (compoundTag != null) {
            if (livingEntity != null) {
                compoundTag.putUUID(TAG_ENTITY, livingEntity.getUUID());
            }
        }
    }

    public static LivingEntity getSummon(ItemStack stack){
        if (hasSummon(stack)){
            return getSummon(tag(stack));
        }
        return null;
    }

    public static LivingEntity getSummon(CompoundTag compoundTag){
        boolean flag = compoundTag.contains(TAG_ENTITY);
        if (flag){
            UUID uuid = compoundTag.getUUID(TAG_ENTITY);
            return EntityFinder.getLivingEntityByUuiD(uuid);
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        addCallText(stack, tooltip);
    }

    public static void addCallText(ItemStack stack, List<Component> tooltip){
        if (hasSummon(stack)) {
            if (!hasSummon(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noSummon"));
            } else {
                LivingEntity livingEntity = getSummon(stack);
                if (livingEntity != null){
                    tooltip.add(Component.translatable("info.goety.focus.summon").append(" ")
                            .append(livingEntity.getCustomName() != null ? livingEntity.getCustomName() : livingEntity.getDisplayName())
                            .withStyle(ChatFormatting.GREEN));
                }
            }
        } else {
            tooltip.add(Component.translatable("info.goety.focus.noSummon"));
        }
    }
}
