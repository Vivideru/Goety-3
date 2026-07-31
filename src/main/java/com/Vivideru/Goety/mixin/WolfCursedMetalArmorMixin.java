package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.items.VivideruItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfCursedMetalArmorMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void goety$cursedMetalWolfArmor(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(VivideruItems.CURSED_METAL_WOLF_ARMOR.get()) && wolf.isTame() && wolf.isOwnedBy(player) && wolf.getBodyArmorItem().isEmpty() && !wolf.isBaby()) {
            // Vanilla Wolf only checks Items.WOLF_ARMOR, so custom canine armor needs the same equip path before vanilla handling runs.
            wolf.setBodyArmorItem(stack.copyWithCount(1));
            stack.consume(1, player);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "hasArmor", at = @At("HEAD"), cancellable = true)
    private void goety$hasCursedMetalWolfArmor(CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getBodyArmorItem().is(VivideruItems.CURSED_METAL_WOLF_ARMOR.get())) {
            // Wolf armor damage mitigation is gated behind hasArmor(), which is hardcoded to the vanilla item in 1.21.
            cir.setReturnValue(true);
        }
    }
}
