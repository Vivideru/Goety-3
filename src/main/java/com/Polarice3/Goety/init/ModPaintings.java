package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModPaintings {
    public static final DeferredRegister<PaintingVariant> PAINTING_VARIANTS = DeferredRegister.create(Registries.PAINTING_VARIANT, Goety.MOD_ID);

    @SuppressWarnings("removal")
    public static void init(){
        ModPaintings.PAINTING_VARIANTS.register(com.Polarice3.Goety.Goety.getModEventBus());
    }

    private static PaintingVariant painting(String name, int pixelWidth, int pixelHeight) {
        // PaintingVariant stores block-sized painting dimensions in 1.21; the renderer converts them back to texture pixels.
        return new PaintingVariant(pixelWidth / 16, pixelHeight / 16, ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, name));
    }

    public static final DeferredHolder<PaintingVariant, PaintingVariant> APOSTLE = PAINTING_VARIANTS.register("apostle", () -> painting("apostle", 16, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> MOVIE = PAINTING_VARIANTS.register("movie", () -> painting("movie", 16, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> KNUCKLES = PAINTING_VARIANTS.register("knuckles", () -> painting("knuckles", 16, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> SATURN = PAINTING_VARIANTS.register("saturn", () -> painting("saturn", 16, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> WICKER = PAINTING_VARIANTS.register("wicker", () -> painting("wicker", 16, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> BEGGING = PAINTING_VARIANTS.register("begging", () -> painting("begging", 16, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> ELDRITCH = PAINTING_VARIANTS.register("eldritch", () -> painting("eldritch", 16, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> CRYPT = PAINTING_VARIANTS.register("crypt", () -> painting("crypt", 32, 16));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> STONEDRAKE = PAINTING_VARIANTS.register("stonedrake", () -> painting("stonedrake", 32, 16));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> LIZARDCALM = PAINTING_VARIANTS.register("lizardcalm", () -> painting("lizardcalm", 32, 16));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> MINISTER = PAINTING_VARIANTS.register("minister", () -> painting("minister", 48, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> FENG = PAINTING_VARIANTS.register("feng", () -> painting("feng", 48, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> FALLEN_KINGDOM = PAINTING_VARIANTS.register("fallen_kingdom", () -> painting("fallen_kingdom", 48, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> LADIES_OF_THE_WOOD = PAINTING_VARIANTS.register("ladies_of_the_wood", () -> painting("ladies_of_the_wood", 48, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> CAPSTONE = PAINTING_VARIANTS.register("capstone", () -> painting("capstone", 48, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> MRAEG_JOEY = PAINTING_VARIANTS.register("mraeg_joey", () -> painting("mraeg_joey", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> MRAEG_WALLY = PAINTING_VARIANTS.register("mraeg_wally", () -> painting("mraeg_wally", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> REVELATION = PAINTING_VARIANTS.register("revelation", () -> painting("revelation", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> THEPANTS = PAINTING_VARIANTS.register("thepants", () -> painting("thepants", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> MANSION = PAINTING_VARIANTS.register("mansion", () -> painting("mansion", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> RUBY = PAINTING_VARIANTS.register("ruby", () -> painting("ruby", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> HOUND = PAINTING_VARIANTS.register("hound", () -> painting("hound", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> KOGANUSAN = PAINTING_VARIANTS.register("koganusan", () -> painting("koganusan", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> GLACIAL_FLOWER = PAINTING_VARIANTS.register("glacial_flower", () -> painting("glacial_flower", 32, 32));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> WRAITH = PAINTING_VARIANTS.register("wraith", () -> painting("wraith", 16, 16));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> BOBBY = PAINTING_VARIANTS.register("bobby", () -> painting("bobby", 16, 16));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> HEART = PAINTING_VARIANTS.register("heart", () -> painting("heart", 16, 16));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> TORMENT = PAINTING_VARIANTS.register("torment", () -> painting("torment", 16, 16));
}
