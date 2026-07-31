package com.Polarice3.Goety.common.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class SoulHungerEffect extends GoetyBaseEffect {

    public SoulHungerEffect() {
        super(MobEffectCategory.NEUTRAL, 5064781);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "e6a036bf-903a-4574-926e-61a772170d97", -4.0D, AttributeModifier.Operation.ADD_VALUE);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "7ad88b67-392d-4d87-a6a5-08c08c2f86eb", (double)-0.05F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.ATTACK_SPEED, "ac113f1f-ef15-4e4f-9c7d-44be1d0e06a9", (double)-0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        // Soul hunger was intentionally incurable; leave the 1.21 cure set empty to preserve that behavior.
    }
}
