package com.Vivideru.Goety.common.entities.util;

import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The entity a servant's owner UUID last resolved to on the server, so the owner is not searched for in every level on each
 * call. It is returned again only while EntityFinder would still find it: tracked by its level (NeoForge clears
 * isAddedToLevel as soon as an entity stops being tracked: chunk unload, removal, a player changing dimension), not removed
 * and under the same UUID. Otherwise the owner is looked up again.
 */
public final class OwnerCache {
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID missedId;
    private int missedTick;

    /** Result of EntityFinder.getLivingEntityByUuiD(level, ownerId). */
    @Nullable
    public LivingEntity get(Level level, UUID ownerId) {
        LivingEntity cached = this.owner;
        return isCurrent(cached, ownerId) ? cached : this.resolve(level.getServer(), ownerId);
    }

    /** Result of EntityFinder.getLivingEntityByUuiD(ownerId). */
    @Nullable
    public LivingEntity get(UUID ownerId) {
        LivingEntity cached = this.owner;
        return isCurrent(cached, ownerId) ? cached : this.resolve(ServerLifecycleHooks.getCurrentServer(), ownerId);
    }

    @Nullable
    private LivingEntity resolve(@Nullable MinecraftServer server, UUID ownerId) {
        // an owner that was not found is not searched for again before the next server tick
        if (server != null && this.owner == null && ownerId.equals(this.missedId) && server.getTickCount() == this.missedTick) {
            return null;
        }
        LivingEntity found = EntityFinder.getLivingEntityByUuiD(server, ownerId);
        this.owner = found;
        if (found == null && server != null) {
            this.missedId = ownerId;
            this.missedTick = server.getTickCount();
        }
        return found;
    }

    private static boolean isCurrent(@Nullable LivingEntity cached, UUID ownerId) {
        return cached != null && cached.isAddedToLevel() && !cached.isRemoved() && ownerId.equals(cached.getUUID());
    }
}
