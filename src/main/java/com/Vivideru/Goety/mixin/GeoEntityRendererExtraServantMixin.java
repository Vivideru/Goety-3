package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.entities.extra.ExtraServants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The same for mobs drawn by GeckoLib, when it is installed. Renderers that pick their texture in a method of their own
 * keep their texture.
 */
@Pseudo
@Mixin(targets = "software.bernie.geckolib.renderer.GeoEntityRenderer", remap = false)
public abstract class GeoEntityRendererExtraServantMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 0)
    private void goety$extraServantTexture(Entity entity, CallbackInfoReturnable<ResourceLocation> cir) {
        ResourceLocation texture = ExtraServants.texture(entity);
        if (texture != null) {
            cir.setReturnValue(texture);
        }
    }
}
