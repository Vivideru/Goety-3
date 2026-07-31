package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.client.render.BlockRenderType;
import com.Polarice3.Goety.common.blocks.entities.ResonanceCrystalBlockEntity;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.RenderBlockUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class ResonanceCrystalRenderer implements BlockEntityRenderer<ResonanceCrystalBlockEntity> {
    public ResonanceCrystalRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    public void render(ResonanceCrystalBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        if (pBlockEntity.isShowBlock()) {
            Map<BlockPos, ColorUtil> renderCubes = new HashMap<>();
            for (BlockPos blockPos : pBlockEntity.getBlockPosList()) {
                renderCubes.put(blockPos, new ColorUtil(0x678f92));
                Vec3 view = Vec3.atLowerCornerOf(pBlockEntity.getBlockPos());
                RenderBlockUtils.renderColourCubes(pMatrixStack, view, renderCubes, 1.0F, 1.0F);
                draw(pBlockEntity, pMatrixStack, pBuffer);
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ResonanceCrystalBlockEntity p_112306_) {
        return true;
    }

    /**
     * Stolem from @lothrazar's codes:<a href="https://github.com/Lothrazar/Cyclic/blob/trunk/1.20.1/src/main/java/com/lothrazar/cyclic/block/laser/RenderLaser.java">...</a>
     */
    public static void draw(ResonanceCrystalBlockEntity tile, PoseStack matrixStackIn, MultiBufferSource bufferIn) {
        for (BlockPos blockPos : tile.getBlockPosList()) {
            if (blockPos == null) {
                return;
            }
            if (tile.getLevel() == null){
                return;
            }
            if (blockPos.equals(BlockPos.ZERO)) {
                return;
            }
            matrixStackIn.pushPose();
            Matrix4f positionMatrix = matrixStackIn.last().pose();
            BlockPos tilePos = tile.getBlockPos();
            Vector3f from = new Vector3f(
                    blockPos.getX() + 0.5F - tilePos.getX(),
                    blockPos.getY() + 0.5F - tilePos.getY(),
                    blockPos.getZ() + 0.5F - tilePos.getZ());
            Vector3f to = new Vector3f(0.5F, 0.5F, 0.5F);
            VertexConsumer builder = bufferIn.getBuffer(BlockRenderType.LASER_MAIN_BEAM);
            ColorUtil colorUtil = new ColorUtil(0x678f92);
            drawDirewolfLaser(builder, positionMatrix, from, to, colorUtil.red(), colorUtil.green(), colorUtil.blue(), colorUtil.alpha(), 0.065F, tilePos);
            matrixStackIn.popPose();
        }
    }

    public static Vector3f adjustBeamToEyes(Vector3f from, Vector3f to, BlockPos tile) {
        Player player = Minecraft.getInstance().player;
        Vector3f vectP = new Vector3f((float) player.getX() - tile.getX(), (float) player.getEyeY() - tile.getY(), (float) player.getZ() - tile.getZ());
        Vector3f vectS = new Vector3f(from);
        vectS.sub(vectP);
        Vector3f vectE = new Vector3f(to);
        vectE.sub(from);
        Vector3f adjustedVec = new Vector3f(vectS);
        adjustedVec.cross(vectE);
        adjustedVec.normalize();
        return adjustedVec;
    }

    public static void drawDirewolfLaser(VertexConsumer builder, Matrix4f positionMatrix, Vector3f from, Vector3f to, float r, float g, float b, float alpha, float thickness, BlockPos tilePos) {
        final float v = 1;
        Vector3f adjustedVec = adjustBeamToEyes(from, to, tilePos);
        adjustedVec.mul(thickness);
        Vector3f p1 = new Vector3f(from);
        p1.add(adjustedVec);
        Vector3f p2 = new Vector3f(from);
        p2.sub(adjustedVec);
        Vector3f p3 = new Vector3f(to);
        p3.add(adjustedVec);
        Vector3f p4 = new Vector3f(to);
        p4.sub(adjustedVec);
        builder.addVertex(positionMatrix, p1.x(), p1.y(), p1.z())
                .setColor(r, g, b, alpha)
                .setUv(1, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880);
        builder.addVertex(positionMatrix, p3.x(), p3.y(), p3.z())
                .setColor(r, g, b, alpha)
                .setUv(1, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880);
        builder.addVertex(positionMatrix, p4.x(), p4.y(), p4.z())
                .setColor(r, g, b, alpha)
                .setUv(0, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880);
        builder.addVertex(positionMatrix, p2.x(), p2.y(), p2.z())
                .setColor(r, g, b, alpha)
                .setUv(0, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880);
    }

}
