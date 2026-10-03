package com.Vivideru.Goety.common.entities;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.entities.ally.Cerberus;
import com.Vivideru.Goety.common.entities.ally.SkeletalWarg;
import com.Vivideru.Goety.common.entities.ally.BreezeServant;
import com.Vivideru.Goety.common.entities.ally.HurricaneServant;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.Vivideru.Goety.common.entities.hostile.Hurricane;
import com.Vivideru.Goety.common.entities.projectiles.HurricaneCyclone;
import com.Vivideru.Goety.common.entities.projectiles.HurricanePunch;
import com.Vivideru.Goety.common.entities.projectiles.ServantWindCharge;
import com.Vivideru.Goety.common.entities.util.HurricaneCoreSummon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VivideruEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Goety.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<Warg>> WARG = ENTITY_TYPES.register("warg",
            () -> EntityType.Builder.of(Warg::new, MobCategory.MONSTER)
                    .sized(1.25F, 1.65F)
                    .passengerAttachments(1.42F)
                    .clientTrackingRange(10)
                    .build(Goety.location("warg").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletalWarg>> SKELETAL_WARG = ENTITY_TYPES.register("skeletal_warg",
            () -> EntityType.Builder.of(SkeletalWarg::new, MobCategory.MONSTER)
                    .sized(1.25F, 1.65F)
                    .passengerAttachments(1.42F)
                    .clientTrackingRange(10)
                    .build(Goety.location("skeletal_warg").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<Cerberus>> CERBERUS = ENTITY_TYPES.register("cerberus",
            () -> EntityType.Builder.of(Cerberus::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.6F)
                    .passengerAttachments(2.3F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build(Goety.location("cerberus").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<BreezeServant>> BREEZE_SERVANT = ENTITY_TYPES.register("breeze_servant",
            () -> EntityType.Builder.of(BreezeServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.77F)
                    .eyeHeight(1.3452F)
                    .clientTrackingRange(10)
                    .build(Goety.location("breeze_servant").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ServantWindCharge>> SERVANT_WIND_CHARGE = ENTITY_TYPES.register("servant_wind_charge",
            () -> EntityType.Builder.<ServantWindCharge>of(ServantWindCharge::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(Goety.location("servant_wind_charge").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<Hurricane>> HURRICANE = ENTITY_TYPES.register("hurricane",
            () -> EntityType.Builder.of(Hurricane::new, MobCategory.MONSTER)
                    .sized(1.6F, 3.4F)
                    .eyeHeight(3.0F)
                    .clientTrackingRange(10)
                    .build(Goety.location("hurricane").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HurricaneServant>> HURRICANE_SERVANT = ENTITY_TYPES.register("hurricane_servant",
            () -> EntityType.Builder.of(HurricaneServant::new, MobCategory.MONSTER)
                    .sized(1.6F, 3.4F)
                    .eyeHeight(3.0F)
                    .clientTrackingRange(10)
                    .build(Goety.location("hurricane_servant").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HurricanePunch>> HURRICANE_PUNCH = ENTITY_TYPES.register("hurricane_punch",
            () -> EntityType.Builder.<HurricanePunch>of(HurricanePunch::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .eyeHeight(0.5F)
                    .clientTrackingRange(6)
                    .updateInterval(2)
                    .build(Goety.location("hurricane_punch").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HurricaneCoreSummon>> HURRICANE_CORE_SUMMON = ENTITY_TYPES.register("hurricane_core_summon",
            () -> EntityType.Builder.<HurricaneCoreSummon>of(HurricaneCoreSummon::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build(Goety.location("hurricane_core_summon").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HurricaneCyclone>> HURRICANE_CYCLONE = ENTITY_TYPES.register("hurricane_cyclone",
            () -> EntityType.Builder.<HurricaneCyclone>of(HurricaneCyclone::new, MobCategory.MISC)
                    .sized(1.0F, 1.5F)
                    .clientTrackingRange(4)
                    .updateInterval(1)
                    .build(Goety.location("hurricane_cyclone").toString()));

    @SuppressWarnings("removal")
    public static void init() {
        ENTITY_TYPES.register(Goety.getModEventBus());
    }
}
