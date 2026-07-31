package com.Vivideru.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BlackWolfModel;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Vivideru.Goety.client.render.layer.CursedBlackWolfArmorLayer;
import com.Vivideru.Goety.client.render.layer.CursedSkeletonWolfArmorLayer;
import com.Vivideru.Goety.client.render.layer.VivideruModelLayers;
import com.Vivideru.Goety.client.render.model.CursedBlackWolfArmorModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Goety.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VivideruClientEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(VivideruModelLayers.CURSED_BLACK_WOLF_ARMOR, CursedBlackWolfArmorModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        addBlackWolfArmorLayer(event.getRenderer(ModEntityType.BLACK_WOLF.get()), event);
        addBlackWolfArmorLayer(event.getRenderer(ModEntityType.WINTER_WOLF.get()), event);
        addBlackWolfArmorLayer(event.getRenderer(ModEntityType.STORMHOUND.get()), event);
        addBlackWolfArmorLayer(event.getRenderer(ModEntityType.HELLHOUND.get()), event);
        addSkeletonWolfArmorLayer(event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addBlackWolfArmorLayer(EntityRenderer<?> renderer, EntityRenderersEvent.AddLayers event) {
        if (renderer instanceof LivingEntityRenderer livingRenderer && livingRenderer.getModel() instanceof BlackWolfModel) {
            livingRenderer.addLayer(new CursedBlackWolfArmorLayer<>((LivingEntityRenderer<BlackWolf, BlackWolfModel<BlackWolf>>) livingRenderer, event.getEntityModels()));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addSkeletonWolfArmorLayer(EntityRenderersEvent.AddLayers event) {
        EntityRenderer<?> renderer = event.getRenderer(ModEntityType.SKELETON_WOLF.get());
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new CursedSkeletonWolfArmorLayer(livingRenderer, event.getEntityModels()));
        }
    }
}
