package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.items.VivideruItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfArmorLayer.class)
public class WolfArmorLayerMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void goety$skipVanillaWolfArmorForCursedMetal(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Wolf wolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (VivideruItems.isVivideruWolfArmor(wolf.getBodyArmorItem())) {
            // Cursed metal armor has extra model geometry, so the vanilla wolf armor layer is replaced by the Vivideru layer for this item.
            ci.cancel();
        }
    }
}
