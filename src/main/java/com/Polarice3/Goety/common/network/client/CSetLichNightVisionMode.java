package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CSetLichNightVisionMode {

    public static void encode(CSetLichNightVisionMode packet, FriendlyByteBuf buffer) {
    }

    public static CSetLichNightVisionMode decode(FriendlyByteBuf buffer) {
        return new CSetLichNightVisionMode();
    }

    public static void consume(CSetLichNightVisionMode packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();
            if (playerEntity != null && LichdomHelper.isLich(playerEntity) && MainConfig.LichNightVision.get()) {
                LichdomHelper.setNightVision(playerEntity, !LichdomHelper.nightVision(playerEntity));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
