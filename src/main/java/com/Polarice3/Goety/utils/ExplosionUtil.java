package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SFungusExplosionPacket;
import com.Polarice3.Goety.common.network.server.SLootingExplosionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public class ExplosionUtil {
    private static final int MAX_EXPLOSION_DEPTH = 8;
    // Explosions run on both logical sides, so depth must be isolated per game thread.
    private static final ThreadLocal<Integer> EXPLOSION_DEPTH = ThreadLocal.withInitial(() -> 0);

    public static LootingExplosion lootExplode(Level world, @Nullable Entity pExploder, double pX, double pY, double pZ, float pSize, boolean pCausesFire, Explosion.BlockInteraction pMode, LootingExplosion.Mode pLootMode) {
        LootingExplosion explosion = new LootingExplosion(world, pExploder, pX, pY, pZ, pSize, pCausesFire, pMode, pLootMode);
        if (net.neoforged.neoforge.event.EventHooks.onExplosionStart(world, explosion)) return explosion;
        explosion.explode();
        if (world instanceof ServerLevel serverLevel) {
            explosion.finalizeExplosion(false);
            for (ServerPlayer serverplayer : serverLevel.getPlayers((p_147157_) -> {
                return p_147157_.distanceToSqr(pX, pY, pZ) < 4096.0D;
            })) {
                ModNetwork.sendTo(serverplayer, new SLootingExplosionPacket(pX, pY, pZ, pSize, explosion.getHitPlayers().get(serverplayer)));
            }
        } else {
            explosion.finalizeExplosion(true);
        }
        return explosion;
    }

    public static FungusExplosion fungusExplode(Level world, @Nullable Entity pExploder, double pX, double pY, double pZ, float pSize, boolean pCausesFire) {
        FungusExplosion explosion = new FungusExplosion(world, pExploder, pX, pY, pZ, pSize, pCausesFire);
        if (net.neoforged.neoforge.event.EventHooks.onExplosionStart(world, explosion)) return explosion;
        explosion.explode();
        if (world instanceof ServerLevel serverLevel) {
            explosion.finalizeExplosion(false);
            for (ServerPlayer serverplayer : serverLevel.getPlayers((p_147157_) -> {
                return p_147157_.distanceToSqr(pX, pY, pZ) < 4096.0D;
            })) {
                ModNetwork.sendTo(serverplayer, new SFungusExplosionPacket(pX, pY, pZ, pSize, explosion.getHitPlayers().get(serverplayer)));
            }
        } else {
            explosion.finalizeExplosion(true);
        }
        return explosion;
    }

    public static boolean enterExplosion() {
        int depth = EXPLOSION_DEPTH.get();
        if (depth >= MAX_EXPLOSION_DEPTH) {
            return false;
        }
        EXPLOSION_DEPTH.set(depth + 1);
        return true;
    }

    public static void exitExplosion() {
        int depth = EXPLOSION_DEPTH.get();
        if (depth <= 1) {
            EXPLOSION_DEPTH.remove();
        } else {
            EXPLOSION_DEPTH.set(depth - 1);
        }
    }
}
