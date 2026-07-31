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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class BrazierRecipe implements Recipe<CraftingInput> {
    public static Serializer SERIALIZER = new Serializer();
    private final ResourceLocation id;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final int soulCost;

    public BrazierRecipe(ResourceLocation p_44246_, ItemStack p_44248_, NonNullList<Ingredient> p_44249_, int soulCost) {
        this.id = p_44246_;
        this.result = p_44248_;
        this.ingredients = p_44249_;
        this.soulCost = soulCost;
    }

    /**
     * Based on Runic Altar Recipe code by @Vazkii
     */
    @Override
    public boolean matches(CraftingInput container, Level p_44003_) {
        List<Ingredient> missingIngredients = Lists.newArrayList(this.ingredients);

        for (int i = 0; i < container.size(); i++) {
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
    public ItemStack assemble(CraftingInput p_44001_, HolderLookup.Provider pAccess) {
        return this.result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pAccess) {
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
        return ModRecipeSerializer.BRAZIER_TYPE.get();
    }

    public int getSoulCost() {
        return this.soulCost;
    }

    public static class Serializer implements RecipeSerializer<BrazierRecipe> {
        private static final MapCodec<BrazierRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("id", ResourceLocation.withDefaultNamespace("brazier")).forGetter(recipe -> recipe.id),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                Codec.INT.optionalFieldOf("soulCost", 0).forGetter(recipe -> recipe.soulCost)
        ).apply(instance, (id, result, ingredients, soulCost) -> {
            NonNullList<Ingredient> list = NonNullList.create();
            list.addAll(ingredients);
            return new BrazierRecipe(id, result, list, soulCost);
        }));

        private static final StreamCodec<RegistryFriendlyByteBuf, BrazierRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<BrazierRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BrazierRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static BrazierRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ResourceLocation recipeId = buffer.readResourceLocation();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients1 = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < ingredients1.size(); ++j) {
                ingredients1.set(j, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }

            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);

            int soulCost = buffer.readVarInt();

            return new BrazierRecipe(recipeId, result, ingredients1, soulCost);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, BrazierRecipe recipe) {
            buffer.writeResourceLocation(recipe.id);
            buffer.writeVarInt(recipe.ingredients.size());

            for(Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeVarInt(recipe.soulCost);
        }
    }
}
