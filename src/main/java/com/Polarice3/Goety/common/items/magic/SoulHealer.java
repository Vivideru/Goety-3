package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.curios.IActivatable;
import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.items.magic.IMobCharm;
import com.Polarice3.Goety.api.items.magic.ISoulContainer;
import com.Polarice3.Goety.api.items.magic.ISpellHolder;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

/**
 * Soul Heal charm: the wearer casts it with the curio key, and an Illager servant carrying it heals itself with souls
 * it collects from its kills.
 */
public class SoulHealer extends SingleStackItem implements ISoulContainer, IActivatable, IMobCharm, ISpellHolder {

    public SoulHealer(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public ISpell getSpell() {
        // The cooldown cache matches spells by identity, so reuse the Soul Heal focus's own instance to share its cooldown.
        return ((IFocus) ModItems.SOUL_HEAL_FOCUS.get()).getSpell();
    }

    @Override
    public int getMaxSouls() {
        return this.getSpell().defaultSoulCost() * 6;
    }

    public int getSoulCost(LivingEntity livingEntity) {
        return WandUtil.getSoulUse(livingEntity, this.getDefaultInstance(), this.getSpell().soulCost(livingEntity, ItemStack.EMPTY));
    }

    @Override
    public void activate(Level level, Player player, ItemStack itemStack) {
        this.playerUse(level, player, itemStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.playerUse(level, player, itemstack)) {
            return InteractionResultHolder.success(itemstack);
        }
        return super.use(level, player, hand);
    }

    private boolean playerUse(Level level, Player player, ItemStack itemStack) {
        if (SEHelper.isOnCooldown(player, itemStack)) {
            return false;
        }
        int soulCost = this.getSoulCost(player);
        if (!SEHelper.getSoulsAmount(player, soulCost)) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            this.mobUse(serverLevel, player, itemStack);
            if (!player.getAbilities().instabuild) {
                SEHelper.decreaseSouls(player, soulCost);
                SEHelper.sendSEUpdatePacket(player);
            }
        }
        return true;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        // Souls and cooldown change the stack's data constantly; only a different item should replay the equip bob.
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean mobShouldUse(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        return ISoulContainer.currentSouls(itemStack) >= this.getSoulCost(livingEntity)
                && livingEntity.getHealth() <= livingEntity.getMaxHealth() * 0.5F
                && IMobCharm.isNotOnCoolDown(itemStack);
    }

    @Override
    public void mobUse(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        SpellStat spellStat = this.getSpell().defaultStats();
        int potency = spellStat.getPotency();
        double radius = spellStat.getRadius();
        if (itemStack.isEnchanted()) {
            potency += WandUtil.getPotencyItemLevel(livingEntity, itemStack);
            radius += WandUtil.getItemLevel(ModEnchantments.RADIUS, livingEntity, itemStack);
        }
        spellStat = spellStat.setPotency(potency).setRadius(radius);
        this.getSpell().SpellResult(serverLevel, livingEntity, ItemStack.EMPTY, spellStat);
        if (livingEntity instanceof Player player) {
            SEHelper.addSpellCooldown(player, this.getSpell(), this.getSpell().spellCooldown(livingEntity));
        } else {
            ISoulContainer.decreaseSouls(itemStack, this.getSoulCost(livingEntity));
            IMobCharm.setCoolDown(itemStack, this.getSpell().spellCooldown(livingEntity));
        }
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return this.getSpell().acceptedEnchantments().stream().anyMatch(enchantment::is);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("info.goety.soul_healer", ModKeybindings.useCurios().getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("info.goety.soul_healer.give").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.empty());
        Player player = Goety.PROXY.getPlayer();
        if (player != null) {
            tooltip.add(Component.translatable("info.goety.wand.cost", this.getSoulCost(player)));
            tooltip.add(Component.translatable("info.goety.wand.coolDown", this.getSpell().spellCooldown(player) / 20.0F));
        }
    }
}
