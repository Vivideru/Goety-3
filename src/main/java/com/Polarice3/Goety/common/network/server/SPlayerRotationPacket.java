package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import com.Polarice3.Goety.common.network.ModNetwork.NetworkDirection;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class SPlayerRotationPacket {
    private final float yRot;
    private final float xRot;

    public SPlayerRotationPacket(float yRot, float xRot){
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public static void encode(SPlayerRotationPacket packet, FriendlyByteBuf buffer) {
        buffer.writeFloat(packet.yRot);
        buffer.writeFloat(packet.xRot);
    }

    public static SPlayerRotationPacket decode(FriendlyByteBuf buffer) {
        return new SPlayerRotationPacket(
                buffer.readFloat(),
                buffer.readFloat());
    }

    public static void consume(SPlayerRotationPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Player player = Goety.PROXY.getPlayer();
                if (player != null){
                    player.setYRot(packet.yRot);
                    player.setXRot(packet.xRot);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
