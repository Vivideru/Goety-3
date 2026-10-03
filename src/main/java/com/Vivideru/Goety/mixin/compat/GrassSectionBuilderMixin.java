package com.Vivideru.Goety.mixin.compat;

import com.Vivideru.Goety.compat.grassiergrass.GrassierGrassCompat;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.leonardoinc22.shortgrass.client.render.GrassSectionBuilder", remap = false)
public abstract class GrassSectionBuilderMixin {

    @Redirect(method = "emitSection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"), require = 0)
    private static boolean goety$redMossIsGrass(BlockState state, Block block) {
        return GrassierGrassCompat.isGrassLike(state, block);
    }

    @Redirect(method = "emitSection", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/block/BlockColors;getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I"), require = 0)
    private static int goety$redMossTint(BlockColors colors, BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (level != null && pos != null && GrassierGrassCompat.standsOnRedMoss(level, pos)) {
            return GrassierGrassCompat.RED_MOSS_TINT;
        }
        return colors.getColor(state, level, pos, tintIndex);
    }
}
