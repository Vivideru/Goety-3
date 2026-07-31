package com.Polarice3.Goety.common.items.armor;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModArmorMaterials {
    // {feet, legs, chest, head}
    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), typeDurability -> {
        typeDurability.put(ArmorItem.Type.BOOTS, 13);
        typeDurability.put(ArmorItem.Type.LEGGINGS, 15);
        typeDurability.put(ArmorItem.Type.CHESTPLATE, 16);
        typeDurability.put(ArmorItem.Type.HELMET, 11);
        typeDurability.put(ArmorItem.Type.BODY, 16);
    });
    private static final Map<Holder<ArmorMaterial>, Integer> DURABILITY_MULTIPLIERS = new java.util.IdentityHashMap<>();

    // Armor materials are created during static item setup before configs load, so these use the declared config defaults.
    public static final Holder<ArmorMaterial> CURSED_KNIGHT = register("cursed_knight", 15,
            Util.make(new EnumMap<>(ArmorItem.Type.class), protection -> {
                protection.put(ArmorItem.Type.BOOTS, 2);
                protection.put(ArmorItem.Type.LEGGINGS, 5);
                protection.put(ArmorItem.Type.CHESTPLATE, 6);
                protection.put(ArmorItem.Type.HELMET, 2);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            0.5F,
            0.0F,
            () -> Ingredient.of(ModItems.CURSED_METAL_INGOT.get()));

    public static final Holder<ArmorMaterial> CURSED_PALADIN = register("cursed_paladin", 30,
            Util.make(new EnumMap<>(ArmorItem.Type.class), protection -> {
                protection.put(ArmorItem.Type.BOOTS, 3);
                protection.put(ArmorItem.Type.LEGGINGS, 6);
                protection.put(ArmorItem.Type.CHESTPLATE, 7);
                protection.put(ArmorItem.Type.HELMET, 3);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_IRON,
            1.0F,
            0.0F,
            () -> Ingredient.of(ModItems.CURSED_METAL_INGOT.get()));

    public static final Holder<ArmorMaterial> BLACK_IRON = register("black_iron", 30,
            Util.make(new EnumMap<>(ArmorItem.Type.class), protection -> {
                protection.put(ArmorItem.Type.BOOTS, 2);
                protection.put(ArmorItem.Type.LEGGINGS, 5);
                protection.put(ArmorItem.Type.CHESTPLATE, 6);
                protection.put(ArmorItem.Type.HELMET, 2);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            2.0F,
            0.0F,
            () -> Ingredient.of(ModItems.CURSED_METAL_INGOT.get()));

    public static final Holder<ArmorMaterial> DARK = register("dark", 15,
            Util.make(new EnumMap<>(ArmorItem.Type.class), protection -> {
                protection.put(ArmorItem.Type.BOOTS, 3);
                protection.put(ArmorItem.Type.LEGGINGS, 6);
                protection.put(ArmorItem.Type.CHESTPLATE, 8);
                protection.put(ArmorItem.Type.HELMET, 3);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            2.0F,
            0.3F,
            () -> Ingredient.of(ModItems.DARK_ALLOY_INGOT.get()));

    public static final Holder<ArmorMaterial> MALEFIC = register("malefic", 30,
            Util.make(new EnumMap<>(ArmorItem.Type.class), protection -> {
                protection.put(ArmorItem.Type.BOOTS, 0);
                protection.put(ArmorItem.Type.LEGGINGS, 0);
                protection.put(ArmorItem.Type.CHESTPLATE, 0);
                protection.put(ArmorItem.Type.HELMET, 2);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            2.0F,
            0.0F,
            () -> Ingredient.of(Tags.Items.BONES));

    public static Item.Properties durability(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties) {
        // Minecraft 1.21 moved armor durability from ArmorMaterial to Item.Properties, so the old multipliers are preserved here.
        return properties.durability(HEALTH_FUNCTION_FOR_TYPE.get(type) * DURABILITY_MULTIPLIERS.get(material));
    }

    public static int configuredDurability(ArmorItem.Type type, ModConfigSpec.ConfigValue<Integer> configValue, int fallbackMultiplier) {
        // Default components are built before common configs are loaded in 1.21, so armor durability has to be read at stack query time.
        return HEALTH_FUNCTION_FOR_TYPE.get(type) * ConfiguredItemUtil.durability(configValue, fallbackMultiplier);
    }

    private static Holder<ArmorMaterial> register(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> defense, int enchantmentValue, Holder<SoundEvent> sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        EnumMap<ArmorItem.Type, Integer> defenseMap = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            defenseMap.put(type, defense.getOrDefault(type, 0));
        }
        Holder<ArmorMaterial> holder = Holder.direct(new ArmorMaterial(defenseMap, enchantmentValue, sound, repairIngredient, List.of(new ArmorMaterial.Layer(Goety.location(name))), toughness, knockbackResistance));
        DURABILITY_MULTIPLIERS.put(holder, durabilityMultiplier);
        return holder;
    }
}
