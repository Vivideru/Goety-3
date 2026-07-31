package com.Polarice3.Goety.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CursedInfuserRecipeSerializer<T extends CursedInfuserRecipes> implements RecipeSerializer<T>{
    private final int defaultCookingTime;
    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CursedInfuserRecipeSerializer(IFactory<T> pFactory, int pDefaultCookingTime) {
        this.defaultCookingTime = pDefaultCookingTime;
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("id", ResourceLocation.withDefaultNamespace("cursed_infuser")).forGetter(recipe -> recipe.id),
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.INT.optionalFieldOf("cookingTime", this.defaultCookingTime).forGetter(recipe -> recipe.cookingTime),
                Codec.BOOL.optionalFieldOf("grim", false).forGetter(recipe -> recipe.grim)
        ).apply(instance, (id, group, ingredient, result, cookingTime, grim) ->
                this.factory.create(id, group, ingredient, result, 0.0F, cookingTime, grim)));
        this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
    }

    private T fromNetwork(RegistryFriendlyByteBuf pBuffer) {
        ResourceLocation id = pBuffer.readResourceLocation();
        String s = pBuffer.readUtf(32767);
        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(pBuffer);
        ItemStack itemstack = ItemStack.STREAM_CODEC.decode(pBuffer);
        int i = pBuffer.readVarInt();
        boolean grim = pBuffer.readBoolean();
        return this.factory.create(id, s, ingredient, itemstack, 0.0F, i, grim);
    }

    private void toNetwork(RegistryFriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.id);
        pBuffer.writeUtf(pRecipe.group);
        Ingredient.CONTENTS_STREAM_CODEC.encode(pBuffer, pRecipe.ingredient);
        ItemStack.STREAM_CODEC.encode(pBuffer, pRecipe.result);
        pBuffer.writeVarInt(pRecipe.cookingTime);
        pBuffer.writeBoolean(pRecipe.grim);
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    interface IFactory<T extends CursedInfuserRecipes> {
        T create(ResourceLocation p_create_1_, String p_create_2_, Ingredient p_create_3_, ItemStack p_create_4_, float p_create_5_, int p_create_6_, boolean p_create_7_);
    }
}
