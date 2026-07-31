package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBanners {
    public static final DeferredRegister<BannerPattern> BANNER_PATTERNS = DeferredRegister.create(Registries.BANNER_PATTERN, Goety.MOD_ID);

    @SuppressWarnings("removal")
    public static void init(){
        BANNER_PATTERNS.register(com.Polarice3.Goety.Goety.getModEventBus());
    }

    public static final DeferredHolder<BannerPattern, BannerPattern> CROSS = create("cross");
    public static final DeferredHolder<BannerPattern, BannerPattern> GALE = create("gale");
    public static final DeferredHolder<BannerPattern, BannerPattern> MOON = create("moon");

    private static DeferredHolder<BannerPattern, BannerPattern> create(String name) {
        return BANNER_PATTERNS.register(name, () -> new BannerPattern(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, name), "block.goety.banner." + name));
    }
}
