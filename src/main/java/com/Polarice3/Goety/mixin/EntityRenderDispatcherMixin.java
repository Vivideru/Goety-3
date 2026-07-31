package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.client.render.LichModeRendererHolder;
import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void goety$getLichRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T>> cir) {
        if (entity instanceof AbstractClientPlayer player
                && LichdomHelper.isLich(player)
                && LichdomHelper.isInLichMode(player)
                && LichModeRendererHolder.renderer != null) {
            // 1.21 removed the string-based player model selector, so lich mode swaps the renderer at dispatch time.
            cir.setReturnValue((EntityRenderer<? super T>) LichModeRendererHolder.renderer);
        }
    }
}
