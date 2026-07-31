package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.init.ModMobType;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class EffectsUtil {

    private static Holder<MobEffect> holder(MobEffect effect) {
        // Mob effect APIs are holder-based in 1.21, while many helpers still expose MobEffect for callers.
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }

    public static void amplifyEffect (LivingEntity infected, MobEffect effect, int duration){
        amplifyEffect(infected, effect, duration, 4, false, true);
    }

    public static void amplifyEffect(LivingEntity infected, MobEffect effect, int duration, int maxAmp, boolean pAmbient, boolean pVisible){
        Holder<MobEffect> holder = holder(effect);
        MobEffectInstance MobEffectInstance1 = infected.getEffect(holder);
        int i = 1;
        if (MobEffectInstance1 != null) {
            i += MobEffectInstance1.getAmplifier();
            infected.removeEffect(holder);
        } else {
            --i;
        }

        i = Mth.clamp(i, 0, maxAmp);
        MobEffectInstance MobEffectInstance = new MobEffectInstance(holder, duration, i, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static void resetDuration(LivingEntity infected, MobEffect effect, int duration){
        resetDuration(infected, effect, duration, false, true);
    }

    public static void resetDuration(LivingEntity infected, MobEffect effect, int duration, boolean pAmbient, boolean pVisible){
        Holder<MobEffect> holder = holder(effect);
        MobEffectInstance MobEffectInstance1 = infected.getEffect(holder);
        int a = 0;
        if (MobEffectInstance1 != null) {
            a = MobEffectInstance1.getAmplifier();
            infected.removeEffect(holder);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(holder, duration, a, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static void increaseDuration(LivingEntity infected, MobEffect effect, int duration){
        Holder<MobEffect> holder = holder(effect);
        MobEffectInstance MobEffectInstance1 = infected.getEffect(holder);
        int i = duration;
        int a = 0;
        if (MobEffectInstance1 != null) {
            a = MobEffectInstance1.getAmplifier();
            i = MobEffectInstance1.getDuration() + duration;
            infected.removeEffect(holder);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(holder, i, a);
        infected.addEffect(MobEffectInstance);
    }

    public static void decreaseDuration(LivingEntity infected, MobEffect effect, float duration, boolean pAmbient, boolean pVisible){
        Holder<MobEffect> holder = holder(effect);
        MobEffectInstance MobEffectInstance1 = infected.getEffect(holder);
        float i = duration;
        int a = 0;
        if (MobEffectInstance1 != null) {
            a = MobEffectInstance1.getAmplifier();
            i = MobEffectInstance1.getDuration() - duration;
            infected.removeEffect(holder);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(holder, (int) i, a, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static void deamplifyEffect(LivingEntity infected, MobEffect effect, int duration){
        deamplifyEffect(infected, effect, 1, duration, false, true);
    }

    public static void deamplifyEffect(LivingEntity infected, MobEffect effect, int deamp, int duration){
        deamplifyEffect(infected, effect, deamp, duration, false, true);
    }

    public static void deamplifyEffect(LivingEntity infected, MobEffect effect, int deamp, int duration, boolean pAmbient, boolean pVisible){
        Holder<MobEffect> holder = holder(effect);
        MobEffectInstance MobEffectInstance1 = infected.getEffect(holder);
        int i = 0;
        if (MobEffectInstance1 != null) {
            int amp = MobEffectInstance1.getAmplifier();
            i = amp - 1;
            infected.removeEffect(holder);
        }

        i = Math.max(0, i);
        MobEffectInstance MobEffectInstance = new MobEffectInstance(holder, duration, i, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static void halveDuration(LivingEntity infected, MobEffect effect, int duration){
        halveDuration(infected, effect, duration, false, true);
    }

    public static void halveDuration(LivingEntity infected, MobEffect effect, int duration, boolean pAmbient, boolean pVisible){
        Holder<MobEffect> holder = holder(effect);
        MobEffectInstance MobEffectInstance1 = infected.getEffect(holder);
        int a = 0;
        if (MobEffectInstance1 != null) {
            a = MobEffectInstance1.getAmplifier();
            infected.removeEffect(holder);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(holder, duration/2, a, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static int getAmplifier(LivingEntity infected, MobEffect effect){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(holder(effect));
        if (MobEffectInstance1 != null){
            return MobEffectInstance1.getAmplifier();
        }
        return 0;
    }

    public static int getAmplifierPlus(LivingEntity infected, MobEffect effect){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(holder(effect));
        if (MobEffectInstance1 != null){
            return MobEffectInstance1.getAmplifier() + 1;
        }
        return 0;
    }

    //Found out how to use Tags for MobEffect with @mraof's codes:https://github.com/lunar-sway/minestuck/blob/1.20.1/src/main/java/com/mraof/minestuck/effects/SoporSicknessEffect.java
    public static boolean is(MobEffect effect, TagKey<MobEffect> tagKey) {
        return holder(effect).is(tagKey);
    }

    public static boolean is(Holder<MobEffect> effect, TagKey<MobEffect> tagKey) {
        return effect.is(tagKey);
    }

    public static boolean canAffectLich(MobEffectInstance effectInstance, Level world) {
        return !is(effectInstance.getEffect(), ModTags.Effects.LICH_IMMUNE)
                && new Skeleton(EntityType.SKELETON, world).canBeAffected(effectInstance);
    }

    public static int getFortuneEffectLevel(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof LivingEntity livingEntity) {
            MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.FORTUNATE);
            if (mobEffectInstance != null){
                int level = mobEffectInstance.getAmplifier();
                return level + 1;
            }
        }
        return 0;
    }

    public static int infiniteEffect(){
        return -1;
    }

    public static List<LivingEntity> addEffectToAllAround(ServerLevel p_216947_, @org.jetbrains.annotations.Nullable Entity p_216948_, Vec3 p_216949_, double p_216950_, MobEffectInstance p_216951_, int p_216952_) {
            Holder<MobEffect> mobeffect = p_216951_.getEffect();
        List<LivingEntity> list = new ArrayList<>();
        for (Entity entity : p_216947_.getAllEntities()){
            if (entity instanceof LivingEntity livingEntity){
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity) && !MobUtil.areAllies(p_216948_, livingEntity) && p_216949_.closerThan(livingEntity.position(), p_216950_) && (!livingEntity.hasEffect(mobeffect) || livingEntity.getEffect(mobeffect).getAmplifier() < p_216951_.getAmplifier() || livingEntity.getEffect(mobeffect).endsWithin(p_216952_ - 1))){
                    list.add(livingEntity);
                }
            }
        }
        list.forEach((p_238232_) -> {
            p_238232_.addEffect(new MobEffectInstance(p_216951_), p_216948_);
        });
        return list;
    }

    public static void Illague(ServerLevel level, LivingEntity infected){
        MobEffectInstance illague = infected.getEffect(GoetyEffects.ILLAGUE);
        if (illague != null) {
            int duration = illague.getDuration() + 1;
            int amplifier = illague.getAmplifier();
            if (MobsConfig.IllagueSpread.get()) {
                for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class, infected.getBoundingBox().inflate(8.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
                    if (livingEntity.isAlive() && !(livingEntity instanceof PatrollingMonster) && !(livingEntity instanceof RaiderServant) && MobTypeHelper.getMobType(livingEntity) != ModMobType.UNDEAD && !livingEntity.hasEffect(GoetyEffects.ILLAGUE)) {
                        if (livingEntity.tickCount % 100 == 0 && livingEntity.getRandom().nextInt(20) == 0) {
                            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.ILLAGUE, duration / 2, amplifier, false, false));
                        }
                    }
                }
            }
            if (infected.tickCount % 20 == 0) {
                for (int i = 0; i < 8; ++i) {
                    level.sendParticles(ModParticleTypes.PLAGUE_EFFECT.get(), infected.getRandomX(0.5D), infected.getRandomY(), infected.getRandomZ(0.5D), 1, 0.0D, 0.5D, 0.0D, 0);
                }
            }
            int i1 = Mth.clamp((amplifier * 50) * 10, 0, 2500);
            int i2 = amplifier + 1;
            int k = 600 >> amplifier;
            if (k > 0) {
                if ((infected.tickCount % k == 0) && level.getDifficulty() != Difficulty.PEACEFUL) {
                    int r = level.random.nextInt(10);
                    int r2 = level.random.nextInt(6000 - i1);
                    int r3 = level.random.nextInt(i2);
                    int r4 = r3 + 1;
                    if (r2 == 0) {
                        EffectsUtil.amplifyEffect(infected, GoetyEffects.ILLAGUE.get(), 6000);
                    }
                    if (!MobUtil.isRaider(infected)) {
                        switch (r) {
                            case 0 ->
                                    infected.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400 * r4, r3, false, false));
                            case 1 ->
                                    infected.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400 * r4, r3, false, false));
                            case 2 ->
                                    infected.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400 * r4, 0, false, false));
                            case 3 ->
                                    infected.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400 * r4, r3, false, false));
                            case 4 ->
                                    infected.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400 * r4, r3, false, false));
                            case 5 ->
                                    infected.addEffect(new MobEffectInstance(MobEffects.POISON, 400 * r4, r3, false, false));
                            case 6 ->
                                    infected.addEffect(new MobEffectInstance(GoetyEffects.SAPPED, 400 * r4, r3, false, false));
                            case 7 ->
                                    infected.addEffect(new MobEffectInstance(GoetyEffects.FREEZING, 400 * r4, r3, false, false));
                            case 8 ->
                                    infected.addEffect(new MobEffectInstance(GoetyEffects.FLAMMABLE, 400 * r4, r3, false, false));
                            case 9 ->
                                    infected.addEffect(new MobEffectInstance(GoetyEffects.FLIMSY, 400 * r4, r3, false, false));
                        }
                    }
                }
            }
        }
    }

    public static void increaseEffect(LivingEntity infected, MobEffect effect, int maxAmp, boolean pAmbient, boolean pVisible){
        Holder<MobEffect> holder = holder(effect);
        MobEffectInstance instance = infected.getEffect(holder);
        int i = 1;
        int d = MathHelper.secondsToTicks(1);
        if (instance != null) {
            i += instance.getAmplifier();
            d = MathHelper.secondsToTicks(i);
        } else {
            --i;
            --d;
        }

        i = Mth.clamp(i, 0, maxAmp);
        MobEffectInstance MobEffectInstance = new MobEffectInstance(holder, d, i, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static void copyEffects(LivingEntity origin, LivingEntity target) {
        if (origin == null || target == null) {
            return;
        }

        if (origin.getActiveEffects().isEmpty()) {
            return;
        }

        try {
            for (MobEffectInstance instance : origin.getActiveEffects()) {
                target.addEffect(new MobEffectInstance(instance));
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }
}
