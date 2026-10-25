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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlinthBlockEntity>> PLINTH = BLOCK_ENTITY.register("plinth",
            () -> BlockEntityType.Builder.of(PlinthBlockEntity::new, VivideruBlocks.PLINTH_STONE.get(), VivideruBlocks.PLINTH_DEEPSLATE.get(), VivideruBlocks.PLINTH_OMINOUS_STONE.get(), VivideruBlocks.PLINTH_NETHER_BRICK.get(), VivideruBlocks.PLINTH_BLACKSTONE.get(), VivideruBlocks.PLINTH_END_STONE.get(), VivideruBlocks.PLINTH_HIGHROCK.get(), VivideruBlocks.PLINTH_MARBLE.get(), VivideruBlocks.PLINTH_PRISMARINE.get(), VivideruBlocks.PLINTH_CRYPT_STONE.get(), VivideruBlocks.PLINTH_DARK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SealerBlockEntity>> SEALER = BLOCK_ENTITY.register("sealer",
            () -> BlockEntityType.Builder.of(SealerBlockEntity::new, VivideruBlocks.SEALER.get()).build(null));

    @SuppressWarnings("removal")
    public static void init() {
        BLOCK_ENTITY.register(Goety.getModEventBus());
    }
}
