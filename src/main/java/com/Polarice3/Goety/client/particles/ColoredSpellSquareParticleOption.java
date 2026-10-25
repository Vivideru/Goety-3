package com.Polarice3.Goety.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ColoredSpellSquareParticleOption implements ParticleOptions {
    public static final MapCodec<ColoredSpellSquareParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.FLOAT.fieldOf("r").forGetter(d -> d.r),
            Codec.FLOAT.fieldOf("g").forGetter(d -> d.g),
            Codec.FLOAT.fieldOf("b").forGetter(d -> d.b),
            Codec.INT.fieldOf("life").forGetter(d -> d.life),
            Codec.BOOL.optionalFieldOf("flame", false).forGetter(d -> d.flame)
    ).apply(instance, ColoredSpellSquareParticleOption::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ColoredSpellSquareParticleOption> STREAM_CODEC = StreamCodec.of(
            (buffer, option) -> option.writeToNetwork(buffer),
            ColoredSpellSquareParticleOption::fromNetwork
    );
    public final float size;
    public final float r, g, b;
    public final int life;
    public final boolean flame;

    private static ColoredSpellSquareParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new ColoredSpellSquareParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readBoolean());
    }

    public ColoredSpellSquareParticleOption(float size, float r, float g, float b, int life) {
        this(size, r, g, b, life, false);
    }

    public ColoredSpellSquareParticleOption(float size, float r, float g, float b, int life, boolean flame) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.life = life;
        this.flame = flame;
    }

    @NotNull
    @Override
    public ParticleType<ColoredSpellSquareParticleOption> getType() {
        return ModParticleTypes.COLORED_SPELL_SQUARE.get();
    }

    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeFloat(this.r);
        buf.writeFloat(this.g);
        buf.writeFloat(this.b);
        buf.writeInt(this.life);
        buf.writeBoolean(this.flame);
    }

    @NotNull
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d %s", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.size, this.r, this.g, this.b, this.life, this.flame);
    }
}
