package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.compat.curios.CuriosLoaded;
import com.Vivideru.Goety.common.items.magic.FocusBagBinding;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

public class TotemFinder {

    private static boolean isFocusBag(ItemStack itemStack) {
        return FocusBagBinding.isFocusContainer(itemStack);
    }

    public static ItemStack findBag(Player playerEntity) {
        return findBagReference(playerEntity).stack();
    }

    public static FocusBagBinding.BagReference findBagReference(Player playerEntity) {
        ItemStack wand = WandUtil.findWand(playerEntity);
        if (FocusBagBinding.isBound(wand)) {
            // A bound wand must not silently fall back to another bag when its selected bag is unavailable.
            return FocusBagBinding.findBoundBag(playerEntity, wand);
        }

        for (int i = 0; i < playerEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && isFocusBag(itemStack)) {
                int slot = i;
                return new FocusBagBinding.BagReference(itemStack, () -> {
                    playerEntity.getInventory().setItem(slot, itemStack);
                    playerEntity.getInventory().setChanged();
                });
            }
        }

        if (CuriosLoaded.CURIOS.isLoaded()) {
            FocusBagBinding.BagReference[] result = {FocusBagBinding.BagReference.empty()};
            CuriosApi.getCuriosInventory(playerEntity).ifPresent(curios -> curios.getCurios().values().forEach(slotHandler -> {
                if (result[0].isPresent()) {
                    return;
                }
                IDynamicStackHandler stacks = slotHandler.getStacks();
                for (int slot = 0; slot < stacks.getSlots(); ++slot) {
                    ItemStack stack = stacks.getStackInSlot(slot);
                    if (isFocusBag(stack)) {
                        int curioSlot = slot;
                        result[0] = new FocusBagBinding.BagReference(stack, () -> stacks.setStackInSlot(curioSlot, stack));
                        break;
                    }
                }
            }));
            return result[0];
        }

        return FocusBagBinding.BagReference.empty();
    }

    public static ItemStack findFocusInBag(Player player){
        ItemStack foundStack = ItemStack.EMPTY;
        ItemStack bag = findBag(player);
        if (!bag.isEmpty()){
            FocusBagItemHandler focusBagItemHandler = FocusBagItemHandler.get(bag);
            for (int i = 1; i < focusBagItemHandler.getSlots(); ++i){
                ItemStack itemStack = focusBagItemHandler.getStackInSlot(i);
                if (itemStack.getItem() instanceof IFocus){
                    foundStack = itemStack;
                }
            }
        }
        return foundStack;
    }

    public static int getFocusBagTotal(Player player){
        int num = 0;
        ItemStack bag = findBag(player);
        if (!bag.isEmpty()){
            FocusBagItemHandler focusBagItemHandler = FocusBagItemHandler.get(bag);
            for (int i = 1; i < focusBagItemHandler.getSlots(); ++i){
                ItemStack itemStack = focusBagItemHandler.getStackInSlot(i);
                if (itemStack.getItem() instanceof IFocus){
                    ++num;
                }
            }
        }
        return num;
    }

    public static boolean hasEmptyBagSpace(Player player){
        int total = 10;
        ItemStack bag = findBag(player);
        if (!bag.isEmpty()){
            FocusBagItemHandler focusBagItemHandler = FocusBagItemHandler.get(bag);
            total = focusBagItemHandler.getSlots();
        }
        return getFocusBagTotal(player) < total;
    }

    public static boolean hasFocusInBag(Player player){
        return !findFocusInBag(player).isEmpty();
    }

    public static boolean canOpenWandCircle(Player player){
        return hasFocusInBag(player) || WandUtil.hasFocusInInv(player) || !WandUtil.findFocus(player).isEmpty();
    }

    private static boolean isTotem(ItemStack itemStack) {
        return itemStack.getItem() instanceof ITotem;
    }

    public static ItemStack FindTotem(Player playerEntity){
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            Optional<SlotResult> slotResult = CuriosApi.getCuriosInventory(playerEntity).map(inv -> inv.findFirstCurio(TotemFinder::isTotem))
                    .orElse(Optional.empty());
            if (slotResult.isPresent()) {
                foundStack = slotResult.get().stack();
            }
        }

        if (isTotem(playerEntity.getOffhandItem())){
            foundStack = playerEntity.getOffhandItem();
        } else {
            for (int i = 0; i <= 9; i++) {
                ItemStack itemStack = playerEntity.getInventory().getItem(i);
                if (!itemStack.isEmpty() && isTotem(itemStack)) {
                    foundStack = itemStack;
                    break;
                }
            }
        }
        return foundStack;
    }
}
