package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.common.items.equipment.SickleItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.ModAttributeUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

public class ConfiguredItemAttributeEvents {

    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof IPersist persist && persist.isBroken(event.getItemStack())) {
            event.clearModifiers();
            return;
        }

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
        } else if (item instanceof SickleItem sickle) {
            replaceAttack(event, ItemConfig.SickleBaseDamage.get() + sickle.getTier().getAttackDamageBonus() - 1.0D);
            replaceSpeed(event, -(4.0D - ItemConfig.SickleAttackSpeed.get()));
        } else if (item instanceof ArmorItem armor) {
            Holder<ArmorMaterial> material = armor.getMaterial();
            if (material == ModArmorMaterials.CURSED_KNIGHT) {
                replaceArmor(event, armor, ItemConfig.CursedKnightFeet, ItemConfig.CursedKnightLegs, ItemConfig.CursedKnightChest, ItemConfig.CursedKnightHead, ItemConfig.CursedKnightToughness, ItemConfig.CursedKnightKnockResist);
            } else if (material == ModArmorMaterials.CURSED_PALADIN) {
                replaceArmor(event, armor, ItemConfig.CursedPaladinFeet, ItemConfig.CursedPaladinLegs, ItemConfig.CursedPaladinChest, ItemConfig.CursedPaladinHead, ItemConfig.CursedPaladinToughness, ItemConfig.CursedPaladinKnockResist);
            } else if (material == ModArmorMaterials.BLACK_IRON) {
                replaceArmor(event, armor, ItemConfig.BlackIronFeet, ItemConfig.BlackIronLegs, ItemConfig.BlackIronChest, ItemConfig.BlackIronHead, ItemConfig.BlackIronToughness, ItemConfig.BlackIronKnockResist);
            } else if (material == ModArmorMaterials.DARK) {
                replaceArmor(event, armor, ItemConfig.DarkArmorFeet, ItemConfig.DarkArmorLegs, ItemConfig.DarkArmorChest, ItemConfig.DarkArmorHead, ItemConfig.DarkArmorToughness, ItemConfig.DarkArmorKnockResist);
            } else if (material == ModArmorMaterials.MALEFIC) {
                replaceArmor(event, armor, null, null, null, ItemConfig.MaleficHead, ItemConfig.MaleficToughness, ItemConfig.MaleficKnockResist);
            }
        }
    }

    private static void replaceArmor(ItemAttributeModifierEvent event, ArmorItem armor, @Nullable ModConfigSpec.ConfigValue<Integer> feet, @Nullable ModConfigSpec.ConfigValue<Integer> legs,
                                     @Nullable ModConfigSpec.ConfigValue<Integer> chest, @Nullable ModConfigSpec.ConfigValue<Integer> head,
                                     ModConfigSpec.ConfigValue<Double> toughness, ModConfigSpec.ConfigValue<Double> knockResist) {
        ModConfigSpec.ConfigValue<Integer> defense = switch (armor.getType()) {
            case BOOTS -> feet;
            case LEGGINGS -> legs;
            case CHESTPLATE -> chest;
            case HELMET -> head;
            default -> null;
        };
        ResourceLocation id = ResourceLocation.withDefaultNamespace("armor." + armor.getType().getName());
        EquipmentSlotGroup slot = EquipmentSlotGroup.bySlot(armor.getEquipmentSlot());
        if (defense != null) {
            event.replaceModifier(Attributes.ARMOR, new AttributeModifier(id, defense.get(), AttributeModifier.Operation.ADD_VALUE), slot);
        }
        event.replaceModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness.get(), AttributeModifier.Operation.ADD_VALUE), slot);
        if (knockResist.get() > 0.0D) {
            event.replaceModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, knockResist.get(), AttributeModifier.Operation.ADD_VALUE), slot);
        } else {
            event.removeModifier(Attributes.KNOCKBACK_RESISTANCE, id);
        }
    }

    private static void replaceAttack(ItemAttributeModifierEvent event, double amount) {
        event.replaceModifier(Attributes.ATTACK_DAMAGE, ModAttributeUtil.create(Item.BASE_ATTACK_DAMAGE_ID, amount, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }

    private static void replaceSpeed(ItemAttributeModifierEvent event, double amount) {
        event.replaceModifier(Attributes.ATTACK_SPEED, ModAttributeUtil.create(Item.BASE_ATTACK_SPEED_ID, amount, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }
}
