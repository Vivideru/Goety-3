package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModSpawnEggs;
import com.Polarice3.Goety.common.items.ServantSpawnEggs;
import com.Polarice3.Goety.compat.patchouli.PatchouliIntegration;
import com.Polarice3.Goety.compat.patchouli.PatchouliLoaded;
import com.Vivideru.Goety.common.items.VivideruItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.resources.RegistryOps;

import java.util.Comparator;
import java.util.function.Predicate;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Goety.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(Goety.MOD_ID, () -> CreativeModeTab.builder()
            .icon(() -> ModItems.TOTEM_OF_SOULS.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.goety"))
            .withSearchBar()
            .displayItems((parameters, output) -> {
                if (PatchouliLoaded.PATCHOULI.isLoaded()){
                    output.accept(PatchouliIntegration.getBlackBook());
                    output.accept(PatchouliIntegration.getWitchesBrew());
                }
                output.accept(ModItems.TOTEM_OF_SOULS.get().getEmptyTotem());
                output.accept(ModItems.TOTEM_OF_SOULS.get().getFilledTotem());
                output.accept(ModItems.TOTEM_OF_ROOTS.get().getEmptyTotem());
                output.accept(ModItems.TOTEM_OF_ROOTS.get().getFilledTotem());
                ModItems.ITEMS.getEntries().forEach(i -> {
                    if (!ModItems.shouldSkipCreativeModTab(i.get()) && !(i.get() instanceof BlockItem) && !ModItems.isFocus(i.get())) {
                        output.accept(i.get());
                        if (i.get() == ModItems.NETHERITE_RAVAGER_ARMOR.get()) {
                            output.accept(VivideruItems.CURSED_METAL_WOLF_ARMOR.get());
                            output.accept(VivideruItems.DARK_WOLF_ARMOR.get());
                            output.accept(VivideruItems.BLACK_BEAST_CURSED_ARMOR.get());
                            output.accept(VivideruItems.BLACK_BEAST_DARK_ARMOR.get());
                            output.accept(VivideruItems.WARG_CURSED_ARMOR.get());
                            output.accept(VivideruItems.WARG_DARK_ARMOR.get());
                        }
                    }
                });
                parameters.holders().lookup(Registries.PAINTING_VARIANT).ifPresent((p_270026_) -> {
                    generatePresetPaintings(output, parameters.holders(), p_270026_, (p_270037_) -> {
                        return p_270037_.is(ModTags.Paintings.MODDED_PAINTINGS);
                    }, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
                ModSpawnEggs.ITEMS.getEntries().forEach(i -> {
                    output.accept(i.get());
                });
                output.accept(VivideruItems.HOSTILE_WARG_SPAWN_EGG.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCK_TAB = CREATIVE_MODE_TABS.register(Goety.MOD_ID + "_block", () -> CreativeModeTab.builder()
            .icon(() -> ModBlocks.SHADE_STONE_CHISELED_BLOCK.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup.goety.block"))
            .withSearchBar()
            .displayItems((parameters, output) -> {
                ModItems.ITEMS.getEntries().forEach(i -> {
                    if (i.get() instanceof BlockItem) {
                        output.accept(i.get());
                    }
                });
                output.accept(VivideruItems.WOLF_TOTEM.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOCUS_TAB = CREATIVE_MODE_TABS.register(Goety.MOD_ID + "_focus", () -> CreativeModeTab.builder()
            .icon(() -> ModItems.FOCUS_BAG.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.goety.focus"))
            .displayItems((parameters, output) -> {
                ModItems.ITEMS.getEntries().forEach(i -> {
                    if (ModItems.isFocus(i.get())) {
                        output.accept(i.get());
                    }
                });
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SERVANT_TAB = CREATIVE_MODE_TABS.register(Goety.MOD_ID + "_servants", () -> CreativeModeTab.builder()
            .icon(() -> ModItems.SOUL_JAR.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.goety.servant"))
            .withSearchBar()
            .displayItems((parameters, output) -> {
                ServantSpawnEggs.ITEMS.getEntries().forEach(i -> {
                    output.accept(i.get());
                });
                // Vivideru servant eggs use a separate registry and must be added to the servant tab explicitly.
                output.accept(VivideruItems.WARG_SPAWN_EGG.get());
                output.accept(VivideruItems.WARG_WINTER_SPAWN_EGG.get());
                output.accept(VivideruItems.WARG_STORM_SPAWN_EGG.get());
                output.accept(VivideruItems.WARG_SKELETAL_SPAWN_EGG.get());
                output.accept(VivideruItems.WARG_GRAY_SPAWN_EGG.get());
                output.accept(VivideruItems.CERBERUS_SPAWN_EGG.get());
            }).build());

    private static final Comparator<Holder<PaintingVariant>> PAINTING_COMPARATOR = Comparator.comparing(Holder::value, Comparator.<PaintingVariant>comparingInt((p_270004_) -> {
        return p_270004_.height() * p_270004_.width();
    }).thenComparing(PaintingVariant::width));

    private static void generatePresetPaintings(CreativeModeTab.Output p_271007_, HolderLookup.Provider holders, HolderLookup.RegistryLookup<PaintingVariant> p_270618_, Predicate<Holder<PaintingVariant>> p_270878_, CreativeModeTab.TabVisibility p_270261_) {
        RegistryOps<Tag> registryops = holders.createSerializationContext(NbtOps.INSTANCE);
        p_270618_.listElements().filter(p_270878_).sorted(PAINTING_COMPARATOR).forEach((p_269979_) -> {
            ItemStack itemstack = new ItemStack(ModItems.HAUNTED_PAINTING.get());
            // Painting entity presets moved from EntityTag NBT to ENTITY_DATA custom data in 1.21.
            CustomData customdata = CustomData.EMPTY
                    .update(registryops, Painting.VARIANT_MAP_CODEC, p_269979_)
                    .getOrThrow()
                    .update(tag -> tag.putString("id", "minecraft:painting"));
            itemstack.set(DataComponents.ENTITY_DATA, customdata);
            p_271007_.accept(itemstack, p_270261_);
        });
    }

}
