package com.Polarice3.Goety.common.items.magic;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.events.ArcaTeleporter;
import com.Polarice3.Goety.common.magic.spells.void_spells.TroopSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TroopFocus extends MagicFocus{
    public static final String TAG_ENTITY_TYPE = "Summon Type";

    public TroopFocus() {
        super(new TroopSpell());
    }

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    private static void updateTag(ItemStack stack, java.util.function.Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            if (entity instanceof LivingEntity target) {
                if (stack.getItem() instanceof TroopFocus) {
                    if (entity instanceof IOwned owned) {
                        if (owned.getTrueOwner() == player) {
                            CompoundTag compoundTag = tag(stack);
                            if (!hasSummonType(stack)) {
                                setSummonType(compoundTag, target.getType());
                                setTag(stack, compoundTag);
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
            if (itemstack.getItem() instanceof TroopFocus){
                if (itemstack.has(DataComponents.CUSTOM_DATA)){
                    if (hasSummonType(itemstack)){
                        updateTag(itemstack, tag -> tag.remove(TAG_ENTITY_TYPE));
                    }
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static void call(ServerPlayer player, ItemStack stack){
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag compoundTag = tag(stack);
            if (player.level() instanceof ServerLevel serverLevel) {
                List<LivingEntity> list = new ArrayList<>();
                if (hasSummonType(stack)){
                    EntityType<?> entityType = getSummonType(compoundTag);
                    if (entityType != null){
                        for (Entity entity : serverLevel.getAllEntities()) {
                            if (entity instanceof LivingEntity livingEntity1 && entity.getType() == entityType) {
                                if (MobUtil.getOwner(livingEntity1) != null){
                                    if (MobUtil.getOwner(livingEntity1) == player && !SEHelper.getGroundedEntities(player).contains(livingEntity1)){
                                        list.add(livingEntity1);
                                    }
                                }
                            }
                        }
                    }
                }
                if (!list.isEmpty()) {
                    for (LivingEntity livingEntity1 : list) {
                        LivingEntity original = null;
                        if (livingEntity1.isPassenger() && livingEntity1.getVehicle() instanceof LivingEntity vehicle) {
                            original = livingEntity1;
                            livingEntity1 = vehicle;
                        }
                        if (!livingEntity1.isDeadOrDying()) {
                            BlockPos blockPos = BlockFinder.SummonRadius(player.blockPosition(), livingEntity1, serverLevel);
                            if (player.isShiftKeyDown() || player.isCrouching()) {
                                if (list.size() == 1) {
                                    blockPos = player.blockPosition();
                                }
                            }
                            if (livingEntity1.level().dimension() == player.level().dimension()) {
                                net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(livingEntity1, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                if (event.isCanceled()) {
                                    break;
                                }
                                MobUtil.teleportTracked(livingEntity1, event.getTargetX(), event.getTargetY(), event.getTargetZ());
                                MobUtil.moveDownToGround(livingEntity1);
                                ModNetwork.sendToALL(new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                                ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockPos, SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                                if (original instanceof IServant servant){
                                    servant.setFollowing();
                                }
                                if (livingEntity1 instanceof IServant servant){
                                    servant.setFollowing();
                                }
                            } else if (player.getServer() != null) {
                                ServerLevel serverWorld = player.getServer().getLevel(player.level().dimension());
                                if (serverWorld != null) {
                                    blockPos = BlockFinder.SummonRadius(player.blockPosition(), livingEntity1, serverWorld);
                                    Vec3 vec3 = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                    net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(livingEntity1, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                    if (event.isCanceled()) {
                                        break;
                                    }
                                    // Dimension changes replace non-player entities in 1.21, so continue with the returned tracked instance.
                                    Entity transferred = livingEntity1.changeDimension(ArcaTeleporter.transition(serverWorld, livingEntity1, vec3));
                                    if (!(transferred instanceof LivingEntity transferredLiving)) {
                                        continue;
                                    }
                                    livingEntity1 = transferredLiving;
                                    MobUtil.teleportTracked(livingEntity1, event.getTargetX(), event.getTargetY(), event.getTargetZ());
                                    MobUtil.moveDownToGround(livingEntity1);
                                    ModNetwork.sendToALL(new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                                    if (original instanceof IServant servant){
                                        servant.setFollowing();
                                    }
                                    if (livingEntity1 instanceof IServant servant){
                                        servant.setFollowing();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean hasSummonType(ItemStack stack) {
        CompoundTag compoundtag = tag(stack);
        return !compoundtag.isEmpty() && compoundtag.contains(TAG_ENTITY_TYPE);
    }

    public static void setSummonType(CompoundTag compoundTag, EntityType<?> entityType){
        if (compoundTag != null) {
            if (entityType != null) {
                ResourceLocation name = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
                if (name != null) {
                    // ItemStack root NBT was removed in 1.21; keep troop focus target data in CUSTOM_DATA.
                    compoundTag.putString(TAG_ENTITY_TYPE, name.toString());
                }
            }
        }
    }

    public static EntityType<?> getSummonType(ItemStack stack){
        CompoundTag compoundTag = tag(stack);
        if (compoundTag != null) {
            boolean flag = compoundTag.contains(TAG_ENTITY_TYPE);
            if (flag) {
                return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(compoundTag.getString(TAG_ENTITY_TYPE)));
            }
        }
        return null;
    }

    public static EntityType<?> getSummonType(CompoundTag compoundTag){
        boolean flag = compoundTag.contains(TAG_ENTITY_TYPE);
        if (flag){
            return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(compoundTag.getString(TAG_ENTITY_TYPE)));
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        addCallText(stack, tooltip);
    }

    public static void addCallText(ItemStack stack, List<Component> tooltip){
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            if (!hasSummonType(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noSummonType"));
            } else {
                EntityType<?> entityType = getSummonType(tag(stack));
                if (entityType != null){
                    tooltip.add(Component.translatable("info.goety.focus.summonType").append(" ")
                            .append(entityType.getDescription())
                            .withStyle(ChatFormatting.DARK_GREEN));
                }
            }
        }
    }
}
