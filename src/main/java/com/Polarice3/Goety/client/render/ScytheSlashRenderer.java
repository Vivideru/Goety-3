package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.TrailEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ScytheSlashRenderer extends EntityRenderer<ScytheSlash> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/slash/1.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/entity/projectiles/pointed_trail.png");

    public ScytheSlashRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(ScytheSlash entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        if (entity.tickCount < 5) {
            return;
        }

        float age = entity.tickCount + partialTicks;
        float yRot = -Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot()) + 180;
        float xRot = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        PoseStack.Pose pose = poseStack.last();
        drawSlash(pose, entity, bufferSource, light, 2);
        poseStack.popPose();

        poseStack.pushPose();
        float x = (float) (Mth.lerp(partialTicks, entity.xOld, entity.getX()));
        float y = (float) (Mth.lerp(partialTicks, entity.yOld, entity.getY()));
        float z = (float) (Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
        Matrix4f transform = new Matrix4f();
        transform.rotate(Axis.YP.rotationDegrees(yRot));
        transform.rotate(Axis.XP.rotationDegrees(xRot));
        Vector4f left = transform.transform(new Vector4f(1.0F, Mth.sin(age * 0.4F) * 0.2F, 0.0F, 1.0F));
        Vector4f right = transform.transform(new Vector4f(-1.0F, Mth.cos(age * 0.4F) * 0.2F, 0.0F, 1.0F));
        entity.leftTrail.prepareRender(new Vec3(x, y, z).add(left.x(), left.y(), left.z()), partialTicks);
        entity.rightTrail.prepareRender(new Vec3(x, y, z).add(right.x(), right.y(), right.z()), partialTicks);
        poseStack.translate(-x, -y, -z);
        ColorUtil colorUtil = new ColorUtil(ChatFormatting.AQUA);
        TrailRenderer.render(entity.leftTrail, bufferSource.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true, colorUtil.red, colorUtil.green, colorUtil.blue, 1, light);
        TrailRenderer.render(entity.rightTrail, bufferSource.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true, colorUtil.red, colorUtil.green, colorUtil.blue, 1, light);
        poseStack.popPose();
    }

    private void drawSlash(PoseStack.Pose pose, ScytheSlash entity, MultiBufferSource bufferSource, int light, float width) {
        Matrix4f poseMatrix = pose.pose();

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderType.wraith(getTextureLocation(entity)));

        float halfWidth = width * 0.5F;

        ColorUtil colorUtil = new ColorUtil(ChatFormatting.AQUA);
        // The 1.21 entity render pass can provide an unopened base cutout buffer here; the wraith RenderType is the intended glow pass and stays valid for this slash.
        consumer.addVertex(poseMatrix, -halfWidth, -0.1F, -halfWidth).setColor(colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, halfWidth, -0.1F, -halfWidth).setColor(colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, halfWidth, -0.1F, halfWidth).setColor(colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, -halfWidth, -0.1F, halfWidth).setColor(colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(ScytheSlash entity) {
        return TEXTURE;
    }
}
