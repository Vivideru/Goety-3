package com.Vivideru.Goety.common.items;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Vivideru.Goety.common.entities.ally.Warg;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class WargSpawnEggItem extends ServantSpawnEggItem {
    private final Warg.Variant variant;
    private final boolean hostile;

    public WargSpawnEggItem(Supplier<? extends net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> entityTypeSupplier,
                            int backgroundColor, int highlightColor, Item.Properties properties,
                            Warg.Variant variant, boolean hostile) {
        super(entityTypeSupplier, backgroundColor, highlightColor, properties);
        this.variant = variant;
        this.hostile = hostile;
    }

    @Override
    protected void configureSpawnedEntity(Entity entity) {
        if (entity instanceof Warg warg) {
            warg.setVariant(this.variant);
            warg.setHostile(this.hostile);
        }
    }
}
