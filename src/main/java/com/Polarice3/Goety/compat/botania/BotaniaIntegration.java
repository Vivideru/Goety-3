package com.Polarice3.Goety.compat.botania;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import vazkii.botania.api.block.PetalApothecary;

public class BotaniaIntegration {

    public static boolean fillApothecary(BlockPos pos, Level world) {
        if (world.getBlockEntity(pos) instanceof PetalApothecary apothecary) {
            if (apothecary.getFluid() == PetalApothecary.State.EMPTY) {
                apothecary.setFluid(PetalApothecary.State.WATER);
                return true;
            }
        }
        return false;
    }
}
