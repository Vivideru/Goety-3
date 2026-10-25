package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModCauldronInteraction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class EndMineralCauldronBlock extends AbstractCauldronBlock {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;

    public EndMineralCauldronBlock(Properties p_153498_) {
        super(p_153498_, ModCauldronInteraction.MINERAL);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> codec() {
        return ModBlockCodecs.singleton(this);
    }

    protected double getContentHeight(BlockState p_153528_) {
        return (6.0D + (double) p_153528_.getValue(LEVEL) * 3.0D) / 16.0D;
    }

    public boolean isFull(BlockState p_153555_) {
        return p_153555_.getValue(LEVEL) == 3;
    }

    public int getAnalogOutputSignal(BlockState p_153530_, Level p_153531_, BlockPos p_153532_) {
        return p_153530_.getValue(LEVEL);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153549_) {
        p_153549_.add(LEVEL);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(Items.CAULDRON);
    }
}
