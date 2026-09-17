package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.block.BlockItemBase;
import com.Polarice3.Goety.common.items.ModSpawnEggItem;
import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Vivideru.Goety.common.blocks.VivideruBlocks;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import com.Vivideru.Goety.common.entities.ally.Warg;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
            () -> new WargSpawnEggItem(VivideruEntityTypes.WARG, 0x17141B, 0x6B6572, new Item.Properties(), Warg.Variant.BLACK, false));
    public static final DeferredHolder<Item, Item> WARG_WINTER_SPAWN_EGG = ITEMS.register("winter_warg_spawn_egg",
            () -> new WargSpawnEggItem(VivideruEntityTypes.WARG, 0xDCE7ED, 0xFFFFFF, new Item.Properties(), Warg.Variant.COLD, false));
    public static final DeferredHolder<Item, Item> WARG_STORM_SPAWN_EGG = ITEMS.register("storm_warg_spawn_egg",
            () -> new WargSpawnEggItem(VivideruEntityTypes.WARG, 0x6E422F, 0xB36A43, new Item.Properties(), Warg.Variant.MODERATE, false));
    public static final DeferredHolder<Item, Item> WARG_SKELETAL_SPAWN_EGG = ITEMS.register("skeletal_warg_spawn_egg",
            () -> new WargSpawnEggItem(VivideruEntityTypes.WARG, 0xE8E8E8, 0xFFFFFF, new Item.Properties(), Warg.Variant.SKELETAL, false));
    public static final DeferredHolder<Item, Item> WARG_GRAY_SPAWN_EGG = ITEMS.register("gray_warg_spawn_egg",
            () -> new WargSpawnEggItem(VivideruEntityTypes.WARG, 0x7E8084, 0xC8C8C8, new Item.Properties(), Warg.Variant.GRAY, false));
    public static final DeferredHolder<Item, Item> HOSTILE_WARG_SPAWN_EGG = ITEMS.register("hostile_warg_spawn_egg",
            () -> new ModSpawnEggItem(VivideruEntityTypes.WARG, 0x132025, 0x495065, hostileWargEggProperties()));
    public static final DeferredHolder<Item, Item> CERBERUS_SPAWN_EGG = ITEMS.register("cerberus_spawn_egg",
            () -> new ServantSpawnEggItem(VivideruEntityTypes.CERBERUS, 0x2B1210, 0xC1440E, new Item.Properties()));
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

    /**
     * The hostile egg is a normal spawn egg, so entity configuration is stored in its
     * default entity-data component instead of using the servant egg implementation.
     */
    private static Item.Properties hostileWargEggProperties() {
        CompoundTag entityData = new CompoundTag();
        entityData.putBoolean("isHostile", true);
        entityData.putInt("WargVariant", Warg.Variant.BLACK.ordinal());
        return new Item.Properties().component(DataComponents.ENTITY_DATA, CustomData.of(entityData));
    }
}
