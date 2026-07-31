package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class NetherMeteor extends ExplosiveProjectile {

    public NetherMeteor(EntityType<? extends ExplosiveProjectile> type, Level world) {
        super(type, world);
    }

    public NetherMeteor(Level worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(ModEntityType.NETHER_METEOR.get(), x, y, z, accelX, accelY, accelZ, worldIn);
    }

    public NetherMeteor(Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntityType.NETHER_METEOR.get(), shooter, accelX, accelY, accelZ, worldIn);
    }

    protected void defineDefaultExplosionAndDamage(SynchedEntityData.Builder builder) {
        builder.define(DATA_EXPLOSION, 4.0F);
        builder.define(DATA_DAMAGE, 6.0F);
    }

    public void tick() {
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.level().addParticle(ParticleTypes.LARGE_SMOKE, d0 + this.getRandom().nextDouble()/2, d1 + 0.5D, d2 + this.getRandom().nextDouble()/2, 0.0D, 0.0D, 0.0D);
        this.level().addParticle(ParticleTypes.FLAME, d0 + this.getRandom().nextDouble()/2, d1 + 0.5D, d2 + this.getRandom().nextDouble()/2, 0.0D, 0.0D, 0.0D);
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            boolean flag = this.isDangerous();
            Explosion.BlockInteraction interaction = flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
            ExplosionUtil.lootExplode(this.level(), this, this.getX(), this.getY(), this.getZ(), this.getExplosionPower(), flag, interaction, LootingExplosion.Mode.LOOT);
            if (MobsConfig.ApocalypseMode.get() && flag){
                if (this.getOwner() instanceof Apostle apostle){
                    apostle.netherSpreaderUtil.clear();
                    for (int i = 0; i < 5; i++) {
                        int range = Mth.floor(this.getExplosionPower());
                        int x = this.random.nextInt(-range, range);
                        int y = this.random.nextInt(-range, -(range / 2));
                        int z = this.random.nextInt(-range, range);
                        BlockPos blockPos = this.blockPosition().below().offset(x, y, z);
                        apostle.netherSpreaderUtil.addCursors(blockPos, 10);
                    }
                }
            }
            this.discard();
        }
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            DamageSource damageSource = ModDamageSource.hellfire(this, this.getOwner());
            entity.hurt(damageSource, this.getDamage() + this.getExtraDamage());
            if (entity1 instanceof LivingEntity && this.level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffects(serverLevel, entity, damageSource);
            }

        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() instanceof IOwned owner){
            if (pEntity instanceof IOwned entity){
                if (owner.getTrueOwner() == entity.getTrueOwner()){
                    return false;
                }
            }
            if (owner.getTrueOwner() == pEntity){
                return false;
            }
        }
        if (MobUtil.areAllies(this.getOwner(), pEntity)){
            return false;
        }
        return super.canHitEntity(pEntity);
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean ignoreExplosion(){
        return true;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.LARGE_SMOKE;
    }

    protected boolean shouldBurn() {
        return false;
    }
}
