package com.Vivideru.Goety.common.blocks;

import com.Vivideru.Goety.common.items.VivideruItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Vaults and trial spawners of Goety structures keep a variant name in their persistent data ({@code NeoForgeData}):
 * the client swaps their textures for the variant's set and the vault asks for the variant's key.
 */
public final class VaultVariants {
    public static final String PERSISTENT_DATA = "NeoForgeData";
    public static final String VARIANT = "goety:vault_variant";
    public static final String WIND_SHRINE = "wind_shrine";
    public static final String CRYPT = "crypt";

    private VaultVariants() {
    }

    @Nullable
    public static String variantOf(BlockEntity blockEntity) {
        if (!(blockEntity instanceof VaultBlockEntity) && !(blockEntity instanceof TrialSpawnerBlockEntity)) {
            return null;
        }
        String variant = blockEntity.getPersistentData().getString(VARIANT);
        return variant.isEmpty() ? null : variant;
    }

    public static boolean isWindShrine(BlockEntity blockEntity) {
        return WIND_SHRINE.equals(variantOf(blockEntity));
    }

    public static CompoundTag marker(String variant) {
        CompoundTag data = new CompoundTag();
        data.putString(VARIANT, variant);
        return data;
    }

    public static CompoundTag windShrineMarker() {
        return marker(WIND_SHRINE);
    }

    /** The key a vault of the given variant accepts, or null when the variant has no key of its own. */
    @Nullable
    public static ItemStack keyFor(String variant, BlockState state) {
        boolean ominous = state.hasProperty(VaultBlock.OMINOUS) && state.getValue(VaultBlock.OMINOUS);
        return switch (variant) {
            case WIND_SHRINE -> new ItemStack(ominous ? VivideruItems.OMINOUS_GALE_KEY.get() : VivideruItems.GALE_KEY.get());
            case CRYPT -> new ItemStack(ominous ? VivideruItems.OMINOUS_CRYPT_KEY.get() : VivideruItems.CRYPT_KEY.get());
            default -> null;
        };
    }

    public static ItemStack windShrineKey(BlockState state) {
        return keyFor(WIND_SHRINE, state);
    }

    /** Texture folder (under {@code textures/}) replacing the vanilla {@code block/vault_*} textures, or null. */
    @Nullable
    public static String vaultTextures(String variant) {
        return switch (variant) {
            case WIND_SHRINE -> "block/wind_vault/";
            case CRYPT -> "block/crypt_vault/";
            default -> null;
        };
    }

    /** Texture folder replacing the vanilla {@code block/trial_spawner_*} textures, or null. */
    @Nullable
    public static String spawnerTextures(String variant) {
        return CRYPT.equals(variant) ? "block/crypt_spawner/" : null;
    }
}
