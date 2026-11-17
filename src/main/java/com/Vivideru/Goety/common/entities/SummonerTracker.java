package com.Vivideru.Goety.common.entities;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Remembers which mob summoned a minion. Servant summoners give their minions to their master
 * (MobUtil.getSummonOwner), so summon caps that count minions by owner no longer see them.
 * Server thread only and never saved: a link is lost when the minion unloads and ends when its owner changes.
 */
public final class SummonerTracker {
    private static final Map<Entity, Link> LINKS = new WeakHashMap<>();

    private SummonerTracker() {
    }

    /** Call once the minion's owner is set. */
    public static void record(Entity summon, Entity summoner) {
        if (!summon.level().isClientSide) {
            LINKS.put(summon, new Link(summoner.getUUID(), ownerId(summon)));
        }
    }

    /** Hands the summoner of one minion over to the mob it turns into, once that mob's owner is set. */
    public static void copy(Entity from, Entity to) {
        UUID summoner = summonerId(from);
        if (summoner != null) {
            LINKS.put(to, new Link(summoner, ownerId(to)));
        }
    }

    public static boolean isSummonedBy(Entity summon, Entity summoner) {
        return summoner.getUUID().equals(summonerId(summon));
    }

    @Nullable
    public static LivingEntity getSummoner(Entity summon) {
        UUID summoner = summonerId(summon);
        return summoner == null ? null : EntityFinder.getLivingEntityByUuiD(summon.level(), summoner);
    }

    @Nullable
    private static UUID summonerId(Entity summon) {
        if (summon.level().isClientSide) {
            return null;
        }
        Link link = LINKS.get(summon);
        if (link == null || !Objects.equals(link.owner(), ownerId(summon))) {
            return null;
        }
        // As with getTrueOwner, a minion under Wild Rage belongs to no one.
        if (summon instanceof LivingEntity living && living.hasEffect(GoetyEffects.WILD_RAGE)) {
            return null;
        }
        return link.summoner();
    }

    @Nullable
    private static UUID ownerId(Entity summon) {
        return summon instanceof IOwned owned ? owned.getOwnerId() : null;
    }

    private record Link(UUID summoner, UUID owner) {
    }
}
