package com.Vivideru.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.Hellhound;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemHooks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Offering a Netherite Ingot to one of three Hellhounds gathered at a Howling Totem fuses them into a Cerberus.
 */
@EventBusSubscriber(modid = Goety.MOD_ID)
public final class CerberusFusionEvents {
    private CerberusFusionEvents() {
    }

    @SubscribeEvent
    public static void onHellhoundOffering(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Hellhound hellhound)) {
            return;
        }
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        if (!stack.is(Items.NETHERITE_INGOT) || !(hellhound instanceof IOwned owned) || owned.getTrueOwner() != player) {
            return;
        }
        if (event.getLevel().isClientSide) {
            return;
        }
        if (WolfTotemHooks.tryFuseCerberus(player, hellhound, event.getHand())) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
