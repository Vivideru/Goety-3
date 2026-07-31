package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;
import java.util.List;
import net.minecraft.resources.ResourceKey;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    private static final Set<ResourceKey<Enchantment>> FOCUS_ENCHANTMENTS = Set.of(
            ModEnchantments.POTENCY,
            ModEnchantments.RADIUS,
            ModEnchantments.RANGE,
            ModEnchantments.DURATION,
            ModEnchantments.VELOCITY
    );

    @ModifyVariable(method = "run", at = @At("STORE"))
    private List<Holder<Enchantment>> filterEnchants(List<Holder<Enchantment>> enchantments) {
        // Minecraft 1.21 builds this list with Stream#toList, so return a filtered copy instead of mutating the immutable list.
        return enchantments.stream()
                .filter(enchantment -> enchantment.unwrapKey().map(key -> !FOCUS_ENCHANTMENTS.contains(key)).orElse(true))
                .toList();
    }
}
