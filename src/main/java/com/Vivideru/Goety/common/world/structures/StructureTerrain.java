package com.Vivideru.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.common.Tags;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class StructureTerrain {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int BASEMENT_DEPTH = 3;
    private static final int UPPER_FLOOR = 6;
    private static final int SEARCH_CHUNKS = 8;
    private static final int OTHER_STRUCTURE_REACH = 32;
    private static final int APRON = 2;
    private static final double RAISE_RATE = 2.0D;
    private static final double CUT_RATE = 2.5D;
    private static final int REGION_BLOCKS = 128;
    private static final int SOIL_DEPTH = 3;
    private static final int FORECOURT = 10;
    private static final int FORECOURT_HALF = 4;

    public record Entrance(String piece, int x, int z, Direction facing) {
    }

    private static final List<Entrance> ENTRANCES = List.of(
            new Entrance("black_assembly/black_assembly", 5, 10, Direction.SOUTH),
            new Entrance("blighted_shack/house", 15, 0, Direction.NORTH),
            new Entrance("blighted_shack/house", 15, 31, Direction.SOUTH),
            new Entrance("dark_manor/dark_manor", 39, 23, Direction.EAST),
            new Entrance("ominous_blacksmith/blacksmith", 14, 40, Direction.SOUTH),
            new Entrance("royal_tribute/shrine_nedeak", 23, 46, Direction.SOUTH),
            new Entrance("royal_tribute/shrine_nightheron", 19, 0, Direction.NORTH),
            new Entrance("ruined_monastery/church", 10, 35, Direction.SOUTH),
            new Entrance("secluded_igloo/secluded_igloo", 7, 16, Direction.SOUTH),
            new Entrance("sorcerous_keep/sorcerous_keep", 0, 12, Direction.WEST),
            new Entrance("graveyard/graveyard_1", 9, 0, Direction.NORTH),
            new Entrance("graveyard/graveyard_2", 9, 0, Direction.NORTH),
            new Entrance("graveyard/graveyard_3", 9, 0, Direction.NORTH),
            new Entrance("graveyard/graveyard_4", 9, 0, Direction.NORTH),
            new Entrance("graveyard/graveyard_big", 16, 30, Direction.SOUTH));

    private record Approach(BlockPos mouth, Direction facing, int floorY) {
        BoundingBox forecourt() {
            Direction side = this.facing.getClockWise();
            BlockPos a = this.mouth.relative(side, -FORECOURT_HALF);
            BlockPos b = this.mouth.relative(this.facing, FORECOURT - 1).relative(side, FORECOURT_HALF);
            return BoundingBox.fromCorners(new BlockPos(a.getX(), this.floorY, a.getZ()), new BlockPos(b.getX(), this.floorY, b.getZ()));
        }
    }

    public record Profile(ResourceKey<Structure> key, int margin, int halfExtent, Optional<String> piece, int trunk) {
        Profile(ResourceKey<Structure> key, int margin, int halfExtent) {
            this(key, margin, halfExtent, Optional.empty(), 0);
        }

        int keepAway() {
            return this.halfExtent + this.margin;
        }

        boolean accepts(StructurePiece candidate) {
            if (this.piece.isEmpty()) {
                return true;
            }
            return candidate instanceof PoolElementStructurePiece pool && pool.getElement().toString().contains(this.piece.get());
        }

        BoundingBox footprint(BoundingBox box) {
            if (this.trunk <= 0) {
                return box;
            }
            int cx = (box.minX() + box.maxX()) / 2;
            int cz = (box.minZ() + box.maxZ()) / 2;
            return new BoundingBox(Math.max(box.minX(), cx - this.trunk), box.minY(), Math.max(box.minZ(), cz - this.trunk),
                    Math.min(box.maxX(), cx + this.trunk), box.maxY(), Math.min(box.maxZ(), cz + this.trunk));
        }

        int reach() {
            return Math.max(this.margin, REGION_BLOCKS - this.halfExtent - 16);
        }
    }

    private static final Map<ResourceKey<Structure>, Profile> PROFILES = new LinkedHashMap<>();

    static {
        profile("black_assembly", 6, 6);
        profile("secluded_igloo", 6, 9);
        profile("blighted_shack", 8, 16);
        profile("graveyard", 8, 17);
        profile("ominous_blacksmith", 10, 24);
        profile("sorcerous_keep", 10, 13);
        profile("dark_manor", 12, 24);
        profile("royal_tribute", 12, 24);
        profile("ruined_monastery", 10, 48);
        ResourceKey<Structure> spiderDen = ResourceKey.create(Registries.STRUCTURE, Goety.location("spider_den"));
        PROFILES.put(spiderDen, new Profile(spiderDen, 6, 12, Optional.of("spider_den/tree"), 5));
    }

    private static void profile(String name, int margin, int halfExtent) {
        ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, Goety.location(name));
        PROFILES.put(key, new Profile(key, margin, halfExtent));
    }

    private StructureTerrain() {
    }

    public static boolean isShaped(ResourceKey<Structure> key) {
        return PROFILES.containsKey(key);
    }

    private record Site(Profile profile, List<BoundingBox> pieces, List<BoundingBox> forecourts) {
    }

    public static void apply(WorldGenLevel level, ChunkAccess chunk) {
        if (!(level instanceof WorldGenRegion region)) {
            return;
        }
        List<Site> sites = sitesNear(region, chunk.getPos());
        if (sites.isEmpty()) {
            return;
        }
        shape(region, chunk, sites);
    }

    private static List<Site> sitesNear(WorldGenRegion region, ChunkPos center) {
        Registry<Structure> registry = region.registryAccess().registryOrThrow(Registries.STRUCTURE);
        List<Site> sites = new ArrayList<>();
        for (int dx = -SEARCH_CHUNKS; dx <= SEARCH_CHUNKS; dx++) {
            for (int dz = -SEARCH_CHUNKS; dz <= SEARCH_CHUNKS; dz++) {
                int cx = center.x + dx;
                int cz = center.z + dz;
                if (!region.hasChunk(cx, cz)) {
                    continue;
                }
                ChunkAccess neighbour;
                try {
                    neighbour = region.getChunk(cx, cz);
                } catch (RuntimeException ignored) {
                    continue;
                }
                for (Map.Entry<Structure, StructureStart> entry : neighbour.getAllStarts().entrySet()) {
                    Optional<ResourceKey<Structure>> key = registry.getResourceKey(entry.getKey());
                    if (key.isEmpty() || !PROFILES.containsKey(key.get()) || !entry.getValue().isValid()) {
                        continue;
                    }
                    Profile profile = PROFILES.get(key.get());
                    List<BoundingBox> pieces = groundPieces(entry.getValue(), profile);
                    if (pieces.isEmpty()) {
                        continue;
                    }
                    List<BoundingBox> forecourts = new ArrayList<>();
                    for (Approach approach : approaches(entry.getValue())) {
                        forecourts.add(approach.forecourt());
                    }
                    int reach = profile.reach() + FORECOURT + 1;
                    boolean touches = false;
                    for (BoundingBox box : pieces) {
                        if (box.maxX() + reach >= center.getMinBlockX() && box.minX() - reach <= center.getMaxBlockX()
                                && box.maxZ() + reach >= center.getMinBlockZ() && box.minZ() - reach <= center.getMaxBlockZ()) {
                            touches = true;
                            break;
                        }
                    }
                    if (touches) {
                        sites.add(new Site(profile, pieces, forecourts));
                    }
                }
            }
        }
        return sites;
    }

    private static List<BoundingBox> groundPieces(StructureStart start, Profile profile) {
        List<StructurePiece> all = start.getPieces();
        if (all.isEmpty()) {
            return List.of();
        }
        List<BoundingBox> pieces = new ArrayList<>();
        if (profile.piece().isPresent()) {
            for (StructurePiece piece : all) {
                if (profile.accepts(piece)) {
                    pieces.add(profile.footprint(piece.getBoundingBox()));
                }
            }
            return pieces;
        }
        int floor = all.get(0).getBoundingBox().minY();
        for (StructurePiece piece : all) {
            BoundingBox box = piece.getBoundingBox();
            if (box.minY() >= floor - BASEMENT_DEPTH && box.minY() <= floor + UPPER_FLOOR) {
                pieces.add(profile.footprint(box));
            }
        }
        return pieces;
    }

    private static List<Approach> approaches(StructureStart start) {
        List<Approach> result = new ArrayList<>();
        for (StructurePiece piece : start.getPieces()) {
            if (!(piece instanceof PoolElementStructurePiece pool)) {
                continue;
            }
            String element = pool.getElement().toString();
            for (Entrance entrance : ENTRANCES) {
                if (!element.contains(entrance.piece())) {
                    continue;
                }
                Rotation rotation = pool.getRotation();
                BlockPos edge = pool.getPosition().offset(StructureTemplate.transform(new BlockPos(entrance.x(), 0, entrance.z()), Mirror.NONE, rotation, BlockPos.ZERO));
                Direction facing = rotation.rotate(entrance.facing());
                result.add(new Approach(edge.relative(facing), facing, pool.getBoundingBox().minY()));
            }
        }
        return result;
    }

    private static final class Column {
        boolean active;
        boolean underPiece;
        boolean forecourt;
        boolean underWater;
        int solidTop;
        int surfaceTop;
        double distance = Double.MAX_VALUE;
        double target;
    }

    private static final int HALO = 2;
    private static final int GRID = 16 + 2 * HALO;

    private static void shape(WorldGenRegion region, ChunkAccess chunk, List<Site> sites) {
        ChunkPos chunkPos = chunk.getPos();
        long seed = region.getSeed();
        Column[][] grid = new Column[GRID][GRID];
        int originX = chunkPos.getMinBlockX() - HALO;
        int originZ = chunkPos.getMinBlockZ() - HALO;
        for (int gx = 0; gx < GRID; gx++) {
            for (int gz = 0; gz < GRID; gz++) {
                Column column = new Column();
                grid[gx][gz] = column;
                int x = originX + gx;
                int z = originZ + gz;
                ChunkAccess holder = chunk;
                if ((x >> 4) != chunkPos.x || (z >> 4) != chunkPos.z) {
                    if (!region.hasChunk(x >> 4, z >> 4)) {
                        continue;
                    }
                    holder = region.getChunk(x >> 4, z >> 4);
                }
                int hint = Math.max(heightOf(holder, x & 15, z & 15, Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.OCEAN_FLOOR),
                        heightOf(holder, x & 15, z & 15, Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.WORLD_SURFACE));
                int y = Math.min(holder.getMaxBuildHeight() - 1, hint + 4);
                int floor = holder.getMinBuildHeight();
                BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
                while (y > floor && holder.getBlockState(probe.set(x, y, z)).isAir()) {
                    y--;
                }
                column.surfaceTop = y;
                boolean water = false;
                while (y > floor) {
                    BlockState state = holder.getBlockState(probe.set(x, y, z));
                    if (!state.getFluidState().isEmpty()) {
                        water = true;
                    } else if (isSolidTop(state)) {
                        break;
                    }
                    y--;
                }
                column.solidTop = y;
                column.underWater = water;
                column.target = column.solidTop;
                double bestDistance = Double.MAX_VALUE;
                int floorY = 0;
                int margin = 0;
                boolean inPiece = false;
                boolean inForecourt = false;
                for (Site site : sites) {
                    for (int list = 0; list < 2; list++) {
                        for (BoundingBox box : list == 0 ? site.pieces() : site.forecourts()) {
                            int dx = x < box.minX() ? box.minX() - x : x > box.maxX() ? x - box.maxX() : 0;
                            int dz = z < box.minZ() ? box.minZ() - z : z > box.maxZ() ? z - box.maxZ() : 0;
                            double distance = Math.sqrt((double) dx * dx + (double) dz * dz);
                            if (distance > site.profile().reach()) {
                                continue;
                            }
                            if (distance == 0.0D) {
                                if (list == 0) {
                                    inPiece = true;
                                } else {
                                    inForecourt = true;
                                }
                            }
                            if (distance < bestDistance || distance == bestDistance && box.minY() < floorY) {
                                bestDistance = distance;
                                floorY = box.minY();
                                margin = site.profile().margin();
                            }
                        }
                    }
                }
                if (bestDistance == Double.MAX_VALUE) {
                    continue;
                }
                column.underPiece = bestDistance == 0.0D;
                column.forecourt = inForecourt && !inPiece;
                column.distance = bestDistance;
                column.active = true;
                column.target = column.underPiece ? floorY : slopedTarget(seed, x, z, bestDistance, floorY, column.solidTop, margin);
            }
        }
        double[][] smoothed = new double[GRID][GRID];
        for (int gx = 1; gx < GRID - 1; gx++) {
            for (int gz = 1; gz < GRID - 1; gz++) {
                Column column = grid[gx][gz];
                smoothed[gx][gz] = column.target;
                if (!column.active || column.underPiece || column.distance <= APRON) {
                    continue;
                }
                double sum = 0.0D;
                double weight = 0.0D;
                for (int ox = -1; ox <= 1; ox++) {
                    for (int oz = -1; oz <= 1; oz++) {
                        Column other = grid[gx + ox][gz + oz];
                        double w = ox == 0 && oz == 0 ? 4.0D : (ox == 0 || oz == 0 ? 2.0D : 1.0D);
                        sum += w * other.target;
                        weight += w;
                    }
                }
                smoothed[gx][gz] = sum / weight;
            }
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int raised = 0;
        int cut = 0;
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                Column column = grid[lx + HALO][lz + HALO];
                if (!column.active) {
                    continue;
                }
                int x = chunkPos.getMinBlockX() + lx;
                int z = chunkPos.getMinBlockZ() + lz;
                double level = smoothed[lx + HALO][lz + HALO];
                if (column.forecourt) {
                    level += noise(seed + 8191L, x, z, 5.0D) * 1.2D;
                } else if (!column.underPiece && column.distance > APRON) {
                    level += jaggedness(seed, x, z, column.distance, Math.abs(level - column.solidTop));
                }
                int target = (int) Math.round(level);
                int clearTo = Math.max(column.surfaceTop, column.solidTop);
                if (target > column.solidTop) {
                    raise(chunk, x, z, column.solidTop, target, clearTo, cursor);
                    raised++;
                } else if (target < column.solidTop) {
                    lower(chunk, x, z, column.solidTop, target, clearTo, cursor);
                    cut++;
                } else if (column.underPiece) {
                    stripCover(chunk, x, z, target, clearTo, cursor);
                }
                fillBelow(chunk, x, z, target, column.underPiece, cursor);
            }
        }
        if (raised + cut > 0) {
            refreshHeightmaps(chunk);
            LOGGER.debug("Structure ground work {}: {} columns raised, {} lowered for {} structure(s)", chunkPos, raised, cut, sites.size());
        }
    }

    public static void refreshHeightmaps(ChunkAccess chunk) {
        Heightmap.primeHeightmaps(chunk, EnumSet.of(Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.OCEAN_FLOOR_WG,
                Heightmap.Types.WORLD_SURFACE, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES));
    }

    private static double jaggedness(long seed, int x, int z, double distance, double relief) {
        double fade = Mth.clamp((distance - APRON) / 5.0D, 0.0D, 1.0D) * Mth.clamp(relief / 5.0D, 0.0D, 1.0D);
        if (fade <= 0.0D) {
            return 0.0D;
        }
        double ridgeA = 1.0D - Math.abs(noise(seed + 1327L, x, z, 9.0D));
        double ridgeB = 1.0D - Math.abs(noise(seed + 2731L, x, z, 4.0D));
        double grain = noise(seed + 4099L, x, z, 2.0D);
        double amplitude = Math.min(4.0D, 0.18D * relief) * fade;
        return ((ridgeA - 0.5D) * 1.2D + (ridgeB - 0.5D) * 0.8D) * amplitude + grain * 0.6D * fade;
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

    private static double slopedTarget(long seed, int x, int z, double distance, int floorY, int natural, int margin) {
        double fade = Mth.clamp((distance - APRON) / 6.0D, 0.0D, 1.0D);
        double warp = noise(seed, x, z, 13.0D) * Math.min(9.0D, 2.5D + 0.12D * distance) * fade;
        double run = Math.max(0.0D, distance - APRON + warp);
        double grade = 1.0D + 0.4D * noise(seed + 31L, x, z, 21.0D) * fade;
        double sloped = floorY >= natural ? Math.max(natural, floorY - run * RAISE_RATE * grade) : Math.min(natural, floorY + run * CUT_RATE * grade);
        double eased = natural;
        if (distance <= margin) {
            double t = distance / margin;
            eased = Mth.lerp(t * t * (3.0D - 2.0D * t), floorY, natural);
        }
        double target = Math.abs(sloped - natural) >= Math.abs(eased - natural) ? sloped : eased;
        double relief = Math.abs(target - natural);
        double amplitude = Math.min(3.0D, 0.15D * relief) * fade * Mth.clamp(relief / 6.0D, 0.0D, 1.0D);
        double bumps = noise(seed + 7919L, x, z, 7.0D) * 0.7D + noise(seed + 104729L, x, z, 3.0D) * 0.3D;
        return target + bumps * amplitude;
    }

    private static void raise(ChunkAccess chunk, int x, int z, int solidTop, int target, int clearTo, BlockPos.MutableBlockPos cursor) {
        BlockState top = chunk.getBlockState(cursor.set(x, solidTop, z));
        BlockState below = solidTop - 1 > chunk.getMinBuildHeight() ? chunk.getBlockState(cursor.set(x, solidTop - 1, z)) : top;
        BlockState soil = isFiller(below) ? below : isFiller(top) ? top : Blocks.DIRT.defaultBlockState();
        BlockState rock = soil.is(BlockTags.SAND) ? (soil.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState()) : Blocks.STONE.defaultBlockState();
        if (!isGround(top)) {
            top = soil;
        }
        for (int y = solidTop; y < target; y++) {
            BlockState state = chunk.getBlockState(cursor.set(x, y, z));
            boolean keep = y == solidTop && isGround(state) && !state.is(Blocks.GRASS_BLOCK) && !state.is(Blocks.PODZOL) && !state.is(Blocks.MYCELIUM);
            BlockState fill = y < target - SOIL_DEPTH ? rock : soil;
            chunk.setBlockState(cursor, keep ? state : fill, false);
        }
        chunk.setBlockState(cursor.set(x, target, z), top, false);
        stripCover(chunk, x, z, target, clearTo, cursor);
        capSurface(chunk, x, target, z, cursor);
    }

    private static void lower(ChunkAccess chunk, int x, int z, int solidTop, int target, int clearTo, BlockPos.MutableBlockPos cursor) {
        BlockState top = chunk.getBlockState(cursor.set(x, solidTop, z));
        stripCover(chunk, x, z, solidTop, clearTo, cursor);
        for (int y = solidTop; y > target; y--) {
            chunk.setBlockState(cursor.set(x, y, z), Blocks.AIR.defaultBlockState(), false);
        }
        BlockState newTop = chunk.getBlockState(cursor.set(x, target, z));
        if (isGround(top) && isGround(newTop) && !newTop.is(BlockTags.BASE_STONE_OVERWORLD)) {
            chunk.setBlockState(cursor, top, false);
        } else if (newTop.isAir() || !newTop.getFluidState().isEmpty()) {
            chunk.setBlockState(cursor, isGround(top) ? top : Blocks.DIRT.defaultBlockState(), false);
        }
        capSurface(chunk, x, target, z, cursor);
    }

    private static void stripCover(ChunkAccess chunk, int x, int z, int groundY, int top, BlockPos.MutableBlockPos cursor) {
        for (int y = groundY + 1; y <= top; y++) {
            BlockState state = chunk.getBlockState(cursor.set(x, y, z));
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                chunk.setBlockState(cursor, Blocks.AIR.defaultBlockState(), false);
            }
        }
    }

    private static final int FILL_DEPTH = 64;
    private static final int SOLID_RUN = 4;

    private static void fillBelow(ChunkAccess chunk, int x, int z, int groundY, boolean underPiece, BlockPos.MutableBlockPos cursor) {
        int solid = 0;
        int bottom = Math.max(chunk.getMinBuildHeight(), groundY - FILL_DEPTH);
        for (int y = groundY - 1; y >= bottom && solid < SOLID_RUN; y--) {
            BlockState state = chunk.getBlockState(cursor.set(x, y, z));
            boolean hollow = state.isAir() || (underPiece && !state.getFluidState().isEmpty()) || state.is(Blocks.POWDER_SNOW) || state.is(Blocks.SNOW);
            if (hollow) {
                chunk.setBlockState(cursor, y < chunk.getMinBuildHeight() + 8 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState(), false);
                solid = 0;
            } else if (isGround(state) || !state.getFluidState().isEmpty()) {
                solid++;
            } else {
                solid++;
            }
        }
    }

    private static void capSurface(ChunkAccess chunk, int x, int y, int z, BlockPos.MutableBlockPos cursor) {
        BlockState top = chunk.getBlockState(cursor.set(x, y, z));
        if (!(top.is(Blocks.DIRT) || top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.PODZOL) || top.is(Blocks.COARSE_DIRT) || top.is(Blocks.ROOTED_DIRT))) {
            return;
        }
        if (y + 1 < chunk.getMaxBuildHeight() && !chunk.getFluidState(cursor.set(x, y + 1, z)).isEmpty()) {
            chunk.setBlockState(cursor.set(x, y, z), Blocks.GRAVEL.defaultBlockState(), false);
            return;
        }
        Holder<Biome> biome = chunk.getNoiseBiome(x >> 2, y >> 2, z >> 2);
        BlockPos pos = cursor.immutable();
        if (biome.value().coldEnoughToSnow(pos.above())) {
            chunk.setBlockState(cursor.set(x, y, z), Blocks.SNOW_BLOCK.defaultBlockState(), false);
        } else if (top.is(Blocks.DIRT)) {
            chunk.setBlockState(cursor.set(x, y, z), Blocks.GRASS_BLOCK.defaultBlockState(), false);
        }
    }

    private static boolean isSolidTop(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty() || state.is(Blocks.POWDER_SNOW) || state.is(Blocks.SNOW)) {
            return false;
        }
        return state.blocksMotion() || isGround(state);
    }

    private static int heightOf(ChunkAccess chunk, int x, int z, Heightmap.Types worldGen, Heightmap.Types finished) {
        Heightmap.Types type = chunk.hasPrimedHeightmap(worldGen) ? worldGen : chunk.hasPrimedHeightmap(finished) ? finished : worldGen;
        return chunk.getHeight(type, x, z);
    }

    private static boolean isFiller(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(BlockTags.SAND) || state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.TERRACOTTA)
                || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) || state.is(Blocks.MUD) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.PACKED_ICE)
                || state.is(Blocks.SANDSTONE) || state.is(Blocks.RED_SANDSTONE);
    }

    private static boolean isGround(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty()) {
            return false;
        }
        return isFiller(state) || state.is(BlockTags.STONE_ORE_REPLACEABLES) || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES) || state.is(Tags.Blocks.ORES)
                || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.CALCITE) || state.is(Blocks.SMOOTH_BASALT) || state.is(Blocks.MYCELIUM);
    }


    private static final ThreadLocal<ChunkGeneratorStructureState> STRUCTURE_STATE = new ThreadLocal<>();

    public static void enterStructureState(ChunkGeneratorStructureState state) {
        STRUCTURE_STATE.set(state);
    }

    public static void exitStructureState() {
        STRUCTURE_STATE.remove();
    }

    public static boolean reserves(Structure candidate, RegistryAccess registryAccess, ChunkGenerator generator, RandomState randomState, LevelHeightAccessor heightAccessor, ChunkPos chunkPos) {
        if (candidate.step() != GenerationStep.Decoration.SURFACE_STRUCTURES || candidate instanceof WindShrineStructure || candidate instanceof WindShrineExpansionStructure) {
            return false;
        }
        ChunkGeneratorStructureState state = STRUCTURE_STATE.get();
        if (state == null) {
            return false;
        }
        Registry<Structure> registry = registryAccess.registryOrThrow(Registries.STRUCTURE);
        Optional<ResourceKey<Structure>> candidateKey = registry.getResourceKey(candidate);
        for (Profile profile : PROFILES.values()) {
            if (candidateKey.isPresent() && candidateKey.get().equals(profile.key())) {
                continue;
            }
            Optional<Holder.Reference<Structure>> holder = registry.getHolder(profile.key());
            if (holder.isEmpty()) {
                continue;
            }
            List<StructurePlacement> placements = state.getPlacementsForStructure(holder.get());
            if (placements.isEmpty()) {
                continue;
            }
            int keepAway = profile.keepAway() + OTHER_STRUCTURE_REACH;
            int reachChunks = Math.floorDiv(keepAway, 16) + 1;
            for (int dx = -reachChunks; dx <= reachChunks; dx++) {
                for (int dz = -reachChunks; dz <= reachChunks; dz++) {
                    int cx = chunkPos.x + dx;
                    int cz = chunkPos.z + dz;
                    int centerX = cx * 16 + 8;
                    int centerZ = cz * 16 + 8;
                    if (centerX + keepAway < chunkPos.getMinBlockX() || centerX - keepAway > chunkPos.getMaxBlockX()
                            || centerZ + keepAway < chunkPos.getMinBlockZ() || centerZ - keepAway > chunkPos.getMaxBlockZ()) {
                        continue;
                    }
                    boolean placed = false;
                    for (StructurePlacement placement : placements) {
                        if (placement.isStructureChunk(state, cx, cz)) {
                            placed = true;
                            break;
                        }
                    }
                    if (!placed || !biomeAllows(holder.get().value(), generator, randomState, heightAccessor, centerX, centerZ)) {
                        continue;
                    }
                    if (candidateKey.isPresent() && PROFILES.containsKey(candidateKey.get())) {
                        int order = profile.key().location().compareTo(candidateKey.get().location());
                        if (order > 0 || order == 0 && new ChunkPos(cx, cz).toLong() >= chunkPos.toLong()) {
                            continue;
                        }
                    }
                    LOGGER.debug("Structure {} in chunk {} gives way to {} expected around chunk [{}, {}]", candidateKey.map(k -> k.location().toString()).orElse("?"), chunkPos, profile.key().location(), cx, cz);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean biomeAllows(Structure structure, ChunkGenerator generator, RandomState randomState, LevelHeightAccessor heightAccessor, int x, int z) {
        int y = generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
        Holder<Biome> biome = generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z), randomState.sampler());
        return structure.biomes().contains(biome);
    }
}
