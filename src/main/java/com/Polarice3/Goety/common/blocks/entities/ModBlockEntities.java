package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

public class ModBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Goety.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SarcophagusBlockEntity>> SARCOPHAGUS = BLOCK_ENTITY.register("sarcophagus", () -> BlockEntityType.Builder.of(SarcophagusBlockEntity::new,
            ModBlocks.SHADE_SARCOPHAGUS.get(), ModBlocks.STONE_SARCOPHAGUS.get(), ModBlocks.DEEPSLATE_SARCOPHAGUS.get(), ModBlocks.SANDSTONE_SARCOPHAGUS.get(), ModBlocks.OMINOUS_SARCOPHAGUS.get(), ModBlocks.CRYPT_SARCOPHAGUS.get()).build(null));

    private static Stream<Block> boundBlocks() {
        // Block entity registration can run while unrelated block holders from the same DeferredRegister are still unbound.
        return ModBlocks.BLOCKS.getEntries().stream()
                .filter(DeferredHolder::isBound)
                .map(DeferredHolder::get);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaBlockEntity>> ARCA = BLOCK_ENTITY.register("arca",
            () -> BlockEntityType.Builder.of(ArcaBlockEntity::new, ModBlocks.ARCA_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CursedInfuserBlockEntity>> CURSED_INFUSER = BLOCK_ENTITY.register("cursed_infuser",
            () -> BlockEntityType.Builder.of(CursedInfuserBlockEntity::new, ModBlocks.CURSED_INFUSER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrimInfuserBlockEntity>> GRIM_INFUSER = BLOCK_ENTITY.register("grim_infuser",
            () -> BlockEntityType.Builder.of(GrimInfuserBlockEntity::new, ModBlocks.GRIM_INFUSER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CursedCageBlockEntity>> CURSED_CAGE = BLOCK_ENTITY.register("cursed_cage",
            () -> BlockEntityType.Builder.of(CursedCageBlockEntity::new, ModBlocks.CURSED_CAGE_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DarkAltarBlockEntity>> DARK_ALTAR = BLOCK_ENTITY.register("dark_altar",
            () -> {
                Block[] blocks = boundBlocks()
                        .filter(block -> block instanceof DarkAltarBlock)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(DarkAltarBlockEntity::new, blocks).build(null);
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PedestalBlockEntity>> PEDESTAL = BLOCK_ENTITY.register("pedestal",
            () -> {
                Block[] blocks = boundBlocks()
                        .filter(block -> block instanceof PedestalBlock)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(PedestalBlockEntity::new, blocks).build(null);
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoulAbsorberBlockEntity>> SOUL_ABSORBER = BLOCK_ENTITY.register("soul_absorber",
            () -> BlockEntityType.Builder.of(SoulAbsorberBlockEntity::new, ModBlocks.SOUL_ABSORBER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoulMenderBlockEntity>> SOUL_MENDER = BLOCK_ENTITY.register("soul_mender",
            () -> BlockEntityType.Builder.of(SoulMenderBlockEntity::new, ModBlocks.SOUL_MENDER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IceBouquetTrapBlockEntity>> ICE_BOUQUET_TRAP = BLOCK_ENTITY.register("ice_bouquet_trap",
            () -> BlockEntityType.Builder.of(IceBouquetTrapBlockEntity::new, ModBlocks.ICE_BOUQUET_TRAP.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindBlowerBlockEntity>> WIND_BLOWER = BLOCK_ENTITY.register("wind_blower",
            () -> BlockEntityType.Builder.of(WindBlowerBlockEntity::new, ModBlocks.WIND_BLOWER.get(), ModBlocks.MARBLE_WIND_BLOWER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonanceCrystalBlockEntity>> RESONANCE_CRYSTAL = BLOCK_ENTITY.register("resonance_crystal",
            () -> BlockEntityType.Builder.of(ResonanceCrystalBlockEntity::new, ModBlocks.RESONANCE_CRYSTAL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkDevourerBlockEntity>> SCULK_DEVOURER = BLOCK_ENTITY.register("sculk_devourer",
            () -> BlockEntityType.Builder.of(SculkDevourerBlockEntity::new, ModBlocks.SCULK_DEVOURER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkConverterBlockEntity>> SCULK_CONVERTER = BLOCK_ENTITY.register("sculk_converter",
            () -> BlockEntityType.Builder.of(SculkConverterBlockEntity::new, ModBlocks.SCULK_CONVERTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkGrowerBlockEntity>> SCULK_GROWER = BLOCK_ENTITY.register("sculk_grower",
            () -> BlockEntityType.Builder.of(SculkGrowerBlockEntity::new, ModBlocks.SCULK_GROWER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForbiddenGrassBlockEntity>> FORBIDDEN_GRASS = BLOCK_ENTITY.register("forbidden_grass",
            () -> BlockEntityType.Builder.of(ForbiddenGrassBlockEntity::new, ModBlocks.FORBIDDEN_GRASS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HookBellBlockEntity>> HOOK_BELL = BLOCK_ENTITY.register("hook_bell",
            () -> BlockEntityType.Builder.of(HookBellBlockEntity::new, ModBlocks.HOOK_BELL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShriekObeliskBlockEntity>> SHRIEKING_OBELISK = BLOCK_ENTITY.register("shriek_obelisk",
            () -> BlockEntityType.Builder.of(ShriekObeliskBlockEntity::new, ModBlocks.SHRIEKING_OBELISK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NecroBrazierBlockEntity>> NECRO_BRAZIER = BLOCK_ENTITY.register("necro_brazier",
            () -> BlockEntityType.Builder.of(NecroBrazierBlockEntity::new, ModBlocks.NECRO_BRAZIER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnimatorBlockEntity>> ANIMATOR = BLOCK_ENTITY.register("animator",
            () -> BlockEntityType.Builder.of(AnimatorBlockEntity::new, ModBlocks.ANIMATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlackCrystalBlockEntity>> BLACK_CRYSTAL = BLOCK_ENTITY.register("black_crystal",
            () -> BlockEntityType.Builder.of(BlackCrystalBlockEntity::new, ModBlocks.BLACK_CRYSTAL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoulCandlestickBlockEntity>> SOUL_CANDLESTICK = BLOCK_ENTITY.register("soul_candlestick",
            () -> BlockEntityType.Builder.of(SoulCandlestickBlockEntity::new, ModBlocks.SOUL_CANDLESTICK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NecroticCandlestickBlockEntity>> NECROTIC_CANDLESTICK = BLOCK_ENTITY.register("necrotic_candlestick",
            () -> BlockEntityType.Builder.of(NecroticCandlestickBlockEntity::new, ModBlocks.NECROTIC_GOLD_CANDLESTICK.get(), ModBlocks.WALL_NECROTIC_GOLD_CANDLESTICK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrewCauldronBlockEntity>> BREWING_CAULDRON = BLOCK_ENTITY.register("witch_cauldron",
            () -> BlockEntityType.Builder.of(BrewCauldronBlockEntity::new, ModBlocks.BREWING_CAULDRON.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HauntedMirrorBlockEntity>> HAUNTED_MIRROR = BLOCK_ENTITY.register("haunted_mirror",
            () -> BlockEntityType.Builder.of(HauntedMirrorBlockEntity::new, ModBlocks.HAUNTED_MIRROR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HauntedJugBlockEntity>> HAUNTED_JUG = BLOCK_ENTITY.register("haunted_jug",
            () -> BlockEntityType.Builder.of(HauntedJugBlockEntity::new, ModBlocks.HAUNTED_JUG.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpiderNestBlockEntity>> SPIDER_NEST = BLOCK_ENTITY.register("spider_nest",
            () -> BlockEntityType.Builder.of(SpiderNestBlockEntity::new, ModBlocks.SPIDER_NEST.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GravestoneBlockEntity>> SHADE_GRAVESTONE = BLOCK_ENTITY.register("shade_gravestone",
            () -> BlockEntityType.Builder.of(GravestoneBlockEntity::new, ModBlocks.SHADE_GRAVESTONE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OssuaryBlockEntity>> SHADE_OSSUARY = BLOCK_ENTITY.register("shade_ossuary",
            () -> BlockEntityType.Builder.of(OssuaryBlockEntity::new, ModBlocks.SHADE_OSSUARY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlazingCageBlockEntity>> BLAZING_CAGE = BLOCK_ENTITY.register("blazing_cage",
            () -> BlockEntityType.Builder.of(BlazingCageBlockEntity::new, ModBlocks.BLAZING_CAGE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OminousPyreBlockEntity>> OMINOUS_PYRE = BLOCK_ENTITY.register("ominous_pyre",
            () -> BlockEntityType.Builder.of(OminousPyreBlockEntity::new, ModBlocks.OMINOUS_PYRE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OminousIdolBlockEntity>> OMINOUS_IDOL = BLOCK_ENTITY.register("ominous_idol",
            () -> BlockEntityType.Builder.of(OminousIdolBlockEntity::new, ModBlocks.OMINOUS_IDOL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PithosBlockEntity>> PITHOS = BLOCK_ENTITY.register("pithos",
            () -> BlockEntityType.Builder.of(PithosBlockEntity::new, ModBlocks.PITHOS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpiderMotherDenBlockEntity>> SPIDER_MOTHER_DEN = BLOCK_ENTITY.register("spider_mother_den",
            () -> BlockEntityType.Builder.of(SpiderMotherDenBlockEntity::new, ModBlocks.SPIDER_MOTHER_DEN.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VoidSpawnerBlockEntity>> VOID_SPAWNER = BLOCK_ENTITY.register("void_spawner",
            () -> BlockEntityType.Builder.of(VoidSpawnerBlockEntity::new, ModBlocks.VOID_SPAWNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VoidVaultBlockEntity>> VOID_VAULT = BLOCK_ENTITY.register("void_vault",
            () -> BlockEntityType.Builder.of(VoidVaultBlockEntity::new, ModBlocks.VOID_VAULT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VoidFrameBlockEntity>> VOID_FRAME = BLOCK_ENTITY.register("void_frame",
            () -> BlockEntityType.Builder.of(VoidFrameBlockEntity::new, ModBlocks.VOID_FRAME.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VoidShrineBlockEntity>> VOID_SHRINE = BLOCK_ENTITY.register("void_shrine",
            () -> BlockEntityType.Builder.of(VoidShrineBlockEntity::new, ModBlocks.VOID_SHRINE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UrnBlockEntity>> URN = BLOCK_ENTITY.register("crypt_urn",
            () -> BlockEntityType.Builder.of(UrnBlockEntity::new, ModBlocks.CRYPT_URN.get(), ModBlocks.STASH_URN.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpiderSacBlockEntity>> SPIDER_SAC = BLOCK_ENTITY.register("spider_sac",
            () -> BlockEntityType.Builder.of(SpiderSacBlockEntity::new, ModBlocks.SPIDER_SAC.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HoleBlockEntity>> HOLE = BLOCK_ENTITY.register("hole",
            () -> BlockEntityType.Builder.of(HoleBlockEntity::new, ModBlocks.HOLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PartLiquidBlockEntity>> PART_LIQUID = BLOCK_ENTITY.register("part_liquid",
            () -> BlockEntityType.Builder.of(PartLiquidBlockEntity::new, ModBlocks.PART_LIQUID.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NightBeaconBlockEntity>> NIGHT_BEACON = BLOCK_ENTITY.register("night_beacon",
            () -> BlockEntityType.Builder.of(NightBeaconBlockEntity::new, ModBlocks.NIGHT_BEACON.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VoidBarrelBlockEntity>> VOID_BARREL = BLOCK_ENTITY.register("void_barrel",
            () -> BlockEntityType.Builder.of(VoidBarrelBlockEntity::new, ModBlocks.VOID_BARREL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagicLightBlockEntity>> MAGIC_LIGHT = BLOCK_ENTITY.register("magic_light",
            () -> BlockEntityType.Builder.of(MagicLightBlockEntity::new, ModBlocks.SOUL_LIGHT_BLOCK.get(), ModBlocks.GLOW_LIGHT_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MandalaBlockEntity>> MANDALA = BLOCK_ENTITY.register("mandala",
            () -> BlockEntityType.Builder.of(MandalaBlockEntity::new, ModBlocks.MANDALA.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OminousStatueBlockEntity>> OMINOUS_STATUE = BLOCK_ENTITY.register("ominous_statue",
            () -> BlockEntityType.Builder.of(OminousStatueBlockEntity::new, ModBlocks.OMINOUS_STATUE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OminousBrazierStatueBlockEntity>> OMINOUS_BRAZIER_STATUE = BLOCK_ENTITY.register("ominous_brazier_statue",
            () -> BlockEntityType.Builder.of(OminousBrazierStatueBlockEntity::new, ModBlocks.OMINOUS_BRAZIER_STATUE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThroneBlockEntity>> THRONE = BLOCK_ENTITY.register("throne",
            () -> {
                Block[] blocks = boundBlocks()
                        .filter(block -> block instanceof ThroneBlock)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(ThroneBlockEntity::new, blocks).build(null);
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TallSkullBlockEntity>> TALL_SKULL = BLOCK_ENTITY.register("tall_skull",
            () -> BlockEntityType.Builder.of(TallSkullBlockEntity::new, ModBlocks.TALL_SKULL_BLOCK.get(), ModBlocks.WALL_TALL_SKULL_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneGolemSkullBlockEntity>> REDSTONE_GOLEM_SKULL = BLOCK_ENTITY.register("redstone_golem_skull",
            () -> BlockEntityType.Builder.of(RedstoneGolemSkullBlockEntity::new, ModBlocks.REDSTONE_GOLEM_SKULL_BLOCK.get(), ModBlocks.WALL_REDSTONE_GOLEM_SKULL_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GraveGolemSkullBlockEntity>> GRAVE_GOLEM_SKULL = BLOCK_ENTITY.register("grave_golem_skull",
            () -> BlockEntityType.Builder.of(GraveGolemSkullBlockEntity::new, ModBlocks.GRAVE_GOLEM_SKULL_BLOCK.get(), ModBlocks.WALL_GRAVE_GOLEM_SKULL_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneMonstrosityHeadBlockEntity>> REDSTONE_MONSTROSITY_HEAD = BLOCK_ENTITY.register("redstone_monstrosity_head",
            () -> BlockEntityType.Builder.of(RedstoneMonstrosityHeadBlockEntity::new, ModBlocks.REDSTONE_MONSTROSITY_HEAD_BLOCK.get(), ModBlocks.WALL_REDSTONE_MONSTROSITY_HEAD_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModChestBlockEntity>> MOD_CHEST = BLOCK_ENTITY.register("chest",
            () -> {
                Block[] blocks = boundBlocks()
                        .filter(block -> block instanceof ModChestBlock
                                && !(block instanceof ModTrappedChestBlock)
                                && !(block instanceof CryptChestBlock)
                                && !(block instanceof LoftyChestBlock))
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(ModChestBlockEntity::new, blocks).build(null);
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModTrappedChestBlockEntity>> MOD_TRAPPED_CHEST = BLOCK_ENTITY.register("trapped_chest",
            () -> {
                Block[] blocks = boundBlocks()
                        .filter(block -> block instanceof ModTrappedChestBlock)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(ModTrappedChestBlockEntity::new, blocks).build(null);
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CryptChestBlockEntity>> CRYPT_CHEST = BLOCK_ENTITY.register("crypt_chest",
            () -> BlockEntityType.Builder.of(CryptChestBlockEntity::new,
                    ModBlocks.CRYPT_CHEST.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LoftyChestBlockEntity>> LOFTY_CHEST = BLOCK_ENTITY.register("lofty_chest",
            () -> BlockEntityType.Builder.of(LoftyChestBlockEntity::new,
                    ModBlocks.LOFTY_CHEST.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModSignBlockEntity>> SIGN_BLOCK_ENTITIES = BLOCK_ENTITY.register("sign",
            () -> {
                Block[] blocks = boundBlocks()
                        .filter(block -> block instanceof ModStandSignBlock || block instanceof ModWallSignBlock)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(ModSignBlockEntity::new, blocks).build(null);
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModHangingSignBlockEntity>> HANGING_SIGN_BLOCK_ENTITIES = BLOCK_ENTITY.register("hanging_sign",
            () -> {
                Block[] blocks = boundBlocks()
                        .filter(block -> block instanceof ModHangingSignBlock || block instanceof ModWallHangingSignBlock)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(ModHangingSignBlockEntity::new, blocks).build(null);
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlushieBlockEntity>> PLUSHIE = BLOCK_ENTITY.register("plushie",
            () -> {
                Block[] blocks = boundBlocks()
                        .filter(block -> block instanceof PlushieBlock)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(PlushieBlockEntity::new, blocks).build(null);
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculpturedStatueBlockEntity>> SCULPTURED_STATUE = BLOCK_ENTITY.register("sculptured_statue",
            () -> BlockEntityType.Builder.of(SculpturedStatueBlockEntity::new,
                    boundBlocks().filter(block -> block instanceof SculpturedStatueBlock).toArray(Block[]::new)).build(null));

}
