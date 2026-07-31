package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.VoidFrameBlock;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class VoidFrameBlockEntity extends BlockEntity {
    public List<MobEffectInstance> eyeEffects = new ArrayList<>();
    public int eyeType = 0;
    public int coolTick;

    public VoidFrameBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.VOID_FRAME.get(), p_155229_, p_155230_);
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (!this.getBlockState().getValue(VoidFrameBlock.LOCKED)) {
                ++this.coolTick;
                if (this.coolTick < 20) {
                    for (Player player : serverLevel.getEntitiesOfClass(Player.class, new AABB(this.getBlockPos()).inflate(64.0F), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
                        player.addEffect(new MobEffectInstance(GoetyEffects.IMPAIRED, 5, 0, false, false));
                    }
                }
                if (this.coolTick >= MainConfig.VoidFrameCoolTime.get()) {
                    this.coolTick = 0;
                    serverLevel.playSound(null, this.getBlockPos().above(), ModSounds.VOID_SPAWNER_CLOSE_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
                    serverLevel.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(VoidFrameBlock.LOCKED, true));
                }
            } else {
                Vec3 vec3 = this.getBlockPos().getCenter().offsetRandom(serverLevel.getRandom(), 1.0F);
                serverLevel.sendParticles(ParticleTypes.ENCHANT, vec3.x, vec3.y + 0.5F, vec3.z, 1, 0.0F, 0.0F, 0.0F, 0.0F);
            }
        }
    }

    public void setCoolTick(int coolTick) {
        this.coolTick = coolTick;
    }

    public int getEyeType() {
        return this.eyeType;
    }

    public List<MobEffectInstance> getEyeEffects() {
        return this.eyeEffects;
    }

    private static DynamicOps<Tag> effectOps(HolderLookup.Provider provider) {
        return provider.createSerializationContext(NbtOps.INSTANCE);
    }

    //Allows custom Endersent to be spawned with either different Eye types or what effects they get
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        if (compoundTag.contains("EyeType")) {
            this.eyeType = compoundTag.getInt("EyeType");
        }
        if (compoundTag.contains("EyeEffects")) {
            // Eye effects are registry-backed in 1.21, so parse them through the supplied lookup context.
            this.eyeEffects.clear();
            ListTag listtag = compoundTag.getList("EyeEffects", 10);
            DynamicOps<Tag> ops = effectOps(provider);

            for(int i = 0; i < listtag.size(); ++i) {
                MobEffectInstance.CODEC.parse(ops, listtag.get(i)).result().ifPresent(this.eyeEffects::add);
            }
        }
        this.coolTick = compoundTag.getInt("CoolTick");
    }

    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        if (!this.eyeEffects.isEmpty()) {
            ListTag listtag = new ListTag();
            DynamicOps<Tag> ops = effectOps(provider);

            for(MobEffectInstance mobeffectinstance : this.eyeEffects) {
                listtag.add(MobEffectInstance.CODEC.encodeStart(ops, mobeffectinstance).getOrThrow());
            }

            compoundTag.put("EyeEffects", listtag);
        }
        compoundTag.putInt("EyeType", this.getEyeType());
        compoundTag.putInt("CoolTick", this.coolTick);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        if (!pkt.getTag().isEmpty()) {
            this.loadWithComponents(pkt.getTag(), provider);
        }
        super.onDataPacket(net, pkt, provider);
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }
}
