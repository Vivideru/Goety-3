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

public class ShootIndicatorParticleOption implements ParticleOptions {
    public static final MapCodec<ShootIndicatorParticleOption> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codec.INT.fieldOf("delay").forGetter((option) -> option.ownerId)).apply(instance, ShootIndicatorParticleOption::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShootIndicatorParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           ShootIndicatorParticleOption::fromNetwork
   );

   private static ShootIndicatorParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new ShootIndicatorParticleOption(buffer.readVarInt());
   }
    private final int ownerId;

    public ShootIndicatorParticleOption(int ownerId) {
        this.ownerId = ownerId;
    }

    public void writeToNetwork(FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(this.ownerId);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.ownerId);
    }

    @Override
    public ParticleType<ShootIndicatorParticleOption> getType() {
        return ModParticleTypes.SHOOT_INDICATOR.get();
    }

    public int getOwnerId() {
        return this.ownerId;
    }
}