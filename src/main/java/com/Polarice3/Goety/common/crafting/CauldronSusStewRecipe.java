package com.Polarice3.Goety.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CauldronSusStewRecipe extends CauldronRecipe {
    public static final Serializer SERIALIZER = new Serializer();
    private static List<FlowerBlock> flowerCache;
    private final NonNullList<Ingredient> baseIngredients;

    public CauldronSusStewRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, Ingredient takeWith,
                                 int levelLeft, int soulCost, int color) {
        super(id, new ItemStack(Items.SUSPICIOUS_STEW), ingredients, takeWith, levelLeft, soulCost, color);
        this.baseIngredients = ingredients;
    }

    public static List<FlowerBlock> getFlowers() {
        if (flowerCache == null) {
            flowerCache = BuiltInRegistries.BLOCK.stream()
                    .filter(FlowerBlock.class::isInstance)
                    .map(FlowerBlock.class::cast)
                    .toList();
        }
        return flowerCache;
    }

    public static void invalidateFlowerCache() {
        flowerCache = null;
    }

    @Nullable
    private static FlowerBlock findFlower(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FlowerBlock flower) {
            return flower;
        }
        return null;
    }

    @Nullable
    private static FlowerBlock findFlowerIn(Container container) {
        for (int i = 0; i < container.getContainerSize(); ++i) {
            FlowerBlock flower = findFlower(container.getItem(i));
            if (flower != null) {
                return flower;
            }
        }
        return null;
    }

    private static Container withoutFlowers(Container container) {
        SimpleContainer filtered = new SimpleContainer(container.getContainerSize());
        int slot = 0;
        boolean flowerSkipped = false;
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack stack = container.getItem(i);
            if (!flowerSkipped && findFlower(stack) != null) {
                flowerSkipped = true;
            } else {
                filtered.setItem(slot++, stack);
            }
        }
        return filtered;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return findFlowerIn(container) != null && super.matches(withoutFlowers(container), level);
    }

    @Override
    public boolean couldMatch(Container container, Level level) {
        return super.couldMatch(withoutFlowers(container), level);
    }

    public ItemStack assemble(Container container, HolderLookup.Provider registries) {
        ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
        FlowerBlock flower = findFlowerIn(container);
        if (flower != null) {
            // Suspicious stew effects moved from item NBT to a data component in 1.21.
            stew.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, flower.getSuspiciousEffects());
        }
        return stew;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> display = NonNullList.create();
        display.addAll(super.getIngredients());
        ItemStack[] flowers = getFlowers().stream().map(ItemStack::new).toArray(ItemStack[]::new);
        if (flowers.length > 0) {
            display.add(Ingredient.of(flowers));
        }
        return display;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<CauldronSusStewRecipe> {
        private static final MapCodec<CauldronSusStewRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(CauldronRecipe::getId),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.baseIngredients),
                Ingredient.CODEC.optionalFieldOf("take_with", Ingredient.EMPTY).forGetter(CauldronRecipe::getTakeWith),
                Codec.INT.optionalFieldOf("levelLeft", 3).forGetter(CauldronRecipe::getLevelLeft),
                Codec.INT.optionalFieldOf("soulCost", 0).forGetter(CauldronRecipe::getSoulCost),
                Codec.INT.optionalFieldOf("color", 0xC28340).forGetter(CauldronRecipe::getColor)
        ).apply(instance, (id, ingredients, takeWith, levelLeft, soulCost, color) -> {
            NonNullList<Ingredient> list = NonNullList.create();
            list.addAll(ingredients);
            return new CauldronSusStewRecipe(id, list, takeWith, levelLeft, soulCost, color);
        }));

        private static final StreamCodec<RegistryFriendlyByteBuf, CauldronSusStewRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<CauldronSusStewRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronSusStewRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CauldronSusStewRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(buffer.readVarInt(), Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            Ingredient takeWith = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new CauldronSusStewRecipe(id, ingredients, takeWith, buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt());
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, CauldronSusStewRecipe recipe) {
            buffer.writeResourceLocation(recipe.getId());
            NonNullList<Ingredient> ingredients = recipe.baseIngredients;
            buffer.writeVarInt(ingredients.size());
            for (Ingredient ingredient : ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getTakeWith());
            buffer.writeVarInt(recipe.getLevelLeft());
            buffer.writeVarInt(recipe.getSoulCost());
            buffer.writeVarInt(recipe.getColor());
        }

    }
}
