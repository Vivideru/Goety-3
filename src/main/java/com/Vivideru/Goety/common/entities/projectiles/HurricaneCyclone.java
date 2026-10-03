package com.Vivideru.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.projectiles.AbstractCyclone;
import com.Polarice3.Goety.common.entities.projectiles.Cyclone;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HurricaneCyclone extends Cyclone {
    private static final double SPEED = 0.14D;
    private Vec3 heading = Vec3.ZERO;
    private boolean held = true;

    public HurricaneCyclone(EntityType<? extends AbstractCyclone> type, Level level) {
        super(type, level);
    }

    public HurricaneCyclone(Level level, LivingEntity shooter) {
        super(level, shooter, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public EntityType<?> getType() {
        return VivideruEntityTypes.HURRICANE_CYCLONE.get();
    }

    public void hold(Vec3 anchor) {
        this.held = true;
        this.setDeltaMovement(Vec3.ZERO);
        this.setPos(anchor.x, anchor.y, anchor.z);
    }

    public void launch(Vec3 direction) {
        this.held = false;
        this.aim(direction);
    }

    private void aim(Vec3 direction) {
        if (direction.lengthSqr() > 1.0E-6D) {
            this.heading = direction.normalize();
        }
    }

    @Override
    public void fakeRemove(double x, double y, double z) {
        this.aim(new Vec3(x, y, z));
    }

    @Override
    public void travel() {
        if (this.held) {
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
        Vec3 delta = this.heading.scale(SPEED);
        this.setDeltaMovement(delta);
        if (delta.lengthSqr() > 1.0E-6D) {
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        }
        this.setPos(this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Held", this.held);
        tag.putDouble("HeadingX", this.heading.x);
        tag.putDouble("HeadingY", this.heading.y);
        tag.putDouble("HeadingZ", this.heading.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.held = tag.getBoolean("Held");
        this.heading = new Vec3(tag.getDouble("HeadingX"), tag.getDouble("HeadingY"), tag.getDouble("HeadingZ"));
    }
}
