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
