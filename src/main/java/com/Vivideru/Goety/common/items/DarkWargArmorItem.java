package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.Goety;
import net.minecraft.resources.ResourceLocation;

public class DarkWargArmorItem extends CursedWargArmorItem {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/black_wolf/warg_dark_alloy_armor.png");

    public DarkWargArmorItem() {
        super(VivideruArmorMaterials.DARK_WARG, TEXTURE);
    }
}
