package com.Vivideru.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class HurricanePunch extends Projectile {
    public static final double SPEED = 1.3D;
    private static final double MAX_DISTANCE = 12.0D;
    private static final int MAX_LIFE = 40;
    private static final double KNOCKBACK = 2.2D;
    private static final double KNOCKBACK_LIFT = 0.35D;
    private static final int SHIELD_DISABLE_TICKS = 100;
    private final Set<Integer> hitEntities = new HashSet<>();
    private float damage = 15.0F;
    private double traveled;

    public HurricanePunch(EntityType<? extends HurricanePunch> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public HurricanePunch(Level level, LivingEntity owner, float damage) {
        this(VivideruEntityTypes.HURRICANE_PUNCH.get(), level);
        this.setOwner(owner);
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 delta = this.getDeltaMovement();
        Vec3 from = this.position();
        Vec3 to = from.add(delta);
        ProjectileUtil.rotateTowardsMovement(this, 1.0F);
        if (!this.level().isClientSide) {
            HitResult blockHit = this.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            boolean hitBlock = blockHit.getType() == HitResult.Type.BLOCK;
            if (hitBlock) {
                to = blockHit.getLocation();
            }
            AABB sweep = this.getBoundingBox().expandTowards(to.subtract(from)).inflate(0.25D);
            for (Entity entity : this.level().getEntities(this, sweep, this::canHitEntity)) {
                if (this.hitEntities.add(entity.getId())) {
                    this.hitEntity((LivingEntity) entity);
                }
            }
            this.traveled += from.distanceTo(to);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD, to.x, to.y, to.z, 2, 0.2D, 0.2D, 0.2D, 0.0D);
            }
            if (hitBlock || this.traveled >= MAX_DISTANCE || this.tickCount > MAX_LIFE) {
                this.setPos(to);
                this.burst();
                this.discard();
                return;
            }
        }
        this.setPos(to);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!(entity instanceof LivingEntity living) || !living.isAlive() || living.isSpectator() || !living.isPickable()) {
            return false;
        }
        Entity owner = this.getOwner();
        if (owner == null) {
            return true;
        }
        if (entity == owner) {
            return false;
        }
        if (owner instanceof Mob mob && mob.getTarget() == entity) {
            return true;
        }
        if (MobUtil.areAllies(owner, entity)) {
            return false;
        }
        if (owner instanceof Owned owned) {
            if (entity == owned.getTrueOwner()) {
                return false;
            }
            if (owned.isHostile() && entity instanceof Enemy) {
                return false;
            }
        }
        if (owner instanceof Enemy && entity instanceof Enemy) {
            return false;
        }
        if (entity instanceof IOwned owned0 && owner instanceof IOwned owned1) {
            return !MobUtil.ownerStack(owned0, owned1);
        }
        return true;
    }

    private void hitEntity(LivingEntity living) {
        LivingEntity owner = this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;
        boolean blocking = living instanceof Player player && player.isBlocking();
        DamageSource source = this.damageSources().mobProjectile(this, owner);
        if (owner != null) {
            owner.setLastHurtMob(living);
        }
        living.hurt(source, this.damage);
        if (blocking) {
            MobUtil.disableShield(living, SHIELD_DISABLE_TICKS);
        }
        Vec3 direction = this.getDeltaMovement();
        Vec3 flat = new Vec3(direction.x, 0.0D, direction.z);
        if (flat.lengthSqr() > 1.0E-4D) {
            Vec3 push = flat.normalize().scale(KNOCKBACK);
            MobUtil.push(living, push.x, KNOCKBACK_LIFT, push.z);
        }
        this.level().playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.HOSTILE, 1.2F, 0.6F);
    }

    private void burst() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST, SoundSource.HOSTILE, 1.0F, 0.7F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putDouble("Traveled", this.traveled);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }
        if (tag.contains("Traveled")) {
            this.traveled = tag.getDouble("Traveled");
        }
    }
}
