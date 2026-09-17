package com.Vivideru.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.client.render.layer.VivideruModelLayers;
import com.Vivideru.Goety.client.render.layer.WargArmorLayer;
import com.Vivideru.Goety.client.render.layer.WargSaddleLayer;
import com.Vivideru.Goety.client.render.layer.WargSwordLayer;
import com.Vivideru.Goety.client.render.model.WargModel;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class WargRenderer extends MobRenderer<Warg, WargModel> {
    private static final ResourceLocation BLACK = Goety.location("textures/entity/servants/black_wolf/black_warg.png");
    private static final ResourceLocation COLD = Goety.location("textures/entity/servants/black_wolf/winter_warg.png");
    private static final ResourceLocation MODERATE = Goety.location("textures/entity/servants/black_wolf/storm_warg.png");
    private static final ResourceLocation WARM = Goety.location("textures/entity/servants/black_wolf/warg_warm.png");
    private static final ResourceLocation SKELETAL = Goety.location("textures/entity/servants/black_wolf/skeletal_warg.png");
    private static final ResourceLocation GRAY = Goety.location("textures/entity/servants/black_wolf/gray_warg.png");
    private static final ResourceLocation HOSTILE = Goety.location("textures/entity/servants/black_wolf/hostile_warg.png");
    private static final ResourceLocation CHAINS = Goety.location("textures/entity/servants/black_wolf/black_warg_chain.png");

    public WargRenderer(EntityRendererProvider.Context context) {
        super(context, new WargModel(context.bakeLayer(VivideruModelLayers.WARG)), 0.75F);
        this.addLayer(new WargEyesLayer(this));
        this.addLayer(new WargTextureLayer(this, new WargModel(context.bakeLayer(VivideruModelLayers.WARG)), CHAINS));
        this.addLayer(new WargArmorLayer(this, context.getModelSet()));
        this.addLayer(new WargSaddleLayer(this, context.getModelSet()));
        this.addLayer(new WargSwordLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Warg warg) {
        if (warg.isHostile()) {
            return HOSTILE;
        }
        return switch (warg.getVariant()) {
            case COLD -> COLD;
            case MODERATE -> MODERATE;
            case WARM -> WARM;
            case SKELETAL -> SKELETAL;
            case GRAY -> GRAY;
            default -> BLACK;
        };
    }

    private static ResourceLocation getEyesTexture(Warg warg) {
        if (warg.isHostile()) {
            return HOSTILE_EYES;
        }
        return switch (warg.getVariant()) {
            case COLD -> COLD_EYES;
            case MODERATE -> MODERATE_EYES;
            case WARM -> WARM_EYES;
            case SKELETAL -> SKELETAL_EYES;
            case GRAY -> GRAY_EYES;
            default -> BLACK_EYES;
        };
    }

    private static final ResourceLocation BLACK_EYES = Goety.location("textures/entity/servants/black_wolf/black_warg_eyes.png");
    private static final ResourceLocation COLD_EYES = Goety.location("textures/entity/servants/black_wolf/winter_warg_eyes.png");
    private static final ResourceLocation MODERATE_EYES = Goety.location("textures/entity/servants/black_wolf/storm_warg_eyes.png");
    private static final ResourceLocation WARM_EYES = Goety.location("textures/entity/servants/black_wolf/warg_warm_eyes.png");
    private static final ResourceLocation SKELETAL_EYES = Goety.location("textures/entity/servants/black_wolf/skeletal_warg_eyes.png");
    private static final ResourceLocation GRAY_EYES = Goety.location("textures/entity/servants/black_wolf/gray_warg_eyes.png");
    private static final ResourceLocation HOSTILE_EYES = Goety.location("textures/entity/servants/black_wolf/hostile_warg_eyes.png");

    private static class WargTextureLayer extends RenderLayer<Warg, WargModel> {
        private final WargModel layerModel;
        private final ResourceLocation texture;

        private WargTextureLayer(WargRenderer parent, WargModel layerModel, ResourceLocation texture) {
            super(parent);
            this.layerModel = layerModel;
            this.texture = texture;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                           float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, this.texture, poseStack, buffer,
                    packedLight, warg, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
        }
    }

    /**
     * Renders the Warg's eyes on their own emissive render type (full brightness, no
     * ambient occlusion / alpha blending), matching the pattern used by the other
     * servant renderers (see BlackWolfRenderer.WolfEyesLayer). RenderLayers are always
     * rendered by LivingEntityRenderer regardless of the entity's invisibility, so this
     * keeps the eyes visible even while the Warg is invisible - unlike EyesLayer, this
     * picks the correct eyes texture per variant/hostile state instead of a single fixed one.
     */
    private static class WargEyesLayer extends RenderLayer<Warg, WargModel> {
        private WargEyesLayer(WargRenderer parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                           float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (warg.getVariant() == Warg.Variant.SKELETAL) {
                // The Skeletal Warg's empty sockets have no eyes to light up.
                return;
            }
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.eyes(getEyesTexture(warg)));
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
