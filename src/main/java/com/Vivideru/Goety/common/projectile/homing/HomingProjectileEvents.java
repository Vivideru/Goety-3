package com.Vivideru.Goety.common.projectile.homing;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import com.Polarice3.Goety.utils.MobUtil;
import com.Vivideru.Goety.common.enchantments.VivideruEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Goety.MOD_ID)
public final class HomingProjectileEvents {
    public static final String HOMING_LEVEL_TAG = "GoetyHomingLevel";
    public static final String HOMING_TARGET_TAG = "GoetyHomingTarget";

    private HomingProjectileEvents() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide
                || !(event.getEntity() instanceof Projectile projectile)
                || projectile instanceof ScytheSlash
                || !(projectile.getOwner() instanceof LivingEntity owner)) {
            return;
        }

        ItemStack focus = findCastingFocus(owner);
        int homingLevel = focus.isEmpty()
                ? 0
                : MobUtil.getItemEnchantmentLevel(owner, focus, VivideruEnchantments.HOMING);
        if (homingLevel <= 0) {
            return;
        }

        CompoundTag data = projectile.getPersistentData();
        data.putInt(HOMING_LEVEL_TAG, Math.min(homingLevel, 3));
        data.remove(HOMING_TARGET_TAG);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        // Checking the current entity avoids scanning every entity in every server level for a small tagged subset.
        if (event.getEntity() instanceof Projectile projectile
                && !projectile.level().isClientSide
                && projectile.getPersistentData().contains(HOMING_LEVEL_TAG)) {
            HomingProjectileController.steer(projectile);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        Entity directEntity = event.getSource().getDirectEntity();
        if (directEntity instanceof Projectile projectile
                && projectile.getPersistentData().contains(HOMING_LEVEL_TAG)) {
            // Homing ends after the first successful contact so persistent or returning projectiles cannot circle-hit a target.
            clearHoming(projectile);
        }
    }

    static void clearHoming(Projectile projectile) {
        projectile.getPersistentData().remove(HOMING_LEVEL_TAG);
        projectile.getPersistentData().remove(HOMING_TARGET_TAG);
    }

    private static ItemStack findCastingFocus(LivingEntity owner) {
        if (owner.isUsingItem()) {
            ItemStack usedStack = owner.getUseItem();
            return usedStack.getItem() instanceof IWand ? IWand.getFocus(usedStack) : ItemStack.EMPTY;
        }

        // Instant spells swing their casting hand immediately before adding the projectile to the level.
        if (owner.swinging) {
            ItemStack swungStack = owner.getItemInHand(owner.swingingArm);
            return swungStack.getItem() instanceof IWand ? IWand.getFocus(swungStack) : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }
}
