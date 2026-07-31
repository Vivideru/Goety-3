package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;

import java.util.List;
import java.util.function.Consumer;

public class SeaAmuletItem extends SingleStackItem{
    private static final String CONDUIT_CHARGES = "Conduit Charges";

    private static int maxPower() {
        // Config values are unavailable during class loading, so the amulet capacity is read when the stack updates.
        return ItemConfig.SeaAmuletMax.get();
    }

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void updateTag(ItemStack stack, Consumer<CompoundTag> updater) {
        // ItemStack root NBT was removed in 1.21; mutate a CUSTOM_DATA copy and write it back.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof Player player) {
            boolean flag = true;
            if (!tag(stack).contains(CONDUIT_CHARGES)) {
                updateTag(stack, compound -> compound.putInt(CONDUIT_CHARGES, 0));
            } else {
                if (this.getConduitChargesAmount(stack) > maxPower()){
                    this.setConduitCharges(stack, maxPower());
                } else if (this.getConduitChargesAmount(stack) < 0){
                    this.setConduitCharges(stack, 0);
                }
                if (CuriosFinder.hasCurio(player, this)){
                    if (player.isUnderWater()) {
                        BlockEntity blockEntity = BlockFinder.findBlockEntity(BlockEntityType.CONDUIT, worldIn, player.blockPosition(), 8);
                        if (blockEntity instanceof ConduitBlockEntity blockEntity1) {
                            if (blockEntity1.isActive()) {
                                if (this.getConduitChargesAmount(stack) < maxPower()) {
                                    if (worldIn instanceof ServerLevel serverLevel) {
                                        ServerParticleUtil.gatheringParticles(ParticleTypes.NAUTILUS, player, serverLevel);
                                    }
                                    this.increaseConduitCharges(stack);
                                }
                                flag = false;
                            }
                        }
                        int duration = MathHelper.secondsToTicks(5);
                        if (player.hasEffect(MobEffects.CONDUIT_POWER)){
                            MobEffectInstance mobEffectInstance = player.getEffect(MobEffects.CONDUIT_POWER);
                            flag = mobEffectInstance != null && mobEffectInstance.getDuration() < duration;
                        }
                        if (flag && this.getConduitChargesAmount(stack) > 0 && MobUtil.validNonLich(player)) {
                            this.decreaseConduitCharges(stack);
                            if (!worldIn.isClientSide){
                                if (player instanceof ServerPlayer serverPlayer){
                                    ModNetwork.sendToClient(serverPlayer, new SPlayPlayerSoundPacket(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, 1.0F, 1.0F));
                                }
                            }
                            player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, duration * 2, 0, false, false));
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        updateTag(pStack, compound -> compound.putInt(CONDUIT_CHARGES, 0));
    }

    public void increaseConduitCharges(ItemStack stack){
        if (tag(stack).contains(CONDUIT_CHARGES) && getConduitChargesAmount(stack) < maxPower()) {
            updateTag(stack, compound -> compound.putInt(CONDUIT_CHARGES, Math.min(maxPower(), getConduitChargesAmount(stack) + ItemConfig.SeaAmuletChargeConsume.get())));
        }
    }

    public void decreaseConduitCharges(ItemStack stack){
        if (tag(stack).contains(CONDUIT_CHARGES) && getConduitChargesAmount(stack) > 0) {
            updateTag(stack, compound -> compound.putInt(CONDUIT_CHARGES, Math.max(0, getConduitChargesAmount(stack) - ItemConfig.SeaAmuletChargeConsume.get())));
        }
    }

    public void setConduitCharges(ItemStack stack, int charges){
        if (tag(stack).contains(CONDUIT_CHARGES)) {
            updateTag(stack, compound -> compound.putInt(CONDUIT_CHARGES, charges));
        }
    }

    public int getConduitChargesAmount(ItemStack stack) {
        return tag(stack).getInt(CONDUIT_CHARGES);
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (1.0F - amountColor(stack))/2.0F);
        return Mth.hsvToRgb(f, 1.0F, f);
    }

    public double amountColor(ItemStack stack){
        if (tag(stack).contains(CONDUIT_CHARGES)) {
            int i = tag(stack).getInt(CONDUIT_CHARGES);
            return 1.0D - (i / (double) maxPower());
        } else {
            return 1.0D;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return tag(stack).contains(CONDUIT_CHARGES);
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (tag(stack).contains(CONDUIT_CHARGES)) {
            int power = tag(stack).getInt(CONDUIT_CHARGES);
            return Math.round((power * 13.0F / maxPower()));
        } else {
            return 0;
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (tag(stack).contains(CONDUIT_CHARGES)) {
            int conduit = tag(stack).getInt(CONDUIT_CHARGES);
            tooltip.add(Component.translatable("info.goety.sea_amulet.amount", conduit));
        } else {
            tooltip.add(Component.translatable("info.goety.sea_amulet.amount", 0));
        }
    }
}
