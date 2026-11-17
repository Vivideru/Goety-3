package com.Vivideru.Goety.common.entities.util;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.util.BrewGas;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Client side spawn of the particle each BrewGas used to send from the server every tick, with the same values and
 * the same reach (ServerLevel.sendParticles only reaches players within 32 blocks).
 */
public final class BrewGasParticles {
    private BrewGasParticles() {
    }

    public static void clientTick(BrewGas gas) {
        Player player = Goety.PROXY.getPlayer();
        if (player == null || player.level() != gas.level()) {
            return;
        }
        double x = gas.getRandomX(1.0D);
        double y = gas.getRandomY();
        double z = gas.getRandomZ(1.0D);
        if (!player.blockPosition().closerToCenterThan(new Vec3(x, y, z), 32.0D)) {
            return;
        }
        ColorUtil color = new ColorUtil(gas.getColor());
        gas.level().addParticle(ModParticleTypes.CULT_SPELL.get(), x, y, z, 0.5F * color.red, 0.5F * color.green, 0.5F * color.blue);
    }
}
