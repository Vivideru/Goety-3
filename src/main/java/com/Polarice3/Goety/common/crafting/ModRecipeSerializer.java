package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModRecipeSerializer {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            BuiltInRegistries.RECIPE_TYPE, Goety.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER, Goety.MOD_ID);

    @SuppressWarnings("removal")
    public static void init(){
        RECIPE_TYPES.register(com.Polarice3.Goety.Goety.getModEventBus());
        RECIPE_SERIALIZERS.register(com.Polarice3.Goety.Goety.getModEventBus());
    }

    public static final DeferredHolder<RecipeType<?>, RecipeType<CursedInfuserRecipes>> CURSED_INFUSER = register("cursed_infuser");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CursedInfuserRecipes>> CURSED_INFUSER_RECIPES = RECIPE_SERIALIZERS.register("cursed_infuser_recipes",
            () -> new CursedInfuserRecipeSerializer<>(CursedInfuserRecipes::new, 60));

    public static final DeferredHolder<RecipeType<?>, RecipeType<SoulAbsorberRecipes>> SOUL_ABSORBER = register("soul_absorber");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SoulAbsorberRecipes>> SOUL_ABSORBER_RECIPES = RECIPE_SERIALIZERS.register("soul_absorber_recipes",
            () -> new SoulAbsorberRecipeSerializer<>(SoulAbsorberRecipes::new, 25, 200));

    public static final DeferredHolder<RecipeType<?>, RecipeType<RitualRecipe>> RITUAL_TYPE = register("ritual");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RitualRecipe>> RITUAL = RECIPE_SERIALIZERS.register("ritual",
            () -> RitualRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BrazierRecipe>> BRAZIER_TYPE = register("brazier");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BrazierRecipe>> BRAZIER = RECIPE_SERIALIZERS.register("brazier",
            () -> BrazierRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CauldronRecipe>> CAULDRON_TYPE = register("cauldron");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CauldronRecipe>> CAULDRON = RECIPE_SERIALIZERS.register("cauldron",
            () -> CauldronRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BrewingRecipe>> BREWING_TYPE = register("brewing");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BrewingRecipe>> BREWING = RECIPE_SERIALIZERS.register("brewing",
            () -> BrewingRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<PulverizeRecipe>> PULVERIZE_TYPE = register("pulverize");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PulverizeRecipe>> PULVERIZE = RECIPE_SERIALIZERS.register("pulverize",
            () -> PulverizeRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ModShapelessRecipe>> MODDED_SHAPELESS = RECIPE_SERIALIZERS.register("crafting_shapeless",
            ModShapelessRecipe.Serializer::new);

    static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> register(final String id) {
        return RECIPE_TYPES.register(id, () -> new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }
}
