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
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
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

/**
 * Wraps the vanilla vault and trial spawner models: when the block entity carries a Goety variant, every quad is
 * retextured with the variant's texture set (same file names under the variant's folder). Missing files keep the vanilla texture.
 */
public class VaultVariantModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<String> VARIANT = new ModelProperty<>();
    private static final String VAULT_PREFIX = "block/vault_";
    private static final String SPAWNER_PREFIX = "block/trial_spawner_";
    private static final int U_OFFSET = 4;
    private static final int V_OFFSET = 5;
    private final Map<String, List<BakedQuad>> retextured = new ConcurrentHashMap<>();

    public VaultVariantModel(BakedModel original) {
        super(original);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        String variant = blockEntity == null ? null : VaultVariants.variantOf(blockEntity);
        return variant == null ? modelData : modelData.derive().with(VARIANT, variant).build();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, data, renderType);
        String variant = data.get(VARIANT);
        if (variant == null || quads.isEmpty()) {
            return quads;
        }
        // A trial spawner waiting for players shares the vanilla "active" model; variants may give that state its own side texture.
        boolean waiting = state != null && state.hasProperty(TrialSpawnerBlock.STATE) && state.getValue(TrialSpawnerBlock.STATE) == TrialSpawnerState.WAITING_FOR_PLAYERS;
        return this.retextured.computeIfAbsent(variant + "|" + (waiting ? "waiting|" : "") + (side == null ? "none" : side.getName()), key -> retexture(quads, variant, waiting));
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        TextureAtlasSprite original = super.getParticleIcon(data);
        String variant = data.get(VARIANT);
        if (variant != null) {
            TextureAtlasSprite replacement = variantSprite(original, variant, false);
            if (replacement != null) {
                return replacement;
            }
        }
        return original;
    }

    private static List<BakedQuad> retexture(List<BakedQuad> quads, String variant, boolean waiting) {
        List<BakedQuad> result = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            TextureAtlasSprite from = quad.getSprite();
            TextureAtlasSprite to = variantSprite(from, variant, waiting);
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
    private static TextureAtlasSprite variantSprite(TextureAtlasSprite original, String variant, boolean waiting) {
        ResourceLocation name = original.contents().name();
        if (!name.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
            return null;
        }
        String folder = name.getPath().startsWith(VAULT_PREFIX) ? VaultVariants.vaultTextures(variant)
                : name.getPath().startsWith(SPAWNER_PREFIX) ? VaultVariants.spawnerTextures(variant) : null;
        if (folder == null) {
            return null;
        }
        String file = name.getPath().substring("block/".length());
        if (waiting && file.startsWith("trial_spawner_side_active")) {
            TextureAtlasSprite sprite = atlasSprite(Goety.location(folder + file.replace("side_active", "side_waiting")));
            if (sprite != null) {
                return sprite;
            }
        }
        return atlasSprite(Goety.location(folder + file));
    }

    @Nullable
    private static TextureAtlasSprite atlasSprite(ResourceLocation location) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(location);
        return sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation()) ? null : sprite;
    }
}
