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
        poseStack.pushPose();
        this.getParentModel().translateToSword(poseStack);
        // Match the original mouth guide while compensating for the muzzle bone pitch, keeping the grip inside the jaws.
        poseStack.translate(0.3125F, -0.3125F, -0.1875F);
        SwordMount mount = getSwordMount(warg.getMainHandItem());
        if (mount.verticalOffset() != 0.0F) {
            // Apply screen-space height correction before rotation so it does not slide the grip along the blade.
            poseStack.translate(0.0F, mount.verticalOffset(), 0.0F);
        }
        // Flat swords are drawn diagonally, while authored 3D swords need a model-specific angle to share the same blade direction.
        poseStack.mulPose(Axis.ZP.rotationDegrees(mount.rotation()));
        if (mount.gripOffset() != 0.0F) {
            // Move the authored handle point onto the mouth bone so rotation occurs around the grip instead of the model center.
            poseStack.translate(0.0F, mount.gripOffset(), 0.0F);
        }
        poseStack.scale(2.5F, 2.5F, 2.5F);
        this.itemRenderer.renderItem(warg, warg.getMainHandItem(), ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    private static SwordMount getSwordMount(ItemStack sword) {
        if (sword.is(ModItems.BLADE_OF_ENDER.get())) {
            return new SwordMount(-90.0F, -0.375F, 0.25F);
        }
        return SwordMount.FLAT_SWORD;
    }

    private record SwordMount(float rotation, float gripOffset, float verticalOffset) {
        private static final SwordMount FLAT_SWORD = new SwordMount(-45.0F, 0.0F, 0.0F);
    }
}
