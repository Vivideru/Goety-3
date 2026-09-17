package com.Vivideru.Goety.common.projectile.homing;

import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class HomingProjectileController {
    private HomingProjectileController() {
    }

    public static void steer(Projectile projectile) {
        if (!(projectile.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Returning scythe slashes use their own movement path and would repeatedly cross the same target if steered.
        if (projectile instanceof ScytheSlash || projectile.isRemoved()) {
            HomingProjectileEvents.clearHoming(projectile);
            return;
        }

        CompoundTag data = projectile.getPersistentData();
        int homingLevel = data.getInt(HomingProjectileEvents.HOMING_LEVEL_TAG);
        Vec3 motion = projectile.getDeltaMovement();
        if (homingLevel <= 0 || motion.lengthSqr() < 0.0001D) {
            return;
        }

        LivingEntity target = getOrFindTarget(serverLevel, projectile, homingLevel);
        if (target == null) {
            data.remove(HomingProjectileEvents.HOMING_TARGET_TAG);
            return;
        }

        double turnStrength = switch (homingLevel) {
            case 1 -> 0.175D;
            case 2 -> 0.335D;
            default -> 0.525D;
        };
        Vec3 targetDirection = target.getBoundingBox().getCenter().subtract(projectile.position()).normalize();
        Vec3 newDirection = motion.normalize().scale(1.0D - turnStrength).add(targetDirection.scale(turnStrength)).normalize();
        projectile.setDeltaMovement(newDirection.scale(motion.length()));
        projectile.hasImpulse = true;
    }

    private static LivingEntity getOrFindTarget(ServerLevel level, Projectile projectile, int homingLevel) {
        CompoundTag data = projectile.getPersistentData();
        if (data.hasUUID(HomingProjectileEvents.HOMING_TARGET_TAG)) {
            UUID targetId = data.getUUID(HomingProjectileEvents.HOMING_TARGET_TAG);
            Entity entity = level.getEntity(targetId);
            if (entity instanceof LivingEntity living && isValidTarget(projectile, living)) {
                return living;
            }
            data.remove(HomingProjectileEvents.HOMING_TARGET_TAG);
        }

        LivingEntity target = findTarget(projectile, homingLevel);
        if (target != null) {
            data.putUUID(HomingProjectileEvents.HOMING_TARGET_TAG, target.getUUID());
        }
        return target;
    }

    private static LivingEntity findTarget(Projectile projectile, int homingLevel) {
        Vec3 motion = projectile.getDeltaMovement();
        if (motion.lengthSqr() < 0.0001D) {
            return null;
        }

        double range;
        double startConeRadius;
        double maxConeRadius;
        if (homingLevel == 1) {
            range = 40.0D;
            startConeRadius = 1.5D;
            maxConeRadius = 6.0D;
        } else if (homingLevel == 2) {
            range = 55.0D;
            startConeRadius = 2.0D;
            maxConeRadius = 8.0D;
        } else {
            range = 70.0D;
            startConeRadius = 2.5D;
            maxConeRadius = 10.0D;
        }

        Vec3 origin = projectile.position();
        Vec3 direction = motion.normalize();
        AABB searchBox = projectile.getBoundingBox().inflate(range);
        List<LivingEntity> targets = projectile.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                target -> isValidTarget(projectile, target)
                        && isInsideCone(origin, direction, target, range, startConeRadius, maxConeRadius)
        );

        return targets.stream()
                .min(Comparator
                        .comparingDouble((LivingEntity target) -> distanceFromLine(origin, direction, target.getBoundingBox().getCenter()))
                        .thenComparingDouble(target -> target.distanceToSqr(projectile)))
                .orElse(null);
    }

    private static boolean isValidTarget(Projectile projectile, LivingEntity target) {
        if (!target.isAlive() || target.isSpectator() || target == projectile.getOwner()) {
            return false;
        }

        Entity owner = projectile.getOwner();
        if (owner instanceof LivingEntity livingOwner) {
            return !livingOwner.isAlliedTo(target)
                    && !target.isAlliedTo(livingOwner)
                    && livingOwner.hasLineOfSight(target);
        }
        return true;
    }

    private static boolean isInsideCone(Vec3 origin, Vec3 direction, LivingEntity target, double range,
                                        double startConeRadius, double maxConeRadius) {
        Vec3 targetCenter = target.getBoundingBox().getCenter();
        Vec3 toTarget = targetCenter.subtract(origin);
        if (toTarget.lengthSqr() < 0.0001D) {
            return false;
        }

        double projectedDistance = toTarget.dot(direction);
        if (projectedDistance <= 0.0D || projectedDistance > range) {
            return false;
        }

        Vec3 closestPoint = origin.add(direction.scale(projectedDistance));
        double allowedRadius = startConeRadius
                + (maxConeRadius - startConeRadius) * (projectedDistance / range);
        return targetCenter.distanceTo(closestPoint) <= allowedRadius;
    }

    private static double distanceFromLine(Vec3 origin, Vec3 direction, Vec3 point) {
        Vec3 toPoint = point.subtract(origin);
        Vec3 closestPoint = origin.add(direction.scale(toPoint.dot(direction)));
        return point.distanceToSqr(closestPoint);
    }
}
