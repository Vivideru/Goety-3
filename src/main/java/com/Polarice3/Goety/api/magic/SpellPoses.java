package com.Polarice3.Goety.api.magic;

import net.minecraft.client.model.HumanoidModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellPoses {
    public static final HumanoidModel.ArmPose SPELL = HumanoidModel.ArmPose.valueOf("GOETY_SPELL");
    public static final HumanoidModel.ArmPose FLIGHT_POSE = HumanoidModel.ArmPose.valueOf("GOETY_FLYING");
    public static final HumanoidModel.ArmPose HOLD_STAFF = HumanoidModel.ArmPose.valueOf("GOETY_HOLD_STAFF");
}
