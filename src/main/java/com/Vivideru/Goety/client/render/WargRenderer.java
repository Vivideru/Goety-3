package com.Vivideru.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.client.render.layer.VivideruModelLayers;
import com.Vivideru.Goety.client.render.layer.WargArmorLayer;
import com.Vivideru.Goety.client.render.layer.WargSaddleLayer;
import com.Vivideru.Goety.client.render.layer.WargSwordLayer;
import com.Vivideru.Goety.client.render.model.WargModel;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class WargRenderer extends MobRenderer<Warg, WargModel> {
    private static final ResourceLocation BLACK = Goety.location("textures/entity/servants/black_wolf/black_warg.png");
    private static final ResourceLocation COLD = Goety.location("textures/entity/servants/black_wolf/warg_cold.png");
    private static final ResourceLocation MODERATE = Goety.location("textures/entity/servants/black_wolf/warg_moderate.png");
    private static final ResourceLocation WARM = Goety.location("textures/entity/servants/black_wolf/warg_warm.png");
    private static final ResourceLocation CHAINS = Goety.location("textures/entity/servants/black_wolf/black_warg_chain.png");

    public WargRenderer(EntityRendererProvider.Context context) {
        super(context, new WargModel(context.bakeLayer(VivideruModelLayers.WARG)), 0.75F);
        this.addLayer(new WargTextureLayer(this, new WargModel(context.bakeLayer(VivideruModelLayers.WARG)), CHAINS));
        this.addLayer(new WargArmorLayer(this, context.getModelSet()));
        this.addLayer(new WargSaddleLayer(this, context.getModelSet()));
        this.addLayer(new WargSwordLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Warg warg) {
        return switch (warg.getVariant()) {
            case COLD -> COLD;
            case MODERATE -> MODERATE;
            case WARM -> WARM;
            default -> BLACK;
        };
    }

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
}
