package com.Vivideru.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.items.CursedMetalWolfArmorItem;
import com.Vivideru.Goety.common.items.VivideruItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber(modid = Goety.MOD_ID)
public class VivideruCommonEvents {
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            removeInvalidCursedWolfArmor(living);
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.BODY || !event.getTo().is(VivideruItems.CURSED_METAL_WOLF_ARMOR.get())) {
            return;
        }
        removeInvalidCursedWolfArmor(event.getEntity());
    }

    private static void removeInvalidCursedWolfArmor(LivingEntity living) {
        ItemStack bodyArmor = living.getItemBySlot(EquipmentSlot.BODY);
        if (!bodyArmor.is(VivideruItems.CURSED_METAL_WOLF_ARMOR.get())) {
            return;
        }
        if (!(bodyArmor.getItem() instanceof CursedMetalWolfArmorItem armorItem)) {
            return;
        }
        if (!armorItem.canEquip(bodyArmor, EquipmentSlot.BODY, living)) {
            // Existing worlds may already contain mobs with this armor in BODY, so strip invalid equips as soon as they are observed.
            living.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
        }
    }
}
