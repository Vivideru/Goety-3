package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.entities.IMobTyped;
import com.Polarice3.Goety.init.ModMobType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;

public class MobTypeHelper {

    public static ModMobType getMobType(LivingEntity livingEntity) {
        // Minecraft 1.21 removed LivingEntity#getMobType, so lich players are mapped here to preserve the old mixin behavior.
        if (LichdomHelper.isLich(livingEntity)) {
            return ModMobType.UNDEAD;
        }
        if (livingEntity instanceof IMobTyped mobTyped) {
            return mobTyped.getGoetyMobType();
        }
        if (livingEntity.getType().is(EntityTypeTags.UNDEAD)) {
            return ModMobType.UNDEAD;
        }
        if (livingEntity.getType().is(EntityTypeTags.ARTHROPOD)) {
            return ModMobType.ARTHROPOD;
        }
        if (livingEntity.getType().is(EntityTypeTags.ILLAGER)) {
            return ModMobType.ILLAGER;
        }
        if (livingEntity.getType().is(EntityTypeTags.AQUATIC)) {
            return ModMobType.WATER;
        }
        return ModMobType.UNDEFINED;
    }

    public static boolean isMobType(LivingEntity livingEntity, ModMobType mobType) {
        return getMobType(livingEntity) == mobType;
    }
}
