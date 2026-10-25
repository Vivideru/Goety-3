package com.Vivideru.Goety.common.world;

import net.neoforged.neoforge.common.ModConfigSpec;

/** World generation options of the port (goety/goety-worldgen.toml). */
public final class VivideruWorldConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue CLASSIC_WIND_SHRINE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("wind_shrine");
        builder.comment(
                "Generate the classic Wind Shrine: the plain jigsaw structure placed on the existing terrain,",
                "with no mountain, no terrain shaping around it, no wings and no Windswept Peaks biome.",
                "Takes effect for chunks generated after the change; existing shrines are left as they are.");
        CLASSIC_WIND_SHRINE = builder.define("classicWindShrine", false);
        builder.pop();
        builder.push("crypt");
        builder.comment(
                "Generate the classic Crypt: the previous single jigsaw dungeon with its old rooms, instead of the layered crypt",
                "with its entrance tower, Libraries, wings, boss chamber and Vault.",
                "Takes effect for chunks generated after the change; existing crypts are left as they are.");
        CLASSIC_CRYPT = builder.define("classicCrypt", false);
        builder.pop();
        SPEC = builder.build();
    }

    public static final ModConfigSpec.BooleanValue CLASSIC_CRYPT;

    public static boolean classicCrypt() {
        return SPEC.isLoaded() && CLASSIC_CRYPT.get();
    }

    private VivideruWorldConfig() {
    }

    /** True when the shrine must be built the classic way; safe to call before the config is loaded (defaults to false). */
    public static boolean classicWindShrine() {
        return SPEC.isLoaded() && CLASSIC_WIND_SHRINE.get();
    }
}
