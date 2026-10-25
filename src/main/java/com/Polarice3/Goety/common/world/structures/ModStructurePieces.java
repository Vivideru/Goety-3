package com.Polarice3.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructurePieces {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE = DeferredRegister.create(Registries.STRUCTURE_PIECE, Goety.MOD_ID);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> WRECKED_MANSION_PIECE = setTemplatePieceId(WreckedMansionPieces.MansionPiece::new, "wrecked_mansion_piece");

    private static DeferredHolder<StructurePieceType, StructurePieceType> setTemplatePieceId(StructurePieceType.StructureTemplateType structureTemplateType, String name) {
        return STRUCTURE_PIECE.register(name, () -> structureTemplateType);
    }
}
