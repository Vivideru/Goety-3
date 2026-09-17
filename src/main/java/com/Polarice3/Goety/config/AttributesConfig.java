package com.Polarice3.Goety.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AttributesConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Double> SkeletonVillagerServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonVillagerServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> WarlockHealth;
    public static final ModConfigSpec.ConfigValue<Double> WarlockArmor;
    public static final ModConfigSpec.ConfigValue<Double> MaverickHealth;
    public static final ModConfigSpec.ConfigValue<Double> MaverickArmor;
    public static final ModConfigSpec.ConfigValue<Double> MaverickDamage;
    public static final ModConfigSpec.ConfigValue<Double> HereticHealth;
    public static final ModConfigSpec.ConfigValue<Double> HereticArmor;
    public static final ModConfigSpec.ConfigValue<Double> HereticDamage;
    public static final ModConfigSpec.ConfigValue<Double> ReprobateHealth;
    public static final ModConfigSpec.ConfigValue<Double> ReprobateArmor;
    public static final ModConfigSpec.ConfigValue<Double> ZPiglinServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> ZPiglinServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> ZPiglinServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> ZPiglinBruteServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> ZPiglinBruteServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> ZPiglinBruteServantDamage;

    public static final ModConfigSpec.ConfigValue<Double> ObsidianMonolithHealth;
    public static final ModConfigSpec.ConfigValue<Double> ObsidianMonolithArmor;

    public static final ModConfigSpec.ConfigValue<Double> BlazeServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> BlazeServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> BlazeServantMeleeDamage;
    public static final ModConfigSpec.ConfigValue<Double> BlazeServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> WildfireHealth;
    public static final ModConfigSpec.ConfigValue<Double> WildfireShieldHealth;
    public static final ModConfigSpec.ConfigValue<Double> WildfireArmor;
    public static final ModConfigSpec.ConfigValue<Double> WildfireMeleeDamage;
    public static final ModConfigSpec.ConfigValue<Double> WildfireRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> InfernoHealth;
    public static final ModConfigSpec.ConfigValue<Double> InfernoArmor;
    public static final ModConfigSpec.ConfigValue<Double> InfernoMeleeDamage;
    public static final ModConfigSpec.ConfigValue<Double> InfernoRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> MalghastHealth;
    public static final ModConfigSpec.ConfigValue<Double> MalghastDamage;

    public static final ModConfigSpec.ConfigValue<Double> SorcererHealth;
    public static final ModConfigSpec.ConfigValue<Double> SorcererArmor;
    public static final ModConfigSpec.ConfigValue<Double> SorcererDamage;
    public static final ModConfigSpec.ConfigValue<Double> EnviokerHealth;
    public static final ModConfigSpec.ConfigValue<Double> EnviokerArmor;
    public static final ModConfigSpec.ConfigValue<Double> EnviokerDamage;
    public static final ModConfigSpec.ConfigValue<Double> TormentorHealth;
    public static final ModConfigSpec.ConfigValue<Double> TormentorArmor;
    public static final ModConfigSpec.ConfigValue<Double> TormentorDamage;
    public static final ModConfigSpec.ConfigValue<Double> InquillagerHealth;
    public static final ModConfigSpec.ConfigValue<Double> InquillagerArmor;
    public static final ModConfigSpec.ConfigValue<Double> InquillagerDamage;
    public static final ModConfigSpec.ConfigValue<Double> PikerHealth;
    public static final ModConfigSpec.ConfigValue<Double> PikerArmor;
    public static final ModConfigSpec.ConfigValue<Double> PikerDamage;
    public static final ModConfigSpec.ConfigValue<Double> RipperHealth;
    public static final ModConfigSpec.ConfigValue<Double> RipperArmor;
    public static final ModConfigSpec.ConfigValue<Double> RipperDamage;
    public static final ModConfigSpec.ConfigValue<Double> TramplerHealth;
    public static final ModConfigSpec.ConfigValue<Double> TramplerArmor;
    public static final ModConfigSpec.ConfigValue<Double> TramplerDamage;
    public static final ModConfigSpec.ConfigValue<Double> CrusherHealth;
    public static final ModConfigSpec.ConfigValue<Double> CrusherArmor;
    public static final ModConfigSpec.ConfigValue<Double> CrusherDamage;
    public static final ModConfigSpec.ConfigValue<Double> StormCasterHealth;
    public static final ModConfigSpec.ConfigValue<Double> StormCasterArmor;
    public static final ModConfigSpec.ConfigValue<Double> StormCasterDamage;
    public static final ModConfigSpec.ConfigValue<Double> CryologerHealth;
    public static final ModConfigSpec.ConfigValue<Double> CryologerArmor;
    public static final ModConfigSpec.ConfigValue<Double> CryologerDamage;
    public static final ModConfigSpec.ConfigValue<Double> PreacherHealth;
    public static final ModConfigSpec.ConfigValue<Double> PreacherArmor;
    public static final ModConfigSpec.ConfigValue<Double> PreacherHeal;
    public static final ModConfigSpec.ConfigValue<Double> ConquillagerHealth;
    public static final ModConfigSpec.ConfigValue<Double> ConquillagerArmor;

    public static final ModConfigSpec.ConfigValue<Double> ReaperHealth;
    public static final ModConfigSpec.ConfigValue<Double> ReaperArmor;
    public static final ModConfigSpec.ConfigValue<Double> ReaperDamage;
    public static final ModConfigSpec.ConfigValue<Double> WraithHealth;
    public static final ModConfigSpec.ConfigValue<Double> WraithArmor;
    public static final ModConfigSpec.ConfigValue<Double> WraithDamage;
    public static final ModConfigSpec.ConfigValue<Double> NecromancerHealth;
    public static final ModConfigSpec.ConfigValue<Double> NecromancerArmor;
    public static final ModConfigSpec.ConfigValue<Double> NecromancerFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> NecromancerDamage;
    public static final ModConfigSpec.ConfigValue<Double> CairnNecromancerHealth;
    public static final ModConfigSpec.ConfigValue<Double> CairnNecromancerArmor;
    public static final ModConfigSpec.ConfigValue<Double> CairnNecromancerFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> CairnNecromancerDamage;
    public static final ModConfigSpec.ConfigValue<Double> MossyNecromancerHealth;
    public static final ModConfigSpec.ConfigValue<Double> MossyNecromancerArmor;
    public static final ModConfigSpec.ConfigValue<Double> MossyNecromancerFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> MossyNecromancerDamage;
    public static final ModConfigSpec.ConfigValue<Double> DrownedNecromancerHealth;
    public static final ModConfigSpec.ConfigValue<Double> DrownedNecromancerArmor;
    public static final ModConfigSpec.ConfigValue<Double> DrownedNecromancerFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> DrownedNecromancerDamage;
    public static final ModConfigSpec.ConfigValue<Double> WitherNecromancerHealth;
    public static final ModConfigSpec.ConfigValue<Double> WitherNecromancerArmor;
    public static final ModConfigSpec.ConfigValue<Double> WitherNecromancerFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> WitherNecromancerDamage;
    public static final ModConfigSpec.ConfigValue<Double> HauntedArmorHealth;
    public static final ModConfigSpec.ConfigValue<Double> HauntedArmorDamage;

    public static final ModConfigSpec.ConfigValue<Double> ZombieServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> ZombieServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> ZombieServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> HuskServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> HuskServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> HuskServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> DrownedServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> DrownedServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> DrownedServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> FrozenZombieServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> FrozenZombieServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> FrozenZombieServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> JungleZombieServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> JungleZombieServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> JungleZombieServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> FrayedServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> FrayedServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> FrayedServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> BlackguardServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> BlackguardServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> BlackguardServantToughness;
    public static final ModConfigSpec.ConfigValue<Double> BlackguardServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> RoyalGuardServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> RoyalGuardServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> RoyalGuardServantToughness;
    public static final ModConfigSpec.ConfigValue<Double> RoyalGuardServantDamage;
    public static final ModConfigSpec.ConfigValue<Integer> RoyalGuardServantShield;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> StrayServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> StrayServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> StrayServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> StrayServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> WitherSkeletonServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> WitherSkeletonServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> WitherSkeletonServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> WitherSkeletonServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> MossySkeletonServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> MossySkeletonServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> MossySkeletonServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> MossySkeletonServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> SunkenSkeletonServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> SunkenSkeletonServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> SunkenSkeletonServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> SunkenSkeletonServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> RattledServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> RattledServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> RattledServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> RattledServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> VanguardServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> VanguardServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> VanguardServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> PhantomServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> PhantomServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> PhantomServantInitialDamage;
    public static final ModConfigSpec.ConfigValue<Double> PhantomServantSizeDamage;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonPillagerHealth;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonPillagerArmor;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonPillagerDamage;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonPillagerRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> ZombieVindicatorHealth;
    public static final ModConfigSpec.ConfigValue<Double> ZombieVindicatorArmor;
    public static final ModConfigSpec.ConfigValue<Double> ZombieVindicatorDamage;
    public static final ModConfigSpec.ConfigValue<Double> BoundEvokerHealth;
    public static final ModConfigSpec.ConfigValue<Double> BoundEvokerArmor;
    public static final ModConfigSpec.ConfigValue<Double> BoundEvokerFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> BoundIceologerHealth;
    public static final ModConfigSpec.ConfigValue<Double> BoundIceologerArmor;
    public static final ModConfigSpec.ConfigValue<Double> BoundIceologerFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> NeollagerHealth;
    public static final ModConfigSpec.ConfigValue<Double> NeollagerArmor;
    public static final ModConfigSpec.ConfigValue<Double> PillagerServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> PillagerServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> PillagerServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> PillagerServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> SignalerServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> SignalerServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> SignalerServantFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> VindicatorServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> VindicatorServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> VindicatorServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> MountaineerServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> MountaineerServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> MountaineerServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> EvokerServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> EvokerServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> EvokerServantFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> GeomancerServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> GeomancerServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> GeomancerServantFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> IceologerServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> IceologerServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> IceologerServantFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> WindCallerServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> WindCallerServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> WindCallerServantFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> WitchServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> WitchServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> WitchServantFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> SummonedVexHealth;
    public static final ModConfigSpec.ConfigValue<Double> SummonedVexDamage;
    public static final ModConfigSpec.ConfigValue<Double> SummonedIrkHealth;
    public static final ModConfigSpec.ConfigValue<Double> SummonedIrkDamage;
    public static final ModConfigSpec.ConfigValue<Double> SpriteHealth;
    public static final ModConfigSpec.ConfigValue<Double> SpriteDamage;
    public static final ModConfigSpec.ConfigValue<Double> MiniGhastHealth;
    public static final ModConfigSpec.ConfigValue<Double> MiniGhastDamage;
    public static final ModConfigSpec.ConfigValue<Double> GhastServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> GhastServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> SpiderServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> SpiderServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> CaveSpiderServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> CaveSpiderServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> WebSpiderServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> WebSpiderServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> IcySpiderServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> IcySpiderServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> BoneSpiderServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> BoneSpiderServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> BoneSpiderServantRangeDamage;
    public static final ModConfigSpec.ConfigValue<Double> BroodMotherHealth;
    public static final ModConfigSpec.ConfigValue<Double> BroodMotherArmor;
    public static final ModConfigSpec.ConfigValue<Double> BroodMotherDamage;
    public static final ModConfigSpec.ConfigValue<Double> BlackWolfHealth;
    public static final ModConfigSpec.ConfigValue<Double> BlackWolfArmor;
    public static final ModConfigSpec.ConfigValue<Double> BlackWolfDamage;
    public static final ModConfigSpec.ConfigValue<Double> WargHealth;
    public static final ModConfigSpec.ConfigValue<Double> WargArmor;
    public static final ModConfigSpec.ConfigValue<Double> WargDamage;
    public static final ModConfigSpec.ConfigValue<Double> CerberusHealth;
    public static final ModConfigSpec.ConfigValue<Double> CerberusArmor;
    public static final ModConfigSpec.ConfigValue<Double> CerberusDamage;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonWolfHealth;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonWolfArmor;
    public static final ModConfigSpec.ConfigValue<Double> SkeletonWolfDamage;
    public static final ModConfigSpec.ConfigValue<Double> TwilightGoatHealth;
    public static final ModConfigSpec.ConfigValue<Double> TwilightGoatArmor;
    public static final ModConfigSpec.ConfigValue<Double> TwilightGoatDamage;
    public static final ModConfigSpec.ConfigValue<Double> SnapperHealth;
    public static final ModConfigSpec.ConfigValue<Double> SnapperArmor;
    public static final ModConfigSpec.ConfigValue<Double> SnapperDamage;
    public static final ModConfigSpec.ConfigValue<Double> GnasherHealth;
    public static final ModConfigSpec.ConfigValue<Double> GnasherArmor;
    public static final ModConfigSpec.ConfigValue<Double> GnasherDamage;
    public static final ModConfigSpec.ConfigValue<Double> GuardianHealth;
    public static final ModConfigSpec.ConfigValue<Double> GuardianArmor;
    public static final ModConfigSpec.ConfigValue<Double> GuardianDamage;
    public static final ModConfigSpec.ConfigValue<Double> ElderGuardianHealth;
    public static final ModConfigSpec.ConfigValue<Double> ElderGuardianArmor;
    public static final ModConfigSpec.ConfigValue<Double> ElderGuardianDamage;
    public static final ModConfigSpec.ConfigValue<Double> BearServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> BearServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> BearServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> HoglinServantHealth;
    public static final ModConfigSpec.ConfigValue<Double> HoglinServantArmor;
    public static final ModConfigSpec.ConfigValue<Double> HoglinServantDamage;
    public static final ModConfigSpec.ConfigValue<Double> BlackBeastHealth;
    public static final ModConfigSpec.ConfigValue<Double> BlackBeastArmor;
    public static final ModConfigSpec.ConfigValue<Double> BlackBeastDamage;
    public static final ModConfigSpec.ConfigValue<Double> CarrionMaggotHealth;
    public static final ModConfigSpec.ConfigValue<Double> CarrionMaggotArmor;
    public static final ModConfigSpec.ConfigValue<Double> CarrionMaggotDamage;
    public static final ModConfigSpec.ConfigValue<Double> CarrionFlyHealth;
    public static final ModConfigSpec.ConfigValue<Double> CarrionFlyArmor;
    public static final ModConfigSpec.ConfigValue<Double> CarrionFlyDamage;
    public static final ModConfigSpec.ConfigValue<Double> WhispererHealth;
    public static final ModConfigSpec.ConfigValue<Double> WhispererArmor;
    public static final ModConfigSpec.ConfigValue<Double> WhispererDamage;
    public static final ModConfigSpec.ConfigValue<Double> LeapleafHealth;
    public static final ModConfigSpec.ConfigValue<Double> LeapleafArmor;
    public static final ModConfigSpec.ConfigValue<Double> LeapleafDamage;
    public static final ModConfigSpec.ConfigValue<Double> QuickGrowingVineHealth;
    public static final ModConfigSpec.ConfigValue<Double> QuickGrowingVineArmor;
    public static final ModConfigSpec.ConfigValue<Double> PoisonQuillVineHealth;
    public static final ModConfigSpec.ConfigValue<Double> PoisonQuillVineArmor;
    public static final ModConfigSpec.ConfigValue<Double> PoisonQuillVineDamage;
    public static final ModConfigSpec.ConfigValue<Double> RavagerSaddleHealth;
    public static final ModConfigSpec.ConfigValue<Double> RavagerArmor;
    public static final ModConfigSpec.ConfigValue<Double> RavagerDamage;
    public static final ModConfigSpec.ConfigValue<Double> WatchlingHealth;
    public static final ModConfigSpec.ConfigValue<Double> WatchlingArmor;
    public static final ModConfigSpec.ConfigValue<Double> WatchlingDamage;
    public static final ModConfigSpec.ConfigValue<Double> BlastlingHealth;
    public static final ModConfigSpec.ConfigValue<Double> BlastlingArmor;
    public static final ModConfigSpec.ConfigValue<Double> BlastlingDamage;
    public static final ModConfigSpec.ConfigValue<Double> SnarelingHealth;
    public static final ModConfigSpec.ConfigValue<Double> SnarelingArmor;
    public static final ModConfigSpec.ConfigValue<Double> SnarelingDamage;
    public static final ModConfigSpec.ConfigValue<Double> EndersentHealth;
    public static final ModConfigSpec.ConfigValue<Double> EndersentArmor;
    public static final ModConfigSpec.ConfigValue<Double> EndersentDamage;
    public static final ModConfigSpec.ConfigValue<Double> StoneMinistrosityHealth;
    public static final ModConfigSpec.ConfigValue<Double> StoneMinistrosityArmor;
    public static final ModConfigSpec.ConfigValue<Double> StoneMinistrosityDamage;
    public static final ModConfigSpec.ConfigValue<Double> StoneMinistrosityFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMinistrosityHealth;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMinistrosityArmor;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMinistrosityDamage;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMinistrosityFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> IceGolemHealth;
    public static final ModConfigSpec.ConfigValue<Double> IceGolemArmor;
    public static final ModConfigSpec.ConfigValue<Double> IceGolemDamage;
    public static final ModConfigSpec.ConfigValue<Double> IceGolemFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> SquallGolemHealth;
    public static final ModConfigSpec.ConfigValue<Double> SquallGolemArmor;
    public static final ModConfigSpec.ConfigValue<Double> SquallGolemDamage;
    public static final ModConfigSpec.ConfigValue<Double> SquallGolemFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneGolemHealth;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneGolemArmor;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneGolemDamage;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneGolemFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> GraveGolemHealth;
    public static final ModConfigSpec.ConfigValue<Double> GraveGolemArmor;
    public static final ModConfigSpec.ConfigValue<Double> GraveGolemDamage;
    public static final ModConfigSpec.ConfigValue<Double> GraveGolemFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> HauntHealth;
    public static final ModConfigSpec.ConfigValue<Double> HauntArmor;
    public static final ModConfigSpec.ConfigValue<Double> HauntDamage;
    public static final ModConfigSpec.ConfigValue<Double> HauntFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMonstrosityHealth;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMonstrosityArmor;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMonstrosityDamage;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMonstrosityFollowRange;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMonstrosityHPPercentDamage;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMonstrosityHurtRange;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneMonstrosityDamageCap;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneCubeHealth;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneCubeArmor;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneCubeDamage;
    public static final ModConfigSpec.ConfigValue<Double> RedstoneCubeFollowRange;

    public static final ModConfigSpec.ConfigValue<Double> WightHealth;
    public static final ModConfigSpec.ConfigValue<Double> WightDamage;
    public static final ModConfigSpec.ConfigValue<Integer> WightSoulAbsorb;
    public static final ModConfigSpec.ConfigValue<Integer> WightSoulHeal;

    public static final ModConfigSpec.ConfigValue<Double> SkullLordHealth;
    public static final ModConfigSpec.ConfigValue<Double> SkullLordDamage;
    public static final ModConfigSpec.ConfigValue<Double> BoneLordHealth;
    public static final ModConfigSpec.ConfigValue<Double> BoneLordDamage;

    public static final ModConfigSpec.ConfigValue<Double> ApostleHealth;
    public static final ModConfigSpec.ConfigValue<Double> ApostleArmor;
    public static final ModConfigSpec.ConfigValue<Double> ApostleToughness;
    public static final ModConfigSpec.ConfigValue<Double> ApostleMagicDamage;
    public static final ModConfigSpec.ConfigValue<Double> EnderKeeperHealth;
    public static final ModConfigSpec.ConfigValue<Double> EnderKeeperArmor;
    public static final ModConfigSpec.ConfigValue<Double> EnderKeeperDamage;
    public static final ModConfigSpec.ConfigValue<Double> EnderKeeperHPPercentDamage;
    public static final ModConfigSpec.ConfigValue<Double> EnderKeeperHurtRange;
    public static final ModConfigSpec.ConfigValue<Double> VizierHealth;
    public static final ModConfigSpec.ConfigValue<Double> VizierDamage;
    public static final ModConfigSpec.ConfigValue<Double> CroneHealth;
    public static final ModConfigSpec.ConfigValue<Double> HeresiarchHealth;
    public static final ModConfigSpec.ConfigValue<Double> HeresiarchArmor;
    public static final ModConfigSpec.ConfigValue<Double> HeresiarchDamage;
    public static final ModConfigSpec.ConfigValue<Double> MinisterHealth;
    public static final ModConfigSpec.ConfigValue<Double> MinisterDamage;

    public static final ModConfigSpec.ConfigValue<Double> ApostleDamageCap;
    public static final ModConfigSpec.ConfigValue<Double> VizierDamageCap;
    public static final ModConfigSpec.ConfigValue<Double> EnderKeeperDamageCap;
    public static final ModConfigSpec.ConfigValue<Integer> ApostleBowDamage;

    public static <T> T get(ModConfigSpec.ConfigValue<T> configValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException exception) {
            if (!"Cannot get config value before config is loaded.".equals(exception.getMessage())) {
                throw exception;
            }
            // Entity attribute suppliers are built before NeoForge guarantees common config binding.
            return configValue.getDefault();
        }
    }

    static {
        BUILDER.push("Attributes");
            BUILDER.push("Cultists");
            MalghastHealth = BUILDER.comment("How much Max Health Malghast have, Default: 20.0")
                    .defineInRange("malghastHealth", 20.0, 1.0, Double.MAX_VALUE);
            MalghastDamage = BUILDER.comment("How much damage Malghasts' hell blasts/lavaballs deals, Default: 6.0")
                .defineInRange("malghastDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.push("Skeleton Villager Servant");
                SkeletonVillagerServantHealth = BUILDER.comment("How much Max Health Skeleton Villager Servants have, Default: 20.0")
                        .defineInRange("skeletonVillagerServantHealth", 20.0, 0.0, Double.MAX_VALUE);
                SkeletonVillagerServantDamage = BUILDER.comment("How much damage Skeleton Villager Servants deals, Default: 2.0")
                        .defineInRange("skeletonVillagerServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Warlock");
                WarlockHealth = BUILDER.comment("How much Max Health Warlocks have, Default: 26.0")
                        .defineInRange("warlockHealth", 26.0, 0.0, Double.MAX_VALUE);
                WarlockArmor = BUILDER.comment("How much natural armor points Warlocks have, Default: 0.0")
                        .defineInRange("warlockArmor", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Maverick");
                MaverickHealth = BUILDER.comment("How much Max Health Mavericks have, Default: 26.0")
                        .defineInRange("maverickHealth", 26.0, 0.0, Double.MAX_VALUE);
                MaverickArmor = BUILDER.comment("How much natural armor points Mavericks have, Default: 0.0")
                        .defineInRange("maverickArmor", 0.0, 0.0, Double.MAX_VALUE);
                MaverickDamage = BUILDER.comment("How much damage Mavericks deals, Default: 2.0")
                        .defineInRange("maverickDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Reprobate");
                ReprobateHealth = BUILDER.comment("How much Max Health Reprobates have, Default: 26.0")
                        .defineInRange("reprobateHealth", 26.0, 0.0, Double.MAX_VALUE);
                ReprobateArmor = BUILDER.comment("How much natural armor points Reprobates have, Default: 0.0")
                        .defineInRange("reprobateArmor", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Heretic");
                HereticHealth = BUILDER.comment("How much Max Health Heretics have, Default: 26.0")
                        .defineInRange("hereticHealth", 26.0, 0.0, Double.MAX_VALUE);
                HereticArmor = BUILDER.comment("How much natural armor points Heretics have, Default: 0.0")
                        .defineInRange("hereticArmor", 0.0, 0.0, Double.MAX_VALUE);
                HereticDamage = BUILDER.comment("How much damage Heretics deals, Default: 5.0")
                        .defineInRange("hereticDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Inferno");
                InfernoHealth = BUILDER.comment("How much Max Health Inferno have, Default: 20.0")
                        .defineInRange("infernoHealth", 20.0, 1.0, Double.MAX_VALUE);
                InfernoArmor = BUILDER.comment("How much natural armor points Inferno have, Default: 5.0")
                        .defineInRange("infernoArmor", 5.0, 0.0, Double.MAX_VALUE);
                InfernoMeleeDamage = BUILDER.comment("How much melee damage Inferno deals, Default: 6.0")
                        .defineInRange("infernoMeleeDamage", 6.0, 1.0, Double.MAX_VALUE);
                InfernoRangeDamage = BUILDER.comment("How much range damage Inferno deals, Default: 5.0")
                        .defineInRange("infernoRangeDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Obsidian Monolith");
                ObsidianMonolithHealth = BUILDER.comment("How much Max Health Obsidian Monolith have, Default: 50.0")
                        .defineInRange("obsidianMonolithHealth", 50.0, 1.0, Double.MAX_VALUE);
                ObsidianMonolithArmor = BUILDER.comment("How much natural armor points Obsidian Monolith have, Default: 12.0")
                        .defineInRange("obsidianMonolithArmor", 12.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
            BUILDER.pop();
            BUILDER.push("Illagers");
                BUILDER.push("Sorcerer");
                SorcererHealth = BUILDER.comment("How much Max Health Sorcerers have, Default: 24.0")
                        .defineInRange("sorcererHealth", 24.0, 1.0, Double.MAX_VALUE);
                SorcererArmor = BUILDER.comment("How much natural Armor Sorcerers have, Default: 0.0")
                        .defineInRange("sorcererArmor", 0.0, 0.0, Double.MAX_VALUE);
                SorcererDamage = BUILDER.comment("How much damage Sorcerers deals, Default: 5.0")
                        .defineInRange("sorcererDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Envioker");
                EnviokerHealth = BUILDER.comment("How much Max Health Envioker have, Default: 24.0")
                        .defineInRange("enviokerHealth", 24.0, 1.0, Double.MAX_VALUE);
                EnviokerArmor = BUILDER.comment("How much natural Armor Enviokers have, Default: 0.0")
                        .defineInRange("enviokerArmor", 0.0, 0.0, Double.MAX_VALUE);
                EnviokerDamage = BUILDER.comment("How much damage Envioker deals, Default: 5.0")
                        .defineInRange("enviokerDamage", 5.0, 1.0, Double.MAX_VALUE);
                TormentorHealth = BUILDER.comment("How much Max Health Tormentor have, Default: 24.0")
                        .defineInRange("tormentorHealth", 24.0, 1.0, Double.MAX_VALUE);
                TormentorArmor = BUILDER.comment("How much natural Armor Tormentors have, Default: 0.0")
                        .defineInRange("tormentorArmor", 0.0, 0.0, Double.MAX_VALUE);
                TormentorDamage = BUILDER.comment("How much damage Tormentor deals, Default: 4.0")
                        .defineInRange("tormentorDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Inquillager");
                InquillagerHealth = BUILDER.comment("How much Max Health Inquillager have, Default: 24.0")
                        .defineInRange("inquillagerHealth", 24.0, 1.0, Double.MAX_VALUE);
                InquillagerArmor = BUILDER.comment("How much natural Armor Inquillager have, Default: 0.0")
                        .defineInRange("inquillagerArmor", 0.0, 0.0, Double.MAX_VALUE);
                InquillagerDamage = BUILDER.comment("How much damage Inquillager deals, Default: 5.0")
                        .defineInRange("inquillagerDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Piker");
                PikerHealth = BUILDER.comment("How much Max Health Pikers have, Default: 24.0")
                        .defineInRange("pikerHealth", 24.0, 1.0, Double.MAX_VALUE);
                PikerArmor = BUILDER.comment("How much Armor Pikers have, Default: 4.0")
                        .defineInRange("pikerArmor", 4.0, 0.0, Double.MAX_VALUE);
                PikerDamage = BUILDER.comment("How much damage Pikers deals, Default: 9.0")
                        .defineInRange("pikerDamage", 9.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Ripper");
                RipperHealth = BUILDER.comment("How much Max Health Rippers have, Default: 16.0")
                        .defineInRange("ripperHealth", 16.0, 1.0, Double.MAX_VALUE);
                RipperArmor = BUILDER.comment("How much Armor Rippers have, Default: 2.0")
                        .defineInRange("ripperArmor", 2.0, 0.0, Double.MAX_VALUE);
                RipperDamage = BUILDER.comment("How much damage Rippers deals, Default: 2.0")
                        .defineInRange("ripperDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Trampler");
                TramplerHealth = BUILDER.comment("How much Max Health Tramplers have, Default: 30.0")
                        .defineInRange("tramplerHealth", 30.0, 1.0, Double.MAX_VALUE);
                TramplerArmor = BUILDER.comment("How much natural Armor Tramplers have, Default: 0.0")
                        .defineInRange("tramplerArmor", 0.0, 0.0, Double.MAX_VALUE);
                TramplerDamage = BUILDER.comment("How much damage Tramplers deals, Default: 6.0")
                        .defineInRange("tramplerDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Crusher");
                CrusherHealth = BUILDER.comment("How much Max Health Crushers have, Default: 24.0")
                        .defineInRange("crusherHealth", 24.0, 1.0, Double.MAX_VALUE);
                CrusherArmor = BUILDER.comment("How much natural Armor Crushers have, Default: 0.0")
                        .defineInRange("crusherArmor", 0.0, 0.0, Double.MAX_VALUE);
                CrusherDamage = BUILDER.comment("How much damage Crushers deals, Default: 12.0")
                        .defineInRange("crusherDamage", 12.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Storm Caster");
                StormCasterHealth = BUILDER.comment("How much Max Health Storm Casters have, Default: 24.0")
                        .defineInRange("stormCasterHealth", 24.0, 1.0, Double.MAX_VALUE);
                StormCasterArmor = BUILDER.comment("How much natural Armor Storm Casters have, Default: 0.0")
                        .defineInRange("stormCasterArmor", 0.0, 0.0, Double.MAX_VALUE);
                StormCasterDamage = BUILDER.comment("How much damage Storm Casters deals, Default: 5.0")
                        .defineInRange("stormCasterDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Cryologer");
                CryologerHealth = BUILDER.comment("How much Max Health Cryologers have, Default: 24.0")
                        .defineInRange("cryologerHealth", 24.0, 1.0, Double.MAX_VALUE);
                CryologerArmor = BUILDER.comment("How much natural Armor Cryologers have, Default: 0.0")
                        .defineInRange("cryologerArmor", 0.0, 0.0, Double.MAX_VALUE);
                CryologerDamage = BUILDER.comment("How much damage Cryologers deals, Default: 5.0")
                        .defineInRange("cryologerDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Preacher");
                PreacherHealth = BUILDER.comment("How much Max Health Preachers have, Default: 24.0")
                        .defineInRange("preacherHealth", 24.0, 1.0, Double.MAX_VALUE);
                PreacherArmor = BUILDER.comment("How much natural Armor Preachers have, Default: 0.0")
                        .defineInRange("preacherArmor", 0.0, 0.0, Double.MAX_VALUE);
                PreacherHeal = BUILDER.comment("How much health Preachers heal, Default: 6.0")
                        .defineInRange("preacherHeal", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
            ConquillagerHealth = BUILDER.comment("How much Max Health Conquillager have, Default: 24.0")
                    .defineInRange("conquillagerHealth", 24.0, 1.0, Double.MAX_VALUE);
            ConquillagerArmor = BUILDER.comment("How much natural Armor Conquillager have, Default: 0.0")
                    .defineInRange("conquillagerArmor", 0.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("General");
                BUILDER.push("Reaper");
                ReaperHealth = BUILDER.comment("How much Max Health Reapers have, Default: 24.0")
                        .defineInRange("reaperHealth", 24.0, 1.0, Double.MAX_VALUE);
                ReaperArmor = BUILDER.comment("How much natural Armor Reapers have, Default: 0.0")
                        .defineInRange("reaperArmor", 0.0, 0.0, Double.MAX_VALUE);
                ReaperDamage = BUILDER.comment("How much damage Reapers deals, Default: 8.0")
                        .defineInRange("reaperDamage", 8.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Wraith");
                WraithHealth = BUILDER.comment("How much Max Health Wraiths have, Default: 25.0")
                        .defineInRange("wraithHealth", 25.0, 1.0, Double.MAX_VALUE);
                WraithArmor = BUILDER.comment("How much natural Armor Wraiths have, Default: 0.0")
                        .defineInRange("wraithArmor", 0.0, 0.0, Double.MAX_VALUE);
                WraithDamage = BUILDER.comment("How much damage Wraith deals, Default: 4.0")
                        .defineInRange("wraithDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Necromancer");
                NecromancerHealth = BUILDER.comment("How much Max Health Necromancers have, Default: 50.0")
                        .defineInRange("necromancerHealth", 50.0, 1.0, Double.MAX_VALUE);
                NecromancerArmor = BUILDER.comment("How much natural Armor Necromancers have, Default: 0.0")
                        .defineInRange("necromancerArmor", 0.0, 0.0, Double.MAX_VALUE);
                NecromancerFollowRange = BUILDER.comment("How much following/detection range Necromancers have, Default: 16.0")
                        .defineInRange("necromancerFollowRange", 16.0, 1.0, 2048.0);
                NecromancerDamage = BUILDER.comment("How much damage Necromancers deals, Default: 4.0")
                        .defineInRange("necromancerDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Cairn Necromancer");
                CairnNecromancerHealth = BUILDER.comment("How much Max Health Cairn Necromancers have, Default: 50.0")
                        .defineInRange("cairnNecromancerHealth", 50.0, 1.0, Double.MAX_VALUE);
                CairnNecromancerArmor = BUILDER.comment("How much natural Armor Cairn Necromancers have, Default: 0.0")
                        .defineInRange("cairnNecromancerArmor", 0.0, 0.0, Double.MAX_VALUE);
                CairnNecromancerFollowRange = BUILDER.comment("How much following/detection range Cairn Necromancers have, Default: 16.0")
                        .defineInRange("cairnNecromancerFollowRange", 16.0, 1.0, 2048.0);
                CairnNecromancerDamage = BUILDER.comment("How much damage Cairn Necromancers deals, Default: 3.0")
                        .defineInRange("cairnNecromancerDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Mossy Necromancer");
                MossyNecromancerHealth = BUILDER.comment("How much Max Health Mossy Necromancers have, Default: 50.0")
                        .defineInRange("mossyNecromancerHealth", 50.0, 1.0, Double.MAX_VALUE);
                MossyNecromancerArmor = BUILDER.comment("How much natural Armor Mossy Necromancers have, Default: 0.0")
                        .defineInRange("mossyNecromancerArmor", 0.0, 0.0, Double.MAX_VALUE);
                MossyNecromancerFollowRange = BUILDER.comment("How much following/detection range Mossy Necromancers have, Default: 16.0")
                        .defineInRange("mossyNecromancerFollowRange", 16.0, 1.0, 2048.0);
                MossyNecromancerDamage = BUILDER.comment("How much damage Mossy Necromancers deals, Default: 3.0")
                        .defineInRange("mossyNecromancerDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Drowned Necromancer");
                DrownedNecromancerHealth = BUILDER.comment("How much Max Health Drowned Necromancers have, Default: 220.0")
                        .defineInRange("drownedNecromancerHealth", 220.0, 1.0, Double.MAX_VALUE);
                DrownedNecromancerArmor = BUILDER.comment("How much natural Armor Drowned Necromancers have, Default: 0.0")
                        .defineInRange("drownedNecromancerArmor", 0.0, 0.0, Double.MAX_VALUE);
                DrownedNecromancerFollowRange = BUILDER.comment("How much following/detection range Drowned Necromancers have, Default: 32.0")
                        .defineInRange("drownedNecromancerFollowRange", 32.0, 1.0, 2048.0);
                DrownedNecromancerDamage = BUILDER.comment("How much damage Drowned Necromancers deals, Default: 4.0")
                        .defineInRange("drownedNecromancerDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Wither Necromancer");
                WitherNecromancerHealth = BUILDER.comment("How much Max Health Wither Necromancers have, Default: 220.0")
                        .defineInRange("witherNecromancerHealth", 220.0, 1.0, Double.MAX_VALUE);
                WitherNecromancerArmor = BUILDER.comment("How much natural Armor Wither Necromancers have, Default: 0.0")
                        .defineInRange("witherNecromancerArmor", 0.0, 0.0, Double.MAX_VALUE);
                WitherNecromancerFollowRange = BUILDER.comment("How much following/detection range Wither Necromancers have, Default: 32.0")
                        .defineInRange("witherNecromancerFollowRange", 32.0, 1.0, 2048.0);
                WitherNecromancerDamage = BUILDER.comment("How much damage Wither Necromancers deals, Default: 4.0")
                        .defineInRange("witherNecromancerDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Haunted Armor");
                HauntedArmorHealth = BUILDER.comment("How much Max Health Haunted Armor have, Default: 25.0")
                        .defineInRange("hauntedArmorHealth", 25.0, 1.0, Double.MAX_VALUE);
                HauntedArmorDamage = BUILDER.comment("How much damage Haunted Armor deals, Default: 3.0")
                        .defineInRange("hauntedArmorDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
            BUILDER.pop();
            BUILDER.push("Summoned Mobs");
                BUILDER.push("Zombie Servant");
                ZombieServantHealth = BUILDER.comment("How much Max Health Zombie Servants have, Default: 20.0")
                        .defineInRange("zombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                ZombieServantArmor = BUILDER.comment("How much natural armor points Zombie Servants have, Default: 2.0")
                        .defineInRange("zombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                ZombieServantDamage = BUILDER.comment("How much damage Zombie Servants deals, Default: 3.0")
                        .defineInRange("zombieServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Husk Servant");
                HuskServantHealth = BUILDER.comment("How much Max Health Husk Servants have, Default: 20.0")
                        .defineInRange("huskServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                HuskServantArmor = BUILDER.comment("How much natural armor points Husk Servants have, Default: 2.0")
                        .defineInRange("huskServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                HuskServantDamage = BUILDER.comment("How much damage Husk Servants deals, Default: 3.0")
                        .defineInRange("huskServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Drowned Servant");
                DrownedServantHealth = BUILDER.comment("How much Max Health Drowned Servants have, Default: 20.0")
                        .defineInRange("drownedServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                DrownedServantArmor = BUILDER.comment("How much natural armor points Drowned Servants have, Default: 2.0")
                        .defineInRange("drownedServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                DrownedServantDamage = BUILDER.comment("How much damage Drowned Servants deals, Default: 3.0")
                        .defineInRange("drownedServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Frozen Zombie Servant");
                FrozenZombieServantHealth = BUILDER.comment("How much Max Health Frozen Zombie Servants have, Default: 20.0")
                        .defineInRange("frozenZombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                FrozenZombieServantArmor = BUILDER.comment("How much natural armor points Frozen Zombie Servants have, Default: 2.0")
                        .defineInRange("frozenZombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                FrozenZombieServantDamage = BUILDER.comment("How much damage Frozen Zombie Servants deals, Default: 3.0")
                        .defineInRange("frozenZombieServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Jungle Zombie Servant");
                JungleZombieServantHealth = BUILDER.comment("How much Max Health Jungle Zombie Servants have, Default: 20.0")
                        .defineInRange("jungleZombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                JungleZombieServantArmor = BUILDER.comment("How much natural armor points Jungle Zombie Servants have, Default: 2.0")
                        .defineInRange("jungleZombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                JungleZombieServantDamage = BUILDER.comment("How much damage Jungle Zombie Servants deals, Default: 3.0")
                        .defineInRange("jungleZombieServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Frayed Servant");
                FrayedServantHealth = BUILDER.comment("How much Max Health Frayed Servants have, Default: 20.0")
                        .defineInRange("frayedServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                FrayedServantArmor = BUILDER.comment("How much natural armor points Frayed Servants have, Default: 2.0")
                        .defineInRange("frayedServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                FrayedServantDamage = BUILDER.comment("How much damage Frayed Servants deals, Default: 3.0")
                        .defineInRange("frayedServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombified Piglin Servant");
                ZPiglinServantHealth = BUILDER.comment("How much Max Health Zombified Piglin Servants have, Default: 20.0")
                        .defineInRange("zombifiedPiglinServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                ZPiglinServantArmor = BUILDER.comment("How much natural armor points Zombified Piglin Servants have, Default: 2.0")
                        .defineInRange("zombifiedPiglinServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                ZPiglinServantDamage = BUILDER.comment("How much damage Zombified Piglin Servants deals, Default: 5.0")
                        .defineInRange("zombifiedPiglinServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombified Piglin Brute Servant");
                ZPiglinBruteServantHealth = BUILDER.comment("How much Max Health Zombified Piglin Brute Servants have, Default: 50.0")
                        .defineInRange("zombifiedPiglinBruteServantHealth", 50.0, 1.0, Double.MAX_VALUE);
                ZPiglinBruteServantArmor = BUILDER.comment("How much natural armor points Zombified Piglin Brute Servants have, Default: 2.0")
                        .defineInRange("zombifiedPiglinBruteServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                ZPiglinBruteServantDamage = BUILDER.comment("How much damage Zombified Piglin Brute Servants deals, Default: 7.0")
                        .defineInRange("zombifiedPiglinBruteServantDamage", 7.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Blackguard Servant");
                BlackguardServantHealth = BUILDER.comment("How much Max Health Blackguard Servants have, Default: 20.0")
                        .defineInRange("blackguardServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                BlackguardServantArmor = BUILDER.comment("How much natural armor points Blackguard Servants have, Default: 20.0")
                        .defineInRange("blackguardServantArmor", 20.0, 0.0, Double.MAX_VALUE);
                BlackguardServantToughness = BUILDER.comment("How much natural armor toughness points Blackguard Servants have, Default: 8.0")
                        .defineInRange("blackguardServantToughness", 8.0, 0.0, Double.MAX_VALUE);
                BlackguardServantDamage = BUILDER.comment("How much damage Blackguard Servants deals, Default: 9.0")
                        .defineInRange("blackguardServantDamage", 9.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Royal Guard Servant");
                RoyalGuardServantHealth = BUILDER.comment("How much Max Health Royal Guard Servants have, Default: 48.0")
                        .defineInRange("royalGuardServantHealth", 48.0, 1.0, Double.MAX_VALUE);
                RoyalGuardServantArmor = BUILDER.comment("How much natural armor points Royal Guard Servants have, Default: 10.0")
                        .defineInRange("royalGuardServantArmor", 10.0, 0.0, Double.MAX_VALUE);
                RoyalGuardServantToughness = BUILDER.comment("How much natural armor toughness points Royal Guard Servants have, Default: 4.0")
                        .defineInRange("royalGuardServantToughness", 4.0, 0.0, Double.MAX_VALUE);
                RoyalGuardServantDamage = BUILDER.comment("How much damage Royal Guard Servants deals, Default: 9.0")
                        .defineInRange("royalGuardServantDamage", 9.0, 1.0, Double.MAX_VALUE);
                RoyalGuardServantShield = BUILDER.comment("Initially, how many hits until Shield is broken, Default: 1")
                        .defineInRange("royalGuardServantShield", 1, 0, Integer.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skeleton Servant");
                SkeletonServantHealth = BUILDER.comment("How much Max Health Skeleton Servants have, Default: 20.0")
                        .defineInRange("skeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                SkeletonServantArmor = BUILDER.comment("How much natural Armor Skeleton Servants have, Default: 0.0")
                        .defineInRange("skeletonServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                SkeletonServantDamage = BUILDER.comment("How much damage Skeleton Servants deals, Default: 2.0")
                        .defineInRange("skeletonServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                SkeletonServantRangeDamage = BUILDER.comment("How much extra damage Skeleton Servants range attack deals, Default: 0.0")
                        .defineInRange("skeletonServantRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Stray Servant");
                StrayServantHealth = BUILDER.comment("How much Max Health Stray Servants have, Default: 20.0")
                        .defineInRange("strayServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                StrayServantArmor = BUILDER.comment("How much natural Armor Stray Servants have, Default: 0.0")
                        .defineInRange("strayServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                StrayServantDamage = BUILDER.comment("How much damage Stray Servants deals, Default: 2.0")
                        .defineInRange("strayServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                StrayServantRangeDamage = BUILDER.comment("How much extra damage Stray Servants range attack deals, Default: 0.0")
                        .defineInRange("strayServantRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Wither Skeleton Servant");
                WitherSkeletonServantHealth = BUILDER.comment("How much Max Health Wither Skeleton Servants have, Default: 20.0")
                        .defineInRange("witherSkeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                WitherSkeletonServantArmor = BUILDER.comment("How much natural Armor Wither Skeleton Servants have, Default: 0.0")
                        .defineInRange("witherSkeletonServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                WitherSkeletonServantDamage = BUILDER.comment("How much damage Wither Skeleton Servants deals, Default: 4.0")
                        .defineInRange("witherSkeletonServantDamage", 4.0, 1.0, Double.MAX_VALUE);
                WitherSkeletonServantRangeDamage = BUILDER.comment("How much extra damage Wither Skeleton Servants range attack deals, Default: 0.0")
                        .defineInRange("witherSkeletonServantRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Mossy Skeleton Servant");
                MossySkeletonServantHealth = BUILDER.comment("How much Max Health Mossy Skeleton Servants have, Default: 20.0")
                        .defineInRange("mossySkeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                MossySkeletonServantArmor = BUILDER.comment("How much natural Armor Mossy Skeleton Servants have, Default: 0.0")
                        .defineInRange("mossySkeletonServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                MossySkeletonServantDamage = BUILDER.comment("How much damage Mossy Skeleton Servants deals, Default: 2.0")
                        .defineInRange("mossySkeletonServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                MossySkeletonServantRangeDamage = BUILDER.comment("How much extra damage Mossy Skeleton Servants range attack deals, Default: 0.0")
                        .defineInRange("mossySkeletonServantRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Sunken Skeleton Servant");
                SunkenSkeletonServantHealth = BUILDER.comment("How much Max Health Sunken Skeleton Servants have, Default: 20.0")
                        .defineInRange("sunkenSkeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                SunkenSkeletonServantArmor = BUILDER.comment("How much natural Armor Sunken Skeleton Servants have, Default: 0.0")
                        .defineInRange("sunkenSkeletonServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                SunkenSkeletonServantDamage = BUILDER.comment("How much damage Sunken Skeleton Servants deals, Default: 2.0")
                        .defineInRange("sunkenSkeletonServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                SunkenSkeletonServantRangeDamage = BUILDER.comment("How much extra damage Sunken Skeleton Servants range attack deals, Default: 0.0")
                        .defineInRange("sunkenSkeletonServantRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Rattled Servant");
                RattledServantHealth = BUILDER.comment("How much Max Health Rattled Servants have, Default: 20.0")
                        .defineInRange("rattledServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                RattledServantArmor = BUILDER.comment("How much natural Armor Rattled Servants have, Default: 0.0")
                        .defineInRange("rattledServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                RattledServantDamage = BUILDER.comment("How much damage Rattled Servants deals, Default: 2.0")
                        .defineInRange("rattledServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                RattledServantRangeDamage = BUILDER.comment("How much extra damage Rattled Servants range attack deals, Default: 0.0")
                        .defineInRange("rattledServantRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Vanguard Servant");
                VanguardServantHealth = BUILDER.comment("How much Max Health Vanguard Servants have, Default: 20.0")
                        .defineInRange("vanguardServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                VanguardServantArmor = BUILDER.comment("How much Armor Vanguard Servants have, Default: 9.0")
                        .defineInRange("vanguardServantArmor", 9.0, 0.0, Double.MAX_VALUE);
                VanguardServantDamage = BUILDER.comment("How much damage Vanguard Servants deals, Default: 7.0")
                        .defineInRange("vanguardServantDamage", 7.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Phantom Servant");
                PhantomServantHealth = BUILDER.comment("How much Max Health Phantom Servants have, Default: 20.0")
                        .defineInRange("phantomServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                PhantomServantArmor = BUILDER.comment("How much Armor Phantom Servants have, Default: 0.0")
                        .defineInRange("phantomServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                PhantomServantInitialDamage = BUILDER.comment("How much damage Phantom Servants deals initially, Default: 2.0")
                        .defineInRange("phantomServantInitialDamage", 2.0, 1.0, Double.MAX_VALUE);
                PhantomServantSizeDamage = BUILDER.comment("How much damage is increased per Phantom Servants size, Default: 6.0")
                        .defineInRange("phantomServantSizeDamage", 6.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skeleton Pillager");
                SkeletonPillagerHealth = BUILDER.comment("How much Max Health Skeleton Pillagers have, Default: 24.0")
                        .defineInRange("skeletonPillagerHealth", 24.0, 1.0, Double.MAX_VALUE);
                SkeletonPillagerArmor = BUILDER.comment("How much natural Armor Skeleton Pillagers have, Default: 0.0")
                        .defineInRange("skeletonPillagerArmor", 0.0, 0.0, Double.MAX_VALUE);
                SkeletonPillagerDamage = BUILDER.comment("How much damage Skeleton Pillagers deals, Default: 5.0")
                        .defineInRange("skeletonPillagerDamage", 5.0, 1.0, Double.MAX_VALUE);
                SkeletonPillagerRangeDamage = BUILDER.comment("How much extra damage Skeleton Pillagers range attack deals, Default: 0.0")
                        .defineInRange("skeletonPillagerRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombie Vindicator");
                ZombieVindicatorHealth = BUILDER.comment("How much Max Health Zombie Vindicators have, Default: 24.0")
                        .defineInRange("zombieVindicatorHealth", 24.0, 1.0, Double.MAX_VALUE);
                ZombieVindicatorArmor = BUILDER.comment("How much natural armor points Zombie Vindicators have, Default: 2.0")
                        .defineInRange("zombieVindicatorArmor", 2.0, 0.0, Double.MAX_VALUE);
                ZombieVindicatorDamage = BUILDER.comment("How much damage Zombie Vindicators deals, Default: 5.0")
                        .defineInRange("zombieVindicatorDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Bound Evoker");
                BoundEvokerHealth = BUILDER.comment("How much Max Health Bound Evokers have, Default: 24.0")
                        .defineInRange("boundEvokerHealth", 24.0, 1.0, Double.MAX_VALUE);
                BoundEvokerArmor = BUILDER.comment("How much natural Armor Bound Evokers have, Default: 0.0")
                        .defineInRange("boundEvokerArmor", 0.0, 0.0, Double.MAX_VALUE);
                BoundEvokerFollowRange = BUILDER.comment("How much following/detection range Bound Evokers have, Default: 12.0")
                        .defineInRange("boundEvokerFollowRange", 12.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Bound Iceologer");
                BoundIceologerHealth = BUILDER.comment("How much Max Health Bound Iceologers have, Default: 20.0")
                        .defineInRange("boundIceologerHealth", 20.0, 1.0, Double.MAX_VALUE);
                BoundIceologerArmor = BUILDER.comment("How much natural Armor Bound Iceologers have, Default: 0.0")
                        .defineInRange("boundIceologerArmor", 0.0, 0.0, Double.MAX_VALUE);
                BoundIceologerFollowRange = BUILDER.comment("How much following/detection range Bound Iceologers have, Default: 12.0")
                        .defineInRange("boundIceologerFollowRange", 12.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Neollager");
                NeollagerHealth = BUILDER.comment("How much Max Health Neollagers have, Default: 20.0")
                        .defineInRange("neollagerHealth", 20.0, 1.0, Double.MAX_VALUE);
                NeollagerArmor = BUILDER.comment("How much natural Armor Neollagers have, Default: 0.0")
                        .defineInRange("neollagerArmor", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Pillager Servant");
                PillagerServantHealth = BUILDER.comment("How much Max Health Pillager Servants have, Default: 24.0")
                        .defineInRange("pillagerServantHealth", 24.0, 1.0, Double.MAX_VALUE);
                PillagerServantArmor = BUILDER.comment("How much natural Armor Pillager Servants have, Default: 0.0")
                        .defineInRange("pillagerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                PillagerServantDamage = BUILDER.comment("How much damage Pillager Servants deals, Default: 5.0")
                        .defineInRange("pillagerServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                PillagerServantRangeDamage = BUILDER.comment("How much extra damage Pillager Servants range attack deals, Default: 0.0")
                        .defineInRange("pillagerServantRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Signaler Servant");
                SignalerServantHealth = BUILDER.comment("How much Max Health Signaler Servants have, Default: 24.0")
                        .defineInRange("signalerServantHealth", 24.0, 1.0, Double.MAX_VALUE);
                SignalerServantArmor = BUILDER.comment("How much natural Armor Signaler Servants have, Default: 0.0")
                        .defineInRange("signalerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                SignalerServantFollowRange = BUILDER.comment("How much initial following/detection range Signaler Servants have, Default: 32.0")
                        .defineInRange("signalerServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Vindicator Servant");
                VindicatorServantHealth = BUILDER.comment("How much Max Health Vindicator Servants have, Default: 24.0")
                        .defineInRange("vindicatorServantHealth", 24.0, 1.0, Double.MAX_VALUE);
                VindicatorServantArmor = BUILDER.comment("How much natural armor points Vindicator Servants have, Default: 0.0")
                        .defineInRange("vindicatorServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                VindicatorServantDamage = BUILDER.comment("How much damage Vindicator Servants deals, Default: 5.0")
                        .defineInRange("vindicatorServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Mountaineer Servant");
                MountaineerServantHealth = BUILDER.comment("How much Max Health Mountaineer Servants have, Default: 28.0")
                        .defineInRange("mountaineerServantHealth", 28.0, 1.0, Double.MAX_VALUE);
                MountaineerServantArmor = BUILDER.comment("How much natural armor points Mountaineer Servants have, Default: 0.0")
                        .defineInRange("mountaineerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                MountaineerServantDamage = BUILDER.comment("How much damage Mountaineer Servants deals, Default: 5.0")
                        .defineInRange("mountaineerServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Evoker Servant");
                EvokerServantHealth = BUILDER.comment("How much Max Health Evoker Servants have, Default: 24.0")
                        .defineInRange("evokerServantHealth", 24.0, 1.0, Double.MAX_VALUE);
                EvokerServantArmor = BUILDER.comment("How much natural Armor Evoker Servants have, Default: 0.0")
                        .defineInRange("evokerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                EvokerServantFollowRange = BUILDER.comment("How much following/detection range Evoker Servants have, Default: 12.0")
                        .defineInRange("evokerServantFollowRange", 12.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Geomancer Servant");
                GeomancerServantHealth = BUILDER.comment("How much Max Health Geomancer Servants have, Default: 18.0")
                        .defineInRange("geomancerServantHealth", 18.0, 1.0, Double.MAX_VALUE);
                GeomancerServantArmor = BUILDER.comment("How much natural Armor Geomancer Servants have, Default: 0.0")
                        .defineInRange("geomancerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                GeomancerServantFollowRange = BUILDER.comment("How much following/detection range Geomancer Servants have, Default: 12.0")
                        .defineInRange("geomancerServantFollowRange", 12.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Iceologer Servant");
                IceologerServantHealth = BUILDER.comment("How much Max Health Iceologer Servants have, Default: 20.0")
                        .defineInRange("iceologerServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                IceologerServantArmor = BUILDER.comment("How much natural Armor Iceologer Servants have, Default: 0.0")
                        .defineInRange("iceologerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                IceologerServantFollowRange = BUILDER.comment("How much following/detection range Iceologer Servants have, Default: 15.0")
                        .defineInRange("iceologerServantFollowRange", 15.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Wind Caller Servant");
                WindCallerServantHealth = BUILDER.comment("How much Max Health Wind Caller Servants have, Default: 24.0")
                        .defineInRange("windCallerServantHealth", 24.0, 1.0, Double.MAX_VALUE);
                WindCallerServantArmor = BUILDER.comment("How much natural Armor Wind Caller Servants have, Default: 0.0")
                        .defineInRange("windCallerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                WindCallerServantFollowRange = BUILDER.comment("How much following/detection range Wind Caller Servants have, Default: 35.0")
                        .defineInRange("windCallerServantFollowRange", 35.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Witch Servant");
                WitchServantHealth = BUILDER.comment("How much Max Health Witch Servants have, Default: 26.0")
                        .defineInRange("witchServantHealth", 26.0, 1.0, Double.MAX_VALUE);
                WitchServantArmor = BUILDER.comment("How much natural Armor Witch Servants have, Default: 0.0")
                        .defineInRange("witchServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                WitchServantFollowRange = BUILDER.comment("How much following/detection range Witch Servants have, Default: 16.0")
                        .defineInRange("witchServantFollowRange", 16.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Vex");
                SummonedVexHealth = BUILDER.comment("How much Max Health Summoned Vexes have, Default: 14.0")
                        .defineInRange("summonedVexHealth", 14.0, 1.0, Double.MAX_VALUE);
                SummonedVexDamage = BUILDER.comment("How much damage Summoned Vexes deals, Default: 4.0")
                        .defineInRange("summonedVexDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Irk");
                SummonedIrkHealth = BUILDER.comment("How much Max Health Irks have, Default: 12.0")
                        .defineInRange("summonedIrkHealth", 12.0, 1.0, Double.MAX_VALUE);
                SummonedIrkDamage = BUILDER.comment("How much damage Irks deals, Default: 4.0")
                        .defineInRange("summonedIrkDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Sprite");
                SpriteHealth = BUILDER.comment("How much Max Health Sprites have, Default: 12.0")
                        .defineInRange("spriteHealth", 12.0, 1.0, Double.MAX_VALUE);
                SpriteDamage = BUILDER.comment("How much damage Sprites deals, Default: 2.0")
                        .defineInRange("spriteDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Mini-Ghast");
                MiniGhastHealth = BUILDER.comment("How much Max Health Mini-Ghasts have, Default: 5.0")
                        .defineInRange("miniGhastHealth", 5.0, 1.0, Double.MAX_VALUE);
                MiniGhastDamage = BUILDER.comment("How much damage Mini-Ghasts' fireballs deals, Default: 5.0")
                        .defineInRange("miniGhastDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Ghast Servant");
                GhastServantHealth = BUILDER.comment("How much Max Health Ghast Servants have, Default: 10.0")
                        .defineInRange("ghastServantHealth", 10.0, 1.0, Double.MAX_VALUE);
                GhastServantDamage = BUILDER.comment("How much damage Ghast Servants' lavaballs deals, Default: 6.0")
                        .defineInRange("ghastServantDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Blaze Servant");
                BlazeServantHealth = BUILDER.comment("How much Max Health Blaze Servants have, Default: 20.0")
                        .defineInRange("blazeServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                BlazeServantArmor = BUILDER.comment("How much natural armor points Blaze Servants have, Default: 0.0")
                        .defineInRange("blazeServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                BlazeServantMeleeDamage = BUILDER.comment("How much melee damage Blaze Servants deals, Default: 6.0")
                        .defineInRange("blazeServantMeleeDamage", 6.0, 1.0, Double.MAX_VALUE);
                BlazeServantRangeDamage = BUILDER.comment("How much ranged damage Blaze Servants deals, Default: 5.0")
                        .defineInRange("blazeServantRangeDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Wildfire");
                WildfireHealth = BUILDER.comment("How much Max Health Wildfires have, Default: 75.0")
                        .defineInRange("wildfireHealth", 75.0, 1.0, Double.MAX_VALUE);
                WildfireShieldHealth = BUILDER.comment("How much Health each Wildfire's shields have, Default: 12.5")
                        .defineInRange("wildfireShieldHealth", 12.5, 1.0, Double.MAX_VALUE);
                WildfireArmor = BUILDER.comment("How much natural armor points Wildfires have, Default: 0.0")
                        .defineInRange("wildfireArmor", 0.0, 0.0, Double.MAX_VALUE);
                WildfireMeleeDamage = BUILDER.comment("How much melee damage Wildfires deals, Default: 6.0")
                        .defineInRange("wildfireMeleeDamage", 6.0, 1.0, Double.MAX_VALUE);
                WildfireRangeDamage = BUILDER.comment("How much ranged damage Wildfires deals, Default: 10.0")
                        .defineInRange("wildfireRangeDamage", 10.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Spider Servant");
                SpiderServantHealth = BUILDER.comment("How much Max Health Spider Servants have, Default: 16.0")
                        .defineInRange("spiderServantHealth", 16.0, 1.0, Double.MAX_VALUE);
                SpiderServantDamage = BUILDER.comment("How much damage Spider Servants melee attack deals, Default: 2.0")
                        .defineInRange("spiderServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Cave Spider Servant");
                CaveSpiderServantHealth = BUILDER.comment("How much Max Health Cave Spider Servants have, Default: 12.0")
                        .defineInRange("caveSpiderServantHealth", 12.0, 1.0, Double.MAX_VALUE);
                CaveSpiderServantDamage = BUILDER.comment("How much damage Cave Spider Servants melee attack deals, Default: 2.0")
                        .defineInRange("caveSpiderServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Web Spider Servant");
                WebSpiderServantHealth = BUILDER.comment("How much Max Health Web Spider Servants have, Default: 16.0")
                        .defineInRange("webSpiderServantHealth", 16.0, 1.0, Double.MAX_VALUE);
                WebSpiderServantDamage = BUILDER.comment("How much damage Web Spider Servants melee attack deals, Default: 2.0")
                        .defineInRange("webSpiderServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Icy Spider Servant");
                IcySpiderServantHealth = BUILDER.comment("How much Max Health Icy Spider Servants have, Default: 16.0")
                        .defineInRange("icySpiderServantHealth", 16.0, 1.0, Double.MAX_VALUE);
                IcySpiderServantDamage = BUILDER.comment("How much damage Icy Spider Servants melee attack deals, Default: 2.0")
                        .defineInRange("icySpiderServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Bone Spider Servant");
                BoneSpiderServantHealth = BUILDER.comment("How much Max Health Bone Spider Servants have, Default: 32.0")
                        .defineInRange("boneSpiderServantHealth", 32.0, 1.0, Double.MAX_VALUE);
                BoneSpiderServantDamage = BUILDER.comment("How much damage Bone Spider Servants melee attack deals, Default: 3.0")
                        .defineInRange("boneSpiderServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BoneSpiderServantRangeDamage = BUILDER.comment("How much extra damage Bone Spider Servants range attack deals, Default: 0.0")
                        .defineInRange("boneSpiderServantRangeDamage", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Brood Mother");
                BroodMotherHealth = BUILDER.comment("How much Max Health Brood Mothers have, Default: 96.0")
                        .defineInRange("broodMotherHealth", 96.0, 1.0, Double.MAX_VALUE);
                BroodMotherArmor = BUILDER.comment("How much natural Armor Brood Mothers have, Default: 0.0")
                        .defineInRange("broodMotherArmor", 0.0, 0.0, Double.MAX_VALUE);
                BroodMotherDamage = BUILDER.comment("How much damage Brood Mothers melee attack deals, Default: 6.0")
                        .defineInRange("broodMotherDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Black Wolf");
                BlackWolfHealth = BUILDER.comment("How much Max Health Black Wolves have, Default: 10.0")
                        .defineInRange("blackWolfHealth", 10.0, 1.0, Double.MAX_VALUE);
                BlackWolfArmor = BUILDER.comment("How much natural Armor Black Wolves have, Default: 0.0")
                        .defineInRange("blackWolfArmor", 0.0, 0.0, Double.MAX_VALUE);
                BlackWolfDamage = BUILDER.comment("How much damage Black Wolves melee attack deals, Default: 4.0")
                        .defineInRange("blackWolfDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Warg");
                WargHealth = BUILDER.comment("How much Max Health Wargs have, Default: 50.0")
                        .defineInRange("wargHealth", 50.0, 1.0, Double.MAX_VALUE);
                WargArmor = BUILDER.comment("How much natural Armor Wargs have, Default: 0.0")
                        .defineInRange("wargArmor", 0.0, 0.0, Double.MAX_VALUE);
                WargDamage = BUILDER.comment("How much damage Wargs melee attacks deal before weapon modifiers, Default: 8.0")
                        .defineInRange("wargDamage", 8.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Cerberus");
                CerberusHealth = BUILDER.comment("How much Max Health Cerberuses have, Default: 100.0")
                        .defineInRange("cerberusHealth", 100.0, 1.0, Double.MAX_VALUE);
                CerberusArmor = BUILDER.comment("How much natural Armor Cerberuses have, Default: 0.0")
                        .defineInRange("cerberusArmor", 0.0, 0.0, Double.MAX_VALUE);
                CerberusDamage = BUILDER.comment("How much damage Cerberus melee attacks deal, Default: 8.0")
                        .defineInRange("cerberusDamage", 8.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skeleton Wolf");
                SkeletonWolfHealth = BUILDER.comment("How much Max Health Skeleton Wolves have, Default: 10.0")
                        .defineInRange("skeletonWolfHealth", 10.0, 1.0, Double.MAX_VALUE);
                SkeletonWolfArmor = BUILDER.comment("How much natural Armor Skeleton Wolves have, Default: 0.0")
                        .defineInRange("skeletonWolfArmor", 0.0, 0.0, Double.MAX_VALUE);
                SkeletonWolfDamage = BUILDER.comment("How much damage Skeleton Wolves melee attack deals, Default: 4.0")
                        .defineInRange("skeletonWolfDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Twilight Goat");
                TwilightGoatHealth = BUILDER.comment("How much Max Health Twilight Goats have, Default: 10.0")
                        .defineInRange("twilightGoatHealth", 10.0, 1.0, Double.MAX_VALUE);
                TwilightGoatArmor = BUILDER.comment("How much natural Armor Twilight Goats have, Default: 0.0")
                        .defineInRange("twilightGoatArmor", 0.0, 0.0, Double.MAX_VALUE);
                TwilightGoatDamage = BUILDER.comment("How much damage Twilight Goats melee attack deals, Default: 2.0")
                        .defineInRange("twilightGoatDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Snapper");
                SnapperHealth = BUILDER.comment("How much Max Health Snappers have, Default: 10.0")
                        .defineInRange("snapperHealth", 10.0, 1.0, Double.MAX_VALUE);
                SnapperArmor = BUILDER.comment("How much natural Armor Snappers have, Default: 0.0")
                        .defineInRange("snapperArmor", 0.0, 0.0, Double.MAX_VALUE);
                SnapperDamage = BUILDER.comment("How much damage Snappers melee attack deals, Default: 4.0")
                        .defineInRange("snapperDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Gnasher");
                GnasherHealth = BUILDER.comment("How much Max Health Gnashers have, Default: 30.0")
                        .defineInRange("gnasherHealth", 30.0, 1.0, Double.MAX_VALUE);
                GnasherArmor = BUILDER.comment("How much natural Armor Gnashers have, Default: 0.0")
                        .defineInRange("gnasherArmor", 0.0, 0.0, Double.MAX_VALUE);
                GnasherDamage = BUILDER.comment("How much damage Gnashers melee attack deals, Default: 6.0")
                        .defineInRange("gnasherDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Guardian Servant");
                GuardianHealth = BUILDER.comment("How much Max Health Guardian Servants have, Default: 30.0")
                        .defineInRange("guardianHealth", 30.0, 1.0, Double.MAX_VALUE);
                GuardianArmor = BUILDER.comment("How much natural Armor Guardian Servants have, Default: 0.0")
                        .defineInRange("guardianArmor", 0.0, 0.0, Double.MAX_VALUE);
                GuardianDamage = BUILDER.comment("How much damage Guardian Servants attack deals, Default: 6.0")
                        .defineInRange("guardianDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Elder Guardian Servant");
                ElderGuardianHealth = BUILDER.comment("How much Max Health Elder Guardian Servants have, Default: 80.0")
                        .defineInRange("elderGuardianHealth", 80.0, 1.0, Double.MAX_VALUE);
                ElderGuardianArmor = BUILDER.comment("How much natural Armor Elder Guardian Servants have, Default: 0.0")
                        .defineInRange("elderGuardianArmor", 0.0, 0.0, Double.MAX_VALUE);
                ElderGuardianDamage = BUILDER.comment("How much damage Elder Guardian Servants attack deals, Default: 8.0")
                        .defineInRange("elderGuardianDamage", 8.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Bear Servant");
                BearServantHealth = BUILDER.comment("How much Max Health Bear Servants have, Default: 30.0")
                        .defineInRange("bearServantHealth", 30.0, 1.0, Double.MAX_VALUE);
                BearServantArmor = BUILDER.comment("How much natural Armor Bear Servants have, Default: 0.0")
                        .defineInRange("bearServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                BearServantDamage = BUILDER.comment("How much damage Bear Servants melee attack deals, Default: 6.0")
                        .defineInRange("bearServantDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Hoglin Servant");
                HoglinServantHealth = BUILDER.comment("How much Max Health Hoglin Servants have, Default: 40.0")
                        .defineInRange("hoglinServantHealth", 40.0, 1.0, Double.MAX_VALUE);
                HoglinServantArmor = BUILDER.comment("How much natural Armor Hoglin Servants have, Default: 0.0")
                        .defineInRange("hoglinServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                HoglinServantDamage = BUILDER.comment("How much damage Hoglin Servants melee attack deals, Default: 6.0")
                        .defineInRange("hoglinServantDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Black Beast");
                BlackBeastHealth = BUILDER.comment("How much Max Health Black Beasts have, Default: 75.0")
                        .defineInRange("blackBeastHealth", 75.0, 1.0, Double.MAX_VALUE);
                BlackBeastArmor = BUILDER.comment("How much natural Armor Black Beasts have, Default: 0.0")
                        .defineInRange("blackBeastArmor", 0.0, 0.0, Double.MAX_VALUE);
                BlackBeastDamage = BUILDER.comment("How much damage Black Beasts melee attack deals, Default: 8.0")
                        .defineInRange("blackBeastDamage", 8.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Carrion Maggot");
                CarrionMaggotHealth = BUILDER.comment("How much Max Health Carrion Maggots have, Default: 8.0")
                        .defineInRange("carrionMaggotHealth", 8.0, 1.0, Double.MAX_VALUE);
                CarrionMaggotArmor = BUILDER.comment("How much natural Armor Carrion Maggots have, Default: 0.0")
                        .defineInRange("carrionMaggotArmor", 0.0, 0.0, Double.MAX_VALUE);
                CarrionMaggotDamage = BUILDER.comment("How much damage Carrion Maggots melee attack deals, Default: 2.0")
                        .defineInRange("carrionMaggotDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Carrion Fly");
                CarrionFlyHealth = BUILDER.comment("How much Max Health Carrion Flies have, Default: 8.0")
                        .defineInRange("carrionFlyHealth", 8.0, 1.0, Double.MAX_VALUE);
                CarrionFlyArmor = BUILDER.comment("How much natural Armor Carrion Flies have, Default: 0.0")
                        .defineInRange("carrionFlyArmor", 0.0, 0.0, Double.MAX_VALUE);
                CarrionFlyDamage = BUILDER.comment("How much damage Carrion Flies melee attack deals, Default: 3.0")
                        .defineInRange("carrionFlyDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Whisperer");
                WhispererHealth = BUILDER.comment("How much Max Health Whisperers have, Default: 20.0")
                        .defineInRange("whispererHealth", 20.0, 1.0, Double.MAX_VALUE);
                WhispererArmor = BUILDER.comment("How much natural Armor Whisperers have, Default: 0.0")
                        .defineInRange("whispererArmor", 0.0, 0.0, Double.MAX_VALUE);
                WhispererDamage = BUILDER.comment("How much damage Whisperer melee attack deals, Default: 2.5")
                        .defineInRange("whispererDamage", 2.5, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Leapleaf");
                LeapleafHealth = BUILDER.comment("How much Max Health Leapleaves have, Default: 70.0")
                        .defineInRange("leapleafHealth", 70.0, 1.0, Double.MAX_VALUE);
                LeapleafArmor = BUILDER.comment("How much natural Armor Leapleaves have, Default: 0.0")
                        .defineInRange("leapleafArmor", 0.0, 0.0, Double.MAX_VALUE);
                LeapleafDamage = BUILDER.comment("How much damage Leapleaves deals, Default: 10.0")
                        .defineInRange("leapleafDamage", 10.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Quick Growing Vine");
                QuickGrowingVineHealth = BUILDER.comment("How much Max Health Quick Growing Vines have, Default: 10.0")
                        .defineInRange("quickGrowingVineHealth", 10.0, 1.0, Double.MAX_VALUE);
                QuickGrowingVineArmor = BUILDER.comment("How much natural Armor Quick Growing Vines have, Default: 0.0")
                        .defineInRange("quickGrowingVineArmor", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Poison-Quill Vine");
                PoisonQuillVineHealth = BUILDER.comment("How much Max Health Poison-Quill Vines have, Default: 40.0")
                        .defineInRange("poisonQuillVineHealth", 40.0, 1.0, Double.MAX_VALUE);
                PoisonQuillVineArmor = BUILDER.comment("How much natural Armor Poison-Quill Vines have, Default: 0.0")
                        .defineInRange("poisonQuillVineArmor", 0.0, 0.0, Double.MAX_VALUE);
                PoisonQuillVineDamage = BUILDER.comment("How much damage Poison-Quill Vines' quills deals, Default: 2.5")
                        .defineInRange("poisonQuillVineDamage", 2.5, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Tamed Ravager");
                RavagerSaddleHealth = BUILDER.comment("How much Max Health Tamed Ravagers have when saddled, Default: 100.0")
                        .defineInRange("ravagerSaddleHealth", 100.0, 1.0, Double.MAX_VALUE);
                RavagerArmor = BUILDER.comment("How much natural Armor Tamed Ravagers have, Default: 0.0")
                        .defineInRange("ravagerArmor", 0.0, 0.0, Double.MAX_VALUE);
                RavagerDamage = BUILDER.comment("How much damage Tamed Ravagers deals, Default: 12.0")
                        .defineInRange("ravagerDamage", 12.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Watchling");
                WatchlingHealth = BUILDER.comment("How much Max Health Watchlings have, Default: 24.0")
                        .defineInRange("watchlingHealth", 24.0, 1.0, Double.MAX_VALUE);
                WatchlingArmor = BUILDER.comment("How much natural Armor Watchlings have, Default: 0.0")
                        .defineInRange("watchlingArmor", 0.0, 0.0, Double.MAX_VALUE);
                WatchlingDamage = BUILDER.comment("How much damage Watchlings deals, Default: 5.0")
                        .defineInRange("watchlingDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Blastling");
                BlastlingHealth = BUILDER.comment("How much Max Health Blastlings have, Default: 32.0")
                        .defineInRange("blastlingHealth", 32.0, 1.0, Double.MAX_VALUE);
                BlastlingArmor = BUILDER.comment("How much natural Armor Blastlings have, Default: 0.0")
                        .defineInRange("blastlingArmor", 0.0, 0.0, Double.MAX_VALUE);
                BlastlingDamage = BUILDER.comment("How much damage Blastlings deals, Default: 3.0")
                        .defineInRange("blastlingDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Snareling");
                SnarelingHealth = BUILDER.comment("How much Max Health Snarelings have, Default: 20.0")
                        .defineInRange("snarelingHealth", 20.0, 1.0, Double.MAX_VALUE);
                SnarelingArmor = BUILDER.comment("How much natural Armor Snarelings have, Default: 0.0")
                        .defineInRange("snarelingArmor", 0.0, 0.0, Double.MAX_VALUE);
                SnarelingDamage = BUILDER.comment("How much damage Snarelings deals, Default: 2.0")
                        .defineInRange("snarelingDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Stone Ministrosity");
                StoneMinistrosityHealth = BUILDER.comment("How much Max Health Stone Ministrosities have, Default: 10.0")
                        .defineInRange("stoneMinistrosityHealth", 10.0, 1.0, Double.MAX_VALUE);
                StoneMinistrosityArmor = BUILDER.comment("How much natural armor points Stone Ministrosities have, Default: 0.0")
                        .defineInRange("stoneMinistrosityArmor", 0.0, 0.0, Double.MAX_VALUE);
                StoneMinistrosityDamage = BUILDER.comment("How much damage Stone Ministrosities deals, Default: 4.0")
                        .defineInRange("stoneMinistrosityDamage", 4.0, 1.0, Double.MAX_VALUE);
                StoneMinistrosityFollowRange = BUILDER.comment("How much following/detection range Stone Ministrosities have, Default: 16.0")
                        .defineInRange("stoneMinistrosityFollowRange", 16.0, 1.0, 2048.0);
                BUILDER.pop();
                BUILDER.push("Redstone Ministrosity");
                RedstoneMinistrosityHealth = BUILDER.comment("How much Max Health Redstone Ministrosities have, Default: 15.0")
                        .defineInRange("redstoneMinistrosityHealth", 15.0, 1.0, Double.MAX_VALUE);
                RedstoneMinistrosityArmor = BUILDER.comment("How much natural armor points Redstone Ministrosities have, Default: 1.0")
                        .defineInRange("redstoneMinistrosityArmor", 1.0, 0.0, Double.MAX_VALUE);
                RedstoneMinistrosityDamage = BUILDER.comment("How much damage Redstone Ministrosities deals, Default: 5.0")
                        .defineInRange("redstoneMinistrosityDamage", 5.0, 1.0, Double.MAX_VALUE);
                RedstoneMinistrosityFollowRange = BUILDER.comment("How much following/detection range Redstone Ministrosities have, Default: 16.0")
                        .defineInRange("redstoneMinistrosityFollowRange", 16.0, 1.0, 2048.0);
                BUILDER.pop();
                BUILDER.push("Ice Golem");
                IceGolemHealth = BUILDER.comment("How much Max Health Ice Golems have, Default: 60.0")
                        .defineInRange("iceGolemHealth", 60.0, 1.0, Double.MAX_VALUE);
                IceGolemArmor = BUILDER.comment("How much natural armor points Ice Golems have, Default: 0.0")
                        .defineInRange("iceGolemArmor", 0.0, 0.0, Double.MAX_VALUE);
                IceGolemDamage = BUILDER.comment("How much damage Ice Golems deals, Default: 7.5")
                        .defineInRange("iceGolemDamage", 7.5, 1.0, Double.MAX_VALUE);
                IceGolemFollowRange = BUILDER.comment("How much following/detection range Ice Golems have, Default: 16.0")
                        .defineInRange("iceGolemFollowRange", 16.0, 1.0, 2048.0);
                BUILDER.pop();
                BUILDER.push("Squall Golem");
                SquallGolemHealth = BUILDER.comment("How much Max Health Squall Golems have, Default: 88.0")
                        .defineInRange("squallGolemHealth", 88.0, 1.0, Double.MAX_VALUE);
                SquallGolemArmor = BUILDER.comment("How much natural armor points Squall Golems have, Default: 0.0")
                        .defineInRange("squallGolemArmor", 0.0, 0.0, Double.MAX_VALUE);
                SquallGolemDamage = BUILDER.comment("How much damage Squall Golems deals, Default: 16.0")
                        .defineInRange("squallGolemDamage", 16.0, 1.0, Double.MAX_VALUE);
                SquallGolemFollowRange = BUILDER.comment("How much following/detection range Squall Golems have, Default: 32.0")
                        .defineInRange("squallGolemFollowRange", 32.0, 1.0, 2048.0);
                BUILDER.pop();
                BUILDER.push("Redstone Golem");
                RedstoneGolemHealth = BUILDER.comment("How much Max Health Redstone Golems have, Default: 240.0")
                        .defineInRange("redstoneGolemHealth", 240.0, 1.0, Double.MAX_VALUE);
                RedstoneGolemArmor = BUILDER.comment("How much natural armor points Redstone Golems have, Default: 0.0")
                        .defineInRange("redstoneGolemArmor", 0.0, 0.0, Double.MAX_VALUE);
                RedstoneGolemDamage = BUILDER.comment("How much damage Redstone Golems deals, Default: 20.0")
                        .defineInRange("redstoneGolemDamage", 20.0, 1.0, Double.MAX_VALUE);
                RedstoneGolemFollowRange = BUILDER.comment("How much following/detection range Redstone Golems have, Default: 32.0")
                        .defineInRange("redstoneGolemFollowRange", 32.0, 1.0, 2048.0);
                BUILDER.pop();
                BUILDER.push("Grave Golem");
                GraveGolemHealth = BUILDER.comment("How much Max Health Grave Golems have, Default: 180.0")
                        .defineInRange("graveGolemHealth", 180.0, 1.0, Double.MAX_VALUE);
                GraveGolemArmor = BUILDER.comment("How much natural armor points Grave Golems have, Default: 0.0")
                        .defineInRange("graveGolemArmor", 0.0, 0.0, Double.MAX_VALUE);
                GraveGolemDamage = BUILDER.comment("How much damage Grave Golems deals, Default: 16.0")
                        .defineInRange("graveGolemDamage", 16.0, 1.0, Double.MAX_VALUE);
                GraveGolemFollowRange = BUILDER.comment("How much following/detection range Grave Golems have, Default: 32.0")
                        .defineInRange("graveGolemFollowRange", 32.0, 1.0, 2048.0);
                HauntHealth = BUILDER.comment("How much Max Health Haunts have, Default: 6.0")
                        .defineInRange("hauntHealth", 6.0, 1.0, Double.MAX_VALUE);
                HauntArmor = BUILDER.comment("How much natural armor points Haunts have, Default: 0.0")
                        .defineInRange("hauntArmor", 0.0, 0.0, Double.MAX_VALUE);
                HauntDamage = BUILDER.comment("How much damage Haunts deals, Default: 2.0")
                        .defineInRange("hauntDamage", 2.0, 1.0, Double.MAX_VALUE);
                HauntFollowRange = BUILDER.comment("How much following/detection range Haunts have, Default: 32.0")
                        .defineInRange("hauntFollowRange", 32.0, 1.0, 2048.0);
                BUILDER.pop();
                BUILDER.push("Redstone Monstrosity");
                RedstoneMonstrosityHealth = BUILDER.comment("How much Max Health Redstone Monstrosities have, Default: 600.0")
                        .defineInRange("redstoneMonstrosityHealth", 600.0, 1.0, Double.MAX_VALUE);
                RedstoneMonstrosityArmor = BUILDER.comment("How much natural armor points Redstone Monstrosities have, Default: 0.0")
                        .defineInRange("redstoneMonstrosityArmor", 0.0, 0.0, Double.MAX_VALUE);
                RedstoneMonstrosityDamage = BUILDER.comment("How much damage Redstone Monstrosities deals, Default: 21.0")
                        .defineInRange("redstoneMonstrosityDamage", 21.0, 1.0, Double.MAX_VALUE);
                RedstoneMonstrosityFollowRange = BUILDER.comment("How much following/detection range Redstone Monstrosities have, Default: 32.0")
                        .defineInRange("redstoneMonstrosityFollowRange", 32.0, 1.0, 2048.0);
                RedstoneMonstrosityHPPercentDamage = BUILDER.comment("Redstone Monstrosities attack HP percent damage, Default: 0.08")
                        .defineInRange("redstoneMonstrosityHPPercentDamage", 0.08, 0.0, 1.0);
                RedstoneMonstrosityHurtRange = BUILDER.comment("How many blocks or distance away an attack can be from Redstone Monstrosities to damage them, Default: 20.0")
                        .defineInRange("redstoneMonstrosityHurtRange", 20.0, 6.0, Double.MAX_VALUE);
                RedstoneMonstrosityDamageCap = BUILDER.comment("The maximum amount of damage an Redstone Monstrosities can attain per hit, Default: 25.0")
                        .defineInRange("redstoneMonstrosityDamageCap", 25.0D, 1.0, Double.MAX_VALUE);
                RedstoneCubeHealth = BUILDER.comment("How much Max Health Redstone Cubes have, Default: 10.0")
                        .defineInRange("redstoneCubeHealth", 10.0, 1.0, Double.MAX_VALUE);
                RedstoneCubeArmor = BUILDER.comment("How much natural armor points Redstone Cubes have, Default: 0.0")
                        .defineInRange("redstoneCubeArmor", 0.0, 0.0, Double.MAX_VALUE);
                RedstoneCubeDamage = BUILDER.comment("How much damage Redstone Cubes deals, Default: 4.0")
                        .defineInRange("redstoneCubeDamage", 4.0, 1.0, Double.MAX_VALUE);
                RedstoneCubeFollowRange = BUILDER.comment("How much following/detection range Redstone Cubes have, Default: 16.0")
                        .defineInRange("redstoneCubeFollowRange", 16.0, 1.0, 2048.0);
                BUILDER.pop();
            BUILDER.pop();
            BUILDER.push("Mini-Bosses");
                BUILDER.push("Crone");
                CroneHealth = BUILDER.comment("How much Max Health Crones have, Default: 130.0")
                        .defineInRange("croneHealth", 130.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Heresiarch");
                HeresiarchHealth = BUILDER.comment("How much Max Health Heresiarch have, Default: 130.0")
                        .defineInRange("heresiarchHealth", 130.0, 1.0, Double.MAX_VALUE);
                HeresiarchArmor = BUILDER.comment("How much natural Armor Points Heresiarch have, Default: 4.0")
                        .defineInRange("heresiarchArmor", 4.0, 0.0, Double.MAX_VALUE);
                HeresiarchDamage = BUILDER.comment("How much damage Heresiarch deals, Default: 2.0")
                        .defineInRange("heresiarchDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Minister");
                MinisterHealth = BUILDER.comment("How much Max Health Ministers have, Default: 128.0")
                        .defineInRange("ministerHealth", 128.0, 1.0, Double.MAX_VALUE);
                MinisterDamage = BUILDER.comment("How much damage Ministers deals, Default: 5.0")
                        .defineInRange("ministerDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Wight");
                WightHealth = BUILDER.comment("How much Max Health Wights have, Default: 200.0")
                        .defineInRange("wightHealth", 200.0, 1.0, Double.MAX_VALUE);
                WightDamage = BUILDER.comment("How much damage Wights deals, Default: 12.5")
                        .defineInRange("wightDamage", 12.5, 1.0, Double.MAX_VALUE);
                WightSoulAbsorb = BUILDER.comment("How much Soul Energy Wights absorb per attack, Default: 10")
                        .defineInRange("wightSoulAbsorb", 10, 0, Integer.MAX_VALUE);
                WightSoulHeal = BUILDER.comment("How much health Wights can heal up per Soul Energy absorption, Default: 10")
                        .defineInRange("wightSoulHeal", 10, 0, Integer.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Endersent");
                EndersentHealth = BUILDER.comment("How much Max Health Endersents have, Default: 200.0")
                        .defineInRange("endersentHealth", 200.0, 1.0, Double.MAX_VALUE);
                EndersentArmor = BUILDER.comment("How much natural Armor Points Endersents have, Default: 0.0")
                        .defineInRange("endersentArmor", 0.0, 0.0, Double.MAX_VALUE);
                EndersentDamage = BUILDER.comment("How much damage Endersents deals, Default: 14.0")
                        .defineInRange("endersentDamage", 14.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skull Lord");
                SkullLordHealth = BUILDER.comment("How much Max Health Skull Lord have, Default: 150.0")
                        .defineInRange("skullLordHealth", 150.0, 1.0, Double.MAX_VALUE);
                SkullLordDamage = BUILDER.comment("How much damage Skull Lord deals, Default: 6.0")
                        .defineInRange("skullLordDamage", 6.0, 1.0, Double.MAX_VALUE);
                BoneLordHealth = BUILDER.comment("How much Max Health Bone Lord have, Default: 20.0")
                        .defineInRange("boneLordHealth", 20.0, 1.0, Double.MAX_VALUE);
                BoneLordDamage = BUILDER.comment("How much damage Bone Lord deals, Default: 3.0")
                        .defineInRange("boneLordDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
            BUILDER.pop();
            BUILDER.push("Bosses");
                BUILDER.push("Vizier");
                VizierHealth = BUILDER.comment("How much Max Health Viziers have, Default: 300.0")
                        .defineInRange("vizierHealth", 300.0, 100.0, Double.MAX_VALUE);
                VizierDamage = BUILDER.comment("How much damage Viziers deals without weapons, Default: 5.0")
                        .defineInRange("vizierDamage", 5.0, 1.0, Double.MAX_VALUE);
                VizierDamageCap = BUILDER.comment("The maximum amount of damage a Vizier can attain per hit, Default: 20.0")
                        .defineInRange("vizierDamageCap", 20.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Ender Keeper");
                EnderKeeperHealth = BUILDER.comment("How much Max Health Ender Keeper have, Default: 320.0")
                        .defineInRange("enderKeeperHealth", 320.0, 100.0, Double.MAX_VALUE);
                EnderKeeperArmor = BUILDER.comment("How much natural Armor Points Ender Keepers have, Default: 10.0")
                        .defineInRange("enderKeeperArmor", 10.0, 0.0, Double.MAX_VALUE);
                EnderKeeperDamageCap = BUILDER.comment("The maximum amount of damage an Ender Keeper can attain per hit, Default: 20.0")
                        .defineInRange("enderKeeperDamageCap", 20.0, 1.0, Double.MAX_VALUE);
                EnderKeeperDamage = BUILDER.comment("How much damage Ender Keeper deals, Default: 14.0")
                        .defineInRange("enderKeeperDamage", 14.0, 1.0, Double.MAX_VALUE);
                EnderKeeperHPPercentDamage = BUILDER.comment("Ender Keepers attack HP percent damage, Default: 0.05")
                        .defineInRange("enderKeeperHPPercentDamage", 0.05, 0.0, 1.0);
                EnderKeeperHurtRange = BUILDER.comment("How many blocks or distance away an attack can be from Ender Keeper to damage them, Default: 12.0")
                        .defineInRange("enderKeeperHurtRange", 12.0, 6.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Apostle");
                ApostleHealth = BUILDER.comment("How much Max Health Apostles have, Default: 320.0")
                        .defineInRange("apostleHealth", 320.0, 100.0, Double.MAX_VALUE);
                ApostleArmor = BUILDER.comment("How much natural Armor Points Apostles have, Default: 12.0")
                        .defineInRange("apostleArmor", 12.0, 0.0, Double.MAX_VALUE);
                ApostleToughness = BUILDER.comment("How much natural Toughness Points Apostles have, Default: 6.0")
                        .defineInRange("apostleToughness", 6.0, 0.0, Double.MAX_VALUE);
                ApostleDamageCap = BUILDER.comment("The maximum amount of damage an Apostle can attain per hit, Default: 20.0")
                        .defineInRange("apostleDamageCap", 20.0, 1.0, Double.MAX_VALUE);
                ApostleBowDamage = BUILDER.comment("Multiplies Apostle's Bow damage, Default: 2")
                        .defineInRange("apostleBowDamage", 2, 1, Integer.MAX_VALUE);
                ApostleMagicDamage = BUILDER.comment("Set Apostle's spell damage (ie, Fire Tornadoes, Fire Blasts, Roars), Default: 6.0")
                        .defineInRange("apostleMagicDamage", 6.0, 6.0, Double.MAX_VALUE);
                BUILDER.pop();
            BUILDER.pop();
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
