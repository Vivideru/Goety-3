package com.Vivideru.Goety.compat.apotheosis;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GoetyApotheosisAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
        DeferredRegister.create(Registries.ATTRIBUTE, ApotheosisCompat.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> SPELL_ARMOR_PIERCE =
        percentAttribute("spell_armor_pierce");
    public static final DeferredHolder<Attribute, Attribute> CASTING_DAMAGE_REDUCTION =
        percentAttribute("casting_damage_reduction");
    public static final DeferredHolder<Attribute, Attribute> BURN_HEX_CHANCE =
        percentAttribute("burn_hex_chance");
    public static final DeferredHolder<Attribute, Attribute> SPELL_LOOTING = ATTRIBUTES.register(
        "spell_looting",
        () -> new RangedAttribute("attribute.goety_apotheosis.spell_looting", 0.0D, 0.0D, 64.0D).setSyncable(true)
    );

    private GoetyApotheosisAttributes() {
    }

    private static DeferredHolder<Attribute, Attribute> percentAttribute(String name) {
        return ATTRIBUTES.register(
            name,
            () -> new RangedAttribute("attribute.goety_apotheosis." + name, 1.0D, 1.0D, 2.0D).setSyncable(true)
        );
    }

    public static void addPlayerAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, SPELL_ARMOR_PIERCE);
        event.add(EntityType.PLAYER, CASTING_DAMAGE_REDUCTION);
        event.add(EntityType.PLAYER, BURN_HEX_CHANCE);
        event.add(EntityType.PLAYER, SPELL_LOOTING);
    }
}
