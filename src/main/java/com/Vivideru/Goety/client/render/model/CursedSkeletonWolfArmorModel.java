package com.Vivideru.Goety.client.render.model;

import com.Polarice3.Goety.client.render.model.SkeletonWolfModel;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class CursedSkeletonWolfArmorModel<T extends SkeletonWolf> extends SkeletonWolfModel<T> {
    public CursedSkeletonWolfArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return CursedWolfArmorModel.createBodyLayer();
    }
}
