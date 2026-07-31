package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class VivideruWolfArmorUtil {
    public static boolean isSupportedWolf(Summoned summoned) {
        return summoned instanceof BlackWolf || summoned instanceof SkeletonWolf;
    }

    public static void equipRingGrantedArmor(Summoned summoned) {
        if (isSupportedWolf(summoned) && summoned.canSpawnArmor() && summoned.getBodyArmorItem().isEmpty()) {
            // Hunting Focus creates hounds directly, so the Ring of the Forge armor is applied explicitly instead of relying on random equipment setup.
            summoned.setItemSlot(EquipmentSlot.BODY, new ItemStack(VivideruItems.CURSED_METAL_WOLF_ARMOR.get()));
            summoned.setDropChance(EquipmentSlot.BODY, 0.0F);
        }
    }
}
