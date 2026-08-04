package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import net.minecraft.resources.ResourceLocation;

public class DarkBlackBeastArmorItem extends CursedBlackBeastArmorItem {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/black_wolf/black_beast_dark_armor.png");

    public DarkBlackBeastArmorItem() {
        super(VivideruArmorMaterials.DARK_BLACK_BEAST, TEXTURE);
    }
}
