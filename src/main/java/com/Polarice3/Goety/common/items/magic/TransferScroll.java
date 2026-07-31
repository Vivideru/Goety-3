package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.items.ItemBase;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class TransferScroll extends ItemBase {
    public static final String TAG_ENTITY = "Servant";

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    private static void updateTag(ItemStack stack, java.util.function.Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            if (stack.has(DataComponents.CUSTOM_DATA)){
                LivingEntity livingEntity = getSummon(tag(stack));
                if (livingEntity == null || livingEntity.isDeadOrDying()){
                    updateTag(stack, tag -> tag.remove(TAG_ENTITY));
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            if (entity instanceof LivingEntity target) {
                if (stack.getItem() == this) {
                    if (entity instanceof IOwned owned) {
                        if (owned.getTrueOwner() == player) {
                            if (!hasSummon(stack)) {
                                CompoundTag compoundTag = tag(stack);
                                setSummon(compoundTag, target);
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
        if (itemstack.getItem() == this){
            if (hasSummon(itemstack)){
                LivingEntity summon = getSummon(itemstack);
                if (itemstack.has(DataComponents.CUSTOM_DATA)) {
                    if (summon instanceof IOwned owned) {
                        if (!level.isClientSide) {
                            if (owned.getTrueOwner() == player) {
                                if (player.isShiftKeyDown() || player.isCrouching()){
                                    updateTag(itemstack, tag -> tag.remove(TAG_ENTITY));
                                    return InteractionResultHolder.success(itemstack);
                                }
                            } else if (RitualRequirements.canSummon(level, player, summon.getType())){
                                owned.setTrueOwner(player);
                                if (summon instanceof RaiderServant servant && servant.isLeader()) {
                                    ItemStack newBanner = servant.getLeaderBannerInstance();
                                    if (!newBanner.isEmpty()) {
                                        servant.setItemSlot(EquipmentSlot.HEAD, newBanner);
                                    }
                                }
                                player.playSound(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 1.0F);
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 1.0F));
                                itemstack.shrink(1);
                                return InteractionResultHolder.success(itemstack);
                            }
                        }
                    }
                }
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    /*@Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand hand) {
        if (itemStack.getItem() == this){
            if (target instanceof Mob mob) {
                if (hasSummon(itemStack)) {
                    LivingEntity summon = getSummon(itemStack);
                    if (itemStack.getTag() != null) {
                        if (summon instanceof IOwned owned) {
                            if (!player.level.isClientSide) {
                                owned.setTrueOwner(mob);
                                if (summon instanceof RaiderServant servant && servant.isLeader()) {
                                    ItemStack newBanner = servant.getLeaderBannerInstance();
                                    if (!newBanner.isEmpty()) {
                                        servant.setItemSlot(EquipmentSlot.HEAD, newBanner);
                                    }
                                }
                                mob.playSound(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 1.0F);
                                itemStack.shrink(1);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
        }
        return super.interactLivingEntity(itemStack, player, target, hand);
    }*/

    public static boolean hasSummon(ItemStack stack) {
        CompoundTag compoundtag = tag(stack);
        return !compoundtag.isEmpty() && compoundtag.contains(TAG_ENTITY);
    }

    public static void setSummon(CompoundTag compoundTag, LivingEntity livingEntity){
        if (compoundTag != null) {
            if (livingEntity != null) {
                // ItemStack root NBT was removed in 1.21; keep transfer target data in CUSTOM_DATA.
                compoundTag.putUUID(TAG_ENTITY, livingEntity.getUUID());
            }
        }
    }

    public static LivingEntity getSummon(ItemStack stack){
        if (stack.has(DataComponents.CUSTOM_DATA)){
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        addTransferText(stack, tooltip);
    }

    public static void addTransferText(ItemStack stack, List<Component> tooltip){
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            if (!hasSummon(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noServant"));
            } else {
                LivingEntity livingEntity = getSummon(tag(stack));
                if (livingEntity != null){
                    tooltip.add(Component.translatable("info.goety.focus.servant").append(" ")
                            .append(livingEntity.getCustomName() != null ? livingEntity.getCustomName() : livingEntity.getDisplayName())
                            .withStyle(ChatFormatting.GREEN));
                    if (livingEntity instanceof IOwned owned) {
                        if (owned.getMasterOwner() != null) {
                            tooltip.add(Component.translatable("info.goety.focus.owner").append(" ")
                                    .append(owned.getMasterOwner().getCustomName() != null ? owned.getMasterOwner().getCustomName() : owned.getMasterOwner().getDisplayName())
                                    .withStyle(ChatFormatting.GREEN));
                        }
                    }
                }
            }
        }
    }
}
