package com.Vivideru.Goety.common.world;

import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WargTotemData extends SavedData {
    private static final String DATA_NAME = "goety_warg_totems";
    private final Map<UUID, Entry> wargs = new HashMap<>();

    public static WargTotemData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(WargTotemData::new, WargTotemData::load), DATA_NAME);
    }

    public boolean canCreate(UUID owner, ResourceKey<Level> dimension, BlockPos totemPos) {
        if (MainConfig.WargLimit.get() <= 0) {
            return false;
        }
        long owned = this.wargs.values().stream().filter(entry -> entry.owner().equals(owner)).count();
        boolean occupiedTotem = this.wargs.values().stream().anyMatch(entry -> entry.owner().equals(owner)
                && entry.dimension().equals(dimension.location()) && entry.totemPos().equals(totemPos));
        return owned < MainConfig.WargLimit.get() && !occupiedTotem;
    }

    public void register(UUID warg, UUID owner, ResourceKey<Level> dimension, BlockPos totemPos) {
        Entry next = new Entry(owner, dimension.location(), totemPos.immutable());
        if (!next.equals(this.wargs.put(warg, next))) {
            this.setDirty();
        }
    }

    public void unregister(UUID warg) {
        if (this.wargs.remove(warg) != null) {
            this.setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag entries = new ListTag();
        this.wargs.forEach((warg, entry) -> {
            CompoundTag saved = new CompoundTag();
            saved.putUUID("Warg", warg);
            saved.putUUID("Owner", entry.owner());
            saved.putString("Dimension", entry.dimension().toString());
            saved.putLong("TotemPos", entry.totemPos().asLong());
            entries.add(saved);
        });
        tag.put("Wargs", entries);
        return tag;
    }

    private static WargTotemData load(CompoundTag tag, HolderLookup.Provider provider) {
        WargTotemData data = new WargTotemData();
        ListTag entries = tag.getList("Wargs", Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); ++i) {
            CompoundTag saved = entries.getCompound(i);
            ResourceLocation dimension = ResourceLocation.tryParse(saved.getString("Dimension"));
            if (saved.hasUUID("Warg") && saved.hasUUID("Owner") && dimension != null) {
                data.wargs.put(saved.getUUID("Warg"), new Entry(saved.getUUID("Owner"), dimension,
                        BlockPos.of(saved.getLong("TotemPos"))));
            }
        }
        return data;
    }

    private record Entry(UUID owner, ResourceLocation dimension, BlockPos totemPos) {
    }
}
