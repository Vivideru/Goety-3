package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.blocks.entities.SculpturedStatueBlockEntity;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SculpturedStatueBlock extends StatueBlock {
    public static final IntegerProperty POSE = ModStateProperties.STATUE_POSE;
    public boolean slim;
    public int type;

    protected SculpturedStatueBlock(Properties p_54120_) {
        super(p_54120_);
    }

    public SculpturedStatueBlock(int type, boolean slim) {
        this(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                .noOcclusion());
        this.type = type;
        this.slim = slim;
        this.registerDefaultState(this.defaultBlockState().setValue(POSE, 0));
    }

    public SculpturedStatueBlock(int type) {
        this(type, false);
    }

    public SculpturedStatueBlock() {
        this(0);
    }

    public BlockEntity newBlockEntity(BlockPos p_151996_, BlockState p_151997_) {
        if (p_151997_.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new SculpturedStatueBlockEntity(p_151996_, p_151997_);
        } else {
            return null;
        }
    }

    public int getStatueType() {
        return this.type;
    }

    public boolean isSlim() {
        return this.slim;
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (itemStack.getItem() instanceof IWand) {
            int pose = (state.getValue(POSE) + 1) % 4;
            BlockPos otherPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            level.setBlockAndUpdate(pos, state.setValue(POSE, pose));
            BlockState otherState = level.getBlockState(otherPos);
            // Both halves carry the pose property, so they must be updated together after a wand interaction.
            if (otherState.is(this)) {
                level.setBlockAndUpdate(otherPos, otherState.setValue(POSE, pose));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ? pState.setValue(POSE, pFacingState.getValue(POSE)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58112_) {
        super.createBlockStateDefinition(p_58112_);
        p_58112_.add(POSE);
    }
}
