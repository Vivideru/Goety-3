package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.items.equipment.DeathScytheItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CScytheStrikePacket {
    public static void encode(CScytheStrikePacket packet, FriendlyByteBuf buffer) {
    }

    public static CScytheStrikePacket decode(FriendlyByteBuf buffer) {
        return new CScytheStrikePacket();
    }

    public static void consume(CScytheStrikePacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                DeathScytheItem.strike(playerEntity.level(), playerEntity);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
