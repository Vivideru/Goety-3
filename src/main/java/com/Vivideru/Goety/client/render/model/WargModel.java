package com.Vivideru.Goety.client.render.model;

import com.Vivideru.Goety.client.render.animation.WargAnimations;
import com.Vivideru.Goety.common.entities.ally.Warg;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import com.mojang.blaze3d.vertex.PoseStack;

public class WargModel extends HierarchicalModel<Warg> {
	private final ModelPart root;
	private final ModelPart bone;
	private final ModelPart mane;
	private final ModelPart mane2;
	private final ModelPart fur_2;
	private final ModelPart head;
	private final ModelPart bone3;
	private final ModelPart fur_1;
	private final ModelPart bone2;
	private final ModelPart sword;
	private final ModelPart bone4;
	private final ModelPart leg3;
	private final ModelPart leg4;
	private final ModelPart body;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart tail;

	public WargModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
		this.mane = this.bone.getChild("mane");
		this.mane2 = this.mane.getChild("mane2");
		this.fur_2 = this.mane.getChild("fur_2");
		this.head = this.mane.getChild("head");
		this.bone3 = this.head.getChild("bone3");
		this.fur_1 = this.head.getChild("fur_1");
		this.bone2 = this.head.getChild("bone2");
		this.sword = this.bone2.getChild("sword");
		this.bone4 = this.head.getChild("bone4");
		this.leg3 = this.mane.getChild("leg3");
		this.leg4 = this.mane.getChild("leg4");
		this.body = this.bone.getChild("body");
		this.leg1 = this.body.getChild("leg1");
		this.leg2 = this.body.getChild("leg2");
		this.tail = this.body.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(-1.5F, 10.5F, 2.0F));

		PartDefinition mane = bone.addOrReplaceChild("mane", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -1.5F, -5.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition mane2 = mane.addOrReplaceChild("mane2", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -17.0F, -13.0F, 14.0F, 14.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 6.0F));

		PartDefinition fur_2 = mane.addOrReplaceChild("fur_2", CubeListBuilder.create().texOffs(63, 32).addBox(0.0F, -2.0F, -2.5F, 0.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -7.0F, 5.5F));

		PartDefinition fur_r1 = fur_2.addOrReplaceChild("fur_r1", CubeListBuilder.create().texOffs(54, 0).addBox(-1.0F, 0.0F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 8.0F, -5.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition fur_r2 = fur_2.addOrReplaceChild("fur_r2", CubeListBuilder.create().texOffs(54, 0).addBox(-1.0F, 0.0F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -5.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition fur_r3 = fur_2.addOrReplaceChild("fur_r3", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(0.0F, 0.0F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 8.0F, -5.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition fur_r4 = fur_2.addOrReplaceChild("fur_r4", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(0.0F, 0.0F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, -5.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition fur_r5 = fur_2.addOrReplaceChild("fur_r5", CubeListBuilder.create().texOffs(84, 19).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -12.5F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r6 = fur_2.addOrReplaceChild("fur_r6", CubeListBuilder.create().texOffs(84, 19).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -12.5F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r7 = fur_2.addOrReplaceChild("fur_r7", CubeListBuilder.create().texOffs(54, 19).addBox(-7.0F, 0.0F, -0.5F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r8 = fur_2.addOrReplaceChild("fur_r8", CubeListBuilder.create().texOffs(54, 19).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 0.4363F, 0.0F, 0.0F));

		PartDefinition head = mane.addOrReplaceChild("head", CubeListBuilder.create().texOffs(38, 27).addBox(-5.0F, -4.0F, -7.0F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(110, 0).addBox(-2.0F, -1.02F, -11.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(92, 0).addBox(-2.0F, 1.98F, -11.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(104, 38).addBox(-5.0F, -4.0F, -2.75F, 10.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.0F, -5.5F, -1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, -4.4F, -3.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -4.4F, -3.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition bone3 = head.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(0.0F, 9.5F, 2.25F));

		PartDefinition fur_1 = head.addOrReplaceChild("fur_1", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, -6.0F));

		PartDefinition fur_r9 = fur_1.addOrReplaceChild("fur_r9", CubeListBuilder.create().texOffs(80, 26).mirror().addBox(-5.0F, -1.0F, 0.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, 4.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r10 = fur_1.addOrReplaceChild("fur_r10", CubeListBuilder.create().texOffs(80, 31).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r11 = fur_1.addOrReplaceChild("fur_r11", CubeListBuilder.create().texOffs(118, 20).addBox(0.0F, -4.0F, 0.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, -1.1345F, 0.0F));

		PartDefinition fur_r12 = fur_1.addOrReplaceChild("fur_r12", CubeListBuilder.create().texOffs(118, 20).mirror().addBox(-4.0F, -4.0F, 0.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.1345F, 0.0F));

		PartDefinition bone2 = head.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(112, 8).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.98F, -7.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition sword = bone2.addOrReplaceChild("sword", CubeListBuilder.create().texOffs(76, 124).addBox(-3.0F, -2.0F, -4.0F, 24.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone4 = head.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(98, 41).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 4.25F, -3.0F));

		PartDefinition leg3 = mane.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 50).mirror().addBox(-2.0F, -1.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -1.0F, -4.5F, -1.5708F, 0.0F, 0.0F));

		PartDefinition leg4 = mane.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 50).addBox(-2.0F, -1.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -1.0F, -4.5F, -1.5708F, 0.0F, 0.0F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 27).addBox(-5.0F, 0.0F, -4.0F, 10.0F, 14.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 0.5F, 2.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, 6.0F, -1.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(18, 50).mirror().addBox(-2.0F, -2.0F, -3.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 10.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 67).addBox(-2.0F, 6.0F, -1.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(18, 50).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 10.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(38, 42).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(38, 59).addBox(-2.5F, 10.0F, -2.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, 0.6981F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Warg entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot += netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot += headPitch * ((float)Math.PI / 180F);
		this.animate(entity.idleAnimationState, WargAnimations.idle, ageInTicks);
		this.animate(entity.walkAnimationState, WargAnimations.walking, ageInTicks);
		this.animate(entity.groundedAnimationState, WargAnimations.grounded, ageInTicks);
		this.animate(entity.jumpAnimationState, WargAnimations.jumping, ageInTicks);
		this.animate(entity.biteAnimationState, WargAnimations.biting, ageInTicks);
		// Sword attacks play faster to match their shortened server-side hit windows.
		this.animate(entity.spinAnimationState, WargAnimations.sword_spin_attack, ageInTicks, 1.5F);
		this.animate(entity.slashAnimationState, WargAnimations.sword_attack, ageInTicks, 1.5F);
		this.tail.xRot += entity.getTailAngle() - 0.6981F;
		this.fur_1.visible = !entity.hasWargArmor();
		this.fur_2.visible = !entity.hasWargArmor();
		this.sword.visible = false;
	}

	public void translateToSword(PoseStack poseStack) {
		this.bone.translateAndRotate(poseStack);
		this.mane.translateAndRotate(poseStack);
		this.head.translateAndRotate(poseStack);
		this.bone2.translateAndRotate(poseStack);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
