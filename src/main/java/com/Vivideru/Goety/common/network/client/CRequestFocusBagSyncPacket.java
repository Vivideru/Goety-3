package com.Vivideru.Goety.common.network.client;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.utils.TotemFinder;
import com.Vivideru.Goety.common.items.magic.FocusBagBinding;
import com.Vivideru.Goety.common.network.server.SFocusBagSyncPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class CRequestFocusBagSyncPacket {
    public static void encode(CRequestFocusBagSyncPacket packet, FriendlyByteBuf buffer) {
    }

    public static CRequestFocusBagSyncPacket decode(FriendlyByteBuf buffer) {
        return new CRequestFocusBagSyncPacket();
    }

    public static void consume(CRequestFocusBagSyncPacket packet, Supplier<ModNetwork.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                FocusBagBinding.BagReference reference = TotemFinder.findBagReference(player);
                if (reference.isPresent()) {
                    SFocusBagSyncPacket.sendTo(player, reference.stack());
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
