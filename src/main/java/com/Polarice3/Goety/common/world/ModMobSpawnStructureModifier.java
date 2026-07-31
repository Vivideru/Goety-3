package com.Polarice3.Goety.common.world;

import com.Polarice3.Goety.Goety;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Based on code by @AlexModGuy
 */
public class ModMobSpawnStructureModifier implements StructureModifier {
    private static final DeferredHolder<MapCodec<? extends StructureModifier>, MapCodec<? extends StructureModifier>> SERIALIZER = DeferredHolder.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, Goety.location("mob_structure_spawns"));

    public ModMobSpawnStructureModifier() {
    }

    public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase == Phase.ADD) {
            ModLevelRegistry.addStructureSpawns(structure, builder);
        }
    }

    public MapCodec<? extends StructureModifier> codec() {
        return SERIALIZER.get();
    }

    public static MapCodec<ModMobSpawnStructureModifier> makeCodec() {
        // Structure modifiers are MapCodec-backed in NeoForge 1.21.
        return MapCodec.unit(new ModMobSpawnStructureModifier());
    }
}
