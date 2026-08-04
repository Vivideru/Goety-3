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
	private final ModelPart bone;
	private final ModelPart mane;
	private final ModelPart group;

	public WargSaddleModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
		this.mane = this.bone.getChild("mane");
		this.group = this.mane.getChild("group");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		// Match the base Warg pivots so the saddle follows the same animated body transform.
		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(-1.5F, 10.5F, 2.0F));

		PartDefinition mane = bone.addOrReplaceChild("mane", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -1.5F, -5.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition group = mane.addOrReplaceChild("group", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.5F, -4.5F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(1.0F, 10.9031F, -1.1421F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Warg entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.idleAnimationState, WargAnimations.idle, ageInTicks);
		this.animate(entity.walkAnimationState, WargAnimations.walking, ageInTicks);
		this.animate(entity.groundedAnimationState, WargAnimations.grounded, ageInTicks);
		this.animate(entity.jumpAnimationState, WargAnimations.jumping, ageInTicks);
		this.animate(entity.biteAnimationState, WargAnimations.biting, ageInTicks);
		this.animate(entity.spinAnimationState, WargAnimations.sword_spin_attack, ageInTicks, 1.5F);
		this.animate(entity.slashAnimationState, WargAnimations.sword_attack, ageInTicks, 1.5F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
