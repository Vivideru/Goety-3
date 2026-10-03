package com.Vivideru.Goety.common.world.structures;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class WindShrineHiddenStructures {
    private static final String HIDDEN_PREFIX = "goety:wind_shrine_expansion";

    private WindShrineHiddenStructures() {
    }

    public static boolean isHidden(String id) {
        return id.startsWith(HIDDEN_PREFIX);
    }

    public static CompletableFuture<Suggestions> filter(ResourceKey<? extends Registry<?>> registryKey, CompletableFuture<Suggestions> suggestions) {
        if (!registryKey.equals(Registries.STRUCTURE)) {
            return suggestions;
        }
        return suggestions.thenApply(result -> {
            List<Suggestion> kept = result.getList().stream().filter(suggestion -> !isHidden(suggestion.getText())).toList();
            return kept.size() == result.getList().size() ? result : new Suggestions(result.getRange(), kept);
        });
    }
}
