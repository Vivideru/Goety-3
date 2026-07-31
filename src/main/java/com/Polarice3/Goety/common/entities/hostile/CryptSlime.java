package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.api.entities.IMobTyped;
import com.Polarice3.Goety.init.ModMobType;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class CryptSlime extends Slime implements IMobTyped {
    public CryptSlime(EntityType<? extends Slime> p_33588_, Level p_33589_) {
        super(p_33588_, p_33589_);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected ParticleOptions getParticleType() {
        return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BONE));
    }

    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return EntityType.SLIME.getDefaultLootTable();
    }

    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource p_33574_, boolean p_33576_) {
        super.dropCustomDeathLoot(serverLevel, p_33574_, p_33576_);
        if (serverLevel.getServer() != null) {
            LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ModLootTables.CRYPT_SLIME));
            LootParams.Builder lootparams$builder = (new LootParams.Builder(serverLevel)).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, p_33574_).withOptionalParameter(LootContextParams.ATTACKING_ENTITY, p_33574_.getEntity()).withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, p_33574_.getDirectEntity());
            if (this.lastHurtByPlayerTime > 0 && this.lastHurtByPlayer != null) {
                lootparams$builder = lootparams$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
            }

            LootParams lootparams = lootparams$builder.create(LootContextParamSets.ENTITY);
            loottable.getRandomItems(lootparams, this.getLootTableSeed(), this::spawnAtLocation);
        }
    }

    @Override
    public ModMobType getGoetyMobType() {
        return ModMobType.UNDEAD;
    }

    public boolean causeFallDamage(float p_149717_, float p_149718_, DamageSource p_149719_) {
        return false;
    }

    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    public void push(Entity p_33636_) {
        if (this.getTarget() != null && this.getTarget() == p_33636_ && this.isDealsDamage()) {
            super.push(p_33636_);
        } else {
            if (!this.isPassengerOfSameVehicle(p_33636_)) {
                if (!p_33636_.noPhysics && !this.noPhysics) {
                    double d0 = p_33636_.getX() - this.getX();
                    double d1 = p_33636_.getZ() - this.getZ();
                    double d2 = Mth.absMax(d0, d1);
                    if (d2 >= (double)0.01F) {
                        d2 = Math.sqrt(d2);
                        d0 /= d2;
                        d1 /= d2;
                        double d3 = 1.0D / d2;
                        if (d3 > 1.0D) {
                            d3 = 1.0D;
                        }

                        d0 *= d3;
                        d1 *= d3;
                        d0 *= (double)0.05F;
                        d1 *= (double)0.05F;
                        if (!this.isVehicle() && this.isPushable()) {
                            this.push(-d0, 0.0D, -d1);
                        }

                        if (!p_33636_.isVehicle() && p_33636_.isPushable()) {
                            p_33636_.push(d0, 0.0D, d1);
                        }
                    }

                }
            }
        }

    }

    public void playerTouch(Player p_33611_) {
        if (this.getTarget() == p_33611_ && this.isDealsDamage()) {
            this.dealDamage(p_33611_);
        }
    }

    protected void dealDamage(LivingEntity livingEntity) {
        if (this.isAlive()) {
            int i = this.getSize();
            if (this.distanceToSqr(livingEntity) < 0.6D * (double)i * 0.6D * (double)i && this.hasLineOfSight(livingEntity) && livingEntity.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, livingEntity, this.damageSources().mobAttack(this));
                }
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED, 60, this.getSize()));
            }
        }
    }

    public static boolean checkMonsterSpawnRules(EntityType<? extends Slime> p_219014_, ServerLevelAccessor p_219015_, MobSpawnType p_219016_, BlockPos p_219017_, RandomSource p_219018_) {
        return p_219015_.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(p_219015_, p_219017_, p_219018_) && checkMobSpawnRules(p_219014_, p_219015_, p_219016_, p_219017_, p_219018_);
    }
}
