package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrinePlacementRules;
import com.Vivideru.Goety.common.world.structures.crypt.CryptPlacementRules;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer", priority = 1100)
public abstract class JigsawPlacerWindShrineMixin {
    @Shadow
    @Final
    private Registry<StructureTemplatePool> pools;

    @Shadow
    @Final
    private StructureTemplateManager structureTemplateManager;

    @Inject(method = "tryPlacingChildren", at = @At("HEAD"))
    private void goety$rememberParent(PoolElementStructurePiece parent, MutableObject<VoxelShape> free, int depth, boolean expansionHack, LevelHeightAccessor heightAccessor, RandomState randomState, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings, CallbackInfo ci) {
        WindShrinePlacementRules.beginParent(parent, depth, ((MutableObject<?>) free).getValue(), this.structureTemplateManager);
        CryptPlacementRules.beginParent(parent, depth);
    }

    @ModifyExpressionValue(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/JigsawBlock;getFrontFacing(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction;", ordinal = 0), require = 0)
    private Direction goety$captureParentDirection(Direction direction) {
        WindShrinePlacementRules.parentDirection(direction);
        CryptPlacementRules.parentDirection(direction);
        return direction;
    }

    @WrapOperation(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;getShuffledTemplates(Lnet/minecraft/util/RandomSource;)Ljava/util/List;"), require = 0)
    private List<StructurePoolElement> goety$reweightCandidates(StructureTemplatePool pool, RandomSource random, Operation<List<StructurePoolElement>> original) {
        return CryptPlacementRules.reweight(WindShrinePlacementRules.reweight(original.call(pool, random), random, this.pools, this.structureTemplateManager));
    }

    @WrapOperation(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructurePoolElement;getBoundingBox(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;", ordinal = 1), require = 0)
    private BoundingBox goety$captureCandidate(StructurePoolElement element, StructureTemplateManager templateManager, BlockPos pos, Rotation rotation, Operation<BoundingBox> original) {
        WindShrinePlacementRules.candidate(element, this.pools, templateManager);
        CryptPlacementRules.candidate(element, this.structureTemplateManager);
        return original.call(element, templateManager, pos, rotation);
    }

    @ModifyExpressionValue(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;deflate(D)Lnet/minecraft/world/phys/AABB;"), require = 0)
    private AABB goety$captureCandidateBounds(AABB bounds) {
        WindShrinePlacementRules.candidateBounds(bounds);
        CryptPlacementRules.candidateBounds(bounds);
        return bounds;
    }

    @WrapOperation(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/Shapes;joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z"), require = 0)
    private boolean goety$collisionTest(VoxelShape free, VoxelShape candidate, BooleanOp op, Operation<Boolean> original) {
        return CryptPlacementRules.rejectOrCollides(WindShrinePlacementRules.rejectOrCollides(original.call(free, candidate, op)));
    }
}
