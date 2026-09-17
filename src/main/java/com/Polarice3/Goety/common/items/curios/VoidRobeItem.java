package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class VoidRobeItem extends SingleStackItem {

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            if (entityIn instanceof LivingEntity livingEntity) {
                if (CuriosFinder.hasVoidRobe(livingEntity)){
                    if (livingEntity.hasEffect(GoetyEffects.VOID_TOUCHED)){
                        livingEntity.removeEffect(GoetyEffects.VOID_TOUCHED);
                    }
                    if (ItemConfig.VoidRobeWaterSapped.get() > 0) {
                        if (livingEntity.isInWaterRainOrBubble()) {
                            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED, 10, ItemConfig.VoidRobeWaterSapped.get() - 1, false, false));
                        }
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
                map.put(IronAttributes.holder(IronAttributes.ENDER_MAGIC_RESIST), com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("1e3b0825-f512-4ed1-b933-312105d92272"), "Robes Iron Spell Resist", 0.5F, AttributeModifier.Operation.ADD_VALUE));
            }
        }
        return map;
    }
}
