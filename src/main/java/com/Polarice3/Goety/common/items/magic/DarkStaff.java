package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.magic.SpellType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class DarkStaff extends DarkWand {

    public DarkStaff(Properties properties, double damage, double attackSpeed, SpellType spellType){
        super(properties.attributes(createStaffAttributes(damage, attackSpeed)), spellType);
    }

    public DarkStaff(Properties properties, double damage, SpellType spellType) {
        this(properties, damage, -2.4D, spellType);
    }

    public DarkStaff(double damage, double attackSpeed, SpellType spellType) {
        this(wandProperties(), damage, attackSpeed, spellType);
    }

    public DarkStaff(double damage, SpellType spellType) {
        this(damage, -2.4D, spellType);
    }

    private static ItemAttributeModifiers createStaffAttributes(double damage, double attackSpeed) {
        // Stack-aware attribute overrides were removed in 1.21; staff combat stats now live in the item attribute component.
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_DAMAGE_ID, damage - 1.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public float getWandVisualHeight(Level level, LivingEntity entity, ItemStack stack) {
        return 0.8F;
    }
}
