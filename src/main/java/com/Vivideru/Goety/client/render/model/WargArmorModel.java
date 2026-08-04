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
	private final ModelPart bone;
	private final ModelPart mane;
	private final ModelPart head;
	private final ModelPart leg3;
	private final ModelPart leg4;
	private final ModelPart body;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart tail;

	public WargArmorModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
		this.mane = this.bone.getChild("mane");
		this.head = this.mane.getChild("head");
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

		// Rebase the exported armor hierarchy onto the Warg pivots so animated neck and torso rotations remain aligned.
		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(-1.5F, 10.5F, 2.0F));

		PartDefinition mane = bone.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(0, 15).addBox(-6.0F, -7.0F, -7.0F, 14.0F, 14.0F, 13.0F, new CubeDeformation(0.25F))
		.texOffs(43, 4).addBox(1.0F, -6.95F, 6.25F, 0.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -1.5F, -5.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition head = mane.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.0F, -7.0F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.5F))
		.texOffs(106, 50).addBox(-1.0F, -5.5F, -8.5F, 2.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -5.5F, -1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(41, -4).addBox(0.0F, -3.5F, -2.5F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition leg3 = mane.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, -1.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -0.5F, -4.5F, -1.5708F, 0.0F, 0.0F));

		PartDefinition leg4 = mane.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 67).addBox(-2.0F, -1.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(4.5F, -0.5F, -4.5F, -1.5708F, 0.0F, 0.0F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 42).addBox(-5.0F, 0.0F, -4.0F, 10.0F, 14.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.5F, 0.5F, 2.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(18, 72).mirror().addBox(-2.0F, 6.0F, -1.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 10.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(18, 72).addBox(-2.0F, 6.0F, -1.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(4.0F, 10.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, 0.6981F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Warg entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		// Armor uses a separate hierarchy, so it must receive the same live head tracking as the base Warg model.
		this.head.yRot += netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot += headPitch * ((float)Math.PI / 180F);
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
