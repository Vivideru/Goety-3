package com.Vivideru.Goety.common.world.structures.crypt;

import com.Vivideru.Goety.common.world.structures.VivideruStructureTypes;
import com.Vivideru.Goety.common.world.structures.WindShrinePlans;
import com.Vivideru.Goety.mixin.StructurePiecesBuilderAccessor;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

/**
 * The main crypt: the hidden entrance mound on the surface, one or two corridors in each direction and a Library at the end
 * of each, descending to level 1. The far lower door of every Library is left open for a level 1 wing
 * ({@link CryptWingStructure}); the wings re-plan this structure to find their door.
 */
public class CryptStructure extends Structure {
    private static final int SAMPLE_RADIUS = 14;
    static final int ATTEMPTS = 8;
    /** Settings of the previous crypt's structure file. */
    private static final int CLASSIC_START_HEIGHT = -34;
    private static final int CLASSIC_SIZE = 30;
    private static final int CLASSIC_MAX_DISTANCE = 1000;
    private static final int CLASSIC_MAX_START_Y = 63 - 16;
    public static final MapCodec<CryptStructure> CODEC = RecordCodecBuilder.<CryptStructure>mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            StructureTemplatePool.CODEC.optionalFieldOf("classic_start_pool").forGetter(s -> s.classicStartPool),
            Codec.intRange(0, 30).fieldOf("size").forGetter(s -> s.maxDepth),
            Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter),
            Codec.INT.optionalFieldOf("surface_offset", -59).forGetter(s -> s.surfaceOffset),
            Codec.intRange(0, 64).optionalFieldOf("max_slope", 10).forGetter(s -> s.maxSlope),
            Codec.INT.optionalFieldOf("min_surface", 60).forGetter(s -> s.minSurface),
            Codec.INT.optionalFieldOf("max_surface", 65).forGetter(s -> s.maxSurface)
    ).apply(instance, CryptStructure::new));

    public final Holder<StructureTemplatePool> startPool;
    /** The previous crypt: one jigsaw dungeon with the old rooms, used when the classic Crypt is configured. */
    public final Optional<Holder<StructureTemplatePool>> classicStartPool;
    public final int maxDepth;
    public final int maxDistanceFromCenter;
    /** Added to the surface height to get the start piece's ground level, so the tower's soil layer sits flush with the terrain. */
    public final int surfaceOffset;
    public final int maxSlope;
    /** The crypt only settles where the ground at its entrance lies between these heights: lowland, never a mountainside. */
    public final int minSurface;
    public final int maxSurface;

    public CryptStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, Optional<Holder<StructureTemplatePool>> classicStartPool, int maxDepth, int maxDistanceFromCenter, int surfaceOffset, int maxSlope, int minSurface, int maxSurface) {
        super(settings);
        this.minSurface = minSurface;
        this.maxSurface = maxSurface;
        this.startPool = startPool;
        this.classicStartPool = classicStartPool;
        this.maxDepth = maxDepth;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.surfaceOffset = surfaceOffset;
        this.maxSlope = maxSlope;
    }

    public static boolean classic() {
        return com.Vivideru.Goety.common.world.VivideruWorldConfig.classicCrypt();
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        if (classic() && this.classicStartPool.isPresent()) {
            // The previous crypt, as its structure file had it: start 34 blocks under the surface, size 30, only where that lands well below sea level.
            BlockPos classicStart = new BlockPos(chunkPos.getMinBlockX(), CLASSIC_START_HEIGHT, chunkPos.getMinBlockZ());
            return JigsawPlacement.addPieces(context, this.classicStartPool.get(), Optional.empty(), CLASSIC_SIZE, classicStart, false, Optional.of(Heightmap.Types.WORLD_SURFACE_WG), CLASSIC_MAX_DISTANCE, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING)
                    .filter(stub -> stub.position().getY() <= CLASSIC_MAX_START_Y);
        }
        // The entrance sits on the lowest ground near its corner, never on a tree or a knoll: the hill pass covers the rest.
        BlockPos start = new BlockPos(chunkPos.getMinBlockX(), this.groundAround(context, chunkPos) + this.surfaceOffset, chunkPos.getMinBlockZ());
        Optional<GenerationStub> stub = JigsawPlacement.addPieces(context, this.startPool, Optional.empty(), this.maxDepth, start, false, Optional.empty(), this.maxDistanceFromCenter, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING);
        if (stub.isEmpty() || !this.isSiteSuitable(context, stub.get().position())) {
            return Optional.empty();
        }
        GenerationStub s = stub.get();
        // Minecraft checks the biome at the generation point: that is the entrance on the surface, not the start deep below it,
        // where cave biomes (lush caves, or the many of Terralith) would turn down crypts under forests and plains.
        int ground = context.chunkGenerator().getFirstFreeHeight(s.position().getX(), s.position().getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        return Optional.of(new GenerationStub(new BlockPos(s.position().getX(), ground, s.position().getZ()), Either.left(builder -> {
            // The dungeon is assembled up to ATTEMPTS times with different seeds until every direction has its Library.
            List<StructurePiece> best = null;
            CryptPlacementRules.Outcome bestOutcome = CryptPlacementRules.Outcome.NONE;
            for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
                Optional<GenerationStub> retry = attempt == 0 ? Optional.of(s)
                        : JigsawPlacement.addPieces(retryContext(context, attempt), this.startPool, Optional.empty(), this.maxDepth, start, false, Optional.empty(), this.maxDistanceFromCenter, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING);
                if (retry.isEmpty()) {
                    continue;
                }
                StructurePiecesBuilder temp = new StructurePiecesBuilder();
                CryptPlacementRules.beginMain(chunkPos, this.maxDepth, context.heightAccessor().getMinBuildHeight());
                CryptPlacementRules.Outcome outcome;
                try {
                    retry.get().generator().ifLeft(generator -> generator.accept(temp));
                } finally {
                    outcome = CryptPlacementRules.end();
                }
                if (best == null || outcome.betterThan(bestOutcome, CryptPlacementRules.Kind.MAIN)) {
                    best = ((StructurePiecesBuilderAccessor) temp).goety$pieces();
                    bestOutcome = outcome;
                }
                if (outcome.satisfies(CryptPlacementRules.Kind.MAIN)) {
                    break;
                }
            }
            if (best == null) {
                return;
            }
            // The whole dungeon is lifted or lowered so that the shade-stone doorway of the vestibule stands exactly on the ground.
            int lift = doorLift(context, best);
            if (lift != 0) {
                for (StructurePiece piece : best) {
                    piece.move(0, lift, 0);
                }
            }
            CryptExits.openExits(best, context.structureTemplateManager(), context.seed());
            CryptExits.closeOpenDoors(best, context.structureTemplateManager(), context.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.TEMPLATE_POOL), context.seed());
            for (StructurePiece piece : best) {
                builder.addPiece(piece);
            }
        })));
    }

    /** The same site with another seed, so the jigsaw assembly takes a different course. */
    static GenerationContext retryContext(GenerationContext context, int attempt) {
        return new GenerationContext(context.registryAccess(), context.chunkGenerator(), context.biomeSource(), context.randomState(), context.structureTemplateManager(),
                context.seed() + attempt * 7919L, context.chunkPos(), context.heightAccessor(), context.validBiome());
    }

    /** How far the assembled dungeon must move so that the bottom row of the doorway sits on the first free block of the ground there. */
    private static int doorLift(GenerationContext context, List<StructurePiece> pieces) {
        for (StructurePiece piece : pieces) {
            if (!(piece instanceof PoolElementStructurePiece pool) || !CryptMound.isSurfacePiece(piece)) {
                continue;
            }
            List<BlockPos> door = CryptShell.templateBlocks(pool, context.structureTemplateManager(), com.Polarice3.Goety.common.blocks.ModBlocks.SHADE_STONE_BLOCK.get());
            if (door.isEmpty()) {
                return 0;
            }
            BlockPos lowest = door.get(0);
            for (BlockPos pos : door) {
                if (pos.getY() < lowest.getY()) {
                    lowest = pos;
                }
            }
            int ground = context.chunkGenerator().getBaseHeight(lowest.getX(), lowest.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
            return ground - lowest.getY();
        }
        return 0;
    }

    /** The base-terrain height (first free block of the noise column, trees and carved caves ignored) at the start corner. */
    private int groundAround(GenerationContext context, ChunkPos chunkPos) {
        return context.chunkGenerator().getBaseHeight(chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
    }

    /** The tower needs dry, fairly level ground: no water at its centre and no more than {@code maxSlope} blocks of relief across it. */
    private boolean isSiteSuitable(GenerationContext context, BlockPos center) {
        int surface = context.chunkGenerator().getFirstFreeHeight(center.getX(), center.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int floor = context.chunkGenerator().getFirstFreeHeight(center.getX(), center.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        if (surface - floor > 1 || floor < this.minSurface || floor > this.maxSurface) {
            return false;
        }
        int lowest = surface;
        int highest = surface;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                int height = context.chunkGenerator().getFirstFreeHeight(center.getX() + dx * SAMPLE_RADIUS, center.getZ() + dz * SAMPLE_RADIUS, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
                lowest = Math.min(lowest, height);
                highest = Math.max(highest, height);
            }
        }
        return highest - lowest <= this.maxSlope;
    }

    @Override
    public StructureType<?> type() {
        return VivideruStructureTypes.CRYPT.get();
    }
}
