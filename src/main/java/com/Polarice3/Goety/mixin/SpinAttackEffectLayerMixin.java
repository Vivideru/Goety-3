package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.utils.MiscCapHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpinAttackEffectLayer.class)
public class SpinAttackEffectLayerMixin {

    @ModifyVariable(method = "render*", at = @At("STORE"))
    public VertexConsumer selectSpinAttackTexture(VertexConsumer original, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, LivingEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        ResourceLocation resourceLocation = MiscCapHelper.getCustomSpinTexture(pEntity);
        if (resourceLocation != null) {
            return pBuffer.getBuffer(RenderType.entityCutoutNoCull(resourceLocation));
        } else {
            return original;
        }
    }
}
