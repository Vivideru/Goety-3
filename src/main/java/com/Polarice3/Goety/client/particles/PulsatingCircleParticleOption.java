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

public record PulsatingCircleParticleOption(float size) implements ParticleOptions {
   public static final MapCodec<PulsatingCircleParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
           Codec.FLOAT.fieldOf("size").forGetter(d -> d.size)
   ).apply(instance, PulsatingCircleParticleOption::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, PulsatingCircleParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           PulsatingCircleParticleOption::fromNetwork
   );

   private static PulsatingCircleParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
         return new PulsatingCircleParticleOption(buffer.readFloat());
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeFloat(this.size);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f",
              BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.size);
   }

   public ParticleType<PulsatingCircleParticleOption> getType() {
      return ModParticleTypes.MINE_PULSE.get();
   }
}