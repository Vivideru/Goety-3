package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ModPotPatterns {
    public static final DeferredRegister<DecoratedPotPattern> POT_PATTERNS = DeferredRegister.create(Registries.DECORATED_POT_PATTERN, Goety.MOD_ID);

    @SuppressWarnings("removal")
    public static void init(){
        ModPotPatterns.POT_PATTERNS.register(com.Polarice3.Goety.Goety.getModEventBus());
    }

    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> CROSS = create("cross_pottery_pattern");
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> DEAD = create("dead_pottery_pattern");
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> HAUNT = create("haunt_pottery_pattern");
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> NIGHT = create("night_pottery_pattern");
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> SOUL = create("soul_pottery_pattern");

    private static DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> create(String name) {
        return POT_PATTERNS.register(name, () -> new DecoratedPotPattern(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, name)));
    }

    private static Map<Item, ResourceKey<DecoratedPotPattern>> itemToPattern;

    @Nullable
    public static ResourceKey<DecoratedPotPattern> patternFor(Item item) {
        if (itemToPattern == null) {
            itemToPattern = Map.of(
                    ModItems.CROSS_POTTERY_SHERD.get(), CROSS.getKey(),
                    ModItems.DEAD_POTTERY_SHERD.get(), DEAD.getKey(),
                    ModItems.HAUNT_POTTERY_SHERD.get(), HAUNT.getKey(),
                    ModItems.NIGHT_POTTERY_SHERD.get(), NIGHT.getKey(),
                    ModItems.SOUL_POTTERY_SHERD.get(), SOUL.getKey());
        }
        return itemToPattern.get(item);
    }

    public static void addPatterns() {
    }
}
