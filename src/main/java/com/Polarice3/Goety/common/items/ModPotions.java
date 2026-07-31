package com.Polarice3.Goety.common.items;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModPotions {
    public static DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, Goety.MOD_ID);

    @SuppressWarnings("removal")
    public static void init(){
        ModPotions.POTIONS.register(com.Polarice3.Goety.Goety.getModEventBus());
    }

    public static final DeferredHolder<Potion, Potion> CLIMBING = POTIONS.register("climbing", () -> new Potion(new MobEffectInstance(GoetyEffects.CLIMBING, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_CLIMBING = POTIONS.register("long_climbing", () -> new Potion("climbing", new MobEffectInstance(GoetyEffects.CLIMBING, 9600)));

    public static final DeferredHolder<Potion, Potion> SPASMS = POTIONS.register("spasms", () -> new Potion(new MobEffectInstance(GoetyEffects.SPASMS, 900)));
    public static final DeferredHolder<Potion, Potion> LONG_SPASMS = POTIONS.register("long_spasms", () -> new Potion("spasms", new MobEffectInstance(GoetyEffects.SPASMS, 1800)));
}
