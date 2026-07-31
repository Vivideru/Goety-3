package com.Polarice3.Goety.api.blocks;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Optional;

public interface IEnchantedBlock {
    Object2IntMap<Enchantment> getEnchantments();

    default void loadEnchants(CompoundTag p_222787_){
        ListTag enchants = p_222787_.getList("enchantments", Tag.TAG_COMPOUND);
        getEnchantments().clear();
        this.enchantmentLookup().ifPresent(lookup -> {
            for (int i = 0; i < enchants.size(); ++i) {
                CompoundTag tag = enchants.getCompound(i);
                ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse(tag.getString("id")));
                lookup.get(key).ifPresent(holder -> getEnchantments().put(holder.value(), tag.getInt("lvl")));
            }
        });
    }

    default void saveEnchants(CompoundTag p_222789_, Item item) {
        ListTag listTag = new ListTag();
        this.enchantmentLookup().ifPresent(lookup -> getEnchantments().forEach((enchantment, level) -> lookup.listElements()
                .filter(holder -> holder.value() == enchantment)
                .findFirst()
                .ifPresent(holder -> {
                    CompoundTag tag = new CompoundTag();
                    tag.putString("id", holder.key().location().toString());
                    tag.putInt("lvl", level);
                    listTag.add(tag);
                })));
        p_222789_.put("enchantments", listTag);
    }

    default Optional<HolderLookup.RegistryLookup<Enchantment>> enchantmentLookup() {
        return Optional.ofNullable(CommonHooks.resolveLookup(Registries.ENCHANTMENT));
    }

}

