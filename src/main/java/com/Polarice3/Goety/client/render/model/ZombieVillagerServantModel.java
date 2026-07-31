package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.geom.ModelPart;

public class ZombieVillagerServantModel<T extends Owned> extends HumanoidModel<T> implements VillagerHeadModel {
   private final ModelPart hatRim = this.hat.getChild("hat_rim");

   public ZombieVillagerServantModel(ModelPart p_171092_) {
      super(p_171092_);
   }

   public void setupAnim(T p_104175_, float p_104176_, float p_104177_, float p_104178_, float p_104179_, float p_104180_) {
      super.setupAnim(p_104175_, p_104176_, p_104177_, p_104178_, p_104179_, p_104180_);
      AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, p_104175_.isAggressive(), this.attackTime, p_104178_);
   }

   public void hatVisible(boolean p_104182_) {
      this.head.visible = p_104182_;
      this.hat.visible = p_104182_;
      this.hatRim.visible = p_104182_;
   }
}