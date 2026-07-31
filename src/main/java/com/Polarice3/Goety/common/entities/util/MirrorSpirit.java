package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MirrorSpirit extends Owned {

    public MirrorSpirit(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 16.0F, 0.4F));
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    protected int decreaseAirSupply(int p_28882_) {
        return p_28882_;
    }

    protected float getSoundVolume() {
        return 0.8F;
    }

    public float getVoicePitch() {
        return 1.0F;
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void push(Entity entityIn) {
    }

    @Override
    public void move(MoverType p_213315_1_, Vec3 p_213315_2_) {
    }

    protected void doPush(Entity p_20971_) {
    }

    public boolean canCollideWith(Entity p_20303_) {
        return false;
    }

    @Override
    public void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_) {
        super.makeStuckInBlock(p_20006_, Vec3.ZERO);
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        this.setNoGravity(true);
        super.tick();
    }

    public void lifeSpanDamage(){
        this.discard();
    }

    public void mobSense(){
    }

    @Override
    public boolean isSteppingCarefully() {
        return true;
    }

    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return p_21016_.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public void die(DamageSource p_21014_) {
        this.discard();
    }
}
