package com.Polarice3.Goety.common.items.block;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

final class SkullItemData {
    private SkullItemData() {
    }

    static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    static void update(ItemStack stack, java.util.function.Consumer<CompoundTag> updater) {
        // 1.21 stores item custom NBT in the CUSTOM_DATA component instead of the removed root ItemStack tag.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }
}
