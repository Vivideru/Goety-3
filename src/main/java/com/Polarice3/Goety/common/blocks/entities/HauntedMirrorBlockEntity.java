package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.HauntedMirrorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class HauntedMirrorBlockEntity extends BlockEntity {

    public HauntedMirrorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.HAUNTED_MIRROR.get(), p_155229_, p_155230_);
    }

    public void tick() {
        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(HauntedMirrorBlock.LIT, this.hasEntitySight()), 3);
        }
    }

    public boolean hasEntitySight(){
        if (this.level == null){
            return false;
        }
        return !this.level.getEntitiesOfClass(LivingEntity.class, this.getSightBBox(this.getBlockPos(), this.getBlockState())).isEmpty();
    }

    public AABB getSightBBox(BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.getValue(HauntedMirrorBlock.FACING);
        float minX = -1.0F;
        float minY = 0.0F;
        float minZ = -1.0F;
        float maxX = 1.0F;
        float maxY = 1.0F;
        float maxZ = 1.0F;
        if(direction == Direction.SOUTH) {
            minZ = -4.0F;
        } else if(direction == Direction.NORTH) {
            maxZ = 4.0F;
        } else if(direction == Direction.WEST) {
            minX = -4.0F;
        } else if(direction == Direction.EAST) {
            maxX = 4.0F;
        }

        return new AABB(blockPos.getX() + minX, blockPos.getY() + minY, blockPos.getZ() + minZ, blockPos.getX() + maxX + 1, blockPos.getY() + maxY, blockPos.getZ() + maxZ + 1);
    }
}
