package com.Vivideru.Goety.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobTargetAccessor {
    /** The target set by goals, which mobs driven by a Brain do not return from getTarget. */
    @Nullable
    @Accessor("target")
    LivingEntity goety$getGoalTarget();
}
