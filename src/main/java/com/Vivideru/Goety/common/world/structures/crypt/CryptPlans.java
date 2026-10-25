package com.Vivideru.Goety.common.world.structures.crypt;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.world.structures.StructureTerrain;
import com.Vivideru.Goety.common.world.structures.WindShrinePlans;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Re-plans a crypt deterministically so its wings find the door they attach to. The wings start at the open doors of the
 * main dungeon's Libraries, on level 1; there is no deeper level.
 */
public final class CryptPlans {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "crypt");
    public static final int DOORS = 4;
    /**
     * Level 1 wings, one per Library of the main dungeon: the Skull Lord chamber behind the first, the Vault behind the
     * second. The stairs and deep wings, which led down to a second level, are retired.
     */
    public static final List<ResourceLocation> LEVEL_ONE = List.of(
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "crypt_boss"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "crypt_vault"));

    /** The crypt and any of its wings, retired ones included, so the crypts of older worlds are still recognised. */
    public static boolean isPart(ResourceLocation id) {
        if (id.equals(ID)) {
            return true;
        }
        for (net.minecraft.resources.ResourceKey<Structure> wing : com.Vivideru.Goety.common.world.structures.VivideruStructures.CRYPT_WINGS) {
            if (wing.location().equals(id)) {
                return true;
            }
        }
        return false;
    }
    private static final int CACHE_SIZE = 256;
    private static final Map<CacheKey, Map<Long, Optional<Plan>>> CACHES = new LinkedHashMap<>();
    private static final ThreadLocal<Map<Long, Optional<Plan>>> PARTIAL = ThreadLocal.withInitial(java.util.HashMap::new);
    private static final Map<Long, Object> LOCKS = new java.util.concurrent.ConcurrentHashMap<>();

    private record CacheKey(ChunkGenerator generator, long seed) {
    }

    public record Plan(long seed, ChunkPos chunk, BlockPos towerCenter, List<BoundingBox> mainBoxes, List<WindShrinePlans.Door> doors0,
                       List<List<BoundingBox>> wing1Boxes, List<Optional<WindShrinePlans.Door>> doors1, List<List<BoundingBox>> wing2Boxes) {
        /** The door a wing of the given level and index starts from. */
        public Optional<WindShrinePlans.Door> door(int level, int index) {
            if (level == 1) {
                return index < this.doors0.size() ? Optional.of(this.doors0.get(index)) : Optional.empty();
            }
            return index < this.doors1.size() ? this.doors1.get(index) : Optional.empty();
        }

        /** Every piece of the whole crypt: the main dungeon and all its wings. */
        public List<BoundingBox> allBoxes() {
            List<BoundingBox> result = new ArrayList<>(this.mainBoxes);
            for (List<BoundingBox> wing : this.wing1Boxes) {
                result.addAll(wing);
            }
            for (List<BoundingBox> wing : this.wing2Boxes) {
                result.addAll(wing);
            }
            return result;
        }

        /** Everything planned before the wing of the given level and index. */
        public List<BoundingBox> obstaclesFor(int level, int index) {
            List<BoundingBox> result = new ArrayList<>(this.mainBoxes);
            int firstLevel = level == 1 ? Math.min(index, this.wing1Boxes.size()) : this.wing1Boxes.size();
            for (int i = 0; i < firstLevel; i++) {
                result.addAll(this.wing1Boxes.get(i));
            }
            if (level == 2) {
                for (int i = 0; i < Math.min(index, this.wing2Boxes.size()); i++) {
                    result.addAll(this.wing2Boxes.get(i));
                }
            }
            return result;
        }
    }

    private CryptPlans() {
    }

    /** Other structures keep this many blocks from every piece of the crypt. */
    public static final int KEEP_AWAY = 24;
    /** How far from its start chunk the whole crypt, wings included, can reach. */
    private static final int REACH_BLOCKS = 320;

    /**
     * Whether a structure of another kind must stand down in this chunk because a crypt is planned too close: any piece
     * of the dungeon, wings included, within {@link #KEEP_AWAY} blocks of the chunk denies the start.
     */
    public static boolean reserves(Structure candidate, RegistryAccess registryAccess, net.minecraft.world.level.chunk.ChunkGenerator generator,
                                   net.minecraft.world.level.levelgen.RandomState randomState, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager templates,
                                   long seed, net.minecraft.world.level.LevelHeightAccessor heightAccessor, ChunkPos chunkPos) {
        if (candidate instanceof CryptStructure || candidate instanceof CryptWingStructure || CryptStructure.classic()) {
            return false;
        }
        int reachChunks = Math.floorDiv(REACH_BLOCKS + KEEP_AWAY, 16) + 1;
        List<ChunkPos> candidates = candidates(registryAccess, seed, chunkPos, reachChunks);
        if (candidates.isEmpty()) {
            return false;
        }
        WindShrinePlans.Resolver resolver = new WindShrinePlans.Resolver(registryAccess, generator, randomState, templates, seed, heightAccessor);
        for (ChunkPos start : candidates) {
            Optional<Plan> plan = plan(resolver, start);
            if (plan.isEmpty()) {
                continue;
            }
            for (BoundingBox box : plan.get().allBoxes()) {
                if (chunkPos.getMaxBlockX() >= box.minX() - KEEP_AWAY && chunkPos.getMinBlockX() <= box.maxX() + KEEP_AWAY
                        && chunkPos.getMaxBlockZ() >= box.minZ() - KEEP_AWAY && chunkPos.getMinBlockZ() <= box.maxZ() + KEEP_AWAY) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Structure {} in chunk {} gives way to the crypt planned at chunk {}", registryAccess.registryOrThrow(Registries.STRUCTURE).getKey(candidate), chunkPos, start);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static Optional<Plan> withDoorIn(WindShrinePlans.Resolver resolver, ChunkPos chunkPos, int level, int door, int searchChunks) {
        for (ChunkPos candidate : candidates(resolver.registryAccess(), resolver.seed(), chunkPos, searchChunks)) {
            Optional<Plan> plan = plan(resolver, candidate);
            if (plan.isEmpty()) {
                continue;
            }
            Optional<WindShrinePlans.Door> found = plan.get().door(level, door);
            if (found.isPresent() && new ChunkPos(found.get().target()).equals(chunkPos)) {
                return plan;
            }
        }
        return Optional.empty();
    }

    /** Chunks around the given one where the crypt structure set may start a crypt. */
    public static List<ChunkPos> candidates(RegistryAccess registryAccess, long seed, ChunkPos around, int searchChunks) {
        Optional<RandomSpreadStructurePlacement> spread = spread(registryAccess);
        if (spread.isEmpty()) {
            return List.of();
        }
        int spacing = spread.get().spacing();
        Set<Long> visited = new HashSet<>();
        List<ChunkPos> result = new ArrayList<>();
        ChunkGeneratorStructureState state = StructureTerrain.structureState();
        for (int rx = Math.floorDiv(around.x - searchChunks, spacing); rx <= Math.floorDiv(around.x + searchChunks, spacing); rx++) {
            for (int rz = Math.floorDiv(around.z - searchChunks, spacing); rz <= Math.floorDiv(around.z + searchChunks, spacing); rz++) {
                ChunkPos candidate = spread.get().getPotentialStructureChunk(seed, rx * spacing, rz * spacing);
                if (candidate.getChessboardDistance(around) > searchChunks || !visited.add(candidate.toLong())) {
                    continue;
                }
                if (state != null && !spread.get().isStructureChunk(state, candidate.x, candidate.z)) {
                    continue;
                }
                result.add(candidate);
            }
        }
        return result;
    }

    public static Optional<RandomSpreadStructurePlacement> spread(RegistryAccess registryAccess) {
        Registry<StructureSet> sets = registryAccess.registryOrThrow(Registries.STRUCTURE_SET);
        StructureSet set = sets.get(ID);
        return set != null && set.placement() instanceof RandomSpreadStructurePlacement spread ? Optional.of(spread) : Optional.empty();
    }

    private static Optional<CryptStructure> crypt(RegistryAccess registryAccess) {
        Structure structure = registryAccess.registryOrThrow(Registries.STRUCTURE).get(ID);
        return structure instanceof CryptStructure crypt ? Optional.of(crypt) : Optional.empty();
    }

    public static Optional<Plan> plan(WindShrinePlans.Resolver resolver, ChunkPos candidate) {
        Map<Long, Optional<Plan>> partial = PARTIAL.get();
        Optional<Plan> inProgress = partial.get(candidate.toLong());
        if (inProgress != null) {
            return inProgress;
        }
        Map<Long, Optional<Plan>> cache = cacheFor(resolver);
        Optional<Plan> cached = cache.get(candidate.toLong());
        if (cached != null) {
            return cached;
        }
        synchronized (LOCKS.computeIfAbsent(resolver.seed() * 31L + candidate.toLong(), key -> new Object())) {
            cached = cache.get(candidate.toLong());
            if (cached != null) {
                return cached;
            }
            Optional<Plan> plan = Optional.empty();
            try {
                Optional<CryptStructure> crypt = crypt(resolver.registryAccess());
                if (crypt.isPresent()) {
                    StructureStart start = resolver.generate(crypt.get(), candidate);
                    if (start.isValid() && !start.getPieces().isEmpty()) {
                        List<BoundingBox> mainBoxes = boxes(start);
                        BlockPos tower = start.getPieces().get(0).getBoundingBox().getCenter();
                        List<WindShrinePlans.Door> doors0 = CryptExits.openDoors(start.getPieces(), resolver.templates(), resolver.seed());
                        List<List<BoundingBox>> wing1 = new ArrayList<>();
                        List<Optional<WindShrinePlans.Door>> doors1 = new ArrayList<>();
                        List<List<BoundingBox>> wing2 = new ArrayList<>();
                        Plan current = new Plan(resolver.seed(), candidate, tower, mainBoxes, doors0, List.copyOf(wing1), List.copyOf(doors1), List.copyOf(wing2));
                        partial.put(candidate.toLong(), Optional.of(current));
                        Registry<Structure> structures = resolver.registryAccess().registryOrThrow(Registries.STRUCTURE);
                        for (int index = 0; index < LEVEL_ONE.size(); index++) {
                            Optional<StructureStart> generated = generateWing(resolver, structures, LEVEL_ONE.get(index), current.door(1, index));
                            wing1.add(generated.map(CryptPlans::boxes).orElse(List.of()));
                            Optional<WindShrinePlans.Door> next = Optional.empty();
                            if (generated.isPresent()) {
                                List<WindShrinePlans.Door> open = CryptExits.openDoors(generated.get().getPieces(), resolver.templates(), resolver.seed());
                                next = open.isEmpty() ? Optional.empty() : Optional.of(open.get(0));
                            }
                            doors1.add(next);
                            current = new Plan(resolver.seed(), candidate, tower, mainBoxes, doors0, List.copyOf(wing1), List.copyOf(doors1), List.copyOf(wing2));
                            partial.put(candidate.toLong(), Optional.of(current));
                        }
                        plan = Optional.of(current);
                        if (LOGGER.isDebugEnabled()) {
                            StringBuilder summary = new StringBuilder();
                            for (int index = 0; index < LEVEL_ONE.size(); index++) {
                                summary.append(' ').append(LEVEL_ONE.get(index).getPath()).append('=').append(index < doors0.size() ? wing1.get(index).size() : -1);
                            }
                            LOGGER.debug("Crypt plan for chunk {}: {} main pieces, {} level-0 doors, wing pieces:{}", candidate, start.getPieces().size(), doors0.size(), summary);
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

    private static Optional<StructureStart> generateWing(WindShrinePlans.Resolver resolver, Registry<Structure> structures, ResourceLocation id, Optional<WindShrinePlans.Door> door) {
        Structure wing = structures.get(id);
        if (wing == null || door.isEmpty()) {
            return Optional.empty();
        }
        StructureStart generated = resolver.generate(wing, new ChunkPos(door.get().target()));
        return generated.isValid() ? Optional.of(generated) : Optional.empty();
    }

    private static List<BoundingBox> boxes(StructureStart start) {
        List<BoundingBox> boxes = new ArrayList<>();
        for (StructurePiece piece : start.getPieces()) {
            boxes.add(piece.getBoundingBox());
        }
        return boxes;
    }

    private static Map<Long, Optional<Plan>> cacheFor(WindShrinePlans.Resolver resolver) {
        CacheKey key = new CacheKey(resolver.generator(), resolver.seed());
        synchronized (CACHES) {
            Map<Long, Optional<Plan>> cache = CACHES.get(key);
            if (cache == null) {
                if (CACHES.size() >= 8) {
                    CACHES.remove(CACHES.keySet().iterator().next());
                }
                cache = Collections.synchronizedMap(new LinkedHashMap<>(64, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Long, Optional<Plan>> eldest) {
                        return this.size() > CACHE_SIZE;
                    }
                });
                CACHES.put(key, cache);
            }
            return cache;
        }
    }
}
