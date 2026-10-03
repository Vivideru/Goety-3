package com.Polarice3.Goety.common.events.spell;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.ICancellableEvent;

public class CastMagicEvent extends LivingEvent implements ICancellableEvent {
    private ISpell spell;

    public CastMagicEvent(LivingEntity entity, ISpell spell) {
        super(entity);
        this.spell = spell;
    }

    public ISpell getSpell(){
        return this.spell;
    }

    public void setSpell(ISpell spell) {
        this.spell = spell;
    }
}
