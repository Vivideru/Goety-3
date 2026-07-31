package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SkeletonWolfModel;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonWolfRenderer extends MobRenderer<SkeletonWolf, SkeletonWolfModel<SkeletonWolf>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/skeleton/skeleton_wolf.png");
    private static final ResourceLocation HOSTILE = Goety.location("textures/entity/servants/skeleton/skeleton_wolf_angry.png");

    public SkeletonWolfRenderer(EntityRendererProvider.Context p_174452_) {
        super(p_174452_, new SkeletonWolfModel<>(p_174452_.bakeLayer(ModelLayers.WOLF)), 0.5F);
        this.addLayer(new WolfCollarLayer(this));
    }

    protected float getBob(SkeletonWolf p_116528_, float p_116529_) {
        return p_116528_.getTailAngle();
    }

    public void render(SkeletonWolf p_116531_, float p_116532_, float p_116533_, PoseStack p_116534_, MultiBufferSource p_116535_, int p_116536_) {
        if (p_116531_.isWet()) {
            float f = p_116531_.getWetShade(p_116533_);
            this.model.setColor(f, f, f);
        }

        super.render(p_116531_, p_116532_, p_116533_, p_116534_, p_116535_, p_116536_);
        if (p_116531_.isWet()) {
            this.model.setColor(1.0F, 1.0F, 1.0F);
        }

    }

    public ResourceLocation getTextureLocation(SkeletonWolf p_116526_) {
        if (p_116526_.isAggressive()){
            return HOSTILE;
        }
        return TEXTURES;
    }

    public static class WolfCollarLayer extends RenderLayer<SkeletonWolf, SkeletonWolfModel<SkeletonWolf>> {
        private static final ResourceLocation WOLF_COLLAR_LOCATION = ResourceLocation.parse("textures/entity/wolf/wolf_collar.png");

        public WolfCollarLayer(RenderLayerParent<SkeletonWolf, SkeletonWolfModel<SkeletonWolf>> p_117707_) {
            super(p_117707_);
        }

        public void render(PoseStack p_117720_, MultiBufferSource p_117721_, int p_117722_, SkeletonWolf p_117723_, float p_117724_, float p_117725_, float p_117726_, float p_117727_, float p_117728_, float p_117729_) {
            if (!p_117723_.isHostile() && !p_117723_.isInvisible() && p_117723_.getTrueOwner() != null) {
                renderColoredCutoutModel(this.getParentModel(), WOLF_COLLAR_LOCATION, p_117720_, p_117721_, p_117722_, p_117723_, p_117723_.getCollarColor().getTextureDiffuseColor());
            }
        }
    }
}
