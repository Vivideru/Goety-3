package com.Polarice3.Goety.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class SoulLeaderCCParticleOption implements ParticleOptions {
    private final ParticleType<SoulLeaderCCParticleOption> type;
    public final float size;
    public final int life;
    public final float cx, cz;
    public final int color;

    public SoulLeaderCCParticleOption(ParticleType<SoulLeaderCCParticleOption> type, float size, int life, float cx, float cz, int color) {
        this.type = type;
        this.size = size;
        this.life = life;
        this.cx = cx;
        this.cz = cz;
        this.color = color;
    }

    public static MapCodec<SoulLeaderCCParticleOption> codec(ParticleType<SoulLeaderCCParticleOption> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
                Codec.INT.fieldOf("life").forGetter(o -> o.life),
                Codec.FLOAT.fieldOf("cx").forGetter(o -> o.cx),
                Codec.FLOAT.fieldOf("cz").forGetter(o -> o.cz),
                Codec.INT.fieldOf("color").forGetter(o -> o.color)
        ).apply(instance, (size, life, cx, cz, color) -> new SoulLeaderCCParticleOption(type, size, life, cx, cz, color)));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, SoulLeaderCCParticleOption> streamCodec(ParticleType<SoulLeaderCCParticleOption> type) {
        return StreamCodec.of(
                (buf, option) -> option.writeToNetwork(buf),
                buf -> new SoulLeaderCCParticleOption(type, buf.readFloat(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readVarInt()));
    }

    public static ParticleType<SoulLeaderCCParticleOption> createType() {
        return new ParticleType<>(true) {
            private final MapCodec<SoulLeaderCCParticleOption> codec = SoulLeaderCCParticleOption.codec(this);
            private final StreamCodec<RegistryFriendlyByteBuf, SoulLeaderCCParticleOption> streamCodec = SoulLeaderCCParticleOption.streamCodec(this);

            @Override
            public @NotNull MapCodec<SoulLeaderCCParticleOption> codec() {
                return this.codec;
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, SoulLeaderCCParticleOption> streamCodec() {
                return this.streamCodec;
            }
        };
    }

    @NotNull
    @Override
    public ParticleType<SoulLeaderCCParticleOption> getType() {
        return this.type;
    }

    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeVarInt(this.life);
        buf.writeFloat(this.cx);
        buf.writeFloat(this.cz);
        buf.writeVarInt(this.color);
    }
}
