package com.Vivideru.Goety.common.entities.extra;

import org.jetbrains.annotations.Nullable;

/** Implemented by every extra servant class (the copies of {@link ExtraServantTemplate}). */
public interface ExtraServant {
    /** The config entry this servant comes from. */
    @Nullable
    ExtraServantDefinition goety$definition();

    /** While true the servant reports the mob it is made from as its type, so that mob's renderer draws it as its own. */
    void goety$setRenderingAsParent(boolean asParent);
}
