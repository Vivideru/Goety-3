package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ScrollCloningRecipe extends CustomRecipe {
   public ScrollCloningRecipe(CraftingBookCategory p_251090_) {
      super(p_251090_);
   }

   public boolean matches(CraftingInput p_272882_, Level p_272812_) {
      if (!this.canCraftInDimensions(p_272882_.width(), p_272882_.height())) {
         return false;
      } else {
         for(int i = 0; i < p_272882_.size(); ++i) {
            ItemStack itemstack = p_272882_.getItem(i);
             if (i == 4) {
                 if (!itemstack.is(ModTags.Items.REPLICABLE_SCROLLS)) {
                     return false;
                 }
             } else {
                 if (!itemstack.is(Items.PAPER)) {
                     return false;
                 }
             }
         }

         return true;
      }
   }

   public ItemStack assemble(CraftingInput p_43812_, HolderLookup.Provider p_267039_) {
       ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_43812_.size(); ++j) {
         ItemStack itemstack1 = p_43812_.getItem(j);
         if (j == 4) {
            if (!itemstack1.is(ModTags.Items.REPLICABLE_SCROLLS)) {
               return ItemStack.EMPTY;
            } else {
               itemstack = itemstack1;
            }
         } else {
            if (!itemstack1.is(Items.PAPER)) {
               return ItemStack.EMPTY;
            }
         }
      }

      if (!itemstack.isEmpty()) {
         return itemstack.copyWithCount(1);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput p_43820_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_43820_.size(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = p_43820_.getItem(i);
         if (itemstack.hasCraftingRemainingItem()) {
            nonnulllist.set(i, itemstack.getCraftingRemainingItem());
         } else if (itemstack.is(ModTags.Items.REPLICABLE_SCROLLS)) {
            nonnulllist.set(i, itemstack.copyWithCount(1));
            break;
         }
      }

      return nonnulllist;
   }

   public RecipeSerializer<?> getSerializer() {
      return ModRecipeSerializer.SCROLL_CLONING.get();
   }

   public boolean canCraftInDimensions(int p_43804_, int p_43805_) {
      return p_43804_ >= 3 && p_43805_ >= 3;
   }
}
