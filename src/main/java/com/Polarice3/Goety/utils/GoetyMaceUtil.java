package com.Polarice3.Goety.utils;

import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class GoetyMaceUtil {
    private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
    private static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;

    public static float getAttackDamageBonus(Entity target, DamageSource damageSource, float scale) {
        if (damageSource.getDirectEntity() instanceof LivingEntity attacker && MaceItem.canSmashAttack(attacker)) {
            float fallDistance = attacker.fallDistance;
            float bonus;
            if (fallDistance <= 3.0F) {
                bonus = 4.0F * fallDistance;
            } else if (fallDistance <= 8.0F) {
                bonus = 12.0F + 2.0F * (fallDistance - 3.0F);
            } else {
                bonus = 22.0F + fallDistance - 8.0F;
            }

            float scaledBonus = bonus * scale;
            return attacker.level() instanceof ServerLevel serverLevel
                    ? scaledBonus + EnchantmentHelper.modifyFallBasedDamage(serverLevel, attacker.getWeaponItem(), target, damageSource, 0.0F) * fallDistance
                    : scaledBonus;
        }
        return 0.0F;
    }

    public static boolean trySmashEffects(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof ServerPlayer serverPlayer && MaceItem.canSmashAttack(serverPlayer) && serverPlayer.level() instanceof ServerLevel serverLevel) {
            // Goety hammer classes keep their own inheritance, so the 1.21 mace landing behavior is mirrored here instead of extending MaceItem.
            if (serverPlayer.isIgnoringFallDamageFromCurrentImpulse() && serverPlayer.currentImpulseImpactPos != null) {
                if (serverPlayer.currentImpulseImpactPos.y > serverPlayer.position().y) {
                    serverPlayer.currentImpulseImpactPos = serverPlayer.position();
                }
            } else {
                serverPlayer.currentImpulseImpactPos = serverPlayer.position();
            }

            serverPlayer.setIgnoreFallDamageFromCurrentImpulse(true);
            serverPlayer.setDeltaMovement(serverPlayer.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
            if (target.onGround()) {
                serverPlayer.setSpawnExtraParticlesOnFall(true);
                SoundEvent soundEvent = serverPlayer.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), soundEvent, serverPlayer.getSoundSource(), 1.0F, 1.0F);
            } else {
                serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.MACE_SMASH_AIR, serverPlayer.getSoundSource(), 1.0F, 1.0F);
            }

            knockback(serverLevel, serverPlayer, target);
            serverPlayer.resetFallDistance();
            return true;
        }
        return false;
    }

    private static void knockback(Level level, Player player, Entity target) {
        level.levelEvent(2013, target.getOnPos(), 750);
        level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(SMASH_ATTACK_KNOCKBACK_RADIUS), knockbackPredicate(player, target))
                .forEach(livingEntity -> {
                    Vec3 vec3 = livingEntity.position().subtract(target.position());
                    double power = getKnockbackPower(player, livingEntity, vec3);
                    Vec3 push = vec3.normalize().scale(power);
                    if (power > 0.0D) {
                        livingEntity.push(push.x, SMASH_ATTACK_KNOCKBACK_POWER, push.z);
                        if (livingEntity instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                        }
                    }
                });
    }

    private static Predicate<LivingEntity> knockbackPredicate(Player player, Entity target) {
        return livingEntity -> {
            boolean tameOwnedByPlayer = livingEntity instanceof TamableAnimal tamableAnimal
                    && tamableAnimal.isTame()
                    && player.getUUID().equals(tamableAnimal.getOwnerUUID());
            return !livingEntity.isSpectator()
                    && livingEntity != player
                    && livingEntity != target
                    && !player.isAlliedTo(livingEntity)
                    && !tameOwnedByPlayer
                    && (!(livingEntity instanceof ArmorStand armorStand) || !armorStand.isMarker())
                    && target.distanceToSqr(livingEntity) <= Math.pow(SMASH_ATTACK_KNOCKBACK_RADIUS, 2.0D);
        };
    }

    private static double getKnockbackPower(Player player, LivingEntity livingEntity, Vec3 offset) {
        return (SMASH_ATTACK_KNOCKBACK_RADIUS - offset.length())
                * SMASH_ATTACK_KNOCKBACK_POWER
                * (double)(player.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD ? 2 : 1)
                * (1.0D - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }
}
