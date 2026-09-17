package com.Polarice3.Goety.client.gui.screen.inventory;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.inventory.container.SoulItemContainer;
import com.Vivideru.Goety.common.items.magic.FocusBagBinding;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SoulItemScreen extends AbstractContainerScreen<SoulItemContainer> {
    private static final ResourceLocation GUI_TEXTURES = ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "textures/gui/container/wand.png");
    private Button bindButton;
    private Button unbindButton;

    public SoulItemScreen(SoulItemContainer p_i51097_1_, Inventory p_i51097_2_, Component p_i51097_3_) {
        super(p_i51097_1_, p_i51097_2_, p_i51097_3_);
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.bindButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.goety.bind_focus_bag"), button -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, SoulItemContainer.BIND_BUTTON);
            }
        }).bounds(this.leftPos + 120, this.topPos + 67, 30, 14).build());
        this.unbindButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.goety.unbind_focus_bag"), button -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, SoulItemContainer.UNBIND_BUTTON);
            }
        }).bounds(this.leftPos + 120, this.topPos + 52, 30, 14).build());
        this.updateButtonStates();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.updateButtonStates();
    }

    private void updateButtonStates() {
        if (this.bindButton != null) {
            this.bindButton.active = !this.menu.getWandStack().isEmpty()
                    && FocusBagBinding.isFocusContainer(this.menu.getBindingStack());
        }
        if (this.unbindButton != null) {
            this.unbindButton.active = this.menu.isWandBound();
        }
    }

    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft != null) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            matrixStack.blit(GUI_TEXTURES, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
    }
}
