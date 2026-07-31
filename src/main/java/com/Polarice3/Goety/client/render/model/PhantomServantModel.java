package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.undead.PhantomServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class PhantomServantModel<T extends PhantomServant> extends HierarchicalModel<T> {
    private static final String TAIL_BASE = "tail_base";
    private static final String TAIL_TIP = "tail_tip";
    private final ModelPart root;
    private final ModelPart leftWingBase;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingBase;
    private final ModelPart rightWingTip;
    private final ModelPart tailBase;
    private final ModelPart tailTip;

    public PhantomServantModel(ModelPart p_170788_) {
        this.root = p_170788_;
        ModelPart modelpart = p_170788_.getChild("body");
        this.tailBase = modelpart.getChild("tail_base");
        this.tailTip = this.tailBase.getChild("tail_tip");
        this.leftWingBase = modelpart.getChild("left_wing_base");
        this.leftWingTip = this.leftWingBase.getChild("left_wing_tip");
        this.rightWingBase = modelpart.getChild("right_wing_base");
        this.rightWingTip = this.rightWingBase.getChild("right_wing_tip");
    }

    public ModelPart root() {
        return this.root;
    }

    public void setupAnim(T p_170791_, float p_170792_, float p_170793_, float p_170794_, float p_170795_, float p_170796_) {
        float f = ((float)p_170791_.getUniqueFlapTickOffset() + p_170794_) * 7.448451F * ((float)Math.PI / 180F);
        float f1 = 16.0F;
        this.leftWingBase.zRot = Mth.cos(f) * f1 * ((float)Math.PI / 180F);
        this.leftWingTip.zRot = Mth.cos(f) * f1 * ((float)Math.PI / 180F);
        this.rightWingBase.zRot = -this.leftWingBase.zRot;
        this.rightWingTip.zRot = -this.leftWingTip.zRot;
        this.tailBase.xRot = -(5.0F + Mth.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
        this.tailTip.xRot = -(5.0F + Mth.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
    }
}
