package com.Polarice3.Goety.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

public final class ModBlockCodecs {
    private ModBlockCodecs() {
    }

    public static <T extends Block> MapCodec<T> singleton(T block) {
        return MapCodec.unit(block);
    }
}
