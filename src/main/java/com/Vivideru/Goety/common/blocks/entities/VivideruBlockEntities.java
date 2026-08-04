package com.Vivideru.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.blocks.VivideruBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VivideruBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Goety.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WolfTotemBlockEntity>> WOLF_TOTEM = BLOCK_ENTITY.register("wolf_totem",
            () -> BlockEntityType.Builder.of(WolfTotemBlockEntity::new, VivideruBlocks.WOLF_TOTEM.get()).build(null));

    @SuppressWarnings("removal")
    public static void init() {
        BLOCK_ENTITY.register(Goety.getModEventBus());
    }
}
