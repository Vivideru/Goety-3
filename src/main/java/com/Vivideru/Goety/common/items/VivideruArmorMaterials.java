package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class VivideruArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Goety.MOD_ID);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CURSED_METAL_WOLF = ARMOR_MATERIALS.register("cursed_metal_wolf",
            () -> material("cursed_metal_wolf",
                    Util.make(new EnumMap<>(ArmorItem.Type.class), protection -> {
                        protection.put(ArmorItem.Type.BOOTS, 3);
                        protection.put(ArmorItem.Type.LEGGINGS, 6);
                        protection.put(ArmorItem.Type.CHESTPLATE, 8);
                        protection.put(ArmorItem.Type.HELMET, 3);
                        protection.put(ArmorItem.Type.BODY, 13);
                    }),
                    10,
                    SoundEvents.ARMOR_EQUIP_WOLF,
                    0.0F,
                    0.0F,
                    () -> Ingredient.of(ModItems.CURSED_METAL_INGOT.get())));

    @SuppressWarnings("removal")
    public static void init() {
        ARMOR_MATERIALS.register(Goety.getModEventBus());
    }

    private static ArmorMaterial material(String name, EnumMap<ArmorItem.Type, Integer> defense, int enchantmentValue,
                                          net.minecraft.core.Holder<SoundEvent> sound, float toughness, float knockbackResistance,
                                          Supplier<Ingredient> repairIngredient) {
        EnumMap<ArmorItem.Type, Integer> defenseMap = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            defenseMap.put(type, defense.getOrDefault(type, 0));
        }
        ResourceLocation materialId = Goety.location(name);
        return new ArmorMaterial(defenseMap, enchantmentValue, sound, repairIngredient,
                List.of(new ArmorMaterial.Layer(materialId)), toughness, knockbackResistance);
    }
}
