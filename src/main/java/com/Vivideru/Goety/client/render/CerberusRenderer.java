package com.Vivideru.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.client.render.layer.CerberusArmorLayer;
import com.Vivideru.Goety.client.render.layer.VivideruModelLayers;
import com.Vivideru.Goety.client.render.model.CerberusModel;
import com.Vivideru.Goety.common.entities.ally.Cerberus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class CerberusRenderer extends MobRenderer<Cerberus, CerberusModel> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/black_wolf/cerberus.png");
    private static final ResourceLocation CHAINS = Goety.location("textures/entity/servants/black_wolf/cerberus_chain.png");

    public CerberusRenderer(EntityRendererProvider.Context context) {
        super(context, new CerberusModel(context.bakeLayer(VivideruModelLayers.CERBERUS)), 1.5F);
        this.addLayer(new CerberusTextureLayer(this, new CerberusModel(context.bakeLayer(VivideruModelLayers.CERBERUS)), CHAINS));
        this.addLayer(new CerberusArmorLayer(this, context.getModelSet()));
    }

    @Override
    protected void scale(Cerberus cerberus, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.0F, 2.0F, 2.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(Cerberus cerberus) {
        return TEXTURE;
    }

    private static class CerberusTextureLayer extends RenderLayer<Cerberus, CerberusModel> {
        private final CerberusModel layerModel;
        private final ResourceLocation texture;

        private CerberusTextureLayer(CerberusRenderer parent, CerberusModel layerModel, ResourceLocation texture) {
            super(parent);
            this.layerModel = layerModel;
            this.texture = texture;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Cerberus cerberus, float limbSwing,
                           float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, this.texture, poseStack, buffer,
                    packedLight, cerberus, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
        }
    }
}
