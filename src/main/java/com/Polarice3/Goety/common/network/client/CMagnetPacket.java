package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.magic.cantrips.MagnetCantrip;
import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CMagnetPacket {
    public static void encode(CMagnetPacket packet, FriendlyByteBuf buffer) {
    }

    public static CMagnetPacket decode(FriendlyByteBuf buffer) {
        return new CMagnetPacket();
    }

    public static void consume(CMagnetPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (LichdomHelper.isLich(playerEntity)) {
                    new MagnetCantrip().callItems(playerEntity);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
