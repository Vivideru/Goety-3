package com.Vivideru.Goety.compat.apotheosis;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.ModDamageSource;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class GemCombatEffects {
    private static final String BURN_HEX_ROLL_TIME = "goety_apotheosis.BurnHexRollTime";

    private GemCombatEffects() {
    }

    @SubscribeEvent
    public static void modifyIncomingDamage(LivingIncomingDamageEvent event) {
        applyBreach(event);
        applyRoyalty(event);
    }

    private static void applyBreach(LivingIncomingDamageEvent event) {
        if (!WandSupport.isGoetySpellDamage(event.getSource())) {
            return;
        }
        Player caster = WandSupport.spellCaster(event.getSource());
        double pierce = percent(caster, GoetyApotheosisAttributes.SPELL_ARMOR_PIERCE, 0.30D);
        if (pierce > 0.0D) {
            event.addReductionModifier(
                DamageContainer.Reduction.ARMOR,
                (container, reduction) -> reduction * (1.0F - (float) pierce)
            );
        }
    }

    private static void applyRoyalty(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)
            || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
            || !player.isUsingItem()
            || WandSupport.resolveWand(player.getUseItem()).isEmpty()) {
            return;
        }

        double reduction = percent(player, GoetyApotheosisAttributes.CASTING_DAMAGE_REDUCTION, 0.50D);
        if (reduction > 0.0D) {
            event.setAmount(event.getAmount() * (1.0F - (float) reduction));
        }
    }

    @SubscribeEvent
    public static void applyBurnHex(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (event.getNewDamage() <= 0.0F
            || !WandSupport.isGoetySpellDamage(source)
            || !(source.is(DamageTypeTags.IS_FIRE)
                || ModDamageSource.hellfireAttacks(source)
                || ModDamageSource.isMagicFire(source))
            || target.hasEffect(GoetyEffects.BURN_HEX)) {
            return;
        }

        Player caster = WandSupport.spellCaster(source);
        double chance = percent(caster, GoetyApotheosisAttributes.BURN_HEX_CHANCE, 0.20D);
        if (chance <= 0.0D) {
            return;
        }

        long gameTime = target.level().getGameTime();
        long lastRoll = target.getPersistentData().getLong(BURN_HEX_ROLL_TIME);
        if (lastRoll != 0L && gameTime - lastRoll < 40L) {
            return;
        }
        target.getPersistentData().putLong(BURN_HEX_ROLL_TIME, gameTime);

        if (target.getRandom().nextDouble() < chance) {
            target.addEffect(new MobEffectInstance(GoetyEffects.BURN_HEX, 100, 0));
        }
    }

    @SubscribeEvent
    public static void applySpellLooting(LivingDropsEvent event) {
        Player beneficiary = spellOrServantOwner(event.getSource());
        if (beneficiary == null || event.getDrops().isEmpty()) {
            return;
        }

        double looting = beneficiary.getAttributeValue(GoetyApotheosisAttributes.SPELL_LOOTING);
        if (looting <= 0.0D) {
            return;
        }

        int bonusItems = Mth.floor(looting);
        if (event.getEntity().getRandom().nextDouble() < looting - bonusItems) {
            bonusItems++;
        }

        List<ItemEntity> candidates = event.getDrops().stream()
            .filter(drop -> !drop.getItem().isEmpty())
            .toList();
        List<ItemEntity> extraDrops = new ArrayList<>();
        for (int i = 0; i < bonusItems && !candidates.isEmpty(); i++) {
            ItemEntity chosen = candidates.get(event.getEntity().getRandom().nextInt(candidates.size()));
            ItemStack stack = chosen.getItem();
            if (stack.getCount() < stack.getMaxStackSize()) {
                stack.grow(1);
            }
            else {
                extraDrops.add(new ItemEntity(
                    event.getEntity().level(),
                    event.getEntity().getX(),
                    event.getEntity().getY(),
                    event.getEntity().getZ(),
                    stack.copyWithCount(1)
                ));
            }
        }
        event.getDrops().addAll(extraDrops);
    }

    private static Player spellOrServantOwner(DamageSource source) {
        if (WandSupport.isGoetySpellDamage(source)) {
            return WandSupport.spellCaster(source);
        }

        Player owner = ownerOf(source.getEntity());
        if (owner != null) {
            return owner;
        }
        if (source.getDirectEntity() instanceof Projectile projectile) {
            return ownerOf(projectile.getOwner());
        }
        return ownerOf(source.getDirectEntity());
    }

    private static Player ownerOf(Entity entity) {
        return entity instanceof IOwned owned && owned.getTrueOwner() instanceof Player player ? player : null;
    }

    private static double percent(Player player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double max) {
        if (player == null) {
            return 0.0D;
        }
        return Mth.clamp(player.getAttributeValue(attribute) - 1.0D, 0.0D, max);
    }
}
