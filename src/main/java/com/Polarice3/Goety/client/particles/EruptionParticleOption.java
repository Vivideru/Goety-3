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

public class EruptionParticleOption implements ParticleOptions {
    private final ParticleType<EruptionParticleOption> type;
    public final float size;
    public final int life;
    public final float baseRed, baseGreen, baseBlue;
    public final float redAmp, greenAmp, blueAmp;

    public EruptionParticleOption(ParticleType<EruptionParticleOption> type, float size, int life, float baseRed, float baseGreen, float baseBlue, float redAmp, float greenAmp, float blueAmp) {
        this.type = type;
        this.size = size;
        this.life = life;
        this.baseRed = baseRed;
        this.baseGreen = baseGreen;
        this.baseBlue = baseBlue;
        this.redAmp = redAmp;
        this.greenAmp = greenAmp;
        this.blueAmp = blueAmp;
    }

    public static MapCodec<EruptionParticleOption> codec(ParticleType<EruptionParticleOption> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
                Codec.INT.fieldOf("life").forGetter(o -> o.life),
                Codec.FLOAT.fieldOf("baseRed").forGetter(o -> o.baseRed),
                Codec.FLOAT.fieldOf("baseGreen").forGetter(o -> o.baseGreen),
                Codec.FLOAT.fieldOf("baseBlue").forGetter(o -> o.baseBlue),
                Codec.FLOAT.fieldOf("redAmp").forGetter(o -> o.redAmp),
                Codec.FLOAT.fieldOf("greenAmp").forGetter(o -> o.greenAmp),
                Codec.FLOAT.fieldOf("blueAmp").forGetter(o -> o.blueAmp)
        ).apply(instance, (size, life, baseRed, baseGreen, baseBlue, redAmp, greenAmp, blueAmp) -> new EruptionParticleOption(type, size, life, baseRed, baseGreen, baseBlue, redAmp, greenAmp, blueAmp)));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, EruptionParticleOption> streamCodec(ParticleType<EruptionParticleOption> type) {
        return StreamCodec.of(
                (buf, option) -> option.writeToNetwork(buf),
                buf -> new EruptionParticleOption(type, buf.readFloat(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()));
    }

    public static ParticleType<EruptionParticleOption> createType() {
        return new ParticleType<>(true) {
            private final MapCodec<EruptionParticleOption> codec = EruptionParticleOption.codec(this);
            private final StreamCodec<RegistryFriendlyByteBuf, EruptionParticleOption> streamCodec = EruptionParticleOption.streamCodec(this);

            @Override
            public @NotNull MapCodec<EruptionParticleOption> codec() {
                return this.codec;
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, EruptionParticleOption> streamCodec() {
                return this.streamCodec;
            }
        };
    }

    @NotNull
    @Override
    public ParticleType<EruptionParticleOption> getType() {
        return this.type;
    }

    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeVarInt(this.life);
        buf.writeFloat(this.baseRed);
        buf.writeFloat(this.baseGreen);
        buf.writeFloat(this.baseBlue);
        buf.writeFloat(this.redAmp);
        buf.writeFloat(this.greenAmp);
        buf.writeFloat(this.blueAmp);
    }
}
