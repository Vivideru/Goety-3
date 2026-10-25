package com.Vivideru.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.AbstractBeam;
import com.Vivideru.Goety.common.blocks.PolarCrystalBlock;
import com.Vivideru.Goety.common.blocks.entities.PlinthHooks;
import com.Vivideru.Goety.common.blocks.entities.SealerHooks;
import com.Vivideru.Goety.common.entities.ai.PlinthReturnGoal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import com.Polarice3.Goety.common.blocks.ApparitionDoorBlock;

@EventBusSubscriber(modid = Goety.MOD_ID)
public final class PlinthSealerCrystalEvents {
    private PlinthSealerCrystalEvents() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Mob mob && PlinthHooks.canRest(mob)) {
            mob.goalSelector.addGoal(1, new PlinthReturnGoal(mob));
        }
    }

    /** A statue skips its whole tick: no AI, no sounds, no particles, no animation. The client snaps it to the server pose. */
    @SubscribeEvent
    public static void onEntityTickPre(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Mob mob) || !PlinthHooks.canRest(mob)) {
            return;
        }
        if (PlinthHooks.isFrozen(mob)) {
            if (mob.level().isClientSide) {
                PlinthHooks.freezeClient(mob);
            }
            event.setCanceled(true);
        } else if (mob.level().isClientSide) {
            PlinthHooks.thawClient(mob);
        }
    }

    /** Statues on a Plinth cannot be hurt; only damage that bypasses invulnerability gets through and wakes them. */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !PlinthHooks.isResting(mob)) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            PlinthHooks.onDisturbed(mob);
            return;
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof BlockHitResult hit && isGoetySpell(event.getProjectile())) {
            PolarCrystalBlock.hit(event.getProjectile().level(), hit.getBlockPos());
        }
    }

    /** Called by beams every tick with the block they end on. */
    public static void onBeamHit(AbstractBeam beam, HitResult hit) {
        if (hit instanceof BlockHitResult blockHit) {
            PolarCrystalBlock.hit(beam.level(), blockHit.getBlockPos());
        }
    }

    private static boolean isGoetySpell(Projectile projectile) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType()).getNamespace().equals(Goety.MOD_ID);
    }

    /** A sealed chest refuses to open and says so; sealed Apparition Doors handle their own clicks. */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide || !SealerHooks.isSealed(level, event.getPos()) || level.getBlockState(event.getPos()).getBlock() instanceof ApparitionDoorBlock) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        level.playSound(null, event.getPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
        event.getEntity().displayClientMessage(Component.translatable("info.goety.sealer.sealed"), true);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getPlayer().isCreative() && SealerHooks.isSealed(event.getLevel(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> SealerHooks.isSealed(event.getLevel(), pos));
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide) {
            SealerHooks.clear(level);
        }
    }
}
