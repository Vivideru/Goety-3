package com.Vivideru.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.utils.TotemFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.Vivideru.Goety.common.items.magic.FocusBagBinding;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class SFocusBagSyncPacket {
    private final UUID bagId;
    private final List<ItemStack> contents;

    public SFocusBagSyncPacket(UUID bagId, List<ItemStack> contents) {
        this.bagId = bagId;
        this.contents = contents.stream().map(ItemStack::copy).toList();
    }

    public static void encode(SFocusBagSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.bagId);
        buffer.writeVarInt(packet.contents.size());
        for (ItemStack stack : packet.contents) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buffer, stack);
        }
    }

    public static SFocusBagSyncPacket decode(FriendlyByteBuf buffer) {
        UUID bagId = buffer.readUUID();
        int size = buffer.readVarInt();
        List<ItemStack> contents = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            contents.add(ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buffer));
        }
        return new SFocusBagSyncPacket(bagId, contents);
    }

    public static void consume(SFocusBagSyncPacket packet, Supplier<ModNetwork.Context> context) {
        context.get().enqueueWork(() -> {
            Player player = Goety.PROXY.getPlayer();
            if (player == null || FocusBagBinding.getBoundId(WandUtil.findWand(player)).filter(packet.bagId::equals).isEmpty()) {
                return;
            }

            FocusBagBinding.BagReference reference = TotemFinder.findBagReference(player);
            if (!reference.isPresent() || FocusBagBinding.getBagId(reference.stack()).filter(packet.bagId::equals).isEmpty()) {
                return;
            }

            FocusBagItemHandler handler = FocusBagItemHandler.get(reference.stack());
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                ItemStack stack = slot < packet.contents.size() ? packet.contents.get(slot).copy() : ItemStack.EMPTY;
                handler.setStackInSlot(slot, stack);
            }
            // Nested item handlers keep their own client-side storage, so write the synchronized bag back through its parents.
            reference.save();

        });
        context.get().setPacketHandled(true);
    }

    public static void sendTo(ServerPlayer player, ItemStack bag) {
        FocusBagBinding.getBagId(bag).ifPresent(id -> {
            FocusBagItemHandler handler = FocusBagItemHandler.get(bag);
            List<ItemStack> contents = new ArrayList<>(handler.getSlots());
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                contents.add(handler.getStackInSlot(slot).copy());
            }
            ModNetwork.sendTo(player, new SFocusBagSyncPacket(id, contents));
        });
    }
}
