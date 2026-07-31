package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CSetLichMode {

    public static void encode(CSetLichMode packet, FriendlyByteBuf buffer) {
    }

    public static CSetLichMode decode(FriendlyByteBuf buffer) {
        return new CSetLichMode();
    }

    public static void consume(CSetLichMode packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();
            if (playerEntity != null && LichdomHelper.isLich(playerEntity)) {
                LichdomHelper.setLichMode(playerEntity, !LichdomHelper.isInLichMode(playerEntity));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
