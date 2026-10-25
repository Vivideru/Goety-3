package com.Vivideru.Goety.client.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ArmorModelCache {
    private static final Map<ModelLayerLocation, Object> MODELS = new HashMap<>();

    private ArmorModelCache() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(ModelLayerLocation layer, Function<ModelPart, T> factory) {
        return (T) MODELS.computeIfAbsent(layer, location -> factory.apply(Minecraft.getInstance().getEntityModels().bakeLayer(location)));
    }

    public static void clear() {
        MODELS.clear();
    }
}
