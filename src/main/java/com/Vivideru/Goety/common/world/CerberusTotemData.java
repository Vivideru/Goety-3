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

public class CerberusTotemData extends SavedData {
    private static final String DATA_NAME = "goety_cerberus_totems";
    private final Map<UUID, Entry> cerberuses = new HashMap<>();

    public static CerberusTotemData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CerberusTotemData::new, CerberusTotemData::load), DATA_NAME);
    }

    public boolean canCreate(UUID owner, ResourceKey<Level> dimension, BlockPos totemPos) {
        if (MainConfig.CerberusLimit.get() <= 0) {
            return false;
        }
        long owned = this.cerberuses.values().stream().filter(entry -> entry.owner().equals(owner)).count();
        boolean occupiedTotem = this.cerberuses.values().stream().anyMatch(entry -> entry.owner().equals(owner)
                && entry.dimension().equals(dimension.location()) && entry.totemPos().equals(totemPos));
        return owned < MainConfig.CerberusLimit.get() && !occupiedTotem;
    }

    public void register(UUID cerberus, UUID owner, ResourceKey<Level> dimension, BlockPos totemPos) {
        Entry next = new Entry(owner, dimension.location(), totemPos.immutable());
        if (!next.equals(this.cerberuses.put(cerberus, next))) {
            this.setDirty();
        }
    }

    public void unregister(UUID cerberus) {
        if (this.cerberuses.remove(cerberus) != null) {
            this.setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag entries = new ListTag();
        this.cerberuses.forEach((cerberus, entry) -> {
            CompoundTag saved = new CompoundTag();
            saved.putUUID("Cerberus", cerberus);
            saved.putUUID("Owner", entry.owner());
            saved.putString("Dimension", entry.dimension().toString());
            saved.putLong("TotemPos", entry.totemPos().asLong());
            entries.add(saved);
        });
        tag.put("Cerberuses", entries);
        return tag;
    }

    private static CerberusTotemData load(CompoundTag tag, HolderLookup.Provider provider) {
        CerberusTotemData data = new CerberusTotemData();
        ListTag entries = tag.getList("Cerberuses", Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); ++i) {
            CompoundTag saved = entries.getCompound(i);
            ResourceLocation dimension = ResourceLocation.tryParse(saved.getString("Dimension"));
            if (saved.hasUUID("Cerberus") && saved.hasUUID("Owner") && dimension != null) {
                data.cerberuses.put(saved.getUUID("Cerberus"), new Entry(saved.getUUID("Owner"), dimension,
                        BlockPos.of(saved.getLong("TotemPos"))));
            }
        }
        return data;
    }

    private record Entry(UUID owner, ResourceLocation dimension, BlockPos totemPos) {
    }
}
