package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.entities.neutral.IRavager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CRavagerRoarPacket {
    public static void encode(CRavagerRoarPacket packet, FriendlyByteBuf buffer) {
    }

    public static CRavagerRoarPacket decode(FriendlyByteBuf buffer) {
        return new CRavagerRoarPacket();
    }

    public static void consume(CRavagerRoarPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (playerEntity.getVehicle() instanceof IRavager ravager){
                    ravager.forceRoar();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
