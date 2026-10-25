package com.Vivideru.Goety.common.world.structures.crypt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;
import java.util.Set;

/** After the decoration step, strips the cave and surface growth that features sprinkle inside crypt pieces (vines, lichen, dripstone, roots). */
public final class CryptCleanup {
    private static boolean isCrypt(ResourceLocation id) {
        return CryptPlans.isPart(id);
    }
    private static final Set<Block> GROWTH = Set.of(Blocks.VINE, Blocks.GLOW_LICHEN, Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT, Blocks.HANGING_ROOTS,
            Blocks.SPORE_BLOSSOM, Blocks.POINTED_DRIPSTONE, Blocks.MOSS_CARPET, Blocks.AZALEA, Blocks.FLOWERING_AZALEA, Blocks.SMALL_DRIPLEAF,
            Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM, Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.DEAD_BUSH,
            Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.SNOW);

    private CryptCleanup() {
    }

    public static void apply(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        if (CryptStructure.classic()) {
            return;
        }
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ChunkPos chunkPos = chunk.getPos();
        List<StructureStart> starts = structureManager.startsForStructure(chunkPos, structure -> {
            ResourceLocation id = registry.getKey(structure);
            return id != null && isCrypt(id);
        });
        if (starts.isEmpty()) {
            return;
        }
        BoundingBox chunkBox = new BoundingBox(chunkPos.getMinBlockX(), chunk.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), chunk.getMaxBuildHeight() - 1, chunkPos.getMaxBlockZ());
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockState air = Blocks.AIR.defaultBlockState();
        for (StructureStart start : starts) {
            for (StructurePiece piece : start.getPieces()) {
                if (piece instanceof PoolElementStructurePiece pool && pool.getElement().toString().contains("crypt/entrance")) {
                    continue; // the entrance mound keeps its surface growth: CryptMound dresses it afterwards
                }
                BoundingBox box = piece.getBoundingBox();
                if (!box.intersects(chunkBox)) {
                    continue;
                }
                int minX = Math.max(box.minX(), chunkBox.minX());
                int maxX = Math.min(box.maxX(), chunkBox.maxX());
                int minZ = Math.max(box.minZ(), chunkBox.minZ());
                int maxZ = Math.min(box.maxZ(), chunkBox.maxZ());
                int minY = Math.max(box.minY(), chunkBox.minY());
                int maxY = Math.min(box.maxY(), chunkBox.maxY());
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        for (int y = minY; y <= maxY; y++) {
                            if (GROWTH.contains(chunk.getBlockState(cursor.set(x, y, z)).getBlock())) {
                                chunk.setBlockState(cursor, air, false);
                            }
                        }
                    }
                }
            }
        }
    }
}
