package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ModBlockEntity extends ChunkLoadBlockEntity {

    public ModBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public abstract void readNetwork(CompoundTag compoundNBT);

    public abstract CompoundTag writeNetwork(CompoundTag pCompound);

    public void readNetwork(CompoundTag compoundNBT, HolderLookup.Provider provider) {
        this.readNetwork(compoundNBT);
    }

    public CompoundTag writeNetwork(CompoundTag pCompound, HolderLookup.Provider provider) {
        return this.writeNetwork(pCompound);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        this.readNetwork(pkt.getTag(), provider);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        this.readNetwork(tag, provider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.writeNetwork(super.getUpdateTag(provider), provider);
    }

    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        this.readNetwork(nbt, provider);
        super.loadAdditional(nbt, provider);
    }

    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        this.writeNetwork(compound, provider);
        super.saveAdditional(compound, provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}
