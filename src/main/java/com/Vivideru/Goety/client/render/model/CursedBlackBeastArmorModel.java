package com.Vivideru.Goety.client.render.model;

import com.Polarice3.Goety.client.render.model.BlackBeastModel;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CursedBlackBeastArmorModel<T extends BlackBeast> extends BlackBeastModel<T> {
    public CursedBlackBeastArmorModel(ModelPart root) {
        // Armor geometry replaces the base fur and must remain visible while the armor is equipped.
        super(root, false);
    }

public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition stalker = partdefinition.addOrReplaceChild("stalker", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = stalker.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 3.0F));

        PartDefinition pelvis = body.addOrReplaceChild("pelvis", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 1.5F));

        PartDefinition upper = pelvis.addOrReplaceChild("upper", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, -1.0F));

        PartDefinition torso = upper.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(6.0F, 3.0F, -4.5F));

        PartDefinition actual_torso = torso.addOrReplaceChild("actual_torso", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = actual_torso.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 32).addBox(-13.0F, -10.0F, -1.0F, 14.0F, 10.0F, 10.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -0.5F, -0.25F, 0.6981F, 0.0F, 0.0F));

        PartDefinition cube_r2 = actual_torso.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 52).addBox(-7.0F, -18.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(0, 52).addBox(-7.0F, -18.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, 12.8F, 0.6981F, 0.0F, 0.0F));

        PartDefinition cube_r3 = actual_torso.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(123, 0).addBox(-6.0F, -18.0F, -1.0F, 0.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6F, 14.3F, 0.6981F, 0.0F, 0.0F));

        PartDefinition fur = torso.addOrReplaceChild("fur", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.0F, -13.4F, 1.1F, 0.4363F, 0.0F, 0.0F));

        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(-4.0F, -7.0F, -1.0F));

        PartDefinition cube_r4 = neck.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(76, 47).addBox(-9.0F, -3.0F, -4.0F, 13.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.2941F, -4.6423F, 1.1345F, 0.0F, 0.0F));

        PartDefinition cube_r5 = neck.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(112, -4).addBox(0.0F, -1.5F, -4.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(112, -4).addBox(-6.0F, -1.5F, -4.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.9086F, -7.2391F, 1.1345F, 0.0F, 0.0F));

        PartDefinition cube_r6 = neck.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(112, -4).addBox(0.0F, -1.5F, -4.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.9086F, -7.2391F, 0.0F, 1.1345F, -1.5708F));

        PartDefinition cube_r7 = neck.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(112, -4).addBox(0.0F, -1.5F, -4.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 2.9086F, -7.2391F, 0.0F, 1.1345F, -1.5708F));

        PartDefinition cube_r8 = neck.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(120, -4).addBox(0.0F, -1.5F, -4.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(120, -4).addBox(-6.0F, -1.5F, -4.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -12.4987F, -0.0546F, 1.1345F, 0.0F, 0.0F));

        PartDefinition cube_r9 = neck.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(120, -4).addBox(0.0F, -1.5F, -4.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -12.4987F, -0.0546F, 0.0F, 1.1345F, -1.5708F));

        PartDefinition cube_r10 = neck.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(120, -4).addBox(0.0F, -1.5F, -4.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -12.4987F, -0.0546F, 0.0F, 1.1345F, -1.5708F));

        PartDefinition cube_r11 = neck.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, 0.4073F, -1.4181F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(117, 0).mirror().addBox(15.0F, 0.4073F, -1.4181F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-10.5F, -5.7993F, -3.628F, 1.1345F, 0.0F, 0.0F));

        PartDefinition cube_r12 = neck.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, -0.5927F, -1.4181F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(117, 0).mirror().addBox(15.0F, -0.5927F, -1.4181F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-10.5F, -6.2677F, -2.8579F, 2.7053F, 0.0F, 0.0F));

        PartDefinition cube_r13 = neck.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, 0.4073F, -1.4181F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(117, 0).mirror().addBox(15.0F, 0.4073F, -1.4181F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-10.5F, -0.3614F, -6.1637F, 1.1345F, 0.0F, 0.0F));

        PartDefinition cube_r14 = neck.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, -0.5927F, -1.4181F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(117, 0).mirror().addBox(15.0F, -0.5927F, -1.4181F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-10.5F, -0.8299F, -5.3936F, 2.7053F, 0.0F, 0.0F));

        PartDefinition cube_r15 = neck.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(74, 49).addBox(-6.0F, -2.0F, -1.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, -5.0F, 1.1345F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -8.0F, -4.0F, 9.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
        .texOffs(0, 16).addBox(-1.0F, -10.5F, -4.5F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(14, 18).addBox(-1.0F, -8.5F, 4.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(0, 19).addBox(-1.0F, -10.5F, -5.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(108, 22).addBox(0.0F, -13.5F, -5.0F, 0.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -9.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -4.0F));

        PartDefinition right_arm = upper.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-7.0F, -8.0F, -7.0F));

        PartDefinition right_upper = right_arm.addOrReplaceChild("right_upper", CubeListBuilder.create().texOffs(48, 54).addBox(-7.5F, 3.5F, -3.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition arm_chain = right_upper.addOrReplaceChild("arm_chain", CubeListBuilder.create(), PartPose.offset(-4.1257F, 5.9357F, 3.5F));

        PartDefinition right_hand = right_upper.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -4.2195F, -14.1338F, 8.0F, 8.0F, 15.0F, new CubeDeformation(0.55F))
        .texOffs(117, 0).addBox(-8.55F, -0.2195F, -4.1338F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(117, 0).addBox(-8.55F, -0.2195F, -12.1338F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 12.0F, 0.5F, 1.0472F, 0.0F, 0.0F));

        PartDefinition cube_r16 = right_hand.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.55F, -0.2195F, -10.6338F, -1.5708F, 0.0F, 0.0F));

        PartDefinition cube_r17 = right_hand.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.55F, -0.2195F, -2.6338F, -1.5708F, 0.0F, 0.0F));

        PartDefinition r_thumb = right_hand.addOrReplaceChild("r_thumb", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -0.7195F, -11.6338F, 0.0F, 0.3491F, 0.0F));

        PartDefinition r_fingers = right_hand.addOrReplaceChild("r_fingers", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -0.2195F, -13.6338F, 0.0F, 0.7854F, 0.0F));

        PartDefinition r_back_finger = r_fingers.addOrReplaceChild("r_back_finger", CubeListBuilder.create(), PartPose.offset(0.0F, 1.9695F, -0.3662F));

        PartDefinition r_front_finger = r_fingers.addOrReplaceChild("r_front_finger", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0305F, -0.3662F));

        PartDefinition left_arm = upper.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(7.0F, -8.0F, -7.0F));

        PartDefinition left_upper = left_arm.addOrReplaceChild("left_upper", CubeListBuilder.create().texOffs(48, 54).mirror().addBox(0.5F, 3.5F, -3.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(-1.0F, 1.0F, 1.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition left_hand = left_upper.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(72, 0).mirror().addBox(-4.0F, -4.2195F, -14.1338F, 8.0F, 8.0F, 15.0F, new CubeDeformation(0.55F)).mirror(false)
        .texOffs(117, 0).mirror().addBox(4.55F, -0.2195F, -12.1338F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(117, 0).mirror().addBox(4.55F, -0.2195F, -4.1338F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, 12.0F, 0.5F, 1.0472F, 0.0F, 0.0F));

        PartDefinition cube_r18 = left_hand.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(117, 0).mirror().addBox(-2.0F, 0.0F, -1.5F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.55F, -0.2195F, -2.6338F, -1.5708F, 0.0F, 0.0F));

        PartDefinition cube_r19 = left_hand.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(117, 0).mirror().addBox(-2.0F, 0.0F, -1.5F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.55F, -0.2195F, -10.6338F, -1.5708F, 0.0F, 0.0F));

        PartDefinition l_thumb = left_hand.addOrReplaceChild("l_thumb", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, -0.7195F, -11.6338F, 0.0F, -0.3491F, 0.0F));

        PartDefinition l_fingers = left_hand.addOrReplaceChild("l_fingers", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -0.2195F, -13.6338F, 0.0F, -0.7854F, 0.0F));

        PartDefinition l_back_finger = l_fingers.addOrReplaceChild("l_back_finger", CubeListBuilder.create(), PartPose.offset(0.0F, 1.9695F, -0.3662F));

        PartDefinition l_front_finger = l_fingers.addOrReplaceChild("l_front_finger", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0305F, -0.3662F));

        PartDefinition tail = pelvis.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 1.0F));

        PartDefinition upper_tail = tail.addOrReplaceChild("upper_tail", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 1.0F));

        PartDefinition lower_tail = tail.addOrReplaceChild("lower_tail", CubeListBuilder.create(), PartPose.offset(-0.5F, 5.5F, 8.0F));

        PartDefinition right_leg = stalker.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-4.0F, -14.0F, 0.0F));

        PartDefinition right_thigh = right_leg.addOrReplaceChild("right_thigh", CubeListBuilder.create().texOffs(82, 32).addBox(-3.5F, -3.5F, -2.0F, 6.0F, 10.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -0.5F, 1.5F, -0.5236F, 0.0F, 0.0F));

        PartDefinition right_shin = right_leg.addOrReplaceChild("right_shin", CubeListBuilder.create().texOffs(108, 24).addBox(-2.5F, -0.5F, -1.5F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.0F, 7.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

        PartDefinition right_toe = right_shin.addOrReplaceChild("right_toe", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -0.75F));

        PartDefinition left_leg = stalker.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(4.0F, -14.0F, 0.0F));

        PartDefinition left_thigh = left_leg.addOrReplaceChild("left_thigh", CubeListBuilder.create().texOffs(82, 32).mirror().addBox(-2.5F, -3.5F, -2.0F, 6.0F, 10.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 1.5F, -0.5236F, 0.0F, 0.0F));

        PartDefinition left_shin = left_leg.addOrReplaceChild("left_shin", CubeListBuilder.create().texOffs(108, 24).addBox(-3.5F, -0.5F, -1.5F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.0F, 7.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

        PartDefinition shin_chain = left_shin.addOrReplaceChild("shin_chain", CubeListBuilder.create(), PartPose.offset(-0.5F, 2.0F, 2.5F));

        PartDefinition left_toe = left_shin.addOrReplaceChild("left_toe", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -0.75F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

}
