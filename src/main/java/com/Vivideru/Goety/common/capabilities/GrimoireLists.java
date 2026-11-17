package com.Vivideru.Goety.common.capabilities;

import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.UUID;

public final class GrimoireLists {
    private GrimoireLists() {
    }

    /**
     * Same answer as resolving every UUID of the set with EntityFinder, skipping the owner, and testing List.contains(target):
     * only the target's own UUID can resolve to an entity equal to it, so only that one is looked up.
     */
    public static boolean resolvesTo(Player owner, Set<UUID> uuids, LivingEntity target) {
        if (uuids.isEmpty() || !uuids.contains(target.getUUID())) {
            return false;
        }
        LivingEntity living = EntityFinder.getLivingEntityByUuiD(target.getUUID());
        return living != null && living != owner && target.equals(living);
    }
}
