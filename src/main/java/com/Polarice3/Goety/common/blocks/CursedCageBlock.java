package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.utils.TotemFinder;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.extensions.IBlockExtension;

import org.jetbrains.annotations.Nullable;

public class CursedCageBlock extends BaseEntityBlock implements IBlockExtension {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public CursedCageBlock() {
        super(Properties.of()
                .mapColor(MapColor.STONE)
                .strength(5.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return ModBlockCodecs.singleton(this);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        // Block entity data is stored directly in a data component in 1.21 instead of inside the old BlockEntityTag wrapper.
        CompoundTag compoundnbt = pStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        if (compoundnbt.contains("item")) {
            pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.TRUE), 2);
        }

    }

    protected ItemInteractionResult useItemOn(ItemStack pClickedStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        // Item-based block interaction replaced the old use hook in 1.21; the cage still only acts from main-hand empty clicks.
        if (pHand == InteractionHand.MAIN_HAND && pPlayer.getMainHandItem().isEmpty()) {
            if (!pState.getValue(POWERED)) {
                if (!TotemFinder.FindTotem(pPlayer).isEmpty() && pState.getBlock() instanceof CursedCageBlock cageBlock) {
                    ItemStack itemStack = TotemFinder.FindTotem(pPlayer);
                    if (!pLevel.isClientSide) {
                        cageBlock.setItem(pLevel, pPos, pState, itemStack);
                    }

                    return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            } else {
                this.dropItem(pLevel, pPos, pPlayer);
                pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.FALSE), 2);
                return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void setItem(Level pLevel, BlockPos pPos, BlockState pState, ItemStack pStack) {
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (tileentity instanceof CursedCageBlockEntity cageBlock) {
            cageBlock.setItem(pStack.split(1));
            pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.TRUE), 2);
        }
    }

    public void dropItem(Level pLevel, BlockPos pPos, @Nullable Player player) {
        if (!pLevel.isClientSide) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof CursedCageBlockEntity cageTileEntity) {
                ItemStack itemstack = cageTileEntity.getItem();
                if (!itemstack.isEmpty()) {
                    ItemStack itemstack1 = itemstack.copy();
                    // 1010 is the vanilla jukebox event in 1.21; use an explicit item-removal sound instead.
                    pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                    cageTileEntity.clearContent();
                    boolean flag = false;
                    if (player != null) {
                        if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                            player.setItemInHand(InteractionHand.MAIN_HAND, itemstack1);
                            pLevel.playSound(null, pPos, SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        } else if (!player.addItem(itemstack1)){
                            flag = true;
                        }
                    } else {
                        flag = true;
                    }
                    if (flag) {
                        float f = 0.7F;
                        double d0 = (double)(pLevel.random.nextFloat() * f) + (double)0.15F;
                        double d1 = (double)(pLevel.random.nextFloat() * f) + (double)0.060000002F + 0.6D;
                        double d2 = (double)(pLevel.random.nextFloat() * f) + (double)0.15F;
                        ItemEntity itementity = new ItemEntity(pLevel, (double)pPos.getX() + d0, (double)pPos.getY() + d1, (double)pPos.getZ() + d2, itemstack1);
                        itementity.setDefaultPickUpDelay();
                        if (pLevel.addFreshEntity(itementity)){
                            pLevel.playSound(null, pPos, SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            this.dropItem(pLevel, pPos, null);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new CursedCageBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof CursedCageBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
