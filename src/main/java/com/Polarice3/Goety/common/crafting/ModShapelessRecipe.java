package com.Polarice3.Goety.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public class ModShapelessRecipe implements CraftingRecipe {
    @Nullable
    private final ResourceLocation id;
    final String group;
    final CraftingBookCategory category;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public ModShapelessRecipe(String p_249640_, CraftingBookCategory p_249390_, ItemStack p_252071_, NonNullList<Ingredient> p_250689_) {
        this(null, p_249640_, p_249390_, p_252071_, p_250689_);
    }

    public ModShapelessRecipe(ResourceLocation p_251840_, String p_249640_, CraftingBookCategory p_249390_, ItemStack p_252071_, NonNullList<Ingredient> p_250689_) {
        this.id = p_251840_;
        this.group = p_249640_;
        this.category = p_249390_;
        this.result = p_252071_;
        this.ingredients = p_250689_;
        this.isSimple = p_250689_.stream().allMatch(Ingredient::isSimple);
    }

    @Nullable
    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS_RECIPE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public String getGroup() {
        return this.group;
    }

    public CraftingBookCategory category() {
        return this.category;
    }

    public ItemStack getResultItem(HolderLookup.Provider p_267111_) {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean matches(CraftingInput p_44262_, Level p_44263_) {
        StackedContents stackedcontents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for(int j = 0; j < p_44262_.size(); ++j) {
            ItemStack itemstack = p_44262_.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, (IntList)null) : net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    public ItemStack assemble(CraftingInput p_44260_, HolderLookup.Provider p_266797_) {
        return this.result.copy();
    }

    public boolean canCraftInDimensions(int p_44252_, int p_44253_) {
        return p_44252_ * p_44253_ >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<ModShapelessRecipe> {
        private static final MapCodec<ModShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ModShapelessRecipe.Serializer::validateIngredients, DataResult::success).forGetter(recipe -> recipe.ingredients)
        ).apply(instance, ModShapelessRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ModShapelessRecipe> STREAM_CODEC = StreamCodec.of(
                ModShapelessRecipe.Serializer::toNetwork, ModShapelessRecipe.Serializer::fromNetwork);

        private static DataResult<NonNullList<Ingredient>> validateIngredients(java.util.List<Ingredient> ingredients) {
            Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new);
            if (aingredient.length == 0) {
                return DataResult.error(() -> "No ingredients for shapeless recipe");
            } else if (aingredient.length > 9) {
                return DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: 9");
            }
            return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
        }

        @Override
        public MapCodec<ModShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ModShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static ModShapelessRecipe fromNetwork(RegistryFriendlyByteBuf p_44294_) {
            String s = p_44294_.readUtf();
            CraftingBookCategory craftingbookcategory = p_44294_.readEnum(CraftingBookCategory.class);
            int i = p_44294_.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.CONTENTS_STREAM_CODEC.decode(p_44294_));
            }

            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(p_44294_);
            return new ModShapelessRecipe(s, craftingbookcategory, itemstack, nonnulllist);
        }

        public static void toNetwork(RegistryFriendlyByteBuf p_44281_, ModShapelessRecipe p_44282_) {
            p_44281_.writeUtf(p_44282_.group);
            p_44281_.writeEnum(p_44282_.category);
            p_44281_.writeVarInt(p_44282_.ingredients.size());

            for(Ingredient ingredient : p_44282_.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(p_44281_, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(p_44281_, p_44282_.result);
        }
    }
}
