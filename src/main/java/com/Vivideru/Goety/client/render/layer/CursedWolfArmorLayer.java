package com.Vivideru.Goety.client.render.layer;

import com.Vivideru.Goety.client.render.model.CursedWolfArmorModel;
import com.Vivideru.Goety.common.items.CursedMetalWolfArmorItem;
import com.Vivideru.Goety.common.items.VivideruItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;

public class CursedWolfArmorLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
    private final CursedWolfArmorModel<Wolf> armorModel;

    public CursedWolfArmorLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new CursedWolfArmorModel<>(modelSet.bakeLayer(VivideruModelLayers.CURSED_WOLF_ARMOR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Wolf wolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = wolf.getBodyArmorItem();
        if (VivideruItems.isVivideruWolfArmor(armor) && armor.getItem() instanceof CursedMetalWolfArmorItem armorItem) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.armorModel, armorItem.getTexture(),
                    poseStack, buffer, packedLight, wolf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
        }
    }
}
