package com.Vivideru.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.client.render.ExtraServantRenderer;
import com.Vivideru.Goety.common.entities.extra.ExtraServantDefinition;
import com.Vivideru.Goety.common.entities.extra.ExtraServantSpawnEggItem;
import com.Vivideru.Goety.common.entities.extra.ExtraServants;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/** Client side of the extra servants: they are drawn by their mob's renderer, and their eggs wear their colours. */
@EventBusSubscriber(modid = Goety.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ExtraServantClientEvents {
    private ExtraServantClientEvents() {
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (ExtraServantDefinition definition : ExtraServants.definitions()) {
            EntityType<?> type = ExtraServants.type(definition);
            if (type != null) {
                // The mob's own renderer is looked up when renderers are built, after every mod has registered its own.
                event.registerEntityRenderer((EntityType<Entity>) type, context -> ExtraServantRenderer.create(context, definition));
            }
        }
    }

    @SubscribeEvent
    public static void eggColours(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            ExtraServantDefinition definition = ExtraServantSpawnEggItem.definition(stack);
            if (definition == null) {
                return -1;
            }
            return FastColor.ARGB32.opaque(tintIndex == 0 ? definition.eggPrimary() : definition.eggSecondary());
        }, ExtraServants.SPAWN_EGG.get());
    }
}
