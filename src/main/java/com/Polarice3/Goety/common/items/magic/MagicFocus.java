package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class MagicFocus extends Item implements IFocus {
    public ISpell spell;

    public MagicFocus(ISpell spell){
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1)
        );
        this.spell = spell;
    }

    public boolean isEnchantable(ItemStack pStack) {
        return pStack.getCount() == 1;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        // Spell foci use a per-spell whitelist; 1.21's enchanting table also checks primary item tags, which would otherwise reject vanilla enchants like Fortune on valid foci.
        return this.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (stack.getItem() instanceof MagicFocus magicFocus){
            if (magicFocus.getSpell() != null){
                if (!magicFocus.getSpell().acceptedEnchantments().isEmpty()){
                    // Minecraft 1.21 passes enchantments as holders; compare them against the spell's resource-key whitelist.
                    return magicFocus.getSpell().acceptedEnchantments().stream().anyMatch(enchantment::is);
                }
            }
        }
        return false;
    }

    public ISpell getSpell(){
        return this.spell;
    }

    public int getSoulCost() {
        // Focus items are constructed during item registration, before NeoForge has loaded common configs.
        return this.spell.defaultSoulCost();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        int soulCost = this.getSoulCost();
        if (soulCost != 0) {
            tooltip.add(Component.translatable("info.goety.focus.cost", soulCost));
        } else {
            tooltip.add(Component.translatable("info.goety.focus.cost", 0));
        }
        tooltip.add(Component.translatable("info.goety.focus.spellType", spell.getSpellType().getName()));
        tooltip.add(Component.translatable("item.goety.focus.info").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.UNDERLINE));
        ItemHelper.addOnShift(tooltip, () -> addInformationAfterShift(tooltip));
    }

    public void addInformationAfterShift(List<Component> tooltip) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".info").withStyle(ChatFormatting.GRAY));
    }

}
