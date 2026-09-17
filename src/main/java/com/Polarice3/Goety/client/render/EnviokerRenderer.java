package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.EnviokerModel;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.SpellcasterIllager;

public class EnviokerRenderer<T extends SpellcasterIllager> extends MobRenderer<T, EnviokerModel<T>> {
   private static final ResourceLocation EVOKER_ILLAGER = Goety.location("textures/entity/illagers/envioker.png");

   public EnviokerRenderer(EntityRendererProvider.Context p_174108_) {
      super(p_174108_, new EnviokerModel<>(p_174108_.bakeLayer(ModModelLayer.ENVIOKER)), 0.5F);
      this.addLayer(new CustomHeadLayer<>(this, p_174108_.getModelSet(), p_174108_.getItemInHandRenderer()));
      this.addLayer(new HumanoidArmorLayer<>(this,
         new VillagerArmorModel<>(p_174108_.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)),
         new VillagerArmorModel<>(p_174108_.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)),
         p_174108_.getModelManager()));
      this.addLayer(new ItemInHandLayer<>(this, p_174108_.getItemInHandRenderer()) {
         public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.getArmPose() != AbstractIllager.IllagerArmPose.CROSSED) {
               super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }

         }
      });
   }

   public ResourceLocation getTextureLocation(T p_114541_) {
      return EVOKER_ILLAGER;
   }
}
