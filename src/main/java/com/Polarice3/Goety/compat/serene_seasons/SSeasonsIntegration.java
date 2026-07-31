package com.Polarice3.Goety.compat.serene_seasons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import sereneseasons.season.SeasonHooks;

public class SSeasonsIntegration {

    public static boolean summonSnowVariant(Level level, BlockPos pos){
        return SeasonHooks.coldEnoughToSnowSeasonal(level, pos);
    }
}
