package com.Vivideru.Goety.client.render.model;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.Wolf;

public class CursedWolfArmorModel<T extends Wolf> extends WolfModel<T> {
    public CursedWolfArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-1.0F, 13.5F, -7.0F));
        head.addOrReplaceChild("real_head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.25F))
                .texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
                .texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
                .texOffs(0, 10).addBox(-0.5F, -0.001F, -5.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
        // Split the back spikes between both torso bones so they stay attached in the sitting pose.
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 14).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.25F))
                        .texOffs(58, 27).addBox(0.0F, -1.0F, 3.25F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(58, 27).addBox(0.0F, 3.0F, 3.25F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(21, 0).addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, new CubeDeformation(0.25F))
                .texOffs(58, 27).addBox(1.0F, -3.0F, 4.25F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(58, 27).addBox(1.0F, 1.0F, 4.25F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 14.0F, -3.0F, 1.5708F, 0.0F, 0.0F));
        CubeListBuilder legs = CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.25F));
        partdefinition.addOrReplaceChild("right_hind_leg", legs, PartPose.offset(-2.5F, 16.0F, 7.0F));
        partdefinition.addOrReplaceChild("left_hind_leg", legs, PartPose.offset(0.5F, 16.0F, 7.0F));
        partdefinition.addOrReplaceChild("right_front_leg", legs, PartPose.offset(-2.5F, 16.0F, -4.0F));
        partdefinition.addOrReplaceChild("left_front_leg", legs, PartPose.offset(0.5F, 16.0F, -4.0F));
        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 12.0F, 8.0F, 0.6283F, 0.0F, 0.0F));
        tail.addOrReplaceChild("real_tail", CubeListBuilder.create().texOffs(9, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
