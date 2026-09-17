package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieRoyalGuardServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class RoyalGuardServant extends AbstractIllagerServant {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(RoyalGuardServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(RoyalGuardServant.class, EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public int shieldHealth = AttributesConfig.get(AttributesConfig.RoyalGuardServantShield);
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState standAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public RoyalGuardServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(4, new RoyalGuardAttackGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.get(AttributesConfig.RoyalGuardServantHealth))
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.get(AttributesConfig.RoyalGuardServantDamage))
                .add(Attributes.ATTACK_KNOCKBACK, 1.5F)
                .add(Attributes.ARMOR, AttributesConfig.get(AttributesConfig.RoyalGuardServantArmor))
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.get(AttributesConfig.RoyalGuardServantToughness));
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.get(AttributesConfig.RoyalGuardServantHealth));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.get(AttributesConfig.RoyalGuardServantDamage));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.get(AttributesConfig.RoyalGuardServantArmor));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.get(AttributesConfig.RoyalGuardServantToughness));
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SHIELD, true);
        builder.define(DATA_FLAGS_ID, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("hasShield")){
            this.setShield(pCompound.getBoolean("hasShield"));
        }
        if (pCompound.contains("ShieldHeath")){
            this.setShieldHealth(pCompound.getInt("ShieldHeath"));
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasShield", this.hasShield());
        pCompound.putInt("ShieldHeath", this.getShieldHealth());
    }

    public double getFollowSpeed() {
        return 1.25D;
    }

    @Override
    public boolean canOpenDoors() {
        return true;
    }

    @Override
    public boolean canHaveWeapon() {
        return false;
    }

    @Override
    public boolean canWearArmor() {
        return false;
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean hasShield(){
        return this.entityData.get(HAS_SHIELD);
    }

    public void setShield(boolean shield){
        this.entityData.set(HAS_SHIELD, shield);
    }

    public int getShieldHealth(){
        return this.shieldHealth;
    }

    public void setShieldHealth(int shieldHealth){
        this.shieldHealth = shieldHealth;
    }

    public int getMaxShieldHealth() {
        return AttributesConfig.get(AttributesConfig.RoyalGuardServantShield);
    }

    public void destroyShield(){
        if (this.hasShield()) {
            if (this.getShieldHealth() > 1){
                this.setShieldHealth(this.getShieldHealth() - 1);
                this.playSound(SoundEvents.SHIELD_BLOCK);
            } else {
                this.setShieldHealth(0);
                this.setShield(false);
                this.playSound(SoundEvents.SHIELD_BREAK);
                if (this.level() instanceof ServerLevel serverLevel){
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ANVIL)), this);
                }
            }
        }
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public void setAttackTick(int attackTick) {
        this.attackTick = attackTick;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.standAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.VINDICATOR_HURT;
    }

    @Override
    public void die(DamageSource pCause) {
        this.playSound(ModSounds.BLACKGUARD_SMASH.get(), 0.75F, this.getVoicePitch());
        this.playSound(ModSounds.PLATE_DROP.get(), this.getSoundVolume(), this.getVoicePitch());
        if (!this.level().isClientSide) {
            if (this.getIdol() == null && this.getTrueOwner() != null && CuriosFinder.hasNamelessSet(this.getTrueOwner())) {
                ZombieRoyalGuardServant servant = this.convertTo(ModEntityType.BLACKGUARD_VARIANT_SERVANT.get(), true);
                if (servant != null) {
                    servant.setTrueOwner(this.getTrueOwner());
                    net.neoforged.neoforge.event.EventHooks.onLivingConvert(this, servant);
                    if (!this.isSilent()) {
                        this.level().levelEvent((Player) null, 1026, this.blockPosition(), 0);
                    }
                }
            }
        }
        super.die(pCause);
    }

    protected void playHurtSound(DamageSource damageSource) {
        SoundEvent soundevent = this.getHurtSound(damageSource);
        if (soundevent != null) {
            this.playSound(soundevent, 0.8F, this.getRandom().nextBoolean() ? 0.9F : 0.8F);
        }
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.playSound(ModSounds.BLACKGUARD_SMASH.get(), 0.75F, this.getVoicePitch());
        this.playSound(ModSounds.PLATE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ModSounds.ROYAL_GUARD_STEP.get(), 0.15F, 1.0F);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        if (this.isNatural()){
            return EntityType.VINDICATOR.getDefaultLootTable();
        } else {
            return super.getDefaultLootTable();
        }
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide){
            if (this.isAlive()){
                this.idleAnimationState.animateWhen(!this.isMeleeAttacking() && !this.isStaying() && !this.isMoving(), this.tickCount);
                this.standAnimationState.animateWhen(!this.isMeleeAttacking() && this.isStaying() && !this.isMoving(), this.tickCount);
                if (!this.isMeleeAttacking()) {
                    this.attackAnimationState.stop();
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.destroyShield();
                return false;
            } else if (this.getTarget() != null && source.getEntity() instanceof LivingEntity livingEntity) {
                double d0 = this.distanceTo(this.getTarget());
                double d1 = this.distanceTo(livingEntity);
                if (MobUtil.ownedCanAttack(this, livingEntity) && livingEntity != this.getTrueOwner() && d0 > d1) {
                    this.setTarget(livingEntity);
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.hasShield()) {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 4){
            this.stopAllAnimations();
            this.attackAnimationState.start(this.tickCount);
        } else if (event == 5){
            this.attackTick = 0;
        } else if (event == 6){
            this.setShield(true);
            this.setShieldHealth(this.getMaxShieldHealth());
        } else {
            super.handleEntityEvent(event);
        }
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 6.0F * this.getBbWidth() * 6.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        return distToEnemySqr <= this.getAttackReachSqr(enemy) || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (!this.level().isClientSide && !this.hasShield() && itemstack.is(Tags.Items.INGOTS_IRON) && this.getTarget() == null && this.hurtTime <= 0){
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.setShield(true);
                this.setShieldHealth(this.getMaxShieldHealth());
                this.level().broadcastEntityEvent(this, (byte) 6);
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    class RoyalGuardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.0F;

        public RoyalGuardAttackGoal() {
            super(RoyalGuardServant.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            return RoyalGuardServant.this.getTarget() != null
                    && RoyalGuardServant.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            RoyalGuardServant.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            RoyalGuardServant.this.getNavigation().stop();
            if (RoyalGuardServant.this.getTarget() == null) {
                RoyalGuardServant.this.setAggressive(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingentity = RoyalGuardServant.this.getTarget();
            if (livingentity == null) {
                return;
            }

            RoyalGuardServant.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = RoyalGuardServant.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());

            if (--this.delayCounter <= 0 && !RoyalGuardServant.this.targetClose(livingentity, d0)) {
                this.delayCounter = 10;
                RoyalGuardServant.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, RoyalGuardServant.this.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (RoyalGuardServant.this.targetClose(enemy, distToEnemySqr)) {
                if (!RoyalGuardServant.this.isMeleeAttacking()) {
                    RoyalGuardServant.this.setMeleeAttacking(true);
                }
            }
        }
    }

    class MeleeGoal extends Goal {
        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return RoyalGuardServant.this.getTarget() != null
                    && RoyalGuardServant.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return RoyalGuardServant.this.attackTick < MathHelper.secondsToTicks(1.42F);
        }

        @Override
        public void start() {
            RoyalGuardServant.this.setMeleeAttacking(true);
            RoyalGuardServant.this.level().broadcastEntityEvent(RoyalGuardServant.this, (byte) 4);
        }

        @Override
        public void stop() {
            RoyalGuardServant.this.setMeleeAttacking(false);
        }

        @Override
        public void tick() {
            if (RoyalGuardServant.this.getTarget() != null) {
                LivingEntity livingentity = RoyalGuardServant.this.getTarget();
                MobUtil.instaLook(RoyalGuardServant.this, livingentity);
            }
            if (RoyalGuardServant.this.attackTick == 1){
                RoyalGuardServant.this.playSound(ModSounds.ROYAL_GUARD_PRE_ATTACK.get(), RoyalGuardServant.this.getSoundVolume() * 0.5F, RoyalGuardServant.this.getVoicePitch() * 0.75F);
            }
            if (RoyalGuardServant.this.attackTick == 9){
                RoyalGuardServant.this.playSound(ModSounds.BLACKGUARD_SMASH.get(), RoyalGuardServant.this.getSoundVolume() + 1.0F, RoyalGuardServant.this.getVoicePitch());
            }
            if (RoyalGuardServant.this.attackTick == 14) {
                double x = RoyalGuardServant.this.getX() + RoyalGuardServant.this.getHorizontalLookAngle().x * 2;
                double y = RoyalGuardServant.this.getY();
                double z = RoyalGuardServant.this.getZ() + RoyalGuardServant.this.getHorizontalLookAngle().z * 2;
                AABB aabb = MobUtil.makeAttackRange(x, y, z, 3, 3, 3);
                for (LivingEntity target : RoyalGuardServant.this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != RoyalGuardServant.this && !MobUtil.areAllies(target, RoyalGuardServant.this)) {
                        RoyalGuardServant.this.doHurtTarget(target);
                    }
                }
                if (RoyalGuardServant.this.level() instanceof ServerLevel serverLevel){
                    double groundY = BlockFinder.findGroundY(serverLevel, x, y, z);
                    BlockPos blockPos = BlockPos.containing(x, groundY - 1.0D, z);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 2; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, x, groundY + 0.25D, z, 1.5F);
                    }
                    ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                    serverLevel.sendParticles(new SmashParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.5F, 10), x, groundY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}


