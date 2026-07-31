package com.Polarice3.Goety.common.items.handler;

import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.items.brew.ThrowableBrewItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class EternalCauldronItemHandler extends ItemStackHandler {
    private final ItemStack itemStack;

    public EternalCauldronItemHandler(ItemStack itemStack) {
        super(1);
        this.itemStack = itemStack;
        itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.stacks);
    }

    public ItemStack extractItem() {
        return this.extractItem(0, 1, false);
    }

    public ItemStack insertItem(ItemStack insert) {
        return this.insertItem(0, insert, false);
    }

    public ItemStack getSlot() {
        return this.getStackInSlot(0);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        Item item = stack.getItem();
        return (item instanceof PotionItem && !(item instanceof ThrowablePotionItem)) || (item instanceof BrewItem && !(item instanceof ThrowableBrewItem));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    protected void onContentsChanged(int slot) {
        // 1.21 stores item inventories in components instead of root NBT/capability share tags.
        this.itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.stacks));
    }

    public NonNullList<ItemStack> getContents() {
        return this.stacks;
    }

    public static EternalCauldronItemHandler get(ItemStack stack) {
        IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler == null) {
            throw new IllegalArgumentException("ItemStack is missing item capability");
        }
        return (EternalCauldronItemHandler) handler;
    }
}
