package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.entities.IWindPowered;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ResonanceCrystalBlock;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import com.Polarice3.Goety.common.items.block.ResonanceBlockItem;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResonanceCrystalBlockEntity extends ModBlockEntity implements IWindPowered {
    public static String GOLEM_LIST = ResonanceBlockItem.GOLEM_LIST;
    public static String BLOCK_LIST = "BlockList";
    public List<BlockPos> blockPosList = new ArrayList<>();
    public List<SquallGolem> squallGolems = new ArrayList<>();
    public List<UUID> uuids = new ArrayList<>();
    public int active;
    private boolean isOn;
    public boolean showBlock;

    public ResonanceCrystalBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.RESONANCE_CRYSTAL.get(), p_155229_, p_155230_);
    }

    @Override
    public boolean shouldChunkLoad() {
        return this.getBlockState().getValue(ResonanceCrystalBlock.POWERED);
    }

    public void tick(){
        if (this.level != null) {
            if (this.active > 0) {
                --this.active;
            }
            boolean powered = this.active > 0;
            if (powered) {
                if (!this.isOn){
                    this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_ON.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.isOn = true;
                } else {
                    if (this.level.getGameTime() % MathHelper.secondsToTicks(6) == 0){
                        this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_LOOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
                if (this.level instanceof ServerLevel world) {
                    this.chunkLoadBlock();
                    BlockPos blockPos = this.getBlockPos();
                    ServerParticleUtil.gatheringBlockParticles(ModParticleTypes.RESONANCE_GATHER.get(), blockPos, world);
                    ServerParticleUtil.windParticle(world, ColorUtil.WHITE, 1.0F + world.random.nextFloat() * 0.5F, 0.0F, -1, Vec3.atBottomCenterOf(blockPos));
                    this.refreshSquallGolems();
                    this.activateGolems();
                }
                if (!this.getBlockPosList().isEmpty()){
                    for (BlockPos blockPos1 : this.getBlockPosList()){
                        BlockEntity blockEntity = this.level.getBlockEntity(blockPos1);
                        if (blockEntity instanceof ResonanceCrystalBlockEntity crystalBlock){
                            crystalBlock.activate(20);
                        }
                    }
                }
            } else {
                if (this.isOn){
                    this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_OFF.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.isOn = false;
                }
            }
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(ResonanceCrystalBlock.POWERED, powered), 3);
        }
    }

    private void refreshSquallGolems() {
        boolean changed = this.squallGolems.removeIf(squallGolem -> squallGolem == null || !squallGolem.isAlive());
        if (!this.uuids.isEmpty()) {
            for (int i = this.uuids.size() - 1; i >= 0; --i) {
                UUID uuid = this.uuids.get(i);
                Entity entity = EntityFinder.getEntityByUuiD(uuid);
                if (entity == null) {
                    // Bound golems can be temporarily unloaded; keep the UUID so the crystal reconnects once the entity is tracked again.
                    continue;
                }
                if (!(entity instanceof SquallGolem squallGolem) || !squallGolem.isAlive()) {
                    this.uuids.remove(i);
                    changed = true;
                    continue;
                }
                if (!this.squallGolems.contains(squallGolem)) {
                    this.squallGolems.add(squallGolem);
                    changed = true;
                }
            }
        }
        if (changed) {
            this.markUpdated();
        }
    }

    public void activateGolems(){
        if (!this.squallGolems.isEmpty()){
            for (SquallGolem squallGolem : this.squallGolems){
                if (squallGolem != null) {
                    squallGolem.activate(20);
                }
            }
        }
    }

    public List<BlockPos> getBlockPosList() {
        return this.blockPosList;
    }

    public void addBlockPos(BlockPos blockPos){
        if (!this.blockPosList.contains(blockPos)) {
            this.blockPosList.add(blockPos);
            this.markUpdated();
        }
    }

    public void removeBlockPos(BlockPos blockPos){
        if (this.blockPosList.remove(blockPos)) {
            this.markUpdated();
        }
    }

    public void clearBlocks(){
        if (!this.blockPosList.isEmpty()) {
            this.blockPosList.clear();
            this.markUpdated();
        }
    }

    public List<SquallGolem> getSquallGolems() {
        return this.squallGolems;
    }

    public void addSquallGolem(SquallGolem squallGolem){
        if (squallGolem != null && !this.uuids.contains(squallGolem.getUUID())) {
            this.uuids.add(squallGolem.getUUID());
            this.squallGolems.add(squallGolem);
            this.markUpdated();
        }
    }

    public List<UUID> getUuids(){
        return this.uuids;
    }

    @Override
    public int activeTicks() {
        return this.active;
    }

    @Override
    public void activate(int tick) {
        this.active = tick;
        this.setChanged();
    }

    public boolean isShowBlock(){
        return this.showBlock;
    }

    public void setShowBlock(boolean showBlock){
        this.showBlock = showBlock;
        this.setChanged();
    }

    @Override
    public void readNetwork(CompoundTag tag) {
        if (tag.contains("active")){
            this.active = tag.getInt("active");
        }
        if (tag.contains("showBlock")) {
            this.showBlock = tag.getBoolean("showBlock");
        }
        this.blockPosList.clear();
        if (tag.contains(BLOCK_LIST)){
            ListTag list = tag.getList(BLOCK_LIST, 10);
            for(int i = 0; i < list.size(); ++i) {
                NbtUtils.readBlockPos(list.getCompound(i), "pos").ifPresent(this.blockPosList::add);
            }
        }
        this.uuids.clear();
        this.squallGolems.clear();
        if (tag.contains(GOLEM_LIST)){
            ListTag list = tag.getList(GOLEM_LIST, 8);
            for(int i = 0; i < list.size(); ++i) {
                try {
                    UUID uuid = UUID.fromString(list.getString(i));
                    if (!this.uuids.contains(uuid)) {
                        this.uuids.add(uuid);
                    }
                } catch (IllegalArgumentException ignored) {
                    // Older saves or edited items can contain invalid UUID strings; skip them instead of breaking chunk loading.
                }
            }
        }
        if (tag.contains("isOn")){
            this.isOn = tag.getBoolean("isOn");
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag tag) {
        tag.putInt("active", this.active);
        // Write fresh lists so removed golems or linked crystals do not survive in stale block entity data.
        if (!this.blockPosList.isEmpty()){
            ListTag blockList = new ListTag();
            for (BlockPos blockPos : this.blockPosList){
                CompoundTag blockTag = new CompoundTag();
                blockTag.put("pos", NbtUtils.writeBlockPos(blockPos));
                blockList.add(blockTag);
            }
            tag.put(BLOCK_LIST, blockList);
        } else {
            tag.remove(BLOCK_LIST);
        }
        if (!this.uuids.isEmpty()) {
            ListTag golemList = new ListTag();
            for (UUID uuid : this.uuids) {
                golemList.add(net.minecraft.nbt.StringTag.valueOf(uuid.toString()));
            }
            tag.put(GOLEM_LIST, golemList);
        } else {
            tag.remove(GOLEM_LIST);
        }
        tag.putBoolean("showBlock", this.showBlock);
        tag.putBoolean("isOn", this.isOn);
        return tag;
    }
}
