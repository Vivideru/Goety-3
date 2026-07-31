package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.TridentStormModel;
import com.Polarice3.Goety.common.entities.util.TridentStorm;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TridentStormRenderer extends EntityRenderer<TridentStorm> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/projectiles/trident_storm.png");
    private final TridentStormModel<TridentStorm> model;

    public TridentStormRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new TridentStormModel<>(renderManagerIn.bakeLayer(ModModelLayer.TRIDENT_STORM));
    }

    public void render(TridentStorm entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entityIn)));
        float f7 = this.getBob(entityIn, partialTicks);
        this.model.setupAnim(entityIn, 0.0F, 0.0F, f7, entityIn.getYRot(), entityIn.getXRot());
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
        matrixStackIn.translate(0.0D, 1.6D, 0.0D);
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected float getBob(TridentStorm p_115305_, float p_115306_) {
        return (float)p_115305_.tickCount + p_115306_;
    }

    @Override
    public ResourceLocation getTextureLocation(TridentStorm p_114482_) {
        return TEXTURES;
    }
}
