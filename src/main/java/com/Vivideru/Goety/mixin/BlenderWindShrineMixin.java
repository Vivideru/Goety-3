package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrineBlend;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Chunks near a Wind Shrine get a blender, as chunks next to old terrain do, whose density blending also builds the
 * shrine's land above the natural ground (see {@link WindShrineBlend}); terrain generators and optimisers that honour
 * Minecraft's blending follow it.
 */
@Mixin(Blender.class)
public abstract class BlenderWindShrineMixin implements WindShrineBlend.Blended {
    @Unique
    @Nullable
    private WindShrineBlend.Area goety$shrineBlend;

    @Override
    public void goety$setShrineBlend(WindShrineBlend.Area area) {
        this.goety$shrineBlend = area;
    }

    @Inject(method = "of", at = @At("RETURN"), cancellable = true)
    private static void goety$blendShrines(@Nullable WorldGenRegion region, CallbackInfoReturnable<Blender> cir) {
        if (region != null) {
            ChunkPos center = region.getCenter();
            cir.setReturnValue(WindShrineBlend.blender(cir.getReturnValue(), region.getLevel().getChunkSource().randomState(), center.getMinBlockX(), center.getMinBlockZ(), 16));
        }
    }

    @Inject(method = "blendDensity", at = @At("RETURN"), cancellable = true)
    private void goety$fillShrineLand(DensityFunction.FunctionContext context, double density, CallbackInfoReturnable<Double> cir) {
        if (this.goety$shrineBlend != null) {
            cir.setReturnValue(this.goety$shrineBlend.fill(context.blockX(), context.blockY(), context.blockZ(), cir.getReturnValue()));
        }
    }
}
