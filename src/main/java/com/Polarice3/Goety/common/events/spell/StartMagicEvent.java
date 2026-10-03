package com.Polarice3.Goety.common.events.spell;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.ICancellableEvent;

public class StartMagicEvent extends LivingEvent implements ICancellableEvent {
    private ISpell spell;
    private final ItemStack useItem;

    public StartMagicEvent(LivingEntity entity, ItemStack useItem, ISpell spell) {
        super(entity);
        this.useItem = useItem;
        this.spell = spell;
    }

    public ISpell getSpell(){
        return this.spell;
    }

    public void setSpell(ISpell spell) {
        this.spell = spell;
    }

    public ItemStack getUseItem() {
        return this.useItem;
    }
}
