package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class HuntersBowItem extends BowItem {
    private static final int DEFAULT_DURABILITY = 133;

    public HuntersBowItem() {
        // Item properties are built during registration before configs load, so use the declared default here.
        super((new Properties()).rarity(Rarity.UNCOMMON).durability(DEFAULT_DURABILITY));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        return ConfiguredItemUtil.durability(ItemConfig.HuntersBowDurability, DEFAULT_DURABILITY);
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() instanceof BowItem;
    }
}
