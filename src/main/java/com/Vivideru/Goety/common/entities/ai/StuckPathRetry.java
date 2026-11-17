package com.Vivideru.Goety.common.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

/**
 * A mob that keeps asking for a path to a spot it cannot reach ends up standing on the only node of a finished path
 * that stops short of it. Searching again from that block finds the same one-node path, which PathNavigation.moveTo
 * then drops because it matches the current one, so the search only has to be repeated once a second, in case the
 * blocks around have changed. Wall climbers are left out: their createPath also sets the spot they head for once the
 * path is finished.
 */
public final class StuckPathRetry {
    private static final int RETRY_INTERVAL = 20;

    private StuckPathRetry() {
    }

    public static boolean shouldWait(Mob mob, BlockPos target) {
        PathNavigation navigation = mob.getNavigation();
        if (navigation instanceof WallClimberNavigation) {
            return false;
        }
        Path path = navigation.getPath();
        if (path == null || !path.isDone() || path.canReach() || path.getNodeCount() != 1 || !target.equals(path.getTarget())) {
            return false;
        }
        Node node = path.getEndNode();
        return node != null && node.asBlockPos().equals(mob.blockPosition()) && (mob.tickCount + mob.getId()) % RETRY_INTERVAL != 0;
    }
}
