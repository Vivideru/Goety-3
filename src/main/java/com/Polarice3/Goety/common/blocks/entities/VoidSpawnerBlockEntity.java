package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.VoidSpawnerBlock;
import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawner;
import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawnerState;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import com.Polarice3.Goety.utils.PlayerDetector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VoidSpawnerBlockEntity extends BlockEntity implements VoidSpawner.StateAccessor{
    private VoidSpawner voidSpawner;

    public VoidSpawnerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.VOID_SPAWNER.get(), blockPos, blockState);
        this.voidSpawner = new VoidSpawner(this, PlayerDetector.NO_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.voidSpawner
                .codec()
                .parse(NbtOps.INSTANCE, compoundTag)
                .resultOrPartial(Goety.LOGGER::error)
                .ifPresent(voidSpawner -> this.voidSpawner = voidSpawner);
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        this.voidSpawner
                .codec()
                .encodeStart(NbtOps.INSTANCE, this.voidSpawner)
                .resultOrPartial(message -> Goety.LOGGER.warn("Failed to encode VoidSpawner {}", message))
                .ifPresent(tag -> compoundTag.merge((CompoundTag)tag));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.voidSpawner.getData().getUpdateTag(this.getBlockState().getValue(VoidSpawnerBlock.STATE));
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public void setEntityId(EntityType<?> entityType, RandomSource randomSource) {
        this.voidSpawner.getData().setEntityId(this.voidSpawner, randomSource, entityType);
        this.setChanged();
    }

    public VoidSpawner getVoidSpawner() {
        return this.voidSpawner;
    }

    @Override
    public VoidSpawnerState getState() {
        return !this.getBlockState().hasProperty(ModStateProperties.VOID_SPAWNER_STATE)
                ? VoidSpawnerState.INACTIVE
                : this.getBlockState().getValue(ModStateProperties.VOID_SPAWNER_STATE);
    }

    @Override
    public void setState(Level level, VoidSpawnerState spawnerState) {
        this.setChanged();
        level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(ModStateProperties.VOID_SPAWNER_STATE, spawnerState));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        if (!pkt.getTag().isEmpty()) {
            this.loadWithComponents(pkt.getTag(), provider);
        }
        super.onDataPacket(net, pkt, provider);
    }

    @Override
    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }

    }
}
