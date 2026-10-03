package com.Vivideru.Goety.common.world.structures;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import com.Polarice3.Goety.Goety;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class WindShrinePlans {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine");
    public static final List<ResourceLocation> WINGS = List.of(
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion_a"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion_b"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion_c"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion_d"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion_e"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion_f"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion_g"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion_h"));
    private static final int CACHE_SIZE = 512;
    private static final Map<Long, Map<Long, Optional<Plan>>> CACHES = new LinkedHashMap<>();

    public record Resolver(RegistryAccess registryAccess, ChunkGenerator generator, RandomState randomState, StructureTemplateManager templates, long seed, LevelHeightAccessor heightAccessor) {
        public static Resolver of(WorldGenRegion region, ChunkGenerator generator) {
            return new Resolver(region.registryAccess(), generator, region.getLevel().getChunkSource().randomState(), region.getLevel().getStructureManager(), region.getSeed(), region);
        }

        public static Resolver of(Structure.GenerationContext context) {
            return new Resolver(context.registryAccess(), context.chunkGenerator(), context.randomState(), context.structureTemplateManager(), context.seed(), context.heightAccessor());
        }

        StructureStart generate(Structure structure, ChunkPos chunkPos) {
            return structure.generate(this.registryAccess, this.generator, this.generator.getBiomeSource(), this.randomState, this.templates, this.seed, chunkPos, 0, this.heightAccessor, structure.biomes()::contains);
        }
    }

    public record Door(BlockPos jigsaw, Direction facing) {
        public BlockPos target() {
            return this.jigsaw.relative(this.facing);
        }
    }

    public static final class Footprint {
        public final int minX;
        public final int minZ;
        public final int maxX;
        public final int maxZ;
        public final int floorY;
        boolean stairs;
        int height;
        private boolean sloped;
        private boolean alongX;
        private int floorAtMin;
        private int floorAtMax;

        Footprint(int minX, int minZ, int maxX, int maxZ, int floorY) {
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
            this.floorY = floorY;
            this.floorAtMin = floorY;
            this.floorAtMax = floorY;
        }

        public double distanceTo(int x, int z) {
            int dx = x < this.minX ? this.minX - x : x > this.maxX ? x - this.maxX : 0;
            int dz = z < this.minZ ? this.minZ - z : z > this.maxZ ? z - this.maxZ : 0;
            if (dx == 0 && dz == 0) {
                return 0.0D;
            }
            return Math.sqrt(dx * dx + dz * dz) * 0.82D + Math.max(dx, dz) * 0.18D;
        }

        public int floorAt(int x, int z) {
            if (!this.sloped) {
                return this.floorY;
            }
            int min = this.alongX ? this.minX : this.minZ;
            int max = this.alongX ? this.maxX : this.maxZ;
            int pos = Mth.clamp(this.alongX ? x : z, min, max);
            double progress = max == min ? 0.0D : (double) (pos - min) / (max - min);
            return (int) Math.round(Mth.lerp(progress, this.floorAtMin, this.floorAtMax));
        }

        private boolean touchesEnd(Footprint other, boolean atMin) {
            if (this.alongX) {
                int face = atMin ? this.minX - 1 : this.maxX + 1;
                return other.minX <= face && other.maxX >= face && other.maxZ >= this.minZ && other.minZ <= this.maxZ;
            }
            int face = atMin ? this.minZ - 1 : this.maxZ + 1;
            return other.minZ <= face && other.maxZ >= face && other.maxX >= this.minX && other.minX <= this.maxX;
        }
    }

    public record Plan(long seed, ChunkPos chunk, int summitY, int slopeRadius, BlockPos center, List<Optional<Door>> doors, List<BoundingBox> parentBoxes, List<List<BoundingBox>> wingBoxes, List<Footprint> footprints, BoundingBox hull) {
        public Optional<Door> door(int index) {
            return index < this.doors.size() ? this.doors.get(index) : Optional.empty();
        }

        public List<BoundingBox> obstaclesFor(int index) {
            List<BoundingBox> result = new ArrayList<>(this.parentBoxes);
            for (int i = 0; i < Math.min(index, this.wingBoxes.size()); i++) {
                result.addAll(this.wingBoxes.get(i));
            }
            return result;
        }

        public int reach() {
            return this.slopeRadius + WindShrineTerrain.PLATEAU_MARGIN + WindShrineTerrain.maxWarp(this.slopeRadius) + 2;
        }

        public boolean touches(ChunkPos chunkPos) {
            int reach = this.reach();
            return chunkPos.getMaxBlockX() >= this.hull.minX() - reach && chunkPos.getMinBlockX() <= this.hull.maxX() + reach
                    && chunkPos.getMaxBlockZ() >= this.hull.minZ() - reach && chunkPos.getMinBlockZ() <= this.hull.maxZ() + reach;
        }
    }

    private WindShrinePlans() {
    }

    public static List<Plan> forChunk(WorldGenRegion region, ChunkGenerator generator) {
        Resolver resolver = Resolver.of(region, generator);
        Optional<WindShrineStructure> shrine = shrine(resolver.registryAccess());
        if (shrine.isEmpty()) {
            return List.of();
        }
        int reachChunks = Math.floorDiv(shrine.get().maxDistanceFromCenter + shrine.get().slopeRadius + 160, 16) + 1;
        ChunkPos current = region.getCenter();
        List<Plan> plans = new ArrayList<>();
        for (ChunkPos candidate : candidates(resolver.registryAccess(), resolver.seed, current, reachChunks)) {
            Optional<Plan> plan = plan(resolver, candidate);
            if (plan.isPresent() && plan.get().touches(current)) {
                plans.add(plan.get());
            }
        }
        return plans;
    }

    public static Optional<Plan> shrineWithDoorIn(Resolver resolver, ChunkPos chunkPos, int door, int searchChunks) {
        for (ChunkPos candidate : candidates(resolver.registryAccess(), resolver.seed, chunkPos, searchChunks)) {
            Optional<Plan> plan = plan(resolver, candidate);
            if (plan.isEmpty()) {
                continue;
            }
            Optional<Door> found = plan.get().door(door);
            if (found.isPresent() && new ChunkPos(found.get().target()).equals(chunkPos)) {
                return plan;
            }
        }
        return Optional.empty();
    }

    public static List<ChunkPos> candidates(RegistryAccess registryAccess, long seed, ChunkPos around, int searchChunks) {
        Optional<RandomSpreadStructurePlacement> spread = spread(registryAccess);
        if (spread.isEmpty()) {
            return List.of();
        }
        int spacing = spread.get().spacing();
        Set<Long> visited = new HashSet<>();
        List<ChunkPos> result = new ArrayList<>();
        for (int rx = Math.floorDiv(around.x - searchChunks, spacing); rx <= Math.floorDiv(around.x + searchChunks, spacing); rx++) {
            for (int rz = Math.floorDiv(around.z - searchChunks, spacing); rz <= Math.floorDiv(around.z + searchChunks, spacing); rz++) {
                ChunkPos candidate = spread.get().getPotentialStructureChunk(seed, rx * spacing, rz * spacing);
                if (candidate.getChessboardDistance(around) <= searchChunks && visited.add(candidate.toLong())) {
                    result.add(candidate);
                }
            }
        }
        return result;
    }

    public static Optional<RandomSpreadStructurePlacement> spread(RegistryAccess registryAccess) {
        Registry<StructureSet> sets = registryAccess.registryOrThrow(Registries.STRUCTURE_SET);
        StructureSet set = sets.get(ID);
        return set != null && set.placement() instanceof RandomSpreadStructurePlacement spread ? Optional.of(spread) : Optional.empty();
    }

    private static Optional<WindShrineStructure> shrine(RegistryAccess registryAccess) {
        Structure structure = registryAccess.registryOrThrow(Registries.STRUCTURE).get(ID);
        return structure instanceof WindShrineStructure shrine ? Optional.of(shrine) : Optional.empty();
    }

    private static final int RESERVED_MARGIN = 200;

    public static boolean reservesSurface(Structure structure, RegistryAccess registryAccess, ChunkGenerator generator, RandomState randomState, StructureTemplateManager templates, long seed, LevelHeightAccessor heightAccessor, ChunkPos chunkPos) {
        if (structure instanceof WindShrineStructure || structure instanceof WindShrineExpansionStructure || structure.step() != net.minecraft.world.level.levelgen.GenerationStep.Decoration.SURFACE_STRUCTURES) {
            return false;
        }
        Optional<WindShrineStructure> shrine = shrine(registryAccess);
        if (shrine.isEmpty()) {
            return false;
        }
        Resolver resolver = new Resolver(registryAccess, generator, randomState, templates, seed, heightAccessor);
        int reachChunks = Math.floorDiv(shrine.get().maxDistanceFromCenter + RESERVED_MARGIN, 16) + 2;
        for (ChunkPos candidate : candidates(registryAccess, seed, chunkPos, reachChunks)) {
            Optional<Plan> plan = plan(resolver, candidate);
            if (plan.isPresent()) {
                BoundingBox hull = plan.get().hull();
                if (chunkPos.getMaxBlockX() >= hull.minX() - RESERVED_MARGIN && chunkPos.getMinBlockX() <= hull.maxX() + RESERVED_MARGIN
                        && chunkPos.getMaxBlockZ() >= hull.minZ() - RESERVED_MARGIN && chunkPos.getMinBlockZ() <= hull.maxZ() + RESERVED_MARGIN) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Optional<Plan> plan(Resolver resolver, ChunkPos candidate) {
        Map<Long, Optional<Plan>> partial = PARTIAL.get();
        Optional<Plan> inProgress = partial.get(candidate.toLong());
        if (inProgress != null) {
            return inProgress;
        }
        Map<Long, Optional<Plan>> cache = cacheFor(resolver.seed);
        Optional<Plan> cached = cache.get(candidate.toLong());
        if (cached != null) {
            return cached;
        }
        synchronized (lockFor(resolver.seed, candidate)) {
            cached = cache.get(candidate.toLong());
            if (cached != null) {
                return cached;
            }
            Optional<Plan> plan = Optional.empty();
            try {
                Optional<WindShrineStructure> shrine = shrine(resolver.registryAccess());
                if (shrine.isPresent()) {
                    StructureStart start = resolver.generate(shrine.get(), candidate);
                    if (start.isValid()) {
                        List<Optional<StructureStart>> wings = new ArrayList<>();
                        Plan current = planFor(resolver, shrine.get(), candidate, start, wings);
                        partial.put(candidate.toLong(), Optional.of(current));
                        Registry<Structure> structures = resolver.registryAccess().registryOrThrow(Registries.STRUCTURE);
                        for (int door = 0; door < WINGS.size(); door++) {
                            Structure wing = structures.get(WINGS.get(door));
                            Optional<Door> exit = current.door(door);
                            Optional<StructureStart> wingStart = Optional.empty();
                            if (wing != null && exit.isPresent()) {
                                StructureStart generated = resolver.generate(wing, new ChunkPos(exit.get().target()));
                                wingStart = generated.isValid() ? Optional.of(generated) : Optional.empty();
                            }
                            wings.add(wingStart);
                            current = planFor(resolver, shrine.get(), candidate, start, wings);
                            partial.put(candidate.toLong(), Optional.of(current));
                        }
                        plan = Optional.of(current);
                        if (LOGGER.isDebugEnabled()) {
                            StringBuilder summary = new StringBuilder();
                            for (int door = 0; door < WINGS.size(); door++) {
                                summary.append(' ').append((char) ('a' + door)).append('=');
                                if (current.door(door).isEmpty()) {
                                    summary.append("no-door");
                                } else {
                                    summary.append(wings.get(door).map(w -> String.valueOf(w.getPieces().size())).orElse("failed"));
                                }
                            }
                            LOGGER.debug("Wind Shrine plan for chunk {}: ring {} pieces, wings:{}", candidate, start.getPieces().size(), summary);
                        }
                    }
                }
            } finally {
                partial.remove(candidate.toLong());
            }
            cache.put(candidate.toLong(), plan);
            return plan;
        }
    }

    private static final ThreadLocal<Map<Long, Optional<Plan>>> PARTIAL = ThreadLocal.withInitial(java.util.HashMap::new);
    private static final Map<Long, Object> LOCKS = new java.util.concurrent.ConcurrentHashMap<>();

    private static Object lockFor(long seed, ChunkPos candidate) {
        return LOCKS.computeIfAbsent(seed * 31L + candidate.toLong(), key -> new Object());
    }

    private static Plan planFor(Resolver resolver, WindShrineStructure shrine, ChunkPos chunk, StructureStart start, List<Optional<StructureStart>> wings) {
        Map<Long, Footprint> byBox = new LinkedHashMap<>();
        List<Footprint> stairs = new ArrayList<>();
        List<BoundingBox> parentBoxes = new ArrayList<>();
        List<List<BoundingBox>> wingBoxes = new ArrayList<>();
        BoundingBox hull = null;
        List<StructureStart> all = new ArrayList<>();
        all.add(start);
        for (Optional<StructureStart> wing : wings) {
            List<BoundingBox> boxes = new ArrayList<>();
            wing.ifPresent(w -> {
                all.add(w);
                w.getPieces().forEach(piece -> boxes.add(piece.getBoundingBox()));
            });
            wingBoxes.add(List.copyOf(boxes));
        }
        List<BoundingBox> allBoxes = new ArrayList<>();
        for (StructureStart current : all) {
            for (StructurePiece piece : current.getPieces()) {
                allBoxes.add(piece.getBoundingBox());
            }
        }
        for (StructureStart current : all) {
            for (StructurePiece piece : current.getPieces()) {
                if (!(piece instanceof PoolElementStructurePiece poolPiece)) {
                    continue;
                }
                BoundingBox box = piece.getBoundingBox();
                if (current == start) {
                    parentBoxes.add(box);
                }
                if (box.getXSpan() <= 3 && box.getZSpan() <= 3 || box.getXSpan() <= 2 || box.getZSpan() <= 2) {
                    continue;
                }
                if (restsOnAnotherPiece(box, allBoxes)) {
                    continue;
                }
                long key = ((long) box.minX() & 0xFFFFL) | (((long) box.minZ() & 0xFFFFL) << 16) | (((long) box.maxX() & 0xFFFFL) << 32) | (((long) box.maxZ() & 0xFFFFL) << 48);
                Footprint existing = byBox.get(key);
                if (existing == null || box.minY() < existing.floorY) {
                    Footprint footprint = new Footprint(box.minX(), box.minZ(), box.maxX(), box.maxZ(), box.minY());
                    footprint.height = box.getYSpan();
                    byBox.put(key, footprint);
                    if (poolPiece.getElement().toString().contains("stairs")) {
                        footprint.alongX = box.getXSpan() > box.getZSpan();
                        stairs.add(footprint);
                        footprint.stairs = true;
                    }
                }
                hull = hull == null ? new BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()) : hull.encapsulate(box);
            }
        }
        List<Footprint> footprints = List.copyOf(byBox.values());
        for (Footprint stair : stairs) {
            slopeStairs(stair, footprints);
        }
        if (hull == null) {
            hull = start.getBoundingBox();
        }
        BlockPos center = WindShrineExits.center(start.getPieces());
        List<Optional<Door>> doors = WindShrineExits.doors(start.getPieces(), resolver.templates, resolver.seed, center);
        return new Plan(resolver.seed, chunk, shrine.summitY, shrine.slopeRadius, center, doors, List.copyOf(parentBoxes), List.copyOf(wingBoxes), footprints, hull);
    }

    private static boolean restsOnAnotherPiece(BoundingBox box, List<BoundingBox> all) {
        for (BoundingBox other : all) {
            if (other == box || other.getXSpan() <= 2 || other.getZSpan() <= 2) {
                continue;
            }
            boolean below = other.maxY() + 1 >= box.minY() - 1 && other.maxY() + 1 <= box.minY() + 1 && other.minY() < box.minY();
            if (below && Math.min(box.maxX(), other.maxX()) >= Math.max(box.minX(), other.minX()) && Math.min(box.maxZ(), other.maxZ()) >= Math.max(box.minZ(), other.minZ())) {
                return true;
            }
        }
        return false;
    }

    private static void slopeStairs(Footprint stair, List<Footprint> footprints) {
        int bottom = stair.floorY;
        int top = stair.floorY + Math.max(0, stair.height - 11);
        int atMin = Integer.MIN_VALUE;
        int atMax = Integer.MIN_VALUE;
        for (Footprint other : footprints) {
            if (other == stair) {
                continue;
            }
            int level = Math.abs(other.floorY - top) <= 2 ? top : Math.abs(other.floorY - bottom) <= 2 ? bottom : Integer.MIN_VALUE;
            if (level == Integer.MIN_VALUE) {
                continue;
            }
            if (stair.touchesEnd(other, true)) {
                atMin = Math.max(atMin, level);
            }
            if (stair.touchesEnd(other, false)) {
                atMax = Math.max(atMax, level);
            }
        }
        if (atMin == Integer.MIN_VALUE && atMax == Integer.MIN_VALUE) {
            return;
        }
        if (atMin == Integer.MIN_VALUE) {
            atMin = atMax == top ? bottom : top;
        }
        if (atMax == Integer.MIN_VALUE) {
            atMax = atMin == top ? bottom : top;
        }
        stair.floorAtMin = atMin;
        stair.floorAtMax = atMax;
        stair.sloped = true;
    }

    private static Map<Long, Optional<Plan>> cacheFor(long seed) {
        synchronized (CACHES) {
            Map<Long, Optional<Plan>> cache = CACHES.get(seed);
            if (cache == null) {
                if (CACHES.size() >= 4) {
                    CACHES.remove(CACHES.keySet().iterator().next());
                }
                cache = Collections.synchronizedMap(new LinkedHashMap<>(128, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Long, Optional<Plan>> eldest) {
                        return this.size() > CACHE_SIZE;
                    }
                });
                CACHES.put(seed, cache);
            }
            return cache;
        }
    }
}
