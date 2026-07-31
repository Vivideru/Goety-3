package com.Polarice3.Goety.common.advancements;

import com.Polarice3.Goety.Goety;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, Goety.MOD_ID);
    public static final DeferredHolder<CriterionTrigger<?>, KilledTrigger> SERVANT_KILLED_ENTITY = TRIGGERS.register("servant_killed_entity", KilledTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> SERVANT_RAID_VICTORY = TRIGGERS.register("servant_raid_victory", PlayerTrigger::new);

    public static void init() {
        // 1.21 freezes vanilla registries before mod construction finishes, so criteria triggers must use the mod event bus.
        TRIGGERS.register(Goety.getModEventBus());
    }
}
