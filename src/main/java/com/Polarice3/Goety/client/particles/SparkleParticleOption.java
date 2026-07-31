/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
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
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SparkleParticleOption implements ParticleOptions {
    public static final MapCodec<SparkleParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.FLOAT.fieldOf("r").forGetter(d -> d.r),
            Codec.FLOAT.fieldOf("g").forGetter(d -> d.g),
            Codec.FLOAT.fieldOf("b").forGetter(d -> d.b),
            Codec.INT.fieldOf("extraLife").forGetter(d -> d.extraLife)
    ).apply(instance, SparkleParticleOption::new));
    public final float size;
    public final float r, g, b;
    public final int extraLife;

    public SparkleParticleOption(float size, ColorUtil colorUtil, int extraLife) {
        this.size = size;
        this.r = colorUtil.red();
        this.g = colorUtil.green();
        this.b = colorUtil.blue();
        this.extraLife = extraLife;
    }

    public SparkleParticleOption(float size, float r, float g, float b, int extraLife) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.extraLife = extraLife;
    }

    @NotNull
    @Override
    public ParticleType<SparkleParticleOption> getType() {
        return ModParticleTypes.SPARKLE.get();
    }

    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(size);
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeInt(extraLife);
    }

    @NotNull
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.size, this.r, this.g, this.b, this.extraLife);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SparkleParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           SparkleParticleOption::fromNetwork
   );

   private static SparkleParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new SparkleParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
   }
}
