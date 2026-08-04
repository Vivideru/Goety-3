package com.Vivideru.Goety.client.render.layer;

import com.Polarice3.Goety.client.render.model.BlackBeastModel;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Vivideru.Goety.client.render.model.CursedBlackBeastArmorModel;
import com.Vivideru.Goety.common.items.CursedBlackBeastArmorItem;
import com.Vivideru.Goety.common.items.VivideruItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;

public class CursedBlackBeastArmorLayer<T extends BlackBeast, M extends BlackBeastModel<T>> extends RenderLayer<T, M> {
    private final CursedBlackBeastArmorModel<T> armorModel;

    public CursedBlackBeastArmorLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new CursedBlackBeastArmorModel<>(modelSet.bakeLayer(VivideruModelLayers.CURSED_BLACK_BEAST_ARMOR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T blackBeast, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = blackBeast.getBodyArmorItem();
        if (VivideruItems.isVivideruBlackBeastArmor(armor) && armor.getItem() instanceof CursedBlackBeastArmorItem armorItem) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.armorModel, armorItem.getTexture(),
                    poseStack, buffer, packedLight, blackBeast, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
        }
    }
}
