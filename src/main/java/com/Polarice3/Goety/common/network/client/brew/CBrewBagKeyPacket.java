package com.Polarice3.Goety.common.network.client.brew;

import com.Polarice3.Goety.client.inventory.container.BrewBagContainer;
import com.Polarice3.Goety.common.items.handler.BrewBagItemHandler;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CBrewBagKeyPacket {

    public static void encode(CBrewBagKeyPacket packet, FriendlyByteBuf buffer) {
    }

    public static CBrewBagKeyPacket decode(FriendlyByteBuf buffer) {
        return new CBrewBagKeyPacket();
    }

    public static void consume(CBrewBagKeyPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack stack = CuriosFinder.findBrewBag(playerEntity);

                if (!stack.isEmpty()){
                    SimpleMenuProvider provider = new SimpleMenuProvider(
                            (id, inventory, player) -> new BrewBagContainer(id, inventory, BrewBagItemHandler.get(stack), stack), Component.translatable(stack.getDescriptionId()));
                    playerEntity.openMenu(provider);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
