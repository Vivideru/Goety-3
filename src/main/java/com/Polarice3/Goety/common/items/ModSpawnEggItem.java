package com.Polarice3.Goety.common.items;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.function.Supplier;

public class ModSpawnEggItem extends DeferredSpawnEggItem {

    public ModSpawnEggItem(final Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier, int backgroundColor, int highlightColor, Properties builder) {
        // DeferredHolder's registry type is EntityType<?> in 1.21; DeferredSpawnEggItem only needs it as a mob type supplier.
        super(entityTypeSupplier, backgroundColor, highlightColor, builder);
    }
}
