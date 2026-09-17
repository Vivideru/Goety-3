package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Map;

public class SteamMissile extends SpellHurtingProjectile {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(SteamMissile.class, EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Goety.location("textures/entity/projectiles/soul_bolt/steam_missile_1.png"));
        map.put(1, Goety.location("textures/entity/projectiles/soul_bolt/steam_missile_2.png"));
        map.put(2, Goety.location("textures/entity/projectiles/soul_bolt/steam_missile_3.png"));
    });

    public SteamMissile(EntityType<? extends SpellHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public SteamMissile(double pX, double pY, double pZ, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.STEAM_MISSILE.get(), pX, pY, pZ, pXPower, pYPower, pZPower, pLevel);
    }

    public SteamMissile(LivingEntity pShooter, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.STEAM_MISSILE.get(), pShooter, pXPower, pYPower, pZPower, pLevel);
    }

    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DATA_TYPE_ID, 0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAnimation(compound.getInt("Animation"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Animation", this.getAnimation());
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(), TEXTURE_BY_TYPE.get(0));
    }

    public void rotateToMatchMovement() {
        this.updateRotation();
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public boolean isOnFire() {
        return false;
    }

    protected void onHitEntity(EntityHitResult p_37626_) {
        super.onHitEntity(p_37626_);
        if (!this.level().isClientSide) {
            float baseDamage = SpellConfig.get(SpellConfig.SteamingDamage).floatValue() * WandUtil.damageMultiply();
            Entity entity = p_37626_.getEntity();
            Entity entity1 = this.getOwner();
            boolean preserveActiveIFrames = SpellConfig.get(SpellConfig.PartialMagicProjectileIFrames)
                    && entity1 instanceof Player
                    && entity instanceof LivingEntity livingEntity
                    && livingEntity.invulnerableTime > 10;
            boolean flag;
            if (entity1 instanceof LivingEntity livingentity) {
                if (livingentity instanceof Mob mob){
                    if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null && mob.getAttributeValue(Attributes.ATTACK_DAMAGE) > 0){
                        baseDamage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    }
                }
                baseDamage += this.getExtraDamage();
                DamageSource damageSource = ModDamageSource.indirectDrench(this, livingentity);
                flag = entity.hurt(damageSource, baseDamage);
                if (flag) {
                    if (entity.isAlive()) {
                        // 1.21 routes post-attack enchantment hooks through EnchantmentHelper instead of Projectile#doEnchantDamageEffects.
                        EnchantmentHelper.doPostAttackEffects((ServerLevel)this.level(), entity, damageSource);
                    }
                }
            } else {
                flag = entity.hurt(this.damageSources().magic(), baseDamage);
            }

            if (flag && entity instanceof LivingEntity livingEntity && !preserveActiveIFrames) {
                // Normal steam hits keep their shorter recovery, while reduced multi-hits must leave the existing timer untouched.
                livingEntity.invulnerableTime = 15;
            }
        }
    }

    protected void onHit(HitResult p_37628_) {
        super.onHit(p_37628_);
        this.playSound(ModSounds.STEAM_IMPACT.get());
        if (!this.level().isClientSide) {
            this.discard();
        }

    }

    public void tick() {
        super.tick();
        if (this.getAnimation() < 2) {
            this.setAnimation(this.getAnimation() + 1);
        } else {
            this.setAnimation(0);
        }
        if (this.tickCount >= MathHelper.secondsToTicks(10)){
            this.discard();
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return super.getAddEntityPacket(serverEntity);
    }
}
