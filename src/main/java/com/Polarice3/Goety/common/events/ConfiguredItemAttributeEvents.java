package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.ModAttributeUtil;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class ConfiguredItemAttributeEvents {

    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        Item item = event.getItemStack().getItem();

        if (item == ModItems.OMINOUS_SCYTHE.get()) {
            replaceAttack(event, ItemConfig.ScytheBaseDamage.get() + ItemConfig.SpecialToolsDamage.get() - 1.0D);
            replaceSpeed(event, -(4.0D - ItemConfig.ScytheAttackSpeed.get()));
        } else if (item == ModItems.DARK_SCYTHE.get()) {
            replaceAttack(event, ItemConfig.ScytheBaseDamage.get() + ItemConfig.DarkToolsDamage.get() - 1.0D);
            replaceSpeed(event, -(4.0D - ItemConfig.ScytheAttackSpeed.get()));
        } else if (item == ModItems.DEATH_SCYTHE.get()) {
            replaceAttack(event, ItemConfig.ScytheBaseDamage.get() + ItemConfig.DeathScytheDamage.get() - 1.0D);
            replaceSpeed(event, -(4.0D - ItemConfig.ScytheAttackSpeed.get()));
        } else if (item == ModItems.GREAT_HAMMER.get()
                || item == ModItems.BONEHEAD_HAMMER.get()
                || item == ModItems.STORMLANDER.get()) {
            replaceAttack(event, ItemConfig.HammerBaseDamage.get() + 2.0D - 1.0D);
            replaceSpeed(event, -(4.0D - ItemConfig.HammerAttackSpeed.get()));
        } else if (item == ModItems.PHILOSOPHERS_MACE.get()) {
            replaceAttack(event, ItemConfig.PhilosophersMaceDamage.get() - 1.0D);
        } else if (item == ModItems.BLADE_OF_ENDER.get()) {
            replaceAttack(event, ItemConfig.BladeOfEnderDamage.get() - 1.0D);
            replaceSpeed(event, -(4.0D - ItemConfig.BladeOfEnderAttackSpeed.get()));
        } else if (item == ModItems.DARK_SWORD.get()) {
            replaceAttack(event, 3.0D + ItemConfig.DarkToolsDamage.get());
        } else if (item == ModItems.DARK_SHOVEL.get()) {
            replaceAttack(event, 1.5D + ItemConfig.DarkToolsDamage.get());
        } else if (item == ModItems.DARK_PICKAXE.get()) {
            replaceAttack(event, 1.0D + ItemConfig.DarkToolsDamage.get());
        } else if (item == ModItems.DARK_AXE.get()) {
            replaceAttack(event, 5.0D + ItemConfig.DarkToolsDamage.get());
        } else if (item == ModItems.DARK_HOE.get()) {
            replaceAttack(event, -3.0D + ItemConfig.DarkToolsDamage.get());
        } else if (item == ModItems.FANGED_DAGGER.get()) {
            replaceAttack(event, ItemConfig.SpecialToolsDamage.get());
        } else if (item == ModItems.HUNGRY_DAGGER.get()) {
            replaceAttack(event, ItemConfig.DarkToolsDamage.get());
        } else if (item == ModItems.EERIE_PICKAXE.get()) {
            replaceAttack(event, 1.0D + ItemConfig.SpecialToolsDamage.get());
        } else if (item == ModItems.RAMPAGING_AXE.get()) {
            replaceAttack(event, 5.0D + ItemConfig.SpecialToolsDamage.get());
        } else if (item == ModItems.GRAVEROBBER_SHOVEL.get()) {
            replaceAttack(event, 1.5D + ItemConfig.SpecialToolsDamage.get());
        } else if (item == ModItems.FELL_BLADE.get()) {
            replaceAttack(event, 3.0D + ItemConfig.SpecialToolsDamage.get());
        } else if (item == ModItems.FROZEN_BLADE.get()) {
            replaceAttack(event, 4.0D + ItemConfig.SpecialToolsDamage.get());
        } else if (item == ModItems.OMINOUS_STAFF.get()) {
            replaceAttack(event, ItemConfig.OminousStaffDamage.get() - 1.0D);
        } else if (item == ModItems.NECRO_STAFF.get()) {
            replaceAttack(event, ItemConfig.NecroStaffDamage.get() - 1.0D);
        } else if (item == ModItems.GEO_STAFF.get()) {
            replaceAttack(event, ItemConfig.GeoStaffDamage.get() - 1.0D);
        } else if (item == ModItems.WIND_STAFF.get()) {
            replaceAttack(event, ItemConfig.WindStaffDamage.get() - 1.0D);
        } else if (item == ModItems.STORM_STAFF.get()) {
            replaceAttack(event, ItemConfig.StormStaffDamage.get() - 1.0D);
        } else if (item == ModItems.FROST_STAFF.get()) {
            replaceAttack(event, ItemConfig.FrostStaffDamage.get() - 1.0D);
        } else if (item == ModItems.WILD_STAFF.get()) {
            replaceAttack(event, ItemConfig.WildStaffDamage.get() - 1.0D);
        } else if (item == ModItems.ABYSS_STAFF.get()) {
            replaceAttack(event, ItemConfig.AbyssStaffDamage.get() - 1.0D);
        } else if (item == ModItems.VOID_STAFF.get()) {
            replaceAttack(event, ItemConfig.VoidStaffDamage.get() - 1.0D);
        } else if (item == ModItems.NETHER_STAFF.get()) {
            replaceAttack(event, ItemConfig.NetherStaffDamage.get() - 1.0D);
        } else if (item == ModItems.NAMELESS_STAFF.get()) {
            replaceAttack(event, ItemConfig.NamelessStaffDamage.get() - 1.0D);
        }
    }

    private static void replaceAttack(ItemAttributeModifierEvent event, double amount) {
        // Item components are created before common configs are loaded in 1.21, so configurable combat values are applied at stack query time.
        event.replaceModifier(Attributes.ATTACK_DAMAGE, ModAttributeUtil.create(Item.BASE_ATTACK_DAMAGE_ID, amount, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }

    private static void replaceSpeed(ItemAttributeModifierEvent event, double amount) {
        event.replaceModifier(Attributes.ATTACK_SPEED, ModAttributeUtil.create(Item.BASE_ATTACK_SPEED_ID, amount, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }
}
