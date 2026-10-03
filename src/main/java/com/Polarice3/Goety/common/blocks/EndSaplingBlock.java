package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.world.features.trees.ChorusTree;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;

public class EndSaplingBlock extends SaplingBlock {
    private final ChorusTree chorusTree;

    public EndSaplingBlock(TreeGrower p_55978_, Properties p_55979_) {
        super(p_55978_, p_55979_);
        this.chorusTree = null;
    }

    public EndSaplingBlock(ChorusTree chorusTree, Properties properties) {
        super(TreeGrower.OAK, properties);
        this.chorusTree = chorusTree;
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (this.chorusTree == null) {
            super.advanceTree(level, pos, state, random);
        } else if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            this.chorusTree.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
        }
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return state.is(ModTags.Blocks.CHORUS_SAPLING_GROW);
    }

    public TriState canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, BlockState plant) {
        return state.is(ModTags.Blocks.CHORUS_SAPLING_GROW) ? TriState.TRUE : super.canSustainPlant(state, world, pos, facing, plant);
    }
}
