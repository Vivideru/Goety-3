package com.Polarice3.Goety.common.listeners;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.events.IllagerSpawner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.HashMap;
import java.util.Map;

public class IllagerAssaultListener extends SimpleJsonResourceReloadListener {
    public static Map<ResourceLocation, IllagerSpawner.IllagerDataType> ILLAGER_LIST = new HashMap<>();
    private static final Gson GSON = (new GsonBuilder()).create();
    private final ICondition.IContext context;

    public IllagerAssaultListener(ICondition.IContext context) {
        super(GSON, "illager_assault");
        this.context = context;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        ILLAGER_LIST.clear();
        for (int i = 0; i < objectIn.size(); i++) {
            ResourceLocation location = (ResourceLocation) objectIn.keySet().toArray()[i];
            JsonObject object = objectIn.get(location).getAsJsonObject();
            // NeoForge 1.21 moved custom JSON conditions to codecs; this keeps condition checks active for this reload listener.
            if (!ICondition.conditionsMatched(JsonOps.INSTANCE, object)){
                Goety.LOGGER.debug("Skipping loading illager entry {} as it's conditions were not met", location);
            } else {
                String name = object.getAsJsonPrimitive("entity_type").getAsString();
                ResourceLocation resourceLocation = ResourceLocation.parse(name);
                JsonObject data = object.getAsJsonObject("registry");
                float thresholdTimes = data.getAsJsonPrimitive("threshold_times").getAsFloat();
                int max = data.getAsJsonPrimitive("max").getAsInt();
                int extra = data.getAsJsonPrimitive("extra").getAsInt();
                float chance = data.getAsJsonPrimitive("chance").getAsFloat();
                JsonObject data2 = data.getAsJsonObject("riding");
                ResourceLocation resourceLocation1 = null;
                float chance2 = 0.0F;
                if (data2 != null) {
                    resourceLocation1 = ResourceLocation.parse(data2.getAsJsonPrimitive("mount_type").getAsString());
                    if (!BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation1)) {
                        resourceLocation1 = null;
                    }
                    chance2 = data2.getAsJsonPrimitive("ride_chance").getAsFloat();
                }
                ILLAGER_LIST.put(location, new IllagerSpawner.IllagerDataType(resourceLocation, thresholdTimes, max, extra, chance, resourceLocation1, chance2));
            }
        }
    }
}
