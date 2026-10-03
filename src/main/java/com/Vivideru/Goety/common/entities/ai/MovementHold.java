package com.Vivideru.Goety.common.entities.ai;

import net.minecraft.world.entity.Mob;

public final class MovementHold {
    private MovementHold() {
    }

    public static void holdStill(Mob mob) {
        mob.getNavigation().stop();
        mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0D);
        mob.setZza(0.0F);
        mob.setXxa(0.0F);
    }
}
