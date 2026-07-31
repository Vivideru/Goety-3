package com.Polarice3.Goety.utils;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LootingExplosion extends Explosion {
    public Mode lootMode;
    private final Level localLevel;
    @Nullable
    private final Entity localSource;
    private final double localX;
    private final double localY;
    private final double localZ;
    private final float localRadius;
    private final boolean localFire;
    private final BlockInteraction localBlockInteraction;
    private final DamageSource localDamageSource;
    private final ExplosionDamageCalculator localDamageCalculator;

    public LootingExplosion(Level p_46041_, @Nullable Entity p_46042_, double p_46043_, double p_46044_, double p_46045_, float p_46046_, boolean p_46047_, BlockInteraction p_46048_, Mode pLootMode, List<BlockPos> p_46049_) {
        this(p_46041_, p_46042_, p_46043_, p_46044_, p_46045_, p_46046_, p_46047_, p_46048_, pLootMode);
        this.getToBlow().addAll(p_46049_);
    }

    public LootingExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction, Mode pLootMode) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
        this.lootMode = pLootMode;
        // Explosion internals are private in 1.21; keep local copies for this custom loot-preserving explosion.
        this.localLevel = pLevel;
        this.localSource = pSource;
        this.localX = pToBlowX;
        this.localY = pToBlowY;
        this.localZ = pToBlowZ;
        this.localRadius = pRadius;
        this.localFire = pFire;
        this.localBlockInteraction = pBlockInteraction;
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

        boolean flag = this.lootMode == Mode.LOOT;

        for (Entity entity : list) {
            if (!entity.ignoreExplosion(this)) {
                if (!(flag && entity instanceof ItemEntity)) {
                    double d12 = Mth.sqrt((float) entity.distanceToSqr(vector3d)) / f2;
                    if (d12 <= 1.0D) {
                        double d5 = entity.getX() - this.localX;
                        double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.localY;
                        double d9 = entity.getZ() - this.localZ;
                        double d13 = Mth.sqrt((float) (d5 * d5 + d7 * d7 + d9 * d9));
                        if (d13 != 0.0D) {
                            d5 = d5 / d13;
                            d7 = d7 / d13;
                            d9 = d9 / d13;
                            double d14 = (double) getSeenPercent(vector3d, entity);
                            double d10 = (1.0D - d12) * d14;
                            boolean hurt = true;
                            if (this.getIndirectSourceEntity() != null){
                                if (MobUtil.areAllies(this.getIndirectSourceEntity(), entity)){
                                    hurt = false;
                                }
                            }
                            if (hurt) {
                                if (flag){
                                    entity.hurt(ModDamageSource.lootExplosion(this.getDirectSourceEntity(), this.getIndirectSourceEntity(), this.localLevel), (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D)));
                                } else {
                                    entity.hurt(this.localDamageSource, (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D)));
                                }
                                double d11 = d10;
                                if (entity instanceof LivingEntity livingEntity) {
                                    // Minecraft 1.21 moved blast-protection knockback dampening to the explosion knockback resistance attribute.
                                    d11 = d10 * (1.0D - livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
                                }

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
    }

    public void finalizeExplosion(boolean pSpawnParticles) {

        this.playEffects();

        boolean flag = this.localBlockInteraction != BlockInteraction.KEEP;

        if (flag) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
            Util.shuffle(this.getToBlow(), this.localLevel.random);

            for (BlockPos blockpos : this.getToBlow()) {
                BlockState blockstate = this.localLevel.getBlockState(blockpos);
                Block block = blockstate.getBlock();
                if (!blockstate.isAir()) {
                    BlockPos blockpos1 = blockpos.immutable();
                    this.localLevel.getProfiler().push("explosion_blocks");
                    if (blockstate.canDropFromExplosion(this.localLevel, blockpos, this) && this.localLevel instanceof ServerLevel) {
                        BlockEntity tileentity = blockstate.hasBlockEntity() ? this.localLevel.getBlockEntity(blockpos) : null;
                        LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel) this.localLevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, tileentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.localSource);
                        if (this.localBlockInteraction == BlockInteraction.DESTROY) {
                            lootcontext$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.localRadius);
                        }

                        blockstate.getDrops(lootcontext$builder).forEach((p_229977_2_) -> {
                            addBlockDrops(objectarraylist, p_229977_2_, blockpos1);
                        });
                    }

                    blockstate.onBlockExploded(this.localLevel, blockpos, this);
                    this.localLevel.getProfiler().pop();
                }
            }

            for (Pair<ItemStack, BlockPos> pair : objectarraylist) {
                Block.popResource(this.localLevel, pair.getSecond(), pair.getFirst());
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

    public void playEffects(){
        boolean flag = this.localBlockInteraction != BlockInteraction.KEEP;

        if (this.localLevel.isClientSide()) {
            this.localLevel.playLocalSound(this.localX, this.localY, this.localZ, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F, (1.0F + (this.localLevel.random.nextFloat() - this.localLevel.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        if (!(this.localRadius < 2.0F) && flag) {
            this.localLevel.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.localX, this.localY, this.localZ, 1.0D, 0.0D, 0.0D);
        } else {
            this.localLevel.addParticle(ParticleTypes.EXPLOSION, this.localX, this.localY, this.localZ, 1.0D, 0.0D, 0.0D);
        }

    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> pDropPositionArray, ItemStack pStack, BlockPos pPos) {
        int i = pDropPositionArray.size();

        for(int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = pDropPositionArray.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, pStack)) {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, pStack, 16);
                pDropPositionArray.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (pStack.isEmpty()) {
                    return;
                }
            }
        }

        pDropPositionArray.add(Pair.of(pStack, pPos));
    }

    public enum Mode {
        REGULAR,
        LOOT;
    }
}
