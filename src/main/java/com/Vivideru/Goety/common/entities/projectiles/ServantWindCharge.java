package com.Vivideru.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ServantWindCharge extends AbstractWindCharge {
    private static final float RADIUS = 3.0F;
    private float damage = 1.0F;
    private float knockbackMultiplier = 1.0F;

    public ServantWindCharge(EntityType<? extends AbstractWindCharge> type, Level level) {
        super(type, level);
    }

    public ServantWindCharge(Level level, LivingEntity owner, double x, double y, double z, float damage, float knockbackMultiplier) {
        super(VivideruEntityTypes.SERVANT_WIND_CHARGE.get(), level, owner, x, y, z);
        this.damage = damage;
        this.knockbackMultiplier = knockbackMultiplier;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getKnockbackMultiplier() {
        return this.knockbackMultiplier;
    }

    public void setKnockbackMultiplier(float knockbackMultiplier) {
        this.knockbackMultiplier = knockbackMultiplier;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity)) {
            return false;
        }
        if (this.getOwner() instanceof Owned owned) {
            return entity != owned.getTrueOwner() && !owned.isAlliedTo(entity);
        }
        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
            Entity entity = result.getEntity();
            if (owner != null) {
                owner.setLastHurtMob(entity);
            }
            DamageSource source = this.damageSources().windCharge(this, owner);
            if (entity.hurt(source, this.damage) && entity instanceof LivingEntity living) {
                EnchantmentHelper.doPostAttackEffects((ServerLevel) this.level(), living, source);
            }
            this.explode(this.position());
        }
    }

    @Override
    protected void explode(Vec3 pos) {
        ExplosionDamageCalculator calculator = this.knockbackMultiplier == 1.0F ? EXPLOSION_DAMAGE_CALCULATOR
                : new SimpleExplosionDamageCalculator(true, false, Optional.of(this.knockbackMultiplier),
                BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()));
        this.level().explode(this, null, calculator, pos.x(), pos.y(), pos.z(), RADIUS, false,
                Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.BREEZE_WIND_CHARGE_BURST);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putFloat("Knockback", this.knockbackMultiplier);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }
        if (tag.contains("Knockback")) {
            this.knockbackMultiplier = tag.getFloat("Knockback");
        }
    }
}
