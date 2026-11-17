package com.Vivideru.Goety.client.render.layer;

import com.Polarice3.Goety.common.items.ModItems;
import com.Vivideru.Goety.client.render.model.WargModel;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WargSwordLayer extends RenderLayer<Warg, WargModel> {
    private static final float PIXEL = 1.0F / 16.0F;
    private final ItemInHandRenderer itemRenderer;

    public WargSwordLayer(RenderLayerParent<Warg, WargModel> parent, ItemInHandRenderer itemRenderer) {
        super(parent);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!warg.hasSword()) {
            return;
        }
        ItemStack sword = warg.getMainHandItem();
        poseStack.pushPose();
        this.getParentModel().translateToSword(poseStack);
        // The model's sword bone holds a 16x16 voxel copy of a sword sprite lying flat, pommel at its -X/-Z corner and tip at
        // the +X/+Z corner: the item sprite (pixel u along +X, v along -Z) is laid on it at full size.
        poseStack.translate(5.0F * PIXEL, -0.5F * PIXEL, 5.0F * PIXEL);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        if (sword.is(ModItems.BLADE_OF_ENDER.get())) {
            // Its model is a straight vertical blade: turned onto the sprite diagonal, grip moved onto the usual grip.
            poseStack.translate(-1.35F * PIXEL, -1.35F * PIXEL, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        }
        this.itemRenderer.renderItem(warg, sword, ItemDisplayContext.NONE, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
