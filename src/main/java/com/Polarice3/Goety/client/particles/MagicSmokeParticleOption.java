package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Locale;

public class MagicSmokeParticleOption implements ParticleOptions {
    public static final MapCodec<MagicSmokeParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("colorFrom").forGetter(MagicSmokeParticleOption::getColorFrom),
            Codec.INT.fieldOf("colorTo").forGetter(MagicSmokeParticleOption::getColorTo),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(MagicSmokeParticleOption::getDuration),
            Codec.FLOAT.fieldOf("size").forGetter(MagicSmokeParticleOption::getSize),
            Codec.FLOAT.fieldOf("gravity").forGetter(MagicSmokeParticleOption::getGravity)
    ).apply(instance, MagicSmokeParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MagicSmokeParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           MagicSmokeParticleOption::fromNetwork
   );

   private static MagicSmokeParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new MagicSmokeParticleOption(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readFloat());
   }
    public int colorFrom;
    public int colorTo;
    public int duration;
    public float size;
    public float gravity;

    public MagicSmokeParticleOption(int colorFrom, int colorTo, int duration, float size, float gravity){
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        this.duration = duration;
        this.size = size;
        this.gravity = gravity;
    }

    public MagicSmokeParticleOption(int colorFrom, int colorTo, int duration, float size){
        this(colorFrom, colorTo, duration, size, -0.1F);
    }

    public ParticleType<MagicSmokeParticleOption> getType() {
        return ModParticleTypes.MAGIC_SMOKE.get();
    }

    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        p_123732_.writeInt(this.getColorFrom());
        p_123732_.writeInt(this.getColorTo());
        p_123732_.writeInt(this.getDuration());
        p_123732_.writeFloat(this.getSize());
        p_123732_.writeFloat(this.getGravity());
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %s %s %s %.2f %.2f",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.colorFrom, this.colorTo, this.duration, this.size, this.gravity);
    }

    public int getColorFrom() {
        return this.colorFrom;
    }

    public int getColorTo() {
        return this.colorTo;
    }

    public int getDuration() {
        return this.duration;
    }

    public float getSize() {
        return this.size;
    }

    public float getGravity() {
        return this.gravity;
    }
}
