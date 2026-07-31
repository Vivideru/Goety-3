package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.blocks.entities.VoidSpawnerBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawnerState;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class VoidSpawnerBlock extends BaseEntityBlock {
    public static final EnumProperty<VoidSpawnerState> STATE = ModStateProperties.VOID_SPAWNER_STATE;

    public VoidSpawnerBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .lightLevel(l -> l.getValue(VoidSpawnerBlock.STATE).lightLevel())
                .strength(50.0F)
                .sound(SoundType.METAL)
                .isViewBlocking(ModBlocks::never)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE, VoidSpawnerState.INACTIVE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        // The DeferredHolder is still unbound while the block is being constructed, so the codec must close over this instance.
        return ModBlockCodecs.singleton(this);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pLevel instanceof ServerLevel) {
            if (tileentity instanceof VoidSpawnerBlockEntity voidSpawnerBlock) {
                ItemStack itemstack = pPlayer.getItemInHand(pHand);
                if (itemstack.getItem() instanceof SpawnEggItem eggItem) {
                    EntityType<?> entitytype1 = eggItem.getType(itemstack);
                    voidSpawnerBlock.setEntityId(entitytype1, pLevel.getRandom());
                    voidSpawnerBlock.setChanged();
                    pLevel.sendBlockUpdated(pPos, pState, pState, 3);
                    pLevel.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, pPos);
                    itemstack.shrink(1);
                    return ItemInteractionResult.CONSUME;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new VoidSpawnerBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return level instanceof ServerLevel serverLevel
                ? createTickerHelper(type, ModBlockEntities.VOID_SPAWNER.get(), (level1, blockPos, state, block) -> block.getVoidSpawner().tickServer(serverLevel, blockPos))
                : createTickerHelper(type, ModBlockEntities.VOID_SPAWNER.get(), (level1, blockPos, state, block) -> block.getVoidSpawner().tickClient(level1, blockPos));
    }

    @Override
    public void appendHoverText(ItemStack p_255714_, Item.TooltipContext p_255801_, List<Component> p_255708_, TooltipFlag p_255667_) {
        super.appendHoverText(p_255714_, p_255801_, p_255708_, p_255667_);
        Optional<Component> optional = this.getSpawnEntityDisplayName(p_255714_);
        if (optional.isPresent()) {
            p_255708_.add(optional.get());
        } else {
            p_255708_.add(CommonComponents.EMPTY);
            p_255708_.add(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
            p_255708_.add(CommonComponents.space().append(Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
        }

    }

    private Optional<Component> getSpawnEntityDisplayName(ItemStack p_256057_) {
        // Block entity data lives in the BLOCK_ENTITY_DATA component in 1.21 instead of the old BlockItem helper.
        CompoundTag compoundtag = p_256057_.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        if (compoundtag.contains("SpawnData", 10)) {
            String s = compoundtag.getCompound("SpawnData").getCompound("entity").getString("id");
            ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
            if (resourcelocation != null) {
                return BuiltInRegistries.ENTITY_TYPE.getOptional(resourcelocation).map((p_255782_) -> {
                    return Component.translatable(p_255782_.getDescriptionId()).withStyle(ChatFormatting.GRAY);
                });
            }
        }

        return Optional.empty();
    }
}
