package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrinePlacementRules;
import com.Vivideru.Goety.common.world.structures.crypt.CryptPlacementRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = JigsawPlacement.class, priority = 1100)
public abstract class JigsawRotationWindShrineMixin {
    @WrapOperation(method = "addPieces", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Rotation;getRandom(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/Rotation;"), require = 0)
    private static Rotation goety$forcedStartRotation(RandomSource random, Operation<Rotation> original) {
        return CryptPlacementRules.forcedRotationOr(WindShrinePlacementRules.forcedRotationOr(original.call(random)));
    }
}
