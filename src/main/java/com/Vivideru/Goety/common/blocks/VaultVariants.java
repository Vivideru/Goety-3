package com.Vivideru.Goety.common.blocks;

import com.Vivideru.Goety.common.items.VivideruItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class VaultVariants {
    public static final String PERSISTENT_DATA = "NeoForgeData";
    public static final String VARIANT = "goety:vault_variant";
    public static final String WIND_SHRINE = "wind_shrine";

    private VaultVariants() {
    }

    public static boolean isWindShrine(BlockEntity blockEntity) {
        return blockEntity instanceof VaultBlockEntity && WIND_SHRINE.equals(blockEntity.getPersistentData().getString(VARIANT));
    }

    public static CompoundTag windShrineMarker() {
        CompoundTag data = new CompoundTag();
        data.putString(VARIANT, WIND_SHRINE);
        return data;
    }

    public static ItemStack windShrineKey(BlockState state) {
        boolean ominous = state.hasProperty(VaultBlock.OMINOUS) && state.getValue(VaultBlock.OMINOUS);
        return new ItemStack(ominous ? VivideruItems.OMINOUS_GALE_KEY.get() : VivideruItems.GALE_KEY.get());
    }
}
