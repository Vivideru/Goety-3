package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.entities.IWaystoneBlock;
import com.Polarice3.Goety.api.magic.GolemType;
import com.Polarice3.Goety.common.blocks.AnimatorBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.HauntedArmorServant;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.magic.construct.SpawnFromBlock;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AnimatorBlockEntity extends BlockEntity implements IWaystoneBlock, Clearable {
    private ItemStack item = ItemStack.EMPTY;
    private CursedCageBlockEntity cursedCageTile;
    private int spinning;
    public boolean showBlock;

    public AnimatorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ANIMATOR.get(), blockPos, blockState);
    }

    public int getSoulCost(){
        if (this.getPosition() != null){
            double distance = this.getBlockPos().distToCenterSqr(this.getPosition().pos().getCenter());
            return (int) (MainConfig.AnimatorCost.get() * distance);
        }
        return 0;
    }

    public AABB getRenderBoundingBox() {
        return AABB.INFINITE;
    }

    public void summonGolem(){
        if (this.level != null) {
            if (this.getPosition() != null) {
                if (this.level.dimension() == this.getPosition().dimension()) {
                    if (this.level.isLoaded(this.getPosition().pos())) {
                        if (this.checkCage() && this.cursedCageTile.getSouls() >= this.getSoulCost()) {
                            ItemStack itemStack = ModItems.ANIMATION_CORE.get().getDefaultInstance();
                            BlockState blockState = this.level.getBlockState(this.getPosition().pos());
                            AABB aabb = new AABB(this.getPosition().pos());
                            List<HauntedArmorStand> list = this.level.getEntitiesOfClass(HauntedArmorStand.class, aabb, ItemHelper::isFullEquipped);
                            Optional<HauntedArmorStand> optional = !list.isEmpty() ? list.stream().findFirst() : Optional.empty();
                            if (optional.isPresent() && SEHelper.hasResearch(this.getOwner(), ResearchList.HAUNTING)){
                                HauntedArmorStand hauntedArmorStand = optional.get();
                                HauntedArmorServant hauntedArmorServant = new HauntedArmorServant(ModEntityType.HAUNTED_ARMOR_SERVANT.get(), this.level);
                                for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                                    hauntedArmorServant.setItemSlot(equipmentSlot, hauntedArmorStand.getItemBySlot(equipmentSlot));
                                    hauntedArmorServant.setGuaranteedDrop(equipmentSlot);
                                }
                                hauntedArmorServant.setPersistenceRequired();
                                hauntedArmorServant.setTrueOwner(this.getOwner());
                                hauntedArmorServant.moveTo(hauntedArmorStand.blockPosition(), hauntedArmorStand.getYRot(), hauntedArmorStand.getXRot());
                                hauntedArmorServant.setLeftHanded(this.getOwner().getMainArm() == HumanoidArm.LEFT);
                                if (this.level.addFreshEntity(hauntedArmorServant)) {
                                    hauntedArmorStand.playSound(ModSounds.SUMMON_SPELL.get());
                                    hauntedArmorStand.showBreakingParticles();
                                    hauntedArmorStand.discard();
                                }
                            } else if (GolemType.getGolemList().containsKey(blockState)) {
                                if (GolemType.getGolemList().get(blockState).spawnServant(this.getOwner(), itemStack, this.level, this.getPosition().pos())) {
                                    this.level.playSound(null, this.getBlockPos(), ModSounds.SUMMON_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    this.level.playSound(null, this.getPosition().pos(), ModSounds.SUMMON_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    this.cursedCageTile.decreaseSouls(this.getSoulCost());
                                    this.generateManyParticles();
                                }
                            } else {
                                if (SpawnFromBlock.spawnServant(this.getOwner(), itemStack, this.level, this.getPosition().pos())) {
                                    this.level.playSound(null, this.getBlockPos(), ModSounds.SUMMON_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    this.level.playSound(null, this.getPosition().pos(), ModSounds.SUMMON_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    this.cursedCageTile.decreaseSouls(this.getSoulCost());
                                    this.generateManyParticles();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack stack) {
        this.item = stack;
        this.markUpdated();
    }

    public Player getOwner(){
        if (this.level != null) {
            if (!this.getItem().isEmpty()) {
                if (this.getItem().getItem() instanceof WaystoneItem) {
                    CompoundTag tag = this.getItemTag();
                    if (tag.contains(WaystoneItem.TAG_OWNER)) {
                        UUID owner = tag.getUUID(WaystoneItem.TAG_OWNER);
                        return this.level.getPlayerByUUID(owner);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public GlobalPos getPosition(){
        if (!this.getItem().isEmpty()) {
            CompoundTag tag = this.getItemTag();
            if (!tag.isEmpty()) {
                return WaystoneItem.getPosition(tag);
            }
        }
        return null;
    }

    public int getSpinning(){
        return this.spinning;
    }

    private CompoundTag getItemTag() {
        return this.getItem().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public void tick() {
        if (this.spinning > 0){
            --this.spinning;
        }
        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(AnimatorBlock.POWERED, this.checkCage() && this.getPosition() != null), 3);
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
                    serverWorld.sendParticles(ParticleTypes.FLAME, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
                }
            }
        }

    }

    private boolean checkCage() {
        if (this.level != null) {
            BlockPos pos = new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() - 1, this.getBlockPos().getZ());
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity tileentity = this.level.getBlockEntity(pos);
                if (tileentity instanceof CursedCageBlockEntity cageBlock) {
                    this.cursedCageTile = cageBlock;
                    return !cursedCageTile.getItem().isEmpty();
                }
            }
        }
        return false;
    }

    public boolean isShowBlock(){
        return this.showBlock;
    }

    public void setShowBlock(boolean showBlock){
        this.showBlock = showBlock;
        this.markUpdated();
    }

    public void markNetworkDirty() {

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
        this.item = ItemStack.parseOptional(provider, tag.getCompound("item"));
        if (tag.contains("showBlock")) {
            this.showBlock = tag.getBoolean("showBlock");
        }
    }

    public void readNetwork(CompoundTag tag) {
        if (this.level != null) {
            this.item = ItemStack.parseOptional(this.level.registryAccess(), tag.getCompound("item"));
        }
        if (tag.contains("showBlock")) {
            this.showBlock = tag.getBoolean("showBlock");
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag, HolderLookup.Provider provider) {
        // Minecraft 1.21 rejects encoding ItemStack.EMPTY; an empty child tag still round-trips through parseOptional.
        tag.put("item", item.isEmpty() ? new CompoundTag() : item.save(provider, new CompoundTag()));
        tag.putBoolean("showBlock", this.showBlock);
        return tag;
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        if (this.level != null) {
            // Minecraft 1.21 rejects encoding ItemStack.EMPTY; an empty child tag still round-trips through parseOptional.
            tag.put("item", item.isEmpty() ? new CompoundTag() : item.save(this.level.registryAccess(), new CompoundTag()));
        }
        tag.putBoolean("showBlock", this.showBlock);
        return tag;
    }

    public void clearContent() {
        this.setItem(ItemStack.EMPTY);
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
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
}
