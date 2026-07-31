package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.Goety;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BrewingRecipe implements Recipe<SingleRecipeInput> {
    public static Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation FALLBACK_ID = Goety.location("brewing");
    private final ResourceLocation id;
    public final Ingredient input;
    private final TagKey<EntityType<?>> entityTypeTag;
    private final EntityType<?> entityType;
    public final MobEffect output;
    public final int soulCost;
    public final int capacityExtra;
    public final int duration;

    public BrewingRecipe(ResourceLocation location,
                         Ingredient ingredient,
                         @Nullable TagKey<EntityType<?>> entityTypeTag,
                         @Nullable EntityType<?> entityType, MobEffect mobEffect, int soulCost, int capacityExtra, int duration) {
        this.id = location;
        this.input = ingredient;
        this.entityTypeTag = entityTypeTag;
        this.entityType = entityType;
        this.output = mobEffect;
        this.soulCost = soulCost;
        this.capacityExtra = capacityExtra;
        this.duration = duration;
    }

    @Override
    public boolean matches(SingleRecipeInput p_44002_, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput p_44001_, HolderLookup.Provider p_267052_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    public Ingredient getInput(){
        return this.input;
    }

    @Nullable
    public TagKey<EntityType<?>> getEntityTypeTag() {
        return entityTypeTag;
    }

    @Nullable
    public EntityType<?> getEntityType(){
        return this.entityType;
    }

    public int getSoulCost(){
        return this.soulCost;
    }

    public int getCapacityExtra(){
        return this.capacityExtra;
    }

    public int getDuration(){
        return this.duration;
    }

    public MobEffect getOutput(){
        return this.output;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_267052_) {
        return ItemStack.EMPTY;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializer.BREWING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        private static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.<BrewingRecipe>mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.input),
                EntityData.CODEC.codec().optionalFieldOf("entity", EntityData.EMPTY).forGetter(EntityData::fromRecipe),
                BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(recipe -> recipe.output),
                Codec.INT.fieldOf("soulCost").forGetter(recipe -> recipe.soulCost),
                Codec.INT.fieldOf("capacityExtra").forGetter(recipe -> recipe.capacityExtra),
                Codec.INT.fieldOf("duration").forGetter(recipe -> recipe.duration)
        ).apply(instance, (ingredient, entityData, effect, soulCost, capacityExtra, duration) ->
                new BrewingRecipe(FALLBACK_ID, ingredient, entityData.entityTypeTag().orElse(null), entityData.entityType().orElse(null), effect, soulCost, capacityExtra, duration)));

        private static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<BrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static BrewingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            TagKey<EntityType<?>> entityTag = null;
            EntityType<?> entityType = null;

            if (buf.readBoolean()) {
                var tagRL = buf.readResourceLocation();
                entityTag = TagKey.create(Registries.ENTITY_TYPE, tagRL);
            }

            if (buf.readBoolean()){
                entityType = BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
            }

            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(buf.readResourceLocation());

            int soulCost = buf.readInt();
            int capacityExtra = buf.readInt();
            int duration = buf.readInt();

            return new BrewingRecipe(FALLBACK_ID,
                    ingredient,
                    entityTag,
                    entityType,
                    mobEffect,
                    soulCost,
                    capacityExtra,
                    duration);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, BrewingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            buf.writeBoolean(recipe.entityTypeTag != null);
            if (recipe.entityTypeTag != null) {
                buf.writeResourceLocation(recipe.entityTypeTag.location());
            }
            buf.writeBoolean(recipe.entityType != null);
            if (recipe.entityType != null) {
                buf.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(recipe.entityType));
            }
            buf.writeResourceLocation(BuiltInRegistries.MOB_EFFECT.getKey(recipe.output));
            buf.writeInt(recipe.soulCost);
            buf.writeInt(recipe.capacityExtra);
            buf.writeInt(recipe.duration);
        }

        private record EntityData(Optional<TagKey<EntityType<?>>> entityTypeTag, Optional<EntityType<?>> entityType) {
            private static final EntityData EMPTY = new EntityData(Optional.empty(), Optional.empty());

            private static final MapCodec<EntityData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("tag").forGetter(EntityData::entityTypeTag),
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("entity_type").forGetter(EntityData::entityType)
            ).apply(instance, EntityData::new));

            private static EntityData fromRecipe(BrewingRecipe recipe) {
                return new EntityData(Optional.ofNullable(recipe.entityTypeTag), Optional.ofNullable(recipe.entityType));
            }
        }
    }
}
