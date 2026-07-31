package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class PendantOfHungerItem extends SingleStackItem {
    private static final String ROTTEN_FLESH = "Rotten Flesh Count";

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
            if (!tag(stack).contains(ROTTEN_FLESH)) {
                updateTag(stack, compound -> compound.putInt(ROTTEN_FLESH, 0));
            } else if (CuriosFinder.hasCurio(player, this)){
                if (getRottenFleshAmount(stack) < ItemConfig.PendantOfHungerLimit.get()) {
                    if (!ItemHelper.findItem(player, Items.ROTTEN_FLESH).isEmpty()) {
                        increaseRottenFlesh(stack);
                        ItemHelper.findItem(player, Items.ROTTEN_FLESH).shrink(1);
                    }
                }
                if (getRottenFleshAmount(stack) > 0) {
                    if (MobUtil.validNonLich(player)) {
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0, false, false));
                        if (player.getFoodData().needsFood()) {
                            player.eat(player.level(), new ItemStack(Items.ROTTEN_FLESH));
                            decreaseRottenFlesh(stack);
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        updateTag(pStack, compound -> compound.putInt(ROTTEN_FLESH, 0));
    }

    public void increaseRottenFlesh(ItemStack stack){
        updateTag(stack, compound -> compound.putInt(ROTTEN_FLESH, getRottenFleshAmount(stack) + 1));
    }

    public void decreaseRottenFlesh(ItemStack stack){
        updateTag(stack, compound -> compound.putInt(ROTTEN_FLESH, getRottenFleshAmount(stack) - 1));
    }

    public int getRottenFleshAmount(ItemStack stack) {
        return tag(stack).getInt(ROTTEN_FLESH);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (1.0F - amountColor(stack))/2.0F);
        return Mth.hsvToRgb(1.0F, f, f);
    }

    public double amountColor(ItemStack stack){
        if (tag(stack).contains(ROTTEN_FLESH)) {
            int i = tag(stack).getInt(ROTTEN_FLESH);
            return 1.0D - (i / (double) ItemConfig.PendantOfHungerLimit.get());
        } else {
            return 1.0D;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return tag(stack).contains(ROTTEN_FLESH);
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (tag(stack).contains(ROTTEN_FLESH)) {
            int power = tag(stack).getInt(ROTTEN_FLESH);
            return Math.round((power * 13.0F / ItemConfig.PendantOfHungerLimit.get()));
        } else {
            return 0;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (tag(stack).contains(ROTTEN_FLESH)) {
            int rottenFlesh = tag(stack).getInt(ROTTEN_FLESH);
            tooltip.add(Component.translatable("info.goety.hunger_pendent.amount", rottenFlesh));
        } else {
            tooltip.add(Component.translatable("info.goety.hunger_pendent.amount", 0));
        }
    }

}
