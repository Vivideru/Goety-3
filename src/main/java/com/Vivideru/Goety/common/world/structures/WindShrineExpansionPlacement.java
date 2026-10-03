package com.Vivideru.Goety.common.world.structures;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

public class WindShrineExpansionPlacement extends StructurePlacement {
    public static final MapCodec<WindShrineExpansionPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("spacing").forGetter(p -> p.spacing),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("separation").forGetter(p -> p.separation),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(p -> p.shrineSalt),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("search_chunks", 4).forGetter(p -> p.searchChunks)
    ).apply(instance, WindShrineExpansionPlacement::new));

    private final int spacing;
    private final int separation;
    private final int searchChunks;
    private final int shrineSalt;
    private final RandomSpreadStructurePlacement shrineSpread;

    public WindShrineExpansionPlacement(int spacing, int separation, int salt, int searchChunks) {
        super(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1.0F, salt, Optional.empty());
        this.spacing = spacing;
        this.separation = separation;
        this.searchChunks = searchChunks;
        this.shrineSalt = salt;
        this.shrineSpread = new RandomSpreadStructurePlacement(spacing, separation, RandomSpreadType.LINEAR, salt);
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int x, int z) {
        for (int rx = Math.floorDiv(x - this.searchChunks, this.spacing); rx <= Math.floorDiv(x + this.searchChunks, this.spacing); rx++) {
            for (int rz = Math.floorDiv(z - this.searchChunks, this.spacing); rz <= Math.floorDiv(z + this.searchChunks, this.spacing); rz++) {
                ChunkPos candidate = this.shrineSpread.getPotentialStructureChunk(state.getLevelSeed(), rx * this.spacing, rz * this.spacing);
                if (candidate.getChessboardDistance(new ChunkPos(x, z)) <= this.searchChunks) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public StructurePlacementType<?> type() {
        return VivideruStructureTypes.WIND_SHRINE_EXPANSION_PLACEMENT.get();
    }
}
