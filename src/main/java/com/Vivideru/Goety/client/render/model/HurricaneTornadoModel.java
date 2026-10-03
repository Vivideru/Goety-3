package com.Vivideru.Goety.client.render.model;

import com.Vivideru.Goety.common.entities.neutral.AbstractHurricane;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class HurricaneTornadoModel extends HierarchicalModel<AbstractHurricane> {
	private final ModelPart root;
	private final ModelPart wind;
	private final ModelPart windTop;
	private final ModelPart windMid;
	private final ModelPart windBottom;
	private float alpha = 1.0F;

	public HurricaneTornadoModel(ModelPart root) {
		super(RenderType::entityTranslucent);
		this.root = root;
		this.wind = root.getChild("wind_body");
		this.windBottom = this.wind.getChild("wind_bottom");
		this.windMid = this.windBottom.getChild("wind_mid");
		this.windTop = this.windMid.getChild("wind_top");
		root.getChild("body").visible = false;
	}

	public static LayerDefinition createBodyLayer() {
		return BreezeModel.createBodyLayer(128, 128);
	}

	@Override
	public void setupAnim(AbstractHurricane entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.wind.getAllParts().forEach(ModelPart::resetPose);
		this.alpha = entity.getDeathFade(ageInTicks - Mth.floor(ageInTicks));
		float f = ageInTicks * (float) Math.PI * -0.1F;
		this.windTop.x = Mth.cos(f) * 0.6F;
		this.windTop.z = Mth.sin(f) * 0.6F;
		this.windMid.x = Mth.sin(f) * 0.4F;
		this.windMid.z = Mth.cos(f) * 0.8F;
		this.windBottom.x = Mth.cos(f) * -0.25F;
		this.windBottom.z = Mth.sin(f) * -0.25F;
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
}
