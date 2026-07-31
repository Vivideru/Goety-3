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

public class MagicAshSmokeParticleOption implements ParticleOptions {
    public static final MapCodec<MagicAshSmokeParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("colorFrom").forGetter(MagicAshSmokeParticleOption::getColorFrom),
            Codec.INT.fieldOf("colorTo").forGetter(MagicAshSmokeParticleOption::getColorTo)
    ).apply(instance, MagicAshSmokeParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MagicAshSmokeParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           MagicAshSmokeParticleOption::fromNetwork
   );

   private static MagicAshSmokeParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new MagicAshSmokeParticleOption(buffer.readInt(), buffer.readInt());
   }
    public int colorFrom;
    public int colorTo;

    public MagicAshSmokeParticleOption(int colorFrom, int colorTo){
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
    }

    public MagicAshSmokeParticleOption(int color){
        this.colorFrom = color;
        this.colorTo = color;
    }

    public ParticleType<MagicAshSmokeParticleOption> getType() {
        return ModParticleTypes.MAGIC_ASH_SMOKE.get();
    }

    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        p_123732_.writeInt(this.getColorFrom());
        p_123732_.writeInt(this.getColorTo());
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %s %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.colorFrom, this.colorTo);
    }

    public int getColorFrom() {
        return this.colorFrom;
    }

    public int getColorTo() {
        return this.colorTo;
    }
}
