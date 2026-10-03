package com.Vivideru.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.CycloneModel;
import com.Vivideru.Goety.common.entities.projectiles.HurricaneCyclone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class HurricaneCycloneRenderer extends EntityRenderer<HurricaneCyclone> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/projectiles/cyclone.png");
    private final CycloneModel<HurricaneCyclone> model;

    public HurricaneCycloneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CycloneModel<>(context.bakeLayer(ModModelLayer.FIRE_TORNADO));
    }

    @Override
    public void render(HurricaneCyclone entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
        this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTicks, 0.0F, 0.0F);
        poseStack.translate(0.0D, entity.getBbHeight(), 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float size = entity.getSize();
        poseStack.scale(size, size, size);
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 0x73FFFFFF);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HurricaneCyclone entity) {
        return TEXTURES;
    }
}
