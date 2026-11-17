package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.items.magic.EsotericTesseract;
import com.Polarice3.Goety.common.network.ModNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CSelectTesseractServantPacket {
    private final String key;

    public CSelectTesseractServantPacket(String key) {
        this.key = key;
    }

    public static void encode(CSelectTesseractServantPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.key, 64);
    }

    public static CSelectTesseractServantPacket decode(FriendlyByteBuf buffer) {
        return new CSelectTesseractServantPacket(buffer.readUtf(64));
    }

    public static void consume(CSelectTesseractServantPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = EsotericTesseract.getHeldTesseract(player);
                if (!stack.isEmpty()) {
                    EsotericTesseract.selectServant(stack, packet.key);
                }
            }
        });
    }
}
