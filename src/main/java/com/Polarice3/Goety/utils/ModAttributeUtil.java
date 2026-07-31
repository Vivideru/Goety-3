package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.Goety;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Locale;
import java.util.UUID;

public class ModAttributeUtil {
    public static AttributeModifier create(UUID uuid, String name, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifier(stableId(uuid, name), amount, operation);
    }

    public static AttributeModifier create(String name, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifier(stableId(name), amount, operation);
    }

    public static AttributeModifier create(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifier(id, amount, operation);
    }

    public static ResourceLocation stableId(UUID uuid, String name) {
        // Minecraft 1.21 keys attribute modifiers by ResourceLocation, so keep the old UUID in the path to preserve modifier identity across the port.
        return ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, sanitize(name) + "_" + uuid);
    }

    public static ResourceLocation stableId(String name) {
        return ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, sanitize(name));
    }

    private static String sanitize(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");
    }
}
