package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class EnchantItemRitual extends Ritual{

    public EnchantItemRitual(RitualRecipe recipe) {
        super(recipe);
    }

    public boolean isValid(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                           Player castingPlayer, ItemStack activationItem,
                           List<Ingredient> remainingAdditionalIngredients) {
        Holder<Enchantment> enchantment = this.recipe.getEnchantmentHolder();
        return enchantment != null
                // NeoForge item hooks carry custom enchantment rules that 1.21's raw Enchantment.canEnchant tag check cannot see.
                && (activationItem.supportsEnchantment(enchantment)
                || activationItem.getItem() instanceof BookItem
                || activationItem.getItem() instanceof EnchantedBookItem)
                && compatibleEnchant(activationItem)
                && this.areAdditionalIngredientsFulfilled(world, darkAltarPos, castingPlayer, remainingAdditionalIngredients);
    }

    public boolean identify(Level world, BlockPos darkAltarPos, Player player, ItemStack activationItem) {
        // Enchantments can resolve after recipe decoding in 1.21, so identify enchant rituals from the live holder lookup.
        return this.recipe.getEnchantmentHolder() != null
                && this.areAdditionalIngredientsFulfilled(world, darkAltarPos, player, this.recipe.getIngredients());
    }

    public int getLevelCost(ItemStack activationItem){
        Holder<Enchantment> enchantment = this.recipe.getEnchantmentHolder();
        if (activationItem.isEnchanted()){
            ItemEnchantments enchantments = activationItem.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            if (enchantment != null && enchantments.getLevel(enchantment) > 0){
                return this.recipe.getXPLevelCost() * (enchantments.getLevel(enchantment) + 1);
            }
        }
        return this.recipe.getXPLevelCost();
    }

    public boolean compatibleEnchant(ItemStack activationItem){
        Holder<Enchantment> enchantment = this.recipe.getEnchantmentHolder();
        if (activationItem.isEnchanted()){
            ItemEnchantments enchantments = activationItem.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            return enchantment != null && EnchantmentHelper.isEnchantmentCompatible(enchantments.keySet(), enchantment)
                    || activationItem.getItem() instanceof BookItem
                    || activationItem.getItem() instanceof EnchantedBookItem
                    || enchantment != null && enchantments.getLevel(enchantment) > 0;
        } else {
            return true;
        }
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);

        for(int i = 0; i < 20; ++i) {
            double d0 = (double)blockPos.getX() + world.random.nextDouble();
            double d1 = (double)blockPos.getY() + world.random.nextDouble();
            double d2 = (double)blockPos.getZ() + world.random.nextDouble();
            world.addParticle(ParticleTypes.POOF, d0, d1, d2, 0, 0, 0);
        }

        Holder<Enchantment> enchantment = this.recipe.getEnchantmentHolder();
        if (enchantment == null) {
            return;
        }
        ItemEnchantments enchantments = activationItem.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemStack result = activationItem;
        EnchantmentInstance enchantmentInstance = new EnchantmentInstance(enchantment, 1);
        if (result.getItem() instanceof BookItem){
            result = EnchantedBookItem.createForEnchantment(enchantmentInstance);
            activationItem.shrink(1);
            IItemHandler handler = tileEntity.itemStackHandler;
            handler.insertItem(0, result, false);
        } else {
            // Enchantments are stored as Holder-keyed data components in 1.21.
            if (enchantments.getLevel(enchantment) > 0) {
                int j2 = enchantments.getLevel(enchantment) + 1;
                if (j2 > enchantment.value().getMaxLevel()) {
                    j2 = enchantment.value().getMaxLevel();
                }
                int level = j2;
                EnchantmentHelper.updateEnchantments(result, mutable -> mutable.set(enchantment, level));
            } else {
                result.enchant(enchantment, 1);
            }
        }
        result.onCraftedBy(world, castingPlayer, 1);
    }
}
