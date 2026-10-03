package com.Polarice3.Goety.common.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

import java.util.Set;
import java.util.function.BooleanSupplier;

public class BrewMobEffect extends GoetyBaseEffect {
    public final BooleanSupplier curable;

    public BrewMobEffect(MobEffectCategory p_19451_, int p_19452_, boolean curable) {
        this(p_19451_, p_19452_, () -> curable);
    }

    public BrewMobEffect(MobEffectCategory p_19451_, int p_19452_, BooleanSupplier curable) {
        super(p_19451_, p_19452_);
        this.curable = curable;
    }

    public BrewMobEffect(MobEffectCategory p_19451_, int p_19452_) {
        this(p_19451_, p_19452_, true);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        if (this.curable.getAsBoolean()) {
            cures.addAll(EffectCures.DEFAULT_CURES);
        }
    }
}
