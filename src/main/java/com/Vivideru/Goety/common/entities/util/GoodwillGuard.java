package com.Vivideru.Goety.common.entities.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.world.data.GrimoireData;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class GoodwillGuard {
    private GoodwillGuard() {
    }

    /**
     * False only when IOwned.isAllyWith is false for every entity, following its two branches: a player owner with empty
     * Grimoire of Goodwill lists, or no Goodwill list saved in the world for the owner UUID.
     */
    public static boolean mayHaveAllies(IOwned owned, @Nullable LivingEntity owner, Level level) {
        if (owner instanceof Player player) {
            ISoulEnergy soulEnergy = SEHelper.getCapability(player);
            return !soulEnergy.allyList().isEmpty() || !soulEnergy.allyTypeList().isEmpty();
        }
        UUID ownerId = owned.getOwnerId();
        if (ownerId != null && level instanceof ServerLevel serverLevel) {
            GrimoireData data = GrimoireData.get(serverLevel);
            CompoundTag lists = data != null ? data.grimoireLists.get(ownerId) : null;
            return lists != null && (lists.contains("allyList") || lists.contains("allyTypeList"));
        }
        return false;
    }
}
