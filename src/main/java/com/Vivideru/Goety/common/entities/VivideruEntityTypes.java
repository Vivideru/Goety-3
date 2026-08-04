package com.Vivideru.Goety.common.entities;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.entities.ally.Warg;
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

    @SuppressWarnings("removal")
    public static void init() {
        ENTITY_TYPES.register(Goety.getModEventBus());
    }
}
