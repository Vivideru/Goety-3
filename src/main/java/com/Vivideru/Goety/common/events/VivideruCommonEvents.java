package com.Vivideru.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemBlockEntity;
import com.Vivideru.Goety.common.items.CursedBlackBeastArmorItem;
import com.Vivideru.Goety.common.items.CursedMetalWolfArmorItem;
import com.Vivideru.Goety.common.items.VivideruItems;
import com.Vivideru.Goety.common.items.CursedWargArmorItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Goety.MOD_ID)
public class VivideruCommonEvents {
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            removeInvalidVivideruWolfArmor(living);
            if (!event.getLevel().isClientSide) {
                // Configurable attributes are reset while reading entity NBT, so restore the saved Totem bonus after joining the level.
                WolfTotemBlockEntity.restoreWolfTotemBonus(living, false);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.BODY
                || !VivideruItems.isVivideruBodyArmor(event.getTo())) {
            return;
        }
        removeInvalidVivideruWolfArmor(event.getEntity());
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity living) || living.level().isClientSide) {
            return;
        }
        ItemStack bodyArmor = living.getItemBySlot(EquipmentSlot.BODY);
        if ((bodyArmor.is(VivideruItems.DARK_WOLF_ARMOR.get()) || bodyArmor.is(VivideruItems.BLACK_BEAST_DARK_ARMOR.get())
                || bodyArmor.is(VivideruItems.WARG_DARK_ARMOR.get()))
                && living.tickCount % 100 == 0 && living.getHealth() < living.getMaxHealth()) {
            // Dark variants mirror the Dark Armor theme with a slow passive recovery instead of a permanent potion effect.
            living.heal(1.0F);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity wearer = event.getEntity();
        if (wearer.level().isClientSide || !VivideruItems.isVivideruBodyArmor(wearer.getItemBySlot(EquipmentSlot.BODY))) {
            return;
        }
        if (!event.getSource().is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
                && !event.getSource().is(DamageTypes.THORNS)
                && event.getSource().getEntity() instanceof LivingEntity attacker
                && attacker != wearer) {
            // These custom body armors behave as if the wearer had one level of Thorns without storing an enchantment on the item.
            attacker.hurt(attacker.damageSources().thorns(wearer), 3.0F);
        }
    }

    private static void removeInvalidVivideruWolfArmor(LivingEntity living) {
        ItemStack bodyArmor = living.getItemBySlot(EquipmentSlot.BODY);
        boolean invalidWolfArmor = bodyArmor.getItem() instanceof CursedMetalWolfArmorItem armorItem
                && !armorItem.canEquip(bodyArmor, EquipmentSlot.BODY, living);
        boolean invalidBeastArmor = bodyArmor.getItem() instanceof CursedBlackBeastArmorItem armorItem
                && !armorItem.canEquip(bodyArmor, EquipmentSlot.BODY, living);
        boolean invalidWargArmor = bodyArmor.getItem() instanceof CursedWargArmorItem armorItem
                && !armorItem.canEquip(bodyArmor, EquipmentSlot.BODY, living);
        if (invalidWolfArmor || invalidBeastArmor || invalidWargArmor) {
            // Existing worlds may already contain mobs with this armor in BODY, so strip invalid equips as soon as they are observed.
            living.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
        }
    }
}
