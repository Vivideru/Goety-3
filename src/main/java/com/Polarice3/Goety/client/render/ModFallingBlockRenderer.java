package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.entities.util.ModFallingBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

public class ModFallingBlockRenderer extends EntityRenderer<ModFallingBlock> {
    public ModFallingBlockRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(ModFallingBlock entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 0.5f, 0);
        if (entityIn.getMode() == ModFallingBlock.FallingBlockMode.MOBILE) {
            matrixStackIn.mulPose((new Quaternionf()).setAngleAxis(0, Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()), 0, 0));
            matrixStackIn.mulPose((new Quaternionf()).setAngleAxis(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()), 0, 0, 0));
        } else {
            matrixStackIn.translate(0, Mth.lerp(partialTicks, entityIn.prevAnimY, entityIn.animY), 0);
            matrixStackIn.translate(0, -1, 0);
        }
        matrixStackIn.translate(-0.5f, -0.5f, -0.5f);
        BlockState blockState = entityIn.getBlock();
        if (blockState != null) {
            // Boss attacks can spawn this entity before the client receives its synced block state; skip only the unsafe frame.
            dispatcher.renderSingleBlock(blockState, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        }
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ModFallingBlock p_114482_) {
        return null;
    }
}
