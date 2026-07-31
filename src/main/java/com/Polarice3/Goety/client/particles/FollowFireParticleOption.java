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

public class FollowFireParticleOption implements ParticleOptions {
    public static final MapCodec<FollowFireParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId)
    ).apply(instance, FollowFireParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FollowFireParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           FollowFireParticleOption::fromNetwork
   );

   private static FollowFireParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new FollowFireParticleOption(buffer.readInt());
   }
    private final int ownerId;

    public FollowFireParticleOption(int ownerId) {
        this.ownerId = ownerId;
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(this.ownerId);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.ownerId);
    }

    public ParticleType<FollowFireParticleOption> getType() {
        return ModParticleTypes.FOLLOW_CULT_SPELL.get();
    }

    public int getOwnerId() {
        return this.ownerId;
    }
}
