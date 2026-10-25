package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrineBlend;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

/** Caves and canyons leave the land built around Wind Shrines whole; below the natural ground they carve as usual. */
@Mixin(WorldCarver.class)
public abstract class WorldCarverWindShrineMixin {
    @Inject(method = "carveBlock", at = @At("HEAD"), cancellable = true)
    private void goety$keepShrineLand(CarvingContext context, CarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, CarvingMask carvingMask,
                                      BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface, CallbackInfoReturnable<Boolean> cir) {
        if (WindShrineBlend.protects(pos.getX(), pos.getY(), pos.getZ())) {
            cir.setReturnValue(false);
        }
    }
}
