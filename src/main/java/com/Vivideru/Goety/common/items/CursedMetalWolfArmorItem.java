package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CursedMetalWolfArmorItem extends AnimalArmorItem {
    public static final ResourceLocation VANILLA_WOLF_TEXTURE = Goety.location("textures/entity/wolf/cursed_metal_wolf_armor.png");
    public static final ResourceLocation BLACK_WOLF_TEXTURE = Goety.location("textures/entity/servants/black_wolf/cursed_metal_black_wolf_armor.png");
    private final ResourceLocation vanillaWolfTexture;
    private final ResourceLocation blackWolfTexture;

    public CursedMetalWolfArmorItem() {
        this(VivideruArmorMaterials.CURSED_METAL_WOLF, VANILLA_WOLF_TEXTURE, BLACK_WOLF_TEXTURE);
    }

    protected CursedMetalWolfArmorItem(Holder<ArmorMaterial> material, ResourceLocation vanillaWolfTexture, ResourceLocation blackWolfTexture) {
        super(material, BodyType.CANINE, false, new Item.Properties()
                .stacksTo(1)
                .durability(ArmorItem.Type.BODY.getDurability(6))
                .fireResistant());
        this.vanillaWolfTexture = vanillaWolfTexture;
        this.blackWolfTexture = blackWolfTexture;
    }

    @Override
    public ResourceLocation getTexture() {
        return this.vanillaWolfTexture;
    }

    public ResourceLocation getBlackWolfTexture() {
        return this.blackWolfTexture;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        // Keep the canine-only armor from being auto-equipped by generic hostile mob equipment logic.
        return armorType == EquipmentSlot.BODY && (entity instanceof Wolf || entity instanceof BlackWolf && !(entity instanceof Warg) || entity instanceof SkeletonWolf);
    }
}
