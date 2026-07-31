package com.Polarice3.Goety.common.world.processors;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Goety.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WaterloggingStopProcessor>> WATERLOGGING_STOP_PROCESSOR = STRUCTURE_PROCESSOR.register("waterlogging_stop_processor", () -> () -> WaterloggingStopProcessor.CODEC);
}
