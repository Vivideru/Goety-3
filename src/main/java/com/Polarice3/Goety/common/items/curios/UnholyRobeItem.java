package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class UnholyRobeItem extends SingleStackItem{

    public UnholyRobeItem() {
        super(new Properties().fireResistant().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            if (CuriosFinder.hasCurio(livingEntity, this)) {
                if (livingEntity.isOnFire()){
                    livingEntity.clearFire();
                }
                if (!worldIn.isClientSide) {
                    if (livingEntity.hasEffect(GoetyEffects.BURN_HEX)){
                        livingEntity.removeEffect(GoetyEffects.BURN_HEX);
                    }
                }
            }
        }

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        UUID uuid, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        if (IronLoaded.IRON_SPELLBOOKS.isLoaded()){
            if (MainConfig.RobesIronResist.get()) {
                map.put(IronAttributes.holder(IronAttributes.FIRE_MAGIC_RESIST), com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("bd88096f-189e-4b29-adc2-e65255f73a9f"), "Robes Iron Fire Spell Resist", 1.0F, AttributeModifier.Operation.ADD_VALUE));
                map.put(IronAttributes.holder(IronAttributes.NATURE_MAGIC_RESIST), com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("3987801f-f418-475d-b662-4544eb267e88"), "Robes Iron Nature Spell Resist", 0.5F, AttributeModifier.Operation.ADD_VALUE));
                map.put(IronAttributes.holder(IronAttributes.BLOOD_MAGIC_RESIST), com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("f81a0876-dcb9-4eee-b382-e5bc6170a54f"), "Robes Iron Blood Spell Resist", 0.75F, AttributeModifier.Operation.ADD_VALUE));
                map.put(IronAttributes.holder(IronAttributes.HOLY_MAGIC_RESIST), com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("d29ed6a0-f38d-4c77-89b2-850743ec96b8"), "Robes Iron Holy Spell Resist", -0.25F, AttributeModifier.Operation.ADD_VALUE));
            }
        }
        return map;
    }
}
