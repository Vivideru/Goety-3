package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CursedBlackBeastArmorItem extends AnimalArmorItem {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/black_wolf/black_beast_cursed_armor.png");
    private final ResourceLocation texture;

    public CursedBlackBeastArmorItem() {
        this(VivideruArmorMaterials.CURSED_BLACK_BEAST, TEXTURE);
    }

    protected CursedBlackBeastArmorItem(Holder<ArmorMaterial> material, ResourceLocation texture) {
        super(material, BodyType.CANINE, false, new Item.Properties()
                .stacksTo(1)
                .durability(ArmorItem.Type.BODY.getDurability(8))
                .fireResistant());
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return armorType == EquipmentSlot.BODY && entity instanceof BlackBeast;
    }
}
