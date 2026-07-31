package com.Polarice3.Goety.common.items.curios;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class WayfarersBeltItem extends SingleStackItem {

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        UUID uuid, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        // Curios applies these as attribute modifiers; additive speed mirrors the original small movement boost more reliably in 1.21.
        map.put(Attributes.MOVEMENT_SPEED, com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("f46dd333-63a3-4c3b-a5d3-065de1e226cd"), "Wayfarer Speed bonus", 0.03F, AttributeModifier.Operation.ADD_VALUE));
        // NeoForge's old step-height addition attribute is vanilla Attributes.STEP_HEIGHT in 1.21.
        map.put(Attributes.STEP_HEIGHT, com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("532a87ef-73fc-40c3-a950-ee26bbbcd2d7"), "Wayfarer Step Height bonus", 1.0625F, AttributeModifier.Operation.ADD_VALUE));
        map.put(NeoForgeMod.SWIM_SPEED, com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("b2e95923-1ce4-49c1-b110-5ceb2f428df8"), "Wayfarer Swim bonus", 0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return map;
    }
}
