package com.Vivideru.Goety.client.events;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.client.audio.CerberusBreathSound;
import com.Vivideru.Goety.common.entities.ally.Cerberus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = Goety.MOD_ID, value = Dist.CLIENT)
public final class CerberusClientEvents {
    private CerberusClientEvents() {
    }

    @SubscribeEvent
    public static void attachBreathLoop(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ClientLevel && event.getEntity() instanceof Cerberus cerberus) {
            Minecraft.getInstance().getSoundManager().play(new CerberusBreathSound(cerberus));
        }
    }
}
