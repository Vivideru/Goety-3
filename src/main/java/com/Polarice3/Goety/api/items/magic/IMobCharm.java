package com.Polarice3.Goety.api.items.magic;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * A charm a servant can carry and use on its own, with a cooldown stored on the stack.
 */
public interface IMobCharm {
    String COOL_DOWN = "CoolDown";

    default void charmTick(ItemStack itemStack) {
        if (!isNotOnCoolDown(itemStack)) {
            decreaseCoolDown(itemStack);
        }
    }

    static int currentCool(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(COOL_DOWN);
    }

    static boolean isNotOnCoolDown(ItemStack itemStack) {
        return currentCool(itemStack) == 0;
    }

    static void setCoolDown(ItemStack itemStack, int cool) {
        if (itemStack.getItem() instanceof IMobCharm) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.putInt(COOL_DOWN, Math.max(cool, 0)));
        }
    }

    static void decreaseCoolDown(ItemStack itemStack) {
        if (itemStack.getItem() instanceof IMobCharm && !isNotOnCoolDown(itemStack)) {
            setCoolDown(itemStack, currentCool(itemStack) - 1);
        }
    }

    default boolean mobShouldUse(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        return isNotOnCoolDown(itemStack);
    }

    default void mobUse(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
    }
}
