package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class SFocusCooldownPacket {
    private final Item item;
    private final int duration;

    public SFocusCooldownPacket(Item p_132000_, int p_132001_) {
        this.item = p_132000_;
        this.duration = p_132001_;
    }

    public static void encode(SFocusCooldownPacket packet, FriendlyByteBuf buffer) {
        buffer.writeById(BuiltInRegistries.ITEM::getId, packet.item);
        buffer.writeVarInt(packet.duration);
    }

    public static SFocusCooldownPacket decode(FriendlyByteBuf buffer) {
        return new SFocusCooldownPacket(buffer.readById(BuiltInRegistries.ITEM::byId), buffer.readVarInt());
    }

    public static void consume(SFocusCooldownPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Goety.PROXY.getPlayer();
            if (player != null) {
                if (packet.duration == 0) {
                    SEHelper.getFocusCoolDown(player).removeCooldown(player, player.level(), packet.item);
                } else {
                    SEHelper.addCooldown(player, packet.item, packet.duration);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
