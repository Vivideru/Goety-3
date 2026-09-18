package com.Vivideru.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import com.Vivideru.Goety.common.entities.ally.SkeletalWarg;
import com.Vivideru.Goety.common.entities.ally.Warg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeletal Wargs used to be the plain Warg type with the SKELETAL variant. Saves from those versions are rebuilt as
 * the dedicated skeletal_warg type when they load, keeping their UUID so Totem links and the Warg limit still match.
 * The old entity is refused before it enters the level and the replacement is added on the next server tick, so the
 * two never exist at the same time with the same UUID.
 */
@EventBusSubscriber(modid = Goety.MOD_ID)
public final class SkeletalWargMigration {
    private static final List<PendingWarg> PENDING = new ArrayList<>();

    private SkeletalWargMigration() {
    }

    @SubscribeEvent
    public static void onLegacySkeletalWargJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof Warg warg)) {
            return;
        }
        if (warg instanceof SkeletalWarg || warg.getType() != VivideruEntityTypes.WARG.get()
                || warg.getVariant() != Warg.Variant.SKELETAL) {
            return;
        }
        PENDING.add(new PendingWarg(level, warg.saveWithoutId(new CompoundTag())));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (PENDING.isEmpty()) {
            return;
        }
        List<PendingWarg> batch = new ArrayList<>(PENDING);
        PENDING.clear();
        for (PendingWarg pending : batch) {
            SkeletalWarg replacement = VivideruEntityTypes.SKELETAL_WARG.get().create(pending.level());
            if (replacement != null) {
                replacement.load(pending.data());
                pending.level().addFreshEntity(replacement);
            }
        }
    }

    private record PendingWarg(ServerLevel level, CompoundTag data) {
    }
}
