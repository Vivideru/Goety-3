package com.Vivideru.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VivideruBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Goety.MOD_ID);

    public static final DeferredHolder<Block, WolfTotemBlock> WOLF_TOTEM = BLOCKS.register("wolf_totem", WolfTotemBlock::new);

    @SuppressWarnings("removal")
    public static void init() {
        BLOCKS.register(Goety.getModEventBus());
    }
}
