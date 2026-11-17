package com.Vivideru.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.RedMossBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Optional;

public final class WindShrineTerrain {
    public static final int PLATEAU_MARGIN = 3;
    private static final int PROTECT_MARGIN = 6;
    private static final int PROTECT_FADE = 22;
    private static final int FOREIGN_SEARCH_CHUNKS = 8;
    private static final double STAIR_MARGIN = 1.0D;
    private static final double STAIR_FADE = 2.5D;
    private static final int MOAT_RADIUS = 5;
    private static final int BLEND_DISTANCE = 6;
    private static final int MIN_SLOPE_RADIUS = 22;
    private static final double WILD_BOOST = 2.6D;
    private static final double SLOPE_PER_LIFT = 1.0D;
    private static final double SLOPE_SHAPE_MIN = 0.7D;
    private static final double SLOPE_SHAPE_MAX = 2.6D;
    private static final double EDGE_WARP = 0.3D;
    private static final double RADIUS_WANDER = 0.6D;
    private static final int MAX_SCAN = 96;
    private static final ResourceKey<ConfiguredFeature<?, ?>> WINDSWEPT_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "windswept_tree"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> PINE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "pine_tree"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_PINE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "mega_pine_tree"));
    private static final int SNOW_BAND = 30;
    private static final int PEAKS_BAND = 55;
    private static final int PEAKS_FADE = 12;
    private static final double STEEP_RATE_MIN = 1.3D;
    private static final double STEEP_RATE_MAX = 2.4D;
    private static final double GENTLE_RATE_MIN = 0.3D;
    private static final double GENTLE_RATE_MAX = 0.55D;
    /** Sets how far the edge of the windswept slopes wanders (see {@link #slope}). */
    private static final double FOOT_LENGTH = 55.0D;
    /** How far past the windswept slopes the mountain base takes to merge into the natural land, before its noise variation. */
    private static final double FOOT_BLEND = 150.0D;
    /** The longest merge, so plans know how far the noise generator is affected. */
    public static final int FOOT_BLEND_MAX = (int) Math.ceil(FOOT_BLEND * 1.25D) + 1;
    /** Relief of the mountain base, faded out as it merges into the land. */
    private static final double FOOT_RELIEF = 6.0D;
    /** Columns this far past the windswept slopes are still painted (snow, red moss) but no longer shaped. */
    private static final int FOOT_PAINT = 16;
    private static final double TERRACE_MIN = 6.0D;
    private static final double TERRACE_MAX = 10.0D;
    private static final double TERRACE_STRENGTH = 0.85D;
    private static final int FACE_DEPTH_MAX = 24;
    private static final int STONE_BLEND = 16;
    private static final int SUMMIT_ROCK_DEPTH = 6;
    private static final int TREE_SPACING = 5;
    private static final double SNOW_LINE_WOBBLE = 6.0D;
    private static final double SNOW_LINE_DETAIL = 2.5D;
    public static final ResourceKey<Biome> WINDSWEPT_PEAKS_BIOME = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "windswept_peaks"));

    private record TreeSpot(BlockPos pos, ResourceKey<ConfiguredFeature<?, ?>> feature) {
    }

    private WindShrineTerrain() {
    }

    public static int maxWarp(int slopeRadius) {
        return Mth.ceil(slopeRadius * (1.0D + RADIUS_WANDER) * (1.0D + EDGE_WARP)) - slopeRadius;
    }

    public static void cleanup(WorldGenLevel level, ChunkAccess chunk) {
        cleanup(chunk);
    }

    public static void cleanup(ChunkAccess chunk) {
        if (chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL) || com.Vivideru.Goety.common.world.VivideruWorldConfig.classicWindShrine()) {
            return;
        }
        boolean windswept = false;
        for (LevelChunkSection section : chunk.getSections()) {
            if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container && container.maybeHas(holder -> holder.is(WINDSWEPT_PEAKS_BIOME))) {
                windswept = true;
                break;
            }
        }
        if (!windswept) {
            return;
        }
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = chunkPos.getMinBlockX() + lx;
                int z = chunkPos.getMinBlockZ() + lz;
                int top = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                if (!chunk.getNoiseBiome(x >> 2, top >> 2, z >> 2).is(WINDSWEPT_PEAKS_BIOME)) {
                    continue;
                }
                BlockState state = chunk.getBlockState(cursor.set(x, top, z));
                if (state.is(Blocks.SNOW)) {
                    chunk.setBlockState(cursor, Blocks.AIR.defaultBlockState(), false);
                    top--;
                    state = chunk.getBlockState(cursor.set(x, top, z));
                }
                if (state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.POWDER_SNOW)) {
                    state = ModBlocks.RED_MOSS_DIRT.get().defaultBlockState();
                    chunk.setBlockState(cursor, state, false);
                }
                if (state.hasProperty(BlockStateProperties.SNOWY) && state.getValue(BlockStateProperties.SNOWY)) {
                    chunk.setBlockState(cursor, state.setValue(BlockStateProperties.SNOWY, false), false);
                }
            }
        }
        settleRedMoss(chunk);
    }

    private static void settleRedMoss(ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos coverPos = new BlockPos.MutableBlockPos();
        int maxY = chunk.getMaxBuildHeight() - 1;
        for (int s = 0; s < chunk.getSectionsCount(); s++) {
            LevelChunkSection section = chunk.getSection(s);
            if (section.hasOnlyAir() || !section.maybeHas(state -> state.getBlock() instanceof RedMossBlock)) {
                continue;
            }
            int bottom = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(s));
            for (int ly = 0; ly < 16 && bottom + ly < maxY; ly++) {
                for (int lx = 0; lx < 16; lx++) {
                    for (int lz = 0; lz < 16; lz++) {
                        BlockState state = section.getBlockState(lx, ly, lz);
                        if (!(state.getBlock() instanceof RedMossBlock moss)) {
                            continue;
                        }
                        pos.set(chunkPos.getMinBlockX() + lx, bottom + ly, chunkPos.getMinBlockZ() + lz);
                        coverPos.setWithOffset(pos, Direction.UP);
                        BlockState cover = chunk.getBlockState(coverPos);
                        BlockState result;
                        if (!mossSurvives(chunk, state, pos, cover, coverPos)) {
                            result = moss.convertState;
                        } else if (state.hasProperty(BlockStateProperties.SNOWY)) {
                            result = state.setValue(BlockStateProperties.SNOWY, cover.is(BlockTags.SNOW));
                        } else {
                            continue;
                        }
                        if (result != state) {
                            chunk.setBlockState(pos, result, false);
                        }
                    }
                }
            }
        }
    }

    private static boolean mossSurvives(ChunkAccess chunk, BlockState state, BlockPos pos, BlockState cover, BlockPos coverPos) {
        if (cover.is(Blocks.SNOW) && cover.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        }
        if (cover.getFluidState().getAmount() == 8) {
            return false;
        }
        return LightEngine.getLightBlockInto(chunk, state, pos, cover, coverPos, Direction.UP, cover.getLightBlock(chunk, coverPos)) < 15;
    }

    public static void apply(WorldGenLevel level, ChunkAccess chunk, ChunkGenerator generator, StructureManager structureManager) {
        if (!(level instanceof WorldGenRegion region)) {
            return;
        }
        WindShrineBlend.settle(chunk, region.getLevel().getChunkSource().randomState());
        List<WindShrinePlans.Plan> plans = WindShrinePlans.forChunk(region, generator);
        if (plans.isEmpty()) {
            return;
        }
        List<BoundingBox> foreign = foreignPieces(region, structureManager, chunk.getPos());
        for (WindShrinePlans.Plan plan : plans) {
            shape(region, chunk, generator, plan, foreign);
        }
        StructureTerrain.refreshHeightmaps(chunk);
    }

    private static boolean isForeign(Structure structure) {
        return !(structure instanceof WindShrineStructure) && !(structure instanceof WindShrineExpansionStructure) && structure.step() == GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    private static List<BoundingBox> foreignPieces(WorldGenRegion region, StructureManager structureManager, ChunkPos chunkPos) {
        List<BoundingBox> boxes = new ArrayList<>();
        Set<StructureStart> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int dx = -FOREIGN_SEARCH_CHUNKS; dx <= FOREIGN_SEARCH_CHUNKS; dx++) {
            for (int dz = -FOREIGN_SEARCH_CHUNKS; dz <= FOREIGN_SEARCH_CHUNKS; dz++) {
                if (!region.hasChunk(chunkPos.x + dx, chunkPos.z + dz)) {
                    continue;
                }
                ChunkAccess neighbour;
                try {
                    neighbour = region.getChunk(chunkPos.x + dx, chunkPos.z + dz);
                } catch (RuntimeException ignored) {
                    continue;
                }
                for (Map.Entry<Structure, StructureStart> entry : neighbour.getAllStarts().entrySet()) {
                    if (isForeign(entry.getKey()) && entry.getValue().isValid() && seen.add(entry.getValue())) {
                        collectForeign(entry.getValue(), chunkPos, boxes);
                    }
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                ChunkPos neighbour = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                if (!region.hasChunk(neighbour.x, neighbour.z)) {
                    continue;
                }
                List<StructureStart> starts;
                try {
                    starts = structureManager.startsForStructure(neighbour, WindShrineTerrain::isForeign);
                } catch (RuntimeException ignored) {
                    continue;
                }
                for (StructureStart start : starts) {
                    if (seen.add(start)) {
                        collectForeign(start, chunkPos, boxes);
                    }
                }
            }
        }
        return boxes;
    }

    private static void collectForeign(StructureStart start, ChunkPos chunkPos, List<BoundingBox> boxes) {
        int reach = PROTECT_MARGIN + PROTECT_FADE + 2;
        for (StructurePiece piece : start.getPieces()) {
            BoundingBox box = piece.getBoundingBox();
            if (box.maxX() >= chunkPos.getMinBlockX() - reach && box.minX() <= chunkPos.getMaxBlockX() + reach
                    && box.maxZ() >= chunkPos.getMinBlockZ() - reach && box.minZ() <= chunkPos.getMaxBlockZ() + reach) {
                boxes.add(box);
            }
        }
    }

    private static double protection(List<BoundingBox> foreign, int x, int z) {
        double weight = 1.0D;
        for (BoundingBox box : foreign) {
            int dx = x < box.minX() ? box.minX() - x : x > box.maxX() ? x - box.maxX() : 0;
            int dz = z < box.minZ() ? box.minZ() - z : z > box.maxZ() ? z - box.maxZ() : 0;
            double distance = Math.sqrt(dx * dx + dz * dz);
            double t = Mth.clamp((distance - PROTECT_MARGIN) / PROTECT_FADE, 0.0D, 1.0D);
            weight = Math.min(weight, t * t * (3.0D - 2.0D * t));
        }
        return weight;
    }

    private static final int NONE = Integer.MIN_VALUE;
    private static final double BANK_SLOPE = 0.7D;
    private static final int BANK_FLAT = 2;
    private static final int BANK_REACH = 32;

    /**
     * The mountain profile at one column, worked out from the shrine's footprints alone, so the noise generator (the base of
     * the mountain) and the terrain pass (its slopes) agree on it. {@code d} is the distance past the plateau, warped so the
     * edge wanders; the steep summit slopes take {@code steepLength} of it, then the windswept slopes {@code windsweptLength}.
     */
    record Slope(WindShrinePlans.Footprint nearestFootprint, double nearest, int floorY, int cap, double steepRate, double gentleRate,
                 double steepDrop, double steepLength, double windsweptTop, double windsweptLength, double beyond, double d) {
        double footStart() {
            return this.steepLength + this.windsweptLength;
        }
    }

    /** The profile at a column, or null when the column is farther than {@code limit} from every footprint. */
    @org.jetbrains.annotations.Nullable
    static Slope slope(WindShrinePlans.Plan plan, int x, int z, double[] distances, int limit) {
        List<WindShrinePlans.Footprint> footprints = plan.footprints();
        double nearest = Double.MAX_VALUE;
        WindShrinePlans.Footprint nearestFootprint = null;
        for (int i = 0; i < footprints.size(); i++) {
            distances[i] = footprints.get(i).distanceTo(x, z);
            if (distances[i] < nearest) {
                nearest = distances[i];
                nearestFootprint = footprints.get(i);
            }
        }
        if (nearest > limit || nearestFootprint == null) {
            return null;
        }
        double weightSum = 0.0D;
        double floorSum = 0.0D;
        int cap = Integer.MAX_VALUE;
        double margin = nearestFootprint.stairs ? STAIR_MARGIN : PLATEAU_MARGIN;
        boolean apron = nearest <= margin;
        for (int i = 0; i < footprints.size(); i++) {
            double extra = distances[i] - nearest;
            WindShrinePlans.Footprint footprint = footprints.get(i);
            if (apron) {
                if (extra <= 0.0D) {
                    weightSum = 1.0D;
                    floorSum = footprint.floorAt(x, z);
                }
                continue;
            }
            if (extra <= BLEND_DISTANCE) {
                double w = 1.0D - extra / (BLEND_DISTANCE + 0.5D);
                w *= w;
                weightSum += w;
                floorSum += w * footprint.floorAt(x, z);
            }
            if (distances[i] <= BANK_REACH && extra <= BLEND_DISTANCE + BANK_FLAT) {
                cap = Math.min(cap, footprint.floorAt(x, z) + (int) Math.floor(Math.max(0.0D, distances[i] - BANK_FLAT) * BANK_SLOPE));
            }
        }
        int floorY = (int) Math.round(floorSum / weightSum);
        long seed = plan.seed();
        int snowLine = plan.summitY() - SNOW_BAND;
        int peaksFoot = snowLine - PEAKS_BAND;
        double steepRate = STEEP_RATE_MIN + (STEEP_RATE_MAX - STEEP_RATE_MIN) * unit(perlin(seed ^ 0x3A9D51C7E2B86F04L, x * 0.02D, z * 0.02D));
        double gentleRate = GENTLE_RATE_MIN + (GENTLE_RATE_MAX - GENTLE_RATE_MIN) * unit(perlin(seed ^ 0x5F1A3C9D2B7E6481L, x * 0.014D, z * 0.014D));
        double footLength = FOOT_LENGTH * (0.7D + 0.6D * unit(perlin(seed ^ 0x1C9E5A3F7D2B8460L, x * 0.02D, z * 0.02D)));
        double steepDrop = Math.max(0.0D, floorY - snowLine);
        double steepLength = steepDrop / steepRate;
        double windsweptTop = Math.min(floorY, snowLine);
        double windsweptLength = Math.max(0.0D, windsweptTop - peaksFoot) / gentleRate;
        double total = steepLength + windsweptLength + footLength;
        double beyond = nearest - margin;
        double warp = beyond <= 0.0D ? 0.0D : boundaryOffset(seed, x, z) * total * EDGE_WARP * Math.min(1.0D, beyond / 20.0D);
        double d = Math.max(0.0D, beyond - warp);
        return new Slope(nearestFootprint, nearest, floorY, cap, steepRate, gentleRate, steepDrop, steepLength, windsweptTop, windsweptLength, beyond, d);
    }

    /** How far from its footprints a shrine still builds land in the noise generator. */
    public static int footReach(WindShrinePlans.Plan plan) {
        return plan.reach() + FOOT_BLEND_MAX;
    }

    /**
     * The base of the mountain at a column, as the noise generator builds it on the natural ground: {@code {weight, height}},
     * or null where the shrine leaves the land alone. The land rises by weight x (height - ground) where the base is higher
     * than the ground. Under the steep and windswept slopes it is filled up to a flat base at the foot of the windswept slopes
     * (weight 1), which the terrain pass then shapes; past them the base keeps sinking at the windswept slope while its weight
     * fades smoothly to nothing, so the natural hills come back little by little. The distance is the true distance from the
     * edge of the windswept slopes, so the foot is as wide wherever that edge wanders.
     */
    @org.jetbrains.annotations.Nullable
    public static double[] foot(WindShrinePlans.Plan plan, int x, int z) {
        double dd = FootField.of(plan).distance(x, z);
        if (Double.isNaN(dd)) {
            return null;
        }
        long seed = plan.seed();
        int peaksFoot = plan.summitY() - SNOW_BAND - PEAKS_BAND;
        if (dd <= 0.0D) {
            return new double[]{1.0D, peaksFoot};
        }
        double length = FOOT_BLEND * (0.75D + 0.5D * unit(perlin(seed ^ 0x4A7C2E9B1D5F3068L, x * 0.01D, z * 0.01D)));
        if (dd >= length) {
            return null;
        }
        double t = dd / length;
        double weight = 1.0D - t * t * (3.0D - 2.0D * t);
        double gentleRate = GENTLE_RATE_MIN + (GENTLE_RATE_MAX - GENTLE_RATE_MIN) * unit(perlin(seed ^ 0x5F1A3C9D2B7E6481L, x * 0.014D, z * 0.014D));
        double relief = (perlin(seed ^ 0x58E1B3D97A2C4F06L, x * 0.025D, z * 0.025D) + 0.4D * perlin(seed ^ 0x2C7E5A1F9B3D6084L, x * 0.07D, z * 0.07D))
                * FOOT_RELIEF * Math.min(1.0D, dd / 16.0D) * (1.0D - t);
        return new double[]{weight, peaksFoot - gentleRate * dd + relief};
    }

    /**
     * Distance from the edge of a shrine's windswept slopes (0 on and under them), sampled once per shrine on a grid and
     * computed with an exact Euclidean distance transform, since the warp that makes the edge wander is no distance itself.
     */
    private static final class FootField {
        private static final int STEP = 4;
        private static final Map<WindShrinePlans.Plan, FootField> FIELDS = new com.google.common.collect.MapMaker().weakKeys().makeMap();
        private final int minX;
        private final int minZ;
        private final int width;
        private final int depth;
        private final float[] distance;

        static FootField of(WindShrinePlans.Plan plan) {
            FootField field = FIELDS.get(plan);
            if (field == null) {
                synchronized (plan) {
                    field = FIELDS.get(plan);
                    if (field == null) {
                        field = new FootField(plan);
                        FIELDS.put(plan, field);
                    }
                }
            }
            return field;
        }

        private FootField(WindShrinePlans.Plan plan) {
            int margin = footReach(plan) + 2 * STEP;
            BoundingBox hull = plan.hull();
            this.minX = Math.floorDiv(hull.minX() - margin, STEP) * STEP;
            this.minZ = Math.floorDiv(hull.minZ() - margin, STEP) * STEP;
            this.width = (hull.maxX() + margin - this.minX) / STEP + 1;
            this.depth = (hull.maxZ() + margin - this.minZ) / STEP + 1;
            double[] squared = new double[this.width * this.depth];
            double[] distances = new double[plan.footprints().size()];
            int reach = plan.reach();
            for (int gx = 0; gx < this.width; gx++) {
                for (int gz = 0; gz < this.depth; gz++) {
                    Slope slope = slope(plan, this.minX + gx * STEP, this.minZ + gz * STEP, distances, reach);
                    boolean under = slope != null && (slope.beyond() <= 0.0D || slope.d() <= slope.footStart());
                    squared[gx * this.depth + gz] = under ? 0.0D : Double.POSITIVE_INFINITY;
                }
            }
            transform(squared, this.width, this.depth);
            this.distance = new float[squared.length];
            for (int i = 0; i < squared.length; i++) {
                this.distance[i] = (float) (Math.sqrt(squared[i]) * STEP);
            }
        }

        /** In blocks, interpolated between the grid points; NaN outside the grid. */
        double distance(int x, int z) {
            double fx = (x - this.minX) / (double) STEP;
            double fz = (z - this.minZ) / (double) STEP;
            int gx = Mth.floor(fx);
            int gz = Mth.floor(fz);
            if (gx < 0 || gz < 0 || gx + 1 >= this.width || gz + 1 >= this.depth) {
                return Double.NaN;
            }
            double tx = fx - gx;
            double tz = fz - gz;
            double d00 = this.distance[gx * this.depth + gz];
            double d10 = this.distance[(gx + 1) * this.depth + gz];
            double d01 = this.distance[gx * this.depth + gz + 1];
            double d11 = this.distance[(gx + 1) * this.depth + gz + 1];
            if (Double.isInfinite(d00) || Double.isInfinite(d10) || Double.isInfinite(d01) || Double.isInfinite(d11)) {
                return Double.NaN;
            }
            return Mth.lerp(tz, Mth.lerp(tx, d00, d10), Mth.lerp(tx, d01, d11));
        }

        /** Squared Euclidean distance transform in place (Felzenszwalb and Huttenlocher), first along z, then along x. */
        private static void transform(double[] grid, int width, int depth) {
            double[] line = new double[Math.max(width, depth)];
            double[] out = new double[line.length];
            int[] hulls = new int[line.length];
            double[] bounds = new double[line.length + 1];
            for (int gx = 0; gx < width; gx++) {
                for (int gz = 0; gz < depth; gz++) {
                    line[gz] = grid[gx * depth + gz];
                }
                transform1d(line, depth, out, hulls, bounds);
                for (int gz = 0; gz < depth; gz++) {
                    grid[gx * depth + gz] = out[gz];
                }
            }
            for (int gz = 0; gz < depth; gz++) {
                for (int gx = 0; gx < width; gx++) {
                    line[gx] = grid[gx * depth + gz];
                }
                transform1d(line, width, out, hulls, bounds);
                for (int gx = 0; gx < width; gx++) {
                    grid[gx * depth + gz] = out[gx];
                }
            }
        }

        private static void transform1d(double[] f, int n, double[] out, int[] hulls, double[] bounds) {
            int k = -1;
            for (int q = 0; q < n; q++) {
                if (Double.isInfinite(f[q])) {
                    continue;
                }
                while (k >= 0) {
                    int p = hulls[k];
                    double s = ((f[q] + q * (double) q) - (f[p] + p * (double) p)) / (2.0D * (q - p));
                    if (s <= bounds[k]) {
                        k--;
                    } else {
                        break;
                    }
                }
                k++;
                hulls[k] = q;
                bounds[k] = k == 0 ? Double.NEGATIVE_INFINITY : ((f[q] + q * (double) q) - (f[hulls[k - 1]] + hulls[k - 1] * (double) hulls[k - 1])) / (2.0D * (q - hulls[k - 1]));
                bounds[k + 1] = Double.POSITIVE_INFINITY;
            }
            if (k < 0) {
                java.util.Arrays.fill(out, 0, n, Double.POSITIVE_INFINITY);
                return;
            }
            int j = 0;
            for (int q = 0; q < n; q++) {
                while (bounds[j + 1] < q) {
                    j++;
                }
                double dq = q - hulls[j];
                out[q] = dq * dq + f[hulls[j]];
            }
        }
    }

    private static final class Column {
        boolean active;
        double nearest;
        int naturalY;
        int surfaceY;
        int waterTop = NONE;
        int cap = Integer.MAX_VALUE;
        int target;
        double wild;
        boolean terraced;
        boolean steep;
        double protect = 1.0D;
    }

    private static void shape(WorldGenRegion region, ChunkAccess chunk, ChunkGenerator generator, WindShrinePlans.Plan plan, List<BoundingBox> foreign) {
        ChunkPos chunkPos = chunk.getPos();
        int minBuildY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight() - 1;
        int reach = plan.reach();
        int snowLine = plan.summitY() - SNOW_BAND;
        int peaksFoot = snowLine - PEAKS_BAND;
        long seed = plan.seed();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        List<WindShrinePlans.Footprint> footprints = plan.footprints();
        double[] distances = new double[footprints.size()];
        Column[][] grid = new Column[20][20];
        int originX = chunkPos.getMinBlockX() - 2;
        int originZ = chunkPos.getMinBlockZ() - 2;
        WaterProfile water = WaterProfile.sample(region, chunk, chunkPos, minBuildY);

        for (int gx = 0; gx < 20; gx++) {
            for (int gz = 0; gz < 20; gz++) {
                Column column = new Column();
                grid[gx][gz] = column;
                int x = originX + gx;
                int z = originZ + gz;
                boolean inside = gx >= 2 && gx <= 17 && gz >= 2 && gz <= 17;

                Slope slope = slope(plan, x, z, distances, reach);
                if (slope == null) {
                    continue;
                }
                WindShrinePlans.Footprint nearestFootprint = slope.nearestFootprint();
                double nearest = slope.nearest();
                int cap = slope.cap();
                int floorY = slope.floorY();

                int sx = inside ? x : Mth.clamp(x, chunkPos.getMinBlockX(), chunkPos.getMaxBlockX());
                int sz = inside ? z : Mth.clamp(z, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ());
                int surfaceY = Mth.clamp(heightAt(region, chunk, true, sx, sz) - 1, minBuildY, maxY);
                int naturalY = findNaturalTerrainY(region, chunk, true, sx, sz, surfaceY, minBuildY, cursor);
                int lift = floorY - naturalY;
                if (lift == 0 && nearest > PLATEAU_MARGIN) {
                    continue;
                }

                double steepRate = slope.steepRate();
                double gentleRate = slope.gentleRate();
                double steepDrop = slope.steepDrop();
                double steepLength = slope.steepLength();
                double windsweptTop = slope.windsweptTop();
                double windsweptLength = slope.windsweptLength();
                double beyond = slope.beyond();
                double d = slope.d();
                // Past the windswept slopes the noise generator has already built the foot of the mountain (WindShrineBlend):
                // the columns just beyond are only painted, the rest is left alone.
                if (beyond > 0.0D && d >= slope.footStart() + FOOT_PAINT) {
                    continue;
                }
                boolean pastProfile = beyond > 0.0D && d > slope.footStart();

                int targetY;
                double columnWild = 0.0D;
                boolean terraced = false;
                if (beyond <= 0.0D) {
                    targetY = floorY;
                } else if (pastProfile) {
                    targetY = naturalY;
                } else {
                    double wild = wildness(seed, x, z) * Math.min(1.0D, beyond / 10.0D);
                    columnWild = wild;
                    double boost = 1.0D + (WILD_BOOST - 1.0D) * wild;
                    double fade = Math.min(1.0D, beyond / (nearestFootprint != null && nearestFootprint.stairs ? STAIR_FADE : 8.0D));
                    double h;
                    if (d <= steepLength) {
                        h = floorY - steepRate * d;
                        double cragAmplitude = (3.0D + 5.0D * Math.min(1.0D, steepDrop / 45.0D)) * boost * fade;
                        h += (ridged(seed, x, z) + 0.6D * ridged(seed ^ 0x2E7B9C4D1F6A8350L, x * 2, z * 2)) * cragAmplitude;
                        h += perlin(seed ^ 0x7B3E9A1C5D2F8046L, x * 0.15D, z * 0.15D) * 2.0D * boost * fade;
                    } else {
                        double dd = d - steepLength;
                        h = windsweptTop - gentleRate * dd;
                        double step = TERRACE_MIN + (TERRACE_MAX - TERRACE_MIN) * unit(perlin(seed ^ 0x6E2A9C4F1B8D3075L, x * 0.012D, z * 0.012D));
                        double stepWarp = perlin(seed ^ 0x1D4B7A9E3C6F5820L, x * 0.06D, z * 0.06D) * step * 0.45D;
                        double shelf = Math.floor((h + stepWarp) / step) * step;
                        h = Mth.lerp(TERRACE_STRENGTH * Math.min(1.0D, dd / 12.0D), h, shelf);
                        h += relief(seed, x, z) * 1.2D * boost;
                        terraced = true;
                    }
                    targetY = (int) Math.round(h);
                    if (naturalY > targetY && d > steepLength && (!terraced || naturalY - targetY > 8)) {
                        targetY = naturalY;
                    }
                }
                column.waterTop = water.surface(x, z);
                if (targetY < naturalY && column.waterTop == NONE) {
                    int bank = water.bankFloor(x, z);
                    if (bank != NONE) {
                        targetY = Math.max(targetY, Math.min(naturalY, bank));
                    }
                }
                column.active = true;
                column.nearest = nearest;
                column.naturalY = naturalY;
                column.surfaceY = surfaceY;
                column.cap = cap;
                column.target = Math.min(targetY, cap);
                column.wild = columnWild;
                column.terraced = terraced;
                column.steep = beyond > 0.0D && !pastProfile && d <= steepLength;
            }
        }

        if (!foreign.isEmpty()) {
            for (int gx = 0; gx < 20; gx++) {
                for (int gz = 0; gz < 20; gz++) {
                    Column column = grid[gx][gz];
                    if (!column.active) {
                        continue;
                    }
                    double weight = protection(foreign, originX + gx, originZ + gz);
                    column.protect = weight;
                    if (weight <= 0.0D) {
                        column.active = false;
                        continue;
                    }
                    column.target = (int) Math.round(Mth.lerp(weight, column.naturalY, column.target));
                    column.cap = weight < 1.0D ? Math.max(column.cap, column.target) : column.cap;
                }
            }
        }
        rebiome(region, chunk, grid, seed, snowLine, peaksFoot);

        int[][] smoothed = new int[16][16];
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                Column column = grid[lx + 2][lz + 2];
                if (!column.active) {
                    continue;
                }
                if (column.nearest <= PLATEAU_MARGIN || column.wild > 0.55D && column.steep) {
                    smoothed[lx][lz] = column.target;
                    continue;
                }
                int radius = column.terraced ? 2 : 1;
                double sum = 0.0D;
                double weights = 0.0D;
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        Column other = grid[lx + 2 + dx][lz + 2 + dz];
                        double w = column.terraced ? (dx == 0 && dz == 0 ? 3.0D : 1.0D) : dx == 0 && dz == 0 ? 8.0D : dx == 0 || dz == 0 ? 2.0D : 1.0D;
                        if (other.active) {
                            sum += w * other.target;
                        } else {
                            sum += w * column.target;
                        }
                        weights += w;
                    }
                }
                smoothed[lx][lz] = Math.min((int) Math.round(sum / weights), column.cap);
            }
        }

        int seaLevel = generator.getSeaLevel();
        List<TreeSpot> treeSpots = new ArrayList<>();
        int[][] finalTop = new int[16][16];
        for (int[] row : finalTop) {
            java.util.Arrays.fill(row, Integer.MIN_VALUE);
        }
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                Column column = grid[lx + 2][lz + 2];
                if (!column.active) {
                    continue;
                }
                int x = chunkPos.getMinBlockX() + lx;
                int z = chunkPos.getMinBlockZ() + lz;
                int targetY = Mth.clamp(smoothed[lx][lz], minBuildY + 1, maxY - 1);
                int naturalY = column.naturalY;
                finalTop[lx][lz] = Integer.MIN_VALUE;
                if (targetY < naturalY) {
                    if (column.waterTop != NONE) {
                        continue;
                    }
                    targetY = Math.max(targetY, Math.min(naturalY, seaLevel + 1));
                    int bank = water.bankFloor(x, z);
                    if (bank != NONE) {
                        targetY = Math.max(targetY, Math.min(naturalY, bank));
                    }
                }
                if (targetY > naturalY) {
                    raiseColumn(chunk, x, z, naturalY, targetY, column.surfaceY, minBuildY, cursor);
                } else if (targetY < naturalY) {
                    cutColumn(chunk, x, z, naturalY, targetY, column.surfaceY, cursor);
                    int flood = water.floodLevel(x, z);
                    for (int y = targetY + 1; y <= Math.min(flood, maxY); y++) {
                        if (chunk.getBlockState(cursor.set(x, y, z)).isAir()) {
                            chunk.setBlockState(cursor, Blocks.WATER.defaultBlockState(), false);
                        }
                    }
                }
                finalTop[lx][lz] = targetY;
                boolean beyondApron = column.nearest > PLATEAU_MARGIN + 2 && column.protect >= 1.0D;
                BlockState top = chunk.getBlockState(cursor.set(x, targetY, z));
                int localSnowLine = snowLineAt(seed, x, z, snowLine);
                double peaks = peaksShare(seed, x, z, targetY, localSnowLine, peaksFoot);
                boolean snowBand = targetY >= localSnowLine;
                if (peaks <= 0.0D && !snowBand) {
                    continue;
                }
                if (beyondApron && (snowBand || peaks <= 0.0D) && isRocky(seed, x, z) && (top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.SNOW_BLOCK) || top.is(Blocks.DIRT) || top.is(Blocks.PODZOL))) {
                    BlockState outcrop = mountainStone(seed, x, targetY, z, snowLine, peaksFoot);
                    chunk.setBlockState(cursor, outcrop != null ? outcrop : Blocks.STONE.defaultBlockState(), false);
                    continue;
                }
                top = windsweptSurface(chunk, seed, x, targetY, z, top, peaks, snowBand, cursor);
                if (column.nearest > PLATEAU_MARGIN + MOAT_RADIUS && isTreeSpot(seed, x, z, peaks > 0.5D && !snowBand) && isTreeGround(top)) {
                    ResourceKey<ConfiguredFeature<?, ?>> feature = treeFor(seed, x, z, peaks, snowBand);
                    if (feature != null) {
                        if (top.is(Blocks.SNOW_BLOCK) || top.is(Blocks.POWDER_SNOW)) {
                            chunk.setBlockState(cursor.set(x, targetY, z), ModBlocks.SNOWY_DARK_DIRT.get().defaultBlockState(), false);
                        }
                        treeSpots.add(new TreeSpot(new BlockPos(x, targetY + 1, z), feature));
                        continue;
                    }
                }
                if (beyondApron && targetY + 1 <= maxY && chunk.getBlockState(cursor.set(x, targetY + 1, z)).isAir()) {
                    groundCover(chunk, seed, x, targetY + 1, z, top, peaks, cursor);
                }
            }
        }
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int top = finalTop[lx][lz];
                if (top == Integer.MIN_VALUE || top < snowLine - STONE_BLEND / 2) {
                    continue;
                }
                int x = chunkPos.getMinBlockX() + lx;
                int z = chunkPos.getMinBlockZ() + lz;
                for (int y = top; y >= top - SUMMIT_ROCK_DEPTH; y--) {
                    BlockState state = chunk.getBlockState(cursor.set(x, y, z));
                    if (state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Blocks.GRAVEL)) {
                        BlockState rock = mountainStone(seed, x, y, z, snowLine, peaksFoot, y == top);
                        if (rock != null) {
                            chunk.setBlockState(cursor, rock, false);
                        }
                    }
                }
            }
        }
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int top = finalTop[lx][lz];
                if (top == Integer.MIN_VALUE || grid[lx + 2][lz + 2].nearest <= PLATEAU_MARGIN + 1 && top < snowLine || top < peaksFoot - STONE_BLEND / 2) {
                    continue;
                }
                int lowest = top;
                for (int dir = 0; dir < 4; dir++) {
                    int nx = lx + (dir == 0 ? 1 : dir == 1 ? -1 : 0);
                    int nz = lz + (dir == 2 ? 1 : dir == 3 ? -1 : 0);
                    if (nx >= 0 && nx <= 15 && nz >= 0 && nz <= 15 && finalTop[nx][nz] != Integer.MIN_VALUE) {
                        lowest = Math.min(lowest, finalTop[nx][nz]);
                    }
                }
                int depth = Math.min(FACE_DEPTH_MAX, top - lowest);
                if (depth < 2) {
                    continue;
                }
                int x = chunkPos.getMinBlockX() + lx;
                int z = chunkPos.getMinBlockZ() + lz;
                for (int y = top - 1; y >= Math.max(top - depth, peaksFoot - STONE_BLEND / 2); y--) {
                    BlockState state = chunk.getBlockState(cursor.set(x, y, z));
                    if (state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.DIRT) || state.is(Blocks.GRAVEL)) {
                        BlockState rock = mountainStone(seed, x, y, z, snowLine, peaksFoot);
                        if (rock != null) {
                            chunk.setBlockState(cursor, rock, false);
                        }
                    }
                }
            }
        }
        plantTrees(region, generator, seed, treeSpots);
    }

    private static int heightAt(WorldGenRegion region, ChunkAccess chunk, boolean inside, int x, int z) {
        return heightOf(inside ? chunk : region.getChunk(x >> 4, z >> 4), x, z, Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.WORLD_SURFACE);
    }

    private static int heightOf(ChunkAccess chunk, int x, int z, Heightmap.Types worldGen, Heightmap.Types finished) {
        Heightmap.Types type = chunk.hasPrimedHeightmap(worldGen) ? worldGen : chunk.hasPrimedHeightmap(finished) ? finished : worldGen;
        return chunk.getHeight(type, x, z);
    }

    private static BlockState stateAt(WorldGenRegion region, ChunkAccess chunk, boolean inside, BlockPos pos) {
        return inside ? chunk.getBlockState(pos) : region.getBlockState(pos);
    }

    private static final class WaterProfile {
        static final int MARGIN = 12;
        static final int BANK = 6;
        static final int FLOOD = 5;
        private final int originX;
        private final int originZ;
        private final int size;
        private final int[] surface;
        private final List<int[]> water = new ArrayList<>();

        private WaterProfile(int originX, int originZ, int size) {
            this.originX = originX;
            this.originZ = originZ;
            this.size = size;
            this.surface = new int[size * size];
        }

        static WaterProfile sample(WorldGenRegion region, ChunkAccess chunk, ChunkPos chunkPos, int minBuildY) {
            int size = 16 + 2 * MARGIN;
            WaterProfile profile = new WaterProfile(chunkPos.getMinBlockX() - MARGIN, chunkPos.getMinBlockZ() - MARGIN, size);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = profile.originX; x < profile.originX + size; x++) {
                for (int z = profile.originZ; z < profile.originZ + size; z++) {
                    boolean inside = x >= chunkPos.getMinBlockX() && x <= chunkPos.getMaxBlockX() && z >= chunkPos.getMinBlockZ() && z <= chunkPos.getMaxBlockZ();
                    int index = profile.index(x, z);
                    profile.surface[index] = NONE;
                    int top = heightAt(region, chunk, inside, x, z) - 1;
                    int floor = Math.max(minBuildY, heightOf(inside ? chunk : region.getChunk(x >> 4, z >> 4), x, z, Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.OCEAN_FLOOR) - 1);
                    for (int y = top; y >= floor; y--) {
                        BlockState state = stateAt(region, chunk, inside, pos.set(x, y, z));
                        if (state.getFluidState().is(net.minecraft.tags.FluidTags.WATER) || state.is(BlockTags.ICE)) {
                            profile.surface[index] = y;
                            profile.water.add(new int[]{x, z, y});
                            break;
                        }
                    }
                }
            }
            return profile;
        }

        private int index(int x, int z) {
            return (x - this.originX) * this.size + (z - this.originZ);
        }

        private boolean covers(int x, int z) {
            return x >= this.originX && x < this.originX + this.size && z >= this.originZ && z < this.originZ + this.size;
        }

        int surface(int x, int z) {
            return this.covers(x, z) ? this.surface[this.index(x, z)] : NONE;
        }

        int bankFloor(int x, int z) {
            int floor = NONE;
            for (int[] column : this.water) {
                double d = Math.hypot(column[0] - x, column[1] - z);
                floor = Math.max(floor, column[2] - (int) Math.floor(Math.max(0.0D, d - BANK)));
            }
            return floor;
        }

        int floodLevel(int x, int z) {
            int level = NONE;
            for (int[] column : this.water) {
                if (Math.hypot(column[0] - x, column[1] - z) <= FLOOD) {
                    level = Math.max(level, column[2]);
                }
            }
            return level;
        }
    }


    private static boolean isRocky(long seed, int x, int z) {
        return perlin(seed ^ 0x2D7A4F9C1B6E8350L, x * 0.06D, z * 0.06D) + ridged(seed, x, z) * 0.25D > 0.42D;
    }

    private static void groundCover(ChunkAccess chunk, long seed, int x, int y, int z, BlockState top, double peaks, BlockPos.MutableBlockPos cursor) {
        int roll = Math.floorMod(hash(seed ^ 0x7F1B3D9A5C2E4068L, x, z), 240);
        BlockState cover = null;
        boolean windsweptGround = top.is(ModBlocks.DARK_DIRT.get()) || top.is(ModBlocks.COBBLED_DARK_DIRT.get()) || top.is(ModBlocks.RED_MOSS_BLOCK.get()) || top.is(ModBlocks.RED_MOSS_DIRT.get());
        if (windsweptGround || (top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.PODZOL)) && roll % 7 < peaks * 7) {
            cover = roll < 50 ? ModBlocks.SIENNA_GRASS.get().defaultBlockState() : roll < 62 ? ModBlocks.SIENNA_FERN.get().defaultBlockState() : roll < 66 ? ModBlocks.WINDSWEPT_DEAD_BUSH.get().defaultBlockState() : null;
        } else if (top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.PODZOL)) {
            cover = roll < 56 ? Blocks.SHORT_GRASS.defaultBlockState() : roll < 72 ? Blocks.FERN.defaultBlockState() : roll == 72 ? Blocks.DANDELION.defaultBlockState() : roll == 73 ? Blocks.POPPY.defaultBlockState() : roll == 74 ? Blocks.AZURE_BLUET.defaultBlockState() : null;
        } else if (top.is(Blocks.SNOW_BLOCK) && roll < 48) {
            cover = Blocks.SNOW.defaultBlockState();
        }
        if (cover != null) {
            chunk.setBlockState(cursor.set(x, y, z), cover, false);
        }
    }

    private static boolean isTreeGround(BlockState top) {
        return top.is(ModBlocks.RED_MOSS_DIRT.get()) || top.is(ModBlocks.DARK_DIRT.get()) || top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.PODZOL) || top.is(Blocks.DIRT) || top.is(Blocks.COARSE_DIRT) || top.is(Blocks.SNOW_BLOCK) || top.is(Blocks.POWDER_SNOW) || top.is(Blocks.MOSS_BLOCK);
    }

    private static boolean isTreeSpot(long seed, int x, int z) {
        return isTreeSpot(seed, x, z, false);
    }

    private static boolean isTreeSpot(long seed, int x, int z, boolean windswept) {
        if (!isRawTreeSpot(seed, x, z, windswept)) {
            return false;
        }
        int rank = spotRank(seed, x, z);
        for (int dx = -TREE_SPACING; dx <= TREE_SPACING; dx++) {
            for (int dz = -TREE_SPACING; dz <= TREE_SPACING; dz++) {
                if ((dx != 0 || dz != 0) && isRawTreeSpot(seed, x + dx, z + dz, windswept) && spotRank(seed, x + dx, z + dz) > rank) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isRawTreeSpot(long seed, int x, int z, boolean windswept) {
        double grove = perlin(seed ^ 0x3A7C1E9B5D2F8064L, x * 0.03D, z * 0.03D);
        if (!windswept && grove < -0.25D) {
            return false;
        }
        int density = windswept ? (grove > 0.2D ? 10 : 22) : grove > 0.3D ? 20 : 48;
        return Math.floorMod(hash(seed ^ 0x51E2B7A4C9D3F086L, x, z), density) == 0;
    }

    private static int spotRank(long seed, int x, int z) {
        return Math.floorMod(hash(seed ^ 0x2F7C1A9E4B6D8350L, x, z), 1 << 20) * 4096 + Math.floorMod(x, 64) * 64 + Math.floorMod(z, 64);
    }

    private static int snowLineAt(long seed, int x, int z, int snowLine) {
        double broad = perlin(seed ^ 0x3D8A6F1C9B2E7450L, x * 0.018D, z * 0.018D);
        double detail = perlin(seed ^ 0x7E1C4B9A2D6F8305L, x * 0.07D, z * 0.07D);
        return snowLine + (int) Math.round(broad * SNOW_LINE_WOBBLE + detail * SNOW_LINE_DETAIL);
    }

    private static double peaksShare(long seed, int x, int z, int y, int snowLine, int peaksFoot) {
        if (y >= snowLine) {
            return 1.0D;
        }
        double share = (y - (peaksFoot - PEAKS_FADE)) / (double) PEAKS_FADE + perlin(seed ^ 0x5B8D2F7A3C1E9640L, x * 0.05D, z * 0.05D) * 0.4D;
        return Mth.clamp(share, 0.0D, 1.0D);
    }

    private static BlockState windsweptSurface(ChunkAccess chunk, long seed, int x, int y, int z, BlockState top, double peaks, boolean snowBand, BlockPos.MutableBlockPos cursor) {
        int roll = Math.floorMod(hash(seed ^ 0x2C9F6E1B4D8A7350L, x, z), 100);
        BlockState replacement = null;
        if (top.is(Blocks.SNOW_BLOCK) && !snowBand && chunk.getNoiseBiome(x >> 2, y >> 2, z >> 2).is(WINDSWEPT_PEAKS_BIOME)) {
            replacement = ModBlocks.RED_MOSS_DIRT.get().defaultBlockState();
        } else if (top.is(Blocks.SNOW_BLOCK)) {
            if (roll < 6) {
                replacement = ModBlocks.SNOWY_RED_MOSS_BLOCK.get().defaultBlockState();
            }
        } else if (!snowBand && chunk.getNoiseBiome(x >> 2, y >> 2, z >> 2).is(WINDSWEPT_PEAKS_BIOME) && isTurfOrRock(top)) {
            replacement = ModBlocks.RED_MOSS_DIRT.get().defaultBlockState();
        } else if (!snowBand && (top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.DIRT) || top.is(Blocks.PODZOL) || top.is(Blocks.COARSE_DIRT)) && roll < peaks * 100.0D) {
            replacement = ModBlocks.RED_MOSS_DIRT.get().defaultBlockState();
        }
        if (replacement == null) {
            return top;
        }
        chunk.setBlockState(cursor.set(x, y, z), replacement, false);
        return replacement;
    }

    private static boolean isTurfOrRock(BlockState top) {
        return top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.DIRT) || top.is(Blocks.PODZOL) || top.is(Blocks.COARSE_DIRT) || top.is(Blocks.STONE) || top.is(Blocks.ANDESITE) || top.is(Blocks.GRAVEL) || top.is(Blocks.SNOW_BLOCK) || top.is(Blocks.MOSS_BLOCK) || top.is(ModBlocks.DARK_DIRT.get());
    }

    private static boolean touchesTree(WorldGenRegion region, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (int dy = 0; dy <= 7; dy++) {
                    BlockState state = region.getBlockState(cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz));
                    if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> treeFor(long seed, int x, int z, double peaks, boolean snowBand) {
        int roll = Math.floorMod(hash(seed ^ 0x4D7B1E9A6C2F8530L, x, z), 20);
        if (snowBand) {
            return roll < 4 ? WINDSWEPT_TREE : null;
        }
        if (roll < 9) {
            return PINE_TREE;
        }
        if (roll == 9 && peaks < 0.8D) {
            return MEGA_PINE_TREE;
        }
        return WINDSWEPT_TREE;
    }

    private static void rebiome(WorldGenRegion region, ChunkAccess chunk, Column[][] grid, long seed, int snowLine, int peaksFoot) {
        Registry<Biome> biomes = region.registryAccess().registryOrThrow(Registries.BIOME);
        Holder<Biome> peaks = biomes.getHolderOrThrow(Biomes.JAGGED_PEAKS);
        Holder<Biome> windswept = biomes.getHolder(WINDSWEPT_PEAKS_BIOME).<Holder<Biome>>map(h -> h).orElseGet(() -> biomes.getHolderOrThrow(Biomes.WINDSWEPT_GRAVELLY_HILLS));
        ChunkPos chunkPos = chunk.getPos();
        for (int cx = 0; cx < 4; cx++) {
            for (int cz = 0; cz < 4; cz++) {
                boolean shaped = false;
                for (int ox = 0; ox < 4 && !shaped; ox++) {
                    for (int oz = 0; oz < 4 && !shaped; oz++) {
                        shaped = grid[cx * 4 + ox + 2][cz * 4 + oz + 2].active;
                    }
                }
                if (!shaped) {
                    continue;
                }
                int x = chunkPos.getMinBlockX() + cx * 4 + 2;
                int z = chunkPos.getMinBlockZ() + cz * 4 + 2;
                double dither = perlin(seed ^ 0x5B8D2F7A3C1E9640L, x * 0.05D, z * 0.05D) * 0.4D;
                int localSnowLine = snowLineAt(seed, x, z, snowLine);
                for (int s = 0; s < chunk.getSectionsCount(); s++) {
                    LevelChunkSection section = chunk.getSection(s);
                    if (!(section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container)) {
                        continue;
                    }
                    int bottom = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(s));
                    for (int cy = 0; cy < 4; cy++) {
                        int y = bottom + cy * 4 + 2;
                        if (y >= localSnowLine) {
                            container.set(cx, cy, cz, peaks);
                        } else if (y >= peaksFoot - PEAKS_FADE && (y - (peaksFoot - PEAKS_FADE)) / (double) PEAKS_FADE + dither > 0.5D) {
                            container.set(cx, cy, cz, windswept);
                        }
                    }
                }
            }
        }
    }

    private static void plantTrees(WorldGenRegion region, ChunkGenerator generator, long seed, List<TreeSpot> spots) {
        if (spots.isEmpty()) {
            return;
        }
        Registry<ConfiguredFeature<?, ?>> features = region.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        for (TreeSpot spot : spots) {
            if (!region.getBlockState(spot.pos()).isAir() || touchesTree(region, spot.pos())) {
                continue;
            }
            Optional<Holder.Reference<ConfiguredFeature<?, ?>>> tree = features.getHolder(spot.feature());
            if (tree.isEmpty()) {
                continue;
            }
            RandomSource random = RandomSource.create(hash(seed, spot.pos().getX(), spot.pos().getZ()));
            tree.get().value().place(region, generator, random, spot.pos());
        }
    }

    private static int terrace(long seed, int x, int z, int targetY, int naturalY, double t) {
        int dy = Math.abs(targetY - naturalY);
        if (dy < 6 || t <= 0.1D || t >= 0.9D) {
            return targetY;
        }
        double zone = perlin(seed ^ 0x6E2A9C4F1B8D3075L, x * 0.018D, z * 0.018D) * 0.7D + perlin(seed ^ 0x1D4B7A9E3C6F5820L, x * 0.05D, z * 0.05D) * 0.3D;
        if (zone <= 0.35D) {
            return targetY;
        }
        int step = zone > 0.55D && dy >= 12 ? 4 : 3;
        return Math.round((float) targetY / step) * step;
    }

    private static int findNaturalTerrainY(WorldGenRegion region, ChunkAccess chunk, boolean inside, int x, int z, int surfaceY, int minBuildY, BlockPos.MutableBlockPos cursor) {
        int limit = Math.max(minBuildY, surfaceY - MAX_SCAN);
        for (int y = surfaceY; y >= limit; y--) {
            BlockState state = stateAt(region, chunk, inside, cursor.set(x, y, z));
            if (isGround(state)) {
                return y;
            }
        }
        return limit;
    }

    private static void raiseColumn(ChunkAccess chunk, int x, int z, int naturalY, int targetY, int surfaceY, int minBuildY, BlockPos.MutableBlockPos cursor) {
        int height = targetY - naturalY;
        int coreDepth = height + 4;
        BlockState[] core = new BlockState[coreDepth];
        for (int i = 0; i < coreDepth; i++) {
            int sampleY = naturalY - i;
            BlockState sample = sampleY < minBuildY ? Blocks.STONE.defaultBlockState() : chunk.getBlockState(cursor.set(x, sampleY, z));
            core[i] = isGround(sample) ? sample : sampleY < 0 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState();
        }
        BlockState oldTop = core[0];
        for (int i = 0; i < height; i++) {
            int y = naturalY + 1 + i;
            BlockState existing = chunk.getBlockState(cursor.set(x, y, z));
            if (!canReplace(existing)) {
                continue;
            }
            int coreIndex = Math.min(coreDepth - 1, height - 1 - i);
            chunk.setBlockState(cursor, core[coreIndex], false);
        }
        if (isOrganicTop(oldTop) && height > 0) {
            chunk.setBlockState(cursor.set(x, naturalY, z), Blocks.DIRT.defaultBlockState(), false);
        }
        stripAbove(chunk, x, z, targetY, Math.max(surfaceY, heightOf(chunk, x, z, Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.WORLD_SURFACE)), cursor);
        capSurface(chunk, x, targetY, z, cursor);
    }

    private static void cutColumn(ChunkAccess chunk, int x, int z, int naturalY, int targetY, int surfaceY, BlockPos.MutableBlockPos cursor) {
        int top = Math.max(surfaceY, heightOf(chunk, x, z, Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.WORLD_SURFACE));
        for (int y = top; y > targetY; y--) {
            BlockState existing = chunk.getBlockState(cursor.set(x, y, z));
            if (canReplace(existing)) {
                chunk.setBlockState(cursor, Blocks.AIR.defaultBlockState(), false);
            }
        }
        capSurface(chunk, x, targetY, z, cursor);
    }

    private static void capSurface(ChunkAccess chunk, int x, int y, int z, BlockPos.MutableBlockPos cursor) {
        if (y + 1 < chunk.getMaxBuildHeight() && !chunk.getFluidState(cursor.set(x, y + 1, z)).isEmpty()) {
            BlockState bed = chunk.getBlockState(cursor.set(x, y, z));
            if (bed.is(BlockTags.DIRT)) {
                chunk.setBlockState(cursor, Blocks.GRAVEL.defaultBlockState(), false);
            }
            return;
        }
        BlockState top = chunk.getBlockState(cursor.set(x, y, z));
        if (!(top.is(Blocks.DIRT) || top.is(Blocks.COARSE_DIRT) || top.is(Blocks.ROOTED_DIRT) || top.is(Blocks.MUD) || top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.PODZOL))) {
            return;
        }
        Holder<Biome> biome = chunk.getNoiseBiome(x >> 2, y >> 2, z >> 2);
        BlockPos pos = cursor.immutable();
        BlockState cap;
        if (biome.value().coldEnoughToSnow(pos.above())) {
            cap = Blocks.SNOW_BLOCK.defaultBlockState();
        } else if (top.is(Blocks.PODZOL) || top.is(Blocks.GRASS_BLOCK)) {
            cap = top;
        } else {
            cap = Blocks.GRASS_BLOCK.defaultBlockState();
        }
        chunk.setBlockState(cursor.set(x, y, z), cap, false);
    }

    private static void stripAbove(ChunkAccess chunk, int x, int z, int groundY, int top, BlockPos.MutableBlockPos cursor) {
        for (int y = groundY + 1; y <= top; y++) {
            BlockState state = chunk.getBlockState(cursor.set(x, y, z));
            if (state.isAir() || !state.getFluidState().isEmpty()) {
                continue;
            }
            if (state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || state.is(BlockTags.SNOW) || state.is(BlockTags.REPLACEABLE) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.SAPLINGS)) {
                chunk.setBlockState(cursor, Blocks.AIR.defaultBlockState(), false);
            }
        }
    }

    private static boolean isGround(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)) {
            return false;
        }
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.DIRT) || state.is(BlockTags.SAND) || state.is(BlockTags.TERRACOTTA)
                || state.is(BlockTags.SNOW) && !state.is(Blocks.SNOW) || state.is(BlockTags.STONE_ORE_REPLACEABLES) || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                || state.is(Tags.Blocks.ORES) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) || state.is(Blocks.MUD) || state.is(Blocks.PACKED_ICE)
                || state.is(Blocks.CALCITE) || state.is(Blocks.SMOOTH_BASALT) || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.SANDSTONE) || state.is(Blocks.RED_SANDSTONE);
    }

    private static boolean canReplace(BlockState state) {
        if (state.is(Blocks.BEDROCK)) {
            return false;
        }
        return state.isAir() || !state.getFluidState().isEmpty() || state.canBeReplaced() || isGround(state)
                || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || state.is(BlockTags.SNOW) || state.is(BlockTags.ICE)
                || state.is(Blocks.POWDER_SNOW) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.SAPLINGS) || state.is(Blocks.POINTED_DRIPSTONE)
                || state.is(Blocks.DRIPSTONE_BLOCK) || state.is(Blocks.SPORE_BLOSSOM) || state.is(Blocks.MOSS_CARPET) || state.is(Blocks.AMETHYST_BLOCK)
                || state.is(Blocks.BUDDING_AMETHYST) || state.is(BlockTags.CAVE_VINES);
    }

    private static boolean isOrganicTop(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.POWDER_SNOW) || state.is(Blocks.MOSS_BLOCK);
    }

    private static double boundaryOffset(long seed, int x, int z) {
        double broad = perlin(seed ^ 0x63317B9A4E2D8F15L, x * 0.012D, z * 0.012D);
        double detail = perlin(seed ^ 0x2F8D1E6A7C4B9350L, x * 0.035D, z * 0.035D);
        return Mth.clamp(broad * 0.75D + detail * 0.35D, -1.0D, 1.0D);
    }

    private static double wildness(long seed, int x, int z) {
        double broad = perlin(seed ^ 0x7C4E1B9D3A6F2058L, x * 0.016D, z * 0.016D) * 0.75D + perlin(seed ^ 0x3B9D6F2C8E1A4705L, x * 0.045D, z * 0.045D) * 0.25D;
        double t = Mth.clamp((broad - 0.12D) / 0.3D, 0.0D, 1.0D);
        return t * t * (3.0D - 2.0D * t);
    }

    private static double unit(double noise) {
        return Mth.clamp(noise * 0.5D + 0.5D, 0.0D, 1.0D);
    }

    private static BlockState mountainStone(long seed, int x, int y, int z, int snowLine, int peaksFoot) {
        return mountainStone(seed, x, y, z, snowLine, peaksFoot, false);
    }

    private static BlockState mountainStone(long seed, int x, int y, int z, int snowLine, int peaksFoot, boolean faceTop) {
        int roll = Math.floorMod(hash(seed ^ 0x1A6E3C9B5D7F2408L, x * 31 + y, z * 17 - y), 1000);
        double cragShare = Mth.clamp((y - (peaksFoot - STONE_BLEND / 2)) / (double) STONE_BLEND, 0.0D, 1.0D);
        if (roll >= cragShare * 1000.0D) {
            return null;
        }
        double highShare = Mth.clamp((y - (snowLine - STONE_BLEND / 2)) / (double) STONE_BLEND, 0.0D, 1.0D);
        if (roll < highShare * 1000.0D) {
            return faceTop && y < snowLine + STONE_BLEND / 2 && roll % 5 < 2 ? ModBlocks.RED_MOSS_HIGHROCK.get().defaultBlockState() : ModBlocks.HIGHROCK_BLOCK.get().defaultBlockState();
        }
        int midBand = (peaksFoot + snowLine) / 2;
        double siltShare = Mth.clamp((y - (midBand - STONE_BLEND / 2)) / (double) STONE_BLEND, 0.0D, 1.0D);
        if (roll < siltShare * 1000.0D) {
            if (faceTop && roll % 5 < 3) {
                return ModBlocks.RED_MOSS_SILTSTONE.get().defaultBlockState();
            }
            return roll % 7 == 0 ? ModBlocks.DIRTY_SILTSTONE_BLOCK.get().defaultBlockState() : ModBlocks.SILTSTONE_BLOCK.get().defaultBlockState();
        }
        return ModBlocks.CRAGROCKS_BLOCK.get().defaultBlockState();
    }

    private static double ridged(long seed, int x, int z) {
        double coarse = 1.0D - 2.0D * Math.abs(perlin(seed ^ 0x5E8C1A7D3B9F2046L, x * 0.09D, z * 0.09D));
        double fine = 1.0D - 2.0D * Math.abs(perlin(seed ^ 0x9D3F7B2E6A1C8450L, x * 0.21D, z * 0.21D));
        return Mth.clamp(coarse * 0.65D + fine * 0.45D, -1.0D, 1.0D);
    }

    private static double relief(long seed, int x, int z) {
        double bendX = perlin(seed ^ 0x0F6D3B8A2C5E7914L, x * 0.011D, z * 0.011D) * 18.0D;
        double bendZ = perlin(seed ^ 0x7A2E9C4B1D6F3850L, x * 0.011D, z * 0.011D) * 18.0D;
        double wx = x + bendX;
        double wz = z + bendZ;
        double ridged = 1.0D - 2.0D * Math.abs(perlin(seed ^ 0x4C8B2F6E9A1D7305L, wx * 0.028D, wz * 0.028D));
        double broad = perlin(seed ^ 0x1B7D4E9F2A6C8053L, wx * 0.019D, wz * 0.019D);
        double fine = perlin(seed ^ 0x6D2A8F3C7B1E9504L, wx * 0.07D, wz * 0.07D);
        return Mth.clamp(ridged * 0.5D + broad * 0.4D + fine * 0.25D, -1.0D, 1.0D);
    }

    private static double perlin(long seed, double x, double z) {
        int x0 = Mth.floor(x);
        int z0 = Mth.floor(z);
        double fx = x - x0;
        double fz = z - z0;
        double u = fade(fx);
        double v = fade(fz);
        double n00 = gradient(hash(seed, x0, z0), fx, fz);
        double n10 = gradient(hash(seed, x0 + 1, z0), fx - 1.0D, fz);
        double n01 = gradient(hash(seed, x0, z0 + 1), fx, fz - 1.0D);
        double n11 = gradient(hash(seed, x0 + 1, z0 + 1), fx - 1.0D, fz - 1.0D);
        return Mth.clamp(Mth.lerp(v, Mth.lerp(u, n00, n10), Mth.lerp(u, n01, n11)) * 1.4142D, -1.0D, 1.0D);
    }

    private static double gradient(int hash, double x, double z) {
        return switch (hash & 7) {
            case 0 -> x + z;
            case 1 -> -x + z;
            case 2 -> x - z;
            case 3 -> -x - z;
            case 4 -> x;
            case 5 -> -x;
            case 6 -> z;
            default -> -z;
        };
    }

    private static int hash(long seed, int x, int z) {
        long v = seed + x * 0x9E3779B97F4A7C15L + z * 0xC2B2AE3D27D4EB4FL;
        v = (v ^ (v >>> 30)) * 0xBF58476D1CE4E5B9L;
        v = (v ^ (v >>> 27)) * 0x94D049BB133111EBL;
        return (int) (v ^ (v >>> 31));
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6.0D - 15.0D) + 10.0D);
    }
}
