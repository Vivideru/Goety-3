package com.Vivideru.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemBlockEntity;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemHooks;
import com.Vivideru.Goety.common.items.VivideruItems;
import com.Vivideru.Goety.common.world.WargTotemData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    public final AnimationState groundedAnimationState = new AnimationState();
    public final AnimationState jumpAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState spinAnimationState = new AnimationState();
    public final AnimationState slashAnimationState = new AnimationState();
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
        // The larger head makes the Black Wolf's idle look goal too restless, so Wargs choose a new idle direction less often.
        this.goalSelector.removeAllGoals(goal -> goal instanceof RandomLookAroundGoal);
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && Warg.this.getRandom().nextInt(4) == 0;
            }
        });
        // Sword attacks need their own range check so the slash can start before the inherited bite goal closes the gap.
        this.goalSelector.addGoal(3, new WargSwordAttackGoal());
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddled(tag.getBoolean("Saddled"));
        this.setVariant(Variant.byId(tag.getInt("WargVariant")));
    }

    @Override
    public void tick() {
        super.tick();
        // Smoothly lift the rider with the airborne Warg model instead of leaving the player inside the animated saddle.
        this.riderJumpOffset = Mth.approach(this.riderJumpOffset, this.onGround() ? 0.0F : 0.25F, 0.05F);
        if (!this.level().isClientSide) {
            this.registerPersistentAssignment();
            this.tickSwordAttack();
        } else {
            this.updateAnimationStates();
        }
    }

    private void registerPersistentAssignment() {
        if (!this.registryChecked && this.level() instanceof ServerLevel serverLevel && this.getOwnerId() != null && this.getRevivePos() != null) {
            // The saved assignment survives the block being broken, preventing a replacement Totem from bypassing either Warg limit.
            WargTotemData.get(serverLevel).register(this.getUUID(), this.getOwnerId(), this.getReviveLevel(), this.getRevivePos());
            this.registryChecked = true;
        }
    }

    private void updateAnimationStates() {
        boolean attacking = this.getAttackTicks() > 0;
        setAnimation(this.biteAnimationState, attacking && this.getAttackType() == ATTACK_BITE);
        setAnimation(this.spinAnimationState, attacking && this.getAttackType() == ATTACK_SPIN);
        setAnimation(this.slashAnimationState, attacking && this.getAttackType() == ATTACK_SLASH);
        setAnimation(this.jumpAnimationState, !attacking && !this.onGround());
        setAnimation(this.groundedAnimationState, !attacking && this.onGround() && this.isSitting());
        setAnimation(this.walkAnimationState, !attacking && this.onGround() && !this.isSitting() && this.walkAnimation.speed() > 0.05F);
        setAnimation(this.idleAnimationState, !attacking && this.onGround() && !this.isSitting() && this.walkAnimation.speed() <= 0.05F);
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
        // Keep the body, head and lunge on one heading until the complete attack animation has finished.
        this.applyLockedAttackFacing();
        this.getNavigation().stop();
        if (this.getAttackType() == ATTACK_SPIN && ticks == 10) {
            this.performSpinAttack();
        } else if (this.getAttackType() == ATTACK_SLASH && ticks == 7) {
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
        // The server windows match the accelerated client animations, allowing the next decision almost immediately.
        this.setAttack(attack, attack == ATTACK_SPIN ? 19 : 13);
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
        this.yHeadRot = this.lockedAttackYaw;
    }

    private void performSpinAttack() {
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.25D), this::canHitWithSword);
        for (LivingEntity target : targets) {
            this.hurtWithSword(target);
        }
    }

    private void performSlashAttack() {
        // Use the locked heading for both movement and damage so the model always faces the actual slash direction.
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
        if (target.hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
            this.curseTarget(target);
            if (this.level() instanceof ServerLevel serverLevel) {
                // Delayed model attacks still run the weapon's normal post-attack enchantment effects when the hit frame arrives.
                EnchantmentHelper.doPostAttackEffects(serverLevel, target, this.getServantAttack());
            }
        }
    }

    @Override
    protected float getJumpPower() {
        return 0.55F;
    }

    @Override
    public float getTailAngle() {
        // The Warg carries its heavier tail at a shallower angle than the Black Wolf.
        return super.getTailAngle() - 0.30F;
    }

    @Override
    protected AABB getAttackBoundingBox() {
        AABB attackBox = super.getAttackBoundingBox();
        // Sword attacks begin outside bite range because both custom swings cover a wider area.
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
    protected int calculateFallDamage(float distance, float multiplier) {
        // Extend the safe fall by four blocks while keeping damage progression continuous above the new threshold.
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
            // Mounted Wargs use a stronger leap than their autonomous pathfinding jump and preserve charge-based control.
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
            // The saddle is behind the entity origin and below the old generic riding offset in the supplied model.
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
            if (held.is(ItemTags.SWORDS) && this.getMainHandItem().isEmpty()) {
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
                    // Return the mouth-held sword directly to the empty hand instead of dropping it at the Warg's feet.
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
            // This runs only after a definitive removal; a Totem rescue cancels death before loot cleanup.
            totem.releaseWarg(this.getUUID());
        }
        WargTotemData.get(level).unregister(this.getUUID());
    }

    public boolean hasSword() {
        return this.getMainHandItem().is(ItemTags.SWORDS);
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

    public void setVariant(Variant variant) {
        this.entityData.set(VARIANT, variant.ordinal());
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public int getAttackTicks() {
        return this.entityData.get(ATTACK_TICKS);
    }

    private void setAttack(int type, int ticks) {
        this.entityData.set(ATTACK_TYPE, type);
        this.entityData.set(ATTACK_TICKS, ticks);
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
                // Frequent path refreshes keep the Warg responsive to targets moving around the four-block attack boundary.
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
        WARM;

        public static Variant byId(int id) {
            return values()[Mth.clamp(id, 0, values().length - 1)];
        }
    }
}
