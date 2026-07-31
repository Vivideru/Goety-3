package com.Polarice3.Goety.common.entities;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.ally.ender.BlastlingServant;
import com.Polarice3.Goety.common.entities.ally.ender.SnarelingServant;
import com.Polarice3.Goety.common.entities.ally.ender.WatchlingServant;
import com.Polarice3.Goety.common.entities.ally.golem.*;
import com.Polarice3.Goety.common.entities.ally.illager.*;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.*;
import com.Polarice3.Goety.common.entities.ally.illager.raider.*;
import com.Polarice3.Goety.common.entities.ally.spider.*;
import com.Polarice3.Goety.common.entities.ally.undead.*;
import com.Polarice3.Goety.common.entities.ally.undead.bound.*;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.*;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.*;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import com.Polarice3.Goety.common.entities.deco.HauntedPainting;
import com.Polarice3.Goety.common.entities.hostile.*;
import com.Polarice3.Goety.common.entities.hostile.cultists.*;
import com.Polarice3.Goety.common.entities.hostile.ender.Blastling;
import com.Polarice3.Goety.common.entities.hostile.ender.Endersent;
import com.Polarice3.Goety.common.entities.hostile.ender.Snareling;
import com.Polarice3.Goety.common.entities.hostile.ender.Watchling;
import com.Polarice3.Goety.common.entities.hostile.illagers.*;
import com.Polarice3.Goety.common.entities.hostile.servants.*;
import com.Polarice3.Goety.common.entities.neutral.*;
import com.Polarice3.Goety.common.entities.projectiles.*;
import com.Polarice3.Goety.common.entities.util.*;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.Polarice3.Goety.common.entities.vehicle.ModChestBoat;
import com.Polarice3.Goety.common.entities.vehicle.HauntedBroom;
import com.Polarice3.Goety.common.entities.vehicle.SeatEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntityType {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Goety.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<NetherMeteor>> NETHER_METEOR = register("nether_meteor",
            EntityType.Builder.<NetherMeteor>of(NetherMeteor::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<ModFireball>> MOD_FIREBALL = register("fireball",
            EntityType.Builder.<ModFireball>of(ModFireball::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<Lavaball>> LAVABALL = register("lavaball",
            EntityType.Builder.<Lavaball>of(Lavaball::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<HellBolt>> HELL_BOLT = register("hell_bolt",
            EntityType.Builder.<HellBolt>of(HellBolt::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<HellBlast>> HELL_BLAST = register("hell_blast",
            EntityType.Builder.<HellBlast>of(HellBlast::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<HellChant>> HELL_CHANT = register("hell_chant",
            EntityType.Builder.<HellChant>of(HellChant::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(64)
                    .updateInterval(3));

    public static final DeferredHolder<EntityType<?>, EntityType<SwordProjectile>> SWORD = register("sword",
            EntityType.Builder.<SwordProjectile>of(SwordProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<IceSpike>> ICE_SPIKE = register("ice_spike",
            EntityType.Builder.<IceSpike>of(IceSpike::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<IceSpear>> ICE_SPEAR = register("ice_spear",
            EntityType.Builder.<IceSpear>of(IceSpear::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<IceStorm>> ICE_STORM = register("ice_storm",
            EntityType.Builder.<IceStorm>of(IceStorm::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<GhostArrow>> GHOST_ARROW = register("ghost_arrow",
            EntityType.Builder.<GhostArrow>of(GhostArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final DeferredHolder<EntityType<?>, EntityType<RainArrow>> RAIN_ARROW = register("rain_arrow",
            EntityType.Builder.<RainArrow>of(RainArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final DeferredHolder<EntityType<?>, EntityType<DeathArrow>> DEATH_ARROW = register("death_arrow",
            EntityType.Builder.<DeathArrow>of(DeathArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final DeferredHolder<EntityType<?>, EntityType<Harpoon>> HARPOON = register("harpoon",
            EntityType.Builder.<Harpoon>of(Harpoon::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<PoisonQuill>> POISON_QUILL = register("poison_quill",
            EntityType.Builder.<PoisonQuill>of(PoisonQuill::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final DeferredHolder<EntityType<?>, EntityType<BoneShard>> BONE_SHARD = register("bone_shard",
            EntityType.Builder.<BoneShard>of(BoneShard::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownBrew>> BREW = register("brew",
            EntityType.Builder.<ThrownBrew>of(ThrownBrew::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<ReprobateCarry>> REPROBATE_CARRY = register("reprobate_carry",
            EntityType.Builder.<ReprobateCarry>of(ReprobateCarry::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(4)
                    .updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<ScytheSlash>> SCYTHE = register("scythe",
            EntityType.Builder.<ScytheSlash>of(ScytheSlash::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<ModDragonFireball>> MOD_DRAGON_FIREBALL = register("dragon_fireball",
            EntityType.Builder.<ModDragonFireball>of(ModDragonFireball::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntedSkullProjectile>> HAUNTED_SKULL_SHOT = register("haunted_skull_shot",
            EntityType.Builder.<HauntedSkullProjectile>of(HauntedSkullProjectile::new, MobCategory.MISC)
                    .sized(0.5f,0.5f)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<ModWitherSkull>> MOD_WITHER_SKULL = register("wither_skull",
            EntityType.Builder.<ModWitherSkull>of(ModWitherSkull::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SoulLight>> SOUL_LIGHT = register("soul_light",
            EntityType.Builder.<SoulLight>of(SoulLight::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<GlowLight>> GLOW_LIGHT = register("glow_light",
            EntityType.Builder.<GlowLight>of(GlowLight::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SoulBullet>> SOUL_BULLET = register("soul_bullet",
            EntityType.Builder.<SoulBullet>of(SoulBullet::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SoulBolt>> SOUL_BOLT = register("soul_bolt",
            EntityType.Builder.<SoulBolt>of(SoulBolt::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<PoisonBolt>> POISON_BOLT = register("poison_bolt",
            EntityType.Builder.<PoisonBolt>of(PoisonBolt::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SteamMissile>> STEAM_MISSILE = register("steam_missile",
            EntityType.Builder.<SteamMissile>of(SteamMissile::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<WitherBolt>> WITHER_BOLT = register("wither_bolt",
            EntityType.Builder.<WitherBolt>of(WitherBolt::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<NecroBolt>> NECRO_BOLT = register("necro_bolt",
            EntityType.Builder.<NecroBolt>of(NecroBolt::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBolt>> MAGIC_BOLT = register("magic_bolt",
            EntityType.Builder.<MagicBolt>of(MagicBolt::new, MobCategory.MISC)
                    .sized(0.625F, 0.625F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<ShieldDebris>> SHIELD_DEBRIS = register("shield_debris",
            EntityType.Builder.<ShieldDebris>of(ShieldDebris::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<VoidShock>> VOID_SHOCK = register("void_shock",
            EntityType.Builder.<VoidShock>of(VoidShock::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<VoidShockBomb>> VOID_SHOCK_BOMB = register("void_shock_bomb",
            EntityType.Builder.<VoidShockBomb>of(VoidShockBomb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<Fangs>> FANG = register("fang",
            EntityType.Builder.<Fangs>of(Fangs::new, MobCategory.MISC)
                    .sized(0.5F, 0.8F)
                    .clientTrackingRange(6)
                    .updateInterval(2));

    public static final DeferredHolder<EntityType<?>, EntityType<Spike>> SPIKE = register("spike",
            EntityType.Builder.<Spike>of(Spike::new, MobCategory.MISC)
                    .sized(0.5F, 0.8F)
                    .clientTrackingRange(6)
                    .updateInterval(2));

    public static final DeferredHolder<EntityType<?>, EntityType<IllBomb>> ILL_BOMB = register("ill_bomb",
            EntityType.Builder.<IllBomb>of(IllBomb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final DeferredHolder<EntityType<?>, EntityType<EyeItemEntity>> CRYPTIC_EYE = register("cryptic_eye",
            EntityType.Builder.<EyeItemEntity>of(EyeItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(4));

    public static final DeferredHolder<EntityType<?>, EntityType<EyeItemEntity>> VOID_EYE = register("void_eye",
            EntityType.Builder.<EyeItemEntity>of(EyeItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(4));

    public static final DeferredHolder<EntityType<?>, EntityType<FlyingItem>> FLYING_ITEM = register("flying_item",
            EntityType.Builder.<FlyingItem>of(FlyingItem::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(4));

    public static final DeferredHolder<EntityType<?>, EntityType<ElectroOrb>> ELECTRO_ORB = register("electro_orb",
            EntityType.Builder.<ElectroOrb>of(ElectroOrb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<SurgingOrb>> SURGING_ORB = register("surging_orb",
            EntityType.Builder.<SurgingOrb>of(SurgingOrb::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(10)
                    .updateInterval(20));

    public static final DeferredHolder<EntityType<?>, EntityType<BouncyBubble>> BOUNCY_BUBBLE = register("bouncy_bubble",
            EntityType.Builder.<BouncyBubble>of(BouncyBubble::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .setUpdateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<IceBouquet>> ICE_BOUQUET = register("ice_bouquet",
            EntityType.Builder.<IceBouquet>of(IceBouquet::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.8F, 1.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicFire>> MAGIC_FIRE = register("magic_fire",
            EntityType.Builder.<MagicFire>of(MagicFire::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.8F, 1.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Hellfire>> HELLFIRE = register("hellfire",
            EntityType.Builder.<Hellfire>of(Hellfire::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.8F, 1.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<IceChunk>> ICE_CHUNK = register("ice_chunk",
            EntityType.Builder.<IceChunk>of(IceChunk::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 1.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<ViciousTooth>> VICIOUS_TOOTH = register("vicious_tooth",
            EntityType.Builder.<ViciousTooth>of(ViciousTooth::new, MobCategory.MISC)
                    .sized(0.6F, 1.4F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<ViciousPike>> VICIOUS_PIKE = register("vicious_pike",
            EntityType.Builder.<ViciousPike>of(ViciousPike::new, MobCategory.MISC)
                    .sized(0.6F, 3.0F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<EarthFist>> EARTH_FIST = register("earth_fist",
            EntityType.Builder.<EarthFist>of(EarthFist::new, MobCategory.MISC)
                    .sized(1.5F, 2.0F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<BlossomThorn>> BLOSSOM_THORN = register("blossom_thorn",
            EntityType.Builder.<BlossomThorn>of(BlossomThorn::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<CorruptedBeam>> CORRUPTED_BEAM = register("corrupted_beam",
            EntityType.Builder.<CorruptedBeam>of(CorruptedBeam::new, MobCategory.MISC)
                    .fireImmune()
                    .setShouldReceiveVelocityUpdates(false)
                    .sized(2.0F, 1.0F)
                    .clientTrackingRange(6)
                    .updateInterval(2));

    public static final DeferredHolder<EntityType<?>, EntityType<ScatterMine>> SCATTER_MINE = register("scatter_mine",
            EntityType.Builder.<ScatterMine>of(ScatterMine::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<ScatterBomb>> SCATTER_BOMB = register("scatter_bomb",
            EntityType.Builder.<ScatterBomb>of(ScatterBomb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SoulBomb>> SOUL_BOMB = register("soul_bomb",
            EntityType.Builder.<SoulBomb>of(SoulBomb::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SnapFungus>> SNAP_FUNGUS = register("snap_fungus",
            EntityType.Builder.<SnapFungus>of(SnapFungus::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<BlastFungus>> BLAST_FUNGUS = register("blast_fungus",
            EntityType.Builder.<BlastFungus>of(BlastFungus::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<BerserkFungus>> BERSERK_FUNGUS = register("berserk_fungus",
            EntityType.Builder.<BerserkFungus>of(BerserkFungus::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<Pyroclast>> PYROCLAST = register("pyroclast",
            EntityType.Builder.<Pyroclast>of(Pyroclast::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SmackStone>> SMACK_STONE = register("smack_stone",
            EntityType.Builder.<SmackStone>of(SmackStone::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<MagmaBomb>> MAGMA_BOMB = register("magma_bomb",
            EntityType.Builder.<MagmaBomb>of(MagmaBomb::new, MobCategory.MISC)
                    .sized(1.25F, 1.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<BlossomBall>> BLOSSOM_BALL = register("blossom_ball",
            EntityType.Builder.<BlossomBall>of(BlossomBall::new, MobCategory.MISC)
                    .sized(1.25F, 1.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<WebShot>> WEB_SHOT = register("web_shot",
            EntityType.Builder.<WebShot>of(WebShot::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SnarelingShot>> SNARELING_SHOT = register("snareling_shot",
            EntityType.Builder.<SnarelingShot>of(SnarelingShot::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<EnderGoo>> ENDER_GOO = register("ender_goo",
            EntityType.Builder.<EnderGoo>of(EnderGoo::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<EntangleVines>> ENTANGLE_VINES = register("entangle_vines",
            EntityType.Builder.<EntangleVines>of(EntangleVines::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SpiderWeb>> SPIDER_WEB = register("spider_web",
            EntityType.Builder.<SpiderWeb>of(SpiderWeb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SnarelingGoop>> SNARELING_GOOP = register("snareling_goop",
            EntityType.Builder.<SnarelingGoop>of(SnarelingGoop::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<ObsidianMonolith>> OBSIDIAN_MONOLITH = register("obsidian_monolith",
            EntityType.Builder.of(ObsidianMonolith::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.0F, 3.1F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<TotemicWall>> TOTEMIC_WALL = register("totemic_wall",
            EntityType.Builder.of(TotemicWall::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.0F, 3.1F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<TotemicBomb>> TOTEMIC_BOMB = register("totemic_bomb",
            EntityType.Builder.of(TotemicBomb::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.0F, 3.1F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<GlacialWall>> GLACIAL_WALL = register("glacial_wall",
            EntityType.Builder.of(GlacialWall::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.1F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<QuickGrowingVine>> QUICK_GROWING_VINE = register("quick_growing_vine",
            EntityType.Builder.of(QuickGrowingVine::new, MobCategory.MONSTER)
                    .sized(1.0F, 4.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<QuickGrowingKelp>> QUICK_GROWING_KELP = register("quick_growing_kelp",
            EntityType.Builder.of(QuickGrowingKelp::new, MobCategory.MONSTER)
                    .sized(1.0F, 4.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<PoisonQuillVine>> POISON_QUILL_VINE = register("poison_quill_vine",
            EntityType.Builder.of(PoisonQuillVine::new, MobCategory.MONSTER)
                    .sized(1.0F, 4.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<PoisonAnemone>> POISON_ANEMONE = register("poison_anemone",
            EntityType.Builder.of(PoisonAnemone::new, MobCategory.MONSTER)
                    .sized(1.0F, 4.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<BioMine>> BIOMINE = register("biomine",
            EntityType.Builder.of(BioMine::new, MobCategory.MISC)
                    .sized(1.2F, 1.2F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SpiderEgg>> SPIDER_EGG = register("spider_egg",
            EntityType.Builder.of(SpiderEgg::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<InsectSwarm>> INSECT_SWARM = register("insect_swarm",
            EntityType.Builder.<InsectSwarm>of(InsectSwarm::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<BeastHead>> BEAST_HEAD = register("beast_head",
            EntityType.Builder.<BeastHead>of(BeastHead::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(20));

    public static final DeferredHolder<EntityType<?>, EntityType<GulfTentacle>> GULF_TENTACLE = register("gulf_tentacle",
            EntityType.Builder.<GulfTentacle>of(GulfTentacle::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(20));

    public static final DeferredHolder<EntityType<?>, EntityType<Volcano>> VOLCANO = register("volcano",
            EntityType.Builder.of(Volcano::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 1.0F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<FireTornado>> FIRE_TORNADO = register("fire_tornado",
            EntityType.Builder.<FireTornado>of(FireTornado::new, MobCategory.MISC)
                    .sized(1.0F, 1.5F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<Cyclone>> CYCLONE = register("cyclone",
            EntityType.Builder.<Cyclone>of(Cyclone::new, MobCategory.MISC)
                    .sized(1.0F, 1.5F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<TidalSurge>> TIDAL_SURGE = register("tidal_surge",
            EntityType.Builder.<TidalSurge>of(TidalSurge::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.5F, 1.5F)
                    .setShouldReceiveVelocityUpdates(true)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<RazorWind>> RAZOR_WIND = register("razor_wind",
            EntityType.Builder.<RazorWind>of(RazorWind::new, MobCategory.MISC)
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(64));

    public static final DeferredHolder<EntityType<?>, EntityType<VoidSlash>> VOID_SLASH = register("void_slash",
            EntityType.Builder.<VoidSlash>of(VoidSlash::new, MobCategory.MISC)
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(64));

    public static final DeferredHolder<EntityType<?>, EntityType<ModFallingBlock>> FALLING_BLOCK = register("falling_block",
            EntityType.Builder.<ModFallingBlock>of(ModFallingBlock::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<ModBoat>> MOD_BOAT = register("boat",
            EntityType.Builder.<ModBoat>of(ModBoat::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<ModChestBoat>> MOD_CHEST_BOAT = register("chest_boat",
            EntityType.Builder.<ModChestBoat>of(ModChestBoat::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntedPainting>> MOD_PAINTING = register("haunted_painting",
            EntityType.Builder.<HauntedPainting>of(HauntedPainting::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntedArmorStand>> HAUNTED_ARMOR_STAND = register("haunted_armor_stand",
            EntityType.Builder.<HauntedArmorStand>of(HauntedArmorStand::new, MobCategory.MISC)
                    .sized(0.5F, 1.975F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntedBroom>> HAUNTED_BROOM = register("haunted_broom",
            EntityType.Builder.<HauntedBroom>of(HauntedBroom::new, MobCategory.MISC)
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Apostle>> APOSTLE = register("apostle",
            EntityType.Builder.of(Apostle::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Warlock>> WARLOCK = register("warlock",
            EntityType.Builder.of(Warlock::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Wartling>> WARTLING = register("wartling",
            EntityType.Builder.of(Wartling::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.2F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Heretic>> HERETIC = register("heretic",
            EntityType.Builder.of(Heretic::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Maverick>> MAVERICK = register("maverick",
            EntityType.Builder.of(Maverick::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Reprobate>> REPROBATE = register("reprobate",
            EntityType.Builder.of(Reprobate::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Crone>> CRONE = register("crone",
            EntityType.Builder.of(Crone::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Heresiarch>> HERESIARCH = register("heresiarch",
            EntityType.Builder.of(Heresiarch::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4375F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletonVillagerServant>> SKELETON_VILLAGER_SERVANT = register("skeleton_villager_servant",
            EntityType.Builder.of(SkeletonVillagerServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<ZPiglinServant>> ZPIGLIN_SERVANT = register("zpiglin_servant",
            EntityType.Builder.of(ZPiglinServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .fireImmune()
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<ZPiglinBruteServant>> ZPIGLIN_BRUTE_SERVANT = register("zpiglin_brute_servant",
            EntityType.Builder.of(ZPiglinBruteServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .fireImmune()
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Malghast>> MALGHAST = register("malghast",
            EntityType.Builder.of(Malghast::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.0F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Inferno>> INFERNO = register("inferno",
            EntityType.Builder.of(Inferno::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .fireImmune()
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Damned>> DAMNED = register("damned",
            EntityType.Builder.of(Damned::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<VampireBat>> VAMPIRE_BAT = register("vampire_bat",
            EntityType.Builder.of(VampireBat::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.9F)
                    .clientTrackingRange(5));

    public static final DeferredHolder<EntityType<?>, EntityType<HostileBlackWolf>> HOSTILE_BLACK_WOLF = register("hostile_black_wolf",
            EntityType.Builder.of(HostileBlackWolf::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Frayed>> FRAYED = register("frayed",
            EntityType.Builder.of(Frayed::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Rattled>> RATTLED = register("rattled",
            EntityType.Builder.of(Rattled::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Reaper>> REAPER = register("reaper",
            EntityType.Builder.of(Reaper::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Wraith>> WRAITH = register("wraith",
            EntityType.Builder.of(Wraith::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BorderWraith>> BORDER_WRAITH = register("border_wraith",
            EntityType.Builder.of(BorderWraith::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MuckWraith>> MUCK_WRAITH = register("muck_wraith",
            EntityType.Builder.of(MuckWraith::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    // Slime dimensions are multiplied by the synced Size value in 1.21, so the base type size must match vanilla's smallest slime.
    public static final DeferredHolder<EntityType<?>, EntityType<CryptSlime>> CRYPT_SLIME = register("crypt_slime",
            EntityType.Builder.<CryptSlime>of(CryptSlime::new, MobCategory.MONSTER)
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<WebSpider>> WEB_SPIDER = register("web_spider",
            EntityType.Builder.of(WebSpider::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<IcySpider>> ICY_SPIDER = register("icy_spider",
            EntityType.Builder.of(IcySpider::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BoneSpider>> BONE_SPIDER = register("bone_spider",
            EntityType.Builder.of(BoneSpider::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BroodMother>> BROOD_MOTHER = register("brood_mother",
            EntityType.Builder.of(BroodMother::new, MobCategory.MONSTER)
                    .sized(3.2F, 1.8F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<HostileNecromancer>> NECROMANCER = register("necromancer",
            EntityType.Builder.of(HostileNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<CairnNecromancer>> CAIRN_NECROMANCER = register("cairn_necromancer",
            EntityType.Builder.of(CairnNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MossyNecromancer>> MOSSY_NECROMANCER = register("mossy_necromancer",
            EntityType.Builder.of(MossyNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntedArmor>> HAUNTED_ARMOR = register("haunted_armor",
            EntityType.Builder.of(HauntedArmor::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Watchling>> WATCHLING = register("watchling",
            EntityType.Builder.of(Watchling::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.6F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Blastling>> BLASTLING = register("blastling",
            EntityType.Builder.of(Blastling::new, MobCategory.MONSTER)
                    .sized(1.2F, 2.6F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Snareling>> SNARELING = register("snareling",
            EntityType.Builder.of(Snareling::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.6F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Endersent>> ENDERSENT = register("endersent",
            EntityType.Builder.of(Endersent::new, MobCategory.MONSTER)
                    .sized(0.8F, 5.6F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<EnderKeeper>> ENDER_KEEPER = register("ender_keeper",
            EntityType.Builder.of(EnderKeeper::new, MobCategory.MONSTER)
                    .sized(1.5F, 3.0F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));

    public static final DeferredHolder<EntityType<?>, EntityType<AllyVex>> VEX_SERVANT = register("vex_servant",
            EntityType.Builder.of(AllyVex::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<AllyIrk>> IRK_SERVANT = register("irk_servant",
            EntityType.Builder.of(AllyIrk::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<ZombieServant>> ZOMBIE_SERVANT = register("zombie_servant",
            EntityType.Builder.of(ZombieServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<ZombieVillagerServant>> ZOMBIE_VILLAGER_SERVANT = register("zombie_villager_servant",
            EntityType.Builder.of(ZombieVillagerServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<HuskServant>> HUSK_SERVANT = register("husk_servant",
            EntityType.Builder.of(HuskServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<DrownedServant>> DROWNED_SERVANT = register("drowned_servant",
            EntityType.Builder.of(DrownedServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<FrozenZombieServant>> FROZEN_ZOMBIE_SERVANT = register("frozen_zombie_servant",
            EntityType.Builder.of(FrozenZombieServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<JungleZombieServant>> JUNGLE_ZOMBIE_SERVANT = register("jungle_zombie_servant",
            EntityType.Builder.of(JungleZombieServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<FrayedServant>> FRAYED_SERVANT = register("frayed_servant",
            EntityType.Builder.of(FrayedServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BlackguardServant>> BLACKGUARD_SERVANT = register("blackguard_servant",
            EntityType.Builder.of(BlackguardServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletonServant>> SKELETON_SERVANT = register("skeleton_servant",
            EntityType.Builder.of(SkeletonServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<StrayServant>> STRAY_SERVANT = register("stray_servant",
            EntityType.Builder.of(StrayServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<WitherSkeletonServant>> WITHER_SKELETON_SERVANT = register("wither_skeleton_servant",
            EntityType.Builder.of(WitherSkeletonServant::new, MobCategory.MONSTER)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE)
                    .sized(0.7F, 2.4F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MossySkeletonServant>> MOSSY_SKELETON_SERVANT = register("mossy_skeleton_servant",
            EntityType.Builder.of(MossySkeletonServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SunkenSkeletonServant>> SUNKEN_SKELETON_SERVANT = register("sunken_skeleton_servant",
            EntityType.Builder.of(SunkenSkeletonServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<RattledServant>> RATTLED_SERVANT = register("rattled_servant",
            EntityType.Builder.of(RattledServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<NecromancerServant>> NECROMANCER_SERVANT = register("necromancer_servant",
            EntityType.Builder.of(NecromancerServant::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<CairnNecromancerServant>> CAIRN_NECROMANCER_SERVANT = register("cairn_necromancer_servant",
            EntityType.Builder.of(CairnNecromancerServant::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MossyNecromancerServant>> MOSSY_NECROMANCER_SERVANT = register("mossy_necromancer_servant",
            EntityType.Builder.of(MossyNecromancerServant::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<DrownedNecromancer>> DROWNED_NECROMANCER_SERVANT = register("drowned_necromancer_servant",
            EntityType.Builder.of(DrownedNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.8875F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<WitherNecromancerServant>> WITHER_NECROMANCER_SERVANT = register("wither_necromancer_servant",
            EntityType.Builder.of(WitherNecromancerServant::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.8875F)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<VanguardServant>> VANGUARD_SERVANT = register("vanguard_servant",
            EntityType.Builder.of(VanguardServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<ReaperServant>> REAPER_SERVANT = register("reaper_servant",
            EntityType.Builder.of(ReaperServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<WraithServant>> WRAITH_SERVANT = register("wraith_servant",
            EntityType.Builder.of(WraithServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BorderWraithServant>> BORDER_WRAITH_SERVANT = register("border_wraith_servant",
            EntityType.Builder.of(BorderWraithServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MuckWraithServant>> MUCK_WRAITH_SERVANT = register("muck_wraith_servant",
            EntityType.Builder.of(MuckWraithServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<PhantomServant>> PHANTOM_SERVANT = register("phantom_servant",
            EntityType.Builder.of(PhantomServant::new, MobCategory.MONSTER)
                    .sized(0.9F, 0.5F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletonPillagerServant>> SKELETON_PILLAGER_SERVANT = register("skeleton_pillager",
            EntityType.Builder.of(SkeletonPillagerServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<ZombieVindicatorServant>> ZOMBIE_VINDICATOR_SERVANT = register("zombie_vindicator",
            EntityType.Builder.of(ZombieVindicatorServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BoundEvoker>> BOUND_EVOKER = register("bound_evoker",
            EntityType.Builder.of(BoundEvoker::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BoundGeomancer>> BOUND_GEOMANCER = register("bound_geomancer",
            EntityType.Builder.of(BoundGeomancer::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<BoundIceologer>> BOUND_ICEOLOGER = register("bound_iceologer",
            EntityType.Builder.of(BoundIceologer::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BoundCryologer>> BOUND_CRYOLOGER = register("bound_cryologer",
            EntityType.Builder.of(BoundCryologer::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<BoundWindCaller>> BOUND_WIND_CALLER = register("bound_wind_caller",
            EntityType.Builder.of(BoundWindCaller::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<BoundStormCaster>> BOUND_STORM_CASTER = register("bound_storm_caster",
            EntityType.Builder.of(BoundStormCaster::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntedArmorServant>> HAUNTED_ARMOR_SERVANT = register("haunted_armor_servant",
            EntityType.Builder.of(HauntedArmorServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntedSkull>> HAUNTED_SKULL = register("haunted_skull",
            EntityType.Builder.of(HauntedSkull::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<SpriteMob>> SPRITE = register("sprite",
            EntityType.Builder.of(SpriteMob::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BurningHoglin>> BURNING_HOGLIN = register("burning_hoglin",
            EntityType.Builder.of(BurningHoglin::new, MobCategory.MONSTER)
                    .sized(1.05F, 1.05F)
                    .clientTrackingRange(8)
                    .fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<Doppelganger>> DOPPELGANGER = register("doppelganger",
            EntityType.Builder.of(Doppelganger::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<MiniGhast>> MINI_GHAST = register("mini_ghast",
            EntityType.Builder.of(MiniGhast::new, MobCategory.MONSTER)
                    .sized(1.2F, 1.2F)
                    .clientTrackingRange(10)
                    .fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<GhastServant>> GHAST_SERVANT = register("ghast_servant",
            EntityType.Builder.of(GhastServant::new, MobCategory.MONSTER)
                    .sized(4.0F, 4.0F)
                    .clientTrackingRange(10)
                    .fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<BlazeServant>> BLAZE_SERVANT = register("blaze_servant",
            EntityType.Builder.of(BlazeServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<Wildfire>> WILDFIRE = register("wildfire",
            EntityType.Builder.of(Wildfire::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.25F)
                    .clientTrackingRange(10)
                    .fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<SlimeServant>> SLIME_SERVANT = register("slime_servant",
            EntityType.Builder.of(SlimeServant::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<MagmaCubeServant>> MAGMA_CUBE_SERVANT = register("magma_cube_servant",
            EntityType.Builder.of(MagmaCubeServant::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .fireImmune()
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<CryptSlimeServant>> CRYPT_SLIME_SERVANT = register("crypt_slime_servant",
            EntityType.Builder.of(CryptSlimeServant::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<TropicalSlimeServant>> TROPICAL_SLIME_SERVANT = register("tropical_slime_servant",
            EntityType.Builder.of(TropicalSlimeServant::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<SpiderServant>> SPIDER_SERVANT = register("spider_servant",
            EntityType.Builder.of(SpiderServant::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<CaveSpiderServant>> CAVE_SPIDER_SERVANT = register("cave_spider_servant",
            EntityType.Builder.of(CaveSpiderServant::new, MobCategory.MONSTER)
                    .sized(0.7F, 0.5F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<WebSpiderServant>> WEB_SPIDER_SERVANT = register("web_spider_servant",
            EntityType.Builder.of(WebSpiderServant::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<IcySpiderServant>> ICY_SPIDER_SERVANT = register("icy_spider_servant",
            EntityType.Builder.of(IcySpiderServant::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BoneSpiderServant>> BONE_SPIDER_SERVANT = register("bone_spider_servant",
            EntityType.Builder.of(BoneSpiderServant::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BroodMotherServant>> BROOD_MOTHER_SERVANT = register("brood_mother_servant",
            EntityType.Builder.of(BroodMotherServant::new, MobCategory.MONSTER)
                    .sized(3.2F, 1.8F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Prisoner>> PRISONER = register("prisoner",
            EntityType.Builder.of(Prisoner::new, MobCategory.CREATURE)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Neollager>> NEOLLAGER = register("neollager",
            EntityType.Builder.of(Neollager::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<PillagerServant>> PILLAGER_SERVANT = register("pillager_servant",
            EntityType.Builder.of(PillagerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<PikerServant>> PIKER_SERVANT = register("piker_servant",
            EntityType.Builder.of(PikerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SignalerServant>> SIGNALER_SERVANT = register("signaler_servant",
            EntityType.Builder.of(SignalerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<VindicatorServant>> VINDICATOR_SERVANT = register("vindicator_servant",
            EntityType.Builder.of(VindicatorServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<VindicatorChefServant>> VINDICATOR_CHEF_SERVANT = register("vindicator_chef_servant",
            EntityType.Builder.of(VindicatorChefServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<MountaineerServant>> MOUNTAINEER_SERVANT = register("mountaineer_servant",
            EntityType.Builder.of(MountaineerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<CrusherServant>> CRUSHER_SERVANT = register("crusher_servant",
            EntityType.Builder.of(CrusherServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<EvokerServant>> EVOKER_SERVANT = register("evoker_servant",
            EntityType.Builder.of(EvokerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<GeomancerServant>> GEOMANCER_SERVANT = register("geomancer_servant",
            EntityType.Builder.of(GeomancerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<IceologerServant>> ICEOLOGER_SERVANT = register("iceologer_servant",
            EntityType.Builder.of(IceologerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<CryologerServant>> CRYOLOGER_SERVANT = register("cryologer_servant",
            EntityType.Builder.of(CryologerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<WindCallerServant>> WIND_CALLER_SERVANT = register("wind_caller_servant",
            EntityType.Builder.of(WindCallerServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<StormCasterServant>> STORM_CASTER_SERVANT = register("storm_caster_servant",
            EntityType.Builder.of(StormCasterServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<RipperServant>> RIPPER_SERVANT = register("ripper_servant",
            EntityType.Builder.of(RipperServant::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<AllyTrampler>> TRAMPLER_SERVANT = register("trampler_servant",
            EntityType.Builder.of(AllyTrampler::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(1.3964844F, 1.6F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Ravaged>> RAVAGED = register("ravaged",
            EntityType.Builder.of(Ravaged::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<ModRavager>> MOD_RAVAGER = register("ravager",
            EntityType.Builder.of(ModRavager::new, MobCategory.MONSTER)
                    .sized(1.95F, 2.2F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<ArmoredRavager>> ARMORED_RAVAGER = register("armored_ravager",
            EntityType.Builder.of(ArmoredRavager::new, MobCategory.MONSTER)
                    .sized(1.95F, 2.2F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<ZombieRavager>> ZOMBIE_RAVAGER = register("zombie_ravager",
            EntityType.Builder.of(ZombieRavager::new, MobCategory.MONSTER)
                    .sized(1.95F, 2.2F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<WitchServant>> WITCH_SERVANT = register("witch_servant",
            EntityType.Builder.of(WitchServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<WarlockServant>> WARLOCK_SERVANT = register("warlock_servant",
            EntityType.Builder.of(WarlockServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<HereticServant>> HERETIC_SERVANT = register("heretic_servant",
            EntityType.Builder.of(HereticServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MaverickServant>> MAVERICK_SERVANT = register("maverick_servant",
            EntityType.Builder.of(MaverickServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<ReprobateServant>> REPROBATE_SERVANT = register("reprobate_servant",
            EntityType.Builder.of(ReprobateServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BlackWolf>> BLACK_WOLF = register("black_wolf",
            EntityType.Builder.of(BlackWolf::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletonWolf>> SKELETON_WOLF = register("skeleton_wolf",
            EntityType.Builder.of(SkeletonWolf::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<WinterWolf>> WINTER_WOLF = register("winter_wolf",
            EntityType.Builder.of(WinterWolf::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Stormhound>> STORMHOUND = register("stormhound",
            EntityType.Builder.of(Stormhound::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Hellhound>> HELLHOUND = register("hellhound",
            EntityType.Builder.of(Hellhound::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.85F)
                    .fireImmune()
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<TwilightGoat>> TWILIGHT_GOAT = register("twilight_goat",
            EntityType.Builder.of(TwilightGoat::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.3F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Snapper>> SNAPPER = register("snapper",
            EntityType.Builder.of(Snapper::new, MobCategory.MONSTER)
                    .sized(0.85F, 0.6F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Gnasher>> GNASHER = register("gnasher",
            EntityType.Builder.of(Gnasher::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<GuardianServant>> GUARDIAN_SERVANT = register("guardian_servant",
            EntityType.Builder.of(GuardianServant::new, MobCategory.MONSTER)
                    .sized(0.85F, 0.85F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BearServant>> BEAR_SERVANT = register("bear_servant",
            EntityType.Builder.of(BearServant::new, MobCategory.MONSTER)
                    .sized(1.4F, 1.4F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<BearServant>> POLAR_BEAR_SERVANT = register("polar_bear_servant",
            EntityType.Builder.of(BearServant::new, MobCategory.MONSTER)
                    .immuneTo(Blocks.POWDER_SNOW)
                    .sized(1.4F, 1.4F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<HoglinServant>> HOGLIN_SERVANT = register("hoglin_servant",
            EntityType.Builder.of(HoglinServant::new, MobCategory.MONSTER)
                    .sized(1.3964844F, 1.4F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BlackBeast>> BLACK_BEAST = register("black_beast",
            EntityType.Builder.of(BlackBeast::new, MobCategory.MONSTER)
                    .sized(0.7F, 1.8F)
                    .clientTrackingRange(20));

    public static final DeferredHolder<EntityType<?>, EntityType<Whisperer>> WHISPERER = register("whisperer",
            EntityType.Builder.of(Whisperer::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Wavewhisperer>> WAVEWHISPERER = register("wavewhisperer",
            EntityType.Builder.of(Wavewhisperer::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Leapleaf>> LEAPLEAF = register("leapleaf",
            EntityType.Builder.of(Leapleaf::new, MobCategory.MONSTER)
                    .sized(1.9F, 1.9F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<StoneMinistrosity>> STONE_MINISTROSITY = register("stone_ministrosity",
            EntityType.Builder.of(StoneMinistrosity::new, MobCategory.MONSTER)
                    .sized(0.5F, 1.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<RedstoneMinistrosity>> REDSTONE_MINISTROSITY = register("redstone_ministrosity",
            EntityType.Builder.of(RedstoneMinistrosity::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.5F, 1.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<IceGolem>> ICE_GOLEM = register("ice_golem",
            EntityType.Builder.of(IceGolem::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.5F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<SquallGolem>> SQUALL_GOLEM = register("squall_golem",
            EntityType.Builder.of(SquallGolem::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.5F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<RedstoneGolem>> REDSTONE_GOLEM = register("redstone_golem",
            EntityType.Builder.of(RedstoneGolem::new, MobCategory.MONSTER)
                    .sized(2.7F, 3.9F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<GraveGolem>> GRAVE_GOLEM = register("grave_golem",
            EntityType.Builder.of(GraveGolem::new, MobCategory.MONSTER)
                    .sized(2.7F, 3.9F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Haunt>> HAUNT = register("haunt",
            EntityType.Builder.of(Haunt::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.8F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<RedstoneMonstrosity>> REDSTONE_MONSTROSITY = register("redstone_monstrosity",
            EntityType.Builder.of(RedstoneMonstrosity::new, MobCategory.MONSTER)
                    .sized(4.0F, 5.4F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<RedstoneCube>> REDSTONE_CUBE = register("redstone_cube",
            EntityType.Builder.of(RedstoneCube::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<WatchlingServant>> WATCHLING_SERVANT = register("watchling_servant",
            EntityType.Builder.of(WatchlingServant::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.6F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BlastlingServant>> BLASTLING_SERVANT = register("blastling_servant",
            EntityType.Builder.of(BlastlingServant::new, MobCategory.MONSTER)
                    .sized(1.2F, 2.6F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SnarelingServant>> SNARELING_SERVANT = register("snareling_servant",
            EntityType.Builder.of(SnarelingServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.6F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Sorcerer>> SORCERER = register("sorcerer",
            EntityType.Builder.of(Sorcerer::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Envioker>> ENVIOKER = register("envioker",
            EntityType.Builder.of(Envioker::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Tormentor>> TORMENTOR = register("tormentor",
            EntityType.Builder.of(Tormentor::new, MobCategory.MONSTER)
                    .fireImmune()
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Inquillager>> INQUILLAGER = register("inquillager",
            EntityType.Builder.of(Inquillager::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Conquillager>> CONQUILLAGER = register("conquillager",
            EntityType.Builder.of(Conquillager::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Piker>> PIKER = register("piker",
            EntityType.Builder.of(Piker::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Ripper>> RIPPER = register("ripper",
            EntityType.Builder.of(Ripper::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Trampler>> TRAMPLER = register("trampler",
            EntityType.Builder.of(Trampler::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(1.3964844F, 1.6F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Crusher>> CRUSHER = register("crusher",
            EntityType.Builder.of(Crusher::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<StormCaster>> STORM_CASTER = register("storm_caster",
            EntityType.Builder.of(StormCaster::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Cryologer>> CRYOLOGER = register("cryologer",
            EntityType.Builder.of(Cryologer::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Preacher>> PREACHER = register("preacher",
            EntityType.Builder.of(Preacher::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Minister>> MINISTER = register("minister",
            EntityType.Builder.of(Minister::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<HostileRedstoneGolem>> HOSTILE_REDSTONE_GOLEM = register("hostile_redstone_golem",
            EntityType.Builder.of(HostileRedstoneGolem::new, MobCategory.MONSTER)
                    .sized(2.7F, 3.9F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<HostileRedstoneMonstrosity>> HOSTILE_REDSTONE_MONSTROSITY = register("hostile_redstone_monstrosity",
            EntityType.Builder.of(HostileRedstoneMonstrosity::new, MobCategory.MONSTER)
                    .sized(4.0F, 5.4F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<Vizier>> VIZIER = register("vizier",
            EntityType.Builder.of(Vizier::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<VizierClone>> VIZIER_CLONE = register("vizier_clone",
            EntityType.Builder.of(VizierClone::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Irk>> IRK = register("irk",
            EntityType.Builder.of(Irk::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<Wight>> WIGHT = register("wight",
            EntityType.Builder.of(Wight::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.9F)
                    .clientTrackingRange(16));

    public static final DeferredHolder<EntityType<?>, EntityType<CarrionMaggot>> CARRION_MAGGOT = register("carrion_maggot",
            EntityType.Builder.of(CarrionMaggot::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.3F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<CarrionFly>> CARRION_FLY = register("carrion_fly",
            EntityType.Builder.of(CarrionFly::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.5F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SkullLord>> SKULL_LORD = register("skull_lord",
            EntityType.Builder.of(SkullLord::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<BoneLord>> BONE_LORD = register("bone_lord",
            EntityType.Builder.of(BoneLord::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<WitherNecromancer>> WITHER_NECROMANCER = register("wither_necromancer",
            EntityType.Builder.of(WitherNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.8875F)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE)
                    .clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<LightningTrap>> LIGHTNING_TRAP = register("lightning_trap",
            EntityType.Builder.<LightningTrap>of(LightningTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicLightningTrap>> MAGIC_LIGHTNING_TRAP = register("magic_lightning_trap",
            EntityType.Builder.<MagicLightningTrap>of(MagicLightningTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<VoidLightningTrap>> VOID_LIGHTNING_TRAP = register("void_lightning_trap",
            EntityType.Builder.<VoidLightningTrap>of(VoidLightningTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<FireRainTrap>> FIRE_RAIN_TRAP = register("fire_rain_trap",
            EntityType.Builder.<FireRainTrap>of(FireRainTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<ArrowRainTrap>> ARROW_RAIN_TRAP = register("arrow_rain_trap",
            EntityType.Builder.<ArrowRainTrap>of(ArrowRainTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<FireTornadoTrap>> FIRE_TORNADO_TRAP = register("fire_tornado_trap",
            EntityType.Builder.<FireTornadoTrap>of(FireTornadoTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<FireBlastTrap>> FIRE_BLAST_TRAP = register("fire_blast_trap",
            EntityType.Builder.<FireBlastTrap>of(FireBlastTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<UpdraftBlast>> UPDRAFT_BLAST = register("updraft_blast",
            EntityType.Builder.<UpdraftBlast>of(UpdraftBlast::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<EffectBlastTrap>> EFFECT_BLAST_TRAP = register("effect_blast_trap",
            EntityType.Builder.<EffectBlastTrap>of(EffectBlastTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<Cushion>> CUSHION = register("cushion",
            EntityType.Builder.<Cushion>of(Cushion::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicGround>> MAGIC_GROUND = register("magic_ground",
            EntityType.Builder.<MagicGround>of(MagicGround::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.25F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<AcidPool>> ACID_POOL = register("acid_pool",
            EntityType.Builder.of(AcidPool::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<FirePillar>> FIRE_PILLAR = register("fire_pillar",
            EntityType.Builder.<FirePillar>of(FirePillar::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.25F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<VoidRift>> VOID_RIFT = register("void_rift",
            EntityType.Builder.<VoidRift>of(VoidRift::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 2.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<TridentStorm>> TRIDENT_STORM = register("trident_storm",
            EntityType.Builder.<TridentStorm>of(TridentStorm::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(4.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<DelayedSummon>> DELAYED_SUMMON = register("delayed_summon",
            EntityType.Builder.<DelayedSummon>of(DelayedSummon::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SummonCircle>> SUMMON_CIRCLE = register("summon_circle",
            EntityType.Builder.<SummonCircle>of(SummonCircle::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SummonCircleBoss>> SUMMON_CIRCLE_BOSS = register("summon_circle_boss",
            EntityType.Builder.<SummonCircleBoss>of(SummonCircleBoss::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SummonCircleVariant>> SUMMON_FIERY = register("summon_fiery",
            EntityType.Builder.<SummonCircleVariant>of(SummonCircleVariant::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<RaidBossSummon>> RAID_BOSS_SUMMON = register("raid_boss_summon",
            EntityType.Builder.of(RaidBossSummon::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<StormEntity>> STORM_UTIL = register("storm_util",
            EntityType.Builder.of(StormEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<SummonApostle>> SUMMON_APOSTLE = register("summon_apostle",
            EntityType.Builder.of(SummonApostle::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 1.95F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<HailCloud>> HAIL_CLOUD = register("hail_cloud",
            EntityType.Builder.<HailCloud>of(HailCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<MonsoonCloud>> MONSOON_CLOUD = register("monsoon_cloud",
            EntityType.Builder.<MonsoonCloud>of(MonsoonCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<HellCloud>> HELL_CLOUD = register("hell_cloud",
            EntityType.Builder.<HellCloud>of(HellCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SpellLightningBolt>> SPELL_LIGHTNING_BOLT = register("spell_lightning_bolt",
            EntityType.Builder.of(SpellLightningBolt::new, MobCategory.MISC)
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(16)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<AlliedEffectCloud>> ALLIED_EFFECT_CLOUD = register("allied_effect_cloud",
            EntityType.Builder.<AlliedEffectCloud>of(AlliedEffectCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<BrewEffectCloud>> BREW_EFFECT_CLOUD = register("brew_effect_cloud",
            EntityType.Builder.<BrewEffectCloud>of(BrewEffectCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<BrewGas>> BREW_EFFECT_GAS = register("brew_effect_gas",
            EntityType.Builder.<BrewGas>of(BrewGas::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<DragonBreathCloud>> DRAGON_BREATH_CLOUD = register("dragon_breath_cloud",
            EntityType.Builder.<DragonBreathCloud>of(DragonBreathCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<VineHook>> VINE_HOOK = register("vine_hook",
            EntityType.Builder.<VineHook>of(VineHook::new, MobCategory.MISC)
                    .noSave()
                    .noSummon()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5));

    public static final DeferredHolder<EntityType<?>, EntityType<SurveyEye>> SURVEY_EYE = register("survey_eye",
            EntityType.Builder.of(SurveyEye::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<CameraShake>> CAMERA_SHAKE = register("camera_shake",
            EntityType.Builder.<CameraShake>of(CameraShake::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(1.0F, 1.0F)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT = register("seat",
            EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .clientTrackingRange(5)
                    .updateInterval(Integer.MAX_VALUE)
                    .setShouldReceiveVelocityUpdates(false)
                    .sized(0.25F, 0.35F));

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String p_20635_, EntityType.Builder<T> p_20636_) {
        return ENTITY_TYPE.register(p_20635_, () -> p_20636_.build(Goety.location(p_20635_).toString()));
    }
}
