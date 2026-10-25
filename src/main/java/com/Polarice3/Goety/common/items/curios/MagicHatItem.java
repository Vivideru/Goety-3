package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.ModAttributeUtil;
import com.Polarice3.Goety.utils.ModUUIDUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class MagicHatItem extends SingleStackItem {

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        if (this == ModItems.DARK_CROWN.get()){
            map.put(Attributes.ARMOR, ModAttributeUtil.create(ModUUIDUtil.createUUID("item.goety.dark_crown.armor"), "Dark Crown Armor", 2.0F, AttributeModifier.Operation.ADD_VALUE));
        }
        return map;
    }

}
