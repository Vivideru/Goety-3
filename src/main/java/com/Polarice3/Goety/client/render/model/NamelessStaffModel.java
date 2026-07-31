package com.Polarice3.Goety.client.render.model;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class NamelessStaffModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart root;
	private final ModelPart Staff;
	private final ModelPart handle;
	private final ModelPart group;
	private final ModelPart head;
	private final ModelPart center;
	private final ModelPart mid;
	private final ModelPart outer;

	public NamelessStaffModel(ModelPart root) {
		this.root = root;
		this.Staff = this.root.getChild("Staff");
		this.handle = this.Staff.getChild("handle");
		this.group = this.Staff.getChild("group");
		this.head = this.Staff.getChild("head");
		this.center = this.head.getChild("center");
		this.mid = this.head.getChild("mid");
		this.outer = this.head.getChild("outer");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Staff = partdefinition.addOrReplaceChild("Staff", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition handle = Staff.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -16.0F, 0.0F, 1.0F, 24.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition group = Staff.addOrReplaceChild("group", CubeListBuilder.create().texOffs(4, 0).addBox(-1.0F, -17.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = Staff.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition center = head.addOrReplaceChild("center", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-1.0F)), PartPose.offset(0.5F, -28.25F, 0.5F));

		PartDefinition mid = head.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(16, 8).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-3.75F)), PartPose.offset(0.5F, -28.25F, 0.5F));

		PartDefinition outer = head.addOrReplaceChild("outer", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-5.0F)), PartPose.offset(0.5F, -28.25F, 0.5F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	public void animate(float ageInTicks){
		this.root.getAllParts().forEach(ModelPart::resetPose);
		ageInTicks *= 0.05F;
		this.center.xRot += (float)Math.PI * ageInTicks * 2.0F;
		this.center.yRot += (float)Math.PI * ageInTicks * 2.0F;
		this.center.zRot += (float)Math.PI * ageInTicks * 2.0F;

		this.mid.xRot -= (float)Math.PI * ageInTicks * 1.5F;
		this.mid.yRot -= (float)Math.PI * ageInTicks * 1.5F;
		this.mid.zRot -= (float)Math.PI * ageInTicks * 1.5F;

		this.outer.xRot += (float)Math.PI * ageInTicks;
		this.outer.yRot += (float)Math.PI * ageInTicks;
		this.outer.zRot += (float)Math.PI * ageInTicks;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
