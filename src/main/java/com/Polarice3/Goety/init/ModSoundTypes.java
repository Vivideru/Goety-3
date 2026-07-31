package com.Polarice3.Goety.init;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class ModSoundTypes {
    public static final SoundType URN = new DeferredSoundType(1.0F, 1.0F,
            ModSounds.URN_BREAK,
            () -> SoundEvents.GLASS_STEP,
            () -> SoundEvents.GLASS_PLACE,
            () -> SoundEvents.GLASS_HIT,
            () -> SoundEvents.GLASS_FALL);

    public static final SoundType MOD_METAL = new DeferredSoundType(1.0F, 1.0F,
            () -> SoundEvents.METAL_BREAK,
            () -> SoundEvents.EMPTY,
            () -> SoundEvents.METAL_PLACE,
            () -> SoundEvents.METAL_HIT,
            () -> SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);

    public static final SoundType CHORUS_GRASS = new DeferredSoundType(1.0F, 1.0F,
            () -> SoundEvents.STONE_BREAK,
            () -> SoundEvents.GRASS_STEP,
            () -> SoundEvents.STONE_PLACE,
            () -> SoundEvents.STONE_HIT,
            () -> SoundEvents.GRASS_FALL);

}
