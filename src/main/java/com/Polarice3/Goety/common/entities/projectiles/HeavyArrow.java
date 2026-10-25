package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.TrailEffect;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class HeavyArrow extends Arrow {
    public TrailEffect trail = new TrailEffect(0.1F, 3.0F);
    public boolean hitEnemy = false;
    private byte heavyPierceLevel = 0;
    @Nullable
    private IntOpenHashSet heavyPiercingIgnoreEntityIds;
    @Nullable
    private List<Entity> heavyPiercedAndKilledEntities;

    public HeavyArrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public HeavyArrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
        super(p_36861_, p_36862_, p_36863_, p_36864_, new ItemStack(Items.ARROW), null);
    }

    public HeavyArrow(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_, new ItemStack(Items.ARROW), null);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.HEAVY_ARROW.get();
    }

    /**
     * Vanilla's setPierceLevel is private in 1.21, so Heavy Arrows keep their own pierce count.
     */
    public void setPierceLevel(byte pierceLevel) {
        this.heavyPierceLevel = pierceLevel;
    }

    @Override
    public byte getPierceLevel() {
        return this.heavyPierceLevel;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("PierceLevel", this.heavyPierceLevel);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.heavyPierceLevel = compound.getByte("PierceLevel");
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(this.tickCount < 10 && !this.hitEnemy && !this.inGround);
        if (this.level().isClientSide) {
            if (this.tickCount > 2 && this.hasTrail()) {
                Vec3 oldPos = new Vec3(this.xOld, this.yOld + getBbHeight() / 2, this.zOld);
                this.trail.update(oldPos);
            }
        }
    }

    public boolean hasTrail() {
        return !this.inGround && !this.hitEnemy;
    }

    @Override
    public void handleEntityEvent(byte p_36869_) {
        if (p_36869_ == 4) {
            this.hitEnemy = true;
        } else {
            super.handleEntityEvent(p_36869_);
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (this.heavyPiercingIgnoreEntityIds == null || !this.heavyPiercingIgnoreEntityIds.contains(entity.getId()));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.heavyPiercedAndKilledEntities != null) {
            this.heavyPiercedAndKilledEntities.clear();
        }
        if (this.heavyPiercingIgnoreEntityIds != null) {
            this.heavyPiercingIgnoreEntityIds.clear();
        }
        this.heavyPierceLevel = 0;
    }

    protected void onHitEntity(EntityHitResult p_36757_) {
        Entity entity = p_36757_.getEntity();
        double baseDamage = this.getBaseDamage();
        this.hitEnemy = true;
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 4);
        }
        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().arrow(this, entity1 != null ? entity1 : this);
        if (this.getWeaponItem() != null && this.level() instanceof ServerLevel serverLevel) {
            baseDamage = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), entity, damagesource, (float) baseDamage);
        }

        if (this.getPierceLevel() > 0) {
            if (this.heavyPiercingIgnoreEntityIds == null) {
                this.heavyPiercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.heavyPiercedAndKilledEntities == null) {
                this.heavyPiercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.heavyPiercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                this.discard();
                return;
            }

            this.heavyPiercingIgnoreEntityIds.add(entity.getId());
        }

        if (this.isCritArrow()) {
            long j = this.random.nextInt(Mth.floor(baseDamage) / 2 + 2);
            baseDamage = (int)Math.min(j + (long)baseDamage, 2147483647L);
        }

        if (entity1 instanceof LivingEntity livingOwner) {
            livingOwner.setLastHurtMob(entity);
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !flag) {
            entity.igniteForSeconds(5.0F);
        }

        if (entity.hurt(damagesource, (float)baseDamage)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity livingentity) {
                if (!this.level().isClientSide && this.getPierceLevel() <= 0) {
                    livingentity.setArrowCount(livingentity.getArrowCount() + 1);
                }

                this.doKnockback(livingentity, damagesource);
                if (this.level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, livingentity, damagesource, this.getWeaponItem());
                }

                this.doPostHurtEffects(livingentity);
                if (livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer)entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }

                if (!entity.isAlive() && this.heavyPiercedAndKilledEntities != null) {
                    this.heavyPiercedAndKilledEntities.add(livingentity);
                }

                if (!this.level().isClientSide && entity1 instanceof ServerPlayer serverplayer) {
                    if (this.heavyPiercedAndKilledEntities != null && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayer, this.heavyPiercedAndKilledEntities);
                    } else if (!entity.isAlive() && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayer, Arrays.asList(entity));
                    }
                }
            }

            this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.discard();
            }
        } else {
            entity.setRemainingFireTicks(k);
            this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.2D));
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
        }

    }
}
