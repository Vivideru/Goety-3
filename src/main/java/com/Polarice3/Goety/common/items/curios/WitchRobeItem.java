package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class WitchRobeItem extends SingleStackItem {
    public static String INVENTORY = "WITCH_ROBE_BREW";

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void putInventory(ItemStack stack, int inventory) {
        // ItemStack root NBT was removed in 1.21; mutate a CUSTOM_DATA copy and write it back.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, compound -> compound.putInt(INVENTORY, inventory));
    }

    public static int getInventoryId(ItemStack stack) {
        return tag(stack).getInt(INVENTORY);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            if (ModSaveInventory.getInstance() != null) {
                if (!tag(stack).contains(INVENTORY)) {
                    putInventory(stack, ModSaveInventory.getInstance().addAndCreateWitchRobe());
                } else {
                    WitchRobeInventory inventory = ModSaveInventory.getInstance().getWitchRobeInventory((tag(stack).getInt(INVENTORY)), livingEntity);

                    if (!worldIn.isClientSide) {
                        if (CuriosFinder.hasWitchHat(livingEntity)) {
                            inventory.setIncreaseSpeed(1);
                        } else {
                            inventory.setIncreaseSpeed(0);
                        }

                        inventory.tick();
                    }
                }
            }
        }
    }
}
