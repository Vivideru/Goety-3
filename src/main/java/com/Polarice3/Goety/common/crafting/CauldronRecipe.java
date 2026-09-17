package com.Polarice3.Goety.common.crafting;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class CauldronRecipe implements Recipe<CraftingInput> {
    public static Serializer SERIALIZER = new Serializer();
    private final ResourceLocation id;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final Ingredient takeWith;
    private final int levelLeft;
    private final int soulCost;
    private final int color;

    public CauldronRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> ingredients, Ingredient takeWith, int levelLeft, int soulCost, int color) {
        this.id = id;
        this.result = result;
        this.ingredients = ingredients;
        this.takeWith = takeWith;
        this.levelLeft = Mth.clamp(levelLeft, 1, 3);
        this.soulCost = soulCost;
        this.color = color;
    }

    /**
     * Based on Runic Altar Recipe code by Vazkii.
     */
    public boolean matches(Container container, Level level) {
        List<Ingredient> missingIngredients = Lists.newArrayList(this.ingredients);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.isEmpty()) {
                break;
            }

            int index = -1;

            for (int j = 0; j < missingIngredients.size(); j++) {
                Ingredient ingredient = missingIngredients.get(j);
                if (ingredient.test(itemStack)) {
                    index = j;
                    break;
                }
            }

            if (index == -1) {
                return false;
            } else {
                missingIngredients.remove(index);
            }
        }

        return missingIngredients.isEmpty();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializer.CAULDRON_TYPE.get();
    }

    public Ingredient getTakeWith() {
        return this.takeWith;
    }

    public int getLevelLeft() {
        return this.levelLeft;
    }

    public int getSoulCost() {
        return this.soulCost;
    }

    public int getColor() {
        return this.color;
    }

    public boolean couldMatch(Container container, Level level) {
        List<Ingredient> remainingIngredients = Lists.newArrayList(this.ingredients);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            int index = -1;
            for (int j = 0; j < remainingIngredients.size(); j++) {
                if (remainingIngredients.get(j).test(itemStack)) {
                    index = j;
                    break;
                }
            }
            if (index == -1) {
                return false;
            }
            remainingIngredients.remove(index);
        }
        return true;
    }

    public static class Serializer implements RecipeSerializer<CauldronRecipe> {
        private static final MapCodec<CauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(recipe -> recipe.id),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                Ingredient.CODEC.optionalFieldOf("take_with", Ingredient.EMPTY).forGetter(recipe -> recipe.takeWith),
                Codec.INT.optionalFieldOf("levelLeft", 3).forGetter(recipe -> recipe.levelLeft),
                Codec.INT.optionalFieldOf("soulCost", 0).forGetter(recipe -> recipe.soulCost),
                Codec.INT.optionalFieldOf("color", 0x3F76E4).forGetter(recipe -> recipe.color)
        ).apply(instance, (id, result, ingredients, takeWith, levelLeft, soulCost, color) -> {
            NonNullList<Ingredient> list = NonNullList.create();
            list.addAll(ingredients);
            return new CauldronRecipe(id, result, list, takeWith, levelLeft, soulCost, color);
        }));

        private static final StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<CauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CauldronRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ResourceLocation recipeId = buffer.readResourceLocation();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }

            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            Ingredient takeWith = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            int levelLeft = buffer.readVarInt();
            int soulCost = buffer.readVarInt();
            int color = buffer.readVarInt();

            return new CauldronRecipe(recipeId, result, ingredients, takeWith, levelLeft, soulCost, color);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, CauldronRecipe recipe) {
            buffer.writeResourceLocation(recipe.id);
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.takeWith);
            buffer.writeVarInt(recipe.levelLeft);
            buffer.writeVarInt(recipe.soulCost);
            buffer.writeVarInt(recipe.color);
        }
    }
}
