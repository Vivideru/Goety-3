package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.utils.LootingLevelHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EnchantedCountIncreaseFunction.class, remap = false)
public class EnchantedCountIncreaseFunctionMixin {

    @ModifyExpressionValue(
            method = "run",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I")
    )
    private int goety$modifyLootingLevel(int original, ItemStack stack, LootContext context) {
        // Keeping the vanilla method body intact lets Curios inject into the same loot function after Goety adjusts the looting value.
        return LootingLevelHelper.modifyLootingLevel(context, original);
    }
}
