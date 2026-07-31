package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.hostile.Wight;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class DreadOverlay {
    public static final LayeredDraw.Layer OVERLAY = DreadOverlay::drawOverlay;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void drawOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (minecraft.player != null){
            Player player = minecraft.player;
            Wight wight = Wight.findWight(player);
            if (wight != null) {
                ResourceLocation overlay;
                int frame = minecraft.gui.getGuiTicks() % 16;

                overlay = switch (frame) {
                    default -> Goety.location("textures/gui/dread/dread_overlay_0.png");
                    case 4, 5, 6, 7 -> Goety.location("textures/gui/dread/dread_overlay_1.png");
                    case 8, 9, 10, 11 -> Goety.location("textures/gui/dread/dread_overlay_2.png");
                    case 12, 13, 14, 15 -> Goety.location("textures/gui/dread/dread_overlay_3.png");
                };

                int screenWidth = guiGraphics.guiWidth();
                int screenHeight = guiGraphics.guiHeight();
                float distance = wight.distanceTo(player);
                if (!player.hasLineOfSight(wight)) {
                    distance *= 1.5F;
                }
                float alpha = 1.0F - (Math.min(1.0F, distance / 48.0F));
                renderOverlay(overlay, alpha, screenWidth, screenHeight);
            }
        }
    }

    public static void renderOverlay(ResourceLocation location, float alpha, int screenWidth, int screenHeight) {
        alpha = Math.clamp(alpha, 0.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        // The 1.21 immediate GUI path does not enable blending for this manual quad, so the dread texture's alpha would render as a solid black screen.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.setShaderTexture(0, location);
        // Minecraft 1.21 builds immediate GUI meshes through Tesselator#begin and uploads the finished mesh explicitly.
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(0.0F, screenHeight, -90.0F).setUv(0.0F, 1.0F);
        bufferbuilder.addVertex(screenWidth, screenHeight, -90.0F).setUv(1.0F, 1.0F);
        bufferbuilder.addVertex(screenWidth, 0.0F, -90.0F).setUv(1.0F, 0.0F);
        bufferbuilder.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
