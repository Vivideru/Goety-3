package com.Polarice3.Goety.common.world.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrimoireData extends SavedData {
    private static final String DATA_NAME = "goety_grimoire_data";

    public Map<UUID, CompoundTag> grimoireLists = new HashMap<>();

    @Nullable
    public static GrimoireData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return null;
        }
        DimensionDataStorage storage = overworld.getDataStorage();
        // DimensionDataStorage now takes a Factory bundling the constructor and registry-aware loader.
        return storage.computeIfAbsent(new SavedData.Factory<>(GrimoireData::new, GrimoireData::load), DATA_NAME);
    }

    public static GrimoireData load(CompoundTag tag, HolderLookup.Provider provider) {
        GrimoireData data = new GrimoireData();

        CompoundTag playersTag = tag.getCompound("Players");
        for (String uuidString : playersTag.getAllKeys()) {
            UUID playerUUID = UUID.fromString(uuidString);
            CompoundTag playerData = playersTag.getCompound(uuidString);
            data.grimoireLists.put(playerUUID, playerData);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag playersTag = new CompoundTag();

        for (Map.Entry<UUID, CompoundTag> entry : this.grimoireLists.entrySet()) {
            playersTag.put(entry.getKey().toString(), entry.getValue());
        }

        tag.put("Players", playersTag);
        return tag;
    }

    public CompoundTag getPlayerLists(UUID playerUUID) {
        return this.grimoireLists.getOrDefault(playerUUID, new CompoundTag());
    }

    public void setPlayerLists(UUID playerUUID, CompoundTag listsData) {
        this.grimoireLists.put(playerUUID, listsData);
        this.setDirty();
    }

    public void removePlayerLists(UUID playerUUID) {
        this.grimoireLists.remove(playerUUID);
        this.setDirty();
    }

    public boolean hasPlayerData(UUID playerUUID) {
        return this.grimoireLists.containsKey(playerUUID);
    }
}
