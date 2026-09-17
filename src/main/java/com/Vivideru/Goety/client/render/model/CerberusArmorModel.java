package com.Vivideru.Goety.client.render.model;

import com.Vivideru.Goety.client.render.animation.CerberusAnimations;
import com.Vivideru.Goety.common.entities.ally.Cerberus;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CerberusArmorModel extends HierarchicalModel<Cerberus> {
	private final ModelPart root;
	private final ModelPart bone;
	private final ModelPart mane;
	private final ModelPart head1;
	private final ModelPart head2;
	private final ModelPart head3;
	private final ModelPart leg3;
	private final ModelPart leg4;
	private final ModelPart body;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart tail;

	public CerberusArmorModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
		this.mane = this.bone.getChild("mane");
		this.head1 = this.mane.getChild("head1");
		this.head2 = this.mane.getChild("head2");
		this.head3 = this.mane.getChild("head3");
		this.leg3 = this.mane.getChild("leg3");
		this.leg4 = this.mane.getChild("leg4");
		this.body = this.bone.getChild("body");
		this.leg1 = this.body.getChild("leg1");
		this.leg2 = this.body.getChild("leg2");
		this.tail = this.body.getChild("tail");
	}

	// Bone names below are kept identical to CerberusModel's (head1/head2/head3/body/tail/leg1-4) so the
	// same CerberusAnimations definitions drive both hierarchies in lockstep, exactly like WargArmorModel.
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(-1.5F, 10.5F, -13.0F));

		PartDefinition mane = bone.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(0, 15).addBox(-6.0F, -7.0F, -7.0F, 14.0F, 14.0F, 13.0F, new CubeDeformation(0.25F))
		.texOffs(43, 4).addBox(1.0F, -6.95F, 6.25F, 0.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -1.5F, 10.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition head1 = mane.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.0F, -7.0F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.5F))
		// Pivot z matches CerberusModel's head1 (2.0F): the export left it at 0.0F, which sank the helmet
		// by two units once the 90-degree mane rotation turned that axis into the vertical one.
		.texOffs(106, 50).addBox(-1.0F, -5.5F, -8.5F, 2.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -5.5F, 2.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(41, -4).addBox(0.0F, -3.5F, -2.5F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition head2 = mane.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -3.9F, -7.25F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.5F))
		// Same two-unit sink as head1, kept once the rotated box offsets are compensated for.
		.texOffs(106, 50).addBox(-1.25F, -5.5F, -8.6F, 2.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.75F, -2.5F, 0.1F, -1.5708F, 0.0F, -0.6109F));

		PartDefinition cube_r2 = head2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(41, -4).addBox(0.0F, 4.1F, -2.25F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -13.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition head3 = mane.addOrReplaceChild("head3", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5.0F, -3.9F, -7.25F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.5F)).mirror(false)
		.texOffs(106, 50).mirror().addBox(-0.75F, -5.5F, -8.6F, 2.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.75F, -2.5F, 0.1F, -1.5708F, 0.0F, 0.6109F));

		PartDefinition cube_r3 = head3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(41, -4).mirror().addBox(0.0F, 4.1F, -2.25F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -7.0F, -13.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition leg3 = mane.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, -1.0F, -3.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -0.5F, -4.5F, -1.5708F, 0.0F, 0.0F));

		PartDefinition leg4 = mane.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 67).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(4.5F, -0.5F, -4.5F, -1.5708F, 0.0F, 0.0F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 42).addBox(-5.0F, 0.0F, -4.0F, 10.0F, 14.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.5F, 0.5F, 17.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(18, 72).mirror().addBox(-2.0F, 6.0F, -1.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 10.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(18, 72).addBox(-2.0F, 6.0F, -1.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(4.0F, 10.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, 0.6981F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Cerberus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		// Armor uses a separate hierarchy, so it must receive the same live head tracking as the base Cerberus model.
		this.head1.yRot += netHeadYaw * ((float)Math.PI / 180F);
		this.head1.xRot += headPitch * ((float)Math.PI / 180F);
		this.animate(entity.idleAnimationState, CerberusAnimations.idle, ageInTicks);
		this.animate(entity.walkAnimationState, CerberusAnimations.walking, ageInTicks);
		this.animate(entity.groundedAnimationState, CerberusAnimations.grounded, ageInTicks);
		this.animate(entity.jumpAnimationState, CerberusAnimations.jumping, ageInTicks);
		this.animate(entity.biteAnimationState, CerberusAnimations.biting, ageInTicks);
		this.animate(entity.fireBreathAnimationState, CerberusAnimations.fire_breath, ageInTicks);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
