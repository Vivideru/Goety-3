package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrineBlend;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorWindShrineMixin {
    /** Height queries (structure placement, heightmaps of unfinished chunks) see the land built around Wind Shrines too. */
    @ModifyExpressionValue(method = "iterateNoiseColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/blending/Blender;empty()Lnet/minecraft/world/level/levelgen/blending/Blender;"))
    private Blender goety$blendShrinesInColumn(Blender original, @Local(argsOnly = true) RandomState randomState, @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int z) {
        return WindShrineBlend.blender(original, randomState, x, z, 1);
    }

    /** Carvers keep out of the land built around Wind Shrines while this chunk is carved (see {@link WindShrineBlend#beginCarving}). */
    @Inject(method = "applyCarvers", at = @At("HEAD"))
    private void goety$guardShrineLand(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step, CallbackInfo ci) {
        WindShrineBlend.beginCarving(randomState, chunk.getPos());
    }

    @Inject(method = "applyCarvers", at = @At("RETURN"))
    private void goety$releaseShrineLand(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step, CallbackInfo ci) {
        WindShrineBlend.endCarving();
    }
}
