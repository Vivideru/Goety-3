package com.Polarice3.Goety.client.inventory.container;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Vivideru.Goety.common.items.magic.FocusBagBinding;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SoulItemContainer extends AbstractContainerMenu {
    public static final int BIND_BUTTON = 0;
    public static final int UNBIND_BUTTON = 1;
    private final ItemStack stack;
    private final InteractionHand hand;
    private final SimpleContainer bindingSlot = new SimpleContainer(1);
    private final DataSlot boundState = DataSlot.standalone();

    public static SoulItemContainer createContainerClientSide(int id, Inventory inventory, FriendlyByteBuf buffer) {
        InteractionHand hand = buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack wand = inventory.player.getItemInHand(hand);
        return new SoulItemContainer(id, inventory, new SoulUsingItemHandler(ItemStack.EMPTY), wand, hand);
    }

    public SoulItemContainer(int id, Inventory playerInventory, SoulUsingItemHandler handler, ItemStack stack, InteractionHand hand) {
        super(ModContainerType.WAND.get(), id);
        this.stack = stack;
        this.hand = hand;
        this.boundState.set(FocusBagBinding.isBound(stack) ? 1 : 0);
        this.addDataSlot(this.boundState);
        this.addSlot(new SlotItemHandler(handler, 0, 80, 35));
        this.addSlot(new Slot(this.bindingSlot, 0, 152, 66) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FocusBagBinding.isFocusContainer(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(this.hand) == this.stack && !this.stack.isEmpty();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!this.stillValid(player)) {
            return false;
        }
        if (id == BIND_BUTTON) {
            ItemStack bag = this.bindingSlot.getItem(0);
            if (!FocusBagBinding.isFocusContainer(bag)) {
                return false;
            }
            FocusBagBinding.bind(this.stack, bag);
            this.boundState.set(1);
            this.bindingSlot.setChanged();
            player.getInventory().setChanged();
            this.broadcastChanges();
            return true;
        }
        if (id == UNBIND_BUTTON && FocusBagBinding.isBound(this.stack)) {
            FocusBagBinding.unbind(this.stack);
            this.boundState.set(0);
            player.getInventory().setChanged();
            this.broadcastChanges();
            return true;
        }
        return false;
    }

    public ItemStack getWandStack() {
        return this.stack;
    }

    public ItemStack getBindingStack() {
        return this.bindingSlot.getItem(0);
    }

    public boolean isWandBound() {
        return this.boundState.get() != 0;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            this.clearContainer(player, this.bindingSlot);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (FocusBagBinding.isFocusContainer(itemstack1) && this.moveItemStackTo(itemstack1, 1, 2, false)) {
                // The bag binding slot has priority over ordinary inventory movement.
            } else if (itemstack1.getItem() instanceof IFocus && this.moveItemStackTo(itemstack1, 0, 1, false)) {
                // Focuses retain the existing shortcut into the wand slot.
            } else if (index >= 2 && index < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 38) {
                if (!this.moveItemStackTo(itemstack1, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
