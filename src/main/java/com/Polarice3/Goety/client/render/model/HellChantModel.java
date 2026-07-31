package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.projectiles.HellChant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class HellChantModel<T extends HellChant> extends EntityModel<T> {
	private final ModelPart scream;
	private HellChant entity;

	public HellChantModel(ModelPart root) {
		this.scream = root.getChild("scream");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("scream", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int pPackedLight, int pOverlay, int color) {
		stack.pushPose();
		float progress = this.entity.getGrowProgress(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
		float scale = 0.6F + progress;
		stack.scale(scale, scale, scale);
		int alpha = Math.max(0, ((color >> 24) & 255) - (int)(63.75F * progress));
		this.scream.render(stack, consumer, pPackedLight, pOverlay, (alpha << 24) | (color & 0x00FFFFFF));
		stack.popPose();
	}

	@Override
	public void setupAnim(T scream, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.entity = scream;
		this.scream.xRot = (float) -Math.toRadians(Mth.lerp(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false), this.entity.xRotO, this.entity.getXRot()));
	}
}
