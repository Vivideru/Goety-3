package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class HuntersBowItem extends BowItem {
    private static final int DEFAULT_DURABILITY = 133;

    public HuntersBowItem() {
        super((new Properties()).rarity(Rarity.UNCOMMON).durability(DEFAULT_DURABILITY));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ConfiguredItemUtil.durability(ItemConfig.HuntersBowDurability, DEFAULT_DURABILITY);
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() instanceof BowItem;
    }
}
