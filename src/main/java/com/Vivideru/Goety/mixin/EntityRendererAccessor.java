package com.Vivideru.Goety.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor {
    @Accessor("shadowRadius")
    float goety$getShadowRadius();

    @Accessor("shadowStrength")
    float goety$getShadowStrength();

    @Invoker("getBlockLightLevel")
    int goety$getBlockLightLevel(Entity entity, BlockPos pos);

    @Invoker("getSkyLightLevel")
    int goety$getSkyLightLevel(Entity entity, BlockPos pos);
}
