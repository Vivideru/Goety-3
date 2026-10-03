package com.Vivideru.Goety.client.render.model;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.blocks.VaultVariants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VaultVariantModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Boolean> WIND_SHRINE = new ModelProperty<>();
    private static final String WIND_SHRINE_TEXTURES = "block/wind_vault/";
    private static final int U_OFFSET = 4;
    private static final int V_OFFSET = 5;
    private final Map<Object, List<BakedQuad>> retextured = new ConcurrentHashMap<>();
    private static final Object NO_SIDE = new Object();

    public VaultVariantModel(BakedModel original) {
        super(original);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && VaultVariants.isWindShrine(blockEntity)) {
            return modelData.derive().with(WIND_SHRINE, true).build();
        }
        return modelData;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, data, renderType);
        if (!Boolean.TRUE.equals(data.get(WIND_SHRINE)) || quads.isEmpty()) {
            return quads;
        }
        return this.retextured.computeIfAbsent(side == null ? NO_SIDE : side, key -> retexture(quads));
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        TextureAtlasSprite original = super.getParticleIcon(data);
        if (Boolean.TRUE.equals(data.get(WIND_SHRINE))) {
            TextureAtlasSprite replacement = windShrineSprite(original);
            if (replacement != null) {
                return replacement;
            }
        }
        return original;
    }

    private static List<BakedQuad> retexture(List<BakedQuad> quads) {
        List<BakedQuad> result = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            TextureAtlasSprite from = quad.getSprite();
            TextureAtlasSprite to = windShrineSprite(from);
            if (to == null) {
                result.add(quad);
                continue;
            }
            int[] vertices = quad.getVertices().clone();
            int stride = vertices.length / 4;
            for (int vertex = 0; vertex < 4; vertex++) {
                int base = vertex * stride;
                float u = Float.intBitsToFloat(vertices[base + U_OFFSET]);
                float v = Float.intBitsToFloat(vertices[base + V_OFFSET]);
                float uRatio = (u - from.getU0()) / (from.getU1() - from.getU0());
                float vRatio = (v - from.getV0()) / (from.getV1() - from.getV0());
                vertices[base + U_OFFSET] = Float.floatToRawIntBits(to.getU0() + uRatio * (to.getU1() - to.getU0()));
                vertices[base + V_OFFSET] = Float.floatToRawIntBits(to.getV0() + vRatio * (to.getV1() - to.getV0()));
            }
            result.add(new BakedQuad(vertices, quad.getTintIndex(), quad.getDirection(), to, quad.isShade(), quad.hasAmbientOcclusion()));
        }
        return result;
    }

    @Nullable
    private static TextureAtlasSprite windShrineSprite(TextureAtlasSprite original) {
        ResourceLocation name = original.contents().name();
        if (!name.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) || !name.getPath().startsWith("block/vault_")) {
            return null;
        }
        ResourceLocation replacement = Goety.location(WIND_SHRINE_TEXTURES + name.getPath().substring("block/".length()));
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(replacement);
        return sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation()) ? null : sprite;
    }
}
