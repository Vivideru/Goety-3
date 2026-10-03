package com.Vivideru.Goety.mixin;

import com.Vivideru.Goety.common.world.structures.WindShrineHiddenStructures;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ResourceOrTagKeyArgument.class)
public abstract class ResourceOrTagKeyArgumentWindShrineMixin<T> {
    @Shadow
    @Final
    ResourceKey<? extends Registry<T>> registryKey;

    @Inject(method = "listSuggestions", at = @At("RETURN"), cancellable = true)
    private <S> void goety$hideWindShrineWings(CommandContext<S> context, SuggestionsBuilder builder, CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
        cir.setReturnValue(WindShrineHiddenStructures.filter(this.registryKey, cir.getReturnValue()));
    }
}
