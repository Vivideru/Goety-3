package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** A Goety focus made from the config: named and described there, it casts the spell that summons its servant. */
public class ExtraServantFocus extends MagicFocus {
    private final ExtraServantDefinition definition;
    private final ExtraServantFocusDefinition focus;

    public ExtraServantFocus(ExtraServantDefinition definition, ExtraServantFocusDefinition focus) {
        super(new ExtraServantSpell(definition, focus));
        this.definition = definition;
        this.focus = focus;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatableWithFallback(this.getDescriptionId(stack), this.focus.name());
    }

    @Override
    public void addInformationAfterShift(List<Component> tooltip) {
        Component text;
        if (!this.focus.description().isEmpty()) {
            text = Component.translatableWithFallback(this.getDescriptionId() + ".info", this.focus.description());
        } else {
            EntityType<?> parent = ExtraServants.parentType(this.definition);
            Component servant = parent != null ? Component.translatable("entity.goety.extra_servant", parent.getDescription()) : Component.literal(this.definition.id());
            text = Component.translatable("item.goety.extra_servant_focus.info", servant);
        }
        tooltip.add(text.copy().withStyle(ChatFormatting.GRAY));
    }
}
