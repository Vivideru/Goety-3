package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.handler.BrewBagItemHandler;
import com.Polarice3.Goety.common.items.handler.EternalCauldronItemHandler;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Goety.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // NeoForge 1.21 item capabilities are registered through the mod bus instead of Item#initCapabilities.
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new FocusBagItemHandler(stack, 11), ModItems.FOCUS_BAG.get());
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new FocusBagItemHandler(stack, 21), ModItems.FOCUS_PACK.get());
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new BrewBagItemHandler(stack), ModItems.BREW_BAG.get());
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new EternalCauldronItemHandler(stack), ModItems.ETERNAL_CAULDRON.get());

        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new SoulUsingItemHandler(stack),
                ModItems.DARK_WAND.get(),
                ModItems.OMINOUS_STAFF.get(),
                ModItems.NECRO_STAFF.get(),
                ModItems.GEO_STAFF.get(),
                ModItems.WIND_STAFF.get(),
                ModItems.STORM_STAFF.get(),
                ModItems.FROST_STAFF.get(),
                ModItems.WILD_STAFF.get(),
                ModItems.ABYSS_STAFF.get(),
                ModItems.VOID_STAFF.get(),
                ModItems.NETHER_STAFF.get(),
                ModItems.NAMELESS_STAFF.get());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.PEDESTAL.get(), (blockEntity, context) -> blockEntity.itemStackHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.DARK_ALTAR.get(), (blockEntity, context) -> blockEntity.itemStackHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.VOID_SHRINE.get(), (blockEntity, context) -> blockEntity.itemStackHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.HAUNTED_JUG.get(), (blockEntity, context) -> blockEntity.getFluidTank());
    }
}
