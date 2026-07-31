package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.Goety;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class PulverizeRecipe implements Recipe<SingleRecipeInput> {
    public static Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation FALLBACK_ID = Goety.location("pulverize");
    protected final ResourceLocation id;
    public final Ingredient ingredient;
    protected final ItemStack itemResult;
    protected final Block blockResult;

    public PulverizeRecipe(ResourceLocation pId, Ingredient pIngredient, ItemStack pResult, Block pBlock) {
        this.id = pId;
        this.ingredient = pIngredient;
        this.itemResult = pResult;
        this.blockResult = pBlock;
    }

    @Override
    public boolean matches(SingleRecipeInput pInv, Level pLevel) {
        return this.ingredient.test(pInv.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput p_44001_, HolderLookup.Provider p_267165_) {
        return this.itemResult.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_267052_) {
        return this.itemResult;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    public Block getBlockResult(){
        return this.blockResult;
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
        return ModRecipeSerializer.PULVERIZE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<PulverizeRecipe>{
        private static final MapCodec<PulverizeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("item_result", ItemStack.EMPTY).forGetter(recipe -> recipe.itemResult),
                BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("block_result", Blocks.CAVE_AIR).forGetter(recipe -> recipe.blockResult)
        ).apply(instance, (ingredient, itemStack, block) -> new PulverizeRecipe(FALLBACK_ID, ingredient, itemStack, block)));

        private static final StreamCodec<RegistryFriendlyByteBuf, PulverizeRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<PulverizeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PulverizeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static PulverizeRecipe fromNetwork(RegistryFriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(pBuffer);
            ItemStack itemstack = ItemStack.OPTIONAL_STREAM_CODEC.decode(pBuffer);
            Block block = BuiltInRegistries.BLOCK.get(pBuffer.readResourceLocation());
            return new PulverizeRecipe(FALLBACK_ID, ingredient, itemstack, block);
        }

        private static void toNetwork(RegistryFriendlyByteBuf pBuffer, PulverizeRecipe pRecipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(pBuffer, pRecipe.ingredient);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(pBuffer, pRecipe.itemResult);
            pBuffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(pRecipe.blockResult));
        }
    }
}
