package com.Vivideru.Goety.mixin;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MoveControl.class)
public abstract class MoveControlStrafeMixin {
    @Shadow
    @Final
    protected Mob mob;

    @Inject(method = "isWalkable", at = @At("RETURN"), cancellable = true)
    private void goety$strafeOnPartialBlocks(float relativeX, float relativeZ, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() || !this.mob.onGround()
                || !Goety.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(this.mob.getType()).getNamespace())) {
            return;
        }
        NodeEvaluator evaluator = this.mob.getNavigation().getNodeEvaluator();
        if (evaluator == null) {
            return;
        }
        PathType type = evaluator.getPathType(this.mob, BlockPos.containing(this.mob.getX() + relativeX, this.mob.getY() + 0.5D, this.mob.getZ() + relativeZ));
        if (type == PathType.WALKABLE || type != PathType.OPEN && type != PathType.BLOCKED && this.mob.getPathfindingMalus(type) == 0.0F) {
            cir.setReturnValue(true);
        }
    }
}
