package com.Vivideru.Goety.common.world.structures.crypt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The last pass over the crypt entrance, run once the piece stands in the world and its structure voids have become the
 * real ground. The earthen dome over the vestibule is grown into a hill: its shape, read from the template, spreads
 * outward along a gentle, noise-warped slope until it meets the land, whether that lies below or above the dome's foot,
 * so no regular dome and no flattened platform remain. Every exposed block of earth takes the surface of the nearest dry
 * natural column (grass, sand, snow...), the core its soil, and the same ground cover is scattered on top. The vestibule,
 * the air the template keeps in front of the doorway and a path leading out of it are never built over; where the land
 * rises in front of the door the path is dug through it as a gentle ramp.
 */
public final class CryptMound {
    /** The piece that shows on the surface: the vestibule under its mound, standing on the buried base piece. */
    public static final String SURFACE_PIECE = "crypt/entrance_upper";
    /** How far above the surface piece's bottom the ground lies. */
    public static final int GROUND_OFFSET = 30;

    /** Whether a piece is the crypt's surface piece, the one the land is shaped around rather than buried over. */
    public static boolean isSurfacePiece(net.minecraft.world.level.levelgen.structure.StructurePiece piece) {
        return piece instanceof PoolElementStructurePiece pool && pool.getElement().toString().contains(SURFACE_PIECE);
    }
    /** How far beyond the piece the hill may spread. */
    private static final int SPREAD = 22;
    /** Blocks of height lost per block of distance from the dome. */
    private static final double SLOPE = 0.55D;
    private static final double WARP = 3.5D;
    private static final double BUMPS = 1.3D;
    /** Length of the open path kept in front of the doorway, and how much of it stays level with the door sill. */
    private static final int DOOR_PATH = 14;
    private static final int DOOR_LANDING = 3;
    /** How far beside the path its banks slope down to it. */
    private static final int BANK = 4;
    private static final Set<Block> EARTH = Set.of(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL);
    private static final float COVER_CHANCE = 0.55F;
    private static final float GRASS_CHANCE = 0.2F;

    private CryptMound() {
    }

    /** The natural ground of the chunk being decorated, taken before any structure piece is placed in it. */
    private record Natural(int chunkX, int chunkZ, int[] tops) {
    }

    private static final ThreadLocal<Natural> NATURAL = new ThreadLocal<>();
    private static final ThreadLocal<boolean[]> GROWN = ThreadLocal.withInitial(() -> new boolean[1]);

    /**
     * Grows the hill once per chunk: right before the vegetation, once the pieces are placed, so trees grow on the hill
     * instead of being swallowed by it; at the end of the decoration at the latest, for chunks without vegetation.
     */
    public static void growOnce(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        boolean[] grown = GROWN.get();
        if (!grown[0]) {
            grown[0] = true;
            apply(level, chunk, structureManager);
        }
    }

    /** Called before the pieces are placed: the land as it was is what the entrance must be settled into. */
    public static void remember(ChunkAccess chunk) {
        GROWN.get()[0] = false;
        int[] tops = new int[256];
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                tops[lx << 4 | lz] = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, lx, lz) - 1;
            }
        }
        NATURAL.set(new Natural(chunk.getPos().x, chunk.getPos().z, tops));
    }

    private static int naturalTop(int x, int z, int fallback) {
        Natural natural = NATURAL.get();
        if (natural == null || (x >> 4) != natural.chunkX() || (z >> 4) != natural.chunkZ()) {
            return fallback;
        }
        return natural.tops()[(x & 15) << 4 | (z & 15)];
    }

    private record Surface(BlockState top, BlockState filler, BlockState cover) {
    }

    /** The dome as the template describes it: the top of its earth per column, the air it keeps open and the door path. */
    /**
     * {@code banks}: columns beside the door path, coded as distance * 1000 + step, where the cut through the hill is given
     * sloping sides instead of walls.
     */
    private record Dome(BoundingBox box, int ground, Map<Long, Integer> heights, Set<Long> keptAir, Set<Long> voids, Map<Long, Integer> path,
                        Map<Long, Integer> banks, int doorFloor) {
        static long column(int x, int z) {
            return ((long) x << 32) | (z & 0xffffffffL);
        }
    }

    public static void apply(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        if (CryptStructure.classic()) {
            return;
        }
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ChunkPos chunkPos = chunk.getPos();
        List<StructureStart> starts = structureManager.startsForStructure(chunkPos, structure -> {
            ResourceLocation id = registry.getKey(structure);
            return id != null && id.equals(CryptPlans.ID);
        });
        if (starts.isEmpty()) {
            return;
        }
        BoundingBox chunkBox = new BoundingBox(chunkPos.getMinBlockX(), chunk.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), chunk.getMaxBuildHeight() - 1, chunkPos.getMaxBlockZ());
        StructureTemplateManager templates = level.getLevel().getStructureManager();
        boolean shaped = false;
        for (StructureStart start : starts) {
            for (StructurePiece piece : start.getPieces()) {
                if (!(piece instanceof PoolElementStructurePiece pool) || !isSurfacePiece(piece)) {
                    continue;
                }
                BoundingBox box = piece.getBoundingBox();
                if (!box.inflatedBy(SPREAD).intersects(chunkBox)) {
                    continue;
                }
                Dome dome = read(pool, templates);
                if (dome != null) {
                    grow(level, dome, chunkBox);
                    shaped = true;
                }
            }
        }
        if (shaped) {
            com.Vivideru.Goety.common.world.structures.StructureTerrain.refreshHeightmaps(chunk);
        }
    }

    /**
     * Where the column scans start. The generator's heightmaps are not updated when a structure piece is placed, so after
     * placement they still show the land as it was: every scan here starts above the piece and the hill instead.
     */
    private static int ceiling(Dome dome) {
        return dome.box().maxY() + SPREAD;
    }

    private static Dome read(PoolElementStructurePiece piece, StructureTemplateManager templates) {
        BoundingBox box = piece.getBoundingBox();
        int ground = box.minY() + GROUND_OFFSET;
        // The hill grows out of the piece's own earth: the dome and the soil disc it stands on. The air outside the dome is
        // exported as structure void, so the box never cuts the land and leaves no mark of its own.
        Map<Long, Integer> heights = new HashMap<>();
        for (Block earth : EARTH) {
            for (BlockPos pos : CryptShell.templateBlocks(piece, templates, earth)) {
                if (pos.getY() >= ground - 2) {
                    heights.merge(Dome.column(pos.getX(), pos.getZ()), pos.getY(), Math::max);
                }
            }
        }
        if (heights.isEmpty()) {
            return null;
        }
        Set<Long> voids = new HashSet<>();
        for (BlockPos pos : CryptShell.templateBlocks(piece, templates, Blocks.STRUCTURE_VOID)) {
            voids.add(pos.asLong());
        }
        // The template's air is kept only where it rests on the vestibule's own blocks (its rooms, the doorway): air over
        // earth or over the land is free, and the hill may rise through it.
        Set<Long> keptAir = new HashSet<>();
        for (BlockPos pos : CryptShell.templateBlocks(piece, templates, Blocks.AIR)) {
            if (pos.getY() >= ground) {
                keptAir.add(pos.asLong());
            }
        }
        // The doorway is the Shade Stone: the path runs from it away from the piece's centre.
        Map<Long, Integer> path = new HashMap<>();
        Map<Long, Integer> banks = new HashMap<>();
        int doorFloor = ground;
        List<BlockPos> door = CryptShell.templateBlocks(piece, templates, com.Polarice3.Goety.common.blocks.ModBlocks.SHADE_STONE_BLOCK.get());
        if (!door.isEmpty()) {
            double doorX = 0.0D;
            double doorZ = 0.0D;
            int lowest = Integer.MAX_VALUE;
            for (BlockPos pos : door) {
                doorX += pos.getX();
                doorZ += pos.getZ();
                lowest = Math.min(lowest, pos.getY());
            }
            doorX /= door.size();
            doorZ /= door.size();
            BlockPos centre = box.getCenter();
            double dx = doorX - centre.getX();
            double dz = doorZ - centre.getZ();
            Direction out = Math.abs(dx) >= Math.abs(dz) ? (dx >= 0.0D ? Direction.EAST : Direction.WEST) : (dz >= 0.0D ? Direction.SOUTH : Direction.NORTH);
            doorFloor = lowest - 1;
            for (BlockPos pos : door) {
                for (int step = 1; step <= DOOR_PATH; step++) {
                    BlockPos open = pos.relative(out, step);
                    path.merge(Dome.column(open.getX(), open.getZ()), step, Math::min);
                }
            }
            Direction side = out.getClockWise();
            for (BlockPos pos : door) {
                for (int step = 1; step <= DOOR_PATH; step++) {
                    for (int distance = 1; distance <= BANK; distance++) {
                        for (int sign = -1; sign <= 1; sign += 2) {
                            BlockPos bank = pos.relative(out, step).relative(side, sign * distance);
                            long column = Dome.column(bank.getX(), bank.getZ());
                            if (!path.containsKey(column)) {
                                banks.merge(column, distance * 1000 + step, Math::min);
                            }
                        }
                    }
                }
            }
        }
        return new Dome(box, ground, heights, keptAir, voids, path, banks, doorFloor);
    }

    private static void grow(WorldGenLevel level, Dome dome, BoundingBox chunkBox) {
        long seed = level.getSeed();
        BoundingBox reach = dome.box().inflatedBy(SPREAD);
        int minX = Math.max(reach.minX(), chunkBox.minX());
        int maxX = Math.min(reach.maxX(), chunkBox.maxX());
        int minZ = Math.max(reach.minZ(), chunkBox.minZ());
        int maxZ = Math.min(reach.maxZ(), chunkBox.maxZ());
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        // Surfaces and targets are settled before anything is built, so a column never samples a neighbour already raised.
        Surface[][] surfaces = new Surface[maxX - minX + 1][maxZ - minZ + 1];
        int[][] solidTops = new int[maxX - minX + 1][maxZ - minZ + 1];
        int[][] targets = new int[maxX - minX + 1][maxZ - minZ + 1];
        Surface prevailing = prevailing(level, dome.box(), ceiling(dome));
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int solid = solidTop(level, x, z, ceiling(dome));
                boolean inside = dome.box().isInside(x, dome.ground(), z);
                // Inside the piece the template's air has cut the land away: the ground as it was is put back under the hill.
                int natural = inside ? naturalTop(x, z, solid) : solid;
                surfaces[x - minX][z - minZ] = sample(level, dome.box(), x, z, ceiling(dome), prevailing);
                solidTops[x - minX][z - minZ] = solid;
                Integer step = dome.path().get(Dome.column(x, z));
                Integer bank = dome.banks().get(Dome.column(x, z));
                int target;
                if (step != null) {
                    target = pathTarget(dome, step, natural);
                } else {
                    target = Math.max(hillTarget(seed, dome, x, z), natural);
                    if (bank != null) {
                        // Beside the path the hill and the land slope down to it, two blocks per block, instead of standing as walls.
                        target = Math.min(target, pathTarget(dome, bank % 1000, natural) + 2 * (bank / 1000));
                    }
                }
                targets[x - minX][z - minZ] = target;
            }
        }
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Surface surface = surfaces[x - minX][z - minZ];
                int solid = solidTops[x - minX][z - minZ];
                int target = targets[x - minX][z - minZ];
                boolean onPath = dome.path().containsKey(Dome.column(x, z));
                if (target > solid) {
                    for (int y = solid + 1; y <= target; y++) {
                        if (dome.keptAir().contains(BlockPos.asLong(x, y, z)) && restsOnCrypt(level, x, y, z, cursor)) {
                            break;
                        }
                        BlockState state = level.getBlockState(cursor.set(x, y, z));
                        if (!state.isAir() && !state.canBeReplaced() && !(state.getBlock() instanceof BushBlock) && !state.is(BlockTags.ICE)) {
                            break;
                        }
                        level.setBlock(cursor, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                } else if ((onPath || dome.banks().containsKey(Dome.column(x, z))) && target < solid) {
                    // The land stands in the way of the door: the path and its banks are dug down to a ramp, trees and all.
                    // Inside the piece only natural ground (the template's structure voids) is dug, never the dome or the vestibule.
                    boolean inside = dome.box().isInside(x, dome.ground(), z);
                    int top = ceiling(dome);
                    for (int y = top; y > target; y--) {
                        BlockState state = level.getBlockState(cursor.set(x, y, z));
                        if (!state.isAir() && (!inside || dome.voids().contains(BlockPos.asLong(x, y, z)))) {
                            level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                    if (EARTH.contains(level.getBlockState(cursor.set(x, target, z)).getBlock()) || level.getBlockState(cursor).is(Blocks.STONE)) {
                        level.setBlock(cursor, surface.top(), Block.UPDATE_CLIENTS);
                    }
                }
                dress(level, dome, surface, x, z, seed, cursor);
            }
        }
    }

    /** Whether the first block under this air belongs to the crypt itself (a floor, a wall): that air is a room, not land. */
    private static boolean restsOnCrypt(WorldGenLevel level, int x, int y, int z, BlockPos.MutableBlockPos cursor) {
        for (int below = y - 1; below > level.getMinBuildHeight(); below--) {
            BlockState state = level.getBlockState(cursor.set(x, below, z));
            if (state.isAir()) {
                continue;
            }
            return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals(com.Polarice3.Goety.Goety.MOD_ID);
        }
        return false;
    }

    /** The height the hill wants here: the dome's own earth spread outward along a warped, bumpy slope. */
    private static int hillTarget(long seed, Dome dome, int x, int z) {
        double warp = noise(seed + 1327L, x, z, 9.0D) * WARP;
        double best = Double.NEGATIVE_INFINITY;
        for (Map.Entry<Long, Integer> entry : dome.heights().entrySet()) {
            int dx = (int) (entry.getKey() >> 32);
            int dz = (int) (long) entry.getKey();
            double distance = Math.sqrt((double) (dx - x) * (dx - x) + (double) (dz - z) * (dz - z));
            if (distance > SPREAD + WARP) {
                continue;
            }
            best = Math.max(best, entry.getValue() - Math.max(0.0D, distance + warp) * SLOPE);
        }
        if (best == Double.NEGATIVE_INFINITY) {
            return Integer.MIN_VALUE;
        }
        best += (noise(seed + 2731L, x, z, 5.0D) * 0.7D + noise(seed + 4099L, x, z, 2.5D) * 0.3D) * BUMPS;
        return (int) Math.round(best);
    }

    /** The path out of the door: level with the sill at first, then eased onto the natural ground. */
    private static int pathTarget(Dome dome, int step, int natural) {
        if (step <= DOOR_LANDING) {
            return dome.doorFloor();
        }
        double t = (step - DOOR_LANDING) / (double) (DOOR_PATH - DOOR_LANDING);
        return (int) Math.round(Mth.lerp(t, dome.doorFloor(), natural));
    }

    /** Dresses every block of earth of this column at or above the ground: exposed faces get the surface, the core the soil. */
    private static void dress(WorldGenLevel level, Dome dome, Surface surface, int x, int z, long seed, BlockPos.MutableBlockPos cursor) {
        int top = -1;
        int ceiling = dome.box().maxY() + SPREAD;
        int floor = dome.ground() - SPREAD;
        for (int y = ceiling; y >= floor; y--) {
            if (EARTH.contains(level.getBlockState(cursor.set(x, y, z)).getBlock())) {
                top = y;
                break;
            }
        }
        if (top < 0) {
            return;
        }
        for (int y = top; y >= floor; y--) {
            BlockState state = level.getBlockState(cursor.set(x, y, z));
            if (!EARTH.contains(state.getBlock())) {
                if (y < dome.ground()) {
                    break;
                }
                continue;
            }
            boolean exposed = y == top;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (exposed) {
                    break;
                }
                BlockState side = level.getBlockState(cursor.set(x + direction.getStepX(), y, z + direction.getStepZ()));
                exposed = side.isAir() || side.canBeReplaced() || side.getBlock() instanceof BushBlock;
            }
            BlockState wanted = exposed ? surface.top() : surface.filler();
            if (!state.equals(wanted)) {
                level.setBlock(cursor.set(x, y, z), wanted, Block.UPDATE_CLIENTS);
            }
        }
        BlockPos above = new BlockPos(x, top + 1, z);
        if (!level.getBlockState(above).isAir()) {
            return;
        }
        float roll = Math.abs((Mth.getSeed(x, top, z) ^ seed) % 1000) / 1000.0F;
        if (surface.cover() != null && surface.cover().canSurvive(level, above) && roll < COVER_CHANCE) {
            level.setBlock(above, surface.cover(), Block.UPDATE_CLIENTS);
        } else if (surface.cover() == null && surface.top().is(Blocks.GRASS_BLOCK) && roll < GRASS_CHANCE) {
            level.setBlock(above, Blocks.SHORT_GRASS.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    /** The highest block of this column that is ground rather than air, water, plant or tree. */
    private static int solidTop(WorldGenLevel level, int x, int z, int ceiling) {
        int y = ceiling;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        while (y > level.getMinBuildHeight()) {
            BlockState state = level.getBlockState(cursor.set(x, y, z));
            if (!state.isAir() && state.getFluidState().isEmpty() && !isCover(state) && !state.is(BlockTags.ICE)) {
                return y;
            }
            y--;
        }
        return y;
    }

    /**
     * The surface of the nearest dry natural column outside the piece: what the hill must look like here. The sides of
     * the piece are tried nearest first, so a river bed or lake on one side never dresses the hill in bare earth.
     */
    /** Outside the piece a column keeps its own surface; over the piece the hill wears the land's prevailing one. */
    private static Surface sample(WorldGenLevel level, BoundingBox box, int x, int z, int ceiling, Surface prevailing) {
        boolean inside = box.minX() <= x && x <= box.maxX() && box.minZ() <= z && z <= box.maxZ();
        if (!inside) {
            Surface own = surfaceAt(level, x, z, ceiling);
            return own != null ? own : prevailing;
        }
        return prevailing;
    }

    /**
     * The surface most of the land around the piece shows, read on a ring a few blocks outside it: one skin for the whole
     * hill, so a beach, a path or a patch of gravel on one side never paints a stripe across it.
     */
    private static Surface prevailing(WorldGenLevel level, BoundingBox box, int ceiling) {
        BoundingBox ring = box.inflatedBy(3);
        Map<Block, Integer> counts = new HashMap<>();
        Map<Block, Surface> examples = new HashMap<>();
        int samples = 0;
        int snowy = 0;
        List<int[]> columns = new java.util.ArrayList<>();
        for (int x = ring.minX(); x <= ring.maxX(); x += 2) {
            columns.add(new int[]{x, ring.minZ()});
            columns.add(new int[]{x, ring.maxZ()});
        }
        for (int z = ring.minZ() + 2; z < ring.maxZ(); z += 2) {
            columns.add(new int[]{ring.minX(), z});
            columns.add(new int[]{ring.maxX(), z});
        }
        for (int[] column : columns) {
            Surface found = surfaceAt(level, column[0], column[1], ceiling);
            if (found == null) {
                continue;
            }
            samples++;
            counts.merge(found.top().getBlock(), 1, Integer::sum);
            examples.putIfAbsent(found.top().getBlock(), found);
            if (found.cover() != null && found.cover().is(Blocks.SNOW)) {
                snowy++;
            }
        }
        Surface best = new Surface(Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState(), null);
        int bestCount = 0;
        for (Map.Entry<Block, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestCount = entry.getValue();
                best = examples.get(entry.getKey());
            }
        }
        BlockState cover = samples > 0 && snowy * 2 >= samples ? Blocks.SNOW.defaultBlockState() : null;
        return new Surface(best.top(), best.filler(), cover);
    }

    /** The surface of a natural column, or null when it lies under water or outside the generation region. */
    private static Surface surfaceAt(WorldGenLevel level, int sx, int sz, int ceiling) {
        if (!level.hasChunk(sx >> 4, sz >> 4)) {
            return null;
        }
        try {
            int y = ceiling;
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            BlockState cover = null;
            while (y > level.getMinBuildHeight()) {
                BlockState state = level.getBlockState(cursor.set(sx, y, sz));
                if (!state.getFluidState().isEmpty() || state.is(BlockTags.ICE) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.ROOTED_DIRT)) {
                    return null;
                }
                if (isCover(state)) {
                    cover = state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) ? null : state;
                    y--;
                    continue;
                }
                if (state.isAir()) {
                    cover = null;
                    y--;
                    continue;
                }
                BlockState below = level.getBlockState(cursor.set(sx, y - 1, sz));
                BlockState filler = below.is(BlockTags.DIRT) || below.is(BlockTags.SAND) || below.is(Blocks.SANDSTONE) || below.is(Blocks.RED_SANDSTONE)
                        || below.is(Blocks.GRAVEL) || below.is(Blocks.CLAY) || below.is(Blocks.MUD) ? below : Blocks.DIRT.defaultBlockState();
                return new Surface(state, filler, cover);
            }
        } catch (RuntimeException ignored) {
            // A column outside the generation region.
        }
        return null;
    }

    private static boolean isCover(BlockState state) {
        return state.getBlock() instanceof BushBlock || state.is(Blocks.SNOW) || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)
                || state.is(Blocks.VINE) || state.is(Blocks.MOSS_CARPET) || state.canBeReplaced() && !state.isAir() && state.getFluidState().isEmpty();
    }

    private static double noise(long seed, int x, int z, double scale) {
        double fx = x / scale;
        double fz = z / scale;
        int x0 = Mth.floor(fx);
        int z0 = Mth.floor(fz);
        double tx = fx - x0;
        double tz = fz - z0;
        tx = tx * tx * (3.0D - 2.0D * tx);
        tz = tz * tz * (3.0D - 2.0D * tz);
        double a = hash(seed, x0, z0);
        double b = hash(seed, x0 + 1, z0);
        double c = hash(seed, x0, z0 + 1);
        double d = hash(seed, x0 + 1, z0 + 1);
        return Mth.lerp(tz, Mth.lerp(tx, a, b), Mth.lerp(tx, c, d)) * 2.0D - 1.0D;
    }

    private static double hash(long seed, int x, int z) {
        long h = seed ^ (x * 0x9E3779B97F4A7C15L) ^ (z * 0xC2B2AE3D27D4EB4FL);
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        h ^= h >>> 31;
        return (h >>> 11) * 0x1.0p-53;
    }
}
