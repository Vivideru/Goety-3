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

public class ModShriekParticleOption implements ParticleOptions {
   public static final MapCodec<ModShriekParticleOption> CODEC = RecordCodecBuilder.mapCodec((p_235952_) -> {
      return p_235952_.group(Codec.INT.fieldOf("delay").forGetter((p_235954_) -> {
         return p_235954_.delay;
      })).apply(p_235952_, ModShriekParticleOption::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, ModShriekParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           ModShriekParticleOption::fromNetwork
   );

   private static ModShriekParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
         return new ModShriekParticleOption(buffer.readVarInt());
   }
   private final int delay;

   public ModShriekParticleOption(int p_235949_) {
      this.delay = p_235949_;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeVarInt(this.delay);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.delay);
   }

   public ParticleType<ModShriekParticleOption> getType() {
      return ModParticleTypes.MOD_SHRIEK.get();
   }

   public int getDelay() {
      return this.delay;
   }
}