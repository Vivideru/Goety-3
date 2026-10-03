package com.Polarice3.Goety.common.events.spell;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.ICancellableEvent;

public class StopMagicEvent extends LivingEvent implements ICancellableEvent {
    private final ISpell spell;
    private final ItemStack useItem;
    private final int castTime;
    private final int timeRemaining;

    public StopMagicEvent(LivingEntity entity, ItemStack useItem, ISpell spell, int castTime, int timeRemaining) {
        super(entity);
        this.useItem = useItem;
        this.spell = spell;
        this.castTime = castTime;
        this.timeRemaining = timeRemaining;
    }

    public ISpell getSpell(){
        return this.spell;
    }

    public ItemStack getUseItem() {
        return this.useItem;
    }

    public int castingTime() {
        return this.castTime;
    }

    public int getTimeRemaining() {
        return this.timeRemaining;
    }
}
