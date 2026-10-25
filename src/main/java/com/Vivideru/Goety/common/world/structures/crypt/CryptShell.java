package com.Vivideru.Goety.common.world.structures.crypt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.block.Mirror;

import java.util.ArrayList;
import java.util.Optional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Seals the crypt into the rock: every cave, pocket of water or plant within a few blocks of a crypt piece, below the
 * ground surface, becomes stone (deepslate at depth), so rooms and corridors are completely surrounded.
 */
public final class CryptShell {
    private static final int MARGIN = 4;
    private static final int DEEPSLATE_TOP = 8;
    /** Structure starts are available to the decoration step only this many chunks away. */
    private static final int REGION_CHUNKS = 8;

    private CryptShell() {
    }

    public static void apply(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        if (CryptStructure.classic()) {
            return;
        }
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ChunkPos chunkPos = chunk.getPos();
        Set<StructureStart> starts = startsAround(level, chunkPos, registry);
        if (starts.isEmpty()) {
            return;
        }
        BoundingBox chunkBox = new BoundingBox(chunkPos.getMinBlockX(), chunk.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), chunk.getMaxBuildHeight() - 1, chunkPos.getMaxBlockZ());
        BoundingBox wide = chunkBox.inflatedBy(MARGIN);
        BoundingBox coverReach = chunkBox.inflatedBy(COVER_SPREAD);
        List<BoundingBox> pieces = new ArrayList<>();
        List<BoundingBox> shells = new ArrayList<>();
        List<BoundingBox> covers = new ArrayList<>();
        for (StructureStart start : starts) {
            for (StructurePiece piece : start.getPieces()) {
                BoundingBox box = piece.getBoundingBox();
                boolean entrance = piece instanceof PoolElementStructurePiece pool && pool.getElement().toString().contains("crypt/entrance");
                if (!entrance && box.getXSpan() > 1 && box.getZSpan() > 1 && box.intersects(coverReach)) {
                    covers.add(box);
                }
                if (!box.intersects(wide)) {
                    continue;
                }
                pieces.add(box);
                if (box.getXSpan() > 1 && box.getZSpan() > 1) {
                    shells.add(box.inflatedBy(MARGIN));
                }
            }
        }
        if (!covers.isEmpty()) {
            cover(chunk, covers);
        }
        if (shells.isEmpty()) {
            return;
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        long seed = level.getSeed();
        // Structure voids inside a piece are "let the ground show": underground they must be rock too, never a cave.
        StructureTemplateManager templates = level.getLevel().getStructureManager();
        for (StructureStart start : starts) {
            for (StructurePiece piece : start.getPieces()) {
                if (!(piece instanceof PoolElementStructurePiece poolPiece) || !piece.getBoundingBox().intersects(chunkBox)) {
                    continue;
                }
                for (BlockPos pos : voids(poolPiece, templates)) {
                    if (!chunkBox.isInside(pos) || pos.getY() >= chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, pos.getX() & 15, pos.getZ() & 15)) {
                        continue;
                    }
                    BlockState state = chunk.getBlockState(pos);
                    if (state.isAir() || !state.getFluidState().isEmpty() || state.canBeReplaced()) {
                        chunk.setBlockState(pos, rock(seed, pos.getX(), pos.getY(), pos.getZ()), false);
                    }
                }
            }
        }
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = chunkPos.getMinBlockX() + lx;
                int z = chunkPos.getMinBlockZ() + lz;
                int ground = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, lx, lz);
                int minY = Integer.MAX_VALUE;
                int maxY = Integer.MIN_VALUE;
                for (BoundingBox shell : shells) {
                    if (x >= shell.minX() && x <= shell.maxX() && z >= shell.minZ() && z <= shell.maxZ()) {
                        minY = Math.min(minY, shell.minY());
                        maxY = Math.max(maxY, shell.maxY());
                    }
                }
                if (minY == Integer.MAX_VALUE) {
                    continue;
                }
                minY = Math.max(minY, chunk.getMinBuildHeight());
                maxY = Math.min(maxY, ground - 1);
                for (int y = minY; y <= maxY; y++) {
                    if (insidePiece(pieces, x, y, z)) {
                        continue;
                    }
                    // The shell follows the rounded distance from the pieces and a grain of noise, so from a cave it reads as rock, not as a box.
                    double limit = MARGIN + noise3(seed, x, y, z) * SHELL_GRAIN;
                    if (distanceToPieces(pieces, x, y, z) > limit) {
                        continue;
                    }
                    BlockState state = chunk.getBlockState(cursor.set(x, y, z));
                    if (state.isAir() || !state.getFluidState().isEmpty() || state.canBeReplaced() || state.is(Blocks.VINE) || state.is(Blocks.GLOW_LICHEN)) {
                        chunk.setBlockState(cursor, rock(seed, x, y, z), false);
                    }
                }
            }
        }
    }

    /**
     * Every crypt start (dungeon or wing) that this chunk or one of its eight neighbours knows about. The neighbours'
     * references are read directly: the generation region holds structure starts only within 8 chunks of this chunk,
     * so a start a neighbour points to further away must be skipped, not fetched.
     */
    public static Set<StructureStart> startsAround(WorldGenLevel level, ChunkPos chunkPos, Registry<Structure> registry) {
        Set<StructureStart> starts = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                ChunkAccess neighbour = level.getChunk(chunkPos.x + dx, chunkPos.z + dz, ChunkStatus.STRUCTURE_REFERENCES);
                for (Map.Entry<Structure, LongSet> entry : neighbour.getAllReferences().entrySet()) {
                    ResourceLocation id = registry.getKey(entry.getKey());
                    if (id == null || !CryptPlans.isPart(id)) {
                        continue;
                    }
                    for (long reference : entry.getValue()) {
                        ChunkPos startChunk = new ChunkPos(reference);
                        if (startChunk.getChessboardDistance(chunkPos) > REGION_CHUNKS) {
                            continue;
                        }
                        StructureStart start = level.getChunk(startChunk.x, startChunk.z, ChunkStatus.STRUCTURE_STARTS).getStartForStructure(entry.getKey());
                        if (start != null && start.isValid()) {
                            starts.add(start);
                        }
                    }
                }
            }
        }
        return starts;
    }

    /** World positions of the structure voids of a placed piece, read from its template with the piece's rotation. */
    private static List<BlockPos> voids(PoolElementStructurePiece piece, StructureTemplateManager templates) {
        return templateBlocks(piece, templates, Blocks.STRUCTURE_VOID);
    }

    /** World positions of every block of the given kind in a placed piece, read from its template with the piece rotation. */
    public static List<BlockPos> templateBlocks(PoolElementStructurePiece piece, StructureTemplateManager templates, net.minecraft.world.level.block.Block block) {
        String element = piece.getElement().toString();
        int start = element.indexOf("goety:");
        if (start < 0) {
            return List.of();
        }
        int end = start;
        while (end < element.length() && (Character.isLetterOrDigit(element.charAt(end)) || "_:/.-".indexOf(element.charAt(end)) >= 0)) {
            end++;
        }
        ResourceLocation id = ResourceLocation.tryParse(element.substring(start, end));
        if (id == null) {
            return List.of();
        }
        Optional<StructureTemplate> template = templates.get(id);
        if (template.isEmpty()) {
            return List.of();
        }
        List<BlockPos> result = new ArrayList<>();
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(piece.getRotation()).setMirror(Mirror.NONE);
        for (StructureTemplate.StructureBlockInfo info : template.get().filterBlocks(piece.getPosition(), settings, block)) {
            result.add(info.pos());
        }
        return result;
    }

    /** Rock between a buried piece and the surface, and how far the cover eases out around it. */
    private static final int COVER_DEPTH = 3;
    private static final int COVER_SPREAD = 12;
    private static final double COVER_SLOPE = 0.8D;

    /**
     * Where a valley, a river or a cliff would leave a room or corridor open to the sky, the land is raised over it: rock up
     * to a few blocks above the piece, then the column's own soil and surface block, easing down to the natural ground
     * around. The entrance is left to its own hill.
     */
    private static void cover(ChunkAccess chunk, List<BoundingBox> covers) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean raised = false;
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = chunkPos.getMinBlockX() + lx;
                int z = chunkPos.getMinBlockZ() + lz;
                int required = Integer.MIN_VALUE;
                for (BoundingBox box : covers) {
                    int dx = x < box.minX() ? box.minX() - x : x > box.maxX() ? x - box.maxX() : 0;
                    int dz = z < box.minZ() ? box.minZ() - z : z > box.maxZ() ? z - box.maxZ() : 0;
                    double distance = Math.sqrt((double) dx * dx + (double) dz * dz);
                    if (distance > COVER_SPREAD) {
                        continue;
                    }
                    required = Math.max(required, box.maxY() + COVER_DEPTH - (int) Math.round(distance * COVER_SLOPE));
                }
                int ground = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, lx, lz) - 1;
                if (required <= ground || ground < chunk.getMinBuildHeight()) {
                    continue;
                }
                BlockState top = chunk.getBlockState(cursor.set(x, ground, z));
                BlockState below = chunk.getBlockState(cursor.set(x, ground - 1, z));
                if (!isSurface(top)) {
                    top = nearestSurface(chunk, lx, lz, cursor);
                }
                BlockState filler = isSoil(below) && !below.is(Blocks.GRASS_BLOCK) ? below : top.is(Blocks.GRASS_BLOCK) ? Blocks.DIRT.defaultBlockState() : top;
                for (int y = ground; y <= required; y++) {
                    BlockState state = y == required ? top : y >= required - 2 ? filler : Blocks.STONE.defaultBlockState();
                    if (y == ground && !chunk.getBlockState(cursor.set(x, y, z)).isAir() && chunk.getBlockState(cursor).getFluidState().isEmpty()) {
                        continue;
                    }
                    chunk.setBlockState(cursor.set(x, y, z), state, false);
                }
                raised = true;
            }
        }
        if (raised) {
            com.Vivideru.Goety.common.world.structures.StructureTerrain.refreshHeightmaps(chunk);
        }
    }

    /** A block the land shows on top: grass, podzol, sand, snow... never bare dirt or stone. */
    private static boolean isSurface(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM) || state.is(net.minecraft.tags.BlockTags.SAND)
                || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.MUD) || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.GRAVEL);
    }

    /** The surface block of the nearest column of this chunk that shows one, grass failing that. */
    private static BlockState nearestSurface(ChunkAccess chunk, int lx, int lz, BlockPos.MutableBlockPos cursor) {
        for (int radius = 1; radius <= 8; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int sx = lx + dx;
                    int sz = lz + dz;
                    if (sx < 0 || sx > 15 || sz < 0 || sz > 15 || Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    int top = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, sx, sz) - 1;
                    BlockState state = chunk.getBlockState(cursor.set(chunk.getPos().getMinBlockX() + sx, top, chunk.getPos().getMinBlockZ() + sz));
                    if (isSurface(state)) {
                        return state;
                    }
                }
            }
        }
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    private static boolean isSoil(BlockState state) {
        return state.is(net.minecraft.tags.BlockTags.DIRT) || state.is(net.minecraft.tags.BlockTags.SAND) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY)
                || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.SANDSTONE) || state.is(Blocks.RED_SANDSTONE) || state.is(Blocks.MUD);
    }

    private static boolean insidePiece(List<BoundingBox> pieces, int x, int y, int z) {
        for (BoundingBox box : pieces) {
            if (box.isInside(x, y, z)) {
                return true;
            }
        }
        return false;
    }

    /** How far the noise moves the edge of the shell in and out. */
    private static final double SHELL_GRAIN = 1.8D;

    /** Euclidean distance from a cell to the nearest piece box (0 inside the inflated footprint of a box face). */
    private static double distanceToPieces(List<BoundingBox> pieces, int x, int y, int z) {
        double best = Double.MAX_VALUE;
        for (BoundingBox box : pieces) {
            int dx = x < box.minX() ? box.minX() - x : x > box.maxX() ? x - box.maxX() : 0;
            int dy = y < box.minY() ? box.minY() - y : y > box.maxY() ? y - box.maxY() : 0;
            int dz = z < box.minZ() ? box.minZ() - z : z > box.maxZ() ? z - box.maxZ() : 0;
            best = Math.min(best, Math.sqrt((double) dx * dx + (double) dy * dy + (double) dz * dz));
        }
        return best;
    }

    /** Smooth value noise in three dimensions, in [-1, 1], with a feature size of about five blocks. */
    private static double noise3(long seed, int x, int y, int z) {
        double scale = 5.0D;
        double fx = x / scale;
        double fy = y / scale;
        double fz = z / scale;
        int x0 = Mth.floor(fx);
        int y0 = Mth.floor(fy);
        int z0 = Mth.floor(fz);
        double tx = smooth(fx - x0);
        double ty = smooth(fy - y0);
        double tz = smooth(fz - z0);
        double c00 = Mth.lerp(tx, hash3(seed, x0, y0, z0), hash3(seed, x0 + 1, y0, z0));
        double c10 = Mth.lerp(tx, hash3(seed, x0, y0 + 1, z0), hash3(seed, x0 + 1, y0 + 1, z0));
        double c01 = Mth.lerp(tx, hash3(seed, x0, y0, z0 + 1), hash3(seed, x0 + 1, y0, z0 + 1));
        double c11 = Mth.lerp(tx, hash3(seed, x0, y0 + 1, z0 + 1), hash3(seed, x0 + 1, y0 + 1, z0 + 1));
        return Mth.lerp(tz, Mth.lerp(ty, c00, c10), Mth.lerp(ty, c01, c11)) * 2.0D - 1.0D;
    }

    private static double smooth(double t) {
        return t * t * (3.0D - 2.0D * t);
    }

    private static double hash3(long seed, int x, int y, int z) {
        long h = seed ^ (x * 0x9E3779B97F4A7C15L) ^ (y * 0xC2B2AE3D27D4EB4FL) ^ (z * 0x165667B19E3779F9L);
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        h ^= h >>> 31;
        return (h >>> 11) * 0x1.0p-53;
    }

    private static BlockState rock(long seed, int x, int y, int z) {
        if (y <= 0) {
            return Blocks.DEEPSLATE.defaultBlockState();
        }
        if (y < DEEPSLATE_TOP && Math.floorMod(Mth.getSeed(x, y, z) ^ seed, DEEPSLATE_TOP) >= y) {
            return Blocks.DEEPSLATE.defaultBlockState();
        }
        return Blocks.STONE.defaultBlockState();
    }
}
