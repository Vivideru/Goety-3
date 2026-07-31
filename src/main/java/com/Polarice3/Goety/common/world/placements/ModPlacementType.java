package com.Polarice3.Goety.common.world.placements;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModPlacementType {
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPE = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, Goety.MOD_ID);

    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<ModRandomSpread>> MOD_RANDOM_SPREAD = STRUCTURE_PLACEMENT_TYPE.register("random_spread", () -> () -> ModRandomSpread.CODEC);
    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<ModMinorRandomSpread>> MOD_MINOR_RANDOM_SPREAD = STRUCTURE_PLACEMENT_TYPE.register("minor_random_spread", () -> () -> ModMinorRandomSpread.CODEC);

}
