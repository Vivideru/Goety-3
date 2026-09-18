package com.Vivideru.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemHooks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * An ordinary tamed wolf raised at a Howling Totem becomes a Gray Warg. Vanilla wolves answer right-clicks by
 * sitting down before any held item is used, so the Waystone has to be handled before the wolf's own interaction.
 */
@EventBusSubscriber(modid = Goety.MOD_ID)
public final class GrayWargEvents {
    private GrayWargEvents() {
    }

    @SubscribeEvent
    public static void onWolfWaystone(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Wolf wolf)) {
            return;
        }
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        if (!(stack.getItem() instanceof WaystoneItem) || !wolf.isTame() || wolf.getOwner() != player) {
            return;
        }
        if (event.getLevel().isClientSide) {
            return;
        }
        InteractionResult result = WolfTotemHooks.tryLinkToTotem(new WolfTotemHooks.ItemStackAccess(stack), player, wolf, event.getHand());
        if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}
