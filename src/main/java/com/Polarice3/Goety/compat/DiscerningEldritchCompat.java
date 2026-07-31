package com.Polarice3.Goety.compat;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class DiscerningEldritchCompat {
    private static final String SOUL_FIRE_SCYTHE_CLASS = "net.acetheeldritchking.discerning_the_eldritch.items.weapons.SoulFireScytheItem";
    private static final ResourceLocation[] SOUL_FIRE_STACK_COMPONENTS = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "soul_fire_stacks"),
            ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "soulfire_stacks"),
            ResourceLocation.fromNamespaceAndPath("soul_fire_d", "soul_fire_stacks"),
            ResourceLocation.fromNamespaceAndPath("soul_fire_d", "soulfire_stacks")
    };

    private DiscerningEldritchCompat() {
    }

    public static void initializeSoulFireScythe(ItemStack stack) {
        if (stack.isEmpty() || !SOUL_FIRE_SCYTHE_CLASS.equals(stack.getItem().getClass().getName())) {
            return;
        }
        // Discerning the Eldritch 1.4.3 can dereference its soul-fire stack component before it is initialized.
        for (ResourceLocation componentId : SOUL_FIRE_STACK_COMPONENTS) {
            setMissingIntegerComponent(stack, componentId, 0);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setMissingIntegerComponent(ItemStack stack, ResourceLocation componentId, int value) {
        DataComponentType<?> componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(componentId);
        if (componentType != null && stack.get(componentType) == null) {
            stack.set((DataComponentType) componentType, value);
        }
    }
}
