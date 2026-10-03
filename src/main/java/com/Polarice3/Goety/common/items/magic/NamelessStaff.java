package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.render.item.CustomItemsRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class NamelessStaff extends DarkStaff{
    private static final double DEFAULT_DAMAGE = 6.0D;

    public NamelessStaff() {
        super(DEFAULT_DAMAGE, SpellType.NECROMANCY);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new DarkWandClient() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new CustomItemsRenderer();
            }
        });
    }
}
