package com.Vivideru.Goety.client.render;

import net.minecraft.world.level.block.Blocks;
import com.Vivideru.Goety.common.entities.util.HurricaneCoreSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HurricaneCoreSummonRenderer extends EntityRenderer<HurricaneCoreSummon> {
    private final BlockRenderDispatcher dispatcher;

    public HurricaneCoreSummonRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.dispatcher = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.3F;
    }

    @Override
    public void render(HurricaneCoreSummon entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState state = Blocks.HEAVY_CORE.defaultBlockState();
        float time = (float) entity.tickCount + partialTicks;
        float rise = entity.getRise(partialTicks);
        float spin = time * (2.0F + 10.0F * rise);
        float bob = Mth.sin(time * 0.25F) * 0.05F * rise;
        poseStack.pushPose();
        poseStack.translate(0.0D, bob, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        this.dispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HurricaneCoreSummon entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
