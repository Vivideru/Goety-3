package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrineTerrain;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeWindShrineMixin {
    @Inject(method = "shouldSnow", at = @At("HEAD"), cancellable = true)
    private void goety$noSnowOnWindsweptPeaks(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ChunkAccess chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.BIOMES, false);
        if (chunk != null && chunk.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2).is(WindShrineTerrain.WINDSWEPT_PEAKS_BIOME)) {
            cir.setReturnValue(false);
        }
    }
}
