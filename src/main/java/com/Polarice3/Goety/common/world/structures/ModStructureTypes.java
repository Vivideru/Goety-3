package com.Polarice3.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(Registries.STRUCTURE_TYPE, Goety.MOD_ID);

    public static DeferredHolder<StructureType<?>, StructureType<BiggerJigsawStructure>> BIGGER_JIGSAW_STRUCTURE = STRUCTURE_TYPE.register("bigger_jigsaw", () -> () -> BiggerJigsawStructure.CODEC);
    public static DeferredHolder<StructureType<?>, StructureType<CryptStructure>> CRYPT_STRUCTURE = STRUCTURE_TYPE.register("crypt", () -> () -> CryptStructure.CODEC);
    public static DeferredHolder<StructureType<?>, StructureType<FinalTerminalStructure>> FINAL_TERMINAL_STRUCTURE = STRUCTURE_TYPE.register("final_terminal", () -> () -> FinalTerminalStructure.CODEC);

}
