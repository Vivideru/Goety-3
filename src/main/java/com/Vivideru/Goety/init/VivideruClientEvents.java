package com.Vivideru.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BlackBeastModel;
import com.Polarice3.Goety.client.render.model.BlackWolfModel;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Vivideru.Goety.client.render.layer.CursedBlackBeastArmorLayer;
import com.Vivideru.Goety.client.render.layer.CursedBlackWolfArmorLayer;
import com.Vivideru.Goety.client.render.layer.CursedSkeletonWolfArmorLayer;
import com.Vivideru.Goety.client.render.layer.CursedWolfArmorLayer;
import com.Vivideru.Goety.client.render.layer.VivideruModelLayers;
import com.Vivideru.Goety.client.render.BreezeServantRenderer;
import com.Vivideru.Goety.client.render.CerberusRenderer;
import com.Vivideru.Goety.client.render.HurricaneCoreSummonRenderer;
import com.Vivideru.Goety.client.render.HurricaneCycloneRenderer;
import com.Vivideru.Goety.client.render.HurricanePunchRenderer;
import com.Vivideru.Goety.client.render.HurricaneRenderer;
import com.Vivideru.Goety.client.render.WargRenderer;
import com.Vivideru.Goety.client.render.model.BreezeServantModel;
import com.Vivideru.Goety.client.render.model.HurricaneModel;
import com.Vivideru.Goety.client.render.model.HurricanePunchModel;
import com.Vivideru.Goety.client.render.model.HurricaneTornadoModel;
import com.Vivideru.Goety.client.render.model.CerberusArmorModel;
import com.Vivideru.Goety.client.render.model.CerberusModel;
import com.Vivideru.Goety.client.render.model.CursedBlackBeastArmorModel;
import com.Vivideru.Goety.client.render.model.CursedBlackWolfArmorModel;
import com.Vivideru.Goety.client.render.model.CursedSkeletonWolfArmorModel;
import com.Vivideru.Goety.client.render.model.CursedWolfArmorModel;
import com.Vivideru.Goety.client.render.model.WargArmorModel;
import com.Vivideru.Goety.client.render.model.WargModel;
import com.Vivideru.Goety.client.render.model.WargSaddleModel;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.WindChargeRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Goety.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VivideruClientEvents {
    @SubscribeEvent
    public static void wrapVaultModels(net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult event) {
        for (net.minecraft.world.level.block.state.BlockState state : net.minecraft.world.level.block.Blocks.VAULT.getStateDefinition().getPossibleStates()) {
            event.getModels().computeIfPresent(net.minecraft.client.renderer.block.BlockModelShaper.stateToModelLocation(state),
                    (location, model) -> model instanceof com.Vivideru.Goety.client.render.model.VaultVariantModel ? model : new com.Vivideru.Goety.client.render.model.VaultVariantModel(model));
        }
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(VivideruModelLayers.CURSED_WOLF_ARMOR, CursedWolfArmorModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.CURSED_SKELETON_WOLF_ARMOR, CursedSkeletonWolfArmorModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.CURSED_BLACK_WOLF_ARMOR, CursedBlackWolfArmorModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.CURSED_BLACK_BEAST_ARMOR, CursedBlackBeastArmorModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.WARG, WargModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.WARG_ARMOR, WargArmorModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.WARG_SADDLE, WargSaddleModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.CERBERUS, CerberusModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.CERBERUS_ARMOR, CerberusArmorModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.BREEZE_SERVANT, () -> BreezeServantModel.createBodyLayer(32, 32));
        event.registerLayerDefinition(VivideruModelLayers.BREEZE_SERVANT_WIND, () -> BreezeServantModel.createBodyLayer(128, 128));
        event.registerLayerDefinition(VivideruModelLayers.HURRICANE, HurricaneModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.HURRICANE_TORNADO, HurricaneTornadoModel::createBodyLayer);
        event.registerLayerDefinition(VivideruModelLayers.HURRICANE_PUNCH, HurricanePunchModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VivideruEntityTypes.WARG.get(), WargRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.SKELETAL_WARG.get(), WargRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.CERBERUS.get(), CerberusRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.BREEZE_SERVANT.get(), BreezeServantRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.SERVANT_WIND_CHARGE.get(), WindChargeRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.HURRICANE.get(), HurricaneRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.HURRICANE_SERVANT.get(), HurricaneRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.HURRICANE_PUNCH.get(), HurricanePunchRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.HURRICANE_CYCLONE.get(), HurricaneCycloneRenderer::new);
        event.registerEntityRenderer(VivideruEntityTypes.HURRICANE_CORE_SUMMON.get(), HurricaneCoreSummonRenderer::new);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        addBlackWolfArmorLayer(event.getRenderer(ModEntityType.BLACK_WOLF.get()), event);
        addBlackWolfArmorLayer(event.getRenderer(ModEntityType.WINTER_WOLF.get()), event);
        addBlackWolfArmorLayer(event.getRenderer(ModEntityType.STORMHOUND.get()), event);
        addBlackWolfArmorLayer(event.getRenderer(ModEntityType.HELLHOUND.get()), event);
        addBlackBeastArmorLayer(event.getRenderer(ModEntityType.BLACK_BEAST.get()), event);
        addVanillaWolfArmorLayer(event);
        addSkeletonWolfArmorLayer(event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addBlackBeastArmorLayer(EntityRenderer<?> renderer, EntityRenderersEvent.AddLayers event) {
        if (renderer instanceof LivingEntityRenderer livingRenderer && livingRenderer.getModel() instanceof BlackBeastModel) {
            livingRenderer.addLayer(new CursedBlackBeastArmorLayer<>((LivingEntityRenderer<BlackBeast, BlackBeastModel<BlackBeast>>) livingRenderer, event.getEntityModels()));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addBlackWolfArmorLayer(EntityRenderer<?> renderer, EntityRenderersEvent.AddLayers event) {
        if (renderer instanceof LivingEntityRenderer livingRenderer && livingRenderer.getModel() instanceof BlackWolfModel) {
            livingRenderer.addLayer(new CursedBlackWolfArmorLayer<>((LivingEntityRenderer<BlackWolf, BlackWolfModel<BlackWolf>>) livingRenderer, event.getEntityModels()));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addVanillaWolfArmorLayer(EntityRenderersEvent.AddLayers event) {
        EntityRenderer<?> renderer = event.getRenderer(EntityType.WOLF);
        if (renderer instanceof LivingEntityRenderer livingRenderer && livingRenderer.getModel() instanceof WolfModel) {
            livingRenderer.addLayer(new CursedWolfArmorLayer((LivingEntityRenderer<Wolf, WolfModel<Wolf>>) livingRenderer, event.getEntityModels()));
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
