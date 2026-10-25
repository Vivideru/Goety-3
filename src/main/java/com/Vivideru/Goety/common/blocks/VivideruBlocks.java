package com.Vivideru.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.init.ModSoundTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VivideruBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Goety.MOD_ID);

    public static final DeferredHolder<Block, WolfTotemBlock> WOLF_TOTEM = BLOCKS.register("wolf_totem", WolfTotemBlock::new);
    public static final DeferredHolder<Block, WhirlingCageBlock> WHIRLING_CAGE = BLOCKS.register("whirling_cage", WhirlingCageBlock::new);
    public static final DeferredHolder<Block, Block> CURSED_METAL_GRATE = BLOCKS.register("cursed_metal_grate",
            () -> new HalfTransparentBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .noOcclusion()
                    .dynamicShape()
                    .sound(ModSoundTypes.MOD_METAL)));

    public static final DeferredHolder<Block, PlinthBlock> PLINTH_STONE = BLOCKS.register("plinth_stone", () -> new PlinthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_DEEPSLATE = BLOCKS.register("plinth_deepslate", () -> new PlinthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_OMINOUS_STONE = BLOCKS.register("plinth_ominous_stone", () -> new PlinthBlock(ModBlocks.OminousStoneProperties()));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_NETHER_BRICK = BLOCKS.register("plinth_nether_brick", () -> new PlinthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_BLACKSTONE = BLOCKS.register("plinth_blackstone", () -> new PlinthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_END_STONE = BLOCKS.register("plinth_end_stone", () -> new PlinthBlock(ModBlocks.EndStoneProperties()));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_HIGHROCK = BLOCKS.register("plinth_highrock", () -> new PlinthBlock(ModBlocks.HighrockProperties()));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_MARBLE = BLOCKS.register("plinth_marble", () -> new PlinthBlock(ModBlocks.MarbleProperties()));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_PRISMARINE = BLOCKS.register("plinth_prismarine", () -> new PlinthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_CRYPT_STONE = BLOCKS.register("plinth_crypt_stone", () -> new PlinthBlock(ModBlocks.CryptStoneProperties()));
    public static final DeferredHolder<Block, PlinthBlock> PLINTH_DARK = BLOCKS.register("plinth_dark", () -> new PlinthBlock(ModBlocks.ShadeStoneProperties()));
    public static final DeferredHolder<Block, SealerBlock> SEALER = BLOCKS.register("sealer",
            () -> new SealerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(ModSoundTypes.MOD_METAL)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(SealerBlock.LIT) ? 7 : 0)));
    public static final DeferredHolder<Block, PolarCrystalBlock> POLAR_CRYSTAL = BLOCKS.register("polar_crystal",
            () -> new PolarCrystalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER)
                    .lightLevel(state -> state.getValue(PolarCrystalBlock.POWERED) ? 10 : 2)));

    static {
        BLOCKS.addAlias(Goety.location("hurricane_core"), Goety.location("whirling_cage"));
    }

    @SuppressWarnings("removal")
    public static void init() {
        BLOCKS.register(Goety.getModEventBus());
    }
}
