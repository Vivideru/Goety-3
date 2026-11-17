package com.Vivideru.Goety.client.render;

import com.Vivideru.Goety.common.entities.extra.ExtraServant;
import com.Vivideru.Goety.common.entities.extra.ExtraServantDefinition;
import com.Vivideru.Goety.common.entities.extra.ExtraServants;
import com.Vivideru.Goety.mixin.EntityRendererAccessor;
import com.Vivideru.Goety.mixin.EntityRenderersAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

/**
 * Draws an extra servant with the renderer of the mob it is made from. Many renderers pick their textures or models by
 * entity type (the piglin's does), so while that renderer works the servant reports the mob's type instead of its own.
 */
public class ExtraServantRenderer<T extends Entity> extends EntityRenderer<T> {
    private final EntityRenderer<Entity> parent;

    @SuppressWarnings("unchecked")
    public ExtraServantRenderer(EntityRendererProvider.Context context, ExtraServantDefinition definition) {
        super(context);
        EntityType<?> parentType = ExtraServants.parentType(definition);
        EntityRendererProvider<?> provider = parentType == null ? null : EntityRenderersAccessor.goety$getProviders().get(parentType);
        if (provider == null) {
            throw new IllegalStateException("No renderer for " + definition.parent() + ", the mob goety:" + definition.id() + " is made from");
        }
        this.parent = (EntityRenderer<Entity>) provider.create(context);
        this.shadowRadius = ((EntityRendererAccessor) this.parent).goety$getShadowRadius();
        this.shadowStrength = ((EntityRendererAccessor) this.parent).goety$getShadowStrength();
    }

    /**
     * The renderer of a servant, or an empty one when its mob is missing (its mod is not installed): such a servant can never
     * be made anyway, and the game would not start if its renderer failed.
     */
    public static EntityRenderer<Entity> create(EntityRendererProvider.Context context, ExtraServantDefinition definition) {
        EntityType<?> parentType = ExtraServants.parentType(definition);
        if (parentType == null || !EntityRenderersAccessor.goety$getProviders().containsKey(parentType)) {
            return new NoopRenderer<>(context);
        }
        return new ExtraServantRenderer<>(context, definition);
    }

    private <R> R asParent(T entity, Supplier<R> action) {
        if (!(entity instanceof ExtraServant servant)) {
            return action.get();
        }
        servant.goety$setRenderingAsParent(true);
        try {
            return action.get();
        } finally {
            servant.goety$setRenderingAsParent(false);
        }
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.asParent(entity, () -> {
            this.parent.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return null;
        });
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        ResourceLocation texture = ExtraServants.texture(entity);
        return texture != null ? texture : this.asParent(entity, () -> this.parent.getTextureLocation(entity));
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        return this.asParent(entity, () -> this.parent.shouldRender(entity, frustum, x, y, z));
    }

    @Override
    public Vec3 getRenderOffset(T entity, float partialTicks) {
        return this.asParent(entity, () -> this.parent.getRenderOffset(entity, partialTicks));
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return this.asParent(entity, () -> ((EntityRendererAccessor) this.parent).goety$getBlockLightLevel(entity, pos));
    }

    @Override
    protected int getSkyLightLevel(T entity, BlockPos pos) {
        return this.asParent(entity, () -> ((EntityRendererAccessor) this.parent).goety$getSkyLightLevel(entity, pos));
    }
}
