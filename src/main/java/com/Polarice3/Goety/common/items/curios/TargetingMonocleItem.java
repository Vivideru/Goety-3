package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.api.items.curios.IActivatable;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

public class TargetingMonocleItem extends SingleStackItem implements IActivatable {
    private static final String IS_ACTIVE = "Activated";

    @Override
    public void activate(Level level, Player player, ItemStack itemStack) {
        if (itemStack.is(this)) {
            if (player.isCrouching() || !isActive(itemStack)) {
                setIsActive(itemStack, !isActive(itemStack));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TOCK.get(), player.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isActive(stack);
    }

    public static void setIsActive(ItemStack stack, boolean activate){
        // ItemStack root NBT was removed in 1.21; mutate a CUSTOM_DATA copy and write it back.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, compound -> compound.putBoolean(IS_ACTIVE, activate));
    }

    public static boolean isActive(ItemStack stack) {
        CompoundTag compound = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return compound.getBoolean(IS_ACTIVE);
    }

    @Override
    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
