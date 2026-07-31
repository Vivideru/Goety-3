package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SRemoveEffectPacket;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import org.jetbrains.annotations.Nullable;

import static net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.MOB_TARGET;

@EventBusSubscriber(modid = Goety.MOD_ID)
public class StunnedEvents {

    private static boolean isStunned(@Nullable LivingEntity entity) {
        return entity != null && entity.isAlive() && (entity.hasEffect(GoetyEffects.STUNNED)
                || (entity instanceof Player player && SEHelper.hasCamera(player)));
    }

    public static void cancelEvent(LivingEntity entity, ICancellableEvent event){
        if (isStunned(entity)) {
            // NeoForge 1.21 exposes cancellation through ICancellableEvent instead of the old Event base helpers.
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void cancelPlayerAttack(AttackEntityEvent event) {
        cancelEvent(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void cancelBreakSpeed(PlayerEvent.BreakSpeed event) {
        cancelEvent(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void cancelActivateBlock(PlayerInteractEvent.RightClickBlock event) {
        cancelEvent(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void cancelInteract(PlayerInteractEvent.EntityInteract event) {
        cancelEvent(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void cancelUsingItem(LivingEntityUseItemEvent.Start event) {
        cancelEvent(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void cancelTickUsingItem(LivingEntityUseItemEvent.Tick event) {
        cancelEvent(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void cancelPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        cancelEvent(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onLivingTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob mob && isStunned(mob)) {
            if (event.getTargetType() == MOB_TARGET) {
                event.setNewAboutToBeSetTarget(null);
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (event.getEntity().hasEffect(GoetyEffects.TANGLED)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void PotionApplicationEvents(MobEffectEvent.Applicable event){
        if (event.getEffectInstance().getEffect().is(GoetyEffects.STUNNED)
                || event.getEffectInstance().getEffect().value().getDescriptionId().contains("born_in_chaos_v1:stun")){
            if (event.getEntity().getType().is(ModTags.EntityTypes.UNSTUNNABLE)){
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event){
        if (!event.getEntity().level().isClientSide) {
            if (isStunned(event.getEntity())) {
                event.getEntity().removeEffect(GoetyEffects.STUNNED);
                event.getEntity().removeEffect(GoetyEffects.TANGLED);
                ModNetwork.sendToALL(new SRemoveEffectPacket(event.getEntity().getId(), BuiltInRegistries.MOB_EFFECT.getId(GoetyEffects.STUNNED.get())));
                ModNetwork.sendToALL(new SRemoveEffectPacket(event.getEntity().getId(), BuiltInRegistries.MOB_EFFECT.getId(GoetyEffects.TANGLED.get())));
            }
        }
    }
}
