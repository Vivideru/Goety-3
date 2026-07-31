package com.Vivideru.Goety.compat.apotheosis;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GoetyApotheosisConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue MAX_GEM_POTENCY_PER_WAND;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment(
            "Maximum potency contributed by socketed gems to each potency attribute on one wand.",
            "Affixes and all non-gem bonuses are not affected."
        );
        MAX_GEM_POTENCY_PER_WAND = builder.defineInRange("maxGemPotencyPerWand", 5.0D, 0.0D, 1000.0D);
        SPEC = builder.build();
    }

    private GoetyApotheosisConfig() {
    }
}
