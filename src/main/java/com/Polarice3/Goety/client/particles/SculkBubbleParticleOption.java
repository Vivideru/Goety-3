package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class SculkBubbleParticleOption implements ParticleOptions {
    public static final MapCodec<SculkBubbleParticleOption> CODEC = RecordCodecBuilder.mapCodec((p_235978_) -> {
        return p_235978_.group(PositionSource.CODEC.fieldOf("destination").forGetter((p_235982_) -> {
            return p_235982_.destination;
        }), Codec.INT.fieldOf("arrival_in_ticks").forGetter((p_235980_) -> {
            return p_235980_.arrivalInTicks;
        })).apply(p_235978_, SculkBubbleParticleOption::new);
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, SculkBubbleParticleOption> STREAM_CODEC = StreamCodec.of(
           (buffer, option) -> option.writeToNetwork(buffer),
           SculkBubbleParticleOption::fromNetwork
   );

   private static SculkBubbleParticleOption fromNetwork(RegistryFriendlyByteBuf buffer) {
            PositionSource positionsource = PositionSource.STREAM_CODEC.decode(buffer);
            int i = buffer.readVarInt();
            return new SculkBubbleParticleOption(positionsource, i);
   }
    private final PositionSource destination;
    private final int arrivalInTicks;

    public SculkBubbleParticleOption(PositionSource p_235975_, int p_235976_) {
        this.destination = p_235975_;
        this.arrivalInTicks = p_235976_;
    }

    public void writeToNetwork(RegistryFriendlyByteBuf p_175854_) {
        PositionSource.STREAM_CODEC.encode(p_175854_, this.destination);
        p_175854_.writeVarInt(this.arrivalInTicks);
    }

    public String writeToString() {
        Vec3 vec3 = this.destination.getPosition((Level)null).get();
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), d0, d1, d2, this.arrivalInTicks);
    }

    public ParticleType<SculkBubbleParticleOption> getType() {
        return ModParticleTypes.SCULK_BUBBLE.get();
    }

    public PositionSource getDestination() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}
