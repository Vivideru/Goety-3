package com.Vivideru.Goety.common.world.structures;

import com.Vivideru.Goety.common.world.VivideruWorldConfig;
import com.Vivideru.Goety.mixin.BlenderAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Builds the land around a Wind Shrine inside the noise generator, through the blending Minecraft uses to merge new terrain
 * into old chunks: chunks near a shrine get a {@link Blender} whose density blending fills the air above the natural ground
 * up to the mountain base given by {@link WindShrineTerrain#foot}. Nothing below the natural ground changes (caves, aquifers
 * and ores stay where the world put them), and the natural land is never lowered.
 */
public final class WindShrineBlend {
    private static final ThreadLocal<int[]> SUPPRESSED = ThreadLocal.withInitial(() -> new int[1]);
    /** Wind Shrine start chunks considered around an area, beyond its own reach. */
    private static final int SEARCH_MARGIN_CHUNKS = 2;
    /**
     * Density per block of the added land, before Minecraft squeezes it: the slope of its smooth blending terrain (factor 10
     * over the 128-block depth scale), four times steeper below the surface as in its own terrain.
     */
    private static final double FILL_GRADIENT = 10.0D / 128.0D;
    /** Above the added land the natural density is left alone past this many blocks. */
    private static final int FILL_TOP_MARGIN = 8;
    private static final double[] NO_FILL = new double[0];
    /** Carvers are kept out of, and air is filled up to, this many blocks above the top of the built land. */
    private static final int LAND_TOP_MARGIN = 2;
    /** Holes filled this close under soil become dirt, deeper ones stone. */
    private static final int SOIL_DEPTH = 3;

    /** Implemented on {@link Blender} by a mixin: the shrines a blender also builds land for. */
    public interface Blended {
        void goety$setShrineBlend(Area area);
    }

    private WindShrineBlend() {
    }

    /**
     * Turns the blending off on this thread until {@link #resume()}: a shrine's own site is judged on the natural land, and
     * so are the plans the blending is computed from and the natural ground it builds on.
     */
    public static void suppress() {
        SUPPRESSED.get()[0]++;
    }

    public static void resume() {
        SUPPRESSED.get()[0]--;
    }

    /** The blender for terrain covering the given block area: {@code original}, or one that also builds the land of the shrines reaching it. */
    public static Blender blender(Blender original, RandomState randomState, int minX, int minZ, int size) {
        Area area = area(randomState, minX, minZ, size);
        if (area == null) {
            return original;
        }
        Blender blender = original == Blender.empty() ? BlenderAccessor.goety$create(new Long2ObjectOpenHashMap<>(), new Long2ObjectOpenHashMap<>()) : original;
        ((Blended) blender).goety$setShrineBlend(area);
        return blender;
    }

    /**
     * Carvers (caves and canyons) run after the noise in the same places as in a world without the shrine; near the old
     * surface they would cut through the land built on top and leave thin floating crusts. While a chunk is carved they
     * are kept out of that land, from the natural ground up; everything below it is carved as usual.
     */
    public static void beginCarving(RandomState randomState, ChunkPos chunkPos) {
        Area area = area(randomState, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), 16);
        CARVING.set(area == null ? null : new LandGrid(area, chunkPos));
    }

    public static void endCarving() {
        CARVING.remove();
    }

    /** Whether a carver must leave this block alone while the current chunk is carved. */
    public static boolean protects(int x, int y, int z) {
        LandGrid grid = CARVING.get();
        return grid != null && grid.protects(x, y, z);
    }

    private static final ThreadLocal<LandGrid> CARVING = new ThreadLocal<>();

    /**
     * Fills the air left inside the land built above the natural ground before the chunk is decorated: Minecraft's noodle
     * caves are cut after the blending, in the same places as without the shrine, and right at the old surface they would
     * leave tunnels under thin crusts of the new land; where the land meets the slope below it, the blending may also
     * leave a thin lip. Only air (or water) above the natural ground and under the built land is filled; nothing below the
     * natural ground changes.
     */
    public static void settle(ChunkAccess chunk, RandomState randomState) {
        ChunkPos chunkPos = chunk.getPos();
        Area area = area(randomState, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), 16);
        if (area == null) {
            return;
        }
        LandGrid grid = new LandGrid(area, chunkPos);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean changed = false;
        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
                double ground = grid.ground(x, z);
                double top = grid.top(x, z);
                if (Double.isNaN(ground) || Double.isNaN(top) || top <= ground + 0.5D) {
                    continue;
                }
                // Between noise cells the built land may reach a little above its top, as far as the density is raised.
                int highest = Math.min(chunk.getMaxBuildHeight() - 1, Mth.floor(top) + FILL_TOP_MARGIN);
                int lowest = Math.max(chunk.getMinBuildHeight(), Mth.floor(ground) + 1);
                int cover = Integer.MIN_VALUE;
                boolean soil = false;
                for (int y = highest; y >= lowest; y--) {
                    BlockState state = chunk.getBlockState(cursor.set(x, y, z));
                    boolean open = state.isAir() || state.getFluidState().is(FluidTags.WATER) && state.getFluidState().isSource();
                    if (!open) {
                        // Only land covers a hole: the leaves of a tree from a chunk decorated earlier are not land.
                        if (isLand(state)) {
                            cover = y;
                            soil = state.is(BlockTags.DIRT);
                        }
                        continue;
                    }
                    if (cover != Integer.MIN_VALUE) {
                        chunk.setBlockState(cursor, soil && cover - y <= SOIL_DEPTH ? Blocks.DIRT.defaultBlockState() : Blocks.STONE.defaultBlockState(), false);
                        changed = true;
                    }
                }
            }
        }
        if (changed) {
            StructureTerrain.refreshHeightmaps(chunk);
        }
    }

    private static boolean isLand(BlockState state) {
        return state.blocksMotion() && !state.ignitedByLava() && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS);
    }

    /** Natural ground and top of the built land at the noise cell corners of a chunk, interpolated in between as the generator does. */
    private static final class LandGrid {
        private static final int CELL = 4;
        private final int minX;
        private final int minZ;
        private final double[][] ground = new double[5][5];
        private final double[][] top = new double[5][5];

        LandGrid(Area area, ChunkPos chunkPos) {
            this.minX = chunkPos.getMinBlockX();
            this.minZ = chunkPos.getMinBlockZ();
            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 4; j++) {
                    double[] column = area.column(this.minX + i * CELL, this.minZ + j * CELL);
                    this.ground[i][j] = column.length == 0 ? Double.NaN : column[0];
                    this.top[i][j] = column.length == 0 ? Double.NaN : column[1];
                }
            }
        }

        boolean protects(int x, int y, int z) {
            double ground = this.ground(x, z);
            double top = this.top(x, z);
            return !Double.isNaN(ground) && !Double.isNaN(top) && top > ground + 0.5D && y >= ground && y <= top + LAND_TOP_MARGIN;
        }

        double ground(int x, int z) {
            return this.at(this.ground, x, z);
        }

        double top(int x, int z) {
            return this.at(this.top, x, z);
        }

        private double at(double[][] values, int x, int z) {
            int i = Math.floorDiv(x - this.minX, CELL);
            int j = Math.floorDiv(z - this.minZ, CELL);
            if (i < 0 || j < 0 || i >= 4 || j >= 4) {
                return Double.NaN;
            }
            return lerp2(values, i, j, (x - this.minX - i * CELL) / (double) CELL, (z - this.minZ - j * CELL) / (double) CELL);
        }

        private static double lerp2(double[][] values, int i, int j, double tx, double tz) {
            double low = values[i][j] + (values[i + 1][j] - values[i][j]) * tx;
            double high = values[i][j + 1] + (values[i + 1][j + 1] - values[i][j + 1]) * tx;
            return low + (high - low) * tz;
        }
    }

    @Nullable
    private static Area area(RandomState randomState, int minX, int minZ, int size) {
        if (SUPPRESSED.get()[0] > 0 || VivideruWorldConfig.classicWindShrine() || onRiverPlanningThread()) {
            return null;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        ServerLevel level = null;
        for (ServerLevel candidate : server.getAllLevels()) {
            if (candidate.getChunkSource().randomState() == randomState) {
                level = candidate;
                break;
            }
        }
        if (level == null) {
            return null;
        }
        ChunkGeneratorStructureState state = level.getChunkSource().getGeneratorState();
        if (!WindShrinePlans.possibleIn(state)) {
            return null;
        }
        Optional<WindShrineStructure> shrine = WindShrinePlans.shrine(server.registryAccess());
        if (shrine.isEmpty()) {
            return null;
        }
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        WindShrinePlans.Resolver resolver = new WindShrinePlans.Resolver(server.registryAccess(), generator, randomState, server.getStructureManager(), level.getSeed(), level);
        int maxX = minX + size - 1;
        int maxZ = minZ + size - 1;
        int searchChunks = Math.floorDiv(shrine.get().maxDistanceFromCenter + shrine.get().slopeRadius + 160 + WindShrineTerrain.FOOT_BLEND_MAX, 16) + SEARCH_MARGIN_CHUNKS;
        ChunkPos around = new ChunkPos(Math.floorDiv(minX + maxX, 32), Math.floorDiv(minZ + maxZ, 32));
        List<WindShrinePlans.Plan> plans = new ArrayList<>();
        for (ChunkPos candidate : WindShrinePlans.candidates(resolver.registryAccess(), resolver.seed(), around, searchChunks, state)) {
            Optional<WindShrinePlans.Plan> plan = WindShrinePlans.plan(resolver, candidate);
            if (plan.isPresent() && plan.get().touches(minX, minZ, maxX, maxZ, WindShrineTerrain.footReach(plan.get()))) {
                plans.add(plan.get());
            }
        }
        return plans.isEmpty() ? null : new Area(plans, generator, level, randomState);
    }

    /**
     * Streams Reflowing plans its rivers on its own threads from the height of the land, and it only accepts a structure, the
     * shrine too, once the rivers around it are planned. Those rivers can't wait for a shrine plan, so they see the natural land.
     */
    private static boolean onRiverPlanningThread() {
        String thread = Thread.currentThread().getName();
        return thread.startsWith("streamsreflowing-") || thread.startsWith("sr-chunkfill");
    }

    /** The shrines that reach some terrain, and how to find the natural ground under it. */
    public static final class Area {
        private final List<WindShrinePlans.Plan> plans;
        private final ChunkGenerator generator;
        private final LevelHeightAccessor heights;
        private final RandomState randomState;
        /** Per column: {natural ground, top of the land to build}, or NO_FILL where no shrine reaches. */
        private final Long2ObjectOpenHashMap<double[]> columns = new Long2ObjectOpenHashMap<>();

        Area(List<WindShrinePlans.Plan> plans, ChunkGenerator generator, LevelHeightAccessor heights, RandomState randomState) {
            this.plans = plans;
            this.generator = generator;
            this.heights = heights;
            this.randomState = randomState;
        }

        /**
         * Minecraft's density at a cell corner, with the land between the natural ground and the mountain base made solid:
         * above the ground the density is raised to at least that of a smooth terrain whose surface is the base, below it
         * the natural density is returned untouched.
         */
        public double fill(int x, int y, int z, double density) {
            double[] column = this.column(x, z);
            if (column.length == 0 || column[1] <= column[0] + 0.5D || y <= column[0] || y > column[1] + FILL_TOP_MARGIN) {
                return density;
            }
            double depth = column[1] - y;
            return Math.max(density, depth * FILL_GRADIENT * (depth > 0.0D ? 4.0D : 1.0D));
        }

        synchronized double[] column(int x, int z) {
            long key = ChunkPos.asLong(x, z);
            double[] column = this.columns.get(key);
            if (column != null) {
                return column;
            }
            column = NO_FILL;
            int ground = Integer.MIN_VALUE;
            double top = Double.NEGATIVE_INFINITY;
            for (WindShrinePlans.Plan plan : this.plans) {
                double[] foot = WindShrineTerrain.foot(plan, x, z);
                if (foot == null) {
                    continue;
                }
                if (ground == Integer.MIN_VALUE) {
                    // The natural ground: the generator's own column without any shrine land.
                    suppress();
                    try {
                        ground = this.generator.getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, this.heights, this.randomState) - 1;
                    } finally {
                        resume();
                    }
                }
                top = Math.max(top, ground + foot[0] * Math.max(0.0D, foot[1] - ground));
            }
            if (ground != Integer.MIN_VALUE) {
                column = new double[]{ground, Math.max(ground, top)};
            }
            this.columns.put(key, column);
            return column;
        }
    }
}
