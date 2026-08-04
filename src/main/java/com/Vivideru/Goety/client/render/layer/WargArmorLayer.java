package com.Vivideru.Goety.client.render.layer;

import com.Vivideru.Goety.client.render.model.WargArmorModel;
import com.Vivideru.Goety.client.render.model.WargModel;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.Vivideru.Goety.common.items.CursedWargArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;

public class WargArmorLayer extends RenderLayer<Warg, WargModel> {
    private final WargArmorModel armorModel;

    public WargArmorLayer(RenderLayerParent<Warg, WargModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new WargArmorModel(modelSet.bakeLayer(VivideruModelLayers.WARG_ARMOR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = warg.getBodyArmorItem();
        if (armor.getItem() instanceof CursedWargArmorItem armorItem) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.armorModel, armorItem.getTexture(), poseStack,
                    buffer, packedLight, warg, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
        }
    }
}
