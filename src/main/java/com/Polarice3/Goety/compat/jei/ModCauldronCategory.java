package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.crafting.CauldronRecipe;
import com.Polarice3.Goety.common.crafting.CauldronSusStewRecipe;
import com.Polarice3.Goety.common.items.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.FlowerBlock;

public class ModCauldronCategory implements IRecipeCategory<CauldronRecipe> {
    private final IDrawable background;
    private final IDrawable cauldron;
    private final IDrawable square1;
    private final IDrawable square2;
    private final IDrawable arrow;
    private final IDrawable arrow2;
    private final Component localizedName;
    private final IDrawable icon;

    public ModCauldronCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 80);
        this.localizedName = Component.translatable(Goety.MOD_ID + ".jei.cauldron");
        this.cauldron = guiHelper.createDrawable(Goety.location("textures/gui/jei/cauldron.png"), 0, 0, 31, 31);
        ResourceLocation squares = Goety.location("textures/gui/jei/squares.png");
        this.square1 = guiHelper.createDrawable(squares, 0, 0, 31, 31);
        this.square2 = guiHelper.createDrawable(squares, 32, 0, 31, 31);
        this.arrow = guiHelper.createDrawable(Goety.location("textures/gui/jei/arrow.png"), 0, 0, 64, 46);
        this.arrow2 = guiHelper.createDrawable(Goety.location("textures/gui/jei/down_arrow.png"), 0, 0, 46, 64);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.NIGHTSHADE_BLOSSOM.get()));
    }

    @Override
    public RecipeType<CauldronRecipe> getRecipeType() {
        return JeiRecipeTypes.CAULDRON;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, CauldronRecipe recipe, IFocusGroup ingredients) {
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            recipeLayout.addSlot(RecipeIngredientRole.INPUT, i * 18 + 10, 6)
                    .addIngredients(recipe.getIngredients().get(i));
        }

        IRecipeSlotBuilder output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 109, 35);
        if (recipe instanceof CauldronSusStewRecipe) {
            for (FlowerBlock flower : CauldronSusStewRecipe.getFlowers()) {
                ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
                // Suspicious stew effects are data components rather than item NBT in 1.21.
                stew.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, flower.getSuspiciousEffects());
                output.addItemStack(stew);
            }
        } else {
            output.addItemStack(recipe.getResultItem(null));
        }

        recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 145, 35).addIngredients(recipe.getTakeWith());
        recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 10, 35)
                .addItemStack(new ItemStack(ModItems.NIGHTSHADE_BLOSSOM.get()));
    }

    @Override
    public void draw(CauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        this.cauldron.draw(graphics, 40, 27);
        this.square1.draw(graphics, 137, 27);
        this.square1.draw(graphics, 2, 27);
        this.square2.draw(graphics, 101, 27);
        this.arrow.draw(graphics, 79, 35);
        this.arrow2.draw(graphics, 48, 22);
        RenderSystem.disableBlend();
        this.drawStringCentered(graphics, Minecraft.getInstance().font,
                I18n.get("jei.goety.single.soulcost", recipe.getSoulCost()), 46, 70);
    }

    protected void drawStringCentered(GuiGraphics graphics, Font font, String text, int x, int y) {
        graphics.drawString(font, text, (int) (x - font.width(text) / 2.0F), y, 0, false);
    }
}
