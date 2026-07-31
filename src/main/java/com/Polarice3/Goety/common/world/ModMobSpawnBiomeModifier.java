package com.Polarice3.Goety.common.world;

import com.Polarice3.Goety.Goety;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Based on code by @AlexModGuy
 */
public class ModMobSpawnBiomeModifier implements BiomeModifier {
    private static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<? extends BiomeModifier>> SERIALIZER = DeferredHolder.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Goety.location("mob_spawns"));

    public ModMobSpawnBiomeModifier() {
    }

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            ModLevelRegistry.addBiomeSpawns(biome, builder);
        }
    }

    public MapCodec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static MapCodec<ModMobSpawnBiomeModifier> makeCodec() {
        // Biome modifiers are MapCodec-backed in NeoForge 1.21.
        return MapCodec.unit(new ModMobSpawnBiomeModifier());
    }
}
