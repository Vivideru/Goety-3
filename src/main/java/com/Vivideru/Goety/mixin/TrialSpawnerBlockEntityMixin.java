package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.blocks.VaultVariants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Sends the structure variant of a trial spawner to the client, so it can draw the variant's textures. */
@Mixin(TrialSpawnerBlockEntity.class)
public abstract class TrialSpawnerBlockEntityMixin extends BlockEntity {
    private TrialSpawnerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getUpdateTag", at = @At("RETURN"))
    private void goety$syncVariant(HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
        String variant = VaultVariants.variantOf(this);
        if (variant != null) {
            cir.getReturnValue().put(VaultVariants.PERSISTENT_DATA, VaultVariants.marker(variant));
        }
    }
}
