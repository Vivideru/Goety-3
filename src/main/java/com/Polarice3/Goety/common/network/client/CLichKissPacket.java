package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.magic.cantrips.LichKissCantrip;
import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CLichKissPacket {
    public static void encode(CLichKissPacket packet, FriendlyByteBuf buffer) {
    }

    public static CLichKissPacket decode(FriendlyByteBuf buffer) {
        return new CLichKissPacket();
    }

    public static void consume(CLichKissPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (LichdomHelper.isLich(playerEntity)) {
                    new LichKissCantrip().sendRay(playerEntity);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
