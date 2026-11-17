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

public class MCDEffectOption implements ParticleOptions {
    private final ParticleType<MCDEffectOption> type;
    public final float size;
    public final int life;
    public final float a, b, c;
    public final boolean alternate;

    public MCDEffectOption(ParticleType<MCDEffectOption> type, float size, int life, float a, float b, float c, boolean alternate) {
        this.type = type;
        this.size = size;
        this.life = life;
        this.a = a;
        this.b = b;
        this.c = c;
        this.alternate = alternate;
    }

    public MCDEffectOption(ParticleType<MCDEffectOption> type, float size, int life, float a, float b, float c) {
        this(type, size, life, a, b, c, false);
    }

    public static MapCodec<MCDEffectOption> codec(ParticleType<MCDEffectOption> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
                Codec.INT.fieldOf("life").forGetter(o -> o.life),
                Codec.FLOAT.fieldOf("a").forGetter(o -> o.a),
                Codec.FLOAT.fieldOf("b").forGetter(o -> o.b),
                Codec.FLOAT.fieldOf("c").forGetter(o -> o.c),
                Codec.BOOL.fieldOf("alternate").forGetter(o -> o.alternate)
        ).apply(instance, (size, life, a, b, c, alternate) -> new MCDEffectOption(type, size, life, a, b, c, alternate)));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, MCDEffectOption> streamCodec(ParticleType<MCDEffectOption> type) {
        return StreamCodec.of(
                (buf, option) -> option.writeToNetwork(buf),
                buf -> new MCDEffectOption(type, buf.readFloat(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean()));
    }

    public static ParticleType<MCDEffectOption> createType() {
        return new ParticleType<>(true) {
            private final MapCodec<MCDEffectOption> codec = MCDEffectOption.codec(this);
            private final StreamCodec<RegistryFriendlyByteBuf, MCDEffectOption> streamCodec = MCDEffectOption.streamCodec(this);

            @Override
            public @NotNull MapCodec<MCDEffectOption> codec() {
                return this.codec;
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, MCDEffectOption> streamCodec() {
                return this.streamCodec;
            }
        };
    }

    @NotNull
    @Override
    public ParticleType<MCDEffectOption> getType() {
        return this.type;
    }

    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeVarInt(this.life);
        buf.writeFloat(this.a);
        buf.writeFloat(this.b);
        buf.writeFloat(this.c);
        buf.writeBoolean(this.alternate);
    }
}
