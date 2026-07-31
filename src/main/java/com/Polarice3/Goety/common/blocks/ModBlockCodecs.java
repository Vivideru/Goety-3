package com.Polarice3.Goety.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

public final class ModBlockCodecs {
    private ModBlockCodecs() {
    }

    public static <T extends Block> MapCodec<T> singleton(T block) {
        // Many Goety blocks are registry singletons with fixed constructors; a unit codec preserves that shape for 1.21's required block codec hook.
        return MapCodec.unit(block);
    }
}
