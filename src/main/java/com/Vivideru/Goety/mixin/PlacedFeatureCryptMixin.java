package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.crypt.CryptExclusion;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FossilFeature;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.LargeDripstoneFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * No feature is placed from inside a crypt piece: geodes, monster rooms, springs and the like stay out of the dungeon. The
 * check runs where the feature is finally placed, after its placement modifiers have chosen the spot, not on the chunk origin.
 */
@Mixin(ConfiguredFeature.class)
public abstract class PlacedFeatureCryptMixin {
    @Inject(method = "place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void goety$keepOutOfCrypt(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Feature<?> feature = ((ConfiguredFeature<?, ?>) (Object) this).feature();
        int reach = feature instanceof GeodeFeature || feature instanceof LargeDripstoneFeature || feature instanceof DripstoneClusterFeature || feature instanceof FossilFeature ? CryptExclusion.WIDE_REACH : 0;
        if (CryptExclusion.excludes(pos, reach)) {
            cir.setReturnValue(false);
        }
    }
}
