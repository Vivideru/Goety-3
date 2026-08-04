package com.Vivideru.Goety.client.render.layer;

import com.Polarice3.Goety.client.render.model.SkeletonWolfModel;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Vivideru.Goety.client.render.model.CursedSkeletonWolfArmorModel;
import com.Vivideru.Goety.common.items.CursedMetalWolfArmorItem;
import com.Vivideru.Goety.common.items.VivideruItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;

public class CursedSkeletonWolfArmorLayer extends RenderLayer<SkeletonWolf, SkeletonWolfModel<SkeletonWolf>> {
    private final CursedSkeletonWolfArmorModel<SkeletonWolf> armorModel;

    public CursedSkeletonWolfArmorLayer(RenderLayerParent<SkeletonWolf, SkeletonWolfModel<SkeletonWolf>> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new CursedSkeletonWolfArmorModel<>(modelSet.bakeLayer(VivideruModelLayers.CURSED_SKELETON_WOLF_ARMOR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SkeletonWolf skeletonWolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = skeletonWolf.getBodyArmorItem();
        if (VivideruItems.isVivideruWolfArmor(armor) && armor.getItem() instanceof CursedMetalWolfArmorItem armorItem) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.armorModel, armorItem.getTexture(),
                    poseStack, buffer, packedLight, skeletonWolf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
        }
    }
}
