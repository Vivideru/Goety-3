package com.Polarice3.Goety.client.render.block;

public final class ModISTERs {
    private static ModISTER instance;

    private ModISTERs() {
    }

    public static ModISTER get() {
        // Item renderers are stateless apart from their reusable block entity cache.
        if (instance == null) {
            instance = new ModISTER();
        }
        return instance;
    }
}
