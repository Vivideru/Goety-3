package com.Polarice3.Goety.client.render.item;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.client.render.model.NamelessStaffModel;
import com.Polarice3.Goety.common.items.magic.NamelessStaff;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CustomItemsRenderer extends BlockEntityWithoutLevelRenderer {
    public static int ticksExisted = 0;
    private final NamelessStaffModel<?> staffModel;
    private static final ResourceLocation NAMELESS_STAFF_TEXTURE = Goety.location("textures/item/nameless_staff_model.png");
    private static final ResourceLocation NAMELESS_STAFF_ORB_TEXTURE = Goety.location("textures/item/nameless_staff_orb.png");
    private static final ResourceLocation NAMELESS_STAFF_ORB_CENTER_TEXTURE = Goety.location("textures/item/nameless_staff_orb_center.png");
    private static final ResourceLocation NAMELESS_STAFF_ORB_COLORLESS_TEXTURE = Goety.location("textures/item/nameless_staff_orb_colorless.png");
    private static final ResourceLocation NAMELESS_STAFF_ORB_CENTER_COLORLESS_TEXTURE = Goety.location("textures/item/nameless_staff_orb_center_colorless.png");

    public CustomItemsRenderer() {
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public CustomItemsRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
        this.staffModel = new NamelessStaffModel<>(p_172551_.bakeLayer(ModModelLayer.NAMELESS_STAFF));
    }

    public static void incrementTick() {
        ++ticksExisted;
    }

    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);

        int tick;
        if (Minecraft.getInstance().player != null && !Minecraft.getInstance().isPaused()) {
            tick = Minecraft.getInstance().player.tickCount;
        } else {
            tick = ticksExisted;
        }

        if (itemStackIn.getItem() instanceof NamelessStaff staff) {
            boolean dyed = staff.hasCustomColor(itemStackIn);
            int tint = dyed ? FastColor.ARGB32.opaque(staff.getColor(itemStackIn)) : -1;
            this.staffModel.animate(tick + partialTick);
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(NAMELESS_STAFF_TEXTURE), itemStackIn.hasFoil());
            this.staffModel.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, -1);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer2 = bufferIn.getBuffer(RenderType.eyes(dyed ? NAMELESS_STAFF_ORB_COLORLESS_TEXTURE : NAMELESS_STAFF_ORB_TEXTURE));
            this.staffModel.renderToBuffer(matrixStackIn, vertexconsumer2, 15728640, OverlayTexture.NO_OVERLAY, tint);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer3 = bufferIn.getBuffer(ModRenderType.orbCenter(dyed ? NAMELESS_STAFF_ORB_CENTER_COLORLESS_TEXTURE : NAMELESS_STAFF_ORB_CENTER_TEXTURE));
            this.staffModel.renderToBuffer(matrixStackIn, vertexconsumer3, 15728640, OverlayTexture.NO_OVERLAY, tint);
            matrixStackIn.popPose();
        }
    }
}
