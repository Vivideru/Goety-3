package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.LichCuriosLayer;
import com.Polarice3.Goety.client.render.model.LichModeModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;

/**
 * Based and modified from @TeamLapen's Werewolf Rendering codes: <a href="https://github.com/TeamLapen/Werewolves/blob/1.20/src/main/java/de/teamlapen/werewolves/client/render/player/WerewolfPlayerBeastRenderer.java">...</a>
 */
public class LichModeTestRenderer extends LichPlayerTestRenderer<AbstractClientPlayer, LichModeModel<AbstractClientPlayer>> {
    public static ResourceLocation TEXTURE = Goety.location("textures/entity/lich.png");

    public LichModeTestRenderer(EntityRendererProvider.Context context) {
        super(context, new LichModeModel<>(context.bakeLayer(ModModelLayer.LICH)), 0.5F);
        this.addLayer(new LichCuriosLayer<>(this));
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new ArrowLayer<>(context, this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
        this.addLayer(new ParrotOnShoulderLayer<>(this, context.getModelSet()));
        this.addLayer(new BeeStingerLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer p_114482_) {
        return TEXTURE;
    }

    @Override
    protected void setupSwimRotations(AbstractClientPlayer pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        float f3 = pEntityLiving.isInWater() || pEntityLiving.isInFluidType((fluidType, height) -> pEntityLiving.canSwimInFluidType(fluidType)) ? -90.0F - pEntityLiving.getXRot() : -90.0F;
        float f4 = Mth.lerp(pEntityLiving.getSwimAmount(pPartialTicks), 0.0F, f3);
        pMatrixStack.mulPose(Axis.XP.rotationDegrees(f4));
        if (pEntityLiving.isVisuallySwimming()) {
            pMatrixStack.translate(0.0F, -1.0F, 0.3F);
        }
    }

    public static class ParrotOnShoulderLayer<T extends Player> extends RenderLayer<T, LichModeModel<T>> {
        private final ParrotModel model;

        public ParrotOnShoulderLayer(RenderLayerParent<T, LichModeModel<T>> p_174511_, EntityModelSet p_174512_) {
            super(p_174511_);
            this.model = new ParrotModel(p_174512_.bakeLayer(ModelLayers.PARROT));
        }

        public void render(PoseStack p_117307_, MultiBufferSource p_117308_, int p_117309_, T p_117310_, float p_117311_, float p_117312_, float p_117313_, float p_117314_, float p_117315_, float p_117316_) {
            this.render(p_117307_, p_117308_, p_117309_, p_117310_, p_117311_, p_117312_, p_117315_, p_117316_, true);
            this.render(p_117307_, p_117308_, p_117309_, p_117310_, p_117311_, p_117312_, p_117315_, p_117316_, false);
        }

        private void render(PoseStack p_117318_, MultiBufferSource p_117319_, int p_117320_, T p_117321_, float p_117322_, float p_117323_, float p_117324_, float p_117325_, boolean p_117326_) {
            CompoundTag compoundtag = p_117326_ ? p_117321_.getShoulderEntityLeft() : p_117321_.getShoulderEntityRight();
            EntityType.byString(compoundtag.getString("id")).filter((p_117294_) -> {
                return p_117294_ == EntityType.PARROT;
            }).ifPresent((p_262538_) -> {
                p_117318_.pushPose();
                p_117318_.translate(p_117326_ ? 0.4F : -0.4F, p_117321_.isCrouching() ? -1.3F : -1.5F, 0.0F);
                Parrot.Variant parrot$variant = Parrot.Variant.byId(compoundtag.getInt("Variant"));
                VertexConsumer vertexconsumer = p_117319_.getBuffer(this.model.renderType(ParrotRenderer.getVariantTexture(parrot$variant)));
                this.model.renderOnShoulder(p_117318_, vertexconsumer, p_117320_, OverlayTexture.NO_OVERLAY, p_117322_, p_117323_, p_117324_, p_117325_, p_117321_.tickCount);
                p_117318_.popPose();
            });
        }
    }
}
