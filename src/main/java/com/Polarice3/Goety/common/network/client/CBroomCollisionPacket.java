package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.entities.vehicle.HauntedBroom;
import com.Polarice3.Goety.common.network.ModNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public class CBroomCollisionPacket {
    private final int entityId;
    private final double speed;

    public CBroomCollisionPacket(int entityId, double speed) {
        this.entityId = entityId;
        this.speed = speed;
    }

    public static void encode(CBroomCollisionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.entityId);
        buffer.writeDouble(packet.speed);
    }

    public static CBroomCollisionPacket decode(FriendlyByteBuf buffer) {
        return new CBroomCollisionPacket(buffer.readVarInt(), buffer.readDouble());
    }

    public static void consume(CBroomCollisionPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Entity entity = player.level().getEntity(packet.entityId);
                if (entity instanceof HauntedBroom broom && broom.getControllingPassenger() == player) {
                    broom.applyCollisionDamage(packet.speed);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
