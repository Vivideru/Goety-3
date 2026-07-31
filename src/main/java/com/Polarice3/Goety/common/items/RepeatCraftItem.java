package com.Polarice3.Goety.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;

import org.jetbrains.annotations.NotNull;

public class RepeatCraftItem extends Item implements IItemExtension {
    public RepeatCraftItem(Properties properties){
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @NotNull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return itemStack.copyWithCount(1);
    }
}
