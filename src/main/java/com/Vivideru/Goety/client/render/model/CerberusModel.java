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

public class CerberusModel extends HierarchicalModel<Cerberus> {
	private final ModelPart root;
	private final ModelPart bone;
	private final ModelPart mane;
	private final ModelPart fur_2;
	private final ModelPart head1;
	private final ModelPart fur_1;
	private final ModelPart bone2;
	private final ModelPart bone4;
	private final ModelPart head2;
	private final ModelPart fur_3;
	private final ModelPart bone6;
	private final ModelPart bone7;
	private final ModelPart head3;
	private final ModelPart fur_4;
	private final ModelPart bone5;
	private final ModelPart bone8;
	private final ModelPart leg3;
	private final ModelPart leg4;
	private final ModelPart body;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart tail;

	public CerberusModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
		this.mane = this.bone.getChild("mane");
		this.fur_2 = this.mane.getChild("fur_2");
		this.head1 = this.mane.getChild("head1");
		this.fur_1 = this.head1.getChild("fur_1");
		this.bone2 = this.head1.getChild("bone2");
		this.bone4 = this.head1.getChild("bone4");
		this.head2 = this.mane.getChild("head2");
		this.fur_3 = this.head2.getChild("fur_3");
		this.bone6 = this.head2.getChild("bone6");
		this.bone7 = this.head2.getChild("bone7");
		this.head3 = this.mane.getChild("head3");
		this.fur_4 = this.head3.getChild("fur_4");
		this.bone5 = this.head3.getChild("bone5");
		this.bone8 = this.head3.getChild("bone8");
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

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(-1.5F, 10.5F, -13.0F));

		PartDefinition mane = bone.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -7.0F, -7.0F, 14.0F, 14.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -1.5F, 10.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition fur_2 = mane.addOrReplaceChild("fur_2", CubeListBuilder.create().texOffs(63, 32).addBox(0.0F, -2.0F, -2.5F, 0.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -7.0F, 5.5F));

		PartDefinition fur_r1 = fur_2.addOrReplaceChild("fur_r1", CubeListBuilder.create().texOffs(54, 0).addBox(-1.0F, 0.0F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 8.0F, -5.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition fur_r2 = fur_2.addOrReplaceChild("fur_r2", CubeListBuilder.create().texOffs(54, 0).addBox(-1.0F, 0.0F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -5.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition fur_r3 = fur_2.addOrReplaceChild("fur_r3", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(0.0F, 0.0F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 8.0F, -5.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition fur_r4 = fur_2.addOrReplaceChild("fur_r4", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(0.0F, 0.0F, -7.0F, 1.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, -5.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition fur_r5 = fur_2.addOrReplaceChild("fur_r5", CubeListBuilder.create().texOffs(84, 19).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -12.5F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r6 = fur_2.addOrReplaceChild("fur_r6", CubeListBuilder.create().texOffs(84, 19).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -12.5F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r7 = fur_2.addOrReplaceChild("fur_r7", CubeListBuilder.create().texOffs(54, 19).addBox(-7.0F, 0.0F, -0.5F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r8 = fur_2.addOrReplaceChild("fur_r8", CubeListBuilder.create().texOffs(54, 19).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 0.4363F, 0.0F, 0.0F));

		PartDefinition head1 = mane.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(38, 27).addBox(-5.0F, -4.0F, -7.0F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(110, 0).addBox(-2.0F, -1.02F, -11.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(92, 0).addBox(-2.0F, 1.98F, -11.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(104, 38).addBox(-5.0F, -4.0F, -2.75F, 10.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.0F, -5.5F, 2.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition head_r1 = head1.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, -4.4F, -3.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition head_r2 = head1.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -4.4F, -3.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition fur_1 = head1.addOrReplaceChild("fur_1", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, -6.0F));

		PartDefinition fur_r9 = fur_1.addOrReplaceChild("fur_r9", CubeListBuilder.create().texOffs(80, 26).mirror().addBox(-5.0F, -1.0F, 0.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, 4.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r10 = fur_1.addOrReplaceChild("fur_r10", CubeListBuilder.create().texOffs(80, 31).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r11 = fur_1.addOrReplaceChild("fur_r11", CubeListBuilder.create().texOffs(118, 20).addBox(0.0F, -4.0F, 0.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, -1.1345F, 0.0F));

		PartDefinition fur_r12 = fur_1.addOrReplaceChild("fur_r12", CubeListBuilder.create().texOffs(118, 20).mirror().addBox(-4.0F, -4.0F, 0.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.1345F, 0.0F));

		PartDefinition bone2 = head1.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(112, 8).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.98F, -7.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition bone4 = head1.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(98, 41).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 4.25F, -3.0F));

		PartDefinition head2 = mane.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(38, 27).addBox(-7.25F, -4.0F, -9.25F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(110, 0).addBox(-4.25F, -1.02F, -13.25F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(92, 0).addBox(-4.25F, 1.98F, -13.25F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(104, 38).addBox(-7.25F, -4.0F, -5.0F, 10.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-3.75F, -2.25F, 0.0F, -1.5708F, 0.0F, -0.6109F));

		PartDefinition head_r3 = head2.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.25F, -4.4F, -5.75F, 0.0F, 0.0F, 0.4363F));

		PartDefinition head_r4 = head2.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.75F, -4.4F, -5.75F, 0.0F, 0.0F, -0.4363F));

		PartDefinition head_r5 = head2.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(94, 57).addBox(0.0F, -4.0F, -1.0F, 6.0F, 8.0F, 9.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-5.25F, 0.0F, -2.25F, 0.0F, 0.6109F, 0.0F));

		PartDefinition fur_3 = head2.addOrReplaceChild("fur_3", CubeListBuilder.create(), PartPose.offset(-7.25F, 0.0F, -8.25F));

		PartDefinition fur_r13 = fur_3.addOrReplaceChild("fur_r13", CubeListBuilder.create().texOffs(80, 26).mirror().addBox(-5.0F, -1.0F, 0.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, 4.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r14 = fur_3.addOrReplaceChild("fur_r14", CubeListBuilder.create().texOffs(80, 31).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r15 = fur_3.addOrReplaceChild("fur_r15", CubeListBuilder.create().texOffs(118, 20).addBox(0.0F, -4.0F, 0.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, -1.1345F, 0.0F));

		PartDefinition fur_r16 = fur_3.addOrReplaceChild("fur_r16", CubeListBuilder.create().texOffs(118, 20).mirror().addBox(-4.0F, -4.0F, 0.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.1345F, 0.0F));

		PartDefinition bone6 = head2.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(112, 8).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.25F, 1.98F, -9.25F, 0.6109F, 0.0F, 0.0F));

		PartDefinition bone7 = head2.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(98, 41).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.75F, 4.25F, -5.25F));

		PartDefinition head3 = mane.addOrReplaceChild("head3", CubeListBuilder.create().texOffs(38, 27).mirror().addBox(-2.75F, -4.0F, -9.25F, 10.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(110, 0).mirror().addBox(0.25F, -1.02F, -13.25F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(92, 0).mirror().addBox(0.25F, 1.98F, -13.25F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(104, 38).mirror().addBox(-2.75F, -4.0F, -5.0F, 10.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(5.75F, -2.25F, 0.0F, -1.5708F, 0.0F, 0.6109F));

		PartDefinition head_r6 = head3.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -4.4F, -5.75F, 0.0F, 0.0F, -0.4363F));

		PartDefinition head_r7 = head3.addOrReplaceChild("head_r7", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.75F, -4.4F, -5.75F, 0.0F, 0.0F, 0.4363F));

		PartDefinition head_r8 = head3.addOrReplaceChild("head_r8", CubeListBuilder.create().texOffs(94, 57).mirror().addBox(-6.0F, -4.0F, -1.0F, 6.0F, 8.0F, 9.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(5.25F, 0.0F, -2.25F, 0.0F, -0.6109F, 0.0F));

		PartDefinition fur_4 = head3.addOrReplaceChild("fur_4", CubeListBuilder.create(), PartPose.offset(7.25F, 0.0F, -8.25F));

		PartDefinition fur_r17 = fur_4.addOrReplaceChild("fur_r17", CubeListBuilder.create().texOffs(80, 26).addBox(-5.0F, -1.0F, 0.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 4.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition fur_r18 = fur_4.addOrReplaceChild("fur_r18", CubeListBuilder.create().texOffs(118, 20).mirror().addBox(-4.0F, -4.0F, 0.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-10.0F, 0.0F, 0.0F, 0.0F, 1.1345F, 0.0F));

		PartDefinition fur_r19 = fur_4.addOrReplaceChild("fur_r19", CubeListBuilder.create().texOffs(118, 20).addBox(0.0F, -4.0F, 0.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.1345F, 0.0F));

		PartDefinition fur_r20 = fur_4.addOrReplaceChild("fur_r20", CubeListBuilder.create().texOffs(80, 31).mirror().addBox(-5.0F, 0.0F, 0.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, -4.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition bone5 = head3.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(112, 8).mirror().addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.25F, 1.98F, -9.25F, 0.6109F, 0.0F, 0.0F));

		PartDefinition bone8 = head3.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(98, 41).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.75F, 4.25F, -5.25F));

		PartDefinition leg3 = mane.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 50).mirror().addBox(-2.0F, -1.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -1.0F, -4.5F, -1.5708F, 0.0F, 0.0F));

		PartDefinition leg4 = mane.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 50).addBox(-2.0F, -1.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -1.0F, -4.5F, -1.5708F, 0.0F, 0.0F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 27).addBox(-5.0F, 0.0F, -4.0F, 10.0F, 14.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 0.5F, 17.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, 6.0F, -1.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(18, 50).mirror().addBox(-2.0F, -2.0F, -3.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 10.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 67).addBox(-2.0F, 6.0F, -1.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(18, 50).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 10.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(38, 42).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(38, 59).addBox(-2.5F, 10.0F, -2.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, 0.6981F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Cerberus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		// Only the center head tracks the look target directly; the two side heads keep their idle/attack keyframes.
		this.head1.yRot += netHeadYaw * ((float)Math.PI / 180F);
		this.head1.xRot += headPitch * ((float)Math.PI / 180F);
		this.animate(entity.idleAnimationState, CerberusAnimations.idle, ageInTicks);
		this.animate(entity.walkAnimationState, CerberusAnimations.walking, ageInTicks);
		this.animate(entity.groundedAnimationState, CerberusAnimations.grounded, ageInTicks);
		this.animate(entity.jumpAnimationState, CerberusAnimations.jumping, ageInTicks);
		this.animate(entity.biteAnimationState, CerberusAnimations.biting, ageInTicks);
		this.animate(entity.fireBreathAnimationState, CerberusAnimations.fire_breath, ageInTicks);
		this.tail.xRot += entity.getTailAngle() - 0.6981F;
		this.fur_1.visible = !entity.hasWargArmor();
		this.fur_2.visible = !entity.hasWargArmor();
		this.fur_3.visible = !entity.hasWargArmor();
		this.fur_4.visible = !entity.hasWargArmor();
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
