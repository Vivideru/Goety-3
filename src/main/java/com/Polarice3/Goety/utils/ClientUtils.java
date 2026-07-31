package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.mixin.ClientModLoaderAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.neoforged.fml.ModLoadingException;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClientUtils {
    public static boolean noLoadingExceptions() {
        ModLoadingException error = ClientModLoaderAccessor.getError();
        return error == null || error.getIssues().isEmpty();
    }

    public static PlayerInfo getPlayerInfo(UUID playerId) {
        if (playerId != null) {
            ClientPacketListener listener = Minecraft.getInstance().getConnection();
            if (listener != null) {
                return listener.getPlayerInfo(playerId);
            }
        }
        return null;
    }

    public static ResourceLocation getSkinTextureLocation(UUID uuid, @Nullable PlayerInfo playerInfo) {
        PlayerSkin skin = playerInfo == null ? DefaultPlayerSkin.get(uuid) : playerInfo.getSkin();
        return skin.texture();
    }

    public static String getModelName(UUID uuid, @Nullable PlayerInfo playerInfo) {
        PlayerSkin skin = playerInfo == null ? DefaultPlayerSkin.get(uuid) : playerInfo.getSkin();
        return skin.model().id();
    }

    @Nullable
    public static ResourceLocation getCloakTextureLocation(@Nullable PlayerInfo playerInfo) {
        return playerInfo == null ? null : playerInfo.getSkin().capeTexture();
    }

    public static boolean isModelPartShown(byte customization, PlayerModelPart part) {
        return (customization & part.getMask()) == part.getMask();
    }
}
