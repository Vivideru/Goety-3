package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CursedMetalWolfArmorItem extends AnimalArmorItem {
    public static final ResourceLocation VANILLA_WOLF_TEXTURE = Goety.location("textures/entity/wolf/cursed_metal_wolf_armor.png");
    public static final ResourceLocation BLACK_WOLF_TEXTURE = Goety.location("textures/entity/servants/black_wolf/cursed_metal_black_wolf_armor.png");

    public CursedMetalWolfArmorItem() {
        super(VivideruArmorMaterials.CURSED_METAL_WOLF, BodyType.CANINE, false, new Item.Properties()
                .stacksTo(1)
                .durability(ArmorItem.Type.BODY.getDurability(6))
                .fireResistant());
    }

    @Override
    public ResourceLocation getTexture() {
        return VANILLA_WOLF_TEXTURE;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        // Keep the canine-only armor from being auto-equipped by generic hostile mob equipment logic.
        return armorType == EquipmentSlot.BODY && (entity instanceof Wolf || entity instanceof BlackWolf || entity instanceof SkeletonWolf);
    }
}
