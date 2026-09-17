package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.items.magic.ISpellHolder;
import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps each spell to every item bound to it, so a cooldown on one (e.g. the Soul Healer) lands on all of them.
 */
public class SpellItemCache {
    private static Map<ISpell, List<Item>> spellToItems = null;

    private static Map<ISpell, List<Item>> build() {
        Map<ISpell, List<Item>> map = new IdentityHashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof ISpellHolder holder && holder.getSpell() != null) {
                map.computeIfAbsent(holder.getSpell(), s -> new ArrayList<>()).add(item);
            }
        }
        return map;
    }

    public static List<Item> itemsFor(ISpell spell) {
        if (spellToItems == null) {
            spellToItems = build();
        }
        return spellToItems.getOrDefault(spell, List.of());
    }
}
