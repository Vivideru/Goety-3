package com.Vivideru.Goety.common.blocks.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * For block entities that call markUpdated() every tick: the block entity is still marked changed every tick, but the
 * block update and its data packet are only queued while players watch the chunk and the update tag differs from the
 * last one queued. The caller keeps the returned tag and clears it in its own markUpdated(), so any other sync makes
 * the next call send again.
 */
public final class BlockEntitySync {
    private BlockEntitySync() {
    }

    @Nullable
    public static CompoundTag markUpdatedIfChanged(BlockEntity blockEntity, @Nullable CompoundTag lastSent, Runnable markUpdated) {
        if (!(blockEntity.getLevel() instanceof ServerLevel serverLevel)) {
            markUpdated.run();
            return null;
        }
        // Same players ChunkHolder.broadcastChanges sends to; nobody else could receive the packet.
        if (serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(blockEntity.getBlockPos()), false).isEmpty()) {
            blockEntity.setChanged();
            return null;
        }
        CompoundTag tag = blockEntity.getUpdateTag(serverLevel.registryAccess());
        if (tag.equals(lastSent)) {
            blockEntity.setChanged();
            return lastSent;
        }
        markUpdated.run();
        return tag;
    }
}
