package com.Vivideru.Goety.common.entities.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public final class EntityQueries {
    private EntityQueries() {
    }

    /**
     * Same answer as level.getEntitiesOfClass(type, box).isEmpty() (same class test, same NO_SPECTATORS filter), but the
     * search stops at the first entity found instead of collecting all of them.
     */
    public static <T extends Entity> boolean noneOfClass(Level level, Class<T> type, AABB box) {
        List<T> found = new ArrayList<>(1);
        level.getEntities(EntityTypeTest.forClass(type), box, EntitySelector.NO_SPECTATORS, found, 1);
        return found.isEmpty();
    }
}
