package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import net.minecraft.resources.ResourceLocation;

public class DarkWolfArmorItem extends CursedMetalWolfArmorItem {
    public static final ResourceLocation VANILLA_WOLF_TEXTURE = Goety.location("textures/entity/wolf/dark_wolf_armor.png");
    public static final ResourceLocation BLACK_WOLF_TEXTURE = Goety.location("textures/entity/servants/black_wolf/dark_wolf_armor.png");

    public DarkWolfArmorItem() {
        super(VivideruArmorMaterials.DARK_WOLF, VANILLA_WOLF_TEXTURE, BLACK_WOLF_TEXTURE);
    }
}
