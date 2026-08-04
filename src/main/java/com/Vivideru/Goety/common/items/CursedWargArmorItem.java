package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.entities.ally.Warg;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CursedWargArmorItem extends AnimalArmorItem {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/black_wolf/warg_cursed_metal_armor.png");
    private final ResourceLocation texture;

    public CursedWargArmorItem() {
        this(VivideruArmorMaterials.CURSED_WARG, TEXTURE);
    }

    protected CursedWargArmorItem(Holder<ArmorMaterial> material, ResourceLocation texture) {
        super(material, BodyType.CANINE, false, new Item.Properties()
                .stacksTo(1)
                .durability(ArmorItem.Type.BODY.getDurability(9))
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
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
        // The larger armor model is intentionally restricted to Wargs even though it uses the shared 1.21 body slot.
        return slot == EquipmentSlot.BODY && entity instanceof Warg;
    }
}
