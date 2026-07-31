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

import java.util.Locale;

public class VerticalCircleExplodeParticleOption implements ParticleOptions {
    public static final MapCodec<VerticalCircleExplodeParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.INT.fieldOf("speed").forGetter(d -> d.speed)
    ).apply(instance, VerticalCircleExplodeParticleOption::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, VerticalCircleExplodeParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           VerticalCircleExplodeParticleOption::fromNetwork
   );

   private static VerticalCircleExplodeParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new VerticalCircleExplodeParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
   }
    private final float red;
    private final float green;
    private final float blue;
    private final float size;
    private final int speed;

    public VerticalCircleExplodeParticleOption(float r, float g, float b) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.size = 10;
        this.speed = 0;
    }

    public VerticalCircleExplodeParticleOption(float r, float g, float b, float size, int speed) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.size = size;
        this.speed = speed;
    }

    public void writeToNetwork(FriendlyByteBuf p_235956_) {
        p_235956_.writeFloat(this.red);
        p_235956_.writeFloat(this.green);
        p_235956_.writeFloat(this.blue);
        p_235956_.writeFloat(this.size);
        p_235956_.writeInt(this.speed);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.size, this.speed);
    }

    public ParticleType<VerticalCircleExplodeParticleOption> getType() {
        return ModParticleTypes.VERTICAL_CIRCLE_EXPLODE.get();
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getSize(){
        return this.size;
    }

    public int getSpeed(){
        return this.speed;
    }
}
