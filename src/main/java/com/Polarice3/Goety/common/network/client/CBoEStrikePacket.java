package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.items.equipment.BladeOfEnderItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CBoEStrikePacket {
    public static void encode(CBoEStrikePacket packet, FriendlyByteBuf buffer) {
    }

    public static CBoEStrikePacket decode(FriendlyByteBuf buffer) {
        return new CBoEStrikePacket();
    }

    public static void consume(CBoEStrikePacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                BladeOfEnderItem.strike(playerEntity.level(), playerEntity);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
