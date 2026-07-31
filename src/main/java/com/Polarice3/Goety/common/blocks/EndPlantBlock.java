package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.SpecialPlantable;

public class EndPlantBlock extends BushBlock implements IShearable {
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public EndPlantBlock(Properties properties) {
      super(properties);
   }

   @Override
   protected MapCodec<? extends BushBlock> codec() {
      return ModBlockCodecs.singleton(this);
   }

   public VoxelShape getShape(BlockState p_52419_, BlockGetter p_52420_, BlockPos p_52421_, CollisionContext p_52422_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
      return state.is(ModTags.Blocks.END_PLANTABLES) || state.isSolidRender(world, pos);
   }

   public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, SpecialPlantable plantable) {
      return state.is(ModTags.Blocks.END_PLANTABLES) || state.isSolidRender(world, pos);
   }
}
