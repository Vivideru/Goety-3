package com.Vivideru.Goety.common.world.structures;

import com.Vivideru.Goety.mixin.StructurePiecesBuilderAccessor;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Arrays;
import java.util.Optional;

public class WindShrineStructure extends Structure {
    /** Jigsaw size and reach of the classic shrine, as in its original structure file. */
    private static final int CLASSIC_SIZE = 4;
    private static final int CLASSIC_MAX_DISTANCE = 80;
    public static final MapCodec<WindShrineStructure> CODEC = RecordCodecBuilder.<WindShrineStructure>mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            StructureTemplatePool.CODEC.optionalFieldOf("classic_start_pool").forGetter(s -> s.classicStartPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(s -> s.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(s -> s.maxDepth),
            Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter),
            Codec.INT.fieldOf("summit_y").forGetter(s -> s.summitY),
            Codec.intRange(8, 128).optionalFieldOf("sample_radius", 40).forGetter(s -> s.sampleRadius),
            Codec.INT.optionalFieldOf("min_surface", 100).forGetter(s -> s.minSurface),
            Codec.intRange(0, 256).optionalFieldOf("max_lift", 140).forGetter(s -> s.maxLift),
            Codec.intRange(0, 256).optionalFieldOf("max_cut", 48).forGetter(s -> s.maxCut),
            Codec.intRange(16, 192).optionalFieldOf("slope_radius", 128).forGetter(s -> s.slopeRadius)
    ).apply(instance, WindShrineStructure::new));

    public final Holder<StructureTemplatePool> startPool;
    /** The pre-mountain shrine: a plain jigsaw build used when the classic Wind Shrine is configured. */
    public final Optional<Holder<StructureTemplatePool>> classicStartPool;
    public final Optional<ResourceLocation> startJigsawName;
    public final int maxDepth;
    public final int maxDistanceFromCenter;
    public final int summitY;
    public final int sampleRadius;
    public final int minSurface;
    public final int maxLift;
    public final int maxCut;
    public final int slopeRadius;

    public WindShrineStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, Optional<Holder<StructureTemplatePool>> classicStartPool, Optional<ResourceLocation> startJigsawName, int maxDepth, int maxDistanceFromCenter, int summitY, int sampleRadius, int minSurface, int maxLift, int maxCut, int slopeRadius) {
        super(settings);
        this.startPool = startPool;
        this.classicStartPool = classicStartPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.summitY = summitY;
        this.sampleRadius = sampleRadius;
        this.minSurface = minSurface;
        this.maxLift = maxLift;
        this.maxCut = maxCut;
        this.slopeRadius = slopeRadius;
    }

    /** The classic shrine sits on the terrain like the original data-driven structure did: beard_box adaptation instead of our mountain. */
    @Override
    public net.minecraft.world.level.levelgen.structure.TerrainAdjustment terrainAdaptation() {
        return this.isClassic() ? net.minecraft.world.level.levelgen.structure.TerrainAdjustment.BEARD_BOX : super.terrainAdaptation();
    }

    public boolean isClassic() {
        return this.classicStartPool.isPresent() && com.Vivideru.Goety.common.world.VivideruWorldConfig.classicWindShrine();
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        if (this.isClassic()) {
            BlockPos start = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
            return JigsawPlacement.addPieces(context, this.classicStartPool.get(), Optional.empty(), CLASSIC_SIZE, start, true, Optional.of(Heightmap.Types.WORLD_SURFACE_WG), CLASSIC_MAX_DISTANCE, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING);
        }
        if (!this.isSiteSuitable(context)) {
            return Optional.empty();
        }
        BlockPos start = new BlockPos(chunkPos.getMinBlockX(), this.summitY, chunkPos.getMinBlockZ());
        Optional<GenerationStub> stub = JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName, this.maxDepth, start, false, Optional.empty(), this.maxDistanceFromCenter, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING);
        return stub.map(s -> new GenerationStub(s.position(), Either.left(builder -> {
            WindShrinePlacementRules.beginShrine(this.summitY, this.maxDepth, context.chunkPos());
            try {
                s.generator().ifLeft(generator -> generator.accept(builder));
            } finally {
                WindShrinePlacementRules.end();
            }
            List<StructurePiece> pieces = ((StructurePiecesBuilderAccessor) builder).goety$pieces();
            WindShrineDeadEnds.prune(pieces, context.structureTemplateManager(), context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL), context.seed());
            WindShrineExits.openExits(pieces, context.structureTemplateManager(), context.seed(), WindShrineExits.center(pieces));
        })));
    }

    private boolean isSiteSuitable(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int centerX = chunkPos.getMiddleBlockX();
        int centerZ = chunkPos.getMiddleBlockZ();
        int step = Math.max(1, this.sampleRadius / 2);
        int[] samples = new int[25];
        int i = 0;
        int highest = Integer.MIN_VALUE;
        // The site is judged on the natural land, before the shrine's own mountain raises it.
        WindShrineBlend.suppress();
        try {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    int height = context.chunkGenerator().getBaseHeight(centerX + dx * step, centerZ + dz * step, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()) - 1;
                    samples[i++] = height;
                    highest = Math.max(highest, height);
                }
            }
        } finally {
            WindShrineBlend.resume();
        }
        Arrays.sort(samples);
        int median = samples[samples.length / 2];
        int groundY = this.summitY - 1;
        if (median < this.minSurface) {
            return false;
        }
        if (groundY - median > this.maxLift) {
            return false;
        }
        return highest - groundY <= this.maxCut;
    }

    @Override
    public StructureType<?> type() {
        return VivideruStructureTypes.WIND_SHRINE.get();
    }
}
