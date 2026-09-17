package com.Polarice3.Goety.common.entities.ally.illager.raider;

import com.Polarice3.Goety.utils.MobTypeHelper;

import com.Polarice3.Goety.init.ModMobType;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieRavager;
import com.Polarice3.Goety.common.entities.hostile.ArmoredRavager;
import com.Polarice3.Goety.common.entities.neutral.IRavager;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.RavagerArmorItem;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public class ModRavager extends RaiderServant implements PlayerRideable, IAutoRideable, IRavager {
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("d404309f-25d3-4837-8828-e2b7b0ea79fd");
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(ModRavager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(ModRavager.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = (p_33346_) -> p_33346_.isAlive() && !(p_33346_ instanceof ModRavager);
    private int attackTick;
    private int stunnedTick;
    private int roarTick;
    private int roarCool;

    public ModRavager(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(PathType.LEAVES, 0.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new RavagerMeleeAttackGoal());
    }

    public void targetSelectGoal(){
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, RaiderServant.class){
            @Override
            public boolean canUse() {
                return super.canUse() && (ModRavager.this.isHostile() || ModRavager.this.isNatural());
            }
        }).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, livingEntity -> this.isHostile() && !livingEntity.isBaby()).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false, livingEntity -> this.isHostile()));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this){
            @Override
            public boolean canUse() {
                return super.canUse() && MobTypeHelper.getMobType(ModRavager.this) != ModMobType.UNDEAD;
            }
        });
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.4D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger() instanceof RaiderServant;
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SADDLE_ID, false);
        builder.define(AUTO_MODE, false);
    }

    public float getStepHeight() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 75.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.get(AttributesConfig.RavagerDamage))
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ARMOR, AttributesConfig.get(AttributesConfig.RavagerArmor))
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.get(AttributesConfig.RavagerArmor));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.get(AttributesConfig.RavagerDamage));
    }

    public void addAdditionalSaveData(CompoundTag p_33353_) {
        super.addAdditionalSaveData(p_33353_);
        p_33353_.putInt("AttackTick", this.attackTick);
        p_33353_.putInt("StunTick", this.stunnedTick);
        p_33353_.putInt("RoarTick", this.roarTick);
        p_33353_.putInt("RoarCool", this.roarCool);
        p_33353_.putBoolean("Saddle", this.hasSaddle());
        p_33353_.putBoolean("AutoMode", this.isAutonomous());

        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.CHEST);
        if(!itemStack.isEmpty()) {
            p_33353_.put("ArmorItem", itemStack.save(this.registryAccess()));
        }
    }

    public void readAdditionalSaveData(CompoundTag p_33344_) {
        super.readAdditionalSaveData(p_33344_);
        this.attackTick = p_33344_.getInt("AttackTick");
        this.stunnedTick = p_33344_.getInt("StunTick");
        this.roarTick = p_33344_.getInt("RoarTick");
        this.roarCool = p_33344_.getInt("RoarCool");
        if (p_33344_.contains("Saddle")) {
            this.setSaddle(p_33344_.getBoolean("Saddle"));
        }
        if (p_33344_.contains("AutoMode")) {
            this.setAutonomous(p_33344_.getBoolean("AutoMode"));
        }
        if (p_33344_.contains("ArmorItem")) {
            CompoundTag armorItem = p_33344_.getCompound("ArmorItem");
            ItemStack.parse(this.registryAccess(), armorItem).ifPresent(stack -> this.setArmorEquipment(stack, false));
        }
    }

    public boolean canSpawnArmor(){
        return false;
    }

    public void setSaddle(boolean p_20850_) {
        this.entityData.set(DATA_SADDLE_ID, p_20850_);
    }

    public boolean hasSaddle() {
        return this.entityData.get(DATA_SADDLE_ID);
    }

    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTO_MODE, autonomous);
        if (autonomous) {
            this.playSound(SoundEvents.ARROW_HIT_PLAYER);
            if (!this.isWandering()) {
                this.setWandering(true);
                this.setStaying(false);
            }
        }
    }

    public boolean isAutonomous() {
        return this.entityData.get(AUTO_MODE);
    }

    public int getMaxHeadYRot() {
        return 45;
    }

    public double getPassengersRidingOffset() {
        return 2.1D;
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        // Minecraft 1.21 positions passengers through attachment points, so preserve the original riding height explicitly.
        return this.position().add(0.0D, this.getPassengersRidingOffset(), 0.0D);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Mob mob){
                if (MobsConfig.ServantRideAutonomous.get()){
                    return null;
                }
                return mob;
            } else if (entity instanceof LivingEntity
                    && !this.isAutonomous()) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }

    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    public ItemStack getArmor() {
        return this.getItemBySlot(EquipmentSlot.CHEST);
    }

    public void setArmorEquipment(ItemStack armor, boolean sound) {
        if (!this.level().isClientSide) {
            this.setItemSlot(EquipmentSlot.CHEST, armor);
            float chance = MobsConfig.PlayerRavagerArmorDrop.get() ? 2.0F : 0.0F;
            this.setDropChance(EquipmentSlot.CHEST, chance);
            this.updateArmor();
            if (sound) {
                this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
            }
        }
    }

    public void updateArmor(){
        AttributeInstance attribute = this.getAttribute(Attributes.ARMOR);
        if (attribute != null) {
            attribute.removeModifier(com.Polarice3.Goety.utils.ModAttributeUtil.stableId(ARMOR_MODIFIER_UUID, "Ravager armor bonus"));
            if (this.isArmor(this.getArmor())) {
                int i = ((RavagerArmorItem) this.getArmor().getItem()).getProtection();
                if (i != 0) {
                    attribute.addTransientModifier(com.Polarice3.Goety.utils.ModAttributeUtil.create(ARMOR_MODIFIER_UUID, "Ravager armor bonus", (double) i, AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }
    }

    public boolean isArmor(ItemStack p_30731_) {
        return p_30731_.getItem() instanceof RavagerArmorItem;
    }

    public double regularSpeed(){
        return 0.3D;
    }

    public double aggressiveSpeed(){
        return 0.35D;
    }

    public void aiStep() {
        super.aiStep();
        if (this.isHostile() && MobTypeHelper.getMobType(this) != ModMobType.UNDEAD) {
            if (MobsConfig.RavagerHostileConvert.get()) {
                if (this.tickCount % 20 == 0) {
                    if (this.hasSaddle()) {
                        if (!this.getArmor().isEmpty()) {
                            ArmoredRavager armoredRavager = this.convertTo(ModEntityType.ARMORED_RAVAGER.get(), false);
                            if (armoredRavager != null) {
                                armoredRavager.setArmorEquipment(this.getArmor());
                            }
                        } else {
                            this.convertTo(EntityType.RAVAGER, false);
                        }
                    }
                }
            }
        }
        if (this.isAlive()) {
            AttributeInstance attribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attribute != null) {
                if (this.isImmobile()) {
                    attribute.setBaseValue(0.0D);
                } else {
                    double d0 = this.getTarget() != null ? this.aggressiveSpeed() : this.regularSpeed();
                    double d1 = attribute.getBaseValue();
                    attribute.setBaseValue(Mth.lerp(0.1D, d1, d0));
                }
            }
            if (!this.level().isClientSide) {
                if (net.neoforged.neoforge.event.EventHooks.canEntityGrief(this.level(), this)) {
                    boolean flag = false;
                    AABB aabb = this.getBoundingBox().inflate(0.2D);

                    for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                        BlockState blockstate = this.level().getBlockState(blockpos);
                        Block block = blockstate.getBlock();
                        if (block instanceof LeavesBlock) {
                            flag = this.level().destroyBlock(blockpos, true, this) || flag;
                        }
                    }
                }
            }

            if (this.roarCool > 0){
                --this.roarCool;
            }

            if (this.roarTick > 0) {
                --this.roarTick;
                if (this.roarTick == 10) {
                    this.roar();
                }
            }

            if (this.attackTick > 0) {
                --this.attackTick;
            }

            if (this.stunnedTick > 0) {
                --this.stunnedTick;
                this.stunEffect();
                if (this.stunnedTick == 0) {
                    this.playSound(this.getRoarSound(), 1.0F, 1.0F);
                    this.roarTick = 20;
                }
            }

            if (this.getControllingPassenger() instanceof IServant summoned
                && summoned instanceof Mob mob) {
                this.setTarget(mob.getTarget());
            }
        }
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasSaddle()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.getY() + (double)this.getBbHeight() - 0.3D;
            double d2 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.49803922F, 0.5137255F, 0.57254905F), d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return this.stunnedTick <= 0 && this.roarTick <= 0 && super.hasLineOfSight(p_149755_);
    }

    protected void blockedByShield(LivingEntity p_33361_) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5D) {
                this.stunnedTick = 40;
                this.roarCool = this.getRoarCoolMax();
                this.playSound(this.getStunnedSound(), 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte)39);
                p_33361_.push(this);
            } else {
                this.strongKnockback(p_33361_);
            }

            p_33361_.hurtMarked = true;
        }
    }

    public void forceRoar(){
        if (this.roarCool <= 0) {
            this.roarTick = 20;
            this.playSound(this.getRoarSound(), 1.0F, 1.0F);
            this.roarCool = this.getRoarCoolMax();
            this.level().broadcastEntityEvent(this, (byte) 40);
        }
    }

    private void roar() {
        if (this.isAlive()) {
            for(LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), NO_RAVAGER_AND_ALIVE)) {
                if (!MobUtil.areAllies(this, livingentity)) {
                    livingentity.hurt(this.getServantAttack(), MobUtil.getSpecialAttackDamage(this, 6.0F));
                }

                this.strongKnockback(livingentity);
            }

            Vec3 vec3 = this.getBoundingBox().getCenter();

            for(int i = 0; i < 40; ++i) {
                double d0 = this.random.nextGaussian() * 0.2D;
                double d1 = this.random.nextGaussian() * 0.2D;
                double d2 = this.random.nextGaussian() * 0.2D;
                this.level().addParticle(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, d0, d1, d2);
            }

            this.gameEvent(GameEvent.ENTITY_ACTION);
        }

    }

    private void strongKnockback(Entity p_33340_) {
        double d0 = p_33340_.getX() - this.getX();
        double d1 = p_33340_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        p_33340_.push(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
    }

    public void handleEntityEvent(byte p_33335_) {
        if (p_33335_ == 4) {
            this.attackTick = 10;
            this.playSound(this.getAttackSound(), 1.0F, 1.0F);
        } else if (p_33335_ == 39) {
            this.stunnedTick = 40;
            this.roarCool = this.getRoarCoolMax();
        } else if (p_33335_ == 40) {
            this.roarTick = 20;
            this.playSound(this.getRoarSound(), 1.0F, 1.0F);
            this.roarCool = this.getRoarCoolMax();
        }

        super.handleEntityEvent(p_33335_);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public int getStunnedTick() {
        return this.stunnedTick;
    }

    public int getRoarTick() {
        return this.roarTick;
    }

    public int getRoarCool(){
        return this.roarCool;
    }

    public boolean doHurtTarget(Entity p_33328_) {
        this.attackTick = 10;
        this.level().broadcastEntityEvent(this, (byte)4);
        this.playSound(this.getAttackSound(), 1.0F, 1.0F);
        return super.doHurtTarget(p_33328_);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_33359_) {
        return SoundEvents.RAVAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    protected SoundEvent getStepSound(){
        if (this.hasSaddle()) {
            return SoundEvents.RAVAGER_STEP;
        } else {
            return SoundEvents.POLAR_BEAR_STEP;
        }
    }

    protected SoundEvent getAttackSound(){
        return SoundEvents.RAVAGER_ATTACK;
    }

    protected SoundEvent getStunnedSound(){
        return SoundEvents.RAVAGER_STUNNED;
    }

    protected SoundEvent getRoarSound(){
        return SoundEvents.RAVAGER_ROAR;
    }

    protected void playStepSound(BlockPos p_33350_, BlockState p_33351_) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
        if (!this.getArmor().isEmpty()){
            this.playSound(SoundEvents.CHAIN_STEP, 0.15F, 1.0F);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.RAVAGER_CELEBRATE;
    }

    public boolean checkSpawnObstruction(LevelReader p_33342_) {
        return !p_33342_.containsAnyLiquid(this.getBoundingBox());
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.isVehicle() && this.hasSaddle() && rider instanceof Player && !this.isAutonomous()) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float speed = 0.35F;
                if (MobTypeHelper.getMobType(this) == ModMobType.UNDEAD) {
                    speed = 0.325F;
                }
                float f = rider.xxa * speed;
                float f1 = rider.zza * speed;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                if (MobTypeHelper.getMobType(this) != ModMobType.UNDEAD) {
                    if (this.isInWater() && this.getFluidTypeHeight(NeoForgeMod.WATER_TYPE.value()) > this.getFluidJumpThreshold() || this.isInLava() || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType) && height > this.getFluidJumpThreshold())) {
                        Vec3 vector3d = this.getDeltaMovement();
                        this.setDeltaMovement(vector3d.x, 0.04F, vector3d.z);
                        this.hasImpulse = true;
                        if (f1 > 0.0F) {
                            float f2 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
                            float f3 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
                            this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f2 * 0.04F), 0.0D, (double) (0.4F * f3 * 0.04F)));
                        }
                    }
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;

                this.calculateEntityAnimation(false);
            } else {
                super.travel(pTravelVector);
            }
        }
    }

    @Override
    public boolean canUpdateMove() {
        return !(this.getControllingPassenger() instanceof Mob);
    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.level().isClientSide) {
            if (this.getIdol() == null) {
                if (this.getTrueOwner() != null && MobTypeHelper.getMobType(this) != ModMobType.UNDEAD) {
                    if (CuriosFinder.hasNamelessSet(this.getTrueOwner())) {
                        ZombieRavager servant = this.convertTo(ModEntityType.ZOMBIE_RAVAGER.get(), true);
                        if (servant != null) {
                            servant.setTrueOwner(this.getTrueOwner());
                            if (this.hasSaddle()) {
                                servant.equipSaddle(false);
                            }
                            servant.updateArmor();
                            net.neoforged.neoforge.event.EventHooks.onLivingConvert(this, servant);
                            if (!this.isSilent()) {
                                this.level().levelEvent((Player) null, 1026, this.blockPosition(), 0);
                            }
                        }
                    }
                }
            }
        }
        super.die(pCause);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!pPlayer.level().isClientSide) {
            if (pPlayer == this.getTrueOwner()) {
                if (this.hasSaddle() && !pPlayer.isCrouching()) {
                    if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer){
                        this.getFirstPassenger().stopRiding();
                        return InteractionResult.SUCCESS;
                    } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)){
                        this.doPlayerRide(pPlayer);
                        return InteractionResult.SUCCESS;
                    }
                } else if (pPlayer.getItemInHand(pHand).is(ModItems.OMINOUS_SADDLE.get()) && !this.hasSaddle()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getItemInHand(pHand).shrink(1);
                    }
                    this.equipSaddle(true);
                    return InteractionResult.SUCCESS;
                } else if (this.isArmor(pPlayer.getItemInHand(pHand)) && this.hasSaddle()) {
                    if (!this.getArmor().isEmpty()) {
                        if (this.spawnAtLocation(this.getArmor()) != null) {
                            this.setArmorEquipment(pPlayer.getMainHandItem().copy(), true);
                            if (!pPlayer.getAbilities().instabuild) {
                                pPlayer.getMainHandItem().shrink(1);
                            }
                        }
                    } else {
                        this.setArmorEquipment(pPlayer.getMainHandItem().copy(), true);
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getMainHandItem().shrink(1);
                        }
                    }
                    EntityFinder.sendEntityUpdatePacket(pPlayer, this);
                    return InteractionResult.SUCCESS;
                } else if (this.isFood(pPlayer.getItemInHand(pHand)) && this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodProperties = pPlayer.getMainHandItem().get(DataComponents.FOOD);
                    if (foodProperties != null) {
                        this.heal((float) foodProperties.nutrition());
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getMainHandItem().shrink(1);
                        }
                        this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), 0.5F);
                        this.gameEvent(GameEvent.EAT, this);
                        return InteractionResult.SUCCESS;
                    } else {
                        return InteractionResult.PASS;
                    }
                } else if (pPlayer.getItemInHand(pHand).is(Items.STICK) && !this.getArmor().isEmpty()){
                    if (this.spawnAtLocation(this.getArmor()) != null) {
                        this.setArmorEquipment(ItemStack.EMPTY, true);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public boolean isFood(ItemStack p_30440_) {
        FoodProperties foodProperties = p_30440_.get(DataComponents.FOOD);
        // FoodProperties no longer exposes the old meat helper here; accept edible component data and let interaction ownership/health checks gate feeding.
        return foodProperties != null;
    }

    public void equipSaddle(boolean playSound) {
        if (playSound) {
            this.level().playSound(null, this, SoundEvents.HORSE_SADDLE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(AttributesConfig.get(AttributesConfig.RavagerSaddleHealth));
        }
        this.setSaddle(true);
    }

    class RavagerMeleeAttackGoal extends MeleeAttackGoal {
        public RavagerMeleeAttackGoal() {
            super(ModRavager.this, 1.0D, true);
        }

        protected double getAttackReachSqr(LivingEntity p_33377_) {
            float f = ModRavager.this.getBbWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + p_33377_.getBbWidth());
        }
    }
}
