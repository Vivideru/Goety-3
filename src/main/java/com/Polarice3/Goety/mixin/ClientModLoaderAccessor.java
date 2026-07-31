package com.Polarice3.Goety.mixin;

import net.neoforged.neoforge.client.loading.ClientModLoader;
import net.neoforged.fml.ModLoadingException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientModLoader.class)
public interface ClientModLoaderAccessor {

    @Accessor(value = "error", remap = false)
    static ModLoadingException getError() {
        throw new IllegalStateException("Failed to inject Accessor");
    }
}
