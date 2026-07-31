package com.Polarice3.Goety.common.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CSetDeltaMovement {
    public int mob;
    public double x;
    public double y;
    public double z;

    public CSetDeltaMovement(int id, double x, double y, double z) {
        this.mob = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(CSetDeltaMovement packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
        buffer.writeDouble(packet.x);
        buffer.writeDouble(packet.y);
        buffer.writeDouble(packet.z);
    }

    public static CSetDeltaMovement decode(FriendlyByteBuf buffer) {
        return new CSetDeltaMovement(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void consume(CSetDeltaMovement packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(packet.mob);
                if (entity != null) {
                    entity.setDeltaMovement(packet.x, packet.y, packet.z);
                    entity.hasImpulse = true;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
