package com.Polarice3.Goety.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class DustCloudParticleOption implements ParticleOptions {
   public static final MapCodec<DustCloudParticleOption> CODEC = RecordCodecBuilder.mapCodec((p_175793_) -> {
      return p_175793_.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((p_175797_) -> {
         return p_175797_.color;
      }), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
         return p_175795_.scale;
      })).apply(p_175793_, DustCloudParticleOption::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, DustCloudParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           DustCloudParticleOption::fromNetwork
   );

   private static DustCloudParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
         return new DustCloudParticleOption(buffer.readVector3f(), buffer.readFloat());
   }

   private final Vector3f color;
   private final float scale;

   public DustCloudParticleOption(Vector3f p_175790_, float p_175791_) {
      this.color = p_175790_;
      this.scale = p_175791_;
   }

   public ParticleType<DustCloudParticleOption> getType() {
      return ModParticleTypes.DUST_CLOUD.get();
   }

   public void writeToNetwork(FriendlyByteBuf buffer) {
      buffer.writeVector3f(this.color);
      buffer.writeFloat(this.scale);
   }

   public Vector3f getColor() {
      return this.color;
   }

   public float getScale() {
      return this.scale;
   }
}
