package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class ModBlockEnchantmentHelper {
    private ModBlockEnchantmentHelper() {
    }

    public static boolean hasFrostWalker(LivingEntity entity) {
        // 1.21 removed EnchantmentHelper.hasFrostWalker, so keep the old behavior by checking the Frost Walker enchantment on boots directly.
        Holder<Enchantment> frostWalker = entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FROST_WALKER);
        return EnchantmentHelper.getItemEnchantmentLevel(frostWalker, entity.getItemBySlot(EquipmentSlot.FEET)) > 0;
    }
}
