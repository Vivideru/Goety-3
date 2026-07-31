package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.common.ritual.ModRituals;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.CommonHooks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RitualRecipe implements Recipe<CraftingInput> {
    public static Serializer SERIALIZER = new Serializer();

    @Nullable
    private final ResourceLocation id;
    private final String group;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final ResourceLocation ritualType;
    private final Ritual ritual;
    private final String craftType;
    private final int soulCost;
    private final Ingredient activationItem;
    private final TagKey<EntityType<?>> entityToSacrifice;
    private final TagKey<EntityType<?>> entityToConvert;
    private final EntityType<?> entityToSummon;
    private final EntityType<?> entityToConvertInto;
    private final TagKey<Structure> structureTag;
    private final String structureName;
    private final Enchantment enchantment;
    private final ResourceLocation enchantmentId;
    private final int xpLevelCost;
    private final int duration;
    private final int summonLife;
    private final float durationPerIngredient;
    private final String entityToSacrificeDisplayName;
    private final String entityToConvertDisplayName;
    private final String research;

    public RitualRecipe(ResourceLocation id, String group, String pCraftType, ResourceLocation ritualType,
                        ItemStack result, EntityType<?> entityToSummon, EntityType<?> entityToConvertInto, Ingredient activationItem, NonNullList<Ingredient> input, int duration, int summonLife, int pSoulCost,
                        TagKey<EntityType<?>> entityToSacrifice, String entityToSacrificeDisplayName,
                        TagKey<EntityType<?>> entityToConvert, String entityToConvertDisplayName,
                        TagKey<Structure> structureTag, String structureName,
                        Enchantment enchantment, int xpLevelCost, String research) {
        this(id, group, pCraftType, ritualType, result, entityToSummon, entityToConvertInto, activationItem, input, duration, summonLife, pSoulCost,
                entityToSacrifice, entityToSacrificeDisplayName, entityToConvert, entityToConvertDisplayName, structureTag, structureName,
                enchantment, null, xpLevelCost, research);
    }

    public RitualRecipe(ResourceLocation id, String group, String pCraftType, ResourceLocation ritualType,
                        ItemStack result, EntityType<?> entityToSummon, EntityType<?> entityToConvertInto, Ingredient activationItem, NonNullList<Ingredient> input, int duration, int summonLife, int pSoulCost,
                        TagKey<EntityType<?>> entityToSacrifice, String entityToSacrificeDisplayName,
                        TagKey<EntityType<?>> entityToConvert, String entityToConvertDisplayName,
                        TagKey<Structure> structureTag, String structureName,
                        Enchantment enchantment, ResourceLocation enchantmentId, int xpLevelCost, String research) {
        // Rituals are altar-only recipes, so they must not expose CraftingRecipe metadata to integrations such as Create mixers.
        this.id = id;
        this.group = group;
        this.result = result;
        this.ingredients = input;
        this.craftType = pCraftType;
        this.soulCost = pSoulCost;
        this.entityToSummon = entityToSummon;
        this.entityToConvertInto = entityToConvertInto;
        this.ritualType = ritualType;
        this.ritual = ModRituals.REGISTRY.get(this.ritualType).create(this);
        this.activationItem = activationItem;
        this.duration = duration;
        this.summonLife = summonLife;
        this.durationPerIngredient = this.duration / (float) (this.getIngredients().size() + 1);
        this.entityToSacrifice = entityToSacrifice;
        this.entityToSacrificeDisplayName = entityToSacrificeDisplayName;
        this.entityToConvert = entityToConvert;
        this.entityToConvertDisplayName = entityToConvertDisplayName;
        this.structureTag = structureTag;
        this.structureName = structureName;
        this.enchantment = enchantment;
        this.enchantmentId = enchantmentId;
        this.xpLevelCost = xpLevelCost;
        this.research = research;
    }

    @Nullable
    public ResourceLocation getId() {
        return this.id;
    }

    public String getGroup() {
        return this.group;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pAccess) {
        return this.result;
    }

    public String getCraftType() {
        return this.craftType;
    }

    public int getSoulCost() {
        return this.soulCost;
    }

    public Ingredient getActivationItem() {
        return this.activationItem;
    }

    public int getDuration() {
        return this.duration;
    }

    public float getDurationPerIngredient() {
        return this.durationPerIngredient;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public boolean matches(Level world, BlockPos darkAltarPos, Player player, ItemStack activationItem) {
        return this.ritual.identify(world, darkAltarPos, player, activationItem);
    }

    @Override
    public boolean matches(@NotNull CraftingInput inventory, @NotNull Level world) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput inventoryCrafting, net.minecraft.core.HolderLookup.Provider pAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializer.RITUAL_TYPE.get();
    }

    public TagKey<EntityType<?>> getEntityToSacrifice() {
        return this.entityToSacrifice;
    }

    public boolean requiresSacrifice() {
        return this.entityToSacrifice != null;
    }

    public EntityType<?> getEntityToSummon() {
        return this.entityToSummon;
    }

    public EntityType<?> getEntityToConvertInto() {
        return this.entityToConvertInto;
    }

    public TagKey<EntityType<?>> getEntityToConvert() {
        return this.entityToConvert;
    }

    public boolean isConversion(){
        return this.entityToConvert != null && this.entityToConvertInto != null;
    }

    public boolean isSummoning(){
        return this.entityToSummon != null;
    }

    public ResourceLocation getRitualType() {
        return this.ritualType;
    }

    public Ritual getRitual() {
        return this.ritual;
    }

    public String getEntityToSacrificeDisplayName() {
        return this.entityToSacrificeDisplayName;
    }

    public String getEntityToConvertDisplayName() {
        return this.entityToConvertDisplayName;
    }

    public TagKey<Structure> getStructureTag() {
        return this.structureTag;
    }

    public String getStructureName() {
        return this.structureName;
    }

    public Enchantment getEnchantment(){
        return this.enchantment;
    }

    @Nullable
    public Holder<Enchantment> getEnchantmentHolder() {
        if (this.enchantmentId == null) {
            return null;
        }
        HolderLookup.RegistryLookup<Enchantment> registry = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        if (registry == null) {
            return null;
        }
        return registry.get(ResourceKey.create(Registries.ENCHANTMENT, this.enchantmentId)).orElse(null);
    }

    public int getXPLevelCost(){
        return this.xpLevelCost;
    }

    public String getResearch(){
        return this.research;
    }

    public int getSummonLife() {
        return this.summonLife;
    }

    public static class Serializer implements RecipeSerializer<RitualRecipe> {
        private static final MapCodec<RitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("craftType", "").forGetter(recipe -> recipe.craftType),
                ResourceLocation.CODEC.fieldOf("ritual_type").forGetter(recipe -> recipe.ritualType),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("entity_to_summon").forGetter(recipe -> java.util.Optional.ofNullable(recipe.entityToSummon)),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("entity_to_convert_into").forGetter(recipe -> java.util.Optional.ofNullable(recipe.entityToConvertInto)),
                Ingredient.CODEC.fieldOf("activation_item").forGetter(recipe -> recipe.activationItem),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").xmap(list -> NonNullList.of(Ingredient.EMPTY, list.toArray(Ingredient[]::new)), java.util.List::copyOf).forGetter(RitualRecipe::getIngredients),
                Codec.INT.optionalFieldOf("duration", 30).forGetter(recipe -> recipe.duration),
                Codec.INT.optionalFieldOf("summonLife", -1).forGetter(recipe -> recipe.summonLife),
                Codec.INT.optionalFieldOf("soulCost", 0).forGetter(recipe -> recipe.soulCost),
                RitualEntityTag.CODEC.optionalFieldOf("entity_to_sacrifice").forGetter(recipe -> java.util.Optional.ofNullable(RitualEntityTag.from(recipe.entityToSacrifice, recipe.entityToSacrificeDisplayName))),
                RitualEntityTag.CODEC.optionalFieldOf("entity_to_convert").forGetter(recipe -> java.util.Optional.ofNullable(RitualEntityTag.from(recipe.entityToConvert, recipe.entityToConvertDisplayName))),
                RitualStructureTag.CODEC.optionalFieldOf("structure_to_locate").forGetter(recipe -> java.util.Optional.ofNullable(RitualStructureTag.from(recipe.structureTag, recipe.structureName))),
                ResourceLocation.CODEC.optionalFieldOf("enchantment").forGetter(recipe -> java.util.Optional.ofNullable(recipe.enchantmentId)),
                Codec.INT.optionalFieldOf("xpLevelCost", 0).forGetter(recipe -> recipe.xpLevelCost),
                Codec.STRING.optionalFieldOf("research", "").forGetter(recipe -> recipe.research)
        ).apply(instance, (craftType, ritualType, result, entityToSummon, entityToConvertInto, activationItem, ingredients, duration, summonLife, soulCost,
                          entityToSacrifice, entityToConvert, structureTag, enchantmentId, xpLevelCost, research) -> new RitualRecipe(null, "", craftType, ritualType,
                result, entityToSummon.orElse(null), entityToConvertInto.orElse(null), activationItem, ingredients, duration, summonLife, soulCost,
                entityToSacrifice.map(RitualEntityTag::toTagKey).orElse(null), entityToSacrifice.map(RitualEntityTag::displayName).orElse(""),
                entityToConvert.map(RitualEntityTag::toTagKey).orElse(null), entityToConvert.map(RitualEntityTag::displayName).orElse(""),
                structureTag.map(RitualStructureTag::toTagKey).orElse(null), structureTag.map(RitualStructureTag::displayName).orElse(""),
                enchantmentId.map(Serializer::getEnchantment).orElse(null), enchantmentId.orElse(null), xpLevelCost, research)));
        public static final StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<RitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static RitualRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }

            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            String craftType = buffer.readUtf(32767);

            ResourceLocation ritualType = buffer.readResourceLocation();

            EntityType<?> entityToSummon = null;
            if (buffer.readBoolean()) {
                entityToSummon = BuiltInRegistries.ENTITY_TYPE.get(buffer.readResourceLocation());
            }

            int duration = buffer.readVarInt();
            int summonLife = buffer.readVarInt();
            int soulCost = buffer.readVarInt();

            Ingredient activationItem = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

            TagKey<EntityType<?>> entityToSacrifice = null;
            String entityToSacrificeDisplayName = "";
            if (buffer.readBoolean()) {
                var tagRL = buffer.readResourceLocation();
                entityToSacrifice = TagKey.create(Registries.ENTITY_TYPE, tagRL);
                entityToSacrificeDisplayName = buffer.readUtf();
            }

            EntityType<?> entityToConvertInto = null;
            TagKey<EntityType<?>> entityToConvert = null;
            String entityToConvertDisplayName = "";
            if (buffer.readBoolean()) {
                var tagRL = buffer.readResourceLocation();
                entityToConvert = TagKey.create(Registries.ENTITY_TYPE, tagRL);
                entityToConvertDisplayName = buffer.readUtf();
            }

            if (buffer.readBoolean()){
                entityToConvertInto = BuiltInRegistries.ENTITY_TYPE.get(buffer.readResourceLocation());
            }

            TagKey<Structure> structureTag = null;
            String structureName = "";
            if (buffer.readBoolean()) {
                var tagRL = buffer.readResourceLocation();
                structureTag = TagKey.create(Registries.STRUCTURE, tagRL);
                structureName = buffer.readUtf();
            }

            Enchantment enchantment = null;
            ResourceLocation enchantmentId = null;
            int xpLevelCost = 0;
            if (buffer.readBoolean()){
                enchantmentId = buffer.readResourceLocation();
                enchantment = getEnchantment(enchantmentId);
                xpLevelCost = buffer.readVarInt();
            }
            String research = "";
            if (buffer.readBoolean()){
                research = buffer.readUtf(32767);
            }

            return new RitualRecipe(null, group, craftType, ritualType, result, entityToSummon, entityToConvertInto,
                    activationItem, ingredients, duration, summonLife, soulCost, entityToSacrifice, entityToSacrificeDisplayName, entityToConvert, entityToConvertDisplayName, structureTag, structureName, enchantment, enchantmentId, xpLevelCost, research);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, RitualRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeUtf(recipe.craftType);

            buffer.writeResourceLocation(recipe.ritualType);

            buffer.writeBoolean(recipe.entityToSummon != null);
            if (recipe.entityToSummon != null) {
                buffer.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(recipe.entityToSummon));
            }

            buffer.writeVarInt(recipe.duration);
            buffer.writeVarInt(recipe.summonLife);
            buffer.writeVarInt(recipe.soulCost);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.activationItem);
            buffer.writeBoolean(recipe.entityToSacrifice != null);
            if (recipe.entityToSacrifice != null) {
                buffer.writeResourceLocation(recipe.entityToSacrifice.location());
                buffer.writeUtf(recipe.entityToSacrificeDisplayName);
            }
            buffer.writeBoolean(recipe.entityToConvert != null);
            if (recipe.entityToConvert != null){
                buffer.writeResourceLocation(recipe.entityToConvert.location());
                buffer.writeUtf(recipe.entityToConvertDisplayName);
            }
            buffer.writeBoolean(recipe.entityToConvertInto != null);
            if (recipe.entityToConvertInto != null) {
                buffer.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(recipe.entityToConvertInto));
            }
            buffer.writeBoolean(recipe.structureTag != null);
            if (recipe.structureTag != null) {
                buffer.writeResourceLocation(recipe.structureTag.location());
                buffer.writeUtf(recipe.structureName);
            }
            buffer.writeBoolean(recipe.enchantmentId != null);
            if (recipe.enchantmentId != null) {
                buffer.writeResourceLocation(recipe.enchantmentId);
                buffer.writeVarInt(recipe.xpLevelCost);
            }
            buffer.writeBoolean(recipe.research != null);
            if (recipe.research != null) {
                buffer.writeUtf(recipe.research);
            }
        }

        @Nullable
        private static Enchantment getEnchantment(ResourceLocation id) {
            HolderLookup.RegistryLookup<Enchantment> registry = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
            if (registry == null) {
                return null;
            }
            return registry.get(ResourceKey.create(Registries.ENCHANTMENT, id)).map(Holder.Reference::value).orElse(null);
        }

        private record RitualEntityTag(ResourceLocation tag, String displayName) {
            private static final Codec<RitualEntityTag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("tag").forGetter(RitualEntityTag::tag),
                    Codec.STRING.fieldOf("display_name").forGetter(RitualEntityTag::displayName)
            ).apply(instance, RitualEntityTag::new));

            @Nullable
            private static RitualEntityTag from(@Nullable TagKey<EntityType<?>> tag, String displayName) {
                return tag == null ? null : new RitualEntityTag(tag.location(), displayName);
            }

            private TagKey<EntityType<?>> toTagKey() {
                return TagKey.create(Registries.ENTITY_TYPE, this.tag);
            }
        }

        private record RitualStructureTag(ResourceLocation tag, String displayName) {
            private static final Codec<RitualStructureTag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("tag").forGetter(RitualStructureTag::tag),
                    Codec.STRING.fieldOf("display_name").forGetter(RitualStructureTag::displayName)
            ).apply(instance, RitualStructureTag::new));

            @Nullable
            private static RitualStructureTag from(@Nullable TagKey<Structure> tag, String displayName) {
                return tag == null ? null : new RitualStructureTag(tag.location(), displayName);
            }

            private TagKey<Structure> toTagKey() {
                return TagKey.create(Registries.STRUCTURE, this.tag);
            }
        }
    }
}
/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
