package com.Vivideru.Goety.client.audio;

import com.Polarice3.Goety.init.ModSounds;
import com.Vivideru.Goety.common.entities.ally.Cerberus;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;

/**
 * Follows a Cerberus and is only audible while it is actually breathing. A one-shot sound replayed on a timer
 * keeps roaring after the breath ends, so the loop is muted the instant the breath stops instead.
 */
public class CerberusBreathSound extends AbstractTickableSoundInstance {
    private static final float BREATH_VOLUME = 1.5F;

    private final Cerberus cerberus;

    public CerberusBreathSound(Cerberus cerberus) {
        super(ModSounds.FIRE_BREATH.get(), cerberus.getSoundSource(), SoundInstance.createUnseededRandom());
        this.cerberus = cerberus;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
        this.pitch = 1.0F;
        this.x = cerberus.getX();
        this.y = cerberus.getY();
        this.z = cerberus.getZ();
    }

    @Override
    public void tick() {
        if (this.cerberus.isRemoved() || !this.cerberus.isAlive()) {
            this.stop();
            return;
        }
        this.x = this.cerberus.getX();
        this.y = this.cerberus.getY();
        this.z = this.cerberus.getZ();
        this.volume = this.cerberus.isBreathing() ? BREATH_VOLUME : 0.0F;
    }
}
