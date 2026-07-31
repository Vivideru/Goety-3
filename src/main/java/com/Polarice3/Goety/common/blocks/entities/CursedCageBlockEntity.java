package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class CursedCageBlockEntity extends BlockEntity implements Clearable {
    private ItemStack item = ItemStack.EMPTY;
    private int spinning;

    public CursedCageBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CURSED_CAGE.get(), blockPos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        this.readNetwork(compound, provider);
        super.loadAdditional(compound, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        this.writeNetwork(compound, provider);
        super.saveAdditional(compound, provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack stack) {
        this.item = stack;
        this.setChanged();
    }

    public Player getOwner(){
        CompoundTag tag = this.getCustomTag();
        if (this.item.getItem() == ModItems.SOUL_TRANSFER.get() && tag != null) {
            if (tag.contains("owner")) {
                UUID owner = tag.getUUID("owner");
                return this.level.getPlayerByUUID(owner);
            }
        }
        return null;
    }

    public int getSouls(){
        if (this.level != null) {
            Player player = this.getOwner();
            if (player != null) {
                if (SEHelper.getSEActive(player)) {
                    return SEHelper.getSESouls(player);
                }
            }
            if (this.item.getItem() instanceof ITotem) {
                CompoundTag tag = this.getCustomTag();
                if (tag != null) {
                    return tag.getInt(ITotem.SOULS_AMOUNT);
                }
            }
        }
        return 0;
    }

    public void decreaseSouls(int souls) {
        if (this.item.getItem() instanceof ITotem) {
            CompoundTag tag = this.getCustomTag();
            if (tag != null) {
                int Soulcount = tag.getInt(ITotem.SOULS_AMOUNT);
                if (!this.item.isEmpty()) {
                    if (Soulcount > 0) {
                        Soulcount -= souls;
                        int finalSoulcount = Soulcount;
                        CustomData.update(DataComponents.CUSTOM_DATA, this.item, customTag -> customTag.putInt(ITotem.SOULS_AMOUNT, finalSoulcount));
                        this.generateParticles();
                    }
                }
            }
        }
        if (this.level != null) {
            Player player = this.getOwner();
            if (player != null) {
                if (SEHelper.getSEActive(player)) {
                    int Soulcount = SEHelper.getSESouls(player);
                    if (Soulcount > 0) {
                        SEHelper.decreaseSESouls(player, souls);
                        SEHelper.sendSEUpdatePacket(player);
                        ArcaBlockEntity arcaTile = (ArcaBlockEntity) this.level.getBlockEntity(SEHelper.getArcaBlock(player));
                        if (arcaTile != null) {
                            arcaTile.generateParticles();
                            this.generateParticles();
                        }
                    }
                }
            }
        }
        this.markUpdated();

    }

    private CompoundTag getCustomTag() {
        CustomData customData = this.item.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : null;
    }

    public int getSpinning(){
        return this.spinning;
    }

    public void generateParticles() {
        if (this.getSouls() <= 0){
            return;
        }
        BlockPos blockpos = this.getBlockPos();

        if (this.level != null) {
            if (!this.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                double d0 = (double) blockpos.getX() + this.level.random.nextDouble();
                double d1 = (double) blockpos.getY() + this.level.random.nextDouble();
                double d2 = (double) blockpos.getZ() + this.level.random.nextDouble();
                for (int p = 0; p < 4; ++p) {
                    serverWorld.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                    serverWorld.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0.0D, 5.0E-4D, 0.0D, 5.0E-4D);
                }
            }
            this.spinning = 20;
        }
    }

    public void generateManyParticles(){
        BlockPos blockpos = this.getBlockPos();
        if (this.level != null) {
            if (!this.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                for(int k = 0; k < 20; ++k) {
                    double d9 = (double)blockpos.getX() + 0.5D + (this.level.random.nextDouble() - 0.5D) * 2.0D;
                    double d13 = (double)blockpos.getY() + 0.5D + (this.level.random.nextDouble() - 0.5D) * 2.0D;
                    double d19 = (double)blockpos.getZ() + 0.5D + (this.level.random.nextDouble() - 0.5D) * 2.0D;
                    serverWorld.sendParticles(ParticleTypes.SMOKE, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
                    serverWorld.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
                }
            }
        }

    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.writeNetwork(super.getUpdateTag(provider), provider);
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

    public void readNetwork(CompoundTag tag, HolderLookup.Provider provider) {
        item = ItemStack.parseOptional(provider, tag.getCompound("item"));
    }

    public void readNetwork(CompoundTag tag) {
        if (this.level != null) {
            item = ItemStack.parseOptional(this.level.registryAccess(), tag.getCompound("item"));
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag, HolderLookup.Provider provider) {
        // Minecraft 1.21 rejects encoding ItemStack.EMPTY; an absent/empty child tag still round-trips through parseOptional.
        tag.put("item", item.isEmpty() ? new CompoundTag() : item.save(provider, new CompoundTag()));
        return tag;
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        if (this.level != null) {
            // Minecraft 1.21 rejects encoding ItemStack.EMPTY; an absent/empty child tag still round-trips through parseOptional.
            tag.put("item", item.isEmpty() ? new CompoundTag() : item.save(this.level.registryAccess(), new CompoundTag()));
        }
        return tag;
    }

    public void clearContent() {
        this.setItem(ItemStack.EMPTY);
    }

    public void tick() {
        if (this.spinning > 0){
            --this.spinning;
        }
    }

    public void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}
