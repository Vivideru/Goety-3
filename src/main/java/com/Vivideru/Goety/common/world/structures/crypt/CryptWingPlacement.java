package com.Vivideru.Goety.common.world.structures.crypt;

import com.Vivideru.Goety.common.world.structures.VivideruStructureTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

/** Marks every chunk within {@code search_chunks} of a chunk where the crypt structure set may start a crypt; the wing itself then checks for its door. */
public class CryptWingPlacement extends StructurePlacement {
    public static final MapCodec<CryptWingPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(p -> p.wingSalt),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("search_chunks", 9).forGetter(p -> p.searchChunks)
    ).apply(instance, CryptWingPlacement::new));

    private final int wingSalt;
    private final int searchChunks;

    public CryptWingPlacement(int salt, int searchChunks) {
        super(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1.0F, salt, Optional.empty());
        this.wingSalt = salt;
        this.searchChunks = searchChunks;
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int x, int z) {
        for (Holder<StructureSet> set : state.possibleStructureSets()) {
            if (!set.is(CryptPlans.ID) || !(set.value().placement() instanceof RandomSpreadStructurePlacement spread)) {
                continue;
            }
            int spacing = spread.spacing();
            ChunkPos here = new ChunkPos(x, z);
            for (int rx = Math.floorDiv(x - this.searchChunks, spacing); rx <= Math.floorDiv(x + this.searchChunks, spacing); rx++) {
                for (int rz = Math.floorDiv(z - this.searchChunks, spacing); rz <= Math.floorDiv(z + this.searchChunks, spacing); rz++) {
                    ChunkPos candidate = spread.getPotentialStructureChunk(state.getLevelSeed(), rx * spacing, rz * spacing);
                    if (candidate.getChessboardDistance(here) <= this.searchChunks && spread.isStructureChunk(state, candidate.x, candidate.z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public StructurePlacementType<?> type() {
        return VivideruStructureTypes.CRYPT_WING_PLACEMENT.get();
    }
}
