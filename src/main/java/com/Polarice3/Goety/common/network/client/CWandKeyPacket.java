package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.inventory.container.SoulItemContainer;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CWandKeyPacket {

    public static void encode(CWandKeyPacket packet, FriendlyByteBuf buffer) {
    }

    public static CWandKeyPacket decode(FriendlyByteBuf buffer) {
        return new CWandKeyPacket();
    }

    public static void consume(CWandKeyPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack stack = playerEntity.getMainHandItem();
                ItemStack stack2 = playerEntity.getOffhandItem();

                if (!stack.isEmpty() && stack.getItem() instanceof IWand) {
                    SimpleMenuProvider provider = new SimpleMenuProvider(
                            (id, inventory, player) -> new SoulItemContainer(id, inventory, SoulUsingItemHandler.get(stack), stack, playerEntity.getUsedItemHand()), Component.translatable(stack.getDescriptionId()));
                    playerEntity.openMenu(provider, (buffer) -> buffer.writeBoolean(playerEntity.getUsedItemHand() == InteractionHand.MAIN_HAND));
                } else if (!stack2.isEmpty() && stack2.getItem() instanceof IWand){
                    SimpleMenuProvider provider = new SimpleMenuProvider(
                            (id, inventory, player) -> new SoulItemContainer(id, inventory, SoulUsingItemHandler.get(stack2), stack2, playerEntity.getUsedItemHand()), Component.translatable(stack2.getDescriptionId()));
                    playerEntity.openMenu(provider, (buffer) -> buffer.writeBoolean(playerEntity.getUsedItemHand() == InteractionHand.OFF_HAND));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
