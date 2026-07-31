package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.entities.projectiles.DeathArrow;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
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
            DeathArrow arrow = new DeathArrow(level, shooter);
            // 1.21 arrows read potion effects from the pickup stack component instead of the removed setEffectsFromItem helper.
            arrow.getPickupItemStackOrigin().set(DataComponents.POTION_CONTENTS, stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
            cir.setReturnValue(arrow);
        }
    }
}
