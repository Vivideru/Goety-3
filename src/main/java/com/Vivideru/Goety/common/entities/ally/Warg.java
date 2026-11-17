package com.Vivideru.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemBlockEntity;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemHooks;
import com.Vivideru.Goety.common.entities.ai.MovementHold;
import com.Vivideru.Goety.common.items.VivideruItems;
import com.Vivideru.Goety.common.world.WargTotemData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class Warg extends BlackWolf implements PlayerRideableJumping {
    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_BITE = 1;
    public static final int ATTACK_SPIN = 2;
    public static final int ATTACK_SLASH = 3;
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(Warg.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Warg.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(Warg.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TICKS = SynchedEntityData.defineId(Warg.class, EntityDataSerializers.INT);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();
    public final AnimationState groundedAnimationState = new AnimationState();
    public final AnimationState jumpStartAnimationState = new AnimationState();
    public final AnimationState jumpLoopAnimationState = new AnimationState();
    public final AnimationState jumpLandAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState spinAnimationState = new AnimationState();
    public final AnimationState slashAnimationState = new AnimationState();
    private static final int AIRBORNE_ANIMATION_TICKS = 4;
    // Lengths of the jump take-off (0.38 s) and landing (0.25 s) animations.
    private static final int JUMP_START_TICKS = 8;
    private static final int JUMP_LAND_TICKS = 5;
    private static final double JUMP_RISE_PER_TICK = 0.05D;
    // Horizontal blocks per tick: walking servants move about 0.15, a ridden Warg 0.4 or more.
    private static final float RUN_START_SPEED = 0.22F;
    private static final float RUN_STOP_SPEED = 0.18F;
    private static final float WALK_ANIMATION_SPEED = 0.15F;
    private static final float RUN_ANIMATION_SPEED = 0.4F;
    // Sword attacks last as long as their animations (1 s slash, 1.92 s spin) and hit on their strike frames, one tick after
    // the animation time because clients start it a tick later. The spin turns twice: fastest at 0.5 s and, back the other
    // way, at 1.55 s.
    private static final int SLASH_ATTACK_TICKS = 20;
    private static final int SLASH_HIT_TICKS_LEFT = 8;
    private static final int SPIN_ATTACK_TICKS = 38;
    private static final int SPIN_FIRST_HIT_TICKS_LEFT = 27;
    private static final int SPIN_SECOND_HIT_TICKS_LEFT = 6;
    private int airborneTicks;
    private int airborneCountedTick = -1;
    private int airAnimationTicks;
    private int landAnimationTicks;
    private boolean leapAnimation;
    private boolean runningAnimation;
    private float animationMoveSpeed;
    private int howlCooldown;
    private boolean howling;
    @Nullable
    private LivingEntity queuedTarget;
    private float lockedAttackYaw;
    private int swordAttackCooldown;
    private float playerJumpPendingScale;
    private float riderJumpOffset;
    private boolean registryChecked;

    public Warg(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal -> goal instanceof RandomLookAroundGoal);
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && Warg.this.getRandom().nextInt(4) == 0;
            }
        });
        this.goalSelector.addGoal(3, new WargSwordAttackGoal());
        this.goalSelector.addGoal(2, new SkeletalHowlGoal());
        // The wolf leap inherited from the Black Wolf, with this mob's own height (none for the Cerberus).
        this.goalSelector.removeAllGoals(goal -> goal instanceof LeapAtTargetGoal);
        float leap = this.getLeapPower();
        if (leap > 0.0F) {
            this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, leap) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !Warg.this.isStaying();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !Warg.this.isStaying();
                }
            });
        }
    }

    /** Upward speed of the leap at the target: 0.27 jumps about half as high as the Black Wolf's 0.4. 0 disables it. */
    protected float getLeapPower() {
        return 0.27F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.STEP_HEIGHT, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.get(AttributesConfig.WargHealth))
                .add(Attributes.ARMOR, AttributesConfig.get(AttributesConfig.WargArmor))
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.get(AttributesConfig.WargDamage));
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.get(AttributesConfig.WargHealth));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.get(AttributesConfig.WargArmor));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.get(AttributesConfig.WargDamage));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SADDLED, false);
        builder.define(VARIANT, Variant.BLACK.ordinal());
        builder.define(ATTACK_TYPE, ATTACK_NONE);
        builder.define(ATTACK_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Saddled", this.isSaddled());
        tag.putInt("WargVariant", this.getVariant().ordinal());
        tag.putInt("HowlCooldown", this.howlCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddled(tag.getBoolean("Saddled"));
        this.setVariant(Variant.byId(tag.getInt("WargVariant")));
        this.howlCooldown = tag.getInt("HowlCooldown");
    }

    @Override
    public void tick() {
        super.tick();
        this.riderJumpOffset = Mth.approach(this.riderJumpOffset, this.onGround() ? 0.0F : 0.25F, 0.05F);
        if (!this.level().isClientSide) {
            this.registerPersistentAssignment();
            this.tickSwordAttack();
            if (this.howlCooldown > 0) {
                --this.howlCooldown;
            }
        } else {
            this.updateAnimationStates();
        }
    }

    private void registerPersistentAssignment() {
        if (!this.registryChecked && this.level() instanceof ServerLevel serverLevel && this.getOwnerId() != null && this.getRevivePos() != null) {
            WargTotemData.get(serverLevel).register(this.getUUID(), this.getOwnerId(), this.getReviveLevel(), this.getRevivePos());
            this.registryChecked = true;
        }
    }

    protected boolean tickAirborneAnimation() {
        if (this.airborneCountedTick != this.tickCount) {
            this.airborneCountedTick = this.tickCount;
            if (this.onGround()) {
                this.airborneTicks = 0;
            } else if (this.airborneTicks < AIRBORNE_ANIMATION_TICKS) {
                ++this.airborneTicks;
            }
        }
        return this.airborneTicks >= AIRBORNE_ANIMATION_TICKS;
    }

    private void updateAnimationStates() {
        boolean airborne = this.tickAirborneAnimation();
        boolean attacking = this.getAttackTicks() > 0;
        boolean rising = !this.onGround() && this.getY() - this.yo > JUMP_RISE_PER_TICK;
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        this.animationMoveSpeed = Mth.lerp(0.3F, this.animationMoveSpeed, (float) Math.sqrt(dx * dx + dz * dz));

        // A real jump plays the take-off, then the air loop; a fall goes straight to the loop. Landing follows either.
        if (!attacking && (airborne || rising)) {
            if (this.airAnimationTicks == 0) {
                this.leapAnimation = rising;
            }
            ++this.airAnimationTicks;
            this.landAnimationTicks = 0;
        } else {
            if (this.airAnimationTicks > 0) {
                this.landAnimationTicks = !attacking && this.onGround() ? JUMP_LAND_TICKS : 0;
            } else if (this.landAnimationTicks > 0) {
                --this.landAnimationTicks;
            }
            this.airAnimationTicks = 0;
        }
        boolean inAir = this.airAnimationTicks > 0;
        boolean takeOff = inAir && this.leapAnimation && this.airAnimationTicks <= JUMP_START_TICKS;
        boolean landing = !inAir && !attacking && this.landAnimationTicks > 0;
        boolean onFeet = !attacking && !inAir && !landing;
        boolean moving = onFeet && !this.isSitting() && this.walkAnimation.speed() > 0.05F;
        this.runningAnimation = moving && this.animationMoveSpeed > (this.runningAnimation ? RUN_STOP_SPEED : RUN_START_SPEED);

        setAnimation(this.biteAnimationState, attacking && this.getAttackType() == ATTACK_BITE);
        setAnimation(this.spinAnimationState, attacking && this.getAttackType() == ATTACK_SPIN);
        setAnimation(this.slashAnimationState, attacking && this.getAttackType() == ATTACK_SLASH);
        setAnimation(this.jumpStartAnimationState, takeOff);
        setAnimation(this.jumpLoopAnimationState, inAir && !takeOff);
        setAnimation(this.jumpLandAnimationState, landing);
        setAnimation(this.groundedAnimationState, onFeet && this.isSitting());
        setAnimation(this.runAnimationState, this.runningAnimation);
        setAnimation(this.walkAnimationState, moving && !this.runningAnimation);
        setAnimation(this.idleAnimationState, onFeet && !this.isSitting() && !moving);
    }

    /** Playback speed of the walk animation, following how fast the Warg actually moves. */
    public float getWalkAnimationScale() {
        return Mth.clamp(this.animationMoveSpeed / WALK_ANIMATION_SPEED, 0.7F, 1.4F);
    }

    /** Playback speed of the run animation, following how fast the Warg actually moves. */
    public float getRunAnimationScale() {
        return Mth.clamp(this.animationMoveSpeed / RUN_ANIMATION_SPEED, 0.8F, 1.3F);
    }

    private void setAnimation(AnimationState state, boolean running) {
        if (running) {
            state.startIfStopped(this.tickCount);
        } else {
            state.stop();
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.hasSword()) {
            if (this.getAttackTicks() <= 0 && target instanceof LivingEntity living) {
                this.startSwordAttack(living);
            }
            return true;
        }
        this.setAttack(ATTACK_BITE, 20);
        return super.doHurtTarget(target);
    }

    private void tickSwordAttack() {
        int ticks = this.getAttackTicks();
        if (ticks <= 0) {
            if (this.swordAttackCooldown > 0) {
                --this.swordAttackCooldown;
            }
            return;
        }
        if (this.getAttackType() == ATTACK_BITE) {
            this.entityData.set(ATTACK_TICKS, ticks - 1);
            return;
        }
        this.applyLockedAttackFacing();
        this.getNavigation().stop();
        if (this.getAttackType() == ATTACK_SPIN && (ticks == SPIN_FIRST_HIT_TICKS_LEFT || ticks == SPIN_SECOND_HIT_TICKS_LEFT)) {
            this.performSpinAttack();
        } else if (this.getAttackType() == ATTACK_SLASH && ticks == SLASH_HIT_TICKS_LEFT) {
            this.performSlashAttack();
        }
        this.entityData.set(ATTACK_TICKS, ticks - 1);
        if (ticks == 1) {
            this.entityData.set(ATTACK_TYPE, ATTACK_NONE);
            this.queuedTarget = null;
            this.swordAttackCooldown = 2;
        }
    }

    private void startSwordAttack(LivingEntity target) {
        this.queuedTarget = target;
        this.lockAttackFacing(target);
        int attack = this.horizontalDistanceToSqr(target) > 4.0D ? ATTACK_SLASH : ATTACK_SPIN;
        this.setAttack(attack, attack == ATTACK_SPIN ? SPIN_ATTACK_TICKS : SLASH_ATTACK_TICKS);
        this.getNavigation().stop();
    }

    private double horizontalDistanceToSqr(LivingEntity target) {
        double x = target.getX() - this.getX();
        double z = target.getZ() - this.getZ();
        return x * x + z * z;
    }

    private void lockAttackFacing(LivingEntity target) {
        double x = target.getX() - this.getX();
        double z = target.getZ() - this.getZ();
        if (x * x + z * z > 1.0E-4D) {
            this.lockedAttackYaw = (float)(Mth.atan2(-x, z) * (180.0D / Math.PI));
        } else {
            this.lockedAttackYaw = this.getYRot();
        }
        this.applyLockedAttackFacing();
    }

    private void applyLockedAttackFacing() {
        this.setYRot(this.lockedAttackYaw);
        this.yBodyRot = this.lockedAttackYaw;
        this.yHeadRot = this.lockedAttackYaw + this.headYawOffsetToTarget();
    }

    private float headYawOffsetToTarget() {
        if (this.queuedTarget == null) {
            return 0.0F;
        }
        double x = this.queuedTarget.getX() - this.getX();
        double z = this.queuedTarget.getZ() - this.getZ();
        if (x * x + z * z <= 1.0E-4D) {
            return 0.0F;
        }
        float targetYaw = (float) (Mth.atan2(-x, z) * (180.0D / Math.PI));
        return Mth.clamp(Mth.wrapDegrees(targetYaw - this.lockedAttackYaw), -(float) this.getMaxHeadYRot(), (float) this.getMaxHeadYRot());
    }

    private void performSpinAttack() {
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.25D), this::canHitWithSword);
        for (LivingEntity target : targets) {
            // two hits per spin, so each deals half: together they match one full sword hit
            this.hurtWithSword(target, 0.5F);
        }
    }

    private void performSlashAttack() {
        Vec3 forward = Vec3.directionFromRotation(0.0F, this.lockedAttackYaw).normalize();
        AABB swept = this.getBoundingBox().expandTowards(forward.scale(2.75D)).inflate(0.75D, 0.35D, 0.75D);
        this.move(MoverType.SELF, forward.scale(2.75D));
        if (this.queuedTarget != null && this.queuedTarget.getBoundingBox().intersects(swept) && this.canHitWithSword(this.queuedTarget)) {
            this.hurtWithSword(this.queuedTarget);
            return;
        }
        this.level().getEntitiesOfClass(LivingEntity.class, swept, this::canHitWithSword).stream().findFirst().ifPresent(this::hurtWithSword);
    }

    private boolean canHitWithSword(LivingEntity target) {
        return target != this && target.isAlive() && !this.isAlliedTo(target);
    }

    private void hurtWithSword(LivingEntity target) {
        this.hurtWithSword(target, 1.0F);
    }

    private void hurtWithSword(LivingEntity target, float damageScale) {
        DamageSource source = this.getServantAttack();
        ItemStack sword = this.getMainHandItem();
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (this.level() instanceof ServerLevel serverLevel) {
            damage = EnchantmentHelper.modifyDamage(serverLevel, sword, target, source, damage);
        }
        if (target.hurt(source, damage * damageScale)) {
            this.curseTarget(target);
            if (this.level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, source, sword);
            }
        }
    }

    @Override
    protected float getJumpPower() {
        return 0.55F;
    }

    @Override
    public float getTailAngle() {
        float missing = this.getMaxHealth() > 0.0F ? Mth.clamp(1.0F - this.getHealth() / this.getMaxHealth(), 0.0F, 1.0F) : 0.0F;
        return Mth.lerp(missing, 0.55F * (float) Math.PI - 0.30F, -0.9F);
    }

    @Override
    protected AABB getAttackBoundingBox() {
        AABB attackBox = super.getAttackBoundingBox();
        return this.hasSword() ? attackBox.inflate(1.75D, 0.5D, 1.75D) : attackBox;
    }

    @Override
    public int getMaxHeadXRot() {
        return 25;
    }

    @Override
    public int getMaxHeadYRot() {
        return 35;
    }

    @Override
    public int getHeadRotSpeed() {
        return 5;
    }

    @Override
    public void curseTarget(Entity entity) {
        if (this.getVariant() == Variant.COLD && entity instanceof LivingEntity livingEntity) {
            int amplifier = 0;
            Holder<MobEffect> effect = MobEffects.MOVEMENT_SLOWDOWN;
            if (CuriosFinder.hasFrostRobes(this.getMasterOwner())) {
                if (!entity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)) {
                    effect = GoetyEffects.FREEZING;
                } else {
                    amplifier = 1;
                }
            }
            livingEntity.addEffect(new MobEffectInstance(effect, MathHelper.secondsToTicks(5), amplifier), this);
        } else if (this.getVariant() == Variant.MODERATE && entity instanceof LivingEntity livingEntity) {
            int amplifier = CuriosFinder.hasStormRobes(this.getMasterOwner()) ? 1 : 0;
            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SPASMS, MathHelper.secondsToTicks(5), amplifier), this);
        } else if (this.getVariant() != Variant.WARM) {
            super.curseTarget(entity);
        }
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float amount) {
        amount = super.getDamageAfterMagicAbsorb(source, amount);
        if (this.getVariant() == Variant.MODERATE
                && (ModDamageSource.shockAttacks(source) || source.is(DamageTypeTags.IS_LIGHTNING))) {
            amount *= 0.5F;
        }
        return amount;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return super.canBeAffected(effect)
                && (this.getVariant() != Variant.MODERATE || !effect.getEffect().is(GoetyEffects.SPASMS));
    }

    @Override
    protected int calculateFallDamage(float distance, float multiplier) {
        return distance <= 8.0F ? 0 : super.calculateFallDamage(distance - 5.0F, multiplier);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return this.isSaddled() && passenger instanceof Player player && this.isOwnedByPlayer(player) ? player : null;
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float sideways = player.xxa * 0.5F;
        float forward = player.zza;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }
        return new Vec3(sideways, 0.0D, forward);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        Vec2 rotation = new Vec2(player.getXRot() * 0.5F, player.getYRot());
        this.setRot(rotation.y, rotation.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        if (this.onGround() && this.playerJumpPendingScale > 0.0F) {
            Vec3 movement = this.getDeltaMovement();
            double verticalPower = 0.65D + 0.20D * this.playerJumpPendingScale;
            this.setDeltaMovement(movement.x, verticalPower, movement.z);
            if (travelVector.z > 0.0D) {
                double forwardPower = 1.10D * this.playerJumpPendingScale;
                float yaw = this.getYRot() * ((float)Math.PI / 180F);
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * forwardPower, 0.0D, Mth.cos(yaw) * forwardPower));
            }
            this.hasImpulse = true;
            this.playerJumpPendingScale = 0.0F;
        }
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (player.isSprinting() ? 1.25F : 1.0F);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            Vec3 facing = Vec3.directionFromRotation(0.0F, this.getYRot());
            Vec3 saddleOffset = facing.scale(-0.68D);
            double bounce = 0.04D * Mth.cos(this.walkAnimation.position() * 0.7F) * this.walkAnimation.speed();
            moveFunction.accept(passenger, this.getX() + saddleOffset.x,
                    this.getY() + 0.75D + bounce + this.riderJumpOffset, this.getZ() + saddleOffset.z);
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.isSaddled() && this.getPassengers().isEmpty() && passenger instanceof Player;
    }

    @Override
    public void onPlayerJump(int strength) {
        strength = Mth.clamp(strength, 0, 90);
        this.playerJumpPendingScale = strength >= 90 ? 1.0F : 0.4F + 0.6F * strength / 90.0F;
    }

    @Override
    public boolean canJump() {
        return this.isSaddled() && this.onGround();
    }

    @Override
    public void handleStartJump(int strength) {
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (this.isOwnedByPlayer(player)) {
            if (VivideruItems.isVivideruWolfArmor(held)) {
                return InteractionResult.FAIL;
            }
            if (VivideruItems.isWargArmor(held) && this.getBodyArmorItem().isEmpty()) {
                this.setBodyArmorItem(held.copyWithCount(1));
                this.setDropChance(EquipmentSlot.BODY, 2.0F);
                consumeOne(player, held);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (held.canPerformAction(ItemAbilities.SHEARS_REMOVE_ARMOR) && this.hasWargArmor()
                    && (!EnchantmentHelper.has(this.getBodyArmorItem(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || player.isCreative())) {
                held.hurtAndBreak(1, player, getSlotForHand(hand));
                this.spawnAtLocation(this.getBodyArmorItem().copy());
                this.setBodyArmorItem(ItemStack.EMPTY);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (isUsableSword(held) && this.getMainHandItem().isEmpty()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, held.copyWithCount(1));
                this.setDropChance(EquipmentSlot.MAINHAND, 2.0F);
                consumeOne(player, held);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (held.is(Items.SADDLE) && !this.isSaddled()) {
                this.setSaddled(true);
                this.playSound(SoundEvents.HORSE_SADDLE, 0.5F, 0.85F);
                consumeOne(player, held);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (held.isEmpty() && this.hasSword()) {
                if (!this.level().isClientSide) {
                    ItemStack sword = this.getMainHandItem().copy();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    player.setItemInHand(hand, sword);
                    this.playSound(SoundEvents.ITEM_PICKUP, 0.35F, 1.0F);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (player.isCrouching() && held.isEmpty()) {
                if (this.isSaddled()) {
                    this.setSaddled(false);
                    this.spawnAtLocation(Items.SADDLE);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }
            if (!player.isCrouching() && held.isEmpty() && this.isSaddled()) {
                if (!this.level().isClientSide) {
                    player.setYRot(this.getYRot());
                    player.setXRot(this.getXRot());
                    player.startRiding(this);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    private static void consumeOne(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    @Override
    public boolean canUseSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.BODY || slot == EquipmentSlot.MAINHAND || super.canUseSlot(slot);
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && armor != null && attack != null) {
            health.setBaseValue(AttributesConfig.get(AttributesConfig.WargHealth) * (upgraded ? 1.5D : 1.0D));
            armor.setBaseValue(AttributesConfig.get(AttributesConfig.WargArmor) + (upgraded ? 1.0D : 0.0D));
            attack.setBaseValue(AttributesConfig.get(AttributesConfig.WargDamage) + (upgraded ? 1.0D : 0.0D));
        }
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }
        this.releaseTotemSlot(level);
    }

    @Override
    public void dismiss() {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.releaseTotemSlot(serverLevel);
        }
        super.dismiss();
    }

    private void releaseTotemSlot(ServerLevel level) {
        WolfTotemBlockEntity totem = WolfTotemHooks.getTotem((LivingEntity) this);
        if (totem != null) {
            totem.releaseWarg(this.getUUID());
        }
        WargTotemData.get(level).unregister(this.getUUID());
    }

    public boolean hasSword() {
        return isUsableSword(this.getMainHandItem());
    }

    private static boolean isUsableSword(ItemStack stack) {
        return stack.is(ItemTags.SWORDS) || stack.getItem() instanceof SwordItem
                || stack.canPerformAction(ItemAbilities.SWORD_DIG);
    }

    public boolean hasWargArmor() {
        return VivideruItems.isWargArmor(this.getBodyArmorItem());
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    public Variant getVariant() {
        return Variant.byId(this.entityData.get(VARIANT));
    }

    @Override
    public ModMobType getGoetyMobType() {
        return this.getVariant() == Variant.SKELETAL ? ModMobType.UNDEAD : super.getGoetyMobType();
    }

    public void setVariant(Variant variant) {
        this.entityData.set(VARIANT, variant.ordinal());
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public int getAttackTicks() {
        return this.entityData.get(ATTACK_TICKS);
    }

    public boolean isHowling() {
        return this.howling;
    }

    private void setAttack(int type, int ticks) {
        this.entityData.set(ATTACK_TYPE, type);
        this.entityData.set(ATTACK_TICKS, ticks);
    }

    private class SkeletalHowlGoal extends Goal {
        private static final int HOWL_TIME = MathHelper.secondsToTicks(3.25F);
        private static final int RALLY_TIME = MathHelper.secondsToTicks(3);
        private static final int HOWL_COOLDOWN = 100;
        private static final double HOWL_RANGE = 8.0D;
        private int howlTime;

        private SkeletalHowlGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = Warg.this.getTarget();
            return Warg.this.getVariant() == Variant.SKELETAL
                    && target != null && target.isAlive()
                    && Warg.this.howlCooldown <= 0
                    && Warg.this.getAttackTicks() <= 0
                    && Warg.this.onGround()
                    && Warg.this.getRandom().nextBoolean();
        }

        @Override
        public boolean canContinueToUse() {
            return this.howlTime > 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.howlTime = HOWL_TIME;
            Warg.this.howling = true;
            this.holdStill();
            Warg.this.playSound(ModSounds.SKELETON_WOLF_HOWL.get(), 1.5F, 0.8F);
        }

        @Override
        public void stop() {
            Warg.this.howling = false;
            Warg.this.howlCooldown = HOWL_COOLDOWN;
        }

        @Override
        public void tick() {
            --this.howlTime;
            this.holdStill();
            if (this.howlTime == RALLY_TIME) {
                for (LivingEntity livingEntity : Warg.this.level().getEntitiesOfClass(LivingEntity.class, Warg.this.getBoundingBox().inflate(HOWL_RANGE))) {
                    if (livingEntity != Warg.this && this.isRallied(livingEntity)) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, MathHelper.secondsToTicks(5)));
                    }
                }
            }
        }

        private void holdStill() {
            MovementHold.holdStill(Warg.this);
            Warg.this.setDeltaMovement(Warg.this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
        }

        private boolean isRallied(LivingEntity livingEntity) {
            if (Warg.this.isHostile() && livingEntity instanceof AbstractSkeleton) {
                return true;
            }
            boolean skeletonType = livingEntity instanceof AbstractSkeletonServant
                    || livingEntity instanceof SkeletonWolf
                    || livingEntity instanceof Warg warg && warg.getVariant() == Variant.SKELETAL
                    || livingEntity.getType().is(ModTags.EntityTypes.SKELETON_WOLF_BUFF);
            return skeletonType && !(livingEntity instanceof DrownedNecromancer) && MobUtil.areAllies(Warg.this, livingEntity);
        }
    }

    private class WargSwordAttackGoal extends Goal {
        private int pathRefresh;

        private WargSwordAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = Warg.this.getTarget();
            return Warg.this.hasSword() && target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = Warg.this.getTarget();
            return Warg.this.hasSword() && target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.pathRefresh = 0;
            Warg.this.setAggressive(true);
        }

        @Override
        public void stop() {
            Warg.this.getNavigation().stop();
            Warg.this.setAggressive(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = Warg.this.getTarget();
            if (target == null) {
                return;
            }
            if (Warg.this.getAttackTicks() > 0) {
                return;
            }

            Warg.this.getLookControl().setLookAt(target, 90.0F, 45.0F);
            double distance = Warg.this.horizontalDistanceToSqr(target);
            if (distance <= 16.0D && Math.abs(target.getY() - Warg.this.getY()) <= 3.0D) {
                Warg.this.getNavigation().stop();
                if (Warg.this.swordAttackCooldown <= 0) {
                    Warg.this.startSwordAttack(target);
                }
            } else if (--this.pathRefresh <= 0) {
                Warg.this.getNavigation().moveTo(target, 1.35D);
                this.pathRefresh = 3;
            }
        }
    }

    private boolean isOwnedByPlayer(Player player) {
        UUID owner = this.getOwnerId();
        return owner != null && owner.equals(player.getUUID());
    }

    public enum Variant {
        BLACK,
        COLD,
        MODERATE,
        WARM,
        SKELETAL,
        GRAY;

        public static Variant byId(int id) {
            return values()[Mth.clamp(id, 0, values().length - 1)];
        }
    }
}
