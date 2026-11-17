package com.Vivideru.Goety.common.entities.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public final class ShieldedHits {
    private ShieldedHits() {
    }

    /**
     * True when the hit comes from the owner (while owners cannot hurt their servants) or from an ally of the shielded mob:
     * its shield then blocks the hit without losing durability.
     */
    public static boolean isFriendly(Mob mob, DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null || attacker == mob) {
            return false;
        }
        if (mob instanceof IOwned owned && (attacker == owned.getTrueOwner() || attacker == owned.getMasterOwner())) {
            return MobsConfig.OwnerAttackCancel.get();
        }
        return MobUtil.areAllies(attacker, mob);
    }
}
