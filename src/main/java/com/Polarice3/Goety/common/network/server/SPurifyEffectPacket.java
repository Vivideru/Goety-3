package com.Polarice3.Goety.common.network.server;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.Goety;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class SPurifyEffectPacket {
    private final int mob;
    private final boolean removeDebuff;

    public SPurifyEffectPacket(int mob, boolean removeDebuff){
        this.mob = mob;
        this.removeDebuff = removeDebuff;
    }

    public static void encode(SPurifyEffectPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
        buffer.writeBoolean(packet.removeDebuff);
    }

    public static SPurifyEffectPacket decode(FriendlyByteBuf buffer) {
        return new SPurifyEffectPacket(buffer.readInt(), buffer.readBoolean());
    }

    public static void consume(SPurifyEffectPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Goety.PROXY.getLevel();
            if (level instanceof ClientLevel clientWorld) {
                Entity entity = clientWorld.getEntity(packet.mob);
                if (entity instanceof LivingEntity livingEntity) {
                    for (MobEffect mobEffect : BuiltInRegistries.MOB_EFFECT){
                        boolean flag;
                        if (packet.removeDebuff) {
                            flag = !mobEffect.isBeneficial();
                        } else {
                            flag = mobEffect.isBeneficial();
                        }
                        if (flag){
                            // Vanilla 1.21 removed MobEffect curative-item metadata; preserve the beneficial/debuff filtering.
                            livingEntity.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(mobEffect));
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
