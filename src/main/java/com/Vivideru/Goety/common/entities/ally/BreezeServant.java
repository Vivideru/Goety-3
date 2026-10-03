package com.Vivideru.Goety.common.entities.ally;

import com.Polarice3.Goety.api.entities.IMobTyped;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.MobUtil;
import com.Vivideru.Goety.common.entities.projectiles.ServantWindCharge;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class BreezeServant extends Summoned implements IMobTyped {
    private static final int SLIDE_PARTICLES_AMOUNT = 20;
    private static final int IDLE_PARTICLES_AMOUNT = 1;
    private static final int JUMP_TRAIL_PARTICLES_AMOUNT = 3;
    private static final int JUMP_TRAIL_DURATION_TICKS = 5;
    private static final float FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD = 3.0F;
    private static final int WHIRL_SOUND_FREQUENCY_MIN = 1;
    private static final int WHIRL_SOUND_FREQUENCY_MAX = 80;
    private static final float KNOCKBACK_PER_BUFF_LEVEL = 0.5F;
    private static final ProjectileDeflection PROJECTILE_DEFLECTION = (projectile, entity, random) -> {
        entity.level().playSound(null, entity, SoundEvents.BREEZE_DEFLECT, entity.getSoundSource(), 1.0F, 1.0F);
        ProjectileDeflection.REVERSE.deflect(projectile, entity, random);
    };

    public final AnimationState idle = new AnimationState();
    public final AnimationState slide = new AnimationState();
    public final AnimationState slideBack = new AnimationState();
    public final AnimationState longJump = new AnimationState();
    public final AnimationState shoot = new AnimationState();
    public final AnimationState inhale = new AnimationState();
    private int jumpTrailStartedTick = 0;
    private int soundTick = 0;

    public BreezeServant(EntityType<? extends Owned> type, Level level) {
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
        this.goalSelector.addGoal(4, new BreezeAttackGoal(this));
        this.goalSelector.addGoal(7, new WanderGoal<>(this, 0.6D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.63D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.get(AttributesConfig.BreezeServantHealth))
                .add(Attributes.ARMOR, AttributesConfig.get(AttributesConfig.BreezeServantArmor))
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.get(AttributesConfig.BreezeServantRangeDamage))
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.get(AttributesConfig.BreezeServantHealth));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.get(AttributesConfig.BreezeServantArmor));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.get(AttributesConfig.BreezeServantRangeDamage));
    }

    @Override
    public ModMobType getGoetyMobType() {
        return ModMobType.UNDEFINED;
    }

    @Override
    public int xpReward() {
        return 10;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (this.level().isClientSide() && DATA_POSE.equals(accessor)) {
            this.resetAnimations();
            Pose pose = this.getPose();
            switch (pose) {
                case SHOOTING -> this.shoot.startIfStopped(this.tickCount);
                case INHALING -> this.longJump.startIfStopped(this.tickCount);
                case SLIDING -> this.slide.startIfStopped(this.tickCount);
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void resetAnimations() {
        this.shoot.stop();
        this.idle.stop();
        this.inhale.stop();
        this.longJump.stop();
    }

    @Override
    public void tick() {
        Pose pose = this.getPose();
        switch (pose) {
            case SHOOTING, INHALING, STANDING -> this.resetJumpTrail().emitGroundParticles(IDLE_PARTICLES_AMOUNT + this.getRandom().nextInt(1));
            case SLIDING -> this.emitGroundParticles(SLIDE_PARTICLES_AMOUNT);
            case LONG_JUMPING -> this.emitJumpTrailParticles();
            default -> {
            }
        }

        if (pose != Pose.SLIDING && this.slide.isStarted()) {
            this.slideBack.start(this.tickCount);
            this.slide.stop();
        }

        if (!this.level().isClientSide && this.isAlive()) {
            boolean walking = !this.getNavigation().isDone() && this.onGround();
            if (walking && pose == Pose.STANDING) {
                this.playSound(SoundEvents.BREEZE_SLIDE);
                this.setPose(Pose.SLIDING);
            } else if (!walking && pose == Pose.SLIDING && this.getNavigation().isDone()) {
                this.setPose(Pose.STANDING);
            }
        }

        this.soundTick = this.soundTick == 0 ? this.random.nextIntBetweenInclusive(WHIRL_SOUND_FREQUENCY_MIN, WHIRL_SOUND_FREQUENCY_MAX) : this.soundTick - 1;
        if (this.soundTick == 0) {
            this.playWhirlSound();
        }

        super.tick();
    }

    public BreezeServant resetJumpTrail() {
        this.jumpTrailStartedTick = 0;
        return this;
    }

    public void emitJumpTrailParticles() {
        if (++this.jumpTrailStartedTick <= JUMP_TRAIL_DURATION_TICKS) {
            BlockState blockstate = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
            Vec3 motion = this.getDeltaMovement();
            Vec3 pos = this.position().add(motion).add(0.0D, 0.1F, 0.0D);
            for (int i = 0; i < JUMP_TRAIL_PARTICLES_AMOUNT; i++) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void emitGroundParticles(int amount) {
        if (!this.isPassenger()) {
            Vec3 center = this.getBoundingBox().getCenter();
            Vec3 pos = new Vec3(center.x, this.position().y, center.z);
            BlockState blockstate = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                for (int i = 0; i < amount; i++) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public void playAmbientSound() {
        if (this.getTarget() == null || !this.onGround()) {
            this.level().playLocalSound(this, this.getAmbientSound(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void playWhirlSound() {
        float pitch = 0.7F + 0.4F * this.random.nextFloat();
        float volume = 0.8F + 0.2F * this.random.nextFloat();
        this.level().playLocalSound(this, SoundEvents.BREEZE_WHIRL, this.getSoundSource(), volume, pitch);
    }

    @Override
    public ProjectileDeflection deflection(Projectile projectile) {
        if (projectile.getType() != EntityType.BREEZE_WIND_CHARGE && projectile.getType() != EntityType.WIND_CHARGE && !(projectile instanceof ServantWindCharge)) {
            return this.getType().is(net.minecraft.tags.EntityTypeTags.DEFLECTS_PROJECTILES) ? PROJECTILE_DEFLECTION : ProjectileDeflection.NONE;
        }
        return ProjectileDeflection.NONE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BREEZE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BREEZE_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.onGround() ? SoundEvents.BREEZE_IDLE_GROUND : SoundEvents.BREEZE_IDLE_AIR;
    }

    public boolean withinInnerCircleRange(Vec3 pos) {
        Vec3 center = this.blockPosition().getCenter();
        return pos.closerThan(center, 4.0D, 10.0D);
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    public int getHeadRotSpeed() {
        return 25;
    }

    public double getSnoutYPosition() {
        return this.getEyeY() - 0.4D;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.getEntity() instanceof Breeze || source.getEntity() instanceof BreezeServant || super.isInvulnerableTo(source);
    }

    @Override
    public double getFluidJumpThreshold() {
        return this.getEyeHeight();
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        if (fallDistance > FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD) {
            this.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
        }
        return super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    public int getBuffLevel() {
        return this.hasEffect(GoetyEffects.BUFF) ? EffectsUtil.getAmplifier(this, GoetyEffects.BUFF.get()) + 1 : 0;
    }

    public float getWindChargeDamage() {
        return MobUtil.getSpecialAttackDamage(this, AttributesConfig.get(AttributesConfig.BreezeServantRangeDamage).floatValue());
    }

    public float getWindChargeKnockback() {
        return 1.0F + KNOCKBACK_PER_BUFF_LEVEL * this.getBuffLevel();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
                boolean rod = itemstack.is(Tags.Items.RODS_BREEZE);
                if ((rod || itemstack.is(Items.WIND_CHARGE)) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.BREEZE_IDLE_GROUND, 1.0F, 1.25F);
                    this.heal(rod ? 4.0F : 1.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    public static Vec3 randomPointBehindTarget(LivingEntity target, RandomSource random) {
        float yaw = target.yHeadRot + 180.0F + (float) random.nextGaussian() * 90.0F / 2.0F;
        float distance = Mth.lerp(random.nextFloat(), 4.0F, 8.0F);
        Vec3 offset = Vec3.directionFromRotation(0.0F, yaw).scale(distance);
        return target.position().add(offset);
    }

    public boolean hasLineOfSight(Vec3 pos) {
        Vec3 from = new Vec3(this.getX(), this.getY(), this.getZ());
        return pos.distanceTo(from) <= 50.0D
                && this.level().clip(new ClipContext(from, pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    static class BreezeAttackGoal extends Goal {
        private static final int ATTACK_RANGE_MIN_SQRT = 4;
        private static final int ATTACK_RANGE_MAX_SQRT = 256;
        private static final float PROJECTILE_MOVEMENT_SCALE = 0.7F;
        private static final float PROJECTILE_INACCURACY = 1.0F;
        private static final int SHOOT_INITIAL_DELAY_TICKS = 15;
        private static final int SHOOT_RECOVER_DELAY_TICKS = 4;
        private static final int SHOOT_COOLDOWN_TICKS = 10;
        private static final int SHOOT_WINDOW_AFTER_JUMP = 100;
        private static final int SHOOT_WINDOW_AFTER_SLIDE = 60;
        private static final int JUMP_COOLDOWN_TICKS = 10;
        private static final int JUMP_COOLDOWN_WHEN_HURT_TICKS = 2;
        private static final int INHALING_DURATION_TICKS = 10;
        private static final int REQUIRED_AIR_BLOCKS_ABOVE = 4;
        private static final float MAX_JUMP_VELOCITY = 1.4F;
        private static final float SLIDE_SPEED = 0.6F;
        private static final int SLIDE_TIMEOUT_TICKS = 60;
        private static final int STUCK_TICKS_BEFORE_SHOOTING = 20;
        private static final List<Integer> ALLOWED_ANGLES = List.of(40, 55, 60, 75, 80);

        private enum Phase {
            REPOSITION,
            INHALING,
            JUMPING,
            SLIDING,
            SHOOTING
        }

        private final BreezeServant breeze;
        private Phase phase = Phase.REPOSITION;
        private int timer;
        private int shootWindow;
        private int shootCooldown;
        private int jumpCooldown;
        private int stuckTicks;
        private boolean fired;
        @Nullable
        private BlockPos jumpTarget;
        @Nullable
        private Vec3 slideTarget;

        BreezeAttackGoal(BreezeServant breeze) {
            this.breeze = breeze;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.breeze.getTarget();
            return target != null && target.isAlive() && this.breeze.canAttack(target);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.phase = Phase.REPOSITION;
            this.timer = 0;
            this.shootWindow = 0;
            this.stuckTicks = 0;
        }

        @Override
        public void stop() {
            this.breeze.getNavigation().stop();
            this.breeze.setDiscardFriction(false);
            if (this.breeze.getPose() != Pose.STANDING) {
                this.breeze.setPose(Pose.STANDING);
            }
            this.jumpTarget = null;
            this.slideTarget = null;
        }

        @Override
        public void tick() {
            LivingEntity target = this.breeze.getTarget();
            if (target == null) {
                return;
            }
            if (this.shootCooldown > 0) {
                --this.shootCooldown;
            }
            if (this.jumpCooldown > 0) {
                --this.jumpCooldown;
            }
            if (this.shootWindow > 0) {
                --this.shootWindow;
            }
            switch (this.phase) {
                case REPOSITION -> this.tickReposition(target);
                case INHALING -> this.tickInhaling();
                case JUMPING -> this.tickJumping();
                case SLIDING -> this.tickSliding(target);
                case SHOOTING -> this.tickShooting(target);
            }
        }

        private boolean isTargetWithinShootRange(LivingEntity target) {
            double distance = this.breeze.position().distanceToSqr(target.position());
            return distance > ATTACK_RANGE_MIN_SQRT && distance < ATTACK_RANGE_MAX_SQRT;
        }

        private void tickReposition(LivingEntity target) {
            this.breeze.getLookControl().setLookAt(target, 10.0F, 10.0F);
            if (this.shootWindow > 0 && this.shootCooldown <= 0 && this.isTargetWithinShootRange(target) && this.breeze.getPose() == Pose.STANDING) {
                this.beginShooting();
                return;
            }
            boolean grounded = this.breeze.onGround() || this.breeze.isInWater();
            if (grounded && this.jumpCooldown <= 0 && this.tryStartJump(target)) {
                return;
            }
            if (this.breeze.onGround() && !this.breeze.isInWater() && this.breeze.getPose() == Pose.STANDING) {
                this.startSlide(target);
                return;
            }
            if (++this.stuckTicks > STUCK_TICKS_BEFORE_SHOOTING && this.shootCooldown <= 0 && this.isTargetWithinShootRange(target)) {
                this.beginShooting();
            }
        }

        private boolean tryStartJump(LivingEntity target) {
            if (target.distanceTo(this.breeze) - 4.0F <= 0.0F) {
                return false;
            }
            if (!this.canJumpFromCurrentPosition()) {
                return false;
            }
            BlockPos landing = this.snapToSurface(randomPointBehindTarget(target, this.breeze.getRandom()));
            if (landing == null) {
                return false;
            }
            BlockState below = this.breeze.level().getBlockState(landing.below());
            if (this.breeze.getType().isBlockDangerous(below)) {
                return false;
            }
            if (!this.breeze.hasLineOfSight(landing.getCenter()) && !this.breeze.hasLineOfSight(landing.above(REQUIRED_AIR_BLOCKS_ABOVE).getCenter())) {
                return false;
            }
            this.jumpTarget = landing;
            this.phase = Phase.INHALING;
            this.timer = 0;
            this.stuckTicks = 0;
            this.breeze.getNavigation().stop();
            this.breeze.setPose(Pose.INHALING);
            this.breeze.level().playSound(null, this.breeze, SoundEvents.BREEZE_CHARGE, this.breeze.getSoundSource(), 1.0F, 1.0F);
            this.breeze.lookAt(EntityAnchorArgument.Anchor.EYES, landing.getCenter());
            return true;
        }

        private boolean canJumpFromCurrentPosition() {
            BlockPos pos = this.breeze.blockPosition();
            for (int i = 1; i <= REQUIRED_AIR_BLOCKS_ABOVE; i++) {
                BlockPos above = pos.relative(Direction.UP, i);
                if (!this.breeze.level().getBlockState(above).isAir() && !this.breeze.level().getFluidState(above).is(FluidTags.WATER)) {
                    return false;
                }
            }
            return true;
        }

        @Nullable
        private BlockPos snapToSurface(Vec3 pos) {
            ClipContext down = new ClipContext(pos, pos.relative(Direction.DOWN, 10.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.breeze);
            HitResult hit = this.breeze.level().clip(down);
            if (hit.getType() == HitResult.Type.BLOCK) {
                return BlockPos.containing(hit.getLocation()).above();
            }
            ClipContext up = new ClipContext(pos, pos.relative(Direction.UP, 10.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.breeze);
            HitResult hitUp = this.breeze.level().clip(up);
            return hitUp.getType() == HitResult.Type.BLOCK ? BlockPos.containing(hitUp.getLocation()).above() : null;
        }

        private void tickInhaling() {
            if (this.jumpTarget != null) {
                this.breeze.lookAt(EntityAnchorArgument.Anchor.EYES, this.jumpTarget.getCenter());
            }
            if (++this.timer < INHALING_DURATION_TICKS) {
                return;
            }
            Vec3 velocity = this.jumpTarget == null ? null : this.calculateOptimalJumpVector(Vec3.atBottomCenterOf(this.jumpTarget)).orElse(null);
            if (velocity == null) {
                this.breeze.setPose(Pose.STANDING);
                this.jumpCooldown = JUMP_COOLDOWN_TICKS;
                this.phase = Phase.REPOSITION;
                return;
            }
            this.breeze.playSound(SoundEvents.BREEZE_JUMP, 1.0F, 1.0F);
            this.breeze.setPose(Pose.LONG_JUMPING);
            this.breeze.setYRot(this.breeze.yBodyRot);
            this.breeze.setDiscardFriction(true);
            this.breeze.setDeltaMovement(velocity);
            this.phase = Phase.JUMPING;
            this.timer = 0;
        }

        private Optional<Vec3> calculateOptimalJumpVector(Vec3 target) {
            for (int angle : net.minecraft.Util.toShuffledList(ALLOWED_ANGLES.stream(), this.breeze.getRandom())) {
                Optional<Vec3> vector = LongJumpUtil.calculateJumpVectorForAngle(this.breeze, target, MAX_JUMP_VELOCITY, angle, false);
                if (vector.isPresent()) {
                    return vector;
                }
            }
            return Optional.empty();
        }

        private void tickJumping() {
            ++this.timer;
            boolean landed = this.breeze.onGround() || (this.breeze.isInWater() && this.timer > 5);
            if (landed || this.timer > 100) {
                this.breeze.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
                this.breeze.setPose(Pose.STANDING);
                this.breeze.setDiscardFriction(false);
                this.jumpCooldown = this.breeze.getLastHurtByMob() != null ? JUMP_COOLDOWN_WHEN_HURT_TICKS : JUMP_COOLDOWN_TICKS;
                this.shootWindow = SHOOT_WINDOW_AFTER_JUMP;
                this.jumpTarget = null;
                this.phase = Phase.REPOSITION;
            }
        }

        private void startSlide(LivingEntity target) {
            Vec3 destination = null;
            if (this.breeze.withinInnerCircleRange(target.position())) {
                Vec3 away = DefaultRandomPos.getPosAway(this.breeze, 5, 5, target.position());
                if (away != null && this.breeze.hasLineOfSight(away) && target.distanceToSqr(away.x, away.y, away.z) > target.distanceToSqr(this.breeze)) {
                    destination = away;
                }
            }
            if (destination == null) {
                destination = this.breeze.getRandom().nextBoolean() ? randomPointBehindTarget(target, this.breeze.getRandom()) : this.randomPointInMiddleCircle(target);
            }
            this.slideTarget = destination;
            this.phase = Phase.SLIDING;
            this.timer = 0;
            this.stuckTicks = 0;
            this.breeze.playSound(SoundEvents.BREEZE_SLIDE);
            this.breeze.setPose(Pose.SLIDING);
            BlockPos pos = BlockPos.containing(destination);
            this.breeze.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), SLIDE_SPEED);
        }

        private Vec3 randomPointInMiddleCircle(LivingEntity target) {
            Vec3 toTarget = target.position().subtract(this.breeze.position());
            double distance = toTarget.length() - Mth.lerp(this.breeze.getRandom().nextDouble(), 8.0D, 4.0D);
            Vec3 offset = toTarget.normalize().multiply(distance, distance, distance);
            return this.breeze.position().add(offset);
        }

        private void tickSliding(LivingEntity target) {
            ++this.timer;
            boolean arrived = this.slideTarget != null && this.breeze.position().closerThan(this.slideTarget, 1.5D);
            boolean stuck = this.breeze.getNavigation().isDone() && this.timer > 2;
            if (arrived || stuck || this.timer > SLIDE_TIMEOUT_TICKS) {
                this.breeze.getNavigation().stop();
                this.breeze.setPose(Pose.STANDING);
                this.shootWindow = SHOOT_WINDOW_AFTER_SLIDE;
                this.slideTarget = null;
                this.phase = Phase.REPOSITION;
            }
        }

        private void beginShooting() {
            this.phase = Phase.SHOOTING;
            this.timer = 0;
            this.fired = false;
            this.stuckTicks = 0;
            this.breeze.getNavigation().stop();
            this.breeze.setPose(Pose.SHOOTING);
            this.breeze.playSound(SoundEvents.BREEZE_INHALE, 1.0F, 1.0F);
        }

        private void tickShooting(LivingEntity target) {
            ++this.timer;
            this.breeze.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
            if (this.timer >= SHOOT_INITIAL_DELAY_TICKS && !this.fired) {
                this.fired = true;
                if (this.isFacingTarget(target) && this.breeze.level() instanceof ServerLevel serverLevel) {
                    double dx = target.getX() - this.breeze.getX();
                    double dy = target.getY(target.isPassenger() ? 0.8D : 0.3D) - this.breeze.getY(0.5D);
                    double dz = target.getZ() - this.breeze.getZ();
                    ServantWindCharge charge = new ServantWindCharge(serverLevel, this.breeze, this.breeze.getX(), this.breeze.getSnoutYPosition(), this.breeze.getZ(), this.breeze.getWindChargeDamage(), this.breeze.getWindChargeKnockback());
                    this.breeze.playSound(SoundEvents.BREEZE_SHOOT, 1.5F, 1.0F);
                    charge.shoot(dx, dy, dz, PROJECTILE_MOVEMENT_SCALE, PROJECTILE_INACCURACY);
                    serverLevel.addFreshEntity(charge);
                }
            }
            if (this.timer >= SHOOT_INITIAL_DELAY_TICKS + SHOOT_RECOVER_DELAY_TICKS) {
                if (this.breeze.getPose() == Pose.SHOOTING) {
                    this.breeze.setPose(Pose.STANDING);
                }
                this.shootCooldown = SHOOT_COOLDOWN_TICKS;
                this.phase = Phase.REPOSITION;
            }
        }

        private boolean isFacingTarget(LivingEntity target) {
            Vec3 view = this.breeze.getViewVector(1.0F);
            Vec3 toTarget = target.position().subtract(this.breeze.position()).normalize();
            return view.dot(toTarget) > 0.5D;
        }
    }
}
