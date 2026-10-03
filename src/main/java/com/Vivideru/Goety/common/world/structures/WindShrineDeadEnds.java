package com.Vivideru.Goety.common.world.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;

public final class WindShrineDeadEnds {
    private static final ResourceKey<StructureTemplatePool> END_POOL = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("goety", "wind_shrine/end"));
    private static final String CAP_ELEMENT = "connector_end_1";

    private WindShrineDeadEnds() {
    }

    public static void prune(List<StructurePiece> pieces, StructureTemplateManager templates, Registry<StructureTemplatePool> pools, long seed) {
        StructurePoolElement cap = capElement(pools);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (StructurePiece piece : new ArrayList<>(pieces)) {
                if (!(piece instanceof PoolElementStructurePiece stair) || !stair.getElement().toString().contains("stairs")) {
                    continue;
                }
                StructureTemplate.StructureBlockInfo top = null;
                StructureTemplate.StructureBlockInfo bottom = null;
                for (StructureTemplate.StructureBlockInfo info : stair.getElement().getShuffledJigsawBlocks(templates, stair.getPosition(), stair.getRotation(), RandomSource.create(seed))) {
                    if (JigsawBlock.getFrontFacing(info.state()).getAxis().isVertical()) {
                        continue;
                    }
                    if (top == null || info.pos().getY() > top.pos().getY()) {
                        top = info;
                    }
                    if (bottom == null || info.pos().getY() < bottom.pos().getY()) {
                        bottom = info;
                    }
                }
                if (top == null || bottom == null || top == bottom) {
                    continue;
                }
                BlockPos foot = bottom.pos().relative(JigsawBlock.getFrontFacing(bottom.state()));
                StructurePiece below = pieceContaining(pieces, foot, piece);
                if (below != null && !isClosure(below)) {
                    continue;
                }
                pieces.remove(piece);
                if (below != null) {
                    pieces.remove(below);
                }
                if (cap != null) {
                    StructurePiece wall = capPiece(cap, templates, top.pos(), JigsawBlock.getFrontFacing(top.state()));
                    if (wall != null) {
                        pieces.add(wall);
                    }
                }
                changed = true;
            }
        }
    }

    private static boolean isClosure(StructurePiece piece) {
        if (!(piece instanceof PoolElementStructurePiece pool)) {
            return false;
        }
        String name = pool.getElement().toString();
        return name.contains("connector_end") || name.contains("mobs/");
    }

    private static StructurePiece pieceContaining(List<StructurePiece> pieces, BlockPos pos, StructurePiece except) {
        for (StructurePiece piece : pieces) {
            if (piece != except && piece.getBoundingBox().isInside(pos)) {
                return piece;
            }
        }
        return null;
    }

    private static StructurePoolElement capElement(Registry<StructureTemplatePool> pools) {
        StructureTemplatePool pool = pools.get(END_POOL);
        if (pool == null) {
            return null;
        }
        for (StructurePoolElement element : pool.getShuffledTemplates(RandomSource.create(0L))) {
            if (element.toString().contains(CAP_ELEMENT)) {
                return element;
            }
        }
        return null;
    }

    private static StructurePiece capPiece(StructurePoolElement element, StructureTemplateManager templates, BlockPos jigsawPos, Direction facing) {
        for (Rotation rotation : Rotation.values()) {
            for (StructureTemplate.StructureBlockInfo info : element.getShuffledJigsawBlocks(templates, BlockPos.ZERO, rotation, RandomSource.create(0L))) {
                if (JigsawBlock.getFrontFacing(info.state()) == facing) {
                    BlockPos origin = jigsawPos.subtract(info.pos());
                    return new PoolElementStructurePiece(templates, element, origin, element.getGroundLevelDelta(), rotation, element.getBoundingBox(templates, origin, rotation), LiquidSettings.APPLY_WATERLOGGING);
                }
            }
        }
        return null;
    }
}
