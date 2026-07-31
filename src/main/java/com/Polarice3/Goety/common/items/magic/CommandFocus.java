package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.magic.spells.utility.CommandSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

import java.util.List;
import java.util.UUID;

public class CommandFocus extends MagicFocus{
    public static final String TAG_ENTITY = "Servant";
    public static final String TAG_ENTITY_CLIENT = "ServantClient";

    public CommandFocus() {
        super(new CommandSpell());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            if (hasServant(stack)){
                LivingEntity livingEntity = getServant(stack);
                if (livingEntity == null || livingEntity.isDeadOrDying()){
                    clearServant(stack);
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            if (entity instanceof LivingEntity target) {
                if (stack.getItem() instanceof CommandFocus) {
                    if (entity instanceof IServant owned && owned.canBeCommanded()) {
                        if (owned.getTrueOwner() == player) {
                            if (!hasServant(stack)) {
                                setServant(stack, target);
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
            if (itemstack.getItem() instanceof CommandFocus){
                if (hasServant(itemstack)){
                    clearServant(itemstack);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return hasServant(p_41453_);
    }

    public static boolean hasServant(ItemStack stack) {
        return tag(stack).contains(TAG_ENTITY);
    }

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void clearServant(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, compoundTag -> {
            compoundTag.remove(TAG_ENTITY);
            compoundTag.remove(TAG_ENTITY_CLIENT);
        });
    }

    public static void setServant(ItemStack stack, LivingEntity livingEntity) {
        // ItemStack root NBT was removed in 1.21; keep the commanded servant ids in CUSTOM_DATA.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, compoundTag -> setServant(compoundTag, livingEntity));
    }

    public static void setServant(CompoundTag compoundTag, LivingEntity livingEntity){
        if (compoundTag != null) {
            if (livingEntity != null) {
                compoundTag.putUUID(TAG_ENTITY, livingEntity.getUUID());
                compoundTag.putInt(TAG_ENTITY_CLIENT, livingEntity.getId());
            }
        }
    }

    public static LivingEntity getServant(ItemStack stack) {
        return hasServant(stack) ? getServant(tag(stack)) : null;
    }

    public static LivingEntity getServant(CompoundTag compoundTag){
        boolean flag = compoundTag.contains(TAG_ENTITY);
        if (flag){
            UUID uuid = compoundTag.getUUID(TAG_ENTITY);
            return EntityFinder.getLivingEntityByUuiD(uuid);
        }
        return null;
    }

    public static LivingEntity getServantClient(Level level, ItemStack stack) {
        CompoundTag compoundtag = tag(stack);
        return compoundtag.contains(TAG_ENTITY_CLIENT) ? getServantClient(level, compoundtag) : null;
    }

    public static LivingEntity getServantClient(Level level, CompoundTag compoundTag){
        boolean flag = compoundTag.contains(TAG_ENTITY_CLIENT);
        if (flag){
            return level.getEntity(compoundTag.getInt(TAG_ENTITY_CLIENT)) instanceof LivingEntity livingEntity ? livingEntity : null;
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        addCommandText(context.level(), stack, tooltip);
    }

    public static void addCommandText(Level level, ItemStack stack, List<Component> tooltip){
        if (tag(stack).contains(TAG_ENTITY) || tag(stack).contains(TAG_ENTITY_CLIENT)) {
            if (hasServant(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noServant"));
            } else if (level != null) {
                LivingEntity livingEntity = getServantClient(level, tag(stack));
                if (livingEntity != null){
                    tooltip.add(Component.translatable("info.goety.focus.servant").append(" ")
                            .append(livingEntity.getCustomName() != null ? livingEntity.getCustomName() : livingEntity.getDisplayName())
                            .withStyle(ChatFormatting.GREEN));
                }
            }
        }
    }
}
