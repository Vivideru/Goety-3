package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.block.BlockItemBase;
import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Vivideru.Goety.common.blocks.VivideruBlocks;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VivideruItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Goety.MOD_ID);

    public static final DeferredHolder<Item, Item> CURSED_METAL_WOLF_ARMOR = ITEMS.register("cursed_metal_wolf_armor", CursedMetalWolfArmorItem::new);
    public static final DeferredHolder<Item, Item> DARK_WOLF_ARMOR = ITEMS.register("dark_wolf_armor", DarkWolfArmorItem::new);
    public static final DeferredHolder<Item, Item> BLACK_BEAST_CURSED_ARMOR = ITEMS.register("black_beast_cursed_armor", CursedBlackBeastArmorItem::new);
    public static final DeferredHolder<Item, Item> BLACK_BEAST_DARK_ARMOR = ITEMS.register("black_beast_dark_armor", DarkBlackBeastArmorItem::new);
    public static final DeferredHolder<Item, Item> WARG_CURSED_ARMOR = ITEMS.register("warg_cursed_armor", CursedWargArmorItem::new);
    public static final DeferredHolder<Item, Item> WARG_DARK_ARMOR = ITEMS.register("warg_dark_armor", DarkWargArmorItem::new);
    public static final DeferredHolder<Item, Item> WARG_SPAWN_EGG = ITEMS.register("warg_spawn_egg",
            () -> new ServantSpawnEggItem(VivideruEntityTypes.WARG, 0x17141B, 0x6B6572, new Item.Properties()));
    public static final DeferredHolder<Item, Item> WOLF_TOTEM = ITEMS.register("wolf_totem",
            () -> new BlockItemBase(VivideruBlocks.WOLF_TOTEM.get()));

    @SuppressWarnings("removal")
    public static void init() {
        ITEMS.register(Goety.getModEventBus());
    }

    public static boolean isCursedMetalWolfArmor(ItemStack stack) {
        return stack.is(CURSED_METAL_WOLF_ARMOR.get());
    }

    public static boolean isVivideruWolfArmor(ItemStack stack) {
        return stack.is(CURSED_METAL_WOLF_ARMOR.get()) || stack.is(DARK_WOLF_ARMOR.get());
    }

    public static boolean isVivideruBlackBeastArmor(ItemStack stack) {
        return stack.is(BLACK_BEAST_CURSED_ARMOR.get()) || stack.is(BLACK_BEAST_DARK_ARMOR.get());
    }

    public static boolean isVivideruBodyArmor(ItemStack stack) {
        return isVivideruWolfArmor(stack) || isVivideruBlackBeastArmor(stack) || isWargArmor(stack);
    }

    public static boolean isWargArmor(ItemStack stack) {
        return stack.is(WARG_CURSED_ARMOR.get()) || stack.is(WARG_DARK_ARMOR.get());
    }
}
