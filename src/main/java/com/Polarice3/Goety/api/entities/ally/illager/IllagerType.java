package com.Polarice3.Goety.api.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.train.ModIllagerType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

// This enum is not registered in enumextensions.json, so it must stay a regular enum on NeoForge.
public enum IllagerType {
    NONE(null),
    NORMAL(new ModIllagerType());

    private final ITrainIllager illager;

    IllagerType(ITrainIllager illager){
        this.illager = illager;
    }

    public ITrainIllager getIllager(){
        return this.illager;
    }

    public static ITrainIllager getIllagerFromType(Level level, BlockPos blockPos, int range, EntityType<? extends Mob> entityType){
        for (IllagerType illagerType : IllagerType.values()){
            if (illagerType.getIllager() != null) {
                if (illagerType.getIllager().getIllager(level, blockPos, range) == entityType) {
                    return illagerType.getIllager();
                }
            }
        }
        return null;
    }

    public static List<IllagerType> getIllagerList(){
        List<IllagerType> list = new ArrayList<>();
        for (IllagerType illagerType : IllagerType.values()){
            if (illagerType.illager != null) {
                list.add(illagerType);
            }
        }
        return list;
    }
}

