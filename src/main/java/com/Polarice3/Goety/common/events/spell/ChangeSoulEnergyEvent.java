package com.Polarice3.Goety.common.events.spell;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.ICancellableEvent;

public class ChangeSoulEnergyEvent extends PlayerEvent implements ICancellableEvent {
    private int soulChange;

    public ChangeSoulEnergyEvent(Player entity, int soulChange) {
        super(entity);
        this.soulChange = soulChange;
    }

    public int getSoulChange() {
        return this.soulChange;
    }

    public void setSoulChange(int soulChange) {
        this.soulChange = soulChange;
    }

        public static class Gain extends ChangeSoulEnergyEvent {
        public Gain(Player e, int soulChange){
            super(e, soulChange);
        }
    }

        public static class Loss extends ChangeSoulEnergyEvent {
        public Loss(Player e, int soulChange){
            super(e, soulChange);
        }
    }
}
