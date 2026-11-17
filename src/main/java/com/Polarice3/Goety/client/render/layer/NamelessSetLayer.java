package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.INamelessDyeable;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.NecroCapeModel;
import com.Polarice3.Goety.common.entities.ally.Doppelganger;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NamelessSetLayer<T extends Doppelganger> extends RenderLayer<T, PlayerModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/models/curios/nameless_cape.png");
    private static final ResourceLocation COLORLESS = Goety.location("textures/models/curios/nameless_cape_colorless.png");
    private static final ResourceLocation COLORLESS_LAYER = Goety.location("textures/models/curios/nameless_cape_colorless_layer.png");
    private final NecroCapeModel<T> crownModel;
    private final NecroCapeModel<T> capeModel;

    public NamelessSetLayer(RenderLayerParent<T, PlayerModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
        super(p_i50919_1_);
        this.crownModel = new NecroCapeModel<>(p_174555_.bakeLayer(ModModelLayer.NAMELESS_CROWN));
        this.capeModel = new NecroCapeModel<>(p_174555_.bakeLayer(ModModelLayer.NECRO_CAPE));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.isUndeadClone()) {
            int crownColor = -1;
            int capeColor = -1;
            if (entitylivingbaseIn.getTrueOwner() instanceof Player player) {
                ItemStack crown = CuriosFinder.findCurio(player, ModItems.NAMELESS_CROWN.get());
                ItemStack cape = CuriosFinder.findCurio(player, ModItems.NAMELESS_CAPE.get());
                if (crown.getItem() instanceof INamelessDyeable dyeable && dyeable.hasCustomColor(crown)) {
                    crownColor = dyeable.getColor(crown);
                }
                if (cape.getItem() instanceof INamelessDyeable dyeable && dyeable.hasCustomColor(cape)) {
                    capeColor = dyeable.getColor(cape);
                }
            }
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.crownModel, crownColor > -1 ? COLORLESS : TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.capeModel, capeColor > -1 ? COLORLESS : TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
            if (crownColor > -1) {
                coloredCutoutModelCopyLayerRender(this.getParentModel(), this.crownModel, COLORLESS_LAYER, matrixStackIn, bufferIn, LightTexture.FULL_BRIGHT, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, FastColor.ARGB32.opaque(crownColor));
            }
            if (capeColor > -1) {
                coloredCutoutModelCopyLayerRender(this.getParentModel(), this.capeModel, COLORLESS_LAYER, matrixStackIn, bufferIn, LightTexture.FULL_BRIGHT, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, FastColor.ARGB32.opaque(capeColor));
            }
        }
    }
}
