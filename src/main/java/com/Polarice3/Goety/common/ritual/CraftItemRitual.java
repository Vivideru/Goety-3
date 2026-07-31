package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class CraftItemRitual extends Ritual{

    public CraftItemRitual(RitualRecipe recipe) {
        super(recipe);
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);

        ItemStack result = this.recipe.getResultItem(world.registryAccess()).copy();

        if (activationItem.getItem() instanceof IWand && result.getItem() instanceof IWand){
            SoulUsingItemHandler initWand = SoulUsingItemHandler.get(activationItem);
            SoulUsingItemHandler resultWand = SoulUsingItemHandler.get(result);

            resultWand.insertItem(initWand.getStackInSlot(0));
        }
        if (MainConfig.RitualCraftEnchant.get()) {
            if (activationItem.isEnchanted()) {
                ItemEnchantments enchants = activationItem.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchants.entrySet()) {
                    // NeoForge item hooks carry custom enchantment rules that 1.21's raw Enchantment.canEnchant tag check cannot see.
                    if (result.supportsEnchantment(enchantment.getKey())) {
                        EnchantmentHelper.updateEnchantments(result, mutable -> mutable.set(enchantment.getKey(), enchantment.getIntValue()));
                    }
                }
            }
        }
        if (MainConfig.RitualCraftDamage.get()) {
            if (result.isDamageableItem()) {
                float percent = 1.0F - (float) (activationItem.getMaxDamage() - activationItem.getDamageValue()) / activationItem.getMaxDamage();
                int damage = (int) (result.getMaxDamage() * percent);
                result.setDamageValue(damage);
            }
        }
        activationItem.shrink(1);
        result.onCraftedBy(world, castingPlayer, 1);
        IItemHandler handler = tileEntity.itemStackHandler;
        handler.insertItem(0, result, false);
    }
}
