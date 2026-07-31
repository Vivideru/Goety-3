package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.client.gui.screen.inventory.FocusRadialMenuScreen;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.level.GameType;

public class CurrentFocusGui {
    public static final LayeredDraw.Layer OVERLAY = CurrentFocusGui::drawHUD;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar(){
        return !WandUtil.findFocus(minecraft.player).isEmpty() && (minecraft.gameMode != null && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) && !(minecraft.screen instanceof FocusRadialMenuScreen) && MainConfig.FocusGuiShow.get();
    }

    public static void drawHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(!shouldDisplayBar()) {
            return;
        }
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(1.0F, 1.0F, 1.0F);
        if (WandUtil.findFocus(minecraft.player) != null) {
            guiGraphics.renderFakeItem(WandUtil.findFocus(minecraft.player), ((screenWidth - 16) / 2) + MainConfig.FocusGuiHorizontal.get(), (screenHeight - 52) + MainConfig.FocusGuiVertical.get());
            guiGraphics.renderItemDecorations(minecraft.font, WandUtil.findFocus(minecraft.player), ((screenWidth - 16) / 2) + MainConfig.FocusGuiHorizontal.get(), (screenHeight - 52) + MainConfig.FocusGuiVertical.get());
        }
        guiGraphics.pose().popPose();
    }
}
