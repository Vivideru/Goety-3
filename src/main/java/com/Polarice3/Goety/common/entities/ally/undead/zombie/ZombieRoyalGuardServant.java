package com.Polarice3.Goety.common.entities.ally.undead.zombie;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.RoyalGuardServant;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ZombieRoyalGuardServant extends BlackguardServant {
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieRoyalGuardServant.class, EntityDataSerializers.BOOLEAN);
    public int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;

    public ZombieRoyalGuardServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.shieldHealth = AttributesConfig.get(AttributesConfig.RoyalGuardServantShield);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.get(AttributesConfig.RoyalGuardServantHealth))
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.get(AttributesConfig.RoyalGuardServantDamage))
                .add(Attributes.ATTACK_KNOCKBACK, 1.5F)
                .add(Attributes.ARMOR, AttributesConfig.get(AttributesConfig.RoyalGuardServantArmor) + 2.0F)
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.get(AttributesConfig.RoyalGuardServantToughness));
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.get(AttributesConfig.RoyalGuardServantHealth));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.get(AttributesConfig.RoyalGuardServantDamage));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.get(AttributesConfig.RoyalGuardServantArmor) + 2.0F);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.get(AttributesConfig.RoyalGuardServantToughness));
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CONVERTING_ID, false);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            compound.putUUID("ConversionPlayer", this.conversionStarter);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null, compound.getInt("ConversionTime"));
        }
    }

    public EntityType<?> getVariant(Level level, BlockPos blockPos){
        return ModEntityType.BLACKGUARD_VARIANT_SERVANT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    public void tick() {
        if (this.level() instanceof ServerLevel serverLevel && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();
            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0 && net.neoforged.neoforge.event.EventHooks.canLivingConvert(this, ModEntityType.ROYAL_GUARD_SERVANT.get(), (timer) -> this.villagerConversionTime = timer)) {
                this.finishConversion(serverLevel);
            }
        }
        super.tick();
    }

    public boolean canConvert(){
        return this.isNatural();
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    private void startConverting(@Nullable UUID playerId, int time) {
        this.conversionStarter = playerId;
        this.villagerConversionTime = time;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time, Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, (byte)16);
    }

    public void handleEntityEvent(byte event) {
        if (event == 16) {
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }
        } else {
            super.handleEntityEvent(event);
        }
    }

    private void finishConversion(ServerLevel serverLevel) {
        Player player = null;
        if (this.conversionStarter != null) {
            player = serverLevel.getPlayerByUUID(this.conversionStarter);
        }
        Entity servant = MobUtil.convertTo(this, ModEntityType.ROYAL_GUARD_SERVANT.get(), true, player);
        if (servant instanceof RoyalGuardServant royalGuard){
            royalGuard.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(servant.blockPosition()), MobSpawnType.CONVERSION, null);
            royalGuard.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            if (!this.isSilent()) {
                serverLevel.levelEvent((Player)null, 1027, this.blockPosition(), 0);
            }
            net.neoforged.neoforge.event.EventHooks.onLivingConvert(this, royalGuard);
        }
    }

    private int getConversionProgress() {
        int i = 1;
        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for(int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; ++k) {
                for(int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; ++l) {
                    for(int i1 = (int)this.getZ() - 4; i1 < (int)this.getZ() + 4 && j < 14; ++i1) {
                        BlockState blockstate = this.level().getBlockState(mutableBlockPos.set(k, l, i1));
                        if (blockstate.is(Blocks.IRON_BARS) || blockstate.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }
                            ++j;
                        }
                    }
                }
            }
        }
        return i;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.level().isClientSide && this.canConvert() && itemstack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, hand);
    }
}
