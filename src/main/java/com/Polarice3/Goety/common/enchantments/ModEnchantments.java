package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> SOUL_EATER = key("soul_eater");
    public static final ResourceKey<Enchantment> WANTING = key("wanting");
    public static final ResourceKey<Enchantment> POTENCY = key("potency");
    public static final ResourceKey<Enchantment> RADIUS = key("radius");
    public static final ResourceKey<Enchantment> BURNING = key("burning");
    public static final ResourceKey<Enchantment> RANGE = key("range");
    public static final ResourceKey<Enchantment> ABSORB = key("absorb");
    public static final ResourceKey<Enchantment> MAGNET = key("magnet");
    public static final ResourceKey<Enchantment> DURATION = key("duration");
    public static final ResourceKey<Enchantment> VELOCITY = key("velocity");
    public static final ResourceKey<Enchantment> ROYALTY = key("royalty");
    public static final ResourceKey<Enchantment> FEALTY = key("fealty");
    public static final ResourceKey<Enchantment> HARDY = key("hardy");

    public static void register(IEventBus modEventBus) {
        // Minecraft 1.21 enchantments are data-driven registry entries, so this method only preserves the old registration call site.
    }

    public static Holder<Enchantment> holder(Entity levelSource, ResourceKey<Enchantment> enchantment) {
        return levelSource.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
    }

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, name));
    }
}
