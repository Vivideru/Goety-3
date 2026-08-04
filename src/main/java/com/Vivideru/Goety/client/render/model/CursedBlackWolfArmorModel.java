package com.Vivideru.Goety.client.render.model;

import com.Polarice3.Goety.client.render.model.BlackWolfModel;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CursedBlackWolfArmorModel<T extends BlackWolf> extends BlackWolfModel<T> {
    public CursedBlackWolfArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition hound = partdefinition.addOrReplaceChild("hound", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        hound.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -2.0F, 7.0F, 6.0F, 4.0F, new CubeDeformation(0.25F))
                .texOffs(16, 14).addBox(1.5F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
                .texOffs(16, 14).addBox(-3.5F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
                .texOffs(0, 10).addBox(-2.0F, -0.0156F, -5.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -8.0F, -8.0F));
        // Split the back spikes between both torso bones and compensate for the rear body's lateral offset.
        hound.addOrReplaceChild("body", CubeListBuilder.create().texOffs(58, 27).addBox(-1.0F, -1.0F, 3.25F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(58, 27).addBox(-1.0F, 3.0F, 3.25F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(16, 16).addBox(-4.0F, 0.0F, -4.0F, 6.0F, 7.0F, 7.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.0F, -9.0F, 2.0F, 1.5708F, 0.0F, 0.0F));
        hound.addOrReplaceChild("upperBody", CubeListBuilder.create().texOffs(22, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
                .texOffs(58, 27).addBox(0.0F, -7.0F, 4.25F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(58, 27).addBox(0.0F, -3.0F, 4.25F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 2.0F, 1.5708F, 0.0F, 0.0F));
        hound.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.5F, -8.0F, 7.0F));
        hound.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(1.5F, -8.0F, 7.0F));
        hound.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.5F, -8.0F, -4.0F));
        hound.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(1.5F, -8.0F, -4.0F));
        hound.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -10.0F, 8.0F, 0.9599F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
