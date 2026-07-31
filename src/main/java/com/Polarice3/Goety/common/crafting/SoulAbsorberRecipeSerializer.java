package com.Polarice3.Goety.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SoulAbsorberRecipeSerializer <T extends SoulAbsorberRecipes>  implements RecipeSerializer<T> {
    private final int defaultSoulIncrease;
    private final int defaultCookingTime;
    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public SoulAbsorberRecipeSerializer(IFactory<T> pFactory, int pDefaultSoulIncrease, int pDefaultCookingTime) {
        this.defaultSoulIncrease = pDefaultSoulIncrease;
        this.defaultCookingTime = pDefaultCookingTime;
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("id", ResourceLocation.withDefaultNamespace("soul_absorber")).forGetter(recipe -> recipe.id),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                Codec.INT.optionalFieldOf("soulIncrease", this.defaultSoulIncrease).forGetter(recipe -> recipe.soulIncrease),
                Codec.INT.optionalFieldOf("cookingtime", this.defaultCookingTime).forGetter(recipe -> recipe.cookingTime)
        ).apply(instance, this.factory::create));
        this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    private T fromNetwork(RegistryFriendlyByteBuf pBuffer) {
        ResourceLocation id = pBuffer.readResourceLocation();
        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(pBuffer);
        int s = pBuffer.readVarInt();
        int i = pBuffer.readVarInt();
        return this.factory.create(id, ingredient, s, i);
    }

    private void toNetwork(RegistryFriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.id);
        Ingredient.CONTENTS_STREAM_CODEC.encode(pBuffer, pRecipe.ingredient);
        pBuffer.writeVarInt(pRecipe.soulIncrease);
        pBuffer.writeVarInt(pRecipe.cookingTime);
    }

    interface IFactory<T extends SoulAbsorberRecipes> {
        T create(ResourceLocation p_create_1_, Ingredient p_create_3_, int p_create_4_, int p_create_5_);
    }
}
