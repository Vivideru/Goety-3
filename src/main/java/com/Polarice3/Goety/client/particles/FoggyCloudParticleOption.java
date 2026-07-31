package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
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

public class FoggyCloudParticleOption implements ParticleOptions {
    public static final MapCodec<FoggyCloudParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.INT.fieldOf("speed").forGetter(d -> d.speed),
            Codec.BOOL.fieldOf("gravity").forGetter(d -> d.gravity)
    ).apply(instance, FoggyCloudParticleOption::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FoggyCloudParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           FoggyCloudParticleOption::fromNetwork
   );

   private static FoggyCloudParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new FoggyCloudParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readBoolean());
   }
    private final float red;
    private final float green;
    private final float blue;
    private final float size;
    private final int speed;
    private final boolean gravity;

    public FoggyCloudParticleOption(ColorUtil colorUtil, float size, int speed) {
        this(colorUtil, size, speed, true);
    }

    public FoggyCloudParticleOption(ColorUtil colorUtil, float size, int speed, boolean gravity) {
        this.red = colorUtil.red;
        this.green = colorUtil.green;
        this.blue = colorUtil.blue;
        this.size = size;
        this.speed = speed;
        this.gravity = gravity;
    }

    public FoggyCloudParticleOption(float r, float g, float b, float size, int speed) {
        this(r, g, b, size, speed, true);
    }

    public FoggyCloudParticleOption(float r, float g, float b, float size, int speed, boolean gravity) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.size = size;
        this.speed = speed;
        this.gravity = gravity;
    }

    public void writeToNetwork(FriendlyByteBuf p_235956_) {
        p_235956_.writeFloat(this.red);
        p_235956_.writeFloat(this.green);
        p_235956_.writeFloat(this.blue);
        p_235956_.writeFloat(this.size);
        p_235956_.writeInt(this.speed);
        p_235956_.writeBoolean(this.gravity);
    }

    public ParticleType<FoggyCloudParticleOption> getType() {
        return ModParticleTypes.FOG_CLOUD.get();
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %s %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.size, this.speed, this.gravity);
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

    public boolean hasGravity(){
        return this.gravity;
    }
}
