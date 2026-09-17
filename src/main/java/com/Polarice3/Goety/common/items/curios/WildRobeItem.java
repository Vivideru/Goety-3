package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class WildRobeItem extends SingleStackItem {

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            if (entityIn instanceof LivingEntity livingEntity) {
                if (CuriosFinder.hasWildRobe(livingEntity)){
                    if (livingEntity.hasEffect(MobEffects.POISON)){
                        livingEntity.removeEffect(MobEffects.POISON);
                    }
                    if (livingEntity.hasEffect(GoetyEffects.ACID_VENOM)){
                        livingEntity.removeEffect(GoetyEffects.ACID_VENOM);
                    }
                }
            }
        }

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        if (IronLoaded.IRON_SPELLBOOKS.isLoaded()){
            if (MainConfig.RobesIronResist.get()) {
                map.put(IronAttributes.holder(IronAttributes.NATURE_MAGIC_RESIST), com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("73cd408f-b6b5-470c-ba69-560cb85f41ad"), "Robes Iron Spell Resist", 0.5F, AttributeModifier.Operation.ADD_VALUE));
            }
        }
        return map;
    }
}
