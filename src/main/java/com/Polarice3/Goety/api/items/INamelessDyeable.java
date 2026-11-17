package com.Polarice3.Goety.api.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public interface INamelessDyeable {

    default boolean canDye() {
        return true;
    }

    default boolean hasCustomColor(ItemStack stack) {
        return stack.has(DataComponents.DYED_COLOR);
    }

    default int getColor(ItemStack stack) {
        DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
        return color != null ? color.rgb() : -1;
    }

    default void clearColor(ItemStack stack) {
        stack.remove(DataComponents.DYED_COLOR);
    }

    default void setColor(ItemStack stack, int color) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
    }
}
