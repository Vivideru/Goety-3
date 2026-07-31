package com.Polarice3.Goety.compat.botania;

import net.neoforged.fml.ModList;

public enum BotaniaLoaded {
    BOTANIA("botania");
    private final boolean loaded;

    BotaniaLoaded(String modid) {
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }

}
