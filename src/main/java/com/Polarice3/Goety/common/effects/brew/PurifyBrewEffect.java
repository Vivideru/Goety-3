package com.Polarice3.Goety.common.effects.brew;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPurifyEffectPacket;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.Nullable;

public class PurifyBrewEffect extends BrewEffect{
    public boolean removeDebuff;

    public PurifyBrewEffect(String effectID, int soulCost, int cap, MobEffectCategory mobEffectCategory, int color, boolean removeDebuff) {
        super(effectID, soulCost, cap, mobEffectCategory, color);
        this.removeDebuff = removeDebuff;
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        if (!pTarget.level().isClientSide) {
            for (Holder.Reference<MobEffect> mobEffectHolder : BuiltInRegistries.MOB_EFFECT.holders().toList()){
                MobEffect mobEffect = mobEffectHolder.value();
                boolean flag;
                if (this.removeDebuff) {
                    flag = !mobEffect.isBeneficial();
                } else {
                    flag = mobEffect.isBeneficial();
                }
                if (flag){
                    // Curative item lists were removed from MobEffect in 1.21; preserve the brew's buff/debuff filter and remove matching active effects by holder.
                    pTarget.removeEffect(mobEffectHolder);
                }
            }
            ModNetwork.sentToTrackingEntity(pTarget, new SPurifyEffectPacket(pTarget.getId(), this.removeDebuff));
        }
    }
}
