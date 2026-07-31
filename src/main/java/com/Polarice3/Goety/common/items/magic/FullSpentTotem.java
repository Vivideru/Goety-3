package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.config.ItemConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import java.util.function.IntSupplier;

public class FullSpentTotem extends TotemOfSouls{

    public FullSpentTotem(int maxSouls) {
        super(maxSouls);
    }

    public FullSpentTotem(IntSupplier maxSouls) {
        super(maxSouls);
    }

    @NotNull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        if (container.has(DataComponents.CUSTOM_DATA)) {
            if (ITotem.tag(container).getInt(SOULS_AMOUNT) > ItemConfig.CraftingSouls.get()) {
                ITotem.decreaseSouls(container, ItemConfig.CraftingSouls.get());
                return container;
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }
}
