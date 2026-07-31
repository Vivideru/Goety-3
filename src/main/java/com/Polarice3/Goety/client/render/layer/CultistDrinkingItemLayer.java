package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.client.render.model.CultistModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;

public class CultistDrinkingItemLayer<T extends Cultist, M extends CultistModel<T>> extends RenderLayer<T, M> {
    private final ItemInHandRenderer itemInHandRenderer;

    public CultistDrinkingItemLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer itemInHandRenderer) {
        super(parent);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T cultist, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemStack = this.getDrinkingItem(cultist);
        if (itemStack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        this.getParentModel().getHead().translateAndRotate(poseStack);
        this.getParentModel().getNose().translateAndRotate(poseStack);
        poseStack.translate(0.0625F, 0.25F, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(140.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(10.0F));
        poseStack.translate(0.0F, -0.4F, 0.4F);
        poseStack.translate(0.0F, 0.4F, -0.4F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        this.itemInHandRenderer.renderItem(cultist, itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private ItemStack getDrinkingItem(T cultist) {
        ItemStack offhand = cultist.getOffhandItem();
        if (isDrinkItem(offhand)) {
            return offhand;
        }
        // Some cultists use the main hand so the witch-style drinking pose stays visible on clients.
        ItemStack mainHand = cultist.getMainHandItem();
        if (isDrinkItem(mainHand)) {
            return mainHand;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isDrinkItem(ItemStack stack) {
        return stack.is(Items.POTION) || stack.is(Items.MILK_BUCKET) || stack.getItem() instanceof PotionItem || stack.getItem() instanceof BrewItem;
    }
}
