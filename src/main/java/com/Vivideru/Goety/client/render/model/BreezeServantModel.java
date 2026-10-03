package com.Vivideru.Goety.client.render.model;

import com.Vivideru.Goety.common.entities.ally.BreezeServant;
import net.minecraft.client.animation.definitions.BreezeAnimation;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class BreezeServantModel<T extends BreezeServant> extends HierarchicalModel<T> {
    private static final float WIND_TOP_SPEED = 0.6F;
    private static final float WIND_MIDDLE_SPEED = 0.8F;
    private static final float WIND_BOTTOM_SPEED = 1.0F;
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart wind;
    private final ModelPart windTop;
    private final ModelPart windMid;
    private final ModelPart windBottom;
    private final ModelPart rods;

    public BreezeServantModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
        this.wind = root.getChild("wind_body");
        this.windBottom = this.wind.getChild("wind_bottom");
        this.windMid = this.windBottom.getChild("wind_mid");
        this.windTop = this.windMid.getChild("wind_top");
        this.head = root.getChild("body").getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.rods = root.getChild("body").getChild("rods");
    }

    public static LayerDefinition createBodyLayer(int textureWidth, int textureHeight) {
        return BreezeModel.createBodyLayer(textureWidth, textureHeight);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float f = ageInTicks * (float) Math.PI * -0.1F;
        this.windTop.x = Mth.cos(f) * 1.0F * WIND_TOP_SPEED;
        this.windTop.z = Mth.sin(f) * 1.0F * WIND_TOP_SPEED;
        this.windMid.x = Mth.sin(f) * 0.5F * WIND_MIDDLE_SPEED;
        this.windMid.z = Mth.cos(f) * WIND_MIDDLE_SPEED;
        this.windBottom.x = Mth.cos(f) * -0.25F * WIND_BOTTOM_SPEED;
        this.windBottom.z = Mth.sin(f) * -0.25F * WIND_BOTTOM_SPEED;
        this.head.y = 4.0F + Mth.cos(f) / 4.0F;
        this.rods.yRot = ageInTicks * (float) Math.PI * 0.1F;
        this.animate(entity.shoot, BreezeAnimation.SHOOT, ageInTicks);
        this.animate(entity.slide, BreezeAnimation.SLIDE, ageInTicks);
        this.animate(entity.slideBack, BreezeAnimation.SLIDE_BACK, ageInTicks);
        this.animate(entity.longJump, BreezeAnimation.JUMP, ageInTicks);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public ModelPart head() {
        return this.head;
    }

    public ModelPart eyes() {
        return this.eyes;
    }

    public ModelPart rods() {
        return this.rods;
    }

    public ModelPart wind() {
        return this.wind;
    }
}
