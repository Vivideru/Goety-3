package com.Polarice3.Goety.api.items.magic;

import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.TotemFinder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public interface ITotem {
    String SOULS_AMOUNT = "Souls";
    String MAX_SOUL_AMOUNT = "Max Souls";

    static int maxSouls() {
        // Config values are unavailable while NeoForge is constructing registries, so callers read the limit lazily.
        return MainConfig.MaxSouls.get();
    }

    int getMaxSouls();

    default void setTagTick(ItemStack stack){
        updateTag(stack, compound -> {
            if (!compound.contains(SOULS_AMOUNT)) {
                compound.putInt(SOULS_AMOUNT, 0);
            }
            if (!compound.contains(MAX_SOUL_AMOUNT)){
                compound.putInt(MAX_SOUL_AMOUNT, this.getMaxSouls());
            }
            if (compound.getInt(SOULS_AMOUNT) > compound.getInt(MAX_SOUL_AMOUNT)){
                compound.putInt(SOULS_AMOUNT, compound.getInt(MAX_SOUL_AMOUNT));
            }
            if (compound.getInt(SOULS_AMOUNT) < 0){
                compound.putInt(SOULS_AMOUNT, 0);
            }
        });
    }

    static boolean isFull(ItemStack itemStack) {
        CompoundTag tag = tag(itemStack);
        if (tag.isEmpty()){
            return false;
        }
        int Soulcount = tag.getInt(SOULS_AMOUNT);
        int MaxSouls = tag.getInt(MAX_SOUL_AMOUNT);
        return Soulcount == MaxSouls;
    }

    static boolean isEmpty(ItemStack itemStack) {
        CompoundTag tag = tag(itemStack);
        if (tag.isEmpty()){
            return true;
        }
        int Soulcount = tag.getInt(SOULS_AMOUNT);
        return Soulcount == 0;
    }

    static boolean UndyingEffect(Player player){
        ItemStack itemStack = TotemFinder.FindTotem(player);
        if (!itemStack.isEmpty()) {
                CompoundTag tag = tag(itemStack);
            if (!tag.isEmpty()) {
                if (MainConfig.TotemUndying.get()) {
                    return tag.getInt(SOULS_AMOUNT) == maxSouls();
                }
            }
        }
        return false;
    }

    static int currentSouls(ItemStack itemStack){
        return tag(itemStack).getInt(SOULS_AMOUNT);
    }

    static int maximumSouls(ItemStack itemStack){
        return tag(itemStack).getInt(MAX_SOUL_AMOUNT);
    }

    static void setSoulsamount(ItemStack itemStack, int souls){
        if (!(itemStack.getItem() instanceof ITotem)) {
            return;
        }
        updateTag(itemStack, compound -> compound.putInt(SOULS_AMOUNT, souls));
    }

    static void setMaxSoulAmount(ItemStack itemStack, int souls){
        if (!(itemStack.getItem() instanceof ITotem)) {
            return;
        }
        updateTag(itemStack, compound -> compound.putInt(MAX_SOUL_AMOUNT, souls));
    }

    static void increaseSouls(ItemStack itemStack, int souls) {
        if (!(itemStack.getItem() instanceof ITotem) || tag(itemStack).isEmpty()) {
            return;
        }
        int Soulcount = tag(itemStack).getInt(SOULS_AMOUNT);
        if (!isFull(itemStack)) {
            int finalCount = Math.min(Soulcount + souls, maximumSouls(itemStack));
            updateTag(itemStack, compound -> compound.putInt(SOULS_AMOUNT, finalCount));
        }
    }

    static void decreaseSouls(ItemStack itemStack, int souls) {
        if (!(itemStack.getItem() instanceof ITotem) || tag(itemStack).isEmpty()) {
            return;
        }
        int Soulcount = tag(itemStack).getInt(SOULS_AMOUNT);
        if (!isEmpty(itemStack)) {
            int finalCount = Math.max(Soulcount - souls, 0);
            updateTag(itemStack, compound -> compound.putInt(SOULS_AMOUNT, finalCount));
        }
    }

    static CompoundTag tag(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    static void updateTag(ItemStack itemStack, java.util.function.Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, updater);
    }
}

