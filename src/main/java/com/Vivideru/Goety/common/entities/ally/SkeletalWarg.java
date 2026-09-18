package com.Vivideru.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * The Skeletal Warg has its own entity type so it can join #minecraft:undead like the Skeleton Wolf, which gives it
 * Smite weakness, inverted healing and harming, and poison/regeneration immunity from vanilla's own tags. It keeps
 * every Warg behaviour and is simply locked to the skeletal breed.
 */
public class SkeletalWarg extends Warg {
    public SkeletalWarg(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        super.setVariant(Variant.SKELETAL);
    }

    @Override
    public Variant getVariant() {
        return Variant.SKELETAL;
    }

    @Override
    public void setVariant(Variant variant) {
        super.setVariant(Variant.SKELETAL);
    }
}
