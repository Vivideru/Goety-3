package com.Vivideru.Goety.common.world.structures.crypt;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.world.structures.VivideruStructureTypes;
import com.Vivideru.Goety.common.world.structures.WindShrinePlans;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import com.Vivideru.Goety.mixin.StructurePiecesBuilderAccessor;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A crypt wing: a corridor that starts at an open Library door of the main dungeon and winds away from the tower, on
 * level 1. The boss and vault wings end in their room. Stairs wings (one more Library down) and deep wings (level 2)
 * are retired and never generate.
 */
public class CryptWingStructure extends Structure {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceKey<StructureTemplatePool> CORRIDOR_EXITS = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "crypt/corridor_exits"));

    public enum Kind implements StringRepresentable {
        STAIRS("stairs", CryptPlacementRules.Kind.STAIRS),
        BOSS("boss", CryptPlacementRules.Kind.BOSS),
        VAULT("vault", CryptPlacementRules.Kind.VAULT),
        DEEP("deep", CryptPlacementRules.Kind.DEEP);

        public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);
        private final String name;
        public final CryptPlacementRules.Kind rules;

        Kind(String name, CryptPlacementRules.Kind rules) {
            this.name = name;
            this.rules = rules;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static final MapCodec<CryptWingStructure> CODEC = RecordCodecBuilder.<CryptWingStructure>mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            ResourceLocation.CODEC.fieldOf("start_jigsaw_name").forGetter(s -> s.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(s -> s.maxDepth),
            Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter),
            Codec.intRange(1, 2).fieldOf("level").forGetter(s -> s.level),
            Codec.intRange(0, 3).fieldOf("door").forGetter(s -> s.door),
            Kind.CODEC.fieldOf("kind").forGetter(s -> s.kind),
            Codec.intRange(1, 32).optionalFieldOf("search_chunks", 12).forGetter(s -> s.searchChunks),
            Codec.STRING.optionalFieldOf("terminal").forGetter(s -> s.terminal),
            ResourceLocation.CODEC.optionalFieldOf("exit_pool").forGetter(s -> s.exitPool)
    ).apply(instance, CryptWingStructure::new));

    public final Holder<StructureTemplatePool> startPool;
    public final ResourceLocation startJigsawName;
    public final int maxDepth;
    public final int maxDistanceFromCenter;
    /** 1: starts at a main dungeon door; 2: starts at the door of the level 1 wing with the same index. */
    public final int level;
    public final int door;
    public final Kind kind;
    public final int searchChunks;
    /** Part of the terminal room's template name (boss chamber, vault); the wing must place it exactly once. */
    public final Optional<String> terminal;
    /** Tries for a wing that must place its terminal room (Skull Lord chamber, Vault). */
    private static final int TERMINAL_ATTEMPTS = 48;
    /** Replaces {@code goety:crypt/corridor_exits} while this wing is assembled, so the terminal room is on offer. */
    public final Optional<ResourceLocation> exitPool;

    public CryptWingStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, ResourceLocation startJigsawName, int maxDepth, int maxDistanceFromCenter, int level, int door, Kind kind, int searchChunks, Optional<String> terminal, Optional<ResourceLocation> exitPool) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.level = level;
        this.door = door;
        this.kind = kind;
        this.searchChunks = searchChunks;
        this.terminal = terminal;
        this.exitPool = exitPool;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (CryptStructure.classic() || this.kind == Kind.STAIRS || this.kind == Kind.DEEP) {
            return Optional.empty();
        }
        WindShrinePlans.Resolver resolver = WindShrinePlans.Resolver.of(context);
        Optional<CryptPlans.Plan> parent = CryptPlans.withDoorIn(resolver, context.chunkPos(), this.level, this.door, this.searchChunks);
        if (parent.isEmpty()) {
            return Optional.empty();
        }
        CryptPlans.Plan plan = parent.get();
        WindShrinePlans.Door door = plan.door(this.level, this.door).orElseThrow();
        Rotation rotation = Rotation.NONE;
        for (Rotation candidate : Rotation.values()) {
            if (candidate.rotate(Direction.NORTH) == door.facing().getOpposite()) {
                rotation = candidate;
            }
        }
        BlockPos start = door.target().above();
        List<BoundingBox> obstacles = new ArrayList<>(plan.obstaclesFor(this.level, this.door));
        for (int other = 0; other < CryptPlans.DOORS; other++) {
            if (other != this.door) {
                plan.door(this.level, other).ifPresent(d -> obstacles.add(CryptExits.landingZone(d.target(), d.facing())));
            }
        }
        BoundingBox startBox = this.startBox(this.startPool.value(), context.structureTemplateManager(), start, rotation);
        if (startBox == null || obstacles.stream().anyMatch(o -> o.intersects(startBox))) {
            LOGGER.debug("Crypt wing {} (level {}, door {}) skipped: its first corridor does not fit at {} of the crypt in chunk {}", this.kind.getSerializedName(), this.level, this.door, door.target(), plan.chunk());
            return Optional.empty();
        }
        PoolAliasLookup aliases = PoolAliasLookup.EMPTY;
        if (this.exitPool.isPresent()) {
            ResourceKey<StructureTemplatePool> exits = ResourceKey.create(Registries.TEMPLATE_POOL, this.exitPool.get());
            aliases = key -> key.equals(CORRIDOR_EXITS) ? exits : key;
        }
        Optional<GenerationStub> stub;
        CryptPlacementRules.forceRotation(rotation);
        try {
            stub = JigsawPlacement.addPieces(context, this.startPool, Optional.of(this.startJigsawName), this.maxDepth, start, false, Optional.empty(), this.maxDistanceFromCenter, aliases, DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING);
        } finally {
            CryptPlacementRules.clearForcedRotation();
        }
        if (stub.isEmpty()) {
            LOGGER.warn("Crypt wing {} (level {}, door {}) could not start at {} (facing {}) of the crypt in chunk {}", this.kind.getSerializedName(), this.level, this.door, door.target(), door.facing(), plan.chunk());
        }
        String label = "wing " + this.kind.getSerializedName() + " " + this.level + "/" + this.door;
        Rotation forced = rotation;
        PoolAliasLookup aliasLookup = aliases;
        return stub.map(s -> new GenerationStub(s.position(), Either.left(builder -> {
            // Assembled with different seeds until the wing has what it is for (its Library or its room). A boss or vault
            // wing must always end in its room, which fits about one time in four, so it gets many more tries.
            List<StructurePiece> best = null;
            CryptPlacementRules.Outcome bestOutcome = CryptPlacementRules.Outcome.NONE;
            int attempts = 0;
            int maxAttempts = this.terminal.isPresent() ? TERMINAL_ATTEMPTS : CryptStructure.ATTEMPTS;
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                Optional<GenerationStub> retry;
                if (attempt == 0) {
                    retry = Optional.of(s);
                } else {
                    CryptPlacementRules.forceRotation(forced);
                    try {
                        retry = JigsawPlacement.addPieces(CryptStructure.retryContext(context, attempt), this.startPool, Optional.of(this.startJigsawName), this.maxDepth, start, false, Optional.empty(), this.maxDistanceFromCenter, aliasLookup, DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING);
                    } finally {
                        CryptPlacementRules.clearForcedRotation();
                    }
                }
                if (retry.isEmpty()) {
                    continue;
                }
                attempts++;
                StructurePiecesBuilder temp = new StructurePiecesBuilder();
                CryptPlacementRules.beginWing(label, context.chunkPos(), obstacles, this.kind.rules, this.terminal.orElse(null), this.maxDepth, plan.towerCenter(), door.facing(),
                        this.level, context.heightAccessor().getMinBuildHeight());
                CryptPlacementRules.Outcome outcome;
                try {
                    retry.get().generator().ifLeft(generator -> generator.accept(temp));
                } finally {
                    outcome = CryptPlacementRules.end();
                }
                if (best == null || outcome.betterThan(bestOutcome, this.kind.rules)) {
                    best = ((StructurePiecesBuilderAccessor) temp).goety$pieces();
                    bestOutcome = outcome;
                }
                if (outcome.satisfies(this.kind.rules)) {
                    break;
                }
            }
            if (best == null) {
                return;
            }
            CryptExits.openExits(best, context.structureTemplateManager(), context.seed());
            CryptExits.closeOpenDoors(best, context.structureTemplateManager(), context.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.TEMPLATE_POOL), context.seed());
            for (StructurePiece piece : best) {
                builder.addPiece(piece);
            }
            LOGGER.debug("Crypt {} in chunk {}: {} pieces after {} attempt(s), satisfied={}", label, context.chunkPos(), best.size(), attempts, bestOutcome.satisfies(this.kind.rules));
        })));
    }

    /** Where the first corridor will stand once its start jigsaw is put on {@code start}, mirroring the placer's own arithmetic. */
    private BoundingBox startBox(StructureTemplatePool pool, StructureTemplateManager templates, BlockPos start, Rotation rotation) {
        List<StructurePoolElement> elements = pool.getShuffledTemplates(RandomSource.create(0L));
        if (elements.isEmpty()) {
            return null;
        }
        StructurePoolElement element = elements.get(0);
        for (StructureTemplate.StructureBlockInfo info : element.getShuffledJigsawBlocks(templates, start, rotation, RandomSource.create(0L))) {
            if (info.nbt() != null && this.startJigsawName.toString().equals(info.nbt().getString("name"))) {
                BlockPos origin = start.subtract(info.pos().subtract(start));
                BoundingBox box = element.getBoundingBox(templates, origin, rotation);
                return box.moved(0, start.getY() - (box.minY() + element.getGroundLevelDelta()), 0);
            }
        }
        return null;
    }

    @Override
    public StructureType<?> type() {
        return VivideruStructureTypes.CRYPT_WING.get();
    }
}
