package com.Polarice3.Goety.utils;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfiguredItemUtil {
    public static int durability(ModConfigSpec.ConfigValue<Integer> configValue, int fallback) {
        return positive(configValue, fallback);
    }

    public static int positive(ModConfigSpec.ConfigValue<Integer> configValue, int fallback) {
        try {
            int value = configValue.get();
            return value > 0 ? value : fallback;
        } catch (IllegalStateException exception) {
            return fallback;
        }
    }
}
