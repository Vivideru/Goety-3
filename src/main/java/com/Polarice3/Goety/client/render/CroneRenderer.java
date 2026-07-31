package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.CroneItemLayer;
import com.Polarice3.Goety.client.render.model.CroneModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.PotionItem;

public class CroneRenderer extends MobRenderer<Crone, CroneModel<Crone>> {
   private static final ResourceLocation WITCH_LOCATION = Goety.location("textures/entity/cultist/crone.png");

   public CroneRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new CroneModel<>(p_174443_.bakeLayer(ModModelLayer.CRONE)), 0.5F);
      this.addLayer(new CroneItemLayer<>(this, p_174443_.getItemInHandRenderer()));
      this.addLayer(new CustomHeadLayer<>(this, p_174443_.getModelSet(), p_174443_.getItemInHandRenderer()));
   }

   public void render(Crone p_116412_, float p_116413_, float p_116414_, PoseStack p_116415_, MultiBufferSource p_116416_, int p_116417_) {
      // Crone drinks from the offhand in 1.21 to avoid interrupting her main-hand ranged brew logic.
      // Only the actual drink timer should switch to the crossed drinking mesh; held brews in idle use the normal sleeves.
      this.model.setHoldingItem(p_116412_.isDrinkingPotion() && ((!p_116412_.getMainHandItem().isEmpty()
              && (p_116412_.getMainHandItem().getItem() instanceof BrewItem
              || p_116412_.getMainHandItem().getItem() instanceof PotionItem))
              || (!p_116412_.getOffhandItem().isEmpty()
              && (p_116412_.getOffhandItem().getItem() instanceof BrewItem
              || p_116412_.getOffhandItem().getItem() instanceof PotionItem))));
      super.render(p_116412_, p_116413_, p_116414_, p_116415_, p_116416_, p_116417_);
   }

   public ResourceLocation getTextureLocation(Crone p_116410_) {
      return WITCH_LOCATION;
   }
}
