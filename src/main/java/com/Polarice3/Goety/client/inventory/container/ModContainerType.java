package com.Polarice3.Goety.client.inventory.container;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModContainerType {
    public static DeferredRegister<MenuType<?>> CONTAINER_TYPE = DeferredRegister.create(BuiltInRegistries.MENU, Goety.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<SoulItemContainer>> WAND = CONTAINER_TYPE.register("wand",
        () -> IMenuTypeExtension.create(SoulItemContainer::createContainerClientSide));

    public static final DeferredHolder<MenuType<?>, MenuType<FocusBagContainer>> FOCUS_BAG = CONTAINER_TYPE.register("focus_bag",
            () -> IMenuTypeExtension.create(FocusBagContainer::createContainerClientSide));

    public static final DeferredHolder<MenuType<?>, MenuType<FocusPackContainer>> FOCUS_PACK = CONTAINER_TYPE.register("focus_pack",
            () -> IMenuTypeExtension.create(FocusPackContainer::createContainerClientSide));

    public static final DeferredHolder<MenuType<?>, MenuType<BrewBagContainer>> BREW_BAG = CONTAINER_TYPE.register("brew_bag",
            () -> IMenuTypeExtension.create(BrewBagContainer::createContainerClientSide));

    public static final DeferredHolder<MenuType<?>, MenuType<EternalCauldronContainer>> ETERNAL_CAULDRON = CONTAINER_TYPE.register("eternal_cauldron",
            () -> IMenuTypeExtension.create(EternalCauldronContainer::createContainerClientSide));

    public static final DeferredHolder<MenuType<?>, MenuType<DarkAnvilMenu>> DARK_ANVIL = CONTAINER_TYPE.register("dark_anvil",
            () -> IMenuTypeExtension.create(DarkAnvilMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CraftingFocusMenu>> CRAFTING_FOCUS = CONTAINER_TYPE.register("crafting_focus",
            () -> IMenuTypeExtension.create(CraftingFocusMenu::new));
}
