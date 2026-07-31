package com.Vivideru.Goety.compat.apotheosis;

import dev.shadowsoffire.apotheosis.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.AttributeBonus;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import dev.shadowsoffire.apothic_attributes.modifiers.StackAttributeModifiersEvent;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public final class GemPotencyCapHandler {
    private static final Map<ResourceLocation, ResourceLocation> POTENCY_GEMS = Map.ofEntries(
        gem("core/lightning", "storm_potency"),
        gem("core/lunar", "frost_potency"),
        gem("core/slipstream", "wind_potency"),
        gem("core/tyrannical", "necromancy_potency"),
        gem("core/warlord", "spell_potency"),
        gem("overworld/earth", "geomancy_potency"),
        gem("overworld/verdant_ruin", "wild_potency"),
        gem("the_end/endersurge", "void_potency"),
        gem("the_end/mageslayer", "abyss_potency"),
        gem("the_nether/inferno", "nether_potency")
    );

    private GemPotencyCapHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void capGemPotency(StackAttributeModifiersEvent event) {
        if (!ApotheosisCompat.isGoetyWand(event.getItemStack())) {
            return;
        }

        Map<ResourceLocation, Double> totals = new HashMap<>();
        for (GemInstance gem : SocketHelper.getGems(event.getItemStack())) {
            // Resolve the compatibility bonus from the gem id instead of relying on the
            // category cached in an older socketed stack. The containing item has already
            // been verified as a Goety wand, so all nine school mappings are safe here.
            if (!gem.gem().isBound()) {
                continue;
            }
            ResourceLocation attributeId = POTENCY_GEMS.get(gem.gem().getId());
            if (attributeId == null) {
                continue;
            }

            Holder<Attribute> attribute = attribute(attributeId);
            double value = purityValue(gem.purity());
            if (gem.getBonus().orElse(null) instanceof AttributeBonus bonus) {
                AttributeModifier nativeModifier = bonus.createModifier(gem);
                value = nativeModifier.amount();
                event.removeModifier(attribute, nativeModifier.id());
            }
            totals.merge(attributeId, value, Double::sum);
        }

        double cap = GoetyApotheosisConfig.MAX_GEM_POTENCY_PER_WAND.get();
        totals.forEach((attributeId, total) -> {
            double capped = Math.min(total, cap);
            if (capped > 0.0D) {
                event.addModifier(
                    attribute(attributeId),
                    new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(
                            ApotheosisCompat.MOD_ID,
                            "capped_gem_potency/" + attributeId.getPath()
                        ),
                        capped,
                        AttributeModifier.Operation.ADD_VALUE
                    ),
                    ALObjects.EquipmentSlotGroups.MAINHAND
                );
            }
        });
    }

    private static Map.Entry<ResourceLocation, ResourceLocation> gem(String gemPath, String attributePath) {
        return Map.entry(
            ResourceLocation.fromNamespaceAndPath("apotheosis", gemPath),
            ResourceLocation.fromNamespaceAndPath("goety", attributePath)
        );
    }

    private static Holder<Attribute> attribute(ResourceLocation id) {
        return BuiltInRegistries.ATTRIBUTE.getHolder(id)
            .orElseThrow(() -> new IllegalStateException("Missing Goety attribute " + id));
    }

    private static double purityValue(Purity purity) {
        return switch (purity) {
            case CRACKED -> 0.25D;
            case CHIPPED -> 1.0D;
            case FLAWED -> 2.0D;
            case NORMAL -> 3.0D;
            case FLAWLESS -> 4.0D;
            case PERFECT -> 5.0D;
        };
    }
}
