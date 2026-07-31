package com.Vivideru.Goety.compat.apotheosis;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class ApotheosisCompat {
    public static final String MOD_ID = "goety_apotheosis";

    private static final Set<ResourceLocation> GOETY_WANDS = Set.of(
        goetyId("dark_wand"),
        goetyId("abyss_staff"),
        goetyId("storm_staff"),
        goetyId("necro_staff"),
        goetyId("nameless_staff"),
        goetyId("frost_staff"),
        goetyId("wild_staff"),
        goetyId("void_staff"),
        goetyId("nether_staff"),
        goetyId("geo_staff"),
        goetyId("wind_staff"),
        goetyId("ominous_staff")
    );

    private static final DeferredRegister<LootCategory> LOOT_CATEGORIES =
        DeferredRegister.create(Apoth.BuiltInRegs.LOOT_CATEGORY.key(), MOD_ID);

    public static final DeferredHolder<LootCategory, LootCategory> DARK_WAND_CATEGORY =
        LOOT_CATEGORIES.register(
            "dark_wand",
            () -> new LootCategory(ApotheosisCompat::isGoetyWand, ALObjects.EquipmentSlotGroups.MAINHAND, 0)
        );

    private ApotheosisCompat() {
    }

    public static void init(IEventBus modEventBus, ModContainer modContainer) {
        LOOT_CATEGORIES.register(modEventBus);
        GoetyApotheosisAttributes.ATTRIBUTES.register(modEventBus);
        modEventBus.addListener(GoetyApotheosisAttributes::addPlayerAttributes);
        NeoForge.EVENT_BUS.register(MasterStaffAttributeBridge.class);
        NeoForge.EVENT_BUS.register(GemPotencyCapHandler.class);
        NeoForge.EVENT_BUS.register(GemCombatEffects.class);
        modContainer.registerConfig(ModConfig.Type.SERVER, GoetyApotheosisConfig.SPEC, "goety/goety-apotheosis.toml");
    }

    public static boolean isGoetyWand(ItemStack stack) {
        return GOETY_WANDS.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    private static ResourceLocation goetyId(String path) {
        return ResourceLocation.fromNamespaceAndPath("goety", path);
    }
}
