package com.Polarice3.Goety.api.magic;

import net.minecraft.network.chat.Component;

// This enum is not registered in enumextensions.json, so it must stay a regular enum on NeoForge.
public enum SpellType {
    NONE("none"),
    NECROMANCY("necromancy"),
    NETHER("nether"),
    ILL("ill"),
    FROST("frost"),
    GEOMANCY("geomancy"),
    WIND("wind"),
    STORM("storm"),
    ABYSS("abyss"),
    WILD("wild"),
    VOID("void");

    private final String baseName;
    private final Component name;

    SpellType(String name){
        this.baseName = name;
        this.name = Component.translatable("spell.goety." + name);
    }

    public String getBaseName() {
        return baseName;
    }

    public Component getName(){
        return name;
    }

}

