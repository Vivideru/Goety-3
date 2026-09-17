package com.Polarice3.Goety.api.entities.ally.illager;

import com.Polarice3.Goety.api.items.magic.IMobCharm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

/**
 * A servant that can carry an {@link IMobCharm} and trigger it by itself.
 */
public interface ICharmUser {
    String CHARM_ITEM = "CharmItem";

    default void charmTick() {
        if (this instanceof Mob mob
                && mob.level() instanceof ServerLevel serverLevel
                && this.getCharm().getItem() instanceof IMobCharm charm) {
            charm.charmTick(this.getCharm());
            if (charm.mobShouldUse(serverLevel, mob, this.getCharm())) {
                charm.mobUse(serverLevel, mob, this.getCharm());
            }
        }
    }

    default ItemStack getCharm() {
        return ItemStack.EMPTY;
    }

    default void setCharm(ItemStack itemStack) {
    }

    default void saveCharmData(CompoundTag compound) {
        if (this instanceof Mob mob && !this.getCharm().isEmpty()) {
            compound.put(CHARM_ITEM, this.getCharm().save(mob.registryAccess()));
        }
    }

    default void readCharmData(CompoundTag compound) {
        if (this instanceof Mob mob && compound.contains(CHARM_ITEM, Tag.TAG_COMPOUND)) {
            ItemStack.parse(mob.registryAccess(), compound.getCompound(CHARM_ITEM)).ifPresent(this::setCharm);
        }
    }
}
