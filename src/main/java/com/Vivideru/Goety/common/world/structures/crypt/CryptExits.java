package com.Vivideru.Goety.common.world.structures.crypt;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.world.structures.WindShrinePlans;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The doors a crypt structure leaves for the next one: the lower doors of its Libraries that nothing was attached to
 * (the placement rules keep the door facing away from the tower free). Sorted by position, so every structure that
 * re-plans the crypt sees them in the same order.
 */
public final class CryptExits {
    /** Name of the jigsaws on a Library's lower floor. */
    public static final String STAIR_BOTTOM = "goety:crypt/stair_bottom";
    public static final String CONNECTOR = "goety:crypt/connector";
    public static final String CAP = "crypt/end_cap";
    public static final String SEGMENT_END = "crypt/segment_end";
    private static final ResourceKey<StructureTemplatePool> CAPS_POOL = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "crypt/caps"));
    /** Landing: the straight corridor that starts the next structure (11 wide, 9 tall, 19 deep) must fit in front of the door. */
    private static final int LANDING_WIDTH = 11;
    private static final int LANDING_HEIGHT = 9;
    private static final int LANDING_DEPTH = 19;

    private CryptExits() {
    }

    /** Removes the caps that would sit in the landing of an open door. */
    public static void openExits(List<StructurePiece> pieces, StructureTemplateManager templates, long seed) {
        for (WindShrinePlans.Door door : openDoors(pieces, templates, seed)) {
            BoundingBox landing = landingZone(door.target(), door.facing());
            pieces.removeIf(piece -> isCap(piece) && piece.getBoundingBox().intersects(landing));
        }
    }

    public static boolean isCap(StructurePiece piece) {
        return piece instanceof PoolElementStructurePiece pool && isCapName(pool.getElement().toString());
    }

    public static boolean isCapName(String name) {
        return name.contains(CAP) || name.contains(SEGMENT_END);
    }

    /** Walls off every corridor or room doorway that ended up with nothing in front of it (the jigsaw placer's fallback can miss some). */
    public static void closeOpenDoors(List<StructurePiece> pieces, StructureTemplateManager templates, Registry<StructureTemplatePool> pools, long seed) {
        StructureTemplatePool pool = pools.get(CAPS_POOL);
        StructurePoolElement cap = null;
        if (pool != null) {
            for (StructurePoolElement element : pool.getShuffledTemplates(RandomSource.create(0L))) {
                if (isCapName(element.toString())) {
                    cap = element;
                    break;
                }
            }
        }
        if (cap == null) {
            return;
        }
        List<StructurePiece> added = new ArrayList<>();
        for (StructurePiece piece : pieces) {
            if (!(piece instanceof PoolElementStructurePiece poolPiece) || isCap(piece)) {
                continue;
            }
            for (StructureTemplate.StructureBlockInfo info : poolPiece.getElement().getShuffledJigsawBlocks(templates, poolPiece.getPosition(), poolPiece.getRotation(), RandomSource.create(seed))) {
                Direction facing = JigsawBlock.getFrontFacing(info.state());
                if (facing.getAxis().isVertical() || info.nbt() == null || !CONNECTOR.equals(info.nbt().getString("name"))) {
                    continue;
                }
                BlockPos target = info.pos().relative(facing);
                if (occupied(pieces, target, piece) || occupied(added, target, null)) {
                    continue;
                }
                StructurePiece wall = capPiece(cap, templates, target, facing.getOpposite());
                if (wall != null) {
                    added.add(wall);
                }
            }
        }
        pieces.addAll(added);
    }

    private static boolean occupied(List<StructurePiece> pieces, BlockPos pos, StructurePiece except) {
        for (StructurePiece other : pieces) {
            if (other != except && other.getBoundingBox().isInside(pos)) {
                return true;
            }
        }
        return false;
    }

    /** A cap rotated so that its jigsaw sits on {@code jigsawPos} looking the given way. */
    private static StructurePiece capPiece(StructurePoolElement element, StructureTemplateManager templates, BlockPos jigsawPos, Direction facing) {
        for (Rotation rotation : Rotation.values()) {
            for (StructureTemplate.StructureBlockInfo info : element.getShuffledJigsawBlocks(templates, BlockPos.ZERO, rotation, RandomSource.create(0L))) {
                if (JigsawBlock.getFrontFacing(info.state()) == facing) {
                    BlockPos origin = jigsawPos.subtract(info.pos());
                    return new PoolElementStructurePiece(templates, element, origin, element.getGroundLevelDelta(), rotation, element.getBoundingBox(templates, origin, rotation), LiquidSettings.IGNORE_WATERLOGGING);
                }
            }
        }
        return null;
    }

    public static List<WindShrinePlans.Door> openDoors(List<StructurePiece> pieces, StructureTemplateManager templates, long seed) {
        List<WindShrinePlans.Door> result = new ArrayList<>();
        for (StructurePiece piece : pieces) {
            if (!(piece instanceof PoolElementStructurePiece poolPiece) || isCap(piece)) {
                continue;
            }
            for (StructureTemplate.StructureBlockInfo info : poolPiece.getElement().getShuffledJigsawBlocks(templates, poolPiece.getPosition(), poolPiece.getRotation(), RandomSource.create(seed))) {
                Direction facing = JigsawBlock.getFrontFacing(info.state());
                if (facing.getAxis().isVertical() || info.nbt() == null || !STAIR_BOTTOM.equals(info.nbt().getString("name"))) {
                    continue;
                }
                BlockPos target = info.pos().relative(facing);
                boolean occupied = false;
                for (StructurePiece other : pieces) {
                    if (other != piece && other.getBoundingBox().isInside(target)) {
                        occupied = true;
                        break;
                    }
                }
                if (!occupied) {
                    result.add(new WindShrinePlans.Door(info.pos(), facing));
                }
            }
        }
        result.sort(Comparator.comparingInt((WindShrinePlans.Door door) -> door.jigsaw().getX()).thenComparingInt(door -> door.jigsaw().getZ()).thenComparingInt(door -> door.jigsaw().getY()));
        return result;
    }

    public static BoundingBox landingZone(BlockPos target, Direction facing) {
        int half = LANDING_WIDTH / 2;
        BlockPos far = target.relative(facing, LANDING_DEPTH - 1);
        boolean alongZ = facing.getAxis() == Direction.Axis.Z;
        return new BoundingBox(Math.min(target.getX(), far.getX()) - (alongZ ? half : 0), target.getY() - 3, Math.min(target.getZ(), far.getZ()) - (alongZ ? 0 : half),
                Math.max(target.getX(), far.getX()) + (alongZ ? half : 0), target.getY() - 3 + LANDING_HEIGHT - 1, Math.max(target.getZ(), far.getZ()) + (alongZ ? 0 : half));
    }
}
