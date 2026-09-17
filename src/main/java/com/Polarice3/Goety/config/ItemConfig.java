package com.Polarice3.Goety.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ItemConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> CraftingSouls;
    public static final ModConfigSpec.ConfigValue<Integer> DarkScytheSouls;
    public static final ModConfigSpec.ConfigValue<Integer> ReviveSecondsCool;
    public static final ModConfigSpec.ConfigValue<Integer> PendantOfHungerLimit;
    public static final ModConfigSpec.ConfigValue<Integer> SeaAmuletChargeConsume;
    public static final ModConfigSpec.ConfigValue<Integer> SeaAmuletMax;
    public static final ModConfigSpec.ConfigValue<Integer> WindRobeSouls;
    public static final ModConfigSpec.ConfigValue<Integer> ItemsRepairAmount;
    public static final ModConfigSpec.ConfigValue<Integer> SpitefulBeltUseAmount;
    public static final ModConfigSpec.ConfigValue<Integer> TesseractCapacity;
    public static final ModConfigSpec.ConfigValue<Integer> TesseractDurability;
    public static final ModConfigSpec.ConfigValue<Double> CommandHornDiameter;
    public static final ModConfigSpec.ConfigValue<Integer> HauntedBroomSoulDistance;
    public static final ModConfigSpec.ConfigValue<Integer> HauntedBroomSouls;

    public static final ModConfigSpec.ConfigValue<Integer> WitchRobeResistance;
    public static final ModConfigSpec.ConfigValue<Integer> WarlockRobeResistance;
    public static final ModConfigSpec.ConfigValue<Integer> FrostRobeResistance;
    public static final ModConfigSpec.ConfigValue<Integer> StormRobeResistance;
    public static final ModConfigSpec.ConfigValue<Integer> VoidRobeWaterSapped;
    public static final ModConfigSpec.ConfigValue<Integer> VoidRobeTeleportChance;
    public static final ModConfigSpec.ConfigValue<Integer> VoidRobeTeleportDistance;
    public static final ModConfigSpec.ConfigValue<Integer> NetherRobeResistance;
    public static final ModConfigSpec.ConfigValue<Integer> UnholyHatNetherResistance;

    public static final ModConfigSpec.ConfigValue<Boolean> StaffOffhandBuff;
    public static final ModConfigSpec.ConfigValue<Double> OminousStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> NecroStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> GeoStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> WindStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> StormStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> FrostStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> WildStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> AbyssStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> VoidStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> NetherStaffDamage;
    public static final ModConfigSpec.ConfigValue<Double> NamelessStaffDamage;

    public static final ModConfigSpec.ConfigValue<Double> ScytheBaseDamage;
    public static final ModConfigSpec.ConfigValue<Double> ScytheAttackSpeed;

    public static final ModConfigSpec.ConfigValue<Double> HammerBaseDamage;
    public static final ModConfigSpec.ConfigValue<Double> HammerAttackSpeed;
    public static final ModConfigSpec.ConfigValue<Integer> HammerDurability;

    public static final ModConfigSpec.ConfigValue<Double> DarkToolsDamage;
    public static final ModConfigSpec.ConfigValue<Double> DarkToolsBreakSpeed;
    public static final ModConfigSpec.ConfigValue<Integer> DarkToolsDurability;
    public static final ModConfigSpec.ConfigValue<Integer> DarkToolsEnchantability;
    public static final ModConfigSpec.ConfigValue<Integer> DarkToolsMiningLevel;
    public static final ModConfigSpec.ConfigValue<Boolean> DarkToolsPersist;

    public static final ModConfigSpec.ConfigValue<Double> DeathScytheDamage;
    public static final ModConfigSpec.ConfigValue<Integer> DeathScytheDurability;
    public static final ModConfigSpec.ConfigValue<Integer> DeathScytheEnchantability;
    public static final ModConfigSpec.ConfigValue<Integer> DeathScytheSappedDuration;
    public static final ModConfigSpec.ConfigValue<Integer> DeathScytheSappedChance;
    public static final ModConfigSpec.ConfigValue<Boolean> DeathScythePersist;

    public static final ModConfigSpec.ConfigValue<Double> SpecialToolsDamage;
    public static final ModConfigSpec.ConfigValue<Double> SpecialToolsBreakSpeed;
    public static final ModConfigSpec.ConfigValue<Integer> SpecialToolsDurability;
    public static final ModConfigSpec.ConfigValue<Integer> SpecialToolsMiningLevel;
    public static final ModConfigSpec.ConfigValue<Integer> HuntersBowDurability;
    public static final ModConfigSpec.ConfigValue<Integer> SpecialToolsEnchantability;
    public static final ModConfigSpec.ConfigValue<Integer> RampagingAxeDuration;
    public static final ModConfigSpec.ConfigValue<Boolean> GraverobberShovelCrouch;
    public static final ModConfigSpec.ConfigValue<Double> SickleBaseDamage;
    public static final ModConfigSpec.ConfigValue<Double> SickleAttackSpeed;

    public static final ModConfigSpec.ConfigValue<Double> PhilosophersMaceDamage;
    public static final ModConfigSpec.ConfigValue<Integer> PhilosophersMaceDurability;
    public static final ModConfigSpec.ConfigValue<Integer> PhilosophersMaceEnchantability;
    public static final ModConfigSpec.ConfigValue<Boolean> PhilosophersMacePersist;

    public static final ModConfigSpec.ConfigValue<Double> BladeOfEnderDamage;
    public static final ModConfigSpec.ConfigValue<Double> BladeOfEnderAttackSpeed;
    public static final ModConfigSpec.ConfigValue<Integer> BladeOfEnderDurability;
    public static final ModConfigSpec.ConfigValue<Integer> BladeOfEnderEnchantability;
    public static final ModConfigSpec.ConfigValue<Boolean> BladeOfEnderPersist;

    public static final ModConfigSpec.ConfigValue<Integer> CursedKnightDurability;
    public static final ModConfigSpec.ConfigValue<Integer> CursedKnightFeet;
    public static final ModConfigSpec.ConfigValue<Integer> CursedKnightLegs;
    public static final ModConfigSpec.ConfigValue<Integer> CursedKnightChest;
    public static final ModConfigSpec.ConfigValue<Integer> CursedKnightHead;
    public static final ModConfigSpec.ConfigValue<Integer> CursedKnightEnchantability;
    public static final ModConfigSpec.ConfigValue<Double> CursedKnightToughness;
    public static final ModConfigSpec.ConfigValue<Double> CursedKnightKnockResist;
    public static final ModConfigSpec.ConfigValue<Boolean> CursedKnightPersist;

    public static final ModConfigSpec.ConfigValue<Integer> CursedPaladinDurability;
    public static final ModConfigSpec.ConfigValue<Integer> CursedPaladinFeet;
    public static final ModConfigSpec.ConfigValue<Integer> CursedPaladinLegs;
    public static final ModConfigSpec.ConfigValue<Integer> CursedPaladinChest;
    public static final ModConfigSpec.ConfigValue<Integer> CursedPaladinHead;
    public static final ModConfigSpec.ConfigValue<Integer> CursedPaladinEnchantability;
    public static final ModConfigSpec.ConfigValue<Double> CursedPaladinToughness;
    public static final ModConfigSpec.ConfigValue<Double> CursedPaladinKnockResist;
    public static final ModConfigSpec.ConfigValue<Boolean> CursedPaladinPersist;

    public static final ModConfigSpec.ConfigValue<Integer> BlackIronDurability;
    public static final ModConfigSpec.ConfigValue<Integer> BlackIronFeet;
    public static final ModConfigSpec.ConfigValue<Integer> BlackIronLegs;
    public static final ModConfigSpec.ConfigValue<Integer> BlackIronChest;
    public static final ModConfigSpec.ConfigValue<Integer> BlackIronHead;
    public static final ModConfigSpec.ConfigValue<Integer> BlackIronEnchantability;
    public static final ModConfigSpec.ConfigValue<Double> BlackIronToughness;
    public static final ModConfigSpec.ConfigValue<Double> BlackIronKnockResist;
    public static final ModConfigSpec.ConfigValue<Boolean> BlackIronPersist;

    public static final ModConfigSpec.ConfigValue<Integer> DarkArmorDurability;
    public static final ModConfigSpec.ConfigValue<Integer> DarkArmorFeet;
    public static final ModConfigSpec.ConfigValue<Integer> DarkArmorLegs;
    public static final ModConfigSpec.ConfigValue<Integer> DarkArmorChest;
    public static final ModConfigSpec.ConfigValue<Integer> DarkArmorHead;
    public static final ModConfigSpec.ConfigValue<Integer> DarkArmorEnchantability;
    public static final ModConfigSpec.ConfigValue<Double> DarkArmorToughness;
    public static final ModConfigSpec.ConfigValue<Double> DarkArmorKnockResist;
    public static final ModConfigSpec.ConfigValue<Boolean> DarkArmorPersist;
    public static final ModConfigSpec.ConfigValue<Boolean> DarkHelmetBlindness;
    public static final ModConfigSpec.ConfigValue<Boolean> DarkHelmetDarkness;

    public static final ModConfigSpec.ConfigValue<Integer> MaleficDurability;
    public static final ModConfigSpec.ConfigValue<Integer> MaleficHead;
    public static final ModConfigSpec.ConfigValue<Integer> MaleficEnchantability;
    public static final ModConfigSpec.ConfigValue<Double> MaleficToughness;
    public static final ModConfigSpec.ConfigValue<Double> MaleficKnockResist;
    public static final ModConfigSpec.ConfigValue<Boolean> MaleficPersist;

    public static final ModConfigSpec.ConfigValue<Boolean> RobeCape;
    public static final ModConfigSpec.ConfigValue<Boolean> AbyssSetMobNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> GeoSetMobNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> FrostSetMobNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> WindSetMobNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> StormSetMobNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> VoidSetMobNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> VoidRobeTeleportDamageCancel;
    public static final ModConfigSpec.ConfigValue<Boolean> WildSetMobNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> NetherSetMobNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> NecroSetUndeadNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> NecroCrownWeakness;
    public static final ModConfigSpec.ConfigValue<Boolean> NecroCapeHunger;
    public static final ModConfigSpec.ConfigValue<Boolean> NecroCapeChangeTexture;
    public static final ModConfigSpec.ConfigValue<Boolean> NamelessSetUndeadNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> WitchSetWitchNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> WarlockRobeWitchNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> NetherRobeWitchNeutral;
    public static final ModConfigSpec.ConfigValue<Boolean> SoulRepair;
    public static final ModConfigSpec.ConfigValue<Boolean> SoulRepairEnchant;
    public static final ModConfigSpec.ConfigValue<Boolean> FirstPersonGloves;
    public static final ModConfigSpec.ConfigValue<Boolean> ShowRobeHoods;
    public static final ModConfigSpec.ConfigValue<Boolean> FireSpawnCage;
    public static final ModConfigSpec.ConfigValue<Boolean> ScytheSlashBreaks;

    public static final ModConfigSpec.ConfigValue<Integer> DarkRobeDiscount;
    public static final ModConfigSpec.ConfigValue<Integer> FrostRobeDiscount;
    public static final ModConfigSpec.ConfigValue<Integer> WindRobeDiscount;
    public static final ModConfigSpec.ConfigValue<Integer> StormRobeDiscount;
    public static final ModConfigSpec.ConfigValue<Integer> WildRobeDiscount;
    public static final ModConfigSpec.ConfigValue<Integer> GeoRobeDiscount;
    public static final ModConfigSpec.ConfigValue<Integer> NetherRobeDiscount;
    public static final ModConfigSpec.ConfigValue<Integer> AbyssRobeDiscount;
    public static final ModConfigSpec.ConfigValue<Integer> VoidRobeDiscount;

    public static final ModConfigSpec.ConfigValue<Double> AbyssSetMobNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> GeoSetMobNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> FrostSetMobNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> WindSetMobNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> StormSetMobNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> VoidSetMobNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> WildSetMobNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> NetherSetMobNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> NecroSetUndeadNeutralHealth;
    public static final ModConfigSpec.ConfigValue<Double> NamelessSetUndeadNeutralHealth;

    static {
        BUILDER.push("General");
        SoulRepair = BUILDER.comment("Certain Items repair themselves using Soul Energy, Default: true")
                .define("soulRepair", true);
        SoulRepairEnchant = BUILDER.comment("If soulRepair is enabled, the cost of Soul Energy multiplies per item's enchantments, Default: true")
                .define("soulRepairEnchant", true);
        CraftingSouls = BUILDER.comment("How much Souls is consumed when crafting with Totem, Default: 1")
                .defineInRange("craftSouls", 1, 0, Integer.MAX_VALUE);
        ItemsRepairAmount = BUILDER.comment("Amount of Souls needed to repair certain Equipments per second, Default: 5")
                .defineInRange("itemsRepairSouls", 5, 1, Integer.MAX_VALUE);
        FireSpawnCage = BUILDER.comment("Fire Spawn Cage are enabled, Default: true")
                .define("fireSpawnCage", true);
        ReviveSecondsCool = BUILDER.comment("How many seconds until a revival item can be used when the servant dies, ie, Soul Jars, Howling Souls, Default: 90")
                .defineInRange("reviveSecondsCool", 90, 0, Integer.MAX_VALUE);
        TesseractCapacity = BUILDER.comment("How many servants an Esoteric Tesseract can contain, Default: 16")
                .defineInRange("tesseractCapacity", 16, 1, Integer.MAX_VALUE);
        TesseractDurability = BUILDER.comment("How many uses before Tesseract is disabled, Default: 16")
                .defineInRange("tesseractDurability", 16, 1, Integer.MAX_VALUE);
        CommandHornDiameter = BUILDER.comment("How far the diameter of Command Horns reach, Default: 8.0")
                .defineInRange("commandHornDiameter", 8.0D, 1.0D, Double.MAX_VALUE);
        HauntedBroomSoulDistance = BUILDER.comment("How many blocks from its previous location will the Haunted Broom take Soul Energy, set to 0 to disable, Default: 8")
                .defineInRange("hauntedBroomSoulDistance", 8, 0, Integer.MAX_VALUE);
        HauntedBroomSouls = BUILDER.comment("How much Soul Energy is taken every 'hauntedBroomSoulDistance' blocks flown with Haunted Brooms, Default: 10")
                .defineInRange("hauntedBroomSouls", 10, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Curios");
            BUILDER.push("Robes");
                BUILDER.push("Discounts");
                DarkRobeDiscount = BUILDER.comment("How much soul cost discount Dark/Grand Robes provides by percent, Default: 15")
                        .defineInRange("darkRobeDiscount", 15, 0, 100);
                FrostRobeDiscount = BUILDER.comment("How much soul cost discount Frost Robes provides by percent, Default: 50")
                        .defineInRange("frostRobeDiscount", 50, 0, 100);
                WindRobeDiscount = BUILDER.comment("How much soul cost discount Wind Robes provides by percent, Default: 50")
                        .defineInRange("windRobeDiscount", 50, 0, 100);
                StormRobeDiscount = BUILDER.comment("How much soul cost discount Storm Robes provides by percent, Default: 50")
                        .defineInRange("stormRobeDiscount", 50, 0, 100);
                WildRobeDiscount = BUILDER.comment("How much soul cost discount Wild Robes provides by percent, Default: 50")
                        .defineInRange("wildRobeDiscount", 50, 0, 100);
                GeoRobeDiscount = BUILDER.comment("How much soul cost discount Geo Robes provides by percent, Default: 50")
                        .defineInRange("geoRobeDiscount", 50, 0, 100);
                NetherRobeDiscount = BUILDER.comment("How much soul cost discount Nether/Unholy Robes provides by percent, Default: 50")
                        .defineInRange("netherRobeDiscount", 50, 0, 100);
                AbyssRobeDiscount = BUILDER.comment("How much soul cost discount Abyss Robes provides by percent, Default: 50")
                        .defineInRange("abyssRobeDiscount", 50, 0, 100);
                VoidRobeDiscount = BUILDER.comment("How much soul cost discount Void Robes provides by percent, Default: 50")
                        .defineInRange("voidRobeDiscount", 50, 0, 100);
                BUILDER.pop();
                BUILDER.push("Resistances");
                WindRobeSouls = BUILDER.comment("How much Soul Energy is taken per second when wearer is falling slowly, Default: 1")
                        .defineInRange("windRobeSouls", 1, 1, Integer.MAX_VALUE);
                WitchRobeResistance = BUILDER.comment("How much magic resistance Witches Robes provides by percent, Default: 85")
                        .defineInRange("witchRobeResistance", 85, 0, 100);
                WarlockRobeResistance = BUILDER.comment("How much magic resistance Warlock Robes provides by percent, Default: 85")
                        .defineInRange("warlockRobeResistance", 85, 0, 100);
                FrostRobeResistance = BUILDER.comment("How much frost resistance Frost Robes provides by percent, Default: 85")
                        .defineInRange("frostRobeResistance", 85, 0, 100);
                StormRobeResistance = BUILDER.comment("How much shock resistance Storm Robes provides by percent, Default: 85")
                        .defineInRange("stormRobeResistance", 85, 0, 100);
                NetherRobeResistance = BUILDER.comment("How much fire resistance Nether Robes provides by percent, Default: 85")
                        .defineInRange("netherRobeResistance", 85, 0, 100);
                UnholyHatNetherResistance = BUILDER.comment("How much damage is reduced when in the Nether the Unholy Hats provides by percent, Default: 50")
                        .defineInRange("unholyHatNetherResistance", 50, 0, 100);
                BUILDER.pop();
                BUILDER.push("Neutral");
                AbyssSetMobNeutral = BUILDER.comment("Whether wearing both Abyss Robe and Crown will cause certain mobs to be neutral, Default: true")
                        .define("abyssSetMobNeutral", true);
                AbyssSetMobNeutralHealth = BUILDER.comment("If 'abyssSetMobNeutral' is enabled, the highest max health affected mobs has to have to be neutral, Default: 50.0")
                        .defineInRange("abyssSetMobNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                GeoSetMobNeutral = BUILDER.comment("Whether wearing both Geo Robe and Amethyst Necklace will cause certain mobs to be neutral, Default: true")
                        .define("geoSetMobNeutral", true);
                GeoSetMobNeutralHealth = BUILDER.comment("If 'geoSetMobNeutral' is enabled, the highest max health affected mobs has to have to be neutral, Default: 50.0")
                        .defineInRange("geoSetMobNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                FrostSetMobNeutral = BUILDER.comment("Whether wearing both Frost Robe and Crown will cause certain mobs to be neutral, Default: true")
                        .define("frostSetMobNeutral", true);
                FrostSetMobNeutralHealth = BUILDER.comment("If 'frostSetMobNeutral' is enabled, the highest max health affected mobs has to have to be neutral, Default: 50.0")
                        .defineInRange("frostSetMobNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                WindSetMobNeutral = BUILDER.comment("Whether wearing both Wind Robe and Crown will cause certain mobs to be neutral, Default: true")
                        .define("windSetMobNeutral", true);
                WindSetMobNeutralHealth = BUILDER.comment("If 'windSetMobNeutral' is enabled, the highest max health affected mobs has to have to be neutral, Default: 50.0")
                        .defineInRange("windSetMobNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                StormSetMobNeutral = BUILDER.comment("Whether wearing both Storm Robe and Crown will cause certain mobs to be neutral, Default: true")
                        .define("stormSetMobNeutral", true);
                StormSetMobNeutralHealth = BUILDER.comment("If 'stormSetMobNeutral' is enabled, the highest max health affected mobs has to have to be neutral, Default: 50.0")
                        .defineInRange("stormSetMobNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                VoidSetMobNeutral = BUILDER.comment("Whether wearing both Void Robe and Crown will cause certain mobs to be neutral, Default: true")
                        .define("voidSetMobNeutral", true);
                VoidSetMobNeutralHealth = BUILDER.comment("If 'voidSetMobNeutral' is enabled, the highest max health affected mobs has to have to be neutral, Default: 50.0")
                        .defineInRange("voidSetMobNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                WildSetMobNeutral = BUILDER.comment("Whether wearing both Wild Robe and Crown will cause certain mobs to be neutral, Default: true")
                        .define("wildSetMobNeutral", true);
                WildSetMobNeutralHealth = BUILDER.comment("If 'wildSetMobNeutral' is enabled, the highest max health affected mobs has to have to be neutral, Default: 50.0")
                        .defineInRange("wildSetMobNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                NetherSetMobNeutral = BUILDER.comment("Whether wearing both Nether Robe and Crown will cause certain mobs to be neutral, Default: true")
                        .define("netherSetMobNeutral", true);
                NetherSetMobNeutralHealth = BUILDER.comment("If 'netherSetMobNeutral' is enabled, the highest max health affected mobs has to have to be neutral, Default: 50.0")
                        .defineInRange("netherSetMobNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                NecroSetUndeadNeutral = BUILDER.comment("Whether wearing both Necro Cape and Crown will cause Undead mobs to be neutral, Default: true")
                        .define("necroSetUndeadNeutral", true);
                NecroSetUndeadNeutralHealth = BUILDER.comment("If 'necroSetUndeadNeutral' is enabled, the highest max health an Undead mob has to have to be neutral, Default: 50.0")
                        .defineInRange("necroSetUndeadNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                NamelessSetUndeadNeutral = BUILDER.comment("Whether wearing both Nameless Cape and Crown will cause Undead mobs to be neutral, Default: true")
                        .define("namelessSetUndeadNeutral", true);
                NamelessSetUndeadNeutralHealth = BUILDER.comment("If 'namelessSetUndeadNeutral' is enabled, the highest max health an Undead mob has to have to be neutral, Default: 50.0")
                        .defineInRange("namelessSetUndeadNeutralHealth", 50.0, 1.0, Double.MAX_VALUE);
                WitchSetWitchNeutral = BUILDER.comment("Whether wearing both Witch Robe and Hat will cause Witches and Warlocks to be neutral, Default: true")
                        .define("witchSetWitchNeutral", true);
                WarlockRobeWitchNeutral = BUILDER.comment("Whether wearing a Warlock Robe will cause Witches and Warlocks to be neutral, Default: true")
                        .define("warlockRobeWitchNeutral", true);
                NetherRobeWitchNeutral = BUILDER.comment("Whether wearing a Nether Robe will cause Witches and Warlocks to be neutral, Default: true")
                        .define("netherRobeWitchNeutral", true);
                BUILDER.pop();
            ShowRobeHoods = BUILDER.comment("Show Hoods when wearing certain robes ie, Illusive Robes, Default: true")
                    .define("showRobeHoods", true);
            RobeCape = BUILDER.comment("Render Capes on certain Robes, Default: true")
                    .define("windRobeCape", true);
            VoidRobeTeleportChance = BUILDER.comment("Determines the chance of Void Robes wearers can teleport away from incoming attacks, set to 0 to disable, Default: 5")
                    .defineInRange("voidRobeTeleportChance", 5, 0, 100);
            VoidRobeTeleportDistance = BUILDER.comment("Determines how far Void Robes wearers can teleport away from original position, Default: 4")
                    .defineInRange("voidRobeTeleportDistance", 4, 1, Integer.MAX_VALUE);
            VoidRobeTeleportDamageCancel = BUILDER.comment("If 'voidRobeTeleport' is enabled, teleporting cancels incoming damage, Default: true")
                    .define("voidRobeTeleportDamageCancel", true);
            VoidRobeWaterSapped = BUILDER.comment("Determines what level of Sapped is applied while wearing Void Robes in rain or water, set to 0 to disable, Default: 2")
                    .defineInRange("voidRobeWaterSapped", 2, 0, 10);
            NecroCrownWeakness = BUILDER.comment("Whether wearing Necro Crown while not being a Lich gives Weakness when in sunlight, Default: true")
                    .define("necroCrownWeakness", true);
            NecroCapeHunger = BUILDER.comment("Whether wearing Necro Cape while not being a Lich gives Hunger when in sunlight, Default: true")
                    .define("necroCapeHunger", true);
            NecroCapeChangeTexture = BUILDER.comment("Necro Crown and Cape change textures when wearing a specific robe, Default: true")
                    .define("necroCapeChangeTexture", true);
            BUILDER.pop();
        FirstPersonGloves = BUILDER.comment("Show gloves in first person, Default: true")
                .define("firstPersonGloves", true);
        SpitefulBeltUseAmount = BUILDER.comment("Amount of Souls needed to use Spiteful Belt functionality, Default: 5")
                .defineInRange("spitefulBeltUseAmount", 5, 1, Integer.MAX_VALUE);
        PendantOfHungerLimit = BUILDER.comment("The total amount of Rotten Flesh a Pendant of Hunger can hold, Default: 1024")
                .defineInRange("pendantOfHungerLimit", 1024, 1, Integer.MAX_VALUE);
        SeaAmuletChargeConsume = BUILDER.comment("How much Charges the Sea Amulet needs to consume to function, Default: 1")
                .defineInRange("seaAmuletChargeConsume", 1, 0, Integer.MAX_VALUE);
        SeaAmuletMax = BUILDER.comment("The total amount of Charges a Sea Amulet can hold, Default: 1024")
                .defineInRange("seaAmuletMax", 1024, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Tools & Weapons");
            BUILDER.push("Staffs");
            StaffOffhandBuff = BUILDER.comment("Holding a Staff offhand increases wielder's melee attack damage by 25%, Default: true")
                    .define("staffOffhandBuff", true);
            OminousStaffDamage = BUILDER.comment("How much base damage Ominous Staffs deals, Default: 4.0")
                    .defineInRange("ominousStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            NecroStaffDamage = BUILDER.comment("How much base damage Necro Staffs deals, Default: 4.0")
                    .defineInRange("necroStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            GeoStaffDamage = BUILDER.comment("How much base damage Geo Staffs deals, Default: 4.0")
                    .defineInRange("geoStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            WindStaffDamage = BUILDER.comment("How much base damage Wind Staffs deals, Default: 4.0")
                    .defineInRange("windStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            StormStaffDamage = BUILDER.comment("How much base damage Storm Staffs deals, Default: 4.0")
                    .defineInRange("stormStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            FrostStaffDamage = BUILDER.comment("How much base damage Frost Staffs deals, Default: 4.0")
                    .defineInRange("frostStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            WildStaffDamage = BUILDER.comment("How much base damage Wild Staffs deals, Default: 4.0")
                    .defineInRange("wildStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            AbyssStaffDamage = BUILDER.comment("How much base damage Abyss Staffs deals, Default: 9.0")
                    .defineInRange("abyssStaffDamage", 9.0, 1.0, Double.MAX_VALUE);
            VoidStaffDamage = BUILDER.comment("How much base damage Void Staffs deals, Default: 4.0")
                    .defineInRange("voidStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            NetherStaffDamage = BUILDER.comment("How much base damage Nether Staffs deals, Default: 4.0")
                    .defineInRange("netherStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            NamelessStaffDamage = BUILDER.comment("How much base damage Nameless Staffs deals, Default: 6.0")
                    .defineInRange("namelessStaffDamage", 6.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Scythes");
            DarkScytheSouls = BUILDER.comment("Amount of Soul Energy Scythes gives when hitting mob(s), Default: 1")
                    .defineInRange("darkScytheSouls", 1, 1, Integer.MAX_VALUE);
            ScytheSlashBreaks = BUILDER.comment("Scythe Slashes from Death Scythe breaks blocks that regular Scythes easily breaks, Default: true")
                    .define("scytheSlashBreaks", true);
            ScytheBaseDamage = BUILDER.comment("How much base damage Scythes deals, the damage added depends on material the scythe is made off (ie. Iron = 2.0), Default: 4.5")
                    .defineInRange("scytheBaseDamage", 4.5, 1.0, Double.MAX_VALUE);
            ScytheAttackSpeed = BUILDER.comment("How fast it takes to fully swing a Scythe with item offhand and not wearing Grave Gloves. The lower the number the slower it takes to recharge, Default: 0.6")
                    .defineInRange("scytheAttackSpeed", 0.6, 0.0, Double.MAX_VALUE);
            DeathScytheDamage = BUILDER.comment("How much damage Death Scythe deals, the configured number is added to Scythe Base Damage, Default: 4.0")
                    .defineInRange("deathScytheDamage", 4.0, 1.0, Double.MAX_VALUE);
            DeathScytheDurability = BUILDER.comment("How many uses before Death Scythe breaks, Default: 444")
                    .defineInRange("deathScytheDurability", 444, 1, Integer.MAX_VALUE);
            DeathScytheEnchantability = BUILDER.comment("Define the Enchantability for Death Scythe, higher number the better, Default: 22")
                    .defineInRange("deathScytheEnchantability", 22, 1, Integer.MAX_VALUE);
            DeathScytheSappedDuration = BUILDER.comment("How many seconds of Sapped the Death Scythe applies, Default: 5")
                    .defineInRange("deathScytheSappedDuration", 5, 0, Integer.MAX_VALUE);
            DeathScytheSappedChance = BUILDER.comment("The chance, by percentage, of Sapped being amplified after hitting affected with Death Scythe, Default: 20")
                    .defineInRange("deathScytheSappedDuration", 20, 0, Integer.MAX_VALUE);
            DeathScythePersist = BUILDER.comment("Whether tools don't break but no longer benefits, Default: true")
                    .define("deathScythePersist", true);
            BUILDER.pop();
            BUILDER.push("Hammers");
            HammerBaseDamage = BUILDER.comment("How much base damage Hammers deals, the damage added depends on material the hammer is made off (ie. Iron = 2.0), Default: 5.0")
                    .defineInRange("hammerBaseDamage", 5.0, 1.0, Double.MAX_VALUE);
            HammerAttackSpeed = BUILDER.comment("How fast it takes to fully swing a Hammers. The lower the number the slower it takes to recharge, Default: 0.5")
                    .defineInRange("hammerAttackSpeed", 0.5, 0.0, Double.MAX_VALUE);
            HammerDurability = BUILDER.comment("How many uses before Hammers breaks, Default: 2500")
                    .defineInRange("hammerDurability", 2500, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Dark Tools");
            DarkToolsDamage = BUILDER.comment("How much damage Dark Tools deals, the configured number is added to Tool Base Damage, Default: 3.0")
                    .defineInRange("darkToolsDamage", 3.0, 1.0, Double.MAX_VALUE);
            DarkToolsBreakSpeed = BUILDER.comment("How fast Dark Tools mines, the higher the better, Default: 8.0")
                    .defineInRange("darkToolsBreakSpeed", 8.0, 1.0, Double.MAX_VALUE);
            DarkToolsDurability = BUILDER.comment("How many uses before Dark Tools breaks, Default: 166")
                    .defineInRange("darkToolsDurability", 166, 1, Integer.MAX_VALUE);
            DarkToolsEnchantability = BUILDER.comment("Define the Enchantability for Dark Tools, higher number the better, Default: 20")
                    .defineInRange("darkToolsEnchantability", 20, 1, Integer.MAX_VALUE);
            DarkToolsMiningLevel = BUILDER.comment("Define the Mining Level for Dark Tools, example, 3 = Diamond, 4 = Netherite, Default: 4")
                    .defineInRange("darkToolsMiningLevel", 4, 0, Integer.MAX_VALUE);
            DarkToolsPersist = BUILDER.comment("Whether tools don't break but no longer benefits, Default: true")
                    .define("darkToolsPersist", true);
            BUILDER.pop();
            BUILDER.push("Special Tools");
            SpecialToolsDamage = BUILDER.comment("How much damage Special Tools deals, the configured number is added to Tool Base Damage, Default: 3.0")
                    .defineInRange("specialToolsDamage", 3.0, 1.0, Double.MAX_VALUE);
            SpecialToolsBreakSpeed = BUILDER.comment("How fast Special Tools mines, the higher the better, Default: 8.0")
                    .defineInRange("specialToolsBreakSpeed", 8.0, 1.0, Double.MAX_VALUE);
            SpecialToolsDurability = BUILDER.comment("How many uses before a Special Tool breaks, Default: 1280")
                    .defineInRange("specialToolsDurability", 1280, 1, Integer.MAX_VALUE);
            HuntersBowDurability = BUILDER.comment("How many uses before a Hunter's Bow breaks, Default: 133")
                    .defineInRange("huntersBowDurability", 133, 1, Integer.MAX_VALUE);
            SpecialToolsEnchantability = BUILDER.comment("Define the Enchantability for Special Tools, higher number the better, Default: 22")
                    .defineInRange("specialToolsEnchantability", 22, 1, Integer.MAX_VALUE);
            SpecialToolsMiningLevel = BUILDER.comment("Define the Mining Level for Special Tools, example, 3 = Diamond, 4 = Netherite, Default: 3")
                    .defineInRange("specialToolsMiningLevel", 3, 0, Integer.MAX_VALUE);
            RampagingAxeDuration = BUILDER.comment("How many seconds Rampaging Axe gives Rampage for, Default: 10")
                    .defineInRange("rampagingAxeDuration", 10, 1, Integer.MAX_VALUE);
            GraverobberShovelCrouch = BUILDER.comment("Whether Graverobber's Shovel only breaks two blocks if player is crouching, set to false to make it so that crouching breaks one block instead of two, Default: false")
                    .define("graverobberShovelCrouch", false);
            SickleBaseDamage = BUILDER.comment("How much base damage Sickles deals, the damage added depends on material the sickle is made off (ie. Iron = 2.0), Default: 0.5")
                    .defineInRange("sickleBaseDamage", 0.5, 0.0, Double.MAX_VALUE);
            SickleAttackSpeed = BUILDER.comment("How fast it takes to fully swing a Sickle. The lower the number the slower it takes to recharge, Default: 3.0")
                    .defineInRange("sickleAttackSpeed", 3.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Philosopher's Mace");
            PhilosophersMaceDamage = BUILDER.comment("How much damage Philosopher's Mace deals, Default: 9.0")
                    .defineInRange("philosophersMaceDamage", 9.0, 1.0, Double.MAX_VALUE);
            PhilosophersMaceDurability = BUILDER.comment("How many uses before the Philosopher's Mace breaks, Default: 128")
                    .defineInRange("philosophersMaceDurability", 128, 1, Integer.MAX_VALUE);
            PhilosophersMaceEnchantability = BUILDER.comment("Define the Enchantability for Philosopher's Mace, higher number the better, Default: 20")
                    .defineInRange("philosophersMaceEnchantability", 20, 1, Integer.MAX_VALUE);
            PhilosophersMacePersist = BUILDER.comment("Whether tools don't break but no longer benefits, Default: true")
                    .define("philosophersMacePersist", true);
            BUILDER.pop();
            BUILDER.push("Blade of Ender");
            BladeOfEnderDamage = BUILDER.comment("How much damage Blade of Ender deals, Default: 9.0")
                    .defineInRange("bladeOfEnderDamage", 9.0, 1.0, Double.MAX_VALUE);
            BladeOfEnderAttackSpeed = BUILDER.comment("How fast it takes to fully swing a Blade of Ender. The lower the number the slower it takes to recharge, Default: 1.2")
                    .defineInRange("bladeOfEnderAttackSpeed", 1.2, 0.0, Double.MAX_VALUE);
            BladeOfEnderDurability = BUILDER.comment("How many uses before the Blade of Ender breaks, Default: 2031")
                    .defineInRange("bladeOfEnderDurability", 2031, 1, Integer.MAX_VALUE);
            BladeOfEnderEnchantability = BUILDER.comment("Define the Enchantability for Blade of Ender, higher number the better, Default: 20")
                    .defineInRange("bladeOfEnderEnchantability", 20, 1, Integer.MAX_VALUE);
            BladeOfEnderPersist = BUILDER.comment("Whether tools don't break but no longer benefits, Default: true")
                    .define("bladeOfEnderPersist", true);
            BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Armor");
            BUILDER.push("Cursed Knight Armor");
                BUILDER.push("Armor Points");
                CursedKnightFeet = BUILDER.comment("Define how much armor points wearing the Boots provides, Default: 2")
                        .defineInRange("cursedKnightFeet", 2, 1, Integer.MAX_VALUE);
                CursedKnightLegs = BUILDER.comment("Define how much armor points wearing the Leggings provides, Default: 5")
                        .defineInRange("cursedKnightLegs", 5, 1, Integer.MAX_VALUE);
                CursedKnightChest = BUILDER.comment("Define how much armor points wearing the Chestplate provides, Default: 6")
                        .defineInRange("cursedKnightChest", 6, 1, Integer.MAX_VALUE);
                CursedKnightHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 2")
                        .defineInRange("cursedKnightHead", 2, 1, Integer.MAX_VALUE);
                BUILDER.pop();
            CursedKnightDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 15")
                    .defineInRange("cursedKnightDurability", 15, 1, Integer.MAX_VALUE);
            CursedKnightEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 15")
                    .defineInRange("cursedKnightEnchantability", 15, 1, Integer.MAX_VALUE);
            CursedKnightToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 0.5")
                    .defineInRange("cursedKnightToughness", 0.5, 0.0, Double.MAX_VALUE);
            CursedKnightKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.0")
                    .defineInRange("cursedKnightKnockResist", 0.0, 0.0, Double.MAX_VALUE);
            CursedKnightPersist = BUILDER.comment("Whether armor pieces don't break but no longer provide armor, Default: true")
                    .define("cursedKnightPersist", true);
            BUILDER.pop();
            BUILDER.push("Cursed Paladin Armor");
                BUILDER.push("Armor Points");
                CursedPaladinFeet = BUILDER.comment("Define how much armor points wearing the Boots provides, Default: 3")
                        .defineInRange("cursedPaladinFeet", 3, 1, Integer.MAX_VALUE);
                CursedPaladinLegs = BUILDER.comment("Define how much armor points wearing the Leggings provides, Default: 6")
                        .defineInRange("cursedPaladinLegs", 6, 1, Integer.MAX_VALUE);
                CursedPaladinChest = BUILDER.comment("Define how much armor points wearing the Chestplate provides, Default: 8")
                        .defineInRange("cursedPaladinChest", 8, 1, Integer.MAX_VALUE);
                CursedPaladinHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 3")
                        .defineInRange("cursedPaladinHead", 3, 1, Integer.MAX_VALUE);
                BUILDER.pop();
            CursedPaladinDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 30")
                    .defineInRange("cursedPaladinDurability", 30, 1, Integer.MAX_VALUE);
            CursedPaladinEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 20")
                    .defineInRange("cursedPaladinEnchantability", 20, 1, Integer.MAX_VALUE);
            CursedPaladinToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 2.0")
                    .defineInRange("cursedPaladinToughness", 2.0, 0.0, Double.MAX_VALUE);
            CursedPaladinKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.0")
                    .defineInRange("cursedPaladinKnockResist", 0.0, 0.0, Double.MAX_VALUE);
            CursedPaladinPersist = BUILDER.comment("Whether armor pieces don't break but no longer provide armor, Default: true")
                    .define("cursedPaladinPersist", true);
            BUILDER.pop();
            BUILDER.push("Black Iron Armor");
                BUILDER.push("Armor Points");
                BlackIronFeet = BUILDER.comment("Define how much armor points wearing the Boots provides, Default: 2")
                        .defineInRange("blackIronFeet", 2, 1, Integer.MAX_VALUE);
                BlackIronLegs = BUILDER.comment("Define how much armor points wearing the Leggings provides, Default: 5")
                        .defineInRange("blackIronLegs", 5, 1, Integer.MAX_VALUE);
                BlackIronChest = BUILDER.comment("Define how much armor points wearing the Chestplate provides, Default: 6")
                        .defineInRange("blackIronChest", 6, 1, Integer.MAX_VALUE);
                BlackIronHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 2")
                        .defineInRange("blackIronHead", 2, 1, Integer.MAX_VALUE);
                BUILDER.pop();
            BlackIronDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 15")
                    .defineInRange("blackIronDurability", 30, 1, Integer.MAX_VALUE);
            BlackIronEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 15")
                    .defineInRange("blackIronEnchantability", 15, 1, Integer.MAX_VALUE);
            BlackIronToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 2.0")
                    .defineInRange("blackIronToughness", 2.0, 0.0, Double.MAX_VALUE);
            BlackIronKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.0")
                    .defineInRange("blackIronKnockResist", 0.0, 0.0, Double.MAX_VALUE);
            BlackIronPersist = BUILDER.comment("Whether armor pieces don't break but no longer provide armor, Default: true")
                    .define("blackIronPersist", true);
            BUILDER.pop();
            BUILDER.push("Dark Armor");
                BUILDER.push("Armor Points");
                DarkArmorFeet = BUILDER.comment("Define how much armor points wearing the Boots provides, Default: 3")
                        .defineInRange("darkArmorFeet", 3, 1, Integer.MAX_VALUE);
                DarkArmorLegs = BUILDER.comment("Define how much armor points wearing the Leggings provides, Default: 6")
                        .defineInRange("darkArmorLegs", 6, 1, Integer.MAX_VALUE);
                DarkArmorChest = BUILDER.comment("Define how much armor points wearing the Chestplate provides, Default: 8")
                        .defineInRange("darkArmorChest", 8, 1, Integer.MAX_VALUE);
                DarkArmorHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 3")
                        .defineInRange("darkArmorHead", 3, 1, Integer.MAX_VALUE);
                BUILDER.pop();
            DarkArmorDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 15")
                    .defineInRange("darkArmorDurability", 15, 1, Integer.MAX_VALUE);
            DarkArmorEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 20")
                    .defineInRange("darkArmorEnchantability", 20, 1, Integer.MAX_VALUE);
            DarkArmorToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 2.0")
                    .defineInRange("darkArmorToughness", 2.0, 0.0, Double.MAX_VALUE);
            DarkArmorKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.3")
                    .defineInRange("darkArmorKnockResist", 0.3, 0.0, Double.MAX_VALUE);
            DarkArmorPersist = BUILDER.comment("Whether armor pieces don't break but no longer provide armor, Default: true")
                    .define("darkArmorPersist", true);
            DarkHelmetBlindness = BUILDER.comment("Whether Dark Helmets makes the wearer immune to Blindness, Default: true")
                    .define("darkHelmetBlindness", true);
            DarkHelmetDarkness = BUILDER.comment("Whether Dark Helmets makes the wearer immune to Darkness, Default: true")
                    .define("darkHelmetDarkness", true);
            BUILDER.pop();
            BUILDER.push("Malefic Helm");
            MaleficHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 2")
                .defineInRange("maleficHead", 2, 1, Integer.MAX_VALUE);
            MaleficDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 15")
                    .defineInRange("maleficDurability", 30, 1, Integer.MAX_VALUE);
            MaleficEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 15")
                    .defineInRange("maleficEnchantability", 15, 1, Integer.MAX_VALUE);
            MaleficToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 2.0")
                    .defineInRange("maleficToughness", 2.0, 0.0, Double.MAX_VALUE);
            MaleficKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.0")
                    .defineInRange("maleficKnockResist", 0.0, 0.0, Double.MAX_VALUE);
            MaleficPersist = BUILDER.comment("Whether armor pieces don't break but no longer provide armor, Default: true")
                    .define("maleficPersist", true);
            BUILDER.pop();
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
