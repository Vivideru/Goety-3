package com.Vivideru.Goety.common.entities.util;

import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * The snap of MobUtil.moveDownToGround for entities that run it on both sides every tick. MobUtil sends a teleport
 * packet on every call; here it is only sent on the tick the snapped position changed and on the tick after. The
 * packet resets the client's move base but not the server's, so the first vanilla relative move after a change (for
 * example after an owned teleport synced with MobUtil.syncTrackedTeleport) lands twice on the client until the next
 * packet resets it.
 */
public final class GroundSnap {
    private GroundSnap() {
    }

    /** Server-side state of one entity: its last snapped position and the packets still owed after a change. */
    public static final class Tracker {
        private double x = Double.NaN;
        private double y = Double.NaN;
        private double z = Double.NaN;
        private int resendTicks;
    }

    public static void moveDownToGround(Entity entity, Tracker tracker) {
        Vec3 startPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Vec3 endPos = new Vec3(entity.getX(), entity.level().getMinBuildHeight(), entity.getZ());
        HitResult rayTrace = entity.level().clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) rayTrace;
            if (hitResult.getDirection() == Direction.UP) {
                BlockState hitBlock = entity.level().getBlockState(hitResult.getBlockPos());
                if (hitBlock.getBlock() instanceof SlabBlock && hitBlock.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                    entity.setPos(entity.getX(), hitResult.getBlockPos().getY() + 1.0625F - 0.5f, entity.getZ());
                } else {
                    entity.setPos(entity.getX(), hitResult.getBlockPos().getY() + 1.0625F, entity.getZ());
                }
                if (entity.level() instanceof ServerLevel serverLevel) {
                    if (entity.getX() != tracker.x || entity.getY() != tracker.y || entity.getZ() != tracker.z) {
                        tracker.x = entity.getX();
                        tracker.y = entity.getY();
                        tracker.z = entity.getZ();
                        tracker.resendTicks = 2;
                    }
                    if (tracker.resendTicks > 0) {
                        --tracker.resendTicks;
                        serverLevel.getChunkSource().broadcastAndSend(entity, new ClientboundTeleportEntityPacket(entity));
                    }
                }
            }
        }
    }
}
