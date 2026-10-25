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

}
