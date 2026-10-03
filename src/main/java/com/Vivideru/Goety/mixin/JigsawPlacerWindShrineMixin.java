package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrinePlacementRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public abstract class JigsawPlacerWindShrineMixin {
    @Shadow
    @Final
    private Registry<StructureTemplatePool> pools;

    @Shadow
    @Final
    private StructureTemplateManager structureTemplateManager;

    @Inject(method = "tryPlacingChildren", at = @At("HEAD"))
    private void goety$rememberParent(PoolElementStructurePiece parent, MutableObject<VoxelShape> free, int depth, boolean expansionHack, LevelHeightAccessor heightAccessor, RandomState randomState, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings, CallbackInfo ci) {
        WindShrinePlacementRules.beginParent(parent, depth, free.getValue(), this.structureTemplateManager);
    }

    @Redirect(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/JigsawBlock;getFrontFacing(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction;", ordinal = 0))
    private Direction goety$captureParentDirection(BlockState state) {
        Direction direction = JigsawBlock.getFrontFacing(state);
        WindShrinePlacementRules.parentDirection(direction);
        return direction;
    }

    @Redirect(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;getShuffledTemplates(Lnet/minecraft/util/RandomSource;)Ljava/util/List;"))
    private List<StructurePoolElement> goety$reweightCandidates(StructureTemplatePool pool, RandomSource random) {
        return WindShrinePlacementRules.reweight(pool.getShuffledTemplates(random), random, this.pools, this.structureTemplateManager);
    }

    @Redirect(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructurePoolElement;getBoundingBox(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;", ordinal = 1))
    private BoundingBox goety$captureCandidate(StructurePoolElement element, StructureTemplateManager templateManager, BlockPos pos, Rotation rotation) {
        WindShrinePlacementRules.candidate(element, this.pools, templateManager);
        return element.getBoundingBox(templateManager, pos, rotation);
    }

    @Redirect(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/Shapes;joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z"))
    private boolean goety$collisionTest(VoxelShape free, VoxelShape candidate, BooleanOp op) {
        return WindShrinePlacementRules.rejectOrCollides(Shapes.joinIsNotEmpty(free, candidate, op), candidate);
    }
}
