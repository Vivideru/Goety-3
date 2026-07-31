package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.init.ModSounds;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FungusExplosion extends Explosion {
    private final Level localLevel;
    @Nullable
    private final Entity localSource;
    private final double localX;
    private final double localY;
    private final double localZ;
    private final float localRadius;
    private final boolean localFire;
    private final DamageSource localDamageSource;
    private final ExplosionDamageCalculator localDamageCalculator;

    public FungusExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, BlockInteraction.KEEP);
        // Explosion internals are private in 1.21; keep local copies for the custom explosion algorithm below.
        this.localLevel = pLevel;
        this.localSource = pSource;
        this.localX = pToBlowX;
        this.localY = pToBlowY;
        this.localZ = pToBlowZ;
        this.localRadius = pRadius;
        this.localFire = pFire;
        this.localDamageSource = pLevel.damageSources().explosion(this);
        this.localDamageCalculator = pSource == null ? new ExplosionDamageCalculator() : new EntityBasedExplosionDamageCalculator(pSource);
    }

    @Override
    public void explode() {
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for(int j = 0; j < i; ++j) {
            for(int k = 0; k < i; ++k) {
                for(int l = 0; l < i; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.localRadius * (0.7F + this.localLevel.random.nextFloat() * 0.6F);
                        double d4 = this.localX;
                        double d6 = this.localY;
                        double d8 = this.localZ;

                        for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = this.localLevel.getBlockState(blockpos);
                            FluidState fluidstate = this.localLevel.getFluidState(blockpos);
                            Optional<Float> optional = this.localDamageCalculator.getBlockExplosionResistance(this, this.localLevel, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.localDamageCalculator.shouldBlockExplode(this, this.localLevel, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;
                        }
                    }
                }
            }
        }

        this.getToBlow().addAll(set);
        float f2 = this.localRadius * 2.0F;
        int k1 = Mth.floor(this.localX - (double)f2 - 1.0D);
        int l1 = Mth.floor(this.localX + (double)f2 + 1.0D);
        int i2 = Mth.floor(this.localY - (double)f2 - 1.0D);
        int i1 = Mth.floor(this.localY + (double)f2 + 1.0D);
        int j2 = Mth.floor(this.localZ - (double)f2 - 1.0D);
        int j1 = Mth.floor(this.localZ + (double)f2 + 1.0D);
        List<Entity> list = this.localLevel.getEntities(this.localSource, new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        net.neoforged.neoforge.event.EventHooks.onExplosionDetonate(this.localLevel, this, list, f2);
        Vec3 vector3d = new Vec3(this.localX, this.localY, this.localZ);

        for (Entity entity : list) {
            if (entity instanceof LivingEntity) {
                if (!entity.ignoreExplosion(this)) {
                    double d12 = Mth.sqrt((float) entity.distanceToSqr(vector3d)) / f2;
                    if (d12 <= 1.0D) {
                        double d5 = entity.getX() - this.localX;
                        double d7 = entity.getEyeY() - this.localY;
                        double d9 = entity.getZ() - this.localZ;
                        double d13 = Mth.sqrt((float) (d5 * d5 + d7 * d7 + d9 * d9));
                        if (d13 != 0.0D) {
                            d5 = d5 / d13;
                            d7 = d7 / d13;
                            d9 = d9 / d13;
                            double d14 = (double) getSeenPercent(vector3d, entity);
                            double d10 = (1.0D - d12) * d14;
                            if (this.getIndirectSourceEntity() != null) {
                                entity.hurt(entity.damageSources().explosion(this.getDirectSourceEntity(), this.getIndirectSourceEntity()), (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D)));
                            } else {
                                entity.hurt(this.localDamageSource, (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D)));
                            }
                            // Minecraft 1.21 moved blast-protection knockback dampening to the explosion knockback resistance attribute.
                            double d11 = d10 * (1.0D - ((LivingEntity) entity).getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));

                            entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
                            if (entity instanceof Player player) {
                                if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                    this.getHitPlayers().put(player, new Vec3(d5 * d10, d7 * d10, d9 * d10));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void finalizeExplosion(boolean pSpawnParticles) {
        if (this.localLevel.isClientSide) {
            this.localLevel.playLocalSound(this.localX, this.localY, this.localZ, ModSounds.BLAST_FUNGUS_EXPLODE.get(), SoundSource.BLOCKS, 4.0F, (1.0F + (this.localLevel.random.nextFloat() - this.localLevel.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        if (pSpawnParticles) {
            if (this.localRadius > 2.0F) {
                this.localLevel.addParticle(ModParticleTypes.FUNGUS_EXPLOSION_EMITTER.get(), this.localX, this.localY, this.localZ, 1.0D, 0.0D, 0.0D);
            } else {
                this.localLevel.addParticle(ModParticleTypes.FUNGUS_EXPLOSION.get(), this.localX, this.localY, this.localZ, 1.0D, 0.0D, 0.0D);
            }
        }

        if (this.localFire) {
            for (BlockPos blockpos2 : this.getToBlow()) {
                if (this.localLevel.random.nextInt(3) == 0 && this.localLevel.getBlockState(blockpos2).isAir() && this.localLevel.getBlockState(blockpos2.below()).isSolidRender(this.localLevel, blockpos2.below())) {
                    this.localLevel.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.localLevel, blockpos2));
                }
            }
        }
    }
}
