package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.Polarice3.Goety.common.items.curios.WitchRobeItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class CExtractPotionKeyPacket {
    public static void encode(CExtractPotionKeyPacket packet, FriendlyByteBuf buffer) {
    }

    public static CExtractPotionKeyPacket decode(FriendlyByteBuf buffer) {
        return new CExtractPotionKeyPacket();
    }

    public static void consume(CExtractPotionKeyPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack stack = CuriosFinder.findCurio(playerEntity, itemStack -> itemStack.getItem() instanceof WitchRobeItem);

                if (!stack.isEmpty()){
                    WitchRobeInventory inventory = ModSaveInventory.getInstance().getWitchRobeInventory(WitchRobeItem.getInventoryId(stack), playerEntity);
                    inventory.extractPotions();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
