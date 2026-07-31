package com.Polarice3.Goety.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class ModPotionUtil extends BrewingRecipe {

    private final ItemStack inputStack;

    public ModPotionUtil(ItemStack inputStack, Ingredient ingredient, ItemStack output) {
        super(Ingredient.of(inputStack), ingredient, output);
        this.inputStack = inputStack;
    }

    @Override
    public boolean isInput(ItemStack stack) {
        return super.isInput(stack) && getPotion(stack).equals(getPotion(inputStack));
    }

    public static Holder<Potion> getPotion(ItemStack stack) {
        // 1.21 removed Potions.EMPTY; water is the vanilla no-effect potion fallback.
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().orElse(Potions.WATER);
    }

    public static List<MobEffectInstance> getMobEffects(ItemStack stack) {
        List<MobEffectInstance> effects = new ArrayList<>();
        stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).forEachEffect(effects::add);
        appendLegacyCustomEffects(stack, effects);
        return effects;
    }

    public static List<MobEffectInstance> getCustomEffects(ItemStack stack) {
        List<MobEffectInstance> effects = new ArrayList<>(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).customEffects());
        appendLegacyCustomEffects(stack, effects);
        return effects;
    }

    public static int getColor(ItemStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
    }

    public static int getColor(Iterable<MobEffectInstance> effects) {
        return PotionContents.getColor(effects);
    }

    public static Iterable<MobEffectInstance> getAllEffects(Holder<Potion> potion, List<MobEffectInstance> customEffects) {
        PotionContents contents = new PotionContents(potion);
        for (MobEffectInstance effect : customEffects) {
            contents = contents.withEffectAdded(effect);
        }
        return contents.getAllEffects();
    }

    public static ItemStack setPotion(ItemStack stack, Holder<Potion> potion) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(potion), contents.customColor(), contents.customEffects()));
        return stack;
    }

    public static ItemStack setCustomEffects(ItemStack stack, List<MobEffectInstance> effects) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(contents.potion(), contents.customColor(), List.copyOf(effects)));
        return stack;
    }

    private static void appendLegacyCustomEffects(ItemStack stack, List<MobEffectInstance> effects) {
        CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (customData.contains("CustomPotionEffects", 9)) {
            // Older conversion code stored brew potion effects in custom data; read them so existing brewed stacks are not empty.
            ListTag listTag = customData.getList("CustomPotionEffects", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                MobEffectInstance instance = MobEffectInstance.load(listTag.getCompound(i));
                if (instance != null) {
                    effects.add(instance);
                }
            }
        }
    }

    private static ItemStack setPotion(Item item, Holder<Potion> pPotion) {
        return PotionContents.createItemStack(item, pPotion);
    }

    public static ItemStack setPotion(Holder<Potion> pPotion) {
        return setPotion(Items.POTION, pPotion);
    }

    public static ItemStack setSplashPotion(Holder<Potion> pPotion) {
        return setPotion(Items.SPLASH_POTION, pPotion);
    }

    public static ItemStack setLingeringPotion(Holder<Potion> pPotion) {
        return setPotion(Items.LINGERING_POTION, pPotion);
    }
}
