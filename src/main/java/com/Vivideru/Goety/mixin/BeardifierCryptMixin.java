package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.crypt.CryptMound;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The crypt buries itself ("bury" terrain adaptation) so that no cave or ravine ever opens into its rooms, but its surface
 * piece, the vestibule under its mound, must not be buried: the land around it is shaped by CryptMound instead. That piece
 * is kept out of the terrain beard here.
 */
@Mixin(Beardifier.class)
public abstract class BeardifierCryptMixin {
    @Redirect(method = "lambda$forStructuresInChunk$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;isCloseToChunk(Lnet/minecraft/world/level/ChunkPos;I)Z"))
    private static boolean goety$skipCryptSurfacePiece(StructurePiece piece, ChunkPos chunkPos, int distance) {
        if (CryptMound.isSurfacePiece(piece)) {
            return false;
        }
        return piece.isCloseToChunk(chunkPos, distance);
    }
}
