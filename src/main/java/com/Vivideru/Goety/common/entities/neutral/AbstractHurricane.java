package com.Vivideru.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IMobTyped;
import com.Polarice3.Goety.client.particles.WindBlowParticleOption;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Vivideru.Goety.common.entities.ally.BreezeServant;
import com.Vivideru.Goety.common.entities.projectiles.HurricaneCyclone;
import com.Vivideru.Goety.common.entities.projectiles.HurricanePunch;
import com.Vivideru.Goety.common.entities.projectiles.ServantWindCharge;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public abstract class AbstractHurricane extends Summoned implements IMobTyped {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(AbstractHurricane.class, EntityDataSerializers.INT);
    public static final int ANIM_NONE = 0;
    public static final int ANIM_IDLE = 1;
    public static final int ANIM_PUNCH = 2;
    public static final int ANIM_CYCLONE = 3;
    public static final int ANIM_STOMP = 4;
    public static final int ANIM_DEATH = 5;
    public static final int DEATH_DURATION = 60;
    private static final float SMASH_RADIUS = 3.5F;
    private static final float SMASH_KNOCKBACK_POWER = 0.7F;
    private static final float VOICE_VOLUME = 1.4F;
    private static final float VOICE_PITCH = 0.45F;
    private static final int WHIRL_SOUND_FREQUENCY_MIN = 1;
    private static final int WHIRL_SOUND_FREQUENCY_MAX = 80;
    private static final ProjectileDeflection PROJECTILE_DEFLECTION = (projectile, entity, random) -> {
        entity.level().playSound(null, entity, SoundEvents.BREEZE_DEFLECT, entity.getSoundSource(), 1.2F, 0.6F);
        ProjectileDeflection.REVERSE.deflect(projectile, entity, random);
    };

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState punchAnimationState = new AnimationState();
    public final AnimationState cycloneAnimationState = new AnimationState();
    public final AnimationState stompAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    private int punchCooldown;
    private int stormCooldown;
    private int jumpCooldown;
    private int soundTick;
    private boolean jumpActive;
    @Nullable
    private Vec3 retreatFrom;

    public AbstractHurricane(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new FollowOwnerGoal<>(this, 0.6D, 10.0F, 2.0F));
    }

    @Override
    public double getCommandSpeed() {
        return 0.6D;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new DivineStormGoal());
        this.goalSelector.addGoal(2, new ZoomPunchGoal());
        this.goalSelector.addGoal(3, new SmashJumpGoal());
        this.goalSelector.addGoal(4, new KeepDistanceGoal());
        this.goalSelector.addGoal(7, new WanderGoal<>(this, 0.6D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.63D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.get(AttributesConfig.HurricaneHealth))
                .add(Attributes.ARMOR, AttributesConfig.get(AttributesConfig.HurricaneArmor))
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.get(AttributesConfig.HurricanePunchDamage))
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.STEP_HEIGHT, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.get(AttributesConfig.HurricaneHealth));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.get(AttributesConfig.HurricaneArmor));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.get(AttributesConfig.HurricanePunchDamage));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIM_STATE, ANIM_NONE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.entityData.get(ANIM_STATE)) {
                case ANIM_IDLE -> this.startAnimation(this.idleAnimationState);
                case ANIM_PUNCH -> this.startAnimation(this.punchAnimationState);
                case ANIM_CYCLONE -> this.startAnimation(this.cycloneAnimationState);
                case ANIM_STOMP -> this.startAnimation(this.stompAnimationState);
                case ANIM_DEATH -> this.startAnimation(this.deathAnimationState);
                default -> this.startAnimation(null);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void startAnimation(@Nullable AnimationState state) {
        for (AnimationState other : List.of(this.idleAnimationState, this.punchAnimationState, this.cycloneAnimationState, this.stompAnimationState, this.deathAnimationState)) {
            if (other != state) {
                other.stop();
            }
        }
        if (state != null) {
            state.startIfStopped(this.tickCount);
        }
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState() {
        return this.entityData.get(ANIM_STATE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PunchCool", this.punchCooldown);
        tag.putInt("StormCool", this.stormCooldown);
        tag.putInt("JumpCool", this.jumpCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.punchCooldown = tag.getInt("PunchCool");
        this.stormCooldown = tag.getInt("StormCool");
        this.jumpCooldown = tag.getInt("JumpCool");
    }

    @Override
    public ModMobType getGoetyMobType() {
        return ModMobType.UNDEFINED;
    }

    @Override
    public int xpReward() {
        return 40;
    }

    public float getPunchDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public float getSmashDamage() {
        return AttributesConfig.get(AttributesConfig.HurricaneSmashDamage).floatValue();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isAlive()) {
            if (--this.soundTick <= 0) {
                this.level().playLocalSound(this, SoundEvents.BREEZE_WHIRL, this.getSoundSource(), this.getSoundVolume() * 0.8F, this.getVoicePitch());
                this.soundTick = Mth.randomBetweenInclusive(this.random, WHIRL_SOUND_FREQUENCY_MIN, WHIRL_SOUND_FREQUENCY_MAX);
            }
            if (this.random.nextInt(4) == 0) {
                this.level().addParticle(ParticleTypes.CLOUD, this.getRandomX(0.6D), this.getY() + this.random.nextDouble() * 0.6D, this.getRandomZ(0.6D), 0.0D, 0.02D, 0.0D);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.punchCooldown > 0) {
                --this.punchCooldown;
            }
            if (this.stormCooldown > 0) {
                --this.stormCooldown;
            }
            if (this.jumpCooldown > 0) {
                --this.jumpCooldown;
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source)
                || source.getEntity() instanceof Breeze
                || source.getEntity() instanceof BreezeServant
                || source.getEntity() instanceof AbstractHurricane
                || source.getDirectEntity() instanceof AbstractArrow
                || source.is(DamageTypes.WIND_CHARGE)
                || source.is(DamageTypeTags.IS_FALL);
    }

    @Override
    public ProjectileDeflection deflection(Projectile projectile) {
        if (projectile.getType() != EntityType.BREEZE_WIND_CHARGE && projectile.getType() != EntityType.WIND_CHARGE && !(projectile instanceof ServantWindCharge)) {
            return this.getType().is(net.minecraft.tags.EntityTypeTags.DEFLECTS_PROJECTILES) ? PROJECTILE_DEFLECTION : ProjectileDeflection.NONE;
        }
        return ProjectileDeflection.NONE;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && this.isAlive() && amount > 0.0F) {
            this.windBurst(amount);
            if (this.getTarget() != null && this.getAnimationState() == ANIM_NONE && !this.jumpActive && source.getEntity() != null && source.getEntity() != this) {
                this.retreatFrom = source.getEntity().position();
            }
        }
        return hurt;
    }

    private void windBurst(float amount) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double strength = Mth.clamp(0.4D + amount * 0.08D, 0.4D, 2.5D);
        double radius = Mth.clamp(2.5D + amount * 0.1D, 2.5D, 6.0D);
        for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(radius))) {
            if (entity instanceof LivingEntity living && living.isAlive() && !living.isSpectator() && !(entity instanceof AbstractHurricane)
                    && !MobUtil.areAllies(this, living) && !entity.getType().is(ModTags.EntityTypes.UNBLOWABLE_ENTITIES)
                    && this.distanceTo(entity) <= radius) {
                MobUtil.knockBack(entity, this, strength, 0.15D * strength, strength);
            }
        }
        double y = this.getY(0.5D);
        for (int i = 0; i < 16; i++) {
            float angle = this.random.nextFloat() * (float) Math.PI * 2.0F;
            double velocity = 0.3D + this.random.nextDouble() * 0.3D * strength;
            int width = this.random.nextIntBetweenInclusive(1, 4);
            float height = this.random.nextFloat() * 0.5F;
            serverLevel.sendParticles(new WindBlowParticleOption(ColorUtil.WHITE, width, height),
                    this.getX() + Mth.cos(angle) * 0.8D, y + this.random.nextGaussian() * 0.6D, this.getZ() + Mth.sin(angle) * 0.8D,
                    0, Mth.cos(angle) * velocity, 0.02D, Mth.sin(angle) * velocity, 1.0D);
        }
        serverLevel.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, this.getX(), y, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST, this.getSoundSource(), 0.8F, 0.7F);
    }

    @Override
    protected float getSoundVolume() {
        return VOICE_VOLUME;
    }

    @Override
    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.1F + VOICE_PITCH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.onGround() ? SoundEvents.BREEZE_IDLE_GROUND : SoundEvents.BREEZE_IDLE_AIR;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BREEZE_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BREEZE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
    }

    private void playVoice(SoundEvent sound, float volumeScale) {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), this.getSoundVolume() * volumeScale, this.getVoicePitch());
    }

    @Override
    public void die(DamageSource cause) {
        this.setAnimationState(ANIM_DEATH);
        this.haltMovement();
        super.die(cause);
    }

    private void haltMovement() {
        this.setDiscardFriction(false);
        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, Math.min(this.getDeltaMovement().y, 0.0D), 0.0D);
        this.hasImpulse = true;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.haltMovement();
        if (this.deathTime >= DEATH_DURATION && !this.level().isClientSide && !this.isRemoved()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(0.5D), this.getZ(), 40, 0.8D, 1.2D, 0.8D, 0.05D);
                serverLevel.sendParticles(ParticleTypes.GUST_EMITTER_LARGE, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    public float getDeathFade(float partialTicks) {
        if (this.deathTime <= 0) {
            return 1.0F;
        }
        return Mth.clamp(1.0F - ((float) this.deathTime + partialTicks) / (float) DEATH_DURATION, 0.0F, 1.0F);
    }

    public boolean hasLineOfSight(Vec3 pos) {
        Vec3 from = this.getEyePosition();
        return pos.distanceTo(from) <= 50.0D
                && this.level().clip(new ClipContext(from, pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    private boolean canSmash(LivingEntity entity) {
        if (entity == this || !entity.isAlive() || entity.isSpectator() || entity instanceof AbstractHurricane) {
            return false;
        }
        if (MobUtil.areAllies(this, entity)) {
            return false;
        }
        return !(entity instanceof Enemy) || entity == this.getTarget();
    }

    private void smash() {
        this.level().levelEvent(2013, this.getOnPos(), 750);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MACE_SMASH_GROUND_HEAVY, this.getSoundSource(), 1.6F, 0.8F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.GUST_EMITTER_LARGE, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        float damage = this.getSmashDamage();
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(SMASH_RADIUS), this::canSmash)) {
            double distance = entity.distanceTo(this);
            if (distance > SMASH_RADIUS) {
                continue;
            }
            entity.hurt(this.damageSources().mobAttack(this), damage);
            Vec3 away = new Vec3(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());
            if (away.lengthSqr() < 1.0E-4D) {
                away = new Vec3(0.0D, 0.0D, 1.0D);
            }
            double power = (SMASH_RADIUS - distance) * SMASH_KNOCKBACK_POWER;
            Vec3 push = away.normalize().scale(power);
            MobUtil.push(entity, push.x, SMASH_KNOCKBACK_POWER, push.z);
        }
    }

    class ZoomPunchGoal extends Goal {
        private static final int AIM_TICKS = 13;
        private static final int LAUNCH_TICK = 15;
        private static final int DURATION = 25;
        private static final double MIN_RANGE_SQR = 3.0D * 3.0D;
        private static final double MAX_RANGE_SQR = 14.0D * 14.0D;
        private int timer;
        @Nullable
        private Vec3 aim;

        ZoomPunchGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractHurricane.this.getTarget();
            if (target == null || !target.isAlive() || AbstractHurricane.this.punchCooldown > 0 || !AbstractHurricane.this.onGround()
                    || AbstractHurricane.this.getAnimationState() != ANIM_NONE) {
                return false;
            }
            double distance = AbstractHurricane.this.distanceToSqr(target);
            return distance > MIN_RANGE_SQR && distance < MAX_RANGE_SQR && AbstractHurricane.this.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.timer < DURATION && AbstractHurricane.this.getTarget() != null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.timer = 0;
            this.aim = null;
            AbstractHurricane.this.getNavigation().stop();
            AbstractHurricane.this.setAnimationState(ANIM_PUNCH);
            AbstractHurricane.this.playVoice(SoundEvents.BREEZE_INHALE, 1.0F);
        }

        @Override
        public void tick() {
            ++this.timer;
            LivingEntity target = AbstractHurricane.this.getTarget();
            if (target == null) {
                return;
            }
            if (this.timer <= AIM_TICKS) {
                this.aim = this.predict(target);
                AbstractHurricane.this.lookAt(EntityAnchorArgument.Anchor.EYES, this.aim);
                AbstractHurricane.this.yBodyRot = AbstractHurricane.this.getYRot();
                AbstractHurricane.this.yHeadRot = AbstractHurricane.this.getYRot();
            }
            if (this.timer == LAUNCH_TICK && this.aim != null) {
                this.launch();
            }
        }

        private Vec3 predict(LivingEntity target) {
            double distance = target.distanceTo(AbstractHurricane.this);
            int flightTicks = (LAUNCH_TICK - this.timer) + Mth.ceil(distance / HurricanePunch.SPEED);
            Vec3 motion = target.getDeltaMovement();
            if (target.onGround()) {
                motion = new Vec3(motion.x, 0.0D, motion.z);
            }
            return target.position().add(motion.scale(flightTicks)).add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        }

        private void launch() {
            Vec3 origin = new Vec3(AbstractHurricane.this.getX(), AbstractHurricane.this.getEyeY() - 0.8D, AbstractHurricane.this.getZ());
            Vec3 direction = this.aim.subtract(origin).normalize();
            origin = origin.add(direction.scale(1.2D));
            HurricanePunch punch = new HurricanePunch(AbstractHurricane.this.level(), AbstractHurricane.this, AbstractHurricane.this.getPunchDamage());
            punch.setPos(origin);
            punch.setDeltaMovement(direction.scale(HurricanePunch.SPEED));
            AbstractHurricane.this.level().addFreshEntity(punch);
            AbstractHurricane.this.playVoice(SoundEvents.BREEZE_SHOOT, 1.2F);
            AbstractHurricane.this.level().playSound(null, AbstractHurricane.this.getX(), AbstractHurricane.this.getY(), AbstractHurricane.this.getZ(), SoundEvents.MACE_SMASH_AIR, AbstractHurricane.this.getSoundSource(), 1.5F, 0.6F);
        }

        @Override
        public void stop() {
            AbstractHurricane.this.setAnimationState(ANIM_NONE);
            AbstractHurricane.this.punchCooldown = 40 + AbstractHurricane.this.random.nextInt(30);
            this.aim = null;
        }
    }

    class DivineStormGoal extends Goal {
        private static final int SPAWN_TICK = 5;
        private static final int LAUNCH_TICK = 30;
        private static final int DURATION = 37;
        private static final double MIN_RANGE_SQR = 5.0D * 5.0D;
        private static final double MAX_RANGE_SQR = 24.0D * 24.0D;
        private static final float CYCLONE_SIZE = 2.0F;
        private static final int CYCLONE_LIFE = 150;
        private int timer;
        @Nullable
        private HurricaneCyclone cyclone;

        DivineStormGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractHurricane.this.getTarget();
            if (target == null || !target.isAlive() || AbstractHurricane.this.stormCooldown > 0 || !AbstractHurricane.this.onGround()
                    || AbstractHurricane.this.getAnimationState() != ANIM_NONE) {
                return false;
            }
            double distance = AbstractHurricane.this.distanceToSqr(target);
            return distance > MIN_RANGE_SQR && distance < MAX_RANGE_SQR && AbstractHurricane.this.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.timer < DURATION && AbstractHurricane.this.getTarget() != null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.timer = 0;
            this.cyclone = null;
            AbstractHurricane.this.getNavigation().stop();
            AbstractHurricane.this.setAnimationState(ANIM_CYCLONE);
            AbstractHurricane.this.playVoice(SoundEvents.BREEZE_CHARGE, 1.5F);
        }

        private Vec3 anchor() {
            return new Vec3(AbstractHurricane.this.getX(), AbstractHurricane.this.getY() + AbstractHurricane.this.getBbHeight() + 0.2D, AbstractHurricane.this.getZ());
        }

        @Override
        public void tick() {
            ++this.timer;
            LivingEntity target = AbstractHurricane.this.getTarget();
            if (target != null) {
                AbstractHurricane.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            Vec3 anchor = this.anchor();
            if (this.timer == SPAWN_TICK) {
                HurricaneCyclone cyclone = new HurricaneCyclone(AbstractHurricane.this.level(), AbstractHurricane.this);
                cyclone.setOwner(AbstractHurricane.this);
                cyclone.setDamage(AttributesConfig.get(AttributesConfig.HurricaneCycloneDamage).floatValue());
                cyclone.setSize(CYCLONE_SIZE);
                cyclone.setTotalLife(CYCLONE_LIFE);
                cyclone.hold(anchor);
                if (AbstractHurricane.this.level().addFreshEntity(cyclone)) {
                    this.cyclone = cyclone;
                }
                AbstractHurricane.this.playVoice(SoundEvents.BREEZE_IDLE_AIR, 1.5F);
            } else if (this.timer < LAUNCH_TICK && this.cyclone != null) {
                if (this.cyclone.isRemoved()) {
                    this.cyclone = null;
                } else {
                    this.cyclone.hold(anchor);
                }
            }
            if (this.timer == LAUNCH_TICK) {
                AbstractHurricane.this.setAnimationState(ANIM_STOMP);
                if (this.cyclone != null && !this.cyclone.isRemoved() && target != null) {
                    Vec3 to = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(this.cyclone.position());
                    this.cyclone.launch(to);
                }
                this.cyclone = null;
                AbstractHurricane.this.playVoice(SoundEvents.BREEZE_SHOOT, 1.3F);
                AbstractHurricane.this.level().playSound(null, AbstractHurricane.this.getX(), AbstractHurricane.this.getY(), AbstractHurricane.this.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST, AbstractHurricane.this.getSoundSource(), 1.5F, 0.6F);
            }
        }

        @Override
        public void stop() {
            AbstractHurricane.this.setAnimationState(ANIM_NONE);
            AbstractHurricane.this.stormCooldown = 200 + AbstractHurricane.this.random.nextInt(120);
            if (this.cyclone != null && !this.cyclone.isRemoved()) {
                this.cyclone.discard();
            }
            this.cyclone = null;
        }
    }
    class SmashJumpGoal extends Goal {
        private static final int INHALE_TICKS = 10;
        private static final float MAX_JUMP_VELOCITY = 3.0F;
        private static final int REQUIRED_AIR_BLOCKS_ABOVE = 5;
        private static final int MAX_AIR_TICKS = 100;
        private static final float MIN_HOP = 8.0F;
        private static final float MAX_HOP = 15.0F;
        private static final double SNAP_RANGE = 20.0D;
        private static final int JUMP_COOLDOWN_TICKS = 10;
        private static final int LANDING_ATTEMPTS = 4;
        private static final List<Integer> FLAT_ANGLES = List.of(40, 50, 60, 70);
        private static final List<Integer> CLIMB_ANGLES = List.of(60, 70, 80, 50);
        private static final List<Integer> STEEP_ANGLES = List.of(80, 85, 75);

        private enum Phase {
            INHALING,
            JUMPING
        }

        @Nullable
        private Phase phase;
        private int timer;
        private int chain;
        @Nullable
        private BlockPos landing;

        SmashJumpGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (!AbstractHurricane.this.onGround() || AbstractHurricane.this.getAnimationState() != ANIM_NONE || !this.canJumpFromCurrentPosition()) {
                return false;
            }
            LivingEntity target = AbstractHurricane.this.getTarget();
            if (AbstractHurricane.this.retreatFrom != null) {
                if (target != null) {
                    return true;
                }
                AbstractHurricane.this.retreatFrom = null;
            }
            return target != null && target.isAlive() && AbstractHurricane.this.jumpCooldown <= 0 && target.distanceTo(AbstractHurricane.this) - 4.0F > 0.0F;
        }

        @Override
        public boolean canContinueToUse() {
            return this.phase != null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            LivingEntity target = AbstractHurricane.this.getTarget();
            Vec3 retreatFrom = AbstractHurricane.this.retreatFrom;
            AbstractHurricane.this.retreatFrom = null;
            this.timer = 0;
            this.landing = null;
            for (int attempt = 0; attempt < LANDING_ATTEMPTS && this.landing == null; attempt++) {
                Vec3 point;
                if (retreatFrom != null) {
                    point = this.randomPointAwayFrom(retreatFrom);
                } else if (target != null) {
                    point = this.randomPointBehindTarget(target);
                } else {
                    break;
                }
                this.landing = this.acceptLanding(this.snapToSurface(point));
            }
            if (this.landing == null) {
                this.phase = null;
                AbstractHurricane.this.jumpCooldown = JUMP_COOLDOWN_TICKS;
                return;
            }
            if (this.chain <= 0) {
                this.chain = this.pickChain();
            }
            this.phase = Phase.INHALING;
            AbstractHurricane.this.jumpActive = true;
            AbstractHurricane.this.getNavigation().stop();
            AbstractHurricane.this.lookAt(EntityAnchorArgument.Anchor.EYES, this.landing.getCenter());
            AbstractHurricane.this.playVoice(SoundEvents.BREEZE_CHARGE, 1.3F);
        }

        private int pickChain() {
            float roll = AbstractHurricane.this.random.nextFloat();
            if (roll < 0.5F) {
                return 1;
            }
            return roll < 0.8F ? 2 : 3;
        }

        private Vec3 randomPointBehindTarget(LivingEntity target) {
            float yaw = target.yHeadRot + 180.0F + (float) AbstractHurricane.this.random.nextGaussian() * 90.0F / 2.0F;
            float distance = Mth.lerp(AbstractHurricane.this.random.nextFloat(), MIN_HOP, MAX_HOP);
            Vec3 offset = Vec3.directionFromRotation(0.0F, yaw).scale(distance);
            return target.position().add(offset);
        }

        private Vec3 randomPointAwayFrom(Vec3 attacker) {
            Vec3 away = new Vec3(AbstractHurricane.this.getX() - attacker.x, 0.0D, AbstractHurricane.this.getZ() - attacker.z);
            float yaw = away.lengthSqr() < 1.0E-4D
                    ? AbstractHurricane.this.random.nextFloat() * 360.0F
                    : (float) (Mth.atan2(-away.x, away.z) * (180.0D / Math.PI)) + (float) AbstractHurricane.this.random.nextGaussian() * 45.0F;
            float distance = Mth.lerp(AbstractHurricane.this.random.nextFloat(), MIN_HOP, MAX_HOP);
            return AbstractHurricane.this.position().add(Vec3.directionFromRotation(0.0F, yaw).scale(distance));
        }

        @Nullable
        private BlockPos snapToSurface(Vec3 pos) {
            ClipContext down = new ClipContext(pos, pos.relative(Direction.DOWN, SNAP_RANGE), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, AbstractHurricane.this);
            HitResult hit = AbstractHurricane.this.level().clip(down);
            if (hit.getType() == HitResult.Type.BLOCK) {
                return BlockPos.containing(hit.getLocation()).above();
            }
            ClipContext up = new ClipContext(pos, pos.relative(Direction.UP, SNAP_RANGE), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, AbstractHurricane.this);
            HitResult hitUp = AbstractHurricane.this.level().clip(up);
            return hitUp.getType() == HitResult.Type.BLOCK ? BlockPos.containing(hitUp.getLocation()).above() : null;
        }

        @Nullable
        private BlockPos acceptLanding(@Nullable BlockPos landing) {
            if (landing == null) {
                return null;
            }
            Level level = AbstractHurricane.this.level();
            if (AbstractHurricane.this.getType().isBlockDangerous(level.getBlockState(landing.below()))) {
                return null;
            }
            for (int i = 0; i < 4; i++) {
                BlockPos above = landing.above(i);
                if (!level.getBlockState(above).getCollisionShape(level, above).isEmpty()) {
                    return null;
                }
            }
            if (!AbstractHurricane.this.hasLineOfSight(landing.getCenter()) && !AbstractHurricane.this.hasLineOfSight(landing.above(REQUIRED_AIR_BLOCKS_ABOVE).getCenter())) {
                return null;
            }
            return landing;
        }

        private boolean canJumpFromCurrentPosition() {
            BlockPos pos = AbstractHurricane.this.blockPosition();
            int top = Mth.ceil(AbstractHurricane.this.getBbHeight());
            for (int i = top; i <= top + REQUIRED_AIR_BLOCKS_ABOVE; i++) {
                if (!AbstractHurricane.this.level().getBlockState(pos.relative(Direction.UP, i)).isAir()) {
                    return false;
                }
            }
            return true;
        }

        private Optional<Vec3> jumpVector(Vec3 target) {
            double rise = target.y - AbstractHurricane.this.getY();
            List<Integer> angles;
            if (rise > 6.0D) {
                angles = STEEP_ANGLES;
            } else if (rise > 2.0D) {
                angles = CLIMB_ANGLES;
            } else {
                angles = FLAT_ANGLES;
            }
            for (int angle : angles) {
                Optional<Vec3> vector = LongJumpUtil.calculateJumpVectorForAngle(AbstractHurricane.this, target, MAX_JUMP_VELOCITY, angle, false);
                if (vector.isPresent()) {
                    return vector;
                }
            }
            return Optional.empty();
        }

        @Override
        public void tick() {
            ++this.timer;
            if (this.phase == Phase.INHALING) {
                if (this.landing != null) {
                    AbstractHurricane.this.lookAt(EntityAnchorArgument.Anchor.EYES, this.landing.getCenter());
                }
                if (this.timer < INHALE_TICKS) {
                    return;
                }
                Vec3 velocity = this.landing == null ? null : this.jumpVector(Vec3.atBottomCenterOf(this.landing)).orElse(null);
                if (velocity == null) {
                    this.phase = null;
                    AbstractHurricane.this.jumpCooldown = JUMP_COOLDOWN_TICKS;
                    return;
                }
                AbstractHurricane.this.playVoice(SoundEvents.BREEZE_JUMP, 1.5F);
                AbstractHurricane.this.setYRot(AbstractHurricane.this.yBodyRot);
                AbstractHurricane.this.setDiscardFriction(true);
                AbstractHurricane.this.setDeltaMovement(velocity);
                AbstractHurricane.this.hasImpulse = true;
                this.phase = Phase.JUMPING;
                this.timer = 0;
            } else if (this.phase == Phase.JUMPING) {
                if (AbstractHurricane.this.level() instanceof ServerLevel serverLevel && this.timer % 2 == 0) {
                    serverLevel.sendParticles(ParticleTypes.CLOUD, AbstractHurricane.this.getX(), AbstractHurricane.this.getY(), AbstractHurricane.this.getZ(), 3, 0.3D, 0.1D, 0.3D, 0.0D);
                }
                boolean landed = this.timer > 3 && (AbstractHurricane.this.onGround() || AbstractHurricane.this.isInWater());
                if (landed || this.timer > MAX_AIR_TICKS) {
                    this.land();
                }
            }
        }

        private void land() {
            AbstractHurricane.this.setDiscardFriction(false);
            AbstractHurricane.this.playVoice(SoundEvents.BREEZE_LAND, 1.5F);
            AbstractHurricane.this.smash();
            --this.chain;
            AbstractHurricane.this.jumpCooldown = this.chain > 0 ? 6 + AbstractHurricane.this.random.nextInt(6) : 20 + AbstractHurricane.this.random.nextInt(20);
            this.phase = null;
        }

        @Override
        public void stop() {
            AbstractHurricane.this.setDiscardFriction(false);
            AbstractHurricane.this.jumpActive = false;
            this.landing = null;
            this.phase = null;
            if (AbstractHurricane.this.jumpCooldown <= 0) {
                AbstractHurricane.this.jumpCooldown = JUMP_COOLDOWN_TICKS;
            }
        }
    }

    class KeepDistanceGoal extends Goal {
        private static final double IDEAL_DISTANCE = 7.0D;
        private static final double TOO_CLOSE = 5.0D;
        private static final double TOO_FAR = 10.0D;
        private static final double MOVE_SPEED = 1.0D;
        private static final double CIRCLE_SPEED = 0.8D;
        private int repathTimer;
        private float circleDirection = 1.0F;

        KeepDistanceGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractHurricane.this.getTarget();
            return target != null && target.isAlive() && AbstractHurricane.this.getAnimationState() == ANIM_NONE && !AbstractHurricane.this.jumpActive;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.repathTimer = 0;
            this.circleDirection = AbstractHurricane.this.random.nextBoolean() ? 1.0F : -1.0F;
        }

        @Override
        public void tick() {
            LivingEntity target = AbstractHurricane.this.getTarget();
            if (target == null) {
                return;
            }
            AbstractHurricane.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.repathTimer > 0) {
                return;
            }
            double distance = AbstractHurricane.this.distanceTo(target);
            Vec3 toUs = new Vec3(AbstractHurricane.this.getX() - target.getX(), 0.0D, AbstractHurricane.this.getZ() - target.getZ());
            if (toUs.lengthSqr() < 1.0E-4D) {
                toUs = new Vec3(1.0D, 0.0D, 0.0D);
            }
            Vec3 destination;
            double speed = MOVE_SPEED;
            if (distance < TOO_CLOSE) {
                Vec3 away = DefaultRandomPos.getPosAway(AbstractHurricane.this, 8, 4, target.position());
                destination = away != null ? away : target.position().add(toUs.normalize().scale(IDEAL_DISTANCE));
                this.repathTimer = 8;
            } else if (distance > TOO_FAR) {
                destination = target.position().add(toUs.normalize().scale(IDEAL_DISTANCE));
                this.repathTimer = 10;
            } else {
                float angle = (float) Mth.atan2(toUs.z, toUs.x) + this.circleDirection * ((float) Math.PI / 6.0F);
                destination = target.position().add(Mth.cos(angle) * IDEAL_DISTANCE, 0.0D, Mth.sin(angle) * IDEAL_DISTANCE);
                speed = CIRCLE_SPEED;
                this.repathTimer = 20;
                if (AbstractHurricane.this.random.nextInt(4) == 0) {
                    this.circleDirection = -this.circleDirection;
                }
            }
            if (!AbstractHurricane.this.getNavigation().moveTo(destination.x, destination.y, destination.z, speed)) {
                this.circleDirection = -this.circleDirection;
                this.repathTimer = 5;
            }
        }

        @Override
        public void stop() {
            AbstractHurricane.this.getNavigation().stop();
        }
    }
}
