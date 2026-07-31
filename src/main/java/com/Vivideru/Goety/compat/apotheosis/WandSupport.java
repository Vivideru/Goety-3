package com.Vivideru.Goety.compat.apotheosis;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public final class WandSupport {
    public static final ResourceLocation MASTER_STAFF_ID =
        ResourceLocation.fromNamespaceAndPath("goety_mastery_of_magic", "master_staff");
    private static final String ACTIVE_SLOT_TAG = "MasterStaffActiveSlot";

    private WandSupport() {
    }

    public static ItemStack resolveWand(ItemStack stack) {
        if (ApotheosisCompat.isGoetyWand(stack)) {
            return stack;
        }
        if (MASTER_STAFF_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
            ItemStack selected = getSelectedWand(stack);
            return ApotheosisCompat.isGoetyWand(selected) ? selected : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getSelectedWand(ItemStack masterStaff) {
        IItemHandler handler = masterStaff.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler == null || handler.getSlots() == 0) {
            return ItemStack.EMPTY;
        }

        CustomData customData = masterStaff.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData == null ? null : customData.copyTag();
        int requestedSlot = tag == null ? 0 : tag.getInt(ACTIVE_SLOT_TAG);
        return handler.getStackInSlot(Mth.clamp(requestedSlot, 0, handler.getSlots() - 1));
    }

    public static Player spellCaster(DamageSource source) {
        if (source.getEntity() instanceof Player player) {
            return player;
        }
        if (source.getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof Player player) {
            return player;
        }
        return null;
    }

    public static boolean isGoetySpellDamage(DamageSource source) {
        if (spellCaster(source) == null) {
            return false;
        }
        if (source.typeHolder().unwrapKey()
            .map(key -> "goety".equals(key.location().getNamespace()))
            .orElse(false)) {
            return true;
        }
        return isGoetyEntity(source.getDirectEntity()) || isGoetyEntity(source.getEntity());
    }

    private static boolean isGoetyEntity(Entity entity) {
        return entity != null && entity.getClass().getName().startsWith("com.Polarice3.Goety.");
    }
}
