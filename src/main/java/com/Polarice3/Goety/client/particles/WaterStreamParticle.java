package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Optional;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

//Based on Rainbow Particle by @AlexModGuy: https://github.com/AlexModGuy/AlexsCaves/blob/main/src/main/java/com/github/alexmodguy/alexscaves/client/particle/RainbowParticle.java
public class WaterStreamParticle extends Particle {
    public static final ParticleGroup PARTICLE_GROUP = new ParticleGroup(100);
    private static final RenderType RENDER_TYPE = ModRenderType.getWaterStream(Goety.location("textures/particle/water_stream.png"));
    public int vecCount;
    public int fadeSpeed = 10;
    public int fillSpeed = 10;
    public Vec3 origin;
    public Vec3 target;

    public double totalDistance;

    public double angle;

    protected Vec3[] bakedVecs;
    public float alphaProgression;
    private float prevAlphaProgression;

    private float prevAlpha;

    public WaterStreamParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
        super(world, x, y, z, 0, 0, 0);
        this.origin = new Vec3(x, y, z);
        this.target = new Vec3(xd, yd, zd);
        this.totalDistance = this.origin.distanceTo(this.target);
        this.vecCount = 64;
        this.bakedVecs = new Vec3[this.vecCount];
        this.rebakeVecs(this.totalDistance);
        this.lifetime = (int) (this.fillSpeed + this.totalDistance * 4);
        this.gravity = 0;
        this.setSize(1.0F, 1.0F);
    }

    protected void rebakeVecs(double totalDistance) {
        Vec3 rotateZero = new Vec3(totalDistance, 0, 0);
        for (int i = 0; i < this.vecCount; i++) {
            float lifeAt = i / (float) this.vecCount;
            float ageJump = (float) Math.sin(lifeAt * Math.PI);
            this.bakedVecs[i] = rotateZero.scale(lifeAt).add(0, ageJump * Math.max(totalDistance, 1.0F), 0);
        }
        Vec3 vecForAngle = this.target.subtract(this.origin);
        this.angle = Math.atan2(vecForAngle.x, vecForAngle.z);
    }

    public boolean shouldCull() {
        return false;
    }

    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(RENDER_TYPE);
        Vec3 cameraPos = camera.getPosition();
        PoseStack posestack = new PoseStack();
        posestack.pushPose();
        posestack.translate(this.origin.x - cameraPos.x, this.origin.y - cameraPos.y, this.origin.z - cameraPos.z);
        float f = (float) this.angle;
        posestack.mulPose(Axis.YP.rotation(f  - Mth.HALF_PI));
        int j = this.getLightColor(partialTick);
        int vertIndex = 0;
        float width = this.getWidth();
        float alphaLerped = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTick;
        float alphaProgressionLerped = this.prevAlphaProgression + (this.alphaProgression - this.prevAlphaProgression) * partialTick;
        while (vertIndex < this.bakedVecs.length - 1) {
            posestack.pushPose();
            float u1 = vertIndex / (float) this.bakedVecs.length;
            float u2 = u1 + 1 / (float) this.bakedVecs.length;

            Vec3 draw1 = this.bakedVecs[vertIndex];
            Vec3 draw2 = this.bakedVecs[vertIndex + 1];
            PoseStack.Pose posestack$pose = posestack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            float alpha0 = calcAlphaForVertex(vertIndex, alphaProgressionLerped) * alphaLerped;
            float alpha1 = calcAlphaForVertex(vertIndex + 1, alphaProgressionLerped) * alphaLerped;
            vertexconsumer.addVertex(matrix4f, (float) draw1.x, (float) draw1.y, (float) draw1.z + width).setColor(1F, 1F, 1F, alpha0).setUv(u1, 1F).setOverlay(NO_OVERLAY).setLight(j).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
            vertexconsumer.addVertex(matrix4f, (float) draw2.x, (float) draw2.y, (float) draw1.z + width).setColor(1F, 1F, 1F, alpha1).setUv(u2, 1F).setOverlay(NO_OVERLAY).setLight(j).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
            vertexconsumer.addVertex(matrix4f, (float) draw2.x, (float) draw2.y, (float) draw2.z - width).setColor(1F, 1F, 1F, alpha1).setUv(u2, 0).setOverlay(NO_OVERLAY).setLight(j).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
            vertexconsumer.addVertex(matrix4f, (float) draw1.x, (float) draw1.y, (float) draw2.z - width).setColor(1F, 1F, 1F, alpha0).setUv(u1, 0).setOverlay(NO_OVERLAY).setLight(j).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
            vertIndex++;
            posestack.popPose();
        }
        multibuffersource$buffersource.endBatch();
        posestack.popPose();

    }

    protected float getWidth() {
        return 0.5F;
    }

    private float calcAlphaForVertex(int vertIndex, float alphaIn) {
        return Mth.clamp(alphaIn - vertIndex, 0, 1F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void tick() {
        super.tick();
        this.prevAlpha = this.alpha;
        this.prevAlphaProgression = this.alphaProgression;
        int left = this.lifetime - this.age;
        if (left <= this.fadeSpeed) {
            this.alpha = left / (float) this.fadeSpeed;
        } else {
            float ageClamp = Mth.clamp(this.age / ((float) this.lifetime - this.fillSpeed), 0, 1F);
            this.alphaProgression = ageClamp * this.vecCount;
        }

    }

    @Override
    public Optional<ParticleGroup> getParticleGroup() {
        return Optional.of(PARTICLE_GROUP);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        public Provider() {
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WaterStreamParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
