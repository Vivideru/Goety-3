package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.RainArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RainArrowRenderer extends ArrowRenderer<RainArrow> {
    public static final ResourceLocation ARROW_LOCATION = Goety.location("textures/entity/projectiles/rain_arrow.png");

    public RainArrowRenderer(EntityRendererProvider.Context p_173917_) {
        super(p_173917_);
    }

    @Override
    public ResourceLocation getTextureLocation(RainArrow p_114482_) {
        return ARROW_LOCATION;
    }
}
