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
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class GatherFrostParticleOption implements ParticleOptions {
    public static final MapCodec<GatherFrostParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("endX").forGetter(d -> d.endX),
            Codec.FLOAT.fieldOf("endY").forGetter(d -> d.endY),
            Codec.FLOAT.fieldOf("endZ").forGetter(d -> d.endZ)
    ).apply(instance, GatherFrostParticleOption::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GatherFrostParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           GatherFrostParticleOption::fromNetwork
   );

   private static GatherFrostParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new GatherFrostParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
   }
    public final float endX;
    public final float endY;
    public final float endZ;

    public GatherFrostParticleOption(Vec3 end) {
        this.endX = (float) end.x;
        this.endY = (float) end.y;
        this.endZ = (float) end.z;
    }

    public GatherFrostParticleOption(float endX, float endY, float endZ) {
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.endX);
        buffer.writeFloat(this.endY);
        buffer.writeFloat(this.endZ);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.endX, this.endY, this.endZ);
    }

    public ParticleType<GatherFrostParticleOption> getType() {
        return ModParticleTypes.FROST_GATHER.get();
    }

    public float getEndX() {
        return this.endX;
    }

    public float getEndY() {
        return this.endY;
    }

    public float getEndZ() {
        return this.endZ;
    }
}
