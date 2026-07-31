package com.Polarice3.Goety.client;

import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

@OnlyIn(Dist.CLIENT)
public class SpellPoseEnumParams {
    public static final EnumProxy<HumanoidModel.ArmPose> SPELL = new EnumProxy<>(
            HumanoidModel.ArmPose.class,
            false,
            (IArmPoseTransformer) SpellPoseEnumParams::applySpellPose
    );
    public static final EnumProxy<HumanoidModel.ArmPose> FLYING = new EnumProxy<>(
            HumanoidModel.ArmPose.class,
            false,
            (IArmPoseTransformer) SpellPoseEnumParams::applyFlyingPose
    );
    public static final EnumProxy<HumanoidModel.ArmPose> HOLD_STAFF = new EnumProxy<>(
            HumanoidModel.ArmPose.class,
            false,
            (IArmPoseTransformer) SpellPoseEnumParams::applyHoldStaffPose
    );

    private static void applySpellPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        // NeoForge 1.21 requires custom third-person arm poses to be supplied through enum extensions.
        float f5 = entity.walkAnimation.position(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot -= MathHelper.modelDegrees(105);
            model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.25F;
            model.leftArm.xRot += MathHelper.modelDegrees(25);
        } else {
            model.leftArm.xRot -= MathHelper.modelDegrees(105);
            model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.25F;
            model.rightArm.xRot += MathHelper.modelDegrees(25);
        }
    }

    private static void applyFlyingPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        float f5 = 1.0F;
        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot = -MathHelper.modelDegrees(105);
            model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.25F;
            model.leftArm.xRot = MathHelper.modelDegrees(25);
        } else {
            model.leftArm.xRot = -MathHelper.modelDegrees(105);
            model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.25F;
            model.rightArm.xRot = MathHelper.modelDegrees(25);
        }
        model.rightLeg.xRot = MathHelper.modelDegrees(17.5F);
        model.leftLeg.xRot = MathHelper.modelDegrees(17.5F);

        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        model.rightLeg.xRot += Mth.sin(partialTick * 0.067F) * 0.05F;
        model.leftLeg.xRot += -Mth.sin(partialTick * 0.067F) * 0.05F;
    }

    private static void applyHoldStaffPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        float f5 = entity.walkAnimation.position(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot -= MathHelper.modelDegrees(90);
            model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.1F;
        } else {
            model.leftArm.xRot -= MathHelper.modelDegrees(90);
            model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.1F;
        }
    }
}
