package com.Polarice3.Goety.common.items.handler;

import com.Polarice3.Goety.api.items.magic.IFocus;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;

public class FocusBagItemHandler extends ItemStackHandler {
    private final ItemStack itemStack;
    private final int size;

    public FocusBagItemHandler(ItemStack itemStack, int size) {
        super(size);
        this.size = size;
        this.itemStack = itemStack;
        itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.stacks);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem() instanceof IFocus;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.size;
    }

    public NonNullList<ItemStack> getContents() {
        return stacks;
    }

    @Override
    protected void onContentsChanged(int slot) {
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.stacks));
    }

    public static FocusBagItemHandler get(ItemStack stack) {
        IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler == null) {
            throw new IllegalArgumentException("ItemStack is missing item capability");
        }
        return (FocusBagItemHandler) handler;
    }
}
