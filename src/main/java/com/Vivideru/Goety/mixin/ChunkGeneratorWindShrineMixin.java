package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.StructureTerrain;
import com.Vivideru.Goety.common.world.structures.WindShrinePlans;
import com.Vivideru.Goety.common.world.structures.WindShrineTerrain;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorWindShrineMixin {
    @Inject(method = "applyBiomeDecoration", at = @At("HEAD"))
    private void goety$structureTerrain(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        if (structureManager.shouldGenerateStructures()) {
            WindShrineTerrain.apply(level, chunk, (ChunkGenerator) (Object) this, structureManager);
            StructureTerrain.apply(level, chunk);
        }
    }

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
    private void goety$windShrineCleanup(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        if (structureManager.shouldGenerateStructures()) {
            WindShrineTerrain.cleanup(level, chunk);
        }
    }

    @Inject(method = "createStructures", at = @At("HEAD"))
    private void goety$enterStructureState(RegistryAccess registryAccess, ChunkGeneratorStructureState state, StructureManager structureManager, ChunkAccess chunk, StructureTemplateManager templates, CallbackInfo ci) {
        StructureTerrain.enterStructureState(state);
    }

    @Inject(method = "createStructures", at = @At("RETURN"))
    private void goety$exitStructureState(RegistryAccess registryAccess, ChunkGeneratorStructureState state, StructureManager structureManager, ChunkAccess chunk, StructureTemplateManager templates, CallbackInfo ci) {
        StructureTerrain.exitStructureState();
    }

    @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true)
    private void goety$reserveGround(StructureSet.StructureSelectionEntry entry, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager templates, long seed, ChunkAccess chunk, ChunkPos chunkPos, SectionPos sectionPos, CallbackInfoReturnable<Boolean> cir) {
        ChunkGenerator generator = (ChunkGenerator) (Object) this;
        if (WindShrinePlans.reservesSurface(entry.structure().value(), registryAccess, generator, randomState, templates, seed, chunk, chunkPos)
                || StructureTerrain.reserves(entry.structure().value(), registryAccess, generator, randomState, chunk, chunkPos)) {
            cir.setReturnValue(false);
        }
    }
}
