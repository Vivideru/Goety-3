package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ZombieVillagerServantModel;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;

public class ZombieVillagerServantRenderer extends HumanoidMobRenderer<ZombieVillagerServant, ZombieVillagerServantModel<ZombieVillagerServant>> {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = Goety.location("textures/entity/servants/zombie/zombie_villager_servant.png");

   public ZombieVillagerServantRenderer(EntityRendererProvider.Context p_174463_) {
      super(p_174463_, new ZombieVillagerServantModel<>(p_174463_.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new ZombieVillagerServantModel<>(p_174463_.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerServantModel<>(p_174463_.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), p_174463_.getModelManager()));
      this.addLayer(new VillagerProfessionLayer<>(this, p_174463_.getResourceManager(), "zombie_villager"));
   }

   public ResourceLocation getTextureLocation(ZombieVillagerServant p_116559_) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   protected boolean isShaking(ZombieVillagerServant p_116561_) {
      return super.isShaking(p_116561_) || p_116561_.isConverting();
   }
}