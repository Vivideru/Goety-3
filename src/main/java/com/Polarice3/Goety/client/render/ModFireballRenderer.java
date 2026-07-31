package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;

public class ModFireballRenderer<T extends Entity & ItemSupplier> extends ThrownItemRenderer<T> {
    public ModFireballRenderer(EntityRendererProvider.Context p_174416_, float p_174417_, boolean p_174418_) {
        super(p_174416_, p_174417_, p_174418_);
    }

    public ModFireballRenderer(EntityRendererProvider.Context p_174414_) {
        super(p_174414_);
    }

    @Override
    public void render(T p_116085_, float p_116086_, float p_116087_, PoseStack p_116088_, MultiBufferSource p_116089_, int p_116090_) {
        EntityType<?> entityType = null;
        if (p_116085_.getType() == ModEntityType.MOD_FIREBALL.get()){
            entityType = EntityType.SMALL_FIREBALL;
        } else if (p_116085_.getType() == ModEntityType.LAVABALL.get()){
            entityType = EntityType.FIREBALL;
        }
        // 1.21 made the dispatcher renderer map private; keep the existing thrown-item fallback instead of reaching into internals.
        super.render(p_116085_, p_116086_, p_116087_, p_116088_, p_116089_, p_116090_);
    }
}
