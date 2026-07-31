package com.Polarice3.Goety.common.items.brew;

import com.Polarice3.Goety.utils.BrewUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.Polarice3.Goety.utils.ModPotionUtil;
import net.minecraft.world.item.alchemy.Potions;

import java.util.List;

public class BrewArrowItem extends ArrowItem {
   public BrewArrowItem(Properties p_43354_) {
      super(p_43354_);
   }

   public ItemStack getDefaultInstance() {
      // 1.21 removed Potions.EMPTY; water is the vanilla no-effect potion fallback.
      return ModPotionUtil.setPotion(super.getDefaultInstance(), Potions.WATER);
   }

   public void appendHoverText(ItemStack p_43359_, TooltipContext p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
      BrewUtils.addBrewTooltip(p_43359_, p_43361_, 0.125F);
   }
}
