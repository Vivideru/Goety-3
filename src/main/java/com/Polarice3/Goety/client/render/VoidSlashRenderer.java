package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.VoidSlash;
import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class VoidSlashRenderer extends EntityRenderer<VoidSlash> {
    private static final ResourceLocation[] TEXTURES = {
            Goety.location("textures/entity/projectiles/slash/1.png"),
            Goety.location("textures/entity/projectiles/slash/2.png"),
            Goety.location("textures/entity/projectiles/slash/3.png"),
            Goety.location("textures/entity/projectiles/slash/4.png")
    };

    public VoidSlashRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(VoidSlash entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        PoseStack.Pose pose = poseStack.last();
        entity.animationTime++;
        float oldWith = (float) entity.oldBB.getXsize();
        float width = entity.getBbWidth();
        width = oldWith + (width - oldWith) * Math.min(partialTicks, 1);

        drawSlash(pose,entity, bufferSource, light, width, 0);

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    private void drawSlash(PoseStack.Pose pose, VoidSlash entity, MultiBufferSource bufferSource, int light, float width, int offset) {
        Matrix4f poseMatrix = pose.pose();
        VertexConsumer consumer = bufferSource.getBuffer(ModRenderType.wraith(getTextureLocation(entity,offset)));
        float halfWidth = width * 0.5F;

        ColorUtil colorUtil = new ColorUtil(ChatFormatting.LIGHT_PURPLE);
        // The 1.21 entity render pass can provide an unopened base cutout buffer here; the wraith RenderType is the intended glow pass and stays valid for this projectile.
        consumer.addVertex(poseMatrix, -halfWidth, -0.1F, -halfWidth).setColor(colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, halfWidth, -0.1F, -halfWidth).setColor(colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, halfWidth, -0.1F, halfWidth).setColor(colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, -halfWidth, -0.1F, halfWidth).setColor(colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidSlash entity) {
        int frame = (entity.animationTime / 4) % TEXTURES.length;
        return TEXTURES[frame];
    }

    private ResourceLocation getTextureLocation(VoidSlash entity,int offset) {
        int frame = (entity.animationTime / 6 + offset) % TEXTURES.length;
        return TEXTURES[frame];
    }
}
