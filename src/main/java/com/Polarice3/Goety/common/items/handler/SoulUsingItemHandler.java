package com.Polarice3.Goety.common.items.handler;

import com.Polarice3.Goety.api.items.magic.IFocus;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;

public class SoulUsingItemHandler extends ItemStackHandler {
    private final ItemStack itemStack;

    public SoulUsingItemHandler(ItemStack itemStack) {
        this.itemStack = itemStack;
        itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.stacks);
    }

    public ItemStack extractItem() {
        return extractItem(0, 1, false);
    }

    public ItemStack insertItem(ItemStack insert) {
        return insertItem(0, insert, false);
    }

    public ItemStack getSlot() {
        return getStackInSlot(0);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem() instanceof IFocus;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    public NonNullList<ItemStack> getContents() {
        return stacks;
    }

    @Override
    protected void onContentsChanged(int slot) {
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.stacks));
    }

    public static SoulUsingItemHandler get(ItemStack stack) {
        IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler == null) {
            throw new IllegalArgumentException("ItemStack is missing item capability");
        }
        return (SoulUsingItemHandler) handler;
    }
}
