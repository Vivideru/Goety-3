package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.blocks.entities.SealerHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Spells and other code that destroy blocks through the level leave sealed blocks alone, unless a boss breaks them. */
@Mixin(Level.class)
public abstract class LevelSealMixin {
    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;I)Z", at = @At("HEAD"), cancellable = true)
    private void goety$keepSealedBlocks(BlockPos pos, boolean dropBlock, Entity entity, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        if (SealerHooks.isSealed((Level) (Object) this, pos)) {
            if (entity == null || !entity.getType().is(Tags.EntityTypes.BOSSES)) {
                cir.setReturnValue(false);
            }
        }
    }
}
