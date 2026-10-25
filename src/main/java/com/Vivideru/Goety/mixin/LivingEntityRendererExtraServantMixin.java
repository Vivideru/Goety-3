package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.entities.extra.ExtraServants;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Extra servants with a texture of their own: the body of the mob is drawn with it instead of the mob's. */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererExtraServantMixin {
    @WrapOperation(method = "getRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation goety$extraServantTexture(LivingEntityRenderer<?, ?> renderer, Entity entity, Operation<ResourceLocation> original) {
        ResourceLocation texture = ExtraServants.texture(entity);
        return texture != null ? texture : original.call(renderer, entity);
    }
}
