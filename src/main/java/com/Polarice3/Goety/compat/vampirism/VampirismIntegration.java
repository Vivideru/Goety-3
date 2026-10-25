package com.Polarice3.Goety.compat.vampirism;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class VampirismIntegration {
    private static final ResourceLocation SUNSCREEN = ResourceLocation.fromNamespaceAndPath("vampirism", "sunscreen");

    public static boolean hasSunscreen(LivingEntity livingEntity) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(SUNSCREEN).map(livingEntity::hasEffect).orElse(false);
    }
}
