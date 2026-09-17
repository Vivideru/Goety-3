package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class RingItem extends SingleStackItem {

    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment)
    {
        if (stack.getItem() == ModItems.RING_OF_WANT.get()) {
            // 1.21 passes enchantments as registry holders, so compare by resource key instead of raw instances.
            return enchantment.is(ModEnchantments.WANTING);
        } else if (stack.getItem() == ModItems.RING_OF_THE_DRAGON.get()) {
            return enchantment.is(ModEnchantments.RADIUS);
        }
        return false;
    }
}
