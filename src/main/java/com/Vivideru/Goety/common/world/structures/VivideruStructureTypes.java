package com.Vivideru.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VivideruStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, Goety.MOD_ID);
    public static final DeferredRegister<StructurePlacementType<?>> PLACEMENT_TYPES = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, Goety.MOD_ID);
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Goety.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<WindShrineStructure>> WIND_SHRINE = STRUCTURE_TYPES.register("wind_shrine", () -> () -> WindShrineStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<WindShrineExpansionStructure>> WIND_SHRINE_EXPANSION = STRUCTURE_TYPES.register("wind_shrine_expansion", () -> () -> WindShrineExpansionStructure.CODEC);
    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<WindShrineExpansionPlacement>> WIND_SHRINE_EXPANSION_PLACEMENT = PLACEMENT_TYPES.register("wind_shrine_expansion", () -> () -> WindShrineExpansionPlacement.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WindShrineFoundationProcessor>> WIND_SHRINE_FOUNDATION = PROCESSOR_TYPES.register("wind_shrine_foundation", () -> () -> WindShrineFoundationProcessor.CODEC);

    public static void init() {
        STRUCTURE_TYPES.register(Goety.getModEventBus());
        PLACEMENT_TYPES.register(Goety.getModEventBus());
        PROCESSOR_TYPES.register(Goety.getModEventBus());
    }
}
