package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.render.item.CustomItemsRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class NamelessStaff extends DarkStaff{
    private static final double DEFAULT_DAMAGE = 6.0D;

    public NamelessStaff() {
        // Staff attributes are baked into item components during registration before configs load, so use the declared default here.
        super(DEFAULT_DAMAGE, SpellType.NECROMANCY);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        // NeoForge only permits one client extension per item; this subclass replaces DarkWand's renderer extension.
        consumer.accept(new DarkWandClient() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new CustomItemsRenderer();
            }
        });
    }
}
