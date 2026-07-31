package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.OminousCharmItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raider.class)
public abstract class RaiderMixin extends PatrollingMonster {
    public RaiderMixin(EntityType<? extends PatrollingMonster> p_33046_, Level p_33047_) {
        super(p_33046_, p_33047_);
    }

    @Inject(method = "die", at = @At("TAIL"))
    private void captureBadOmenForCharm(DamageSource source, CallbackInfo info) {
        Entity entity = source.getEntity();
        // 1.21 no longer grants Bad Omen directly from Raider#die, so preserve the charm capture without cancelling raid cleanup.
        if (this.isPatrolLeader() && entity instanceof Player player) {
            ItemStack itemStack = CuriosFinder.findCurioInAll(player, ModItems.OMINOUS_CHARM.get());
            if (itemStack.is(ModItems.OMINOUS_CHARM.get())) {
                OminousCharmItem.increaseOmenLevel(itemStack, 1);
            }
        }
    }
}
