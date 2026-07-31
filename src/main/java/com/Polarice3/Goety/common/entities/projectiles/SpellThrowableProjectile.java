package com.Polarice3.Goety.common.entities.projectiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class SpellThrowableProjectile extends SpellTargetProjectile {
   protected SpellThrowableProjectile(EntityType<? extends SpellThrowableProjectile> p_37466_, Level p_37467_) {
      super(p_37466_, p_37467_);
   }

   protected SpellThrowableProjectile(EntityType<? extends SpellThrowableProjectile> p_37456_, double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
      this(p_37456_, p_37460_);
      this.setPos(p_37457_, p_37458_, p_37459_);
   }

   protected SpellThrowableProjectile(EntityType<? extends SpellThrowableProjectile> p_37462_, LivingEntity p_37463_, Level p_37464_) {
      this(p_37462_, p_37463_.getX(), p_37463_.getEyeY() - (double)0.1F, p_37463_.getZ(), p_37464_);
      this.setOwner(p_37463_);
   }

   public boolean shouldRenderAtSqrDistance(double p_37470_) {
      double d0 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 *= 64.0D;
      return p_37470_ < d0 * d0;
   }

   public void tick() {
      super.tick();
      HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      boolean flag = false;

      if (hitresult.getType() != HitResult.Type.MISS && !flag && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitresult)) {
         this.onHit(hitresult);
      }

      this.checkInsideBlocks();
      Vec3 vec3 = this.getDeltaMovement();
      double d2 = this.getX() + vec3.x;
      double d0 = this.getY() + vec3.y;
      double d1 = this.getZ() + vec3.z;
      this.updateRotation();
      float f;
      if (this.isInWater()) {
         for(int i = 0; i < 4; ++i) {
            float f1 = 0.25F;
            this.level().addParticle(ParticleTypes.BUBBLE, d2 - vec3.x * 0.25D, d0 - vec3.y * 0.25D, d1 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
         }

         f = 0.8F;
      } else {
         f = 0.99F;
      }

      this.setDeltaMovement(vec3.scale((double)f));
      this.applyGravity();

      this.setPos(d2, d0, d1);
   }

   @Override
   public boolean canUsePortal(boolean p_352918_) {
      return true;
   }

   /**
    * Entity#getGravity is final in 1.21.1. Override the default gravity hook instead,
    * which keeps the old projectile gravity value without replacing vanilla gravity logic.
    */
   @Override
   protected double getDefaultGravity() {
      return 0.03D;
   }
}
