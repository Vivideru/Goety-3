package com.Polarice3.Goety.utils;

import java.util.function.BooleanSupplier;

public interface EventTask extends BooleanSupplier {
    default void startTask(){
    }

    default void tickTask(){
    }

    default void endTask(){
    }
}
