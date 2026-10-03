package com.Vivideru.Goety.compat.grassiergrass;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class GrassierGrassCompat {
    public static final String MOD_ID = "grassiergrass";
    public static final int RED_MOSS_TINT = 0xCC7048;

    private GrassierGrassCompat() {
    }

    public static boolean isRedMossTop(BlockState state) {
        return state.is(ModBlocks.RED_MOSS_BLOCK.get())
                || state.is(ModBlocks.RED_MOSS_DIRT.get())
                || state.is(ModBlocks.RED_MOSS_SILTSTONE.get())
                || state.is(ModBlocks.RED_MOSS_HIGHROCK.get());
    }

    public static boolean isGrassLike(BlockState state, Block block) {
        return state.is(block) || (block == Blocks.GRASS_BLOCK && isRedMossTop(state));
    }

    public static boolean standsOnRedMoss(BlockAndTintGetter level, BlockPos pos) {
        return isRedMossTop(level.getBlockState(pos)) || isRedMossTop(level.getBlockState(pos.below()));
    }
}
