package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.blocks.entities.PlinthHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Statues on a Plinth are drawn with no partial tick, so nothing on them moves between frames, not even the arm bob. */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererPlinthMixin {
    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private float goety$freezeStatue(float partialTicks, LivingEntity entity, float entityYaw, float ignored, PoseStack poseStack, MultiBufferSource buffer, int light) {
        return PlinthHooks.isFrozen(entity) ? 0.0F : partialTicks;
    }
}
