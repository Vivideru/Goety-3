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

public class WargSaddleModel extends HierarchicalModel<Warg> {
	private final ModelPart root;

	public WargSaddleModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition warg = partdefinition.addOrReplaceChild("warg", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, -1.25F));

		PartDefinition upperbody = warg.addOrReplaceChild("upperbody", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mane = upperbody.addOrReplaceChild("mane", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, -6.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition group = mane.addOrReplaceChild("group", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.5F, -4.5F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(1.0F, 11.9031F, -0.1421F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Warg entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
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
