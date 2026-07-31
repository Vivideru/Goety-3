package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class WaterHurtingProjectile extends AbstractHurtingProjectile implements ISpellEntity {
    private double xPower;
    private double yPower;
    private double zPower;
    private boolean leftOwner;
    private boolean hasBeenShot;

    protected WaterHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public WaterHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36817_, double p_36818_, double p_36819_, double p_36820_, double p_36821_, double p_36822_, double p_36823_, Level p_36824_) {
        super(p_36817_, p_36818_, p_36819_, p_36820_, new Vec3(p_36821_, p_36822_, p_36823_), p_36824_);
        this.setDirectionalPower(p_36821_, p_36822_, p_36823_);
    }

    public WaterHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36826_, LivingEntity p_36827_, double p_36828_, double p_36829_, double p_36830_, Level p_36831_) {
        super(p_36826_, p_36827_, new Vec3(p_36828_, p_36829_, p_36830_), p_36831_);
        this.setDirectionalPower(p_36828_, p_36829_, p_36830_);
    }

    private void setDirectionalPower(double xPower, double yPower, double zPower) {
        Vec3 power = new Vec3(xPower, yPower, zPower);
        if (power.lengthSqr() > 1.0E-7D) {
            // 1.20 AbstractHurtingProjectile normalized constructor power to 0.1; storing raw look vectors here makes custom bolts accelerate each tick.
            power = power.normalize().scale(0.1D);
        }
        this.xPower = power.x;
        this.yPower = power.y;
        this.zPower = power.z;
    }

    // Subclasses still need to know whether the custom 1.20-style directional push is active, but the fields remain encapsulated here.
    protected boolean hasDirectionalPower() {
        return this.xPower != 0.0D || this.yPower != 0.0D || this.zPower != 0.0D;
    }

    protected Vec3 getDirectionalPower() {
        return new Vec3(this.xPower, this.yPower, this.zPower);
    }

    public boolean isAffectedByWater(){
        return false;
    }

    public void tick() {
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            // Minecraft 1.21 made Projectile's shot-state fields private. This projectile keeps the old
            // custom tick pipeline, so it mirrors those flags locally instead of delegating to vanilla movement.
            if (!this.hasBeenShot) {
                this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
                this.hasBeenShot = true;
            }

            if (!this.leftOwner) {
                this.leftOwner = this.checkLeftOwner();
            }
            this.baseTick();
            if (this.shouldBurn()) {
                this.igniteForSeconds(1.0F);
            }

            this.hitDetection();

            this.checkInsideBlocks();
            this.travel();
            this.trailParticle();
        } else {
            this.discard();
        }
    }

    /**
     * Stole these methods from @Iron:<a href="https://github.com/iron431/irons-spells-n-spellbooks/blob/1.20.1/src/main/java/io/redspace/ironsspellbooks/entity/spells/AbstractMagicProjectile.java">...</a>
     * From here
     */
    public void shoot(Vec3 rotation) {
        this.setDeltaMovement(rotation.scale(this.getInertia()));
    }

    public void hitDetection(){
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }
    }

    public void travel(){
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        float f = this.getInertia();
        if (this.isInWater()) {
            if (this.isAffectedByWater()) {
                f = 0.8F;
            }
        }

        // In 1.21 the projectile base classes seed directional motion differently; keeping f above 1.0 makes these bolts accelerate every tick.
        f = Math.min(f, 1.0F);
        this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
        this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, this.getGravity(), 0.0D));
        this.setPos(d0, d1, d2);
    }
    /**
     * To here
     */

    public void trailParticle(){
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                float f1 = 0.25F;
                this.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * f1, d1 - vec3.y * f1, d2 - vec3.z * f1, vec3.x, vec3.y, vec3.z);
            }
        }
        this.level().addParticle(this.getTrailParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
    }

    protected double getDefaultGravity(){
        return 0.0F;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        Entity owner = this.getOwner();
        return entity.canBeHitByProjectile()
                && (owner == null || this.leftOwner || !owner.isPassengerOfSameVehicle(entity))
                && !entity.noPhysics;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("GoetyWaterLeftOwner", this.leftOwner);
        compound.putBoolean("GoetyWaterHasBeenShot", this.hasBeenShot);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.leftOwner = compound.getBoolean("GoetyWaterLeftOwner");
        this.hasBeenShot = compound.getBoolean("GoetyWaterHasBeenShot");
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for(Entity entity1 : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_37272_) -> !p_37272_.isSpectator() && p_37272_.isPickable())) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }
}
