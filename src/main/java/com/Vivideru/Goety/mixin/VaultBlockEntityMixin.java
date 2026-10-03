package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.blocks.VaultVariants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VaultBlockEntity.class)
public abstract class VaultBlockEntityMixin extends BlockEntity {
    @Shadow
    private VaultConfig config;

    private VaultBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getUpdateTag", at = @At("RETURN"))
    private void goety$syncVariant(HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
        if (VaultVariants.isWindShrine(this)) {
            cir.getReturnValue().put(VaultVariants.PERSISTENT_DATA, VaultVariants.windShrineMarker());
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void goety$variantKey(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (VaultVariants.isWindShrine(this)) {
            ItemStack key = VaultVariants.windShrineKey(this.getBlockState());
            if (!ItemStack.isSameItemSameComponents(this.config.keyItem(), key)) {
                this.config = new VaultConfig(this.config.lootTable(), this.config.activationRange(), this.config.deactivationRange(), key,
                        this.config.overrideLootTableToDisplay(), this.config.playerDetector(), this.config.entitySelector());
            }
        }
    }
}
