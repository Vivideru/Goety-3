package com.Polarice3.Goety.common.world.processors;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Goety.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WaterloggingStopProcessor>> WATERLOGGING_STOP_PROCESSOR = STRUCTURE_PROCESSOR.register("waterlogging_stop_processor", () -> () -> WaterloggingStopProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<CobbleAgeProcessor>> COBBLE_AGE_PROCESSOR = STRUCTURE_PROCESSOR.register("cobble_age_processor", () -> () -> CobbleAgeProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<ReplaceProcessor>> REPLACE_PROCESSOR = STRUCTURE_PROCESSOR.register("replace_processor", () -> () -> ReplaceProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WeatheredProcessor>> WEATHERED_PROCESSOR = STRUCTURE_PROCESSOR.register("weathered_processor", () -> () -> WeatheredProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<MarshProcessor>> MARSH_PROCESSOR = STRUCTURE_PROCESSOR.register("marsh_processor", () -> () -> MarshProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WreckProcessor>> WRECK_PROCESSOR = STRUCTURE_PROCESSOR.register("wreck_processor", () -> () -> WreckProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<RotPotProcessor>> ROT_POT_PROCESSOR = STRUCTURE_PROCESSOR.register("rot_pot_processor", () -> () -> RotPotProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<RotFarmProcessor>> ROT_FARM_PROCESSOR = STRUCTURE_PROCESSOR.register("rot_farm_processor", () -> () -> RotFarmProcessor.CODEC);
}
