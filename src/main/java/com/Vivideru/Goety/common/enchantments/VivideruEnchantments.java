package com.Vivideru.Goety.common.enchantments;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class VivideruEnchantments {
    public static final ResourceKey<Enchantment> HOMING = ResourceKey.create(Registries.ENCHANTMENT, Goety.location("homing"));

    private VivideruEnchantments() {
    }
}
