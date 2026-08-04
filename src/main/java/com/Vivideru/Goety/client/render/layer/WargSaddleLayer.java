package com.Vivideru.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.client.render.model.WargModel;
import com.Vivideru.Goety.client.render.model.WargSaddleModel;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class WargSaddleLayer extends RenderLayer<Warg, WargModel> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/black_wolf/warg_saddle.png");
    private final WargSaddleModel saddleModel;

    public WargSaddleLayer(RenderLayerParent<Warg, WargModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.saddleModel = new WargSaddleModel(modelSet.bakeLayer(VivideruModelLayers.WARG_SADDLE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (warg.isSaddled()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.saddleModel, TEXTURE, poseStack, buffer,
                    packedLight, warg, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
        }
    }
}
