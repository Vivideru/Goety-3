package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.entities.IOwnedBlock;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public abstract class OwnedBlockEntity extends ChunkLoadBlockEntity implements IOwnedBlock {
    private UUID ownerUUID;
    private int ownerID;

    public OwnedBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
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

    public void readNetwork(CompoundTag tag, HolderLookup.Provider provider) {
        this.readNetwork(tag);
    }

    public CompoundTag writeNetwork(CompoundTag tag, HolderLookup.Provider provider) {
        return this.writeNetwork(tag);
    }

    public void readNetwork(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.setOwnerUUID(tag.getUUID("Owner"));
        }
        if (tag.contains("OwnerID")){
            this.setOwnerId(tag.getInt("OwnerID"));
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        if (this.getOwnerUUID() != null) {
            tag.putUUID("Owner", this.getOwnerUUID());
        }
        tag.putInt("OwnerID", this.getOwnerId());
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(@Nullable UUID p_184754_1_) {
        this.ownerUUID = p_184754_1_;
    }

    public int getOwnerId() {
        return this.ownerID;
    }

    public void setOwnerId(int p_184754_1_) {
        this.ownerID = p_184754_1_;
    }

    public void setOwner(LivingEntity livingEntity){
        this.setOwnerUUID(livingEntity.getUUID());
        this.setOwnerId(livingEntity.getId());
    }

    @Nullable
    public LivingEntity getTrueOwner() {
        if (this.level != null) {
            if (!this.level.isClientSide){
                UUID uuid = this.getOwnerUUID();
                return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
            } else {
                int id = this.getOwnerId();
                return id <= -1 ? null : this.level.getEntity(this.getOwnerId()) instanceof LivingEntity living ? living : null;
            }
        } else {
            return null;
        }
    }

    @Nullable
    public Player getPlayer(){
        if (this.level != null) {
            if (!this.level.isClientSide) {
                if (this.getTrueOwner() instanceof Player player) {
                    return player;
                }
            } else {
                if (this.getOwnerUUID() != null) {
                    return this.level.getPlayerByUUID(this.getOwnerUUID());
                }
            }
        }
        return null;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        if (!pkt.getTag().isEmpty()) {
            this.readNetwork(pkt.getTag(), provider);
        }
    }

    public boolean screenView(){
        return true;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        this.readNetwork(tag, provider);
    }
}
