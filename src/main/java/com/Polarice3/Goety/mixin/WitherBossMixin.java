package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.api.entities.IOwned;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean goety$servantsAreNoFriends(EntityType<?> type, TagKey<EntityType<?>> tag, Operation<Boolean> original, @Local(argsOnly = true) DamageSource source) {
        if (source.getEntity() instanceof IOwned owned && owned.getTrueOwner() != null && !owned.isHostile()) {
            return false;
        }
        return original.call(type, tag);
    }
}
