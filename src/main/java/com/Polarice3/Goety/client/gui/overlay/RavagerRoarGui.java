package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.IRavager;
import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class RavagerRoarGui {
    public static final LayeredDraw.Layer OVERLAY = RavagerRoarGui::drawHUD;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar(){
        return minecraft.player != null && minecraft.player.getVehicle() instanceof IRavager ravager && ravager.getRoarCool() > 0;
    }

    public static void drawHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(!shouldDisplayBar()) {
            return;
        }
        if (minecraft.player == null){
            return;
        }
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int i = (screenWidth/2) + (MainConfig.SoulGuiHorizontal.get());
        int RoarCool = 0;
        int RoarCoolTotal = 1;
        if (minecraft.player.getVehicle() instanceof IRavager ravager){
            RoarCool = ravager.getRoarCool();
            RoarCoolTotal = ravager.getRoarCoolMax();
        }
        int roarLength = 80;
        roarLength *= (RoarCool / (double)RoarCoolTotal);
        int height = screenHeight + (MainConfig.SoulGuiVertical.get() - 20);
        guiGraphics.blit(Goety.location("textures/gui/ravager_roar_bar.png"), i, height - 9, 0, 0, 96,16, 96, 32);
        guiGraphics.blit(Goety.location("textures/gui/ravager_roar_bar.png"), i + 16, height - 9, 16, 16, roarLength,16, 96, 32);
    }
}
