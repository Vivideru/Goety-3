package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MobTypeHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.Nullable;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> p_21024_);

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract boolean wasExperienceConsumed();

    @Shadow @Nullable private LivingEntity lastHurtByMob;

    @Shadow public abstract int getExperienceReward(ServerLevel level, @Nullable Entity source);

    @Shadow protected int lastHurtByPlayerTime;

    @Shadow protected float lastHurt;

    @Unique
    private boolean goety$dealingPartialProjectileDamage;

    @Shadow protected abstract boolean isAlwaysExperienceDropper();

    @Shadow public abstract Map<Holder<MobEffect>, MobEffectInstance> getActiveEffectsMap();

    protected LivingEntityMixin(EntityType<? extends Entity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void goety$partialMagicProjectileDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.goety$dealingPartialProjectileDamage
                && SpellConfig.get(SpellConfig.PartialMagicProjectileIFrames)
                && this.invulnerableTime > 10
                && amount > 0.0F
                && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)
                && source.getDirectEntity() instanceof Projectile projectile
                && projectile instanceof ISpellEntity
                && projectile.getOwner() instanceof Player) {
            int previousInvulnerableTime = this.invulnerableTime;
            float previousLastHurt = this.lastHurt;
            try {
                // Run the reduced hit through the normal damage pipeline, then restore both values that govern i-frames so rapid spell hits never extend them.
                this.goety$dealingPartialProjectileDamage = true;
                this.invulnerableTime = 0;
                cir.setReturnValue(((LivingEntity) (Object) this).hurt(source, amount * 0.25F));
            } finally {
                this.invulnerableTime = previousInvulnerableTime;
                this.lastHurt = previousLastHurt;
                this.goety$dealingPartialProjectileDamage = false;
            }
        }
    }

    @Inject(method = "dropExperience", at = @At("HEAD"))
    public void dropExperience(@Nullable Entity source, CallbackInfo callbackInfo) {
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.lastHurtByPlayerTime <= 0 && !this.isAlwaysExperienceDropper()) {
                if (this.lastHurtByMob instanceof IOwned owned && !this.wasExperienceConsumed() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    if (owned.getMasterOwner() instanceof Player player) {
                        // 1.21 calculates mob experience with the server level and death source before firing the drop event.
                        int reward = net.neoforged.neoforge.event.EventHooks.getExperienceDrop((LivingEntity) (Object) this, player, this.getExperienceReward(serverLevel, source));
                        ExperienceOrb.award(serverLevel, this.position(), reward);
                    }
                }
            }
        }
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void canAttack(LivingEntity target, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (MainConfig.LichUndeadFriends.get()) {
            if (MobTypeHelper.isMobType((LivingEntity) (Object) this, ModMobType.UNDEAD) || this.getType().is(ModTags.EntityTypes.LICH_NEUTRAL)) {
                if (LichdomHelper.isLich(target)) {
                    if (MainConfig.LichPowerfulFoes.get()) {
                        if (this.getMaxHealth() <= MainConfig.LichPowerfulFoesHealth.get()){
                            callbackInfoReturnable.setReturnValue(false);
                        }
                    } else {
                        callbackInfoReturnable.setReturnValue(false);
                    }
                }
            }
        }
    }

    @Inject(method = "isSensitiveToWater", at = @At("HEAD"), cancellable = true)
    public void isSensitiveToWater(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (this.hasEffect(GoetyEffects.SNOW_SKIN)) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "randomTeleport", at = @At("HEAD"), cancellable = true)
    public void randomTeleport(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (this.hasEffect(GoetyEffects.ENDER_GROUND)) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void jumpFromGround(CallbackInfo callbackInfo) {
        if (this.hasEffect(GoetyEffects.STUNNED) || this.hasEffect(GoetyEffects.TANGLED)) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "travelRidden", at = @At("HEAD"), cancellable = true)
    public void travelRidden(Player player, Vec3 vec3, CallbackInfo callbackInfo) {
        if (this instanceof IAutoRideable rideable && rideable.isAutonomous()){
            callbackInfo.cancel();
        }
    }

    @Inject(method = "updateInvisibilityStatus", at = @At(value = "TAIL"))
    public void updateInvisibilityStatus(CallbackInfo callbackInfo) {
        if (this.hasEffect(GoetyEffects.SHADOW_WALK)) {
            this.setInvisible(true);
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    public void addEffect(MobEffectInstance instance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity != null) {
            if (entity instanceof IOwned) {
                if (!MobsConfig.ServantsHarmEffectApply.get()) {
                    if (instance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                        if (MobUtil.areAllies(this, entity)) {
                            cir.setReturnValue(false);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    public void goetyHasEffect(Holder<MobEffect> mobEffect, CallbackInfoReturnable<Boolean> cir) {
        if (mobEffect.is(MobEffects.POISON)) {
            if (this.getActiveEffectsMap().containsKey(GoetyEffects.ACID_VENOM)) {
                cir.setReturnValue(true);
            }
        }
    }
}
