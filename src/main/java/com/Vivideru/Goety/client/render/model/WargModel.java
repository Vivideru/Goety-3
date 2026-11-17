package com.Vivideru.Goety.client.render.model;

import com.Vivideru.Goety.client.render.animation.WargAnimations;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class WargModel extends HierarchicalModel<Warg> {
	private final ModelPart root;
	private final ModelPart warg;
	private final ModelPart upperbody;
	private final ModelPart mane;
	private final ModelPart fur_2;
	private final ModelPart head;
	private final ModelPart fur_1;
	private final ModelPart item;
	private final ModelPart sword;
	private final ModelPart tail;

	public WargModel(ModelPart root) {
		this.root = root;
		this.warg = root.getChild("warg");
		this.upperbody = this.warg.getChild("upperbody");
		this.mane = this.upperbody.getChild("mane");
		this.fur_2 = this.mane.getChild("fur_2");
		this.head = this.mane.getChild("head");
		this.fur_1 = this.head.getChild("fur_1");
		this.item = this.head.getChild("item");
		this.sword = this.item.getChild("sword");
		this.tail = this.upperbody.getChild("body").getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition warg = partdefinition.addOrReplaceChild("warg", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, -1.25F));

		PartDefinition upperbody = warg.addOrReplaceChild("upperbody", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mane = upperbody.addOrReplaceChild("mane", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, -6.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition mane2 = mane.addOrReplaceChild("mane2", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -16.0F, -13.0F, 14.0F, 14.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 7.0F));

		PartDefinition fur_2 = mane.addOrReplaceChild("fur_2", CubeListBuilder.create().texOffs(63, 32).addBox(0.0F, -1.0F, -2.5F, 0.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -7.0F, 6.5F));

		PartDefinition fur_r1 = fur_2.addOrReplaceChild("fur_r1", CubeListBuilder.create().texOffs(54, 0).addBox(-1.4226F, 0.9063F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 8.0F, -5.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition fur_r2 = fur_2.addOrReplaceChild("fur_r2", CubeListBuilder.create().texOffs(54, 0).addBox(-1.4226F, 0.9063F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -5.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition fur_r3 = fur_2.addOrReplaceChild("fur_r3", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(0.4226F, 0.9063F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 8.0F, -5.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition fur_r4 = fur_2.addOrReplaceChild("fur_r4", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(0.4226F, 0.9063F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, -5.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition fur_r5 = fur_2.addOrReplaceChild("fur_r5", CubeListBuilder.create().texOffs(84, 19).addBox(-7.0F, 0.9063F, 0.4226F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -12.5F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r6 = fur_2.addOrReplaceChild("fur_r6", CubeListBuilder.create().texOffs(84, 19).addBox(-7.0F, 0.9063F, 0.4226F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -12.5F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r7 = fur_2.addOrReplaceChild("fur_r7", CubeListBuilder.create().texOffs(54, 19).addBox(-7.0F, 0.9063F, -0.9226F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r8 = fur_2.addOrReplaceChild("fur_r8", CubeListBuilder.create().texOffs(54, 19).addBox(-7.0F, 0.9063F, -1.4226F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 0.4363F, 0.0F, 0.0F));

		PartDefinition head = mane.addOrReplaceChild("head", CubeListBuilder.create().texOffs(38, 27).addBox(-5.0F, -4.0F, -7.0F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(104, 38).addBox(-5.0F, -4.0F, -2.75F, 10.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.0F, -4.5F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -3.0F, 0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, -4.4F, -4.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, 0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -4.4F, -4.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition fur_1 = head.addOrReplaceChild("fur_1", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, -7.0F));

		PartDefinition fur_r9 = fur_1.addOrReplaceChild("fur_r9", CubeListBuilder.create().texOffs(80, 26).mirror().addBox(-5.0F, -1.4226F, 0.9063F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, 4.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r10 = fur_1.addOrReplaceChild("fur_r10", CubeListBuilder.create().texOffs(80, 31).addBox(-5.0F, 0.4226F, 0.9063F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r11 = fur_1.addOrReplaceChild("fur_r11", CubeListBuilder.create().texOffs(118, 20).addBox(0.9063F, -4.0F, 0.4226F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, -1.1345F, 0.0F));

		PartDefinition fur_r12 = fur_1.addOrReplaceChild("fur_r12", CubeListBuilder.create().texOffs(118, 20).mirror().addBox(-4.9063F, -4.0F, 0.4226F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.1345F, 0.0F));

		PartDefinition bone4 = head.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(98, 41).addBox(-2.0F, 0.0F, 1.0F, 3.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 4.25F, -3.25F, 0.0436F, 0.0F, 0.0F));

		PartDefinition bone2 = head.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(112, 8).addBox(-1.5F, -0.0001F, -5.0019F, 3.0F, 1.0F, 5.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 2.5F, -6.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition bone3 = head.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(110, 0).addBox(-2.0F, -1.52F, -5.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(92, 0).addBox(-2.0F, 1.48F, -5.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -6.0F));

		PartDefinition item = head.addOrReplaceChild("item", CubeListBuilder.create(), PartPose.offset(0.25F, 2.5F, -9.0F));

		PartDefinition sword = item.addOrReplaceChild("sword", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.75F, 0.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition body = upperbody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 27).addBox(-5.0F, 1.0F, -4.0F, 10.0F, 14.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, -2.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(38, 42).addBox(-2.5F, -1.613F, -2.9617F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(38, 59).addBox(-2.5F, 10.387F, -2.9617F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition leg3 = warg.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 50).mirror().addBox(-2.0F, -1.0F, -1.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.5F, -3.0F, -8.5F, 0.0F, 0.0F, 0.0175F));

		PartDefinition leg4 = warg.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 50).addBox(-2.0F, -1.0F, -1.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -3.0F, -8.5F));

		PartDefinition leg1 = warg.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, 6.0F, 0.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(18, 50).mirror().addBox(-2.0F, -2.0F, -2.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.0F, -5.0F, 7.5F));

		PartDefinition leg2 = warg.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 67).addBox(-2.0F, 6.0F, 0.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(18, 50).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -5.0F, 7.5F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Warg entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.zRot -= netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot += headPitch * ((float)Math.PI / 180F);
		this.animate(entity.idleAnimationState, WargAnimations.idle, ageInTicks);
		this.animate(entity.walkAnimationState, WargAnimations.walk, ageInTicks, entity.getWalkAnimationScale());
		this.animate(entity.runAnimationState, WargAnimations.run, ageInTicks, entity.getRunAnimationScale());
		this.animate(entity.groundedAnimationState, WargAnimations.grounded, ageInTicks);
		this.animate(entity.jumpStartAnimationState, WargAnimations.jump_start, ageInTicks);
		this.animate(entity.jumpLoopAnimationState, WargAnimations.jump_loop, ageInTicks);
		this.animate(entity.jumpLandAnimationState, WargAnimations.jump_land, ageInTicks);
		this.animate(entity.biteAnimationState, WargAnimations.bite, ageInTicks);
		this.animate(entity.slashAnimationState, WargAnimations.sword_attack, ageInTicks);
		this.animate(entity.spinAnimationState, WargAnimations.sword_spin_attack, ageInTicks);
		this.tail.xRot += entity.getTailAngle() - 0.6981F;
		this.fur_1.visible = !entity.hasWargArmor();
		this.fur_2.visible = !entity.hasWargArmor();
		this.sword.visible = false;
	}

	/** Moves the pose to the sword bone the animations swing: the held sword is drawn there by the sword layer. */
	public void translateToSword(PoseStack poseStack) {
		this.warg.translateAndRotate(poseStack);
		this.upperbody.translateAndRotate(poseStack);
		this.mane.translateAndRotate(poseStack);
		this.head.translateAndRotate(poseStack);
		this.item.translateAndRotate(poseStack);
		this.sword.translateAndRotate(poseStack);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
