package com.Vivideru.Goety.compat.apotheosis;

import dev.shadowsoffire.apothic_attributes.modifiers.StackAttributeModifiers;
import dev.shadowsoffire.apothic_attributes.modifiers.StackAttributeModifiersEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public final class MasterStaffAttributeBridge {
    private MasterStaffAttributeBridge() {
    }

    @SubscribeEvent
    public static void forwardSelectedWandModifiers(StackAttributeModifiersEvent event) {
        ItemStack masterStaff = event.getItemStack();
        if (!WandSupport.MASTER_STAFF_ID.equals(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(masterStaff.getItem()))) {
            return;
        }

        ItemStack selectedWand = WandSupport.getSelectedWand(masterStaff);
        if (selectedWand.isEmpty()) {
            return;
        }

        StackAttributeModifiers selectedModifiers =
            StackAttributeModifiers.fromVanilla(selectedWand.getAttributeModifiers());

        selectedModifiers.modifiers().forEach(entry -> {
            if (isGoetyMagicAttribute(entry)) {
                event.addModifier(entry.attribute(), entry.modifier(), entry.slots());
            }
        });
    }

    private static boolean isGoetyMagicAttribute(StackAttributeModifiers.Entry entry) {
        return entry.attribute().unwrapKey()
            .map(key -> "goety".equals(key.location().getNamespace())
                || ApotheosisCompat.MOD_ID.equals(key.location().getNamespace()))
            .orElse(false);
    }
}
