package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrinePlacementRules;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(JigsawPlacement.class)
public abstract class JigsawRotationWindShrineMixin {
    @Redirect(method = "addPieces", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Rotation;getRandom(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/Rotation;"))
    private static Rotation goety$forcedStartRotation(RandomSource random) {
        return WindShrinePlacementRules.forcedRotationOr(Rotation.getRandom(random));
    }
}
