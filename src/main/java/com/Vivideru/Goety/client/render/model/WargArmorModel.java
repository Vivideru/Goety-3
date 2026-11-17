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

public class WargArmorModel extends HierarchicalModel<Warg> {
	private final ModelPart root;
	private final ModelPart head;

	public WargArmorModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("warg").getChild("upperbody").getChild("mane").getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition warg = partdefinition.addOrReplaceChild("warg", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, -1.25F));

		PartDefinition upperbody = warg.addOrReplaceChild("upperbody", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mane = upperbody.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(0, 15).addBox(-6.0F, -6.0F, -6.0F, 14.0F, 14.0F, 13.0F, new CubeDeformation(0.25F)).texOffs(43, 4).addBox(1.0F, -5.95F, 7.25F, 0.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -6.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition head = mane.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.0F, -7.0F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.5F)).texOffs(106, 50).addBox(-1.0F, -5.5F, -8.5F, 2.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -4.5F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition body = upperbody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 42).addBox(-5.0F, 1.0F, -4.0F, 10.0F, 14.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -5.0F, -2.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition leg3 = warg.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, -1.0F, -1.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(-3.5F, -3.0F, -8.5F, 0.0F, 0.0F, 0.0175F));

		PartDefinition leg4 = warg.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 67).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(3.5F, -3.0F, -8.5F));

		PartDefinition leg1 = warg.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(18, 72).mirror().addBox(-2.0F, 6.0F, 0.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-4.0F, -5.0F, 7.5F));

		PartDefinition leg2 = warg.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(18, 72).addBox(-2.0F, 6.0F, 0.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(4.0F, -5.0F, 7.5F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(41, -4).addBox(0.0F, -3.5F, -2.5F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.0F, 1.5708F, 0.0F, 0.0F));

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
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
