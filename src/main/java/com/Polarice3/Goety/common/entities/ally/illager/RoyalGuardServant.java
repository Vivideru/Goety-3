package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.IShielded;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ShieldedMeleeGoal;
import com.Polarice3.Goety.common.entities.ai.ShieldedPreMeleeGoal;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieRoyalGuardServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RoyalGuardServant extends AbstractIllagerServant implements IShielded {
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
        this.goalSelector.addGoal(1, new ShieldedMeleeGoal<>(this) {
            public void playPreAttack() {
                this.mob.playSound(ModSounds.ROYAL_GUARD_PRE_ATTACK.get(), this.mob.getSoundVolume() * 0.5F, this.mob.getVoicePitch() * 0.75F);
            }

            public void playSmash() {
                this.mob.playSound(ModSounds.BLACKGUARD_SMASH.get(), this.mob.getSoundVolume() + 1.0F, this.mob.getVoicePitch());
            }
        });
        this.goalSelector.addGoal(4, new ShieldedPreMeleeGoal<>(this, this.getFollowSpeed()));
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
        this.readShieldedData(pCompound);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addShieldedData(pCompound);
    }

    @Override
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

    @Override
    public int getMaxShieldHealth() {
        return AttributesConfig.get(AttributesConfig.RoyalGuardServantShield);
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    @Override
    public int getAttackTick() {
        return this.attackTick;
    }

    public void setAttackTick(int attackTick) {
        this.attackTick = attackTick;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
    }

    public boolean wantsToPickUp(ItemStack itemStack) {
        return MobsConfig.IllagerServantPickUpDrops.get()
                && this.isShieldRepair(itemStack)
                && this.getInventory().canAddItem(itemStack)
                || super.wantsToPickUp(itemStack);
    }

    public boolean validLootToStore(ItemStack itemStack) {
        return super.validLootToStore(itemStack) && !this.isShieldRepair(itemStack);
    }

    @Override
    public boolean validItemToTake(ItemStack itemStack) {
        return this.isShieldRepair(itemStack) && this.itemsInInv(this::isShieldRepair).size() <= 24 || super.validItemToTake(itemStack);
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
        this.playSound(ModSounds.DOUBLE_AXE_IMPACT_SHING.get(), 1.5F * 0.5F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F));
        this.playSound(ModSounds.DIRT_SHATTER_THREE.get(), 1.5F * 0.25F * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F));
        this.playSound(ModSounds.HAMMER_SHIMMER_IMPACT_FOUR.get(), 1.5F * 0.4F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F));
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

    @Override
    public int xpReward() {
        return 20;
    }

    protected void playHurtSound(DamageSource damageSource) {
        SoundEvent soundevent = this.getHurtSound(damageSource);
        if (soundevent != null) {
            this.playSound(soundevent, 0.8F, this.getRandom().nextBoolean() ? 0.9F : 0.8F);
        }
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.playSound(ModSounds.DOUBLE_AXE_IMPACT_SHING.get(), 0.8F * 0.5F, this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.DIRT_SHATTER_THREE.get(), 0.8F * 0.25F * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F), this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F);
        this.playSound(ModSounds.HAMMER_SHIMMER_IMPACT_FOUR.get(), 0.8F * 0.4F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
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
    public void healServant() {
        super.healServant();
        if (!this.level().isClientSide) {
            if (this.getShieldHealth() < this.getMaxShieldHealth() && this.getTarget() == null && this.hurtTime <= 0) {
                this.itemsInInv(this::isShieldRepair)
                        .stream()
                        .findFirst()
                        .ifPresent(itemStack -> this.repairShield(this, itemStack));
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return this.hurtShielded(source, amount, () -> super.hurt(source, amount));
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.hasShield()) {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 4 || event == 5 || event == 6){
            this.handleShieldedEvent(event);
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public void animateAttack() {
        this.stopAllAnimations();
        this.attackAnimationState.start(this.tickCount);
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        return this.getShieldedAttackReachSqr(enemy);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (!this.level().isClientSide) {
                if (!this.hasShield() && this.isShieldRepair(itemstack) && this.getTarget() == null && this.hurtTime <= 0) {
                    return this.repairShield(player, itemstack);
                }
            }
        }
        return super.mobInteract(player, hand);
    }
}
