package com.Vivideru.Goety.common.world.structures;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import com.Polarice3.Goety.Goety;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class WindShrineExpansionStructure extends Structure {
    private static final int LANDING_BEYOND = 6;
    private static final List<ResourceLocation> START_POOLS = List.of(
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion/start_stairs_6"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion/start_stairs_12"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion/start_stairs_steep_6"),
            ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "wind_shrine_expansion/start_stairs_steep_12"));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<WindShrineExpansionStructure> CODEC = RecordCodecBuilder.<WindShrineExpansionStructure>mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            ResourceLocation.CODEC.fieldOf("start_jigsaw_name").forGetter(s -> s.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(s -> s.maxDepth),
            Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter),
            Codec.intRange(0, 7).fieldOf("door").forGetter(s -> s.door),
            Codec.intRange(1, 16).optionalFieldOf("search_chunks", 12).forGetter(s -> s.searchChunks)
    ).apply(instance, WindShrineExpansionStructure::new));

    public final Holder<StructureTemplatePool> startPool;
    public final ResourceLocation startJigsawName;
    public final int maxDepth;
    public final int maxDistanceFromCenter;
    public final int door;
    public final int searchChunks;

    public WindShrineExpansionStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, ResourceLocation startJigsawName, int maxDepth, int maxDistanceFromCenter, int door, int searchChunks) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.door = door;
        this.searchChunks = searchChunks;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        WindShrinePlans.Resolver resolver = WindShrinePlans.Resolver.of(context);
        Optional<WindShrinePlans.Plan> parent = WindShrinePlans.shrineWithDoorIn(resolver, context.chunkPos(), this.door, this.searchChunks);
        if (parent.isEmpty()) {
            return Optional.empty();
        }
        WindShrinePlans.Door door = parent.get().door(this.door).orElseThrow();
        Rotation rotation = Rotation.NONE;
        for (Rotation candidate : Rotation.values()) {
            if (candidate.rotate(Direction.NORTH) == door.facing().getOpposite()) {
                rotation = candidate;
            }
        }
        BlockPos start = door.target().above();
        WindShrinePlans.Plan plan = parent.get();
        Holder<StructureTemplatePool> pool = null;
        List<BoundingBox> obstacles = plan.obstaclesFor(this.door);
        Registry<StructureTemplatePool> pools = context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        List<ResourceLocation> order = new ArrayList<>(START_POOLS);
        java.util.Collections.rotate(order, (int) Math.floorMod(context.seed() + this.door * 31L + context.chunkPos().toLong(), (long) order.size()));
        for (ResourceLocation id : order) {
            Optional<Holder.Reference<StructureTemplatePool>> candidate = pools.getHolder(ResourceKey.create(Registries.TEMPLATE_POOL, id));
            if (candidate.isEmpty()) {
                continue;
            }
            BoundingBox box = startBox(candidate.get().value(), context.structureTemplateManager(), start, rotation);
            if (box == null) {
                continue;
            }
            Direction away = door.facing();
            BoundingBox landing = new BoundingBox(box.minX() - (away.getAxis() == Direction.Axis.Z ? 3 : 0), box.minY() - 2, box.minZ() - (away.getAxis() == Direction.Axis.X ? 3 : 0), box.maxX() + (away.getAxis() == Direction.Axis.Z ? 3 : 0), box.minY() + 12, box.maxZ() + (away.getAxis() == Direction.Axis.X ? 3 : 0));
            if (away.getStepX() > 0) { landing = new BoundingBox(landing.minX(), landing.minY(), landing.minZ(), landing.maxX() + LANDING_BEYOND, landing.maxY(), landing.maxZ()); }
            if (away.getStepX() < 0) { landing = new BoundingBox(landing.minX() - LANDING_BEYOND, landing.minY(), landing.minZ(), landing.maxX(), landing.maxY(), landing.maxZ()); }
            if (away.getStepZ() > 0) { landing = new BoundingBox(landing.minX(), landing.minY(), landing.minZ(), landing.maxX(), landing.maxY(), landing.maxZ() + LANDING_BEYOND); }
            if (away.getStepZ() < 0) { landing = new BoundingBox(landing.minX(), landing.minY(), landing.minZ() - LANDING_BEYOND, landing.maxX(), landing.maxY(), landing.maxZ()); }
            BoundingBox stairBox = box;
            BoundingBox landingBox = landing;
            if (obstacles.stream().noneMatch(o -> o.intersects(stairBox) || o.intersects(landingBox))) {
                pool = candidate.get();
                break;
            }
        }
        if (pool == null) {
            LOGGER.debug("Wind Shrine wing {} skipped: no entrance stair fits at door {} of the shrine in chunk {}", this.door, door.target(), plan.chunk());
            return Optional.empty();
        }
        Optional<GenerationStub> stub;
        WindShrinePlacementRules.forceRotation(rotation);
        try {
            stub = JigsawPlacement.addPieces(context, pool, Optional.of(this.startJigsawName), this.maxDepth, start, false, Optional.empty(), this.maxDistanceFromCenter, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING);
        } finally {
            WindShrinePlacementRules.clearForcedRotation();
        }
        if (stub.isEmpty()) {
            LOGGER.warn("Wind Shrine wing {} could not start at door {} (facing {}) of the shrine in chunk {}", this.door, door.target(), door.facing(), plan.chunk());
        }
        return stub.map(s -> new GenerationStub(s.position(), Either.left(builder -> {
            WindShrinePlacementRules.beginWing(plan, this.door, this.maxDepth);
            try {
                s.generator().ifLeft(generator -> generator.accept(builder));
            } finally {
                WindShrinePlacementRules.end();
            }
            WindShrineDeadEnds.prune(((com.Vivideru.Goety.mixin.StructurePiecesBuilderAccessor) builder).goety$pieces(), context.structureTemplateManager(), pools, context.seed());
        })));
    }

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
        return VivideruStructureTypes.WIND_SHRINE_EXPANSION.get();
    }
}
