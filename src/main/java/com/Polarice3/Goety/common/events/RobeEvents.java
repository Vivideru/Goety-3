package com.Polarice3.Goety.common.events;

import net.neoforged.neoforge.event.tick.EntityTickEvent;

import com.Polarice3.Goety.utils.MobTypeHelper;

import com.Polarice3.Goety.init.ModMobType;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IGolem;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.SnapWartsBlock;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import static net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.MOB_TARGET;

@EventBusSubscriber(modid = Goety.MOD_ID)
public class RobeEvents {

    @SubscribeEvent
    public static void LivingEffects(EntityTickEvent.Post event){
        // Entity tick events can fire for non-living entities in NeoForge 1.21, so guard robe minion logic.
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }
        if (livingEntity != null) {
            if (!livingEntity.level().isClientSide) {
                if (MobsConfig.CompatMinionHeal.get()) {
                    if (livingEntity instanceof OwnableEntity ownable && !(livingEntity instanceof IServant)) {
                        if (ownable.getOwnerUUID() != null) {
                            Player owner = livingEntity.level().getPlayerByUUID(ownable.getOwnerUUID());
                            if (owner != null && MiscCapHelper.getNoHealTime(livingEntity) <= 0) {
                                ServantUtil.healServant(owner, livingEntity);
                            }
                        }
                    }
                }
                if (MobsConfig.NecroSetDebuff.get() || MobsConfig.NamelessSetDebuff.get()) {
                    if (MobUtil.getOwner(livingEntity) != null){
                        if (MobTypeHelper.getMobType(livingEntity) != ModMobType.UNDEAD
                                && !(livingEntity instanceof AbstractGolem)
                                && !(livingEntity instanceof IGolem)
                                && !livingEntity.getType().is(ModTags.EntityTypes.NECRO_NO_DEBUFF)) {
                            boolean flag = false;
                            int amp = 0;
                            if (MobsConfig.NecroSetDebuff.get()) {
                                if (CuriosFinder.hasNecroCrown(MobUtil.getOwner(livingEntity)) || CuriosFinder.hasNecroCape(MobUtil.getOwner(livingEntity))) {
                                    if (CuriosFinder.hasNecroSet(MobUtil.getOwner(livingEntity))) {
                                        amp += 2;
                                    }
                                    flag = true;
                                }
                            }
                            if (MobsConfig.NamelessSetDebuff.get()) {
                                if (CuriosFinder.hasNamelessCrown(MobUtil.getOwner(livingEntity)) || CuriosFinder.hasNamelessCrown(MobUtil.getOwner(livingEntity))) {
                                    if (CuriosFinder.hasNamelessSet(MobUtil.getOwner(livingEntity))) {
                                        amp += 2;
                                    }
                                    flag = true;
                                }
                            }
                            if (flag) {
                                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1 + amp));
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED, 20, 2 + amp));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreakingBlock(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        if (CuriosFinder.hasWarlockRobe(player)){
            if (event.getState().is(ModBlocks.SNAP_WARTS.get()) && event.getState().getValue(SnapWartsBlock.AGE) >= 2){
                if (!player.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        if (player.getRandom().nextFloat() <= 0.25F) {
                            Block.dropResources(event.getState(), player.level(), event.getPos(), null, player, player.getUseItem());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void HurtEvent(LivingIncomingDamageEvent event){
        LivingEntity victim = event.getEntity();
        // Accumulate every robe modifier once so later checks do not overwrite earlier resistances.
        float finalDamage = event.getAmount();
        if (CuriosFinder.hasFrostRobes(victim)){
            if (ModDamageSource.freezeAttacks(event.getSource()) || event.getSource().is(DamageTypeTags.IS_FREEZING)){
                float resistance = 1.0F - (ItemConfig.FrostRobeResistance.get() / 100.0F);
                finalDamage *= resistance;
            }
        }
        if (CuriosFinder.hasAbyssRobes(victim)){
            if (event.getSource().is(DamageTypeTags.IS_DROWNING)){
                if (victim.getAirSupply() <= 0){
                    victim.setAirSupply(20);
                }
                finalDamage *= 0.5F;
            }
        }
        if (CuriosFinder.hasNetherRobe(victim)){
            float resistance = 1.0F - (ItemConfig.NetherRobeResistance.get() / 100.0F);
            if (resistance <= 0.0F){
                event.setCanceled(true);
            } else {
                if (ModDamageSource.hellfireAttacks(event.getSource())){
                    resistance = Math.max(0.75F, resistance);
                    finalDamage *= resistance;
                } else if (event.getSource().is(DamageTypeTags.IS_FIRE) || ModDamageSource.isMagicFire(event.getSource())){
                    finalDamage *= resistance;
                }
            }
        }
        if (CuriosFinder.hasCurio(victim, ModItems.GRAND_TURBAN.get())){
            if (victim instanceof Player player) {
                if (SEHelper.getSoulsAmount(player, ItemConfig.ItemsRepairAmount.get())) {
                    int irks = victim.level().getEntitiesOfClass(AllyIrk.class, victim.getBoundingBox().inflate(32)).size();
                    if ((victim.level().random.nextBoolean() || victim.getHealth() <= victim.getMaxHealth() / 2) && irks < 16) {
                        AllyIrk irk = new AllyIrk(ModEntityType.IRK_SERVANT.get(), victim.level());
                        irk.setPos(victim.getX(), victim.getY(), victim.getZ());
                        irk.setLimitedLife(MobUtil.getSummonLifespan(victim.level()));
                        irk.setTrueOwner(victim);
                        if (victim.level().addFreshEntity(irk)) {
                            SEHelper.decreaseSouls(player, ItemConfig.ItemsRepairAmount.get());
                        }
                    }
                }
            }
        }
        if (CuriosFinder.hasCurio(victim, ModItems.GRAND_ROBE.get())){
            if (MobUtil.isSpellCasting(victim)){
                finalDamage /= 2.0F;
            }
        }
        if(CuriosFinder.hasCurio(victim, ModItems.STORM_ROBE.get())){
            if (ModDamageSource.shockAttacks(event.getSource()) || event.getSource().is(DamageTypes.LIGHTNING_BOLT)){
                float resistance = 1.0F - (ItemConfig.StormRobeResistance.get() / 100.0F);
                finalDamage *= resistance;
            }
            if (event.getSource().is(DamageTypes.LIGHTNING_BOLT)){
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300));
            }
        }
        if (CuriosFinder.hasWitchRobe(victim) || CuriosFinder.hasWarlockRobe(victim)){
            if (event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)){
                if (!(LichdomHelper.isLich(victim) && MainConfig.LichMagicResist.get())){
                    float resistance = 1.0F - (ItemConfig.WitchRobeResistance.get() / 100.0F);
                    if (CuriosFinder.hasWarlockRobe(victim)){
                        resistance = 1.0F - (ItemConfig.WarlockRobeResistance.get() / 100.0F);
                    }
                    finalDamage *= resistance;
                }
            }
        }
        if (CuriosFinder.hasUnholyHat(victim)){
            if (victim.level().dimension() == Level.NETHER){
                finalDamage *= 1.0F - (ItemConfig.UnholyHatNetherResistance.get() / 100.0F);
            }
            if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
                finalDamage = Math.min(finalDamage, AttributesConfig.ApostleDamageCap.get().floatValue());
            }
        }
        if (finalDamage != event.getAmount()) {
            event.setAmount(finalDamage);
        }
    }

    @SubscribeEvent
    public static void AttackEvent(LivingIncomingDamageEvent event){
        LivingEntity victim = event.getEntity();
        Entity source = event.getSource().getEntity();
        Entity direct = event.getSource().getDirectEntity();
        if (!event.getEntity().level().isClientSide) {
            if (event.getSource().is(DamageTypeTags.IS_FIRE)){
                LivingEntity source1 = null;
                if (source instanceof LivingEntity living1) {
                    source1 = living1;
                } else if (MobUtil.getOwner(source) != null) {
                    source1 = MobUtil.getOwner(source);
                }
                if (CuriosFinder.hasNetherRobe(source1)){
                    if (victim.isInvulnerableTo(event.getSource()) || victim.hasEffect(MobEffects.FIRE_RESISTANCE)){
                        DamageSource damageSource = ModDamageSource.magicFireBreath(direct, source1);
                        if (CuriosFinder.hasUnholyRobe(source1)){
                            damageSource = ModDamageSource.hellfire(direct, source1);
                        }
                        victim.hurt(damageSource, event.getAmount());
                        event.setCanceled(true);
                    }
                }
            }
            if (event.getAmount() > 0.0F) {
                if (ItemConfig.VoidRobeTeleportChance.get() > 0) {
                    if (CuriosFinder.hasVoidRobe(victim)) {
                        if (!victim.isInvulnerableTo(event.getSource()) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(victim)) {
                            float chance = ItemConfig.VoidRobeTeleportChance.get() / 100.0F;
                            if (victim.getRandom().nextFloat() <= chance) {
                                double d0 = victim.getX() + (victim.getRandom().nextDouble() - 0.5D) * ItemConfig.VoidRobeTeleportDistance.get();
                                double d1 = victim.getY();
                                double d2 = victim.getZ() + (victim.getRandom().nextDouble() - 0.5D) * ItemConfig.VoidRobeTeleportDistance.get();
                                if (MobUtil.teleport(victim, d0, d1, d2)) {
                                    if (ItemConfig.VoidRobeTeleportDamageCancel.get()) {
                                        event.setCanceled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (source instanceof MagmaCube magmaCube){
            if (CuriosFinder.neutralNetherSet(victim)){
                if (magmaCube.getLastHurtByMob() != victim) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void TargetEvents(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity target = event.getOriginalAboutToBeSetTarget();
        if (attacker instanceof Mob mobAttacker) {
            if (target != null) {
                if (target instanceof Player) {
                    if (MobUtil.isWitchType(mobAttacker)) {
                        if (CuriosFinder.isWitchFriendly(target)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.neutralNecroSet(target) || CuriosFinder.neutralNamelessSet(target)) {
                        boolean undead = CuriosFinder.validNecroUndead(mobAttacker);
                        if (target.level() instanceof ServerLevel serverLevel) {
                            if (MobsConfig.HostileCryptUndead.get()) {
                                if (BlockFinder.findStructure(serverLevel, target.blockPosition(), ModTags.Structures.NECRO_HOSTILE)
                                        && !CuriosFinder.neutralNamelessSet(target)) {
                                    undead = false;
                                }
                            }
                        }
                        if (undead || (CuriosFinder.neutralNamelessSet(target) && CuriosFinder.validNamelessUndead(mobAttacker))) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.neutralAbyssSet(target)) {
                        if (CuriosFinder.validAbyssMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.neutralGeoSet(target)) {
                        if (CuriosFinder.validGeoMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.neutralFrostSet(target)) {
                        if (CuriosFinder.validFrostMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.neutralWindSet(target)) {
                        if (CuriosFinder.validWindMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.neutralStormSet(target)) {
                        if (CuriosFinder.validStormMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.neutralNetherSet(target)) {
                        if (CuriosFinder.validNetherMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.hasUnholyRobe(target) || CuriosFinder.hasUnholyHat(target)) {
                        if (mobAttacker instanceof Ghast || mobAttacker instanceof Blaze || mobAttacker instanceof MagmaCube) {
                            if (event.getTargetType() == MOB_TARGET) {
                                event.setNewAboutToBeSetTarget(null);
                            } else {
                                event.setCanceled(true);
                            }
                        }
                    }
                    if (CuriosFinder.neutralVoidSet(target)) {
                        boolean ender = CuriosFinder.validVoidMob(mobAttacker);
                        if (target.level() instanceof ServerLevel serverLevel) {
                            if (MobsConfig.HostileTerminalEnder.get()) {
                                if (BlockFinder.findStructure(serverLevel, target.blockPosition(), ModTags.Structures.VOID_HOSTILE)) {
                                    ender = false;
                                }
                            }
                        }
                        if (ender) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.neutralWildSet(target)) {
                        if (CuriosFinder.validWildMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.hasWarlockRobe(event.getOriginalAboutToBeSetTarget())) {
                        if (MobTypeHelper.getMobType(mobAttacker) == ModMobType.ARTHROPOD){
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewAboutToBeSetTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void VisibilityEvent(LivingEvent.LivingVisibilityEvent event){
        LivingEntity entity = event.getEntity();
        if (event.getLookingEntity() instanceof LivingEntity && entity instanceof Player) {
            if (entity.isInvisible()){
                if (CuriosFinder.hasCurio(entity, ModItems.ILLUSION_ROBE.get())){
                    event.modifyVisibility(0.0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void OnLivingFall(LivingFallEvent event){
        if (CuriosFinder.hasWindyRobes(event.getEntity())){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void PotionApplicationEvents(MobEffectEvent.Applicable event){
        if (event.getEffectInstance().getEffect().is(GoetyEffects.FREEZING)){
            if (CuriosFinder.hasFrostRobes(event.getEntity())){
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
        if (event.getEffectInstance().getEffect().is(GoetyEffects.BURN_HEX)){
            if (CuriosFinder.hasUnholyHat(event.getEntity()) || CuriosFinder.hasUnholyRobe(event.getEntity())){
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
        if (event.getEffectInstance().getEffect().is(MobEffects.POISON)
                || event.getEffectInstance().getEffect().is(GoetyEffects.ACID_VENOM)) {
            if (CuriosFinder.hasWildRobe(event.getEntity())) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
        if (event.getEffectInstance().getEffect().is(GoetyEffects.VOID_TOUCHED)) {
            if (CuriosFinder.hasVoidRobe(event.getEntity())) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
        if (event.getEffectInstance().getEffect().is(MobEffects.BLINDNESS)){
            if (CuriosFinder.hasIllusionRobe(event.getEntity())){
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }
}
