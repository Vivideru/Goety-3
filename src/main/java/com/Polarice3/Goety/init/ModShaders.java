package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.io.IOException;

@EventBusSubscriber(modid = Goety.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ModShaders {

    private static ShaderInstance holeShader;
    private static ShaderInstance enchantBeamShader;
    private static ShaderInstance enchantSwirlShader;
    private static ShaderInstance chargeAuraShader;
    private static ShaderInstance soulParticleShader;
    private static ShaderInstance ditherSmokeShader;

    @SubscribeEvent
    static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "hole"),
                        DefaultVertexFormat.POSITION),
                shader -> holeShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "enchant_beam"),
                        DefaultVertexFormat.POSITION_TEX_COLOR),
                shader -> enchantBeamShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "enchant_swirl"),
                        DefaultVertexFormat.NEW_ENTITY),
                shader -> enchantSwirlShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "charge_aura"),
                        DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
                shader -> chargeAuraShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "soul_particle"),
                        DefaultVertexFormat.PARTICLE),
                shader -> soulParticleShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "dither_smoke"),
                        DefaultVertexFormat.PARTICLE),
                shader -> ditherSmokeShader = shader
        );
    }

    public static ShaderInstance getHoleShader() {
        return holeShader;
    }

    public static ShaderInstance getEnchantBeamShader() {
        return enchantBeamShader;
    }

    public static ShaderInstance getEnchantSwirlShader() {
        return enchantSwirlShader;
    }

    public static ShaderInstance getChargeAuraShader() {
        return chargeAuraShader;
    }

    public static ShaderInstance getSoulParticleShader() {
        return soulParticleShader;
    }

    public static ShaderInstance getDitherSmokeShader() {
        return ditherSmokeShader;
    }

}
