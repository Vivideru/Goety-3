package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.ritual.EnchantItemRitual;
import com.Polarice3.Goety.common.ritual.LocateRitual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.List;

public class RitualRecipeProcessor implements IComponentProcessor {

    protected RitualRecipe recipe;
    protected ItemStack pedestal;

    @Override
    public void setup(Level level, IVariableProvider iVariableProvider) {
        String recipeId = iVariableProvider.get("recipe", level.registryAccess()).asString();
        // Minecraft 1.21 wraps recipe lookups in RecipeHolder; Patchouli still needs the recipe value.
        this.recipe = Minecraft.getInstance().level.getRecipeManager()
                .byKey(ResourceLocation.parse(recipeId)).map(RecipeHolder::value).filter(RitualRecipe.class::isInstance).map(RitualRecipe.class::cast).orElse(null);
        this.pedestal = new ItemStack(ModItems.PEDESTAL_DUMMY.get());
    }

    @Override
    public IVariable process(Level level, String key) {
        if (this.recipe == null) {
            return IVariable.empty();
        }

        if (key.startsWith("activation_item")) {
            if (this.recipe.getRitual() instanceof EnchantItemRitual){
                return IVariable.from(Ingredient.of(Items.BOOK), level.registryAccess());
            } else {
                return IVariable.from(this.recipe.getActivationItem().getItems(), level.registryAccess());
            }
        }

        if (key.startsWith("craftType")) {
            if (this.recipe.getCraftType() != null) {
                return IVariable.wrap(I18n.get("jei.goety.craftType") + I18n.get( "jei.goety.craftType." + I18n.get(recipe.getCraftType())), level.registryAccess());
            }
        }

        if (key.startsWith("ingredient")) {
            int index = Integer.parseInt(key.substring("ingredient".length())) - 1;
            if (index >= this.recipe.getIngredients().size())
                return IVariable.empty();


            Ingredient ingredient = this.recipe.getIngredients().get(index);
            return IVariable.from(ingredient.getItems(), level.registryAccess());
        }

        if (key.startsWith("pedestal")) {
            int index = Integer.parseInt(key.substring("pedestal".length())) - 1;
            if (index >= this.recipe.getIngredients().size())
                return IVariable.empty();

            return IVariable.from(this.pedestal, level.registryAccess());
        }

        if (key.startsWith("enchantment")) {
            Holder<Enchantment> enchantment = this.recipe.getEnchantmentHolder();
            if (enchantment != null) {
                return IVariable.wrap(I18n.get("jei.goety.enchantment", enchantment.value().description().getString()), level.registryAccess());
            }
        }

        if (key.equals("output")) {
            Holder<Enchantment> enchantment = this.recipe.getEnchantmentHolder();
            if (this.recipe.getRitual() instanceof EnchantItemRitual && enchantment != null){
                List<ItemStack> results = new ArrayList<>();
                for (int i = 1; i <= enchantment.value().getMaxLevel(); ++i){
                    EnchantmentInstance enchantmentInstance = new EnchantmentInstance(enchantment, i);
                    results.add(EnchantedBookItem.createForEnchantment(enchantmentInstance));
                }
                List<IVariable> variables = new ArrayList<>();
                for (ItemStack itemStack : results){
                    variables.add(IVariable.from(itemStack, level.registryAccess()));
                }
                return IVariable.wrapList(variables, level.registryAccess());
            } else if (this.recipe.getRitual() instanceof LocateRitual && this.recipe.getStructureTag() != null){
                ItemStack result = new ItemStack(Items.FILLED_MAP);
                String string = "filled_map.goety.magic";
                if (this.recipe.getStructureName() != null) {
                    string = this.recipe.getStructureName();
                }
                result.set(DataComponents.CUSTOM_NAME, Component.translatable(string));
                return IVariable.from(result, level.registryAccess());
            } else if (this.recipe.getResultItem(level.registryAccess()).getItem() != ModItems.JEI_DUMMY_NONE.get()) {
                return IVariable.from(this.recipe.getResultItem(level.registryAccess()), level.registryAccess());
            } else {
                return IVariable.from(new ItemStack(ModItems.JEI_DUMMY_NONE.get()), level.registryAccess());
            }
        }

        if (key.equals("entity_to_summon")) {
            if (this.recipe.getEntityToSummon() != null) {
                return IVariable.wrap(I18n.get("jei.goety.summon", I18n.get(this.recipe.getEntityToSummon().getDescriptionId())), level.registryAccess());
            }
        }

        if (key.equals("entity_to_sacrifice")) {
            if (this.recipe.requiresSacrifice()) {
                return IVariable.wrap(I18n.get("jei.goety.sacrifice", I18n.get(this.recipe.getEntityToSacrificeDisplayName())), level.registryAccess());
            }
        }

        if (key.equals("entity_to_convert")) {
            if (this.recipe.getEntityToConvert() != null) {
                return IVariable.wrap(I18n.get("jei.goety.convert", I18n.get(this.recipe.getEntityToConvertDisplayName())), level.registryAccess());
            }
        }

        if (key.equals("entity_to_convert_into")) {
            if (this.recipe.getEntityToConvertInto() != null) {
                return IVariable.wrap(I18n.get("jei.goety.convertInto", I18n.get(this.recipe.getEntityToConvertInto().getDescriptionId())), level.registryAccess());
            }
        }

        if (key.equals("xp_levels")) {
            if (this.recipe.getEnchantment() != null) {
                return IVariable.wrap(I18n.get("jei.goety.xp", this.recipe.getXPLevelCost()), level.registryAccess());
            }
        }

        if (key.startsWith("soulCost")) {
            return IVariable.wrap(I18n.get("jei.goety.soulCost", this.recipe.getSoulCost()), level.registryAccess());
        }

        if (key.startsWith("duration")) {
            return IVariable.wrap(I18n.get("jei.goety.duration", this.recipe.getDuration()), level.registryAccess());
        }

        return IVariable.empty();
    }
}
