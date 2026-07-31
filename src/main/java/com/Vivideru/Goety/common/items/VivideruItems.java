package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VivideruItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Goety.MOD_ID);

    public static final DeferredHolder<Item, Item> CURSED_METAL_WOLF_ARMOR = ITEMS.register("cursed_metal_wolf_armor", CursedMetalWolfArmorItem::new);

    @SuppressWarnings("removal")
    public static void init() {
        ITEMS.register(Goety.getModEventBus());
    }

    public static boolean isCursedMetalWolfArmor(ItemStack stack) {
        return stack.is(CURSED_METAL_WOLF_ARMOR.get());
    }
}
