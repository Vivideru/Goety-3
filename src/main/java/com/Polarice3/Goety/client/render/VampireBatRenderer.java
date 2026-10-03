package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ambient.Bat;

public class VampireBatRenderer extends MobRenderer<Bat, BatModel> {
   private static final ResourceLocation BAT_LOCATION = Goety.location("textures/entity/servants/vampire_bat.png");

   public VampireBatRenderer(EntityRendererProvider.Context p_173929_) {
      super(p_173929_, new BatModel(p_173929_.bakeLayer(ModelLayers.BAT)), 0.25F);
   }

   public ResourceLocation getTextureLocation(Bat p_113876_) {
      return BAT_LOCATION;
   }
}
