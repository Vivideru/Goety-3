package com.Polarice3.Goety.api.items.magic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;

public interface ISoulContainer {
    String SOULS_AMOUNT = "Souls";

    default boolean hasMaxAmount() {
        return false;
    }

    default int getMaxSouls() {
        return 0;
    }

    static CompoundTag data(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    static boolean isEmpty(ItemStack stack) {
        return currentSouls(stack) <= 0;
    }

    static int currentSouls(ItemStack stack) {
        return data(stack).getInt(SOULS_AMOUNT);
    }

    static void setSoulsAmount(ItemStack stack, int souls) {
        if (stack.getItem() instanceof ISoulContainer) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(SOULS_AMOUNT, Math.max(souls, 0)));
        }
    }

    static void decreaseSouls(ItemStack stack, int souls) {
        if (stack.getItem() instanceof ISoulContainer) {
            setSoulsAmount(stack, Math.max(currentSouls(stack) - souls, 0));
        }
    }

    static void increaseSouls(ItemStack stack, int souls) {
        if (stack.getItem() instanceof ISoulContainer container) {
            int total = currentSouls(stack) + souls;
            if (container.hasMaxAmount()) {
                total = Math.min(total, container.getMaxSouls());
            }
            setSoulsAmount(stack, total);
        }
    }
}
