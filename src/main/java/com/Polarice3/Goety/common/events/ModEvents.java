package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.utils.MobTypeHelper;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.blocks.IEnchanteableBlock;
import com.Polarice3.Goety.api.entities.IChunkLoader;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IHiding;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.SarcophagusBlock;
import com.Polarice3.Goety.common.blocks.ModChestBlock;
import com.Polarice3.Goety.common.capabilities.lichdom.ILichdom;
import com.Polarice3.Goety.common.capabilities.misc.IMisc;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.DefendVillagerGoal;
import com.Polarice3.Goety.common.entities.ai.FreePrisonerGoal;
import com.Polarice3.Goety.common.entities.ai.TargetHostileOwnedGoal;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.ally.golem.IceGolem;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.HereticServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.MaverickServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.ReprobateServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WarlockServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.ModRavager;
import com.Polarice3.Goety.common.entities.ally.illager.raider.Prisoner;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.Ravaged;
import com.Polarice3.Goety.common.entities.ally.undead.GraveGolem;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import com.Polarice3.Goety.common.entities.hostile.WitherNecromancer;
import com.Polarice3.Goety.common.entities.hostile.cultists.*;
import com.Polarice3.Goety.common.entities.hostile.illagers.*;
import com.Polarice3.Goety.common.entities.hostile.servants.Damned;
import com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith;
import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.projectiles.ModDragonFireball;
import com.Polarice3.Goety.common.entities.util.DragonBreathCloud;
import com.Polarice3.Goety.common.entities.util.StormEntity;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.common.items.curios.WarlockGarmentItem;
import com.Polarice3.Goety.common.items.equipment.DarkScytheItem;
import com.Polarice3.Goety.common.items.equipment.IceAxeItem;
import com.Polarice3.Goety.common.items.equipment.PhilosophersMaceItem;
import com.Polarice3.Goety.common.items.equipment.SickleItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.common.world.data.ChunkLoadData;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.compat.patchouli.PatchouliLoaded;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.init.RaidAdditions;
import com.Polarice3.Goety.mixin.VillagerAccessor;
import com.Polarice3.Goety.utils.*;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;

import static net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.MOB_TARGET;

@EventBusSubscriber(modid = Goety.MOD_ID)
public class ModEvents {

    public static void registerMissingMappingAliases() {
        // NeoForge 1.21 removed MissingMappingsEvent; registry aliases preserve the same old-id to new-id remaps.
        BuiltInRegistries.ENTITY_TYPE.addAlias(Goety.location("ally_vex"), ModEntityType.VEX_SERVANT.getId());
        BuiltInRegistries.ENTITY_TYPE.addAlias(Goety.location("ally_irk"), ModEntityType.IRK_SERVANT.getId());
        BuiltInRegistries.ENTITY_TYPE.addAlias(Goety.location("ally_trampler"), ModEntityType.TRAMPLER_SERVANT.getId());
        BuiltInRegistries.ITEM.addAlias(Goety.location("bubble_stream_focus"), ModItems.WATER_JET_FOCUS.getId());
        BuiltInRegistries.BLOCK.addAlias(Goety.location("soiled_oak_planks"), ModBlocks.SOILED_SPRUCE_PLANKS.getId());
        BuiltInRegistries.BLOCK.addAlias(Goety.location("soiled_oak_planks_heavy"), ModBlocks.SOILED_SPRUCE_PLANKS_HEAVY.getId());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        Player original = event.getOriginal();

        // NeoForge attachments are available directly during clone handling.

        ILichdom capability2 = LichdomHelper.getCapability(original);
        ILichdom lichdom = LichdomHelper.getCapability(player);

        lichdom.setLichdom(capability2.getLichdom());

        lichdom.setLichMode(capability2.isLichMode());

        lichdom.setNightVision(capability2.nightVision());

        ISoulEnergy capability3 = SEHelper.getCapability(original);
        ISoulEnergy soulEnergy = SEHelper.getCapability(player);

        soulEnergy.setSEActive(capability3.getSEActive());
        soulEnergy.setSoulEnergy(capability3.getSoulEnergy());
        soulEnergy.setRecoil(capability3.getRecoil());
        soulEnergy.setArcaBlock(capability3.getArcaBlock());
        soulEnergy.setArcaBlockDimension(capability3.getArcaBlockDimension());
        soulEnergy.setRestPeriod(capability3.getRestPeriod());
        for (Research research : capability3.getResearch()){
            soulEnergy.addResearch(research);
        }
        for (UUID uuid : capability3.grudgeList()){
            soulEnergy.addGrudge(uuid);
        }
        for (UUID uuid : capability3.allyList()){
            soulEnergy.addAlly(uuid);
        }
        for (EntityType<?> entityType : capability3.grudgeTypeList()){
            soulEnergy.addGrudgeType(entityType);
        }
        for (EntityType<?> entityType : capability3.allyTypeList()){
            soulEnergy.addAllyType(entityType);
        }
        soulEnergy.setBannerBaseColor(capability3.bannerBaseColor());
        if (capability3.bannerPattern() != null) {
            soulEnergy.setBannerPattern(capability3.bannerPattern());
        }
        soulEnergy.setApostleWarned(capability3.apostleWarned());
        soulEnergy.setCooldowns(capability3.cooldowns());
        soulEnergy.setBottling(capability3.bottling());
        soulEnergy.setCameraUUID(null);
        soulEnergy.setMiningProgress(0);
        soulEnergy.setMiningPos(null);

        IMisc capability4 = MiscCapHelper.getCapability(original);
        IMisc misc = MiscCapHelper.getCapability(player);

        misc.setShields(capability4.shieldsLeft());
        misc.setShieldTime(capability4.shieldTime());
        misc.setShieldCool(capability4.shieldCool());
        misc.setAmbientSoundTime(0);

    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level world = event.getLevel();
        if (entity instanceof LivingEntity && !world.isClientSide()) {
            if (entity instanceof ICustomAttributes configurableAttributes) {
                LivingEntity livingEntity = (LivingEntity) entity;
                AttributeInstance maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
                boolean wasAtFullHealth = livingEntity.getHealth() >= livingEntity.getMaxHealth() - 0.001F;
                @SuppressWarnings("unchecked")
                EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) livingEntity.getType();
                boolean usesRegisteredBase = maxHealth == null || !DefaultAttributes.hasSupplier(livingType)
                        || Math.abs(maxHealth.getBaseValue() - DefaultAttributes.getSupplier(livingType)
                        .getBaseValue(Attributes.MAX_HEALTH)) < 0.000001D;
                if (usesRegisteredBase) {
                    // Attribute suppliers are built before common configs load in 1.21, so refresh untouched mobs when they enter the server level.
                    configurableAttributes.setConfigurableAttributes();
                    if (wasAtFullHealth) {
                        // Keep freshly spawned mobs full while preserving damage already stored on loaded entities.
                        livingEntity.setHealth(livingEntity.getMaxHealth());
                    }
                }
            }
            if (entity instanceof Player player) {
                SEHelper.sendSEUpdatePacket(player);
                LichdomHelper.sendLichUpdatePacket(player);
            }
            if (entity instanceof Mob mob) {
                if (entity instanceof Witch witch) {
                    witch.goalSelector.addGoal(1, new WitchBarterGoal(witch));
                }
                if (mob.getType().is(ModTags.EntityTypes.VILLAGE_GUARDS)) {
                    mob.goalSelector.addGoal(1, new FreePrisonerGoal(mob));
                    mob.targetSelector.addGoal(3, new TargetHostileOwnedGoal<>(mob, Owned.class));
                    mob.targetSelector.addGoal(3, new DefendVillagerGoal(mob));
                }
                if (entity instanceof PathfinderMob creeper && creeper.getType().is(ModTags.EntityTypes.CREEPERS)) {
                    creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(creeper, Player.class, (target) -> target != null && CuriosFinder.hasCurio(target, ModItems.FELINE_AMULET.get()), 6.0F, 1.0D, 1.2D, EntitySelector.NO_SPECTATORS::test));
                }
                if (entity instanceof Zombie zombie) {
                    // 1.21 hides NearestAttackableTargetGoal's target type; vanilla zombies already target villagers, so attach the matching Prisoner target directly.
                    zombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(zombie, Prisoner.class, false));
                }
            }
        }
        if (MainConfig.BetterDragonFireball.get()) {
            if (entity instanceof DragonFireball original) {
                ModDragonFireball dragonFireball;
                if (original.getOwner() instanceof LivingEntity livingEntity) {
                    Vec3 movement = original.getDeltaMovement();
                    // 1.21 no longer exposes projectile acceleration fields; use the current movement vector to preserve the replacement fireball direction.
                    dragonFireball = new ModDragonFireball(entity.level(), livingEntity, movement.x, movement.y, movement.z);
                } else {
                    dragonFireball = new ModDragonFireball(ModEntityType.MOD_DRAGON_FIREBALL.get(), entity.level());
                }
                dragonFireball.moveTo(original.position());
                if (entity.level().addFreshEntity(dragonFireball)) {
                    original.discard();
                    event.setCanceled(true);
                }
            }
            if (entity instanceof AreaEffectCloud cloud){
                if (cloud.getOwner() instanceof EnderDragon){
                    DragonBreathCloud breathCloud = new DragonBreathCloud(entity.level(), cloud.getX(), cloud.getY(), cloud.getZ());
                    breathCloud.setOwner(cloud.getOwner());
                    breathCloud.setRadius(cloud.getRadius());
                    breathCloud.setRadiusOnUse(cloud.getRadiusOnUse());
                    breathCloud.setRadiusPerTick(cloud.getRadiusPerTick());
                    breathCloud.setDuration(cloud.getDuration());
                    breathCloud.setDurationOnUse(cloud.getDurationOnUse());
                    breathCloud.setWaitTime(cloud.getWaitTime());
                    if (entity.level().addFreshEntity(breathCloud)) {
                        cloud.discard();
                        event.setCanceled(true);
                    }
                }
            }
        }
        if (entity instanceof StormEntity){
            if (!entity.level().isClientSide){
                ServerLevel serverWorld = (ServerLevel) entity.level();
                serverWorld.setWeatherParameters(0, 6000, true, true);
            }
        }
        if (entity instanceof Raider raider){
            if (world instanceof ServerLevel) {
                if (raider.hasActiveRaid()) {
                    Raid raid = raider.getCurrentRaid();
                    if (raid != null && raid.isActive() && !raid.isBetweenWaves() && !raid.isOver() && !raid.isStopped()) {
                        Player player = EntityFinder.getNearbyPlayer(world, raid.getCenter());
                        if (player != null) {
                            if (MobsConfig.IllagerRaid.get()) {
                                if (SEHelper.getSoulAmountInt(player) < (MobsConfig.IllagerAssaultSEThreshold.get() * 2)) {
                                    if (raider instanceof HuntingIllagerEntity){
                                        raid.removeFromRaid(raider, true);
                                        event.setCanceled(true);
                                    }
                                }
                            } else {
                                if (raider instanceof HuntingIllagerEntity){
                                    raid.removeFromRaid(raider, true);
                                    event.setCanceled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerEntersWorld(PlayerEvent.PlayerLoggedInEvent event){
        CompoundTag playerData = event.getEntity().getPersistentData();
        CompoundTag data;

        if (!playerData.contains(Player.PERSISTED_NBT_TAG)) {
            data = new CompoundTag();
        } else {
            data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        }
        if (!event.getEntity().level().isClientSide) {
            if (data.getBoolean(ConstantPaths.readScroll())){
                SEHelper.addResearch(event.getEntity(), ResearchList.FORBIDDEN);
            }
            if (MainConfig.StarterTotem.get()) {
                if (!data.getBoolean("goety:gotTotem")) {
                    event.getEntity().addItem(new ItemStack(ModItems.TOTEM_OF_ROOTS.get()));
                    data.putBoolean("goety:gotTotem", true);
                    playerData.put(Player.PERSISTED_NBT_TAG, data);
                }
            }
            if (PatchouliLoaded.PATCHOULI.isLoaded()){
                if (MainConfig.StarterBook.get()){
                    if (!data.getBoolean("goety:starterBook")) {
                        ItemStack book = PatchouliAPI.get().getBookStack(Goety.location("black_book"));
                        event.getEntity().addItem(book);
                        data.putBoolean("goety:starterBook", true);
                        playerData.put(Player.PERSISTED_NBT_TAG, data);
                    }
                }
                if (MainConfig.StarterWitchBook.get()){
                    if (!data.getBoolean("goety:witchBook")) {
                        ItemStack book = PatchouliAPI.get().getBookStack(Goety.location("witches_brew"));
                        event.getEntity().addItem(book);
                        data.putBoolean("goety:witchBook", true);
                        playerData.put(Player.PERSISTED_NBT_TAG, data);
                    }
                }
            }
        }

    }

    private static final Map<ServerLevel, IllagerSpawner> ILLAGER_SPAWN_MAP = new HashMap<>();
    private static final Map<ServerLevel, WightSpawner> WIGHT_SPAWN_MAP = new HashMap<>();

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        RaidAdditions.addRaiders();
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverWorld) {
            ILLAGER_SPAWN_MAP.put(serverWorld, new IllagerSpawner());
            WIGHT_SPAWN_MAP.put(serverWorld, new WightSpawner());
            ChunkLoadData data = ChunkLoadData.get(serverWorld);
            List<BlockPos> toRemove = new ArrayList<>();
            data.getPositions().forEach((pos, radius) -> {
                ChunkPos chunkPos = new ChunkPos(pos);
                serverWorld.getChunkSource().addRegionTicket(
                        ModTicketTypes.BLOCK, chunkPos, radius, pos);
                BlockEntity be = serverWorld.getBlockEntity(pos);
                // Saved loaders may have been disabled since the previous session.
                if (!(be instanceof IChunkLoader loader) || !loader.shouldChunkLoad()) {
                    serverWorld.getChunkSource().removeRegionTicket(ModTicketTypes.BLOCK, chunkPos, radius, pos);
                    toRemove.add(pos);
                }
            });
            if (!toRemove.isEmpty()) {
                for (BlockPos blockPos : toRemove) {
                    data.removePosition(blockPos);
                }
            }
        }
    }

    @SubscribeEvent
    public static void worldUnload(LevelEvent.Unload event) {
        // Custom raid members are handled by the raid spawn hook; Raid.RaiderType.values() is immutable from this path.
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverWorld) {
            ILLAGER_SPAWN_MAP.remove(serverWorld);
            WIGHT_SPAWN_MAP.remove(serverWorld);
        }
    }

    @SubscribeEvent
    public static void onServerTick(LevelTickEvent.Post tick){
        if(!tick.getLevel().isClientSide && tick.getLevel() instanceof ServerLevel serverWorld){
            IllagerSpawner illagerSpawner = ILLAGER_SPAWN_MAP.get(serverWorld);
            if (illagerSpawner != null){
                illagerSpawner.tick(serverWorld);
            }
            WightSpawner wightSpawner = WIGHT_SPAWN_MAP.get(serverWorld);
            if (wightSpawner != null){
                wightSpawner.tick(serverWorld);
            }
        }

    }

    @SubscribeEvent
    public static void CheckSpawnEvents(FinalizeSpawnEvent event){
        if (event.getEntity() instanceof SpellcasterIllager || event.getEntity() instanceof Witch || event.getEntity() instanceof Cultist){
            if (event.getSpawnType() == MobSpawnType.STRUCTURE){
                event.getEntity().addTag(ConstantPaths.structureMob());
            }
        }
        if (event.getSpawnType() == MobSpawnType.STRUCTURE) {
            if (event.getEntity().getTags().contains(ConstantPaths.giveAI())) {
                if (event.getEntity().isNoAi()) {
                    event.getEntity().setNoAi(false);
                    event.getEntity().removeTag(ConstantPaths.giveAI());
                }
            }
        }
        if (event.getEntity() instanceof Cultist cultist){
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                if (serverLevel.getRaidAt(cultist.blockPosition()) != null) {
                    if (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION) {
                        event.setSpawnCancelled(true);
                    }
                }
            }
        }
        Mob mob = event.getEntity();
        if (IronLoaded.IRON_SPELLBOOKS.isLoaded()) {
            if (!IronAttributes.resistances(mob).isEmpty()) {
                for (AttributeInstance attributeInstance : IronAttributes.resistances(mob)) {
                    if (attributeInstance != null){
                        if (mob instanceof Inquillager) {
                            attributeInstance.setBaseValue(1.75D);
                        } else {
                            if (attributeInstance.getAttribute().value() == IronAttributes.EVOCATION_MAGIC_RESIST) {
                                if (mob instanceof Envioker || mob instanceof Minister || mob instanceof Vizier) {
                                    attributeInstance.setBaseValue(1.25D);
                                }
                            }
                            if (attributeInstance.getAttribute().value() == IronAttributes.NATURE_MAGIC_RESIST) {
                                if (mob instanceof Conquillager) {
                                    attributeInstance.setBaseValue(1.25D);
                                }
                                if (mob instanceof Apostle) {
                                    attributeInstance.setBaseValue(1.5D);
                                }
                                if (MobTypeHelper.getMobType(mob) == ModMobType.NATURAL){
                                    attributeInstance.setBaseValue(1.5D);
                                }
                            }
                            if (attributeInstance.getAttribute().value() == IronAttributes.HOLY_MAGIC_RESIST) {
                                if (mob instanceof Conquillager || mob instanceof Preacher || mob instanceof Minister || mob instanceof Vizier) {
                                    attributeInstance.setBaseValue(1.25D);
                                }
                                if (mob instanceof Apostle) {
                                    attributeInstance.setBaseValue(0.25D);
                                }
                            }
                            if (attributeInstance.getAttribute().value() == IronAttributes.ICE_MAGIC_RESIST) {
                                if (mob instanceof Cryologer) {
                                    attributeInstance.setBaseValue(1.5D);
                                }
                                if (mob instanceof IceGolem){
                                    attributeInstance.setBaseValue(2.0D);
                                }
                                if (mob instanceof BlazeServant){
                                    attributeInstance.setBaseValue(0.5D);
                                }
                            }
                            if (attributeInstance.getAttribute().value() == IronAttributes.LIGHTNING_MAGIC_RESIST) {
                                if (mob instanceof StormCaster) {
                                    attributeInstance.setBaseValue(1.5D);
                                }
                            }
                            if (attributeInstance.getAttribute().value() == IronAttributes.FIRE_MAGIC_RESIST) {
                                if (mob instanceof Apostle) {
                                    attributeInstance.setBaseValue(2.0D);
                                }
                                if (mob instanceof IceGolem){
                                    attributeInstance.setBaseValue(0.33D);
                                }
                            }
                            if (attributeInstance.getAttribute().value() == IronAttributes.BLOOD_MAGIC_RESIST) {
                                if (mob instanceof Apostle) {
                                    attributeInstance.setBaseValue(1.75D);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void PlayerTick(PlayerTickEvent.Post event){
        Player player = event.getEntity();
        Level world = player.level();
        if (world instanceof ServerLevel){
            if (player.tickCount % 20 == 0) {
                if (player instanceof ServerPlayer serverPlayer){
                    if (serverPlayer.getServer() != null) {
                        AdvancementHolder advancement3 = serverPlayer.getServer().getAdvancements().get(Goety.location("goety/read_warred_and_haunting_scroll"));
                        if (advancement3 != null) {
                            AdvancementProgress advancementProgress3 = serverPlayer.getAdvancements().getOrStartProgress(advancement3);
                            if (!advancementProgress3.isDone()){
                                AdvancementHolder advancement1 = serverPlayer.getServer().getAdvancements().get(Goety.location("goety/read_warred_scroll"));
                                AdvancementHolder advancement2 = serverPlayer.getServer().getAdvancements().get(Goety.location("goety/read_haunting_scroll"));
                                if (advancement1 != null && advancement2 != null) {
                                    AdvancementProgress advancementProgress1 = serverPlayer.getAdvancements().getOrStartProgress(advancement1);
                                    AdvancementProgress advancementProgress2 = serverPlayer.getAdvancements().getOrStartProgress(advancement2);
                                    if (advancementProgress1.isDone() && advancementProgress2.isDone()){
                                        for(String s : advancementProgress3.getRemainingCriteria()) {
                                            serverPlayer.getAdvancements().award(advancement3, s);
                                        }
                                    }
                                }
                            }
                        }
                        AdvancementHolder advancement4 = serverPlayer.getServer().getAdvancements().get(Goety.location("goety/read_buried_and_bygone_scroll"));
                        if (advancement4 != null) {
                            AdvancementProgress advancementProgress4 = serverPlayer.getAdvancements().getOrStartProgress(advancement4);
                            if (!advancementProgress4.isDone()){
                                AdvancementHolder advancement1 = serverPlayer.getServer().getAdvancements().get(Goety.location("goety/unlock_necromancer"));
                                AdvancementHolder advancement2 = serverPlayer.getServer().getAdvancements().get(Goety.location("goety/read_bygone_scroll"));
                                if (advancement1 != null && advancement2 != null) {
                                    AdvancementProgress advancementProgress1 = serverPlayer.getAdvancements().getOrStartProgress(advancement1);
                                    AdvancementProgress advancementProgress2 = serverPlayer.getAdvancements().getOrStartProgress(advancement2);
                                    if (advancementProgress1.isDone() && advancementProgress2.isDone()){
                                        for(String s : advancementProgress4.getRemainingCriteria()) {
                                            serverPlayer.getAdvancements().award(advancement4, s);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void LivingEffects(EntityTickEvent.Post event){
        // Entity tick events can fire for non-living entities in NeoForge 1.21, so guard living-only state updates.
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }
        if (livingEntity != null && livingEntity.isAlive()){
            tickMiscEffects(livingEntity);
            if (livingEntity instanceof Mob mob){
                double followRange = 32.0D;
                if (mob.getAttribute(Attributes.FOLLOW_RANGE) != null){
                    followRange = mob.getAttributeValue(Attributes.FOLLOW_RANGE) * 2;
                }
//                MiscCapHelper.updateMobTarget(mob); Commented in case it causes lag
                if (mob.getTarget() instanceof Apostle apostle){
                    if (apostle.obsidianInvul > 5){
                        for (AbstractObsidianMonolith obsidianMonolith : mob.level().getEntitiesOfClass(AbstractObsidianMonolith.class, mob.getBoundingBox().inflate(followRange, 8.0D, followRange))){
                            if (obsidianMonolith.getOwner() == apostle){
                                mob.setTarget(obsidianMonolith);
                                try {
                                    mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, obsidianMonolith.getUUID(), 600L);
                                    mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, obsidianMonolith, 600L);
                                    if (mob instanceof Warden warden) {
                                        warden.increaseAngerAt(obsidianMonolith, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
                                        warden.setAttackTarget(obsidianMonolith);
                                    }
                                } catch (NullPointerException ignored) {
                                }
                            }
                        }
                    }
                }
                if (mob.getTarget() instanceof AbstractObsidianMonolith monolith){
                    if (monolith.empowered > 5){
                        for (Heretic heretic : mob.level().getEntitiesOfClass(Heretic.class, mob.getBoundingBox().inflate(followRange, 8.0D, followRange))){
                            if (heretic.getMonolith() == monolith){
                                mob.setTarget(heretic);
                            }
                        }
                    }
                }
                if (mob.getTarget() instanceof IHiding hiding) {
                    if (hiding.isHiding()) {
                        mob.setTarget(null);
                    }
                }
            }
            if (livingEntity instanceof Raider raider) {
                if (raider.getTarget() instanceof Player player) {
                    if (SEHelper.getSoulAmountInt(player) > MobsConfig.IllagerAssaultSEThreshold.get() * 2){
                        if (!raider.isAggressive()) {
                            raider.setAggressive(true);
                        }
                    }
                }
            }
            if (livingEntity instanceof Villager villager){
                if (!villager.level().isClientSide) {
                    Brain<?> brain = villager.getBrain();
                    Optional<LivingEntity> avoidIllager = Optional.empty();
                    NearestVisibleLivingEntities nearestvisiblelivingentities = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
                    for (LivingEntity livingentity : nearestvisiblelivingentities.findAll((p_186157_) -> true)) {
                        if (livingentity instanceof HuntingIllagerEntity || livingentity instanceof Tormentor || livingentity instanceof HostileGolem || livingentity instanceof Trampler || livingentity instanceof Vizier){
                            avoidIllager = Optional.of(livingentity);
                        } else if (livingentity instanceof RaiderServant servant) {
                            if (servant.isRaiding() || servant.isHostile()) {
                                avoidIllager = Optional.of(livingentity);
                                if (servant.isRaiding()) {
                                    brain.setMemory(MemoryModuleType.HEARD_BELL_TIME, villager.level().getGameTime());
                                }
                            }
                        }
                    }
                    if (avoidIllager.isPresent()) {
                        brain.setMemory(MemoryModuleType.NEAREST_HOSTILE, avoidIllager);
                        if (avoidIllager.get() instanceof RaiderServant servant && servant.isRaiding()) {
                            if (!villager.isNoAi() && villager.getRandom().nextInt(100) == 0) {
                                villager.level().broadcastEntityEvent(villager, (byte) 42);
                            }
                        }
                    }
                    Player player = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
                    if (player != null) {
                        if (MobsConfig.VillagerHate.get()) {
                            if (CuriosFinder.hasCurio(player, item -> item.is(ModTags.Items.ROBES))) {
                                if (villager.getPlayerReputation(player) > -25 && villager.getPlayerReputation(player) < 25) {
                                    villager.getGossips().add(player.getUUID(), GossipType.MINOR_NEGATIVE, 25);
                                }
                            }
                        }
                        if (MobsConfig.VillagerHateRavager.get()) {
                            for (Owned owned : player.level().getEntitiesOfClass(Owned.class, player.getBoundingBox().inflate(16.0D))) {
                                if (owned instanceof Ravaged || owned instanceof ModRavager) {
                                    if (owned.getTrueOwner() == player || owned.getMasterOwner() == player) {
                                        if (villager.getPlayerReputation(player) > -200) {
                                            villager.getGossips().add(player.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (villager.level() instanceof ServerLevel serverLevel) {
                        if (MobsConfig.VillagerConvertWarlock.get() || MobsConfig.VillagerConvertWarlockUnholy.get()) {
                            if (BlockFinder.getVerticalBlock(serverLevel, villager.blockPosition(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), 16, true)) {
                                if (villager.getRandom().nextFloat() < 7.5E-4F && serverLevel.getDifficulty() != Difficulty.PEACEFUL) {
                                    if (player != null && CuriosFinder.hasUnholySet(player) && MobsConfig.VillagerConvertWarlockUnholy.get()) {
                                        if (EventHooks.canLivingConvert(villager, ModEntityType.WARLOCK_SERVANT.get(), (timer) -> {
                                        })) {
                                            serverLevel.explode(villager, villager.getX(), villager.getY(), villager.getZ(), 0.1F, Level.ExplosionInteraction.NONE);
                                            WarlockServant warlock = ModEntityType.WARLOCK_SERVANT.get().create(serverLevel);
                                            if (warlock != null) {
                                                warlock.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                                                warlock.setTrueOwner(player);
                                                warlock.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(warlock.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null);
                                                warlock.setNoAi(villager.isNoAi());
                                                if (villager.hasCustomName()) {
                                                    warlock.setCustomName(villager.getCustomName());
                                                    warlock.setCustomNameVisible(villager.isCustomNameVisible());
                                                }

                                                warlock.setPersistenceRequired();
                                                EventHooks.onLivingConvert(villager, warlock);
                                                serverLevel.addFreshEntityWithPassengers(warlock);
                                                MobUtil.releaseAllPois(villager);
                                                villager.discard();
                                            }
                                        }
                                    } else if (MobsConfig.VillagerConvertWarlock.get()) {
                                        if (EventHooks.canLivingConvert(villager, ModEntityType.WARLOCK.get(), (timer) -> {
                                        })) {
                                            serverLevel.explode(villager, villager.getX(), villager.getY(), villager.getZ(), 0.1F, Level.ExplosionInteraction.NONE);
                                            Warlock warlock = ModEntityType.WARLOCK.get().create(serverLevel);
                                            if (warlock != null) {
                                                warlock.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                                                warlock.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(warlock.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null);
                                                warlock.setNoAi(villager.isNoAi());
                                                if (villager.hasCustomName()) {
                                                    warlock.setCustomName(villager.getCustomName());
                                                    warlock.setCustomNameVisible(villager.isCustomNameVisible());
                                                }

                                                warlock.setPersistenceRequired();
                                                EventHooks.onLivingConvert(villager, warlock);
                                                serverLevel.addFreshEntityWithPassengers(warlock);
                                                MobUtil.releaseAllPois(villager);
                                                villager.discard();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (MobsConfig.VillagerConvertHeretic.get() || MobsConfig.VillagerConvertHereticUnholy.get()) {
                            if (villager.getRandom().nextFloat() < 7.5E-4F && villager.isSleeping()) {
                                if (BlockFinder.findNetherPortal(serverLevel, villager.blockPosition(), 8).isPresent()){
                                    if (player != null && (CuriosFinder.hasUnholySet(player) || ItemHelper.hasMaleficHelm(player)) && MobsConfig.VillagerConvertHereticUnholy.get()) {
                                        if (EventHooks.canLivingConvert(villager, ModEntityType.HERETIC_SERVANT.get(), (timer) -> {
                                        })) {
                                            serverLevel.explode(villager, villager.getX(), villager.getY(), villager.getZ(), 0.1F, Level.ExplosionInteraction.NONE);
                                            HereticServant heretic = ModEntityType.HERETIC_SERVANT.get().create(serverLevel);
                                            if (heretic != null) {
                                                heretic.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                                                heretic.setTrueOwner(player);
                                                heretic.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(heretic.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null);
                                                heretic.setNoAi(villager.isNoAi());
                                                if (villager.hasCustomName()) {
                                                    heretic.setCustomName(villager.getCustomName());
                                                    heretic.setCustomNameVisible(villager.isCustomNameVisible());
                                                }

                                                heretic.setPersistenceRequired();
                                                EventHooks.onLivingConvert(villager, heretic);
                                                serverLevel.addFreshEntityWithPassengers(heretic);
                                                MobUtil.releaseAllPois(villager);
                                                villager.discard();
                                            }
                                        }
                                    } else if (MobsConfig.VillagerConvertHeretic.get()) {
                                        if (serverLevel.getDifficulty() != Difficulty.PEACEFUL && EventHooks.canLivingConvert(villager, ModEntityType.HERETIC.get(), (timer) -> {
                                        })) {
                                            serverLevel.explode(villager, villager.getX(), villager.getY(), villager.getZ(), 0.1F, Level.ExplosionInteraction.NONE);
                                            Heretic heretic = ModEntityType.HERETIC.get().create(serverLevel);
                                            if (heretic != null) {
                                                heretic.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                                                heretic.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(heretic.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null);
                                                heretic.setNoAi(villager.isNoAi());
                                                if (villager.hasCustomName()) {
                                                    heretic.setCustomName(villager.getCustomName());
                                                    heretic.setCustomNameVisible(villager.isCustomNameVisible());
                                                }

                                                heretic.setPersistenceRequired();
                                                EventHooks.onLivingConvert(villager, heretic);
                                                serverLevel.addFreshEntityWithPassengers(heretic);
                                                MobUtil.releaseAllPois(villager);
                                                villager.discard();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    int foodLevel = Math.max(0, ((VillagerAccessor) villager).goety$getFoodLevel());
                    // Reading the field directly avoids serializing the full villager and every attachment on each entity tick.
                    if (MiscCapHelper.getCustomFoodLevel(villager) != foodLevel) {
                        MiscCapHelper.setCustomFoodLevel(villager, foodLevel);
                    }
                }
            }
            if (livingEntity instanceof WanderingTrader trader) {
                if (trader.level() instanceof ServerLevel serverLevel) {
                    if (MobsConfig.TraderConvertMaverick.get() || MobsConfig.TraderConvertMaverickUnholy.get()) {
                        if (BlockFinder.getVerticalBlock(serverLevel, trader.blockPosition(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), 16, true)) {
                            if (trader.getRandom().nextFloat() < 7.5E-4F && serverLevel.getDifficulty() != Difficulty.PEACEFUL) {
                                Player player = trader.level().getNearestPlayer(trader.getX(), trader.getY(), trader.getZ(), 16.0D, entity -> entity instanceof Player player1 && (CuriosFinder.hasUnholySet(player1) || ItemHelper.hasMaleficHelm(player1)));
                                if (player != null && (CuriosFinder.hasUnholySet(player) || ItemHelper.hasMaleficHelm(player)) && MobsConfig.TraderConvertMaverickUnholy.get()) {
                                    if (EventHooks.canLivingConvert(trader, ModEntityType.MAVERICK_SERVANT.get(), (timer) -> {
                                    })) {
                                        serverLevel.explode(trader, trader.getX(), trader.getY(), trader.getZ(), 0.1F, Level.ExplosionInteraction.NONE);
                                        MaverickServant maverick = ModEntityType.MAVERICK_SERVANT.get().create(serverLevel);
                                        if (maverick != null) {
                                            maverick.moveTo(trader.getX(), trader.getY(), trader.getZ(), trader.getYRot(), trader.getXRot());
                                            maverick.setTrueOwner(player);
                                            maverick.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(maverick.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null);
                                            maverick.setNoAi(trader.isNoAi());
                                            if (trader.hasCustomName()) {
                                                maverick.setCustomName(trader.getCustomName());
                                                maverick.setCustomNameVisible(trader.isCustomNameVisible());
                                            }

                                            maverick.setPersistenceRequired();
                                            EventHooks.onLivingConvert(trader, maverick);
                                            serverLevel.addFreshEntityWithPassengers(maverick);
                                            trader.discard();
                                        }
                                    }
                                } else if (MobsConfig.TraderConvertMaverick.get()) {
                                    if (EventHooks.canLivingConvert(trader, ModEntityType.MAVERICK.get(), (timer) -> {
                                    })) {
                                        serverLevel.explode(trader, trader.getX(), trader.getY(), trader.getZ(), 0.1F, Level.ExplosionInteraction.NONE);
                                        Maverick maverick = ModEntityType.MAVERICK.get().create(serverLevel);
                                        if (maverick != null) {
                                            maverick.moveTo(trader.getX(), trader.getY(), trader.getZ(), trader.getYRot(), trader.getXRot());
                                            maverick.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(maverick.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null);
                                            maverick.setNoAi(trader.isNoAi());
                                            if (trader.hasCustomName()) {
                                                maverick.setCustomName(trader.getCustomName());
                                                maverick.setCustomNameVisible(trader.isCustomNameVisible());
                                            }

                                            maverick.setPersistenceRequired();
                                            EventHooks.onLivingConvert(trader, maverick);
                                            serverLevel.addFreshEntityWithPassengers(maverick);
                                            trader.discard();
                                        }
                                    }
                                }
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
        if (player.getMainHandItem().getItem() instanceof PhilosophersMaceItem){
            if (event.getState().getBlock().getDescriptionId().contains("nether_gold")){
                if (!player.level().isClientSide) {
                    Block.dropResources(Blocks.GOLD_ORE.defaultBlockState(), player.level(), event.getPos(), null, player, player.getMainHandItem());
                    event.getState().getBlock().playerWillDestroy(player.level(), event.getPos(), event.getState(), player);
                    player.level().setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
                    ItemHelper.hurtAndBreak(player.getMainHandItem(), 1, player);
                    event.setCanceled(true);
                }
            }
        }
        if (player.getMainHandItem().getItem() instanceof DarkScytheItem){
            ItemStack scythe = player.getMainHandItem();
            if (event.getState().getBlock().getDescriptionId().contains("sculk") && event.getState().is(BlockTags.MINEABLE_WITH_HOE)){
                if (!player.level().isClientSide) {
                    ItemStack fakeItem = new ItemStack(Items.DIAMOND_HOE);
                    copyToolEnchantmentsWithSilkTouch(player, scythe, fakeItem);
                    if (event.getState().getBlock() instanceof IEnchanteableBlock){
                        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
                        event.getState().getBlock().playerDestroy(player.level(), event.getPlayer(), event.getPos(), event.getState(), blockEntity, fakeItem);
                    } else {
                        Block.dropResources(event.getState(), player.level(), event.getPos(), null, player, fakeItem);
                    }
                    player.level().levelEvent(player, 2001, event.getPos(), Block.getId(event.getState()));
                    player.level().setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
                    ItemHelper.hurtAndBreak(player.getMainHandItem(), 1, player);
                    event.setCanceled(true);
                }
            }
        }
        if (player.getMainHandItem().getItem() instanceof IceAxeItem){
            ItemStack iceAxe = player.getMainHandItem();
            if (event.getState().is(BlockTags.ICE)){
                if (!player.level().isClientSide) {
                    ItemStack fakeItem = new ItemStack(Items.IRON_PICKAXE);
                    copyToolEnchantmentsWithSilkTouch(player, iceAxe, fakeItem);
                    if (event.getState().getBlock() instanceof IEnchanteableBlock){
                        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
                        event.getState().getBlock().playerDestroy(player.level(), event.getPlayer(), event.getPos(), event.getState(), blockEntity, fakeItem);
                    } else {
                        Block.dropResources(event.getState(), player.level(), event.getPos(), null, player, fakeItem);
                    }
                    event.getState().getBlock().playerWillDestroy(player.level(), event.getPos(), event.getState(), player);
                    player.level().setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
                    ItemHelper.hurtAndBreak(player.getMainHandItem(), 1, player);
                    event.setCanceled(true);
                }
            }
        }
        ItemStack tool = player.getMainHandItem();
        BlockState blockState = event.getState();
        if (tool.getItem() instanceof SickleItem
                && !player.isCreative()
                && !player.level().isClientSide
                && player.level().getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
                && (blockState.is(Blocks.SHORT_GRASS)
                || blockState.is(Blocks.FERN)
                || blockState.is(Blocks.TALL_GRASS)
                || blockState.is(Blocks.LARGE_FERN))) {
            Holder<Enchantment> silkTouch = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.SILK_TOUCH);
            if (tool.getEnchantmentLevel(silkTouch) == 0) {
                Holder<Enchantment> fortune = player.level().registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.FORTUNE);
                int fortuneLevel = tool.getEnchantmentLevel(fortune);

                // These herb seeds were granted by the legacy break event rather than the grass loot tables.
                if (player.level().getRandom().nextFloat() < 0.125F) {
                    int count = 1 + RandomUtil.nextInt(player.level().getRandom(), fortuneLevel);
                    Block.popResource(player.level(), event.getPos(), new ItemStack(ModBlocks.HENBANE_SEEDS.get(), count));
                }
                if (player.level().getRandom().nextFloat() < 0.1F) {
                    int count = 1 + RandomUtil.nextInt(player.level().getRandom(), fortuneLevel);
                    Block.popResource(player.level(), event.getPos(), new ItemStack(ModBlocks.NIGHTSHADE_SEEDS.get(), count));
                }
            }
        }
    }

    private static void tickMiscEffects(LivingEntity livingEntity) {
        IMisc misc = MiscCapHelper.getExistingCapability(livingEntity);
        if (misc == null) {
            return;
        }
        boolean dirty = false;
        if (!MobUtil.isSpellCasting(livingEntity) && misc.getClientTargetID() > 0){
            misc.setClientTargetID(0);
            dirty = true;
        }
        if (misc.shieldsLeft() > 0) {
            if (misc.shieldTime() > 0) {
                misc.decreaseShieldTime();
                dirty = true;
            } else {
                misc.setShields(0);
                dirty = true;
                if (!livingEntity.level().isClientSide) {
                    if (livingEntity instanceof Player player) {
                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.WALL_DISAPPEAR.get(), 1.0F, 2.0F));
                    } else {
                        livingEntity.playSound(ModSounds.WALL_DISAPPEAR.get(), 1.0F, 2.0F);
                    }
                }
            }
        } else if (misc.shieldTime() > 0) {
            misc.setShieldTime(0);
            dirty = true;
        }
        if (misc.shieldCool() > 0){
            misc.decreaseShieldCool();
            dirty = true;
        }
        if (misc.getShakeTime() > 0) {
            misc.setShakeTime(misc.getShakeTime() - 1);
            dirty = true;
        }
        if (misc.getSunscreen() > 0) {
            misc.setSunscreen(misc.getSunscreen() - 1);
            dirty = true;
        }
        if (dirty) {
            // Batch per-tick misc changes so mobs with active Goety state only sync once, and untouched mobs do not create attachments.
            MiscCapHelper.sendMiscUpdatePacket(livingEntity);
        }
    }

    private static void copyToolEnchantmentsWithSilkTouch(Player player, ItemStack source, ItemStack fakeItem) {
        Holder<Enchantment> silkTouch = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        // 1.21 stores enchantments as item components; copy the source component and force Silk Touch for the fake harvesting tool.
        ItemEnchantments sourceEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(source);
        EnchantmentHelper.updateEnchantments(fakeItem, enchantments -> {
            for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Holder<Enchantment>> entry : sourceEnchantments.entrySet()) {
                if (!entry.getKey().is(Enchantments.SILK_TOUCH)) {
                    enchantments.set(entry.getKey(), entry.getIntValue());
                }
            }
            enchantments.set(silkTouch, 1);
        });
    }

    @SubscribeEvent
    public static void TargetEvents(LivingChangeTargetEvent event){
        LivingEntity attacker = event.getEntity();
        LivingEntity target = event.getOriginalAboutToBeSetTarget();
        if (attacker instanceof Mob mobAttacker) {
            if (target != null) {
                if (attacker instanceof IOwned){
                    if (target instanceof ArmorStand || target instanceof HauntedArmorStand){
                        if (event.getTargetType() == MOB_TARGET) {
                            event.setNewAboutToBeSetTarget(null);
                        } else {
                            event.setCanceled(true);
                        }
                    }
                }
                if ((MobTypeHelper.getMobType(mobAttacker) == ModMobType.UNDEAD && !(mobAttacker instanceof IOwned) && mobAttacker.getMaxHealth() < 100.0F) || mobAttacker instanceof Creeper) {
                    if (event.getNewAboutToBeSetTarget() instanceof Apostle) {
                        event.setCanceled(true);
                    }
                }
                if (mobAttacker.getType().is(ModTags.EntityTypes.CREEPERS) && CuriosFinder.hasCurio(target, ModItems.FELINE_AMULET.get())){
                    if (event.getTargetType() == MOB_TARGET) {
                        event.setNewAboutToBeSetTarget(null);
                    } else {
                        event.setCanceled(true);
                    }
                }
                if (mobAttacker instanceof Phantom && CuriosFinder.hasCurio(target, ModItems.FELINE_AMULET.get())){
                    if (event.getTargetType() == MOB_TARGET) {
                        event.setNewAboutToBeSetTarget(null);
                    } else {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    private static final String NO_KNOCKBACK_TAG = "goety:no_knockback";

    @SubscribeEvent
    public static void AttackEvent(LivingIncomingDamageEvent event){
        LivingEntity victim = event.getEntity();
        Entity source = event.getSource().getEntity();
        Entity direct = event.getSource().getDirectEntity();
        if (!event.getEntity().level().isClientSide) {
            if (MiscCapHelper.getShields(victim) > 0
                    && !event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS)
                    && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
                if (MiscCapHelper.getShieldCool(victim) <= 0) {
                    MiscCapHelper.decreaseShields(victim);
                    if (SpellConfig.BulwarkShieldBreakExtra.get() > 0.0D) {
                        int extra = Mth.floor(event.getAmount() / SpellConfig.BulwarkShieldBreakExtra.get());
                        if (extra >= 1) {
                            for (int i = 0; i < extra; ++i) {
                                MiscCapHelper.decreaseShields(victim);
                            }
                        }
                    }
                    MiscCapHelper.setShieldCool(victim, 10);
                    if (event.getSource().getEntity() instanceof LivingEntity livingEntity){
                        MobUtil.knockBack(livingEntity, victim, 1.0D, 0.2D, 1.0D);
                    }
                }
                event.setCanceled(true);
            }
            if (MainConfig.GoodwillNoDamage.get()) {
                Player player = null;
                if (source instanceof Player player1) {
                    player = player1;
                } else if (MobUtil.getOwner(source) instanceof Player player1) {
                    player = player1;
                }
                if (player != null) {
                    if (SEHelper.isAlly(player, victim)) {
                        event.setCanceled(true);
                    }
                } else if (source instanceof IOwned owned) {
                    if (owned.isAllyWith(victim)) {
                        event.setCanceled(true);
                    }
                }
            }
            if (victim instanceof Witch witch) {
                double d0 = witch.getAttributeValue(Attributes.FOLLOW_RANGE);
                AABB axisalignedbb = AABB.unitCubeFromLowerCorner(witch.position()).inflate(d0, 10.0D, d0);
                List<Mob> list = witch.level().getEntitiesOfClass(Mob.class, axisalignedbb);

                for (Mob mob : list){
                    if (mob.getTarget() == null && witch.getLastHurtByMob() != null && !(witch.getLastHurtByMob() instanceof Raider) && !MobUtil.areAllies(witch.getLastHurtByMob(), witch)) {
                        if (mob instanceof Cultist && !mob.getType().is(Tags.EntityTypes.BOSSES) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(witch.getLastHurtByMob())) {
                            mob.setTarget(witch.getLastHurtByMob());
                        }
                    }
                }
            }
        }

        if (event.getSource() instanceof NoKnockBackDamageSource){
            if (!victim.level().isClientSide) {
                CompoundTag tag = victim.getPersistentData();
                tag.putInt(NO_KNOCKBACK_TAG, victim.tickCount);
            }
        }
        
        if (source instanceof IOwned owned){
            if (owned.getMasterOwner() instanceof Player player) {
                victim.setLastHurtByPlayer(player);
            }
        }

        if (direct instanceof AbstractArrow arrowEntity){
            if (arrowEntity.getTags().contains(ConstantPaths.rainArrow()) || arrowEntity.getOwner() instanceof Apostle){
                if (arrowEntity.getOwner() != null) {
                    if (victim instanceof IOwned ownedEntity) {
                        if (ownedEntity.getTrueOwner() != null) {
                            if (ownedEntity.getTrueOwner() == arrowEntity.getOwner()) {
                                event.setCanceled(true);
                            }
                        }
                    }
                    if (victim == arrowEntity.getOwner()){
                        event.setCanceled(true);
                    }
                }
            }
            if (!(arrowEntity.getOwner() instanceof Apostle && victim.level().getDifficulty() == Difficulty.HARD)) {
                if (victim instanceof Player player) {
                    if (MobUtil.starAmuletActive(player)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingIncomingDamageEvent event){
        LivingEntity victim = event.getEntity();
        if (ModDamageSource.shockAttacks(event.getSource())){
            if (victim.level() instanceof ServerLevel serverLevel){
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_ELECTRIC.get(), victim);
                ModNetwork.sendToALL(new SPlayWorldSoundPacket(victim.blockPosition(), ModSounds.ZAP.get(), 2.0F, 1.0F));
            }
        }
        if (ModDamageSource.isMagicFire(event.getSource())){
            float amount = event.getAmount();
            if (victim.fireImmune() && !victim.hasEffect(GoetyEffects.BURN_HEX)) {
                // Burn Hex suppresses the normal fire-immunity reduction for magical flames.
                amount /= 2.0F;
            }
            int k = MobUtil.getDamageProtection(victim, victim.damageSources().inFire());
            if (k > 0) {
                amount = CombatRules.getDamageAfterMagicAbsorb(amount, (float) k);
            }
            event.setAmount(amount);
        }
        if (ModDamageSource.hellfireAttacks(event.getSource())){
            if (victim.level() instanceof ServerLevel serverLevel){
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), victim);
                ModNetwork.sendToALL(new SPlayWorldSoundPacket(victim.blockPosition(), SoundEvents.PLAYER_HURT_ON_FIRE, 2.0F, 1.0F));
            }
            float amount = event.getAmount();
            if (MobsConfig.HellfireFireImmune.get()) {
                if (victim.fireImmune() && !victim.hasEffect(GoetyEffects.BURN_HEX)) {
                    amount /= 2.0F;
                }
            }
            if (MobsConfig.HellfireFireProtection.get()) {
                int k = victim.level() instanceof ServerLevel serverLevel ? Mth.floor(EnchantmentHelper.getDamageProtection(serverLevel, victim, victim.damageSources().inFire())) : 0;
                if (k > 0) {
                    amount = CombatRules.getDamageAfterMagicAbsorb(amount, (float) k / 2.0F);
                }
            }
            event.setAmount(amount);
        }
        if (victim instanceof BlazeServant){
            if (event.getSource().getDirectEntity() instanceof Snowball){
                if (event.getSource().is(DamageTypes.THROWN)) {
                    if (event.getAmount() <= 0.0F) {
                        event.setAmount(3.0F);
                    }
                }
            }
        }
        if (victim instanceof Prisoner) {
            Entity entity = event.getSource().getEntity();
            if (entity instanceof Mob mob) {
                if (mob.getType().is(ModTags.EntityTypes.VILLAGE_GUARDS)) {
                    // 1.21 removed DamageSource#isIndirect; matching direct and causing entities keeps this as a melee-only guard check.
                    if (event.getSource().getDirectEntity() == event.getSource().getEntity()) {
                        if (mob.getTarget() != victim) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void DamageEvent(LivingDamageEvent.Pre event){
        LivingEntity target = event.getEntity();
        if (target instanceof Player player) {
            if (MobUtil.starAmuletActive(player)){
                if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow && !(arrow.getOwner() instanceof Apostle && target.level().getDifficulty() == Difficulty.HARD)){
                    // LivingDamageEvent.Pre is not cancellable in 1.21; zero damage preserves the old cancel outcome.
                    event.setNewDamage(0.0F);
                }
            }
        }

        if (event.getSource().getDirectEntity() instanceof Fangs fangEntity){
            if (fangEntity.getOwner() instanceof Player player) {
                if (fangEntity.isAbsorbing()) {
                    player.heal(event.getNewDamage());
                }
            }
        }
        if (event.getNewDamage() > 0.0F){
            float damageAmount = event.getNewDamage();
            if (event.getSource().is(ModDamageSource.LIFE_LEECH)
                    && event.getSource().getEntity() instanceof LivingEntity livingEntity){
                float percent = SpellConfig.LeechingPercent.get() / 100.0F;
                livingEntity.heal(event.getNewDamage() * percent);
            }
            if (target.isInWaterOrRain()){
                if (ModDamageSource.shockAttacks(event.getSource())){
                    event.setNewDamage(damageAmount * 2.0F);
                }
            }
            if (ModDamageSource.freezeAttacks(event.getSource())){
                if (target.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)){
                    event.setNewDamage(damageAmount * 0.5F);
                }
            }
            if (ModDamageSource.waterAttacks(event.getSource())){
                if (target.isSensitiveToWater()){
                    event.setNewDamage(damageAmount * 2.0F);
                } else if (MobTypeHelper.getMobType(target) == ModMobType.WATER){
                    event.setNewDamage(damageAmount * 0.5F);
                }
            }
            float totalReduce = 0;
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()){
                if (equipmentSlot.isArmor()){
                    ItemStack itemStack = target.getItemBySlot(equipmentSlot);
                    if (itemStack.getItem() instanceof ArmorItem armorItem){
                        if (armorItem.getMaterial() == ModArmorMaterials.BLACK_IRON
                                || armorItem.getMaterial() == ModArmorMaterials.DARK) {
                            float reducedDamage = getReducedDamage(event, armorItem);
                            totalReduce += reducedDamage;
                        }
                    }
                }
            }
            if (totalReduce > 0) {
                damageAmount -= totalReduce;
                damageAmount = Math.max(0, damageAmount);
                event.setNewDamage(damageAmount);
            }
            /*if (event.getSource().getEntity() instanceof Player attacker) {
                if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SOUL_EATER, attacker) > 0) {
                    int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SOUL_EATER, attacker);
                    int percent = ((level - 1) * 5) + 15;
                    float rawPercent = (float) SEHelper.getSoulAmountInt(attacker) / MainConfig.MaxArcaSouls.get();
                    float totalPercent = rawPercent * percent;
                    if (attacker.level.getRandom().nextFloat() <= totalPercent){
                        event.setAmount(damageAmount * 2.0F);
                    }
                }
            }*/
        }
    }

    private static float getReducedDamage(LivingDamageEvent.Pre event, ArmorItem armorItem) {
        float reduction = 0;
        if (event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            reduction = armorItem.getDefense() / 25.0F;
        } else if (event.getSource().is(DamageTypeTags.IS_FIRE) || event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            reduction = armorItem.getDefense() / 10.0F;
        }
        return event.getNewDamage() * reduction;
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event){
        if ((event.getEntity().hasEffect(GoetyEffects.CURSED) || ModDamageSource.hellfireAttacks(event.getEntity().getLastDamageSource())) && event.getAmount() > 0.0F){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void SpecialDeath(LivingDeathEvent event){
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        Level world = killed.getCommandSenderWorld();
        if (killed instanceof PathfinderMob){
            if (killed.hasEffect(GoetyEffects.GOLD_TOUCHED)){
                if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    int amp = Objects.requireNonNull(killed.getEffect(GoetyEffects.GOLD_TOUCHED)).getAmplifier() + 1;
                    for (int i = 0; i < (killed.level().random.nextInt(3) + 1) * amp; ++i) {
                        killed.spawnAtLocation(new ItemStack(Items.GOLD_NUGGET));
                    }
                }
            }
        }
        if (world instanceof ServerLevel serverLevel) {
            // Illague conversion is handled while the effect is active; repeating it on death can duplicate conversion state.
            if (killed instanceof AbstractIllager illager){
                if (!illager.getType().getDescriptionId().contains("magispeller")
                        && !illager.getType().getDescriptionId().contains("faker")
                        && !illager.getType().getDescriptionId().contains("freakager")
                        && !illager.getType().getDescriptionId().contains("spiritcaller")) {
                    for (Apostle apostle : world.getEntitiesOfClass(Apostle.class, illager.getBoundingBox().inflate(32))) {
                        if (apostle.hasLineOfSight(illager)) {
                            Damned damned = new Damned(ModEntityType.DAMNED.get(), world);
                            damned.moveTo(illager.blockPosition().below(2), apostle.getYHeadRot(), apostle.getXRot());
                            damned.setTrueOwner(apostle);
                            damned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(illager.blockPosition().below()), MobSpawnType.MOB_SUMMONED, null);
                            if (illager.hasCustomName()){
                                damned.setCustomName(illager.getCustomName());
                            }
                            damned.setHuman(false);
                            if (apostle.getTarget() != null) {
                                damned.setTarget(apostle.getTarget());
                            }
                            damned.setLimitedLife(100);
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), damned);
                            world.addFreshEntity(damned);
                        }
                    }
                }
            }
        }
        if (killer instanceof Player player){
            if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
                Entity entity = event.getSource().getDirectEntity();
                if (entity instanceof Fangs){
                    if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()) {
                        int enchantment = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.holder(player, ModEnchantments.WANTING), CuriosFinder.findRing(player));
                        if (enchantment >= 3) {
                            if (world.random.nextFloat() <= (enchantment / 9.0F)) {
                                if (killed.getType() == EntityType.SKELETON) {
                                    killed.spawnAtLocation(new ItemStack(Items.SKELETON_SKULL));
                                }
                                if (killed.getType() == EntityType.ZOMBIE) {
                                    killed.spawnAtLocation(new ItemStack(Items.ZOMBIE_HEAD));
                                }
                                if (killed.getType() == EntityType.CREEPER) {
                                    killed.spawnAtLocation(new ItemStack(Items.CREEPER_HEAD));
                                }
                                if (killed.getType() == EntityType.WITHER_SKELETON) {
                                    killed.spawnAtLocation(new ItemStack(Items.WITHER_SKELETON_SKULL));
                                }
                                if (killed.getType() == EntityType.PIGLIN) {
                                    killed.spawnAtLocation(new ItemStack(Items.PIGLIN_HEAD));
                                }
                                if (MobsConfig.TallSkullDrops.get()) {
                                    if (killed instanceof Villager || killed instanceof AbstractIllager) {
                                        killed.spawnAtLocation(new ItemStack(ModBlocks.TALL_SKULL_ITEM.get()));
                                    }
                                    if (killed instanceof Witch || (killed instanceof Cultist && killed.getType() != ModEntityType.APOSTLE.get())) {
                                        killed.spawnAtLocation(new ItemStack(ModBlocks.TALL_SKULL_ITEM.get()));
                                    }
                                }
                            }
                            if (killed instanceof Player player1) {
                                ItemStack head = new ItemStack(Items.PLAYER_HEAD);
                                // Player head ownership moved from raw item NBT to the profile data component in 1.21.
                                head.set(DataComponents.PROFILE, new ResolvableProfile(player1.getGameProfile()));
                                killed.spawnAtLocation(head);
                            }
                        }
                    }
                }
                if (killed.getType() == EntityType.SPIDER){
                    if (CuriosFinder.hasCurio(player, itemStack -> itemStack.getItem() instanceof WarlockGarmentItem)){
                        if (world.random.nextFloat() <= 0.075F){
                            for (int i = 0; i < (world.random.nextInt(2) + 1); ++i) {
                                killed.spawnAtLocation(new ItemStack(ModItems.SPIDER_EGG.get()));
                            }
                        }
                    }
                }
            }
        }
        if (killer instanceof WitherNecromancer necromancer){
            MobUtil.createWitherRose(killed, necromancer);
        }
/*        if (killer instanceof LivingEntity livingEntity){
            net.minecraft.network.chat.Component deathMessage = killed.getCombatTracker().getDeathMessage();
            livingEntity.sendSystemMessage(deathMessage);
        }*/
        if (!event.isCanceled()){
            MiscCapHelper.setFreezing(killed, 0);
            MiscCapHelper.setShields(killed, 0);
            MiscCapHelper.setShieldTime(killed, 0);
            MiscCapHelper.setShakeTime(killed, 0);
        }
    }

    @SubscribeEvent
    public static void ExperienceEvents(LivingExperienceDropEvent event){
        Player player = event.getAttackingPlayer();
        int exp = event.getDroppedExperience();
        if (player != null) {
            if (CuriosFinder.hasCurio(player, ModItems.RING_OF_THIRST.get())) {
                int i = ItemHelper.repairPlayerItems(player, exp);
                if (i > 0) {
                    player.giveExperiencePoints(i);
                }
                event.setCanceled(true);
            }
        }
    }

    private static LootTable getLootTable(ServerLevel serverLevel, ResourceLocation location) {
        return serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, location));
    }

    private static int getLootingLevel(DamageSource damageSource, LivingEntity living) {
        int lootingLevel = 0;
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            Holder<Enchantment> looting = attacker.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
            lootingLevel = EnchantmentHelper.getEnchantmentLevel(looting, attacker);
        }
        // LivingDropsEvent no longer exposes the precomputed looting value, so rebuild it and then apply Goety's existing modifiers.
        return LootingLevelHelper.modifyLootingLevel(damageSource, living, lootingLevel);
    }

    @SubscribeEvent
    public static void DropEvents(LivingDropsEvent event){
        if (event.getEntity() != null) {
            LivingEntity living = event.getEntity();
            if (living instanceof Player player){
                if (CuriosFinder.hasWitchSet(player)){
                    if (living.level() instanceof ServerLevel serverLevel) {
                        LootTable loottable = getLootTable(serverLevel, ModLootTables.PLAYER_WITCH);
                        LootParams.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), living);
                        LootParams ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(living, loot)));
                    }
                }
                if (!living.level().isClientSide) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        for (LivingEntity livingEntity : serverPlayer.level().getEntitiesOfClass(LivingEntity.class, serverPlayer.getBoundingBox().inflate(64.0D))) {
                            if (livingEntity instanceof GraveGolem graveGolem) {
                                if (graveGolem.getTrueOwner() == serverPlayer) {
                                    graveGolem.addDrops(event.getDrops());
                                    event.getDrops().clear();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (living instanceof SpellcasterIllager || living instanceof Witch || living instanceof Cultist) {
                if (living.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    if (living.getTags().contains(ConstantPaths.structureMob())) {
                        float chance = 0.025F;
                        chance += (float) getLootingLevel(event.getSource(), living) / 100;
                        if (living.level().random.nextFloat() <= chance) {
                            event.getDrops().add(ItemHelper.itemEntityDrop(living, new ItemStack(ModItems.FORBIDDEN_FRAGMENT.get())));
                        }
                    }
                }
            }
            if (MobsConfig.TallSkullDrops.get()) {
                if (living.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    if (living instanceof AbstractVillager || living instanceof Prisoner || living instanceof AbstractIllager || living instanceof Witch || living instanceof Cultist) {
                        if (living.level() instanceof ServerLevel serverLevel) {
                            LootTable loottable = getLootTable(serverLevel, ModLootTables.TALL_SKULL);
                            LootParams.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), living);
                            LootParams lootparams = lootcontext$builder.create(LootContextParamSets.ENTITY);
                            loottable.getRandomItems(lootparams).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(living, loot)));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void KnockBackEvents(LivingKnockBackEvent event){
        LivingEntity knocked = event.getEntity();
        if (!knocked.level().isClientSide) {
            CompoundTag tag = knocked.getPersistentData();
            if (tag.contains(NO_KNOCKBACK_TAG)) {
                int stampedTick = tag.getInt(NO_KNOCKBACK_TAG);
                if (knocked.tickCount - stampedTick <= 1) {
                    event.setCanceled(true);
                }
                tag.remove(NO_KNOCKBACK_TAG);
            }
        }
    }

    @SubscribeEvent
    public static void addVillagerTrade(VillagerTradesEvent event){
        ModTradeUtil.addVillagerTrades(event, VillagerProfession.CARTOGRAPHER, 3, new ModTradeUtil.TreasureMapForEmeralds(14, ModTags.Structures.CRYPT_EXPLORER, "filled_map.goety.crypt", MapDecorationTypes.WOODLAND_MANSION, 12, 10));
    }

    @SubscribeEvent
    public static void addWanderTrade(WandererTradesEvent event){
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();
        genericTrades.add(new ModTradeUtil.ItemsForEmeralds(ModItems.JADE.get(), 1, 64, 16));
        genericTrades.add(new ModTradeUtil.ItemsForEmeralds(ModBlocks.WINDSWEPT_SAPLING.get(), 5, 1, 8));
        genericTrades.add(new ModTradeUtil.ItemsForEmeralds(ModBlocks.PINE_SAPLING.get(), 5, 1, 8));
        rareTrades.add(new ModTradeUtil.TreasureMapForEmeralds(8, ModTags.Structures.OMINOUS_BLACKSMITH, "filled_map.goety.ominous_blacksmith", MapDecorationTypes.TARGET_X, 12, 10));
        rareTrades.add(new ModTradeUtil.TreasureMapForEmeralds(8, ModTags.Structures.WIND_SHRINE, "filled_map.goety.wind_shrine", MapDecorationTypes.TARGET_X, 12, 10));
        rareTrades.add(new ModTradeUtil.TreasureMapForEmeralds(8, ModTags.Structures.BLIGHTED_SHACK, "filled_map.goety.blighted_shack", MapDecorationTypes.WOODLAND_MANSION, 12, 10));
        rareTrades.add(new ModTradeUtil.TreasureMapForEmeralds(8, ModTags.Structures.RUINED_MONASTERY, "filled_map.goety.ruined_monastery", MapDecorationTypes.WOODLAND_MANSION, 12, 10));
    }

    @SubscribeEvent
    public static void LightningStruckEvent(EntityStruckByLightningEvent event){
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (level instanceof ServerLevel serverLevel) {
            if (entity instanceof Mob mob) {
                if (mob.getType().is(ModTags.EntityTypes.FRAYED_CONVERT)) {
                    if (MobsConfig.ZombieConvertFrayed.get()) {
                        EntityType<?> entityType = ModEntityType.FRAYED.get();
                        boolean servant = mob instanceof OwnableEntity;
                        if (event.getLightning().getCause() != null) {
                            if (CuriosFinder.hasNamelessSet(event.getLightning().getCause())) {
                                servant = true;
                            }
                        }
                        if (servant) {
                            entityType = ModEntityType.FRAYED_SERVANT.get();
                        }
                        Entity newMob = MobUtil.convertTo(mob, entityType, true, null);
                        if (newMob != null) {
                            if (newMob instanceof IServant servant2) {
                                if (event.getLightning().getCause() != null && CuriosFinder.hasNamelessSet(event.getLightning().getCause())) {
                                    servant2.setTrueOwner(event.getLightning().getCause());
                                } else if (MobUtil.getOwner(mob) != null) {
                                    servant2.setTrueOwner(MobUtil.getOwner(mob));
                                }
                                if (mob instanceof IServant servant1) {
                                    servant2.copyStance(servant1);
                                    servant2.setHostile(servant1.isHostile());
                                    servant2.setNatural(servant1.isNatural());
                                }
                            }
                        }
                    }
                }
                if (mob.getType().is(ModTags.EntityTypes.RATTLED_CONVERT)) {
                    if (MobsConfig.SkeletonConvertRattled.get()) {
                        EntityType<?> entityType = ModEntityType.RATTLED.get();
                        boolean servant = mob instanceof OwnableEntity;
                        if (event.getLightning().getCause() != null) {
                            if (CuriosFinder.hasNamelessSet(event.getLightning().getCause())) {
                                servant = true;
                            }
                        }
                        if (servant) {
                            entityType = ModEntityType.RATTLED_SERVANT.get();
                        }
                        Entity newMob = MobUtil.convertTo(mob, entityType, true, null);
                        if (newMob != null) {
                            if (newMob instanceof IServant servant2) {
                                if (event.getLightning().getCause() != null && CuriosFinder.hasNamelessSet(event.getLightning().getCause())) {
                                    servant2.setTrueOwner(event.getLightning().getCause());
                                } else if (MobUtil.getOwner(mob) != null) {
                                    servant2.setTrueOwner(MobUtil.getOwner(mob));
                                }
                                if (mob instanceof IServant servant1) {
                                    servant2.copyStance(servant1);
                                    servant2.setHostile(servant1.isHostile());
                                    servant2.setNatural(servant1.isNatural());
                                }
                            }
                        }
                    }
                }
            }
            if (entity instanceof WanderingTrader trader) {
                boolean hasConverted = false;
                if (event.getLightning().getCause() != null) {
                    if (CuriosFinder.hasUnholySet(event.getLightning().getCause()) && MobsConfig.TraderConvertReprobateUnholy.get()) {
                        ReprobateServant reprobate = ModEntityType.REPROBATE_SERVANT.get().create(serverLevel);
                        if (reprobate != null) {
                            reprobate.moveTo(trader.getX(), trader.getY(), trader.getZ(), trader.getYRot(), trader.getXRot());
                            reprobate.setTrueOwner(event.getLightning().getCause());
                            reprobate.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(reprobate.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null);
                            reprobate.setNoAi(trader.isNoAi());
                            if (trader.hasCustomName()) {
                                reprobate.setCustomName(trader.getCustomName());
                                reprobate.setCustomNameVisible(trader.isCustomNameVisible());
                            }

                            reprobate.setPersistenceRequired();
                            net.neoforged.neoforge.event.EventHooks.onLivingConvert(trader, reprobate);
                            serverLevel.addFreshEntityWithPassengers(reprobate);
                            hasConverted = true;
                            trader.discard();
                        }
                    }
                }
                if (!hasConverted) {
                    if (MobsConfig.TraderConvertReprobate.get()) {
                        if (serverLevel.getDifficulty() != Difficulty.PEACEFUL && net.neoforged.neoforge.event.EventHooks.canLivingConvert(trader, ModEntityType.MAVERICK.get(), (timer) -> {
                        })) {
                            Reprobate reprobate = ModEntityType.REPROBATE.get().create(serverLevel);
                            if (reprobate != null) {
                                reprobate.moveTo(trader.getX(), trader.getY(), trader.getZ(), trader.getYRot(), trader.getXRot());
                                reprobate.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(reprobate.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null);
                                reprobate.setNoAi(trader.isNoAi());
                                if (trader.hasCustomName()) {
                                    reprobate.setCustomName(trader.getCustomName());
                                    reprobate.setCustomNameVisible(trader.isCustomNameVisible());
                                }

                                reprobate.setPersistenceRequired();
                                net.neoforged.neoforge.event.EventHooks.onLivingConvert(trader, reprobate);
                                serverLevel.addFreshEntityWithPassengers(reprobate);
                                trader.discard();
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void ExplosionStartEvent(ExplosionEvent.Start event){
        /*Explosion explosion = event.getExplosion();
        if (explosion != null && !(explosion instanceof LootingExplosion)) {
            if (explosion.getIndirectSourceEntity() instanceof Player player){
                if (CuriosFinder.hasWanting(player)){
                    ExplosionUtil.lootExplode(explosion.level, explosion.getExploder(), explosion.x, explosion.y, explosion.z, explosion.radius, explosion.fire, explosion.blockInteraction, LootingExplosion.Mode.LOOT);
                    event.setCanceled(true);
                }
            }
        }*/
    }

    @SubscribeEvent
    public static void ExplosionDetonateEvent(ExplosionEvent.Detonate event){
        if (event.getExplosion() != null) {
            event.getAffectedEntities().removeIf(entity -> (entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() == ModItems.UNHOLY_BLOOD.get()));
            event.getAffectedEntities().removeIf(entity -> (entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() == ModBlocks.NIGHT_BEACON_ITEM.get()));
        }
    }

    @SubscribeEvent
    public static void ProjectileImpactEvent(ProjectileImpactEvent event){
        if (event.getProjectile() instanceof AbstractArrow arrowEntity) {
            if (arrowEntity.getTags().contains(ConstantPaths.rainArrow())) {
                arrowEntity.discard();
            }
        }
    }

    @SubscribeEvent
    public static void SleepEvents(CanPlayerSleepEvent event){
        if (event.getEntity() != null) {
            Level level = event.getEntity().level();
            if (level.getBlockState(event.getPos()).getBlock() instanceof SarcophagusBlock) {
                if (level.isDay()) {
                    event.setProblem(null);
                } else {
                    event.setProblem(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
                }
                return;
            }
            if (!event.getEntity().isCreative()) {
                double d0 = 8.0D;
                double d1 = 5.0D;
                Vec3 vec3 = Vec3.atBottomCenterOf(event.getPos());
                List<LivingEntity> list = event.getEntity().level().getEntitiesOfClass(LivingEntity.class, new AABB(vec3.x() - d0, vec3.y() - d1, vec3.z() - d0, vec3.x() + d0, vec3.y() + d1, vec3.z() + d0), (p_9062_) -> {
                    return p_9062_ instanceof IOwned owned
                            && owned.preventsSleep(event.getEntity());
                });
                if (!list.isEmpty()) {
                    event.setProblem(Player.BedSleepingProblem.NOT_SAFE);
                }
            }
        }
    }

    @SubscribeEvent
    public static void sarcophagusCanContinueSleeping(CanContinueSleepingEvent event) {
        if (event.getEntity() instanceof Player player && player.getSleepingPos().map(pos ->
                player.level().getBlockState(pos).getBlock() instanceof SarcophagusBlock).orElse(false)) {
            event.setContinueSleeping(true);
        }
    }

    @SubscribeEvent
    public static void FurnaceBurnItems(FurnaceFuelBurnTimeEvent event){
        if (!event.getItemStack().isEmpty()){
            ItemStack itemStack = event.getItemStack();
            if (itemStack.is(ModBlocks.ROTTEN_BOOKSHELF.get().asItem())
                    || itemStack.is(ModBlocks.WINDSWEPT_BOOKSHELF.get().asItem())
                    || itemStack.is(ModBlocks.PINE_BOOKSHELF.get().asItem())
                    || (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ModChestBlock && blockItem.getBlock().defaultBlockState().ignitedByLava())
                    || itemStack.is(ModBlocks.COMPACTED_WINDSWEPT_PLANKS.get().asItem())
                    || itemStack.is(ModBlocks.COMPACTED_PINE_PLANKS.get().asItem())
                    || itemStack.is(ModBlocks.THATCHED_PINE_PLANKS.get().asItem())
                    || itemStack.is(ModBlocks.SKY_WOOD_PLANKS.get().asItem())
                    || itemStack.is(ModBlocks.OVERGROWN_ROOTS.get().asItem())) {
                event.setBurnTime(300);
            }
            if (itemStack.is(ModBlocks.WITCH_POLE.get().asItem())){
                event.setBurnTime(200);
            }
            if (itemStack.is(ModBlocks.WINDSWEPT_DEAD_BUSH.get().asItem())){
                event.setBurnTime(100);
            }
        }
    }

    @SubscribeEvent
    public static void sarcophagusSleepFinished(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level && level.players().stream().anyMatch(player -> player.isSleeping()
                && player.getSleepingPos().map(pos -> level.getBlockState(pos).getBlock() instanceof SarcophagusBlock).orElse(false))) {
            long dayTime = level.getDayTime();
            long timeOfDay = dayTime % 24000L;
            long wakeUpTime = dayTime - timeOfDay + 13000L;
            if (wakeUpTime < dayTime) {
                wakeUpTime += 24000L;
            }
            event.setTimeAddition(wakeUpTime);
        }
    }


    @SubscribeEvent
    public static void wreckDrops(LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide || !(victim instanceof Mob)
                || event.getSource().is(ModDamageSource.DISMISSED)
                || victim.getType().is(ModTags.EntityTypes.UNWRECKABLE)) {
            return;
        }
        Player player = event.getSource().getEntity() instanceof Player directPlayer ? directPlayer : null;
        if (player == null && victim.getLastHurtByMob() instanceof Player lastHurtPlayer) {
            player = lastHurtPlayer;
        }
        if (player != null && CuriosFinder.hasCurio(player, ModItems.RING_OF_WRECKING.get())) {
            boolean wrecked = event.getDrops().removeIf(drop -> ItemHelper.isWreckable(drop.getItem()));
            if (wrecked && !victim.isSilent()) {
                victim.playSound(SoundEvents.ITEM_BREAK, 1.0F, 0.8F + victim.getRandom().nextFloat() * 0.4F);
            }
        }
    }

    @SubscribeEvent
    public static void onTeleport(EntityTeleportEvent event) {
        if (!(event instanceof EntityTeleportEvent.TeleportCommand)
                && !(event instanceof EntityTeleportEvent.SpreadPlayersCommand)
                && event.getEntity() instanceof Player player) {
            // Command teleports are administrative actions and must not trigger the Dragon Ring blast.
            CuriosFinder.dragonBlast(player, event.getPrev());
        }
    }
}
