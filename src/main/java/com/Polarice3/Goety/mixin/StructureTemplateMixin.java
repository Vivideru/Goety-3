package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.world.processors.ModProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Based from @TelepathicGrunt's codes "<a href="https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.19.0-Forge/src/main/java/com/telepathicgrunt/repurposedstructures/mixin/structures/StructureTemplateMixin.java">...</a>".
 */
@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @Shadow
    @Final
    private List<StructureTemplate.Palette> palettes;

    @Inject(
            method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z",
            at = @At(value = "HEAD")
    )
    private void repurposedstructures_preventAutoWaterlogging(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos1,
                                                              BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings,
                                                              RandomSource random, int flag, CallbackInfoReturnable<Boolean> cir) {

        if(structurePlaceSettings.getProcessors().stream().anyMatch(processor ->
                ((StructureProcessorAccessor)processor).callGetType() == ModProcessors.WATERLOGGING_STOP_PROCESSOR.get())) {
            // 1.21 replaced keep-liquids with LiquidSettings; ignore waterlogging to preserve this processor's behavior.
            structurePlaceSettings.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        }
    }

    @Inject(
            method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z",
            at = @At(value = "RETURN")
    )
    private void goety$updateCursedBarConnections(ServerLevelAccessor level, BlockPos origin,
                                                   BlockPos pivot, StructurePlaceSettings settings,
                                                   RandomSource random, int flags, CallbackInfoReturnable<Boolean> cir) {
        if (!Boolean.TRUE.equals(cir.getReturnValue())) {
            return;
        }

        // Jigsaw placement skips shape updates, so repair only cursed bars stored beside Goety walls.
        for (StructureTemplate.Palette palette : this.palettes) {
            for (StructureTemplate.StructureBlockInfo info : palette.blocks(ModBlocks.CURSED_BARS_BLOCK.get())) {
                this.goety$repairCursedBars(level, origin, settings, info.pos(), flags);
            }
            for (StructureTemplate.StructureBlockInfo info : palette.blocks(ModBlocks.SHADE_STONE_BRICK_WALL_BLOCK.get())) {
                BlockPos wallPos = StructureTemplate.calculateRelativePosition(settings, info.pos()).offset(origin);
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    this.goety$repairCursedBars(level, wallPos.relative(direction), flags);
                }
            }
        }
    }

    private void goety$repairCursedBars(ServerLevelAccessor level, BlockPos origin,
                                         StructurePlaceSettings settings, BlockPos relativePos, int flags) {
        this.goety$repairCursedBars(level, StructureTemplate.calculateRelativePosition(settings, relativePos).offset(origin), flags);
    }

    private void goety$repairCursedBars(ServerLevelAccessor level, BlockPos pos, int flags) {
        BlockState state = level.getBlockState(pos);
        if (state.is(ModBlocks.CURSED_BARS_BLOCK.get())) {
            BlockState updatedState = Block.updateFromNeighbourShapes(state, level, pos);
            if (updatedState != state) {
                level.setBlock(pos, updatedState, flags & ~Block.UPDATE_NEIGHBORS | Block.UPDATE_KNOWN_SHAPE);
            }
        }
    }
}
