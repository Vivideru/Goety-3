package com.Vivideru.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.client.render.layer.VivideruModelLayers;
import com.Vivideru.Goety.client.render.model.HurricanePunchModel;
import com.Vivideru.Goety.common.entities.projectiles.HurricanePunch;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HurricanePunchRenderer extends EntityRenderer<HurricanePunch> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/hurricane/hurricane.png");
    private static final ResourceLocation WIND_TEXTURE = Goety.location("textures/entity/hurricane/hurricane_wind.png");
    private final HurricanePunchModel model;

    public HurricanePunchRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new HurricanePunchModel(context.bakeLayer(VivideruModelLayers.HURRICANE_PUNCH));
    }

    @Override
    public void render(HurricanePunch entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, entity.getBbHeight() / 2.0F, 0.0F);
        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot + 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        float time = (float) entity.tickCount + partialTicks;
        this.model.setupAnim(entity, 0.0F, 0.0F, time, 0.0F, 0.0F);
        this.model.renderToBuffer(poseStack, buffer.getBuffer(this.model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.breezeWind(WIND_TEXTURE, (time * 0.02F) % 1.0F, 0.0F)), packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HurricanePunch entity) {
        return TEXTURE;
    }
}
