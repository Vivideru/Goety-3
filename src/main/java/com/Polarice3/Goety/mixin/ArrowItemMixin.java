package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.entities.projectiles.DeathArrow;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class ArrowItemMixin {

    @Inject(method = "createArrow", at = @At("RETURN"), cancellable = true)
    public void createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weapon, CallbackInfoReturnable<AbstractArrow> cir) {
        if (CuriosFinder.hasUnholySet(shooter)){
            cir.setReturnValue(new DeathArrow(level, shooter, stack.copyWithCount(1), weapon));
        }
    }
}
