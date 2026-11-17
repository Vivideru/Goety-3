package com.Vivideru.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneMonstrosity;
import com.Polarice3.Goety.common.entities.ally.undead.GraveMonstrosity;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.hostile.illagers.HostileRedstoneMonstrosity;
import com.Polarice3.Goety.config.AttributesConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * The most damage a boss takes from one hit, applied after armour, resistances and every other damage handler: what upstream
 * gets by recomputing {@code actuallyHurt}. In NeoForge 21.1 {@code actuallyHurt} reads the damage from the damage container and
 * ignores its argument, so lowering that argument does nothing.
 */
@EventBusSubscriber(modid = Goety.MOD_ID)
public final class BossDamageCaps {
    private BossDamageCaps() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void capDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        double cap = capFor(event.getEntity());
        if (cap > 0.0D) {
            event.setNewDamage(Math.min(event.getNewDamage(), (float) cap));
        }
    }

    private static double capFor(LivingEntity entity) {
        if (entity instanceof Apostle) {
            return AttributesConfig.get(AttributesConfig.ApostleDamageCap);
        } else if (entity instanceof Vizier) {
            return AttributesConfig.get(AttributesConfig.VizierDamageCap);
        } else if (entity instanceof EnderKeeper) {
            return AttributesConfig.get(AttributesConfig.EnderKeeperDamageCap);
        } else if (entity instanceof RedstoneMonstrosity || entity instanceof HostileRedstoneMonstrosity) {
            return AttributesConfig.get(AttributesConfig.RedstoneMonstrosityDamageCap);
        } else if (entity instanceof GraveMonstrosity) {
            return AttributesConfig.get(AttributesConfig.GraveMonstrosityDamageCap);
        }
        return 0.0D;
    }
}
