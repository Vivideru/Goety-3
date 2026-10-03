package com.Vivideru.Goety.client.render.model;

import com.Vivideru.Goety.client.render.animation.HurricaneAnimations;
import com.Vivideru.Goety.common.entities.neutral.AbstractHurricane;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class HurricaneModel<T extends AbstractHurricane> extends HierarchicalModel<T> {
	private static final float FLOAT_AMPLITUDE = 1.0F;
	private static final float FLOAT_SPEED = 0.1F;
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart heavyCore;
	private final ModelPart bodyTop;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightForearm;
	private final ModelPart leftForearm;
	private final ModelPart head;
	private final ModelPart group2;
	private final ModelPart rods;
	private float alpha = 1.0F;

	public HurricaneModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
		this.body = root.getChild("body");
		this.heavyCore = this.body.getChild("heavy_core");
		this.bodyTop = this.body.getChild("body_top");
		this.rightArm = this.bodyTop.getChild("right_arm");
		this.leftArm = this.bodyTop.getChild("left_arm");
		this.rightForearm = this.rightArm.getChild("bone2");
		this.leftForearm = this.leftArm.getChild("bone3");
		this.head = this.bodyTop.getChild("head");
		this.group2 = this.body.getChild("group2");
		this.rods = this.group2.getChild("rods");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

		body.addOrReplaceChild("heavy_core", CubeListBuilder.create().texOffs(0, 78).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-1.0F)), PartPose.offset(0.0F, -24.0F, 1.0F));

		PartDefinition body_top = body.addOrReplaceChild("body_top", CubeListBuilder.create().texOffs(64, 41).addBox(-7.0F, -2.0F, -6.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.5F))
		.texOffs(40, 0).addBox(-7.0F, -2.0F, -6.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -33.0F, 0.0F));

		PartDefinition right_arm = body_top.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 62).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(38, 26).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, -2.0F, 1.0F));

		right_arm.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(32, 59).addBox(-5.0F, -5.0F, -8.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-5.5F, -5.5F, -11.0F, 11.0F, 11.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition left_arm = body_top.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(38, 26).mirror().addBox(-5.0F, -6.0F, -5.0F, 10.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 62).mirror().addBox(-4.0F, -3.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(14.0F, -2.0F, 1.0F));

		left_arm.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(32, 59).mirror().addBox(-5.0F, -5.0F, -8.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 32).mirror().addBox(-5.5F, -5.5F, -11.0F, 11.0F, 11.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition head = body_top.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

		head.addOrReplaceChild("right_eyebrow", CubeListBuilder.create().texOffs(16, 24).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.25F, -4.0F, -2.75F));

		head.addOrReplaceChild("left_eyebrow", CubeListBuilder.create().texOffs(16, 24).mirror().addBox(-2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(3.25F, -4.0F, -2.75F));

		PartDefinition group2 = body.addOrReplaceChild("group2", CubeListBuilder.create().texOffs(72, 65).addBox(-5.0F, -6.0F, -4.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(78, 24).addBox(-5.0F, -6.0F, -4.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -16.0F, 0.0F));

		PartDefinition rods = group2.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.75F, 10.75F, 1.5F));

		rods.addOrReplaceChild("rod_1", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6645F, -0.22F, -6.2138F, 0.3491F, 0.0F, 0.0F));

		PartDefinition rotation_2 = rods.addOrReplaceChild("rotation_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.3613F, -0.0742F, 1.7635F, -3.1416F, 1.0472F, -3.1416F));

		rotation_2.addOrReplaceChild("rod_2", CubeListBuilder.create().texOffs(0, 20).addBox(-4.3032F, -8.1974F, -3.6343F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition rotation_3 = rods.addOrReplaceChild("rotation_3", CubeListBuilder.create(), PartPose.offsetAndRotation(5.0258F, 0.2942F, 4.4503F, -3.1416F, -1.0472F, 3.1416F));

		rotation_3.addOrReplaceChild("rod_3", CubeListBuilder.create().texOffs(0, 20).addBox(-1.6903F, -8.5657F, -1.3212F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.alpha = entity.getDeathFade(ageInTicks - Mth.floor(ageInTicks));
		float phase = ageInTicks * FLOAT_SPEED;
		this.body.y += Mth.sin(phase) * FLOAT_AMPLITUDE;
		this.head.y += Mth.sin(phase - 0.6F) * 0.35F;
		if (!entity.isDeadOrDying()) {
			this.head.yRot = Mth.clamp(netHeadYaw, -60.0F, 60.0F) * ((float) Math.PI / 180F);
			this.head.xRot = headPitch * ((float) Math.PI / 180F);
		}
		this.rods.yRot = ageInTicks * (float) Math.PI * 0.1F;
		if (limbSwingAmount > 0.01F) {
			this.body.zRot = Mth.cos(limbSwing * 0.6F) * 0.06F * limbSwingAmount;
			this.bodyTop.xRot += 0.08F * limbSwingAmount;
		}
		this.wobble(entity, limbSwing, limbSwingAmount, ageInTicks);
		this.animate(entity.idleAnimationState, HurricaneAnimations.IDLE, ageInTicks);
		this.animate(entity.punchAnimationState, HurricaneAnimations.PUNCH, ageInTicks);
		this.animate(entity.cycloneAnimationState, HurricaneAnimations.CREATING_CYCLONE, ageInTicks);
		this.animate(entity.stompAnimationState, HurricaneAnimations.STOMPING_TORNADO, ageInTicks);
		this.animate(entity.deathAnimationState, HurricaneAnimations.DEATH, ageInTicks);
	}

	private void wobble(T entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
		boolean busy = entity.punchAnimationState.isStarted() || entity.cycloneAnimationState.isStarted()
				|| entity.stompAnimationState.isStarted() || entity.deathAnimationState.isStarted();
		float strength = busy ? 0.25F : 1.0F;
		float t = ageInTicks * 0.09F;
		float swing = Mth.cos(limbSwing * 0.6F) * 0.35F * limbSwingAmount;

		this.rightArm.xRot += (Mth.sin(t) * 0.10F + Mth.sin(t * 1.7F) * 0.04F - swing) * strength;
		this.rightArm.zRot += (0.06F + Mth.cos(t * 0.8F) * 0.07F) * strength;
		this.leftArm.xRot += (Mth.sin(t + 2.1F) * 0.10F + Mth.sin(t * 1.5F + 0.7F) * 0.04F + swing) * strength;
		this.leftArm.zRot += (-0.06F - Mth.cos(t * 0.8F + 1.3F) * 0.07F) * strength;

		float trail = 0.5F;
		this.rightForearm.xRot += (Mth.sin(t - trail) * 0.08F - swing * 0.5F) * strength;
		this.rightForearm.zRot += Mth.cos(t * 0.8F - trail) * 0.05F * strength;
		this.leftForearm.xRot += (Mth.sin(t + 2.1F - trail) * 0.08F + swing * 0.5F) * strength;
		this.leftForearm.zRot += -Mth.cos(t * 0.8F + 1.3F - trail) * 0.05F * strength;

		this.head.zRot += Mth.sin(t * 0.9F + 0.4F) * 0.05F * strength;
		this.head.xRot += Mth.sin(t * 1.3F) * 0.03F * strength;
		this.bodyTop.zRot += Mth.sin(t * 0.7F) * 0.02F * strength;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, int color) {
		if (this.alpha < 1.0F) {
			color = FastColor.ARGB32.color((int) (FastColor.ARGB32.alpha(color) * this.alpha), FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
		}
		super.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, color);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public void showBody() {
		this.root.getAllParts().forEach(part -> part.visible = true);
		this.heavyCore.visible = false;
	}

	public void showHeavyCore() {
		this.root.getAllParts().forEach(part -> part.visible = true);
		this.bodyTop.visible = false;
		this.group2.visible = false;
	}

	public void showWind() {
		this.root.getAllParts().forEach(part -> part.visible = true);
		this.heavyCore.visible = false;
		this.head.visible = false;
		this.rods.visible = false;
	}

	public ModelPart rightArm() {
		return this.rightArm;
	}

	public ModelPart leftArm() {
		return this.leftArm;
	}

	public float alpha() {
		return this.alpha;
	}
}
