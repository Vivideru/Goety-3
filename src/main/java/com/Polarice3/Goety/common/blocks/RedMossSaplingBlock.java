package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;

public class RedMossSaplingBlock extends SaplingBlock {
    public RedMossSaplingBlock(TreeGrower p_55978_, Properties p_55979_) {
        super(p_55978_, p_55979_);
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return state.is(ModTags.Blocks.RED_MOSS_PLANTABLES) || super.mayPlaceOn(state, world, pos);
    }

    public TriState canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, BlockState plant) {
        // NeoForge 1.21 switched soil support decisions to TriState so custom support can defer to vanilla when not matched.
        return state.is(ModTags.Blocks.RED_MOSS_PLANTABLES) ? TriState.TRUE : super.canSustainPlant(state, world, pos, facing, plant);
    }
}
