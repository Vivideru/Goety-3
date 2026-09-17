package com.Vivideru.Goety.common.items.magic;

import com.Polarice3.Goety.common.items.magic.FocusBag;
import com.Polarice3.Goety.compat.curios.CuriosLoaded;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;
import java.util.UUID;

public final class FocusBagBinding {
    public static final String BOUND_FOCUS_BAG = "BoundFocusBag";
    public static final String FOCUS_BAG_ID = "FocusBagID";
    private static final int MAX_CONTAINER_DEPTH = 8;

    private FocusBagBinding() {
    }

    public static boolean isFocusContainer(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof FocusBag;
    }

    public static boolean isBound(ItemStack wand) {
        return getUuid(wand, BOUND_FOCUS_BAG).isPresent();
    }

    public static Optional<UUID> getBoundId(ItemStack wand) {
        return getUuid(wand, BOUND_FOCUS_BAG);
    }

    public static Optional<UUID> getBagId(ItemStack bag) {
        return getUuid(bag, FOCUS_BAG_ID);
    }

    public static void bind(ItemStack wand, ItemStack bag) {
        if (wand.isEmpty() || !isFocusContainer(bag)) {
            return;
        }

        UUID bagId = getBagId(bag).orElseGet(UUID::randomUUID);
        CustomData.update(DataComponents.CUSTOM_DATA, bag, tag -> tag.putUUID(FOCUS_BAG_ID, bagId));
        CustomData.update(DataComponents.CUSTOM_DATA, wand, tag -> tag.putUUID(BOUND_FOCUS_BAG, bagId));
    }

    public static void unbind(ItemStack wand) {
        if (!wand.isEmpty()) {
            CustomData.update(DataComponents.CUSTOM_DATA, wand, tag -> tag.remove(BOUND_FOCUS_BAG));
        }
    }

    public static BagReference findBoundBag(Player player, ItemStack wand) {
        Optional<UUID> boundId = getBoundId(wand);
        if (boundId.isEmpty()) {
            return BagReference.empty();
        }

        UUID id = boundId.get();
        Inventory inventory = player.getInventory();

        for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
            ItemStack stack = inventory.getItem(slot);
            if (matches(stack, id)) {
                int inventorySlot = slot;
                return new BagReference(stack, () -> {
                    inventory.setItem(inventorySlot, stack);
                    inventory.setChanged();
                });
            }
        }

        BagReference directCurio = findDirectCurio(player, id);
        if (directCurio.isPresent()) {
            return directCurio;
        }

        BagReference nestedCurio = findNestedCurio(player, id);
        if (nestedCurio.isPresent()) {
            return nestedCurio;
        }

        for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
            ItemStack stack = inventory.getItem(slot);
            int inventorySlot = slot;
            BagReference nested = findNested(stack, () -> {
                inventory.setItem(inventorySlot, stack);
                inventory.setChanged();
            }, id, 0);
            if (nested.isPresent()) {
                return nested;
            }
        }

        return BagReference.empty();
    }

    private static BagReference findDirectCurio(Player player, UUID id) {
        if (!CuriosLoaded.CURIOS.isLoaded()) {
            return BagReference.empty();
        }

        BagReference[] result = {BagReference.empty()};
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> curios.getCurios().values().forEach(slotHandler -> {
            if (result[0].isPresent()) {
                return;
            }
            IDynamicStackHandler stacks = slotHandler.getStacks();
            for (int slot = 0; slot < stacks.getSlots(); ++slot) {
                ItemStack stack = stacks.getStackInSlot(slot);
                if (matches(stack, id)) {
                    int curioSlot = slot;
                    result[0] = new BagReference(stack, () -> stacks.setStackInSlot(curioSlot, stack));
                    break;
                }
            }
        }));
        return result[0];
    }

    private static BagReference findNestedCurio(Player player, UUID id) {
        if (!CuriosLoaded.CURIOS.isLoaded()) {
            return BagReference.empty();
        }

        BagReference[] result = {BagReference.empty()};
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> curios.getCurios().values().forEach(slotHandler -> {
            if (result[0].isPresent()) {
                return;
            }
            IDynamicStackHandler stacks = slotHandler.getStacks();
            for (int slot = 0; slot < stacks.getSlots(); ++slot) {
                ItemStack stack = stacks.getStackInSlot(slot);
                int curioSlot = slot;
                BagReference nested = findNested(stack, () -> stacks.setStackInSlot(curioSlot, stack), id, 0);
                if (nested.isPresent()) {
                    result[0] = nested;
                    break;
                }
            }
        }));
        return result[0];
    }

    private static BagReference findNested(ItemStack container, Runnable saveContainer, UUID id, int depth) {
        if (container.isEmpty() || isFocusContainer(container) || depth >= MAX_CONTAINER_DEPTH) {
            return BagReference.empty();
        }

        IItemHandler handler = container.getCapability(Capabilities.ItemHandler.ITEM);
        if (!(handler instanceof IItemHandlerModifiable modifiable)) {
            return BagReference.empty();
        }

        for (int slot = 0; slot < modifiable.getSlots(); ++slot) {
            ItemStack child = modifiable.getStackInSlot(slot);
            int childSlot = slot;
            Runnable saveChild = () -> {
                // Reinsert each changed child so component-backed containers persist the complete nested path.
                modifiable.setStackInSlot(childSlot, child);
                saveContainer.run();
            };
            if (matches(child, id)) {
                return new BagReference(child, saveChild);
            }

            BagReference nested = findNested(child, saveChild, id, depth + 1);
            if (nested.isPresent()) {
                return nested;
            }
        }

        return BagReference.empty();
    }

    private static boolean matches(ItemStack stack, UUID id) {
        return isFocusContainer(stack) && getBagId(stack).filter(id::equals).isPresent();
    }

    private static Optional<UUID> getUuid(ItemStack stack, String key) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.hasUUID(key) ? Optional.of(tag.getUUID(key)) : Optional.empty();
    }

    public record BagReference(ItemStack stack, Runnable saveAction) {
        public static BagReference empty() {
            return new BagReference(ItemStack.EMPTY, () -> {
            });
        }

        public boolean isPresent() {
            return !this.stack.isEmpty();
        }

        public void save() {
            if (this.isPresent()) {
                this.saveAction.run();
            }
        }
    }
}
