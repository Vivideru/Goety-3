package com.Polarice3.Goety.common.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public class ArcaTeleporter {
    private ArcaTeleporter() {
    }

    public static DimensionTransition transition(ServerLevel destWorld, Entity entity, Vec3 targetPos) {
        // Minecraft 1.21 replaces ITeleporter with DimensionTransition, so this preserves the old target-position behavior at each call site.
        return new DimensionTransition(destWorld, targetPos, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING);
    }
}
