package com.Vivideru.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SkeletalWarg extends Warg {
    public SkeletalWarg(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        super.setVariant(Variant.SKELETAL);
    }

    @Override
    public Variant getVariant() {
        return Variant.SKELETAL;
    }

    @Override
    public void setVariant(Variant variant) {
        super.setVariant(Variant.SKELETAL);
    }

    @Override
    protected boolean canTurnInvisible() {
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof AbstractSkeleton && (this.isHostile() || this.getTrueOwner() == null)) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.SKELETON_WOLF_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    public void playAmbientSound() {
        if (!this.isHowling()) {
            super.playAmbientSound();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SKELETON_WOLF_GROWL.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SKELETON_WOLF_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SKELETON_WOLF_DEATH.get();
    }

    @Override
    protected SoundEvent getShakeSound() {
        return ModSounds.SKELETON_WOLF_SHAKE.get();
    }
}
