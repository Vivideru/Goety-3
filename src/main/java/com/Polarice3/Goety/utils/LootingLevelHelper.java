package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.HuntersBowItem;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootingLevelHelper {
    public static int modifyLootingLevel(LootContext context, int lootingLevel) {
        DamageSource damageSource = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.level().isClientSide || damageSource == null) {
            return lootingLevel;
        }

        return modifyLootingLevel(damageSource, livingEntity, lootingLevel);
    }

    public static int modifyLootingLevel(DamageSource damageSource, LivingEntity livingEntity, int lootingLevel) {
        if (livingEntity.level().isClientSide || damageSource == null) {
            return lootingLevel;
        }

        int modified = applyHunterBowLoot(damageSource, livingEntity, lootingLevel);
        return applyWantingLoot(damageSource, livingEntity, modified);
    }

    private static int applyHunterBowLoot(DamageSource damageSource, LivingEntity target, int lootingLevel) {
        Entity attacker = damageSource.getEntity();
        Entity direct = damageSource.getDirectEntity();
        if (attacker instanceof LivingEntity livingEntity
                && livingEntity.getMainHandItem().getItem() instanceof HuntersBowItem
                && direct instanceof AbstractArrow
                && target instanceof Animal) {
            return lootingLevel + 4;
        }
        return lootingLevel;
    }

    private static int applyWantingLoot(DamageSource damageSource, LivingEntity target, int lootingLevel) {
        Player player = null;
        Entity owner = damageSource.getEntity();
        Entity direct = damageSource.getDirectEntity();
        if (owner instanceof Player player1) {
            player = player1;
        } else if (MobUtil.getOwner(owner) instanceof Player player1) {
            player = player1;
        } else if (target.getKillCredit() instanceof Player player1) {
            player = player1;
        }
        if (player == null) {
            return lootingLevel;
        }

        ItemStack ring = CuriosFinder.findRing(player);
        if (ring.getItem() != ModItems.RING_OF_WANT.get() || !ring.isEnchanted()) {
            return lootingLevel;
        }
        int wanting = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.holder(player, ModEnchantments.WANTING), ring);
        if (wanting <= lootingLevel) {
            return lootingLevel;
        }

        if (owner == null || MobUtil.getOwner(owner) == player) {
            return wanting;
        } else if (direct != null) {
            if (direct.getType().is(ModTags.EntityTypes.WANTING_ENTITIES) || MobUtil.getOwner(direct) == player) {
                return wanting;
            }
        } else if (ModDamageSource.wantingAttacks(damageSource)) {
            return wanting;
        }
        return lootingLevel;
    }
}
