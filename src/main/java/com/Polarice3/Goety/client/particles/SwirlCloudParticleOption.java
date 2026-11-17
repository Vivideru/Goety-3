package com.Polarice3.Goety.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class SwirlCloudParticleOption implements ParticleOptions {
    public static final MapCodec<SwirlCloudParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId),
            Codec.BOOL.fieldOf("reversed").forGetter(d -> d.reversed),
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.DOUBLE.fieldOf("radius").forGetter(d -> d.radius),
            Codec.FLOAT.fieldOf("time").forGetter(d -> d.time),
            Codec.FLOAT.fieldOf("speed").forGetter(d -> d.speed),
            Codec.FLOAT.fieldOf("roll").forGetter(d -> d.roll),
            Vec3.CODEC.fieldOf("offset").forGetter(d -> d.offset)
    ).apply(instance, SwirlCloudParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SwirlCloudParticleOption> STREAM_CODEC = StreamCodec.of(
            (buffer, option) -> option.writeToNetwork(buffer),
            SwirlCloudParticleOption::fromNetwork
    );

    private final int ownerId;
    private final boolean reversed;
    private final float size;
    private final double radius;
    private final float time;
    private final float speed;
    private final float roll;
    private final Vec3 offset;

    public SwirlCloudParticleOption(int ownerId, boolean reversed, float size, double radius, float time, float speed, float roll, Vec3 offset) {
        this.ownerId = ownerId;
        this.reversed = reversed;
        this.size = size;
        this.radius = radius;
        this.time = time;
        this.speed = speed;
        this.roll = roll;
        this.offset = offset;
    }

    public SwirlCloudParticleOption(int ownerId, float size, double radius, float time, float speed, Vec3 offset) {
        this(ownerId, false, size, radius, time, speed, (RandomSource.create().nextFloat() - 0.5F) * 0.1F, offset);
    }

    private static SwirlCloudParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new SwirlCloudParticleOption(buffer.readInt(), buffer.readBoolean(), buffer.readFloat(), buffer.readDouble(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(this.ownerId);
        buffer.writeBoolean(this.reversed);
        buffer.writeFloat(this.size);
        buffer.writeDouble(this.radius);
        buffer.writeFloat(this.time);
        buffer.writeFloat(this.speed);
        buffer.writeFloat(this.roll);
        buffer.writeDouble(this.offset.x);
        buffer.writeDouble(this.offset.y);
        buffer.writeDouble(this.offset.z);
    }

    public ParticleType<SwirlCloudParticleOption> getType() {
        return ModParticleTypes.SWIRL_CLOUD.get();
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public float getSize(){
        return this.size;
    }

    public double getRadius() {
        return this.radius;
    }

    public float getTime() {
        return this.time;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getRoll() {
        return this.roll;
    }

    public Vec3 getOffset() {
        return this.offset;
    }
}
