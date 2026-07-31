package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class ModToolItems {
    private static float getTaggedSpeedFallback(float speed, BlockState blockState, TagKey<Block> mineableTag) {
        // NeoForge 1.21 routes mining speed through the TOOL component; keep legacy tier speed if that component falls back to hand speed.
        return speed <= 1.0F && blockState.is(mineableTag) ? ModTiers.DARK.getSpeed() : speed;
    }

    private static int getDarkToolsDurability() {
        return ConfiguredItemUtil.durability(ItemConfig.DarkToolsDurability, ModTiers.DARK.getUses());
    }

    public static class DarkSwordItem extends SwordItem implements IPersist {

        public DarkSwordItem() {
            // In 1.21 the tier no longer injects durability into these custom properties, and the enchanting table rejects non-damageable items.
            super(ModTiers.DARK, ModItems.baseProperties().durability(ModTiers.DARK.getUses()).attributes(SwordItem.createAttributes(ModTiers.DARK, 3, -2.4F)));
        }

        @Override
        public int getMaxDamage(ItemStack stack) {
            // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
            return getDarkToolsDurability();
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return this.isDamaged(stack);
        }

        public int getBarColor(ItemStack stack) {
            if (this.isBroken(stack)) {
                return 0x800000;
            }
            return super.getBarColor(stack);
        }

        @Override
        public int getBarWidth(ItemStack stack){
            if (this.isBroken(stack)) {
                return 13;
            }
            return super.getBarWidth(stack);
        }

        public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
            if (ItemConfig.DarkToolsPersist.get()) {
                if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
                    if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                        stack.setDamageValue(stack.getMaxDamage() - 1);
                        onBroken.accept(this);
                    }
                    return 0;
                }
            }
            return amount;
        }

        @Override
        public boolean isBroken(ItemStack stack) {
            return IPersist.super.isBroken(stack) && ItemConfig.DarkToolsPersist.get();
        }

        public float getDestroySpeed(ItemStack stack, BlockState blockState) {
            if (this.isNotBroken(stack) || !ItemConfig.DarkToolsPersist.get()) {
                return super.getDestroySpeed(stack, blockState);
            }
            return 1.0F;
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }

        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
            super.appendHoverText(stack, context, tooltip, flagIn);
            if (ItemConfig.DarkToolsPersist.get() && this.isBroken(stack)) {
                // 1.21 removed the stack-aware attribute override; keep the visible broken state while the component-based replacement is rebuilt.
                tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    public static class DarkShovelItem extends ShovelItem implements IPersist {

        public DarkShovelItem() {
            // In 1.21 the tier no longer injects durability into these custom properties, and the enchanting table rejects non-damageable items.
            super(ModTiers.DARK, ModItems.baseProperties().durability(ModTiers.DARK.getUses()).attributes(DiggerItem.createAttributes(ModTiers.DARK, 1.5F, -3.0F)));
        }

        @Override
        public int getMaxDamage(ItemStack stack) {
            // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
            return getDarkToolsDurability();
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return this.isDamaged(stack);
        }

        public int getBarColor(ItemStack stack) {
            if (this.isBroken(stack)) {
                return 0x800000;
            }
            return super.getBarColor(stack);
        }

        @Override
        public int getBarWidth(ItemStack stack){
            if (this.isBroken(stack)) {
                return 13;
            }
            return super.getBarWidth(stack);
        }

        public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
            if (ItemConfig.DarkToolsPersist.get()) {
                if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
                    if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                        stack.setDamageValue(stack.getMaxDamage() - 1);
                        onBroken.accept(this);
                    }
                    return 0;
                }
            }
            return amount;
        }

        @Override
        public boolean isBroken(ItemStack stack) {
            return IPersist.super.isBroken(stack) && ItemConfig.DarkToolsPersist.get();
        }

        public float getDestroySpeed(ItemStack stack, BlockState blockState) {
            if (this.isNotBroken(stack) || !ItemConfig.DarkToolsPersist.get()) {
                return getTaggedSpeedFallback(super.getDestroySpeed(stack, blockState), blockState, BlockTags.MINEABLE_WITH_SHOVEL);
            }
            return 1.0F;
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }

        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
            super.appendHoverText(stack, context, tooltip, flagIn);
            if (ItemConfig.DarkToolsPersist.get() && this.isBroken(stack)) {
                // 1.21 removed the stack-aware attribute override; keep the visible broken state while the component-based replacement is rebuilt.
                tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    public static class DarkPickaxeItem extends PickaxeItem implements IPersist {

        public DarkPickaxeItem() {
            // In 1.21 the tier no longer injects durability into these custom properties, and the enchanting table rejects non-damageable items.
            super(ModTiers.DARK, ModItems.baseProperties().durability(ModTiers.DARK.getUses()).attributes(DiggerItem.createAttributes(ModTiers.DARK, 1, -2.8F)));
        }

        @Override
        public int getMaxDamage(ItemStack stack) {
            // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
            return getDarkToolsDurability();
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return this.isDamaged(stack);
        }

        public int getBarColor(ItemStack stack) {
            if (this.isBroken(stack)) {
                return 0x800000;
            }
            return super.getBarColor(stack);
        }

        @Override
        public int getBarWidth(ItemStack stack){
            if (this.isBroken(stack)) {
                return 13;
            }
            return super.getBarWidth(stack);
        }

        public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
            if (ItemConfig.DarkToolsPersist.get()) {
                if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
                    if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                        stack.setDamageValue(stack.getMaxDamage() - 1);
                        onBroken.accept(this);
                    }
                    return 0;
                }
            }
            return amount;
        }

        @Override
        public boolean isBroken(ItemStack stack) {
            return IPersist.super.isBroken(stack) && ItemConfig.DarkToolsPersist.get();
        }

        public float getDestroySpeed(ItemStack stack, BlockState blockState) {
            if (this.isNotBroken(stack) || !ItemConfig.DarkToolsPersist.get()) {
                return getTaggedSpeedFallback(super.getDestroySpeed(stack, blockState), blockState, BlockTags.MINEABLE_WITH_PICKAXE);
            }
            return 1.0F;
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }

        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
            super.appendHoverText(stack, context, tooltip, flagIn);
            if (ItemConfig.DarkToolsPersist.get() && this.isBroken(stack)) {
                // 1.21 removed the stack-aware attribute override; keep the visible broken state while the component-based replacement is rebuilt.
                tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    public static class DarkAxeItem extends AxeItem implements IPersist {

        public DarkAxeItem() {
            // In 1.21 the tier no longer injects durability into these custom properties, and the enchanting table rejects non-damageable items.
            super(ModTiers.DARK, ModItems.baseProperties().durability(ModTiers.DARK.getUses()).attributes(DiggerItem.createAttributes(ModTiers.DARK, 5.0F, -3.0F)));
        }

        @Override
        public int getMaxDamage(ItemStack stack) {
            // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
            return getDarkToolsDurability();
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return this.isDamaged(stack);
        }

        public int getBarColor(ItemStack stack) {
            if (this.isBroken(stack)) {
                return 0x800000;
            }
            return super.getBarColor(stack);
        }

        @Override
        public int getBarWidth(ItemStack stack){
            if (this.isBroken(stack)) {
                return 13;
            }
            return super.getBarWidth(stack);
        }

        public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
            if (ItemConfig.DarkToolsPersist.get()) {
                if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
                    if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                        stack.setDamageValue(stack.getMaxDamage() - 1);
                        onBroken.accept(this);
                    }
                    return 0;
                }
            }
            return amount;
        }

        @Override
        public boolean isBroken(ItemStack stack) {
            return IPersist.super.isBroken(stack) && ItemConfig.DarkToolsPersist.get();
        }

        public float getDestroySpeed(ItemStack stack, BlockState blockState) {
            if (this.isNotBroken(stack) || !ItemConfig.DarkToolsPersist.get()) {
                return getTaggedSpeedFallback(super.getDestroySpeed(stack, blockState), blockState, BlockTags.MINEABLE_WITH_AXE);
            }
            return 1.0F;
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }

        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
            super.appendHoverText(stack, context, tooltip, flagIn);
            if (ItemConfig.DarkToolsPersist.get() && this.isBroken(stack)) {
                // 1.21 removed the stack-aware attribute override; keep the visible broken state while the component-based replacement is rebuilt.
                tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    public static class DarkHoeItem extends HoeItem implements IPersist {

        public DarkHoeItem() {
            // In 1.21 the tier no longer injects durability into these custom properties, and the enchanting table rejects non-damageable items.
            super(ModTiers.DARK, ModItems.baseProperties().durability(ModTiers.DARK.getUses()).attributes(DiggerItem.createAttributes(ModTiers.DARK, -3, 0.0F)));
        }

        @Override
        public int getMaxDamage(ItemStack stack) {
            // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
            return getDarkToolsDurability();
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            return this.isDamaged(stack);
        }

        public int getBarColor(ItemStack stack) {
            if (this.isBroken(stack)) {
                return 0x800000;
            }
            return super.getBarColor(stack);
        }

        @Override
        public int getBarWidth(ItemStack stack){
            if (this.isBroken(stack)) {
                return 13;
            }
            return super.getBarWidth(stack);
        }

        public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
            if (ItemConfig.DarkToolsPersist.get()) {
                if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
                    if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                        stack.setDamageValue(stack.getMaxDamage() - 1);
                        onBroken.accept(this);
                    }
                    return 0;
                }
            }
            return amount;
        }

        @Override
        public boolean isBroken(ItemStack stack) {
            return IPersist.super.isBroken(stack) && ItemConfig.DarkToolsPersist.get();
        }

        public float getDestroySpeed(ItemStack stack, BlockState blockState) {
            if (this.isNotBroken(stack) || !ItemConfig.DarkToolsPersist.get()) {
                return getTaggedSpeedFallback(super.getDestroySpeed(stack, blockState), blockState, BlockTags.MINEABLE_WITH_HOE);
            }
            return 1.0F;
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }

        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
            super.appendHoverText(stack, context, tooltip, flagIn);
            if (ItemConfig.DarkToolsPersist.get() && this.isBroken(stack)) {
                // 1.21 removed the stack-aware attribute override; keep the visible broken state while the component-based replacement is rebuilt.
                tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
            }
        }
    }
}
