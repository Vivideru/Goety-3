package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Based on @TeamLapen's Werewolf Rendering codes: <a href="https://github.com/TeamLapen/Werewolves/blob/1.20/src/main/java/de/teamlapen/werewolves/client/render/WerewolfPlayerRenderer.java">...</a>
 */
public abstract class LichPlayerTestRenderer<T extends LivingEntity, E extends HumanoidModel<T>> extends LivingEntityRenderer<T, E> {

    public static Optional<String> getLichRenderer(AbstractClientPlayer player) {
        if (LichdomHelper.isLich(player)) {
            if (LichdomHelper.isInLichMode(player)) {
                return Optional.of(Goety.MOD_ID +":lich");
            }
        }
        return Optional.empty();
    }

    public LichPlayerTestRenderer(EntityRendererProvider.Context context, E model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    protected void renderHand(PoseStack stack, MultiBufferSource bufferSource, int p_117778_, T entity, ModelPart arm) {
        E model = this.getModel();
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.xRot = 0.0F;
        arm.render(stack, bufferSource.getBuffer(RenderType.entitySolid(getTextureLocation(entity))), p_117778_, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected void setupRotations(T pEntityLiving, @NotNull PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks, float pScale) {
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (pEntityLiving.isFallFlying()) {
            super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks, pScale);
        } else if (f > 0) {
            super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks, pScale);
            setupSwimRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        } else {
            super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks, pScale);
        }
    }

    protected abstract void setupSwimRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks);
}
