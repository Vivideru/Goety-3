package com.Polarice3.Goety.common.entities.vehicle;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.HauntedBroomItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CBroomCollisionPacket;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class HauntedBroom extends Entity implements OwnableEntity {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> HURT_TIME = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HURT_DIR = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.FLOAT);
    private static final double BASE_SPEED_MULTIPLIER = 0.07D;
    private static final double MAX_ACCELERATION = 0.35D;
    private static final double ACCEL_FACTOR = MAX_ACCELERATION * 100.0D;
    private static final double SPEED_LIMIT = 0.9D;
    private static final double DRAG = 0.99D;
    private static final double GRAVITY = 0.03D;
    public Vec3 prevLoc = Vec3.ZERO;
    public double speedMultiplier = BASE_SPEED_MULTIPLIER;
    public float damageThreshold = 40.0F;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    public HauntedBroom(EntityType<? extends HauntedBroom> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public HauntedBroom(ItemStack itemStack, Level level, double x, double y, double z) {
        this(ModEntityType.HAUNTED_BROOM.get(), level);
        this.setItem(itemStack.copy());
        this.setPos(x, y, z);
        this.prevLoc = new Vec3(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_UNIQUE_ID, Optional.empty());
        builder.define(OWNER_CLIENT_ID, -1);
        builder.define(ITEM, ItemStack.EMPTY);
        builder.define(HURT_TIME, 0);
        builder.define(HURT_DIR, 1);
        builder.define(DAMAGE, 0.0F);
    }

    @Nullable
    public LivingEntity getOwner() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        }
        int id = this.getOwnerClientId();
        return id <= -1 ? null : this.level().getEntity(id) instanceof LivingEntity living ? living : null;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerId(@Nullable UUID ownerId) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(ownerId));
    }

    public int getOwnerClientId() {
        return this.entityData.get(OWNER_CLIENT_ID);
    }

    public void setOwnerClientId(int id) {
        this.entityData.set(OWNER_CLIENT_ID, id);
    }

    public void setOwner(LivingEntity livingEntity) {
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
            this.setOwnerClientId(livingEntity.getId());
        }
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    protected double getDefaultGravity() {
        return 0.0D;
    }

    public double getPassengersRidingOffset() {
        return 0.1D;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level().isClientSide && !this.isRemoved()) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + amount * 10.0F);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            boolean creative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
            if (creative || this.getDamage() > this.getDamageThreshold()) {
                this.playSound(ModSounds.BROOM_BREAK.get(), 0.8F, 1.0F);
                if (!creative && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnBroomItem();
                }
                this.discard();
            } else {
                this.playSound(ModSounds.BROOM_SWING.get(), 0.8F, 1.0F);
            }
            return true;
        }
        return true;
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof HauntedBroom) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = 10;
    }

    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
    }

    @Override
    protected @NotNull MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickLerp();

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }
        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }
        if (this.getOwner() != null) {
            this.ownerCheck();
        }
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            // Client-controlled vehicles do not always receive their own rotation back from the server before rendering.
            // Keep yRotO untouched so the renderer can interpolate turns smoothly between ticks.
            this.setYRot(player.getYRot());
            if (!this.level().isClientSide) {
                if (ItemConfig.HauntedBroomSoulDistance.get() > 0 && this.prevLoc.distanceTo(this.position()) >= Mth.square(ItemConfig.HauntedBroomSoulDistance.get())) {
                    this.prevLoc = this.position();
                    if (SEHelper.getSoulsAmount(player, ItemConfig.HauntedBroomSouls.get())) {
                        SEHelper.decreaseSouls(player, ItemConfig.HauntedBroomSouls.get());
                    } else {
                        SEHelper.addCooldown(player, this.getItem().getItem(), MathHelper.minutesToTicks(1));
                        this.hurt(this.damageSources().dryOut(), 100.0F);
                    }
                }
            }
        }

        if (this.isControlledByLocalInstance()) {
            this.handleInputs();
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double stepX = this.getX() + (this.lerpX - this.getX()) / this.lerpSteps;
            double stepY = this.getY() + (this.lerpY - this.getY()) / this.lerpSteps;
            double stepZ = this.getZ() + (this.lerpZ - this.getZ()) / this.lerpSteps;
            float stepYRot = (float) (this.getYRot() + Mth.wrapDegrees(this.lerpYRot - this.getYRot()) / this.lerpSteps);
            float stepXRot = this.getXRot() + (float) (this.lerpXRot - this.getXRot()) / this.lerpSteps;

            this.setYRot(stepYRot);
            this.setXRot(stepXRot);
            --this.lerpSteps;
            this.setPos(stepX, stepY, stepZ);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    private void handleInputs() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        double distance = this.getDeltaMovement().horizontalDistance();
        Vec3 movement = this.getDeltaMovement();
        double dX = movement.x;
        double dY = movement.y;
        double dZ = movement.z;

        LivingEntity rider = this.getControllingPassenger();
        if (rider != null) {
            boolean goingDown = rider.hasEffect(GoetyEffects.PLUNGE) || MobUtil.starAmuletActive(rider);
            float forward = rider.zza;
            if (forward > 0.0F) {
                double riderDirection = rider.getYRot() * Math.PI / 180.0D;
                double multiplier = this.speedMultiplier * (0.1D + this.getSpeedBoost());
                dX += -Math.sin(riderDirection) * multiplier;
                dZ += Math.cos(riderDirection) * multiplier;

                double riderView = -Math.sin(rider.getXRot() * Math.PI / 180.0D);
                if (riderView > -0.5D && riderView < 0.2D) {
                    riderView = 0.0D;
                } else if (riderView < 0.0D) {
                    riderView *= 0.5D;
                }
                if (!goingDown) {
                    dY = riderView * this.speedMultiplier * 2.0D;
                }
            }
            if (goingDown) {
                dY = -0.2D;
            }
        } else if (!this.onGround()) {
            dY = -GRAVITY;
        }

        double horizontal = Math.sqrt(dX * dX + dZ * dZ);
        double speedLimit = SPEED_LIMIT + this.getSpeedBoost() * 3.0D;
        if (horizontal > speedLimit) {
            double speed = speedLimit / horizontal;
            dX *= speed;
            dY *= speed;
            dZ *= speed;
            horizontal = speedLimit;
        }

        if (horizontal > distance && this.speedMultiplier < MAX_ACCELERATION) {
            this.speedMultiplier += (MAX_ACCELERATION - this.speedMultiplier) / ACCEL_FACTOR;
            if (this.speedMultiplier > MAX_ACCELERATION) {
                this.speedMultiplier = MAX_ACCELERATION;
            }
        } else {
            this.speedMultiplier -= (this.speedMultiplier - BASE_SPEED_MULTIPLIER) / ACCEL_FACTOR;
            if (this.speedMultiplier < BASE_SPEED_MULTIPLIER) {
                this.speedMultiplier = BASE_SPEED_MULTIPLIER;
            }
        }

        this.setDeltaMovement(dX, dY, dZ);
        double speedBeforeImpact = this.getDeltaMovement().horizontalDistance();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.checkCollisionDamage(speedBeforeImpact);
        this.setDeltaMovement(this.getDeltaMovement().multiply(DRAG, DRAG, DRAG));
    }

    public void applyCollisionDamage(double speedBeforeImpact) {
        if (!this.level().isClientSide) {
            double currentSpeed = this.getDeltaMovement().horizontalDistance();
            double lostSpeed = speedBeforeImpact - currentSpeed;
            float damage = (float) (lostSpeed * 10.0D - 3.0D);
            if (damage > 0.0F) {
                DamageSource damageSource = this.damageSources().flyIntoWall();
                LivingEntity rider = this.getControllingPassenger();
                if (rider != null) {
                    rider.hurt(damageSource, damage);
                }
                float threshold = this.getDamageThreshold() / 10.0F;
                if (damage > threshold) {
                    this.hurt(damageSource, damage);
                    if (rider instanceof Player player) {
                        player.getCooldowns().addCooldown(this.getItem().getItem(), 50);
                    }
                    if (this.isBurning()) {
                        float explodeSize = damage / threshold;
                        ExplosionUtil.lootExplode(this.level(), this, this.getX(), this.getY(), this.getZ(), explodeSize, true, Explosion.BlockInteraction.DESTROY, LootingExplosion.Mode.LOOT);
                    }
                }
            }
        }
    }

    private void checkCollisionDamage(double speedBeforeImpact) {
        if (this.horizontalCollision) {
            if (this.level().isClientSide) {
                ModNetwork.sendToServer(new CBroomCollisionPacket(this.getId(), speedBeforeImpact));
            } else {
                this.applyCollisionDamage(speedBeforeImpact);
            }
        }
    }

    @Override
    public void positionRider(Entity rider, MoveFunction moveFunction) {
        super.positionRider(rider, moveFunction);
        if (rider instanceof Player player) {
            player.setYBodyRot(player.getYHeadRot());
        }
    }

    public void ownerCheck() {
        if (!this.level().isClientSide && this.getOwner() != null && this.getOwner().tickCount < 20) {
            Entity entity = this.level().getEntity(this.getOwnerClientId());
            if (!(entity instanceof LivingEntity livingEntity) || livingEntity != this.getOwner()) {
                this.setOwnerClientId(this.getOwner().getId());
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.getOwnerId() != null) {
            tag.putUUID("Owner", this.getOwnerId());
        }
        if (this.getOwnerClientId() > -1) {
            tag.putInt("OwnerClient", this.getOwnerClientId());
        }
        tag.putFloat("DamageThreshold", this.getDamageThreshold());
        tag.put("Item", this.getItem().save(this.registryAccess(), new CompoundTag()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Owner")) {
            this.setOwnerId(tag.getUUID("Owner"));
        }
        if (tag.contains("OwnerClient")) {
            this.setOwnerClientId(tag.getInt("OwnerClient"));
        }
        if (tag.contains("DamageThreshold")) {
            this.setDamageThreshold(tag.getFloat("DamageThreshold"));
        }
        this.setItem(ItemStack.parseOptional(this.registryAccess(), tag.getCompound("Item")));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (this.getOwner() == null || this.getOwner() == player) {
            if (!this.level().isClientSide) {
                if (ItemConfig.HauntedBroomSoulDistance.get() > 0 && !SEHelper.getSoulsAmount(player, ItemConfig.HauntedBroomSouls.get())) {
                    player.displayClientMessage(Component.translatable("info.goety.broom.noSouls.ride"), true);
                    player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    return InteractionResult.PASS;
                }
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void checkFallDamage(double fallDistance, boolean onGround, BlockState state, BlockPos pos) {
        this.fallDistance = 0.0F;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYRot, (float) this.lerpXRot);
        }
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.HAUNTED_BROOM.get());
    }

    public void spawnBroomItem() {
        if (!this.getItem().isEmpty()) {
            if (this.getOwner() instanceof Player player) {
                ItemStack itemStack = this.getItem();
                HauntedBroomItem.setOwner(player, itemStack);
                if (!player.getInventory().add(itemStack)) {
                    player.drop(itemStack, false, true);
                } else if (!this.level().isClientSide) {
                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ITEM_PICKUP, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F));
                }
            } else {
                this.spawnAtLocation(this.getItem());
            }
        } else {
            this.spawnAtLocation(new ItemStack(ModItems.HAUNTED_BROOM.get()));
        }
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public int getHurtTime() {
        return this.entityData.get(HURT_TIME);
    }

    public void setHurtTime(int hurtTime) {
        this.entityData.set(HURT_TIME, hurtTime);
    }

    public int getHurtDir() {
        return this.entityData.get(HURT_DIR);
    }

    public void setHurtDir(int hurtDir) {
        this.entityData.set(HURT_DIR, hurtDir);
    }

    public ItemStack getItem() {
        return this.entityData.get(ITEM);
    }

    public void setItem(ItemStack itemStack) {
        this.entityData.set(ITEM, itemStack);
    }

    public float getDamageThreshold() {
        return this.damageThreshold;
    }

    public void setDamageThreshold(float damageThreshold) {
        this.damageThreshold = damageThreshold;
    }

    public boolean isBurning() {
        return MobUtil.getItemEnchantmentLevel(this, this.getItem(), ModEnchantments.BURNING) > 0;
    }

    public double getSpeedBoost() {
        return MobUtil.getItemEnchantmentLevel(this, this.getItem(), ModEnchantments.VELOCITY) / 10.0D;
    }
}
