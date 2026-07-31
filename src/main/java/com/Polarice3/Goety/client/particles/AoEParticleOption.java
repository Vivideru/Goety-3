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

public class AoEParticleOption implements ParticleOptions {
   public static final MapCodec<AoEParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
           Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
           Codec.FLOAT.fieldOf("growing").forGetter(d -> d.growing),
           Codec.FLOAT.fieldOf("maxSize").forGetter(d -> d.maxSize),
           Codec.INT.fieldOf("life").forGetter(d -> d.life),
           Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId)
   ).apply(instance, AoEParticleOption::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, AoEParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           AoEParticleOption::fromNetwork
   );

   private static AoEParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
         return new AoEParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readInt());
   }
   private final float size;
   private final float growing;
   private final float maxSize;
   private final int life;
   private final int ownerId;

   public AoEParticleOption(float size, int life) {
      this(size, life, -1);
   }

   public AoEParticleOption(float size, int life, int ownerId) {
      this(size, 0.0F, size, life, ownerId);
   }

   public AoEParticleOption(float initialSize, float growing, float maxSize, int life) {
      this(initialSize, growing, maxSize, life, -1);
   }

   public AoEParticleOption(float initialSize, float growing, float maxSize, int life, int ownerId) {
      this.size = initialSize;
      this.growing = growing;
      this.maxSize = maxSize;
      this.life = life;
      this.ownerId = ownerId;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeFloat(this.size);
      p_235956_.writeFloat(this.growing);
      p_235956_.writeFloat(this.maxSize);
      p_235956_.writeInt(this.life);
      p_235956_.writeInt(this.ownerId);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %s %s",
              BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.size, this.growing, this.maxSize, this.life, this.ownerId);
   }

   public ParticleType<AoEParticleOption> getType() {
      return ModParticleTypes.AOE_INDICATOR.get();
   }

   public float getSize(){
      return this.size;
   }

   public float getGrowing(){
      return this.growing;
   }

   public float getMaxSize(){
      return this.maxSize;
   }

   public int getLife(){
      return this.life;
   }

   public int getOwnerId(){
      return this.ownerId;
   }

}