package com.Polarice3.Goety.api.blocks.entities;

import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;

import org.jetbrains.annotations.Nullable;

public interface IWaystoneBlock {

    @Nullable
    default Direction getDirection(){
        return null;
    }

    @Nullable
    default GlobalPos getPosition(){
        return null;
    }

    default int getSoulCost(){
        return 0;
    }

    default boolean isShowBlock(){
        return false;
    }

    default void setShowBlock(boolean showBlock){
    }
}

