package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import com.Polarice3.Goety.utils.GoetyMaceUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class PhilosophersMaceItem extends Item implements ISoulRepair, IPersist {
    private static final int DEFAULT_DURABILITY = 128;
    private static final double DEFAULT_DAMAGE = 9.0D;
    private static final float MACE_DAMAGE_SCALE = 1.0F;

    public PhilosophersMaceItem() {
        // Item properties and attributes are built during registration before configs load, so use the declared defaults here.
        super(new Properties().rarity(Rarity.UNCOMMON).durability(DEFAULT_DURABILITY).fireResistant().attributes(createMaceAttributes()));
    }

    private static ItemAttributeModifiers createMaceAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_DAMAGE_ID, DEFAULT_DAMAGE - 1.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_SPEED_ID, (double)-2.4F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        return ConfiguredItemUtil.durability(ItemConfig.PhilosophersMaceDurability, DEFAULT_DURABILITY);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.isDamaged(stack);
    }

    public int getBarColor(ItemStack stack) {
        if (this.isBroken(stack)) {
            return 0x800000;
        }
        return super.getBarColor(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (this.isBroken(stack)) {
            return 13;
        }
        return super.getBarWidth(stack);
    }

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        if (ItemConfig.PhilosophersMacePersist.get()) {
            if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
                if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                    stack.setDamageValue(stack.getMaxDamage() - 1);
                    onBroken.accept(this);
                }
                return 0;
            }
        }
        return amount;
    }

    @Override
    public boolean isBroken(ItemStack stack) {
        return IPersist.super.isBroken(stack) && ItemConfig.PhilosophersMacePersist.get();
    }

    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        GoetyMaceUtil.trySmashEffects(stack, target, attacker);
        return true;
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        // The Philosopher's Mace is now a full 1.21.1 mace while preserving its existing Goety tool and persistence behavior.
        return GoetyMaceUtil.getAttackDamageBonus(target, damageSource, MACE_DAMAGE_SCALE);
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide && pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(1, pEntityLiving, EquipmentSlot.MAINHAND);
        }

        return true;
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(BlockTags.MINEABLE_WITH_PICKAXE) || pBlock.is(BlockTags.MINEABLE_WITH_AXE) || pBlock.is(BlockTags.MINEABLE_WITH_HOE) || pBlock.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockState) {
        // Minecraft 1.21 queries the stack-aware hook for entity/block tool checks, so keep the mace's legacy all-tool behavior visible there.
        return this.isCorrectToolForDrops(blockState);
    }

    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        if (this.isNotBroken(stack) || !ItemConfig.PhilosophersMacePersist.get()) {
            float blockHard = blockState.getBlock().defaultDestroyTime();
            if (blockState.is(ModTags.Blocks.PHILOSOPHERS_MACE_HARD)){
                return 1.0F;
            } else if (this.isCorrectToolForDrops(stack, blockState) && blockHard >= 1.0F) {
                return 8.0F * blockHard;
            } else {
                return 8.0F;
            }
        }
        return 1.0F;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return ItemConfig.PhilosophersMaceEnchantability.get();
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        ResourceLocation enchantmentId = enchantment.unwrapKey().map(ResourceKey::location).orElse(null);
        // In 1.21 crossbow enchantments also target the main hand, so rely on item tags plus Goety's explicit melee/tool exceptions.
        return (super.supportsEnchantment(stack, enchantment)
                || enchantment.is(Enchantments.LOOTING)
                || enchantment.is(Enchantments.FORTUNE)
                || enchantment.is(Enchantments.EFFICIENCY)
                || Objects.equals(enchantmentId, ResourceLocation.fromNamespaceAndPath("vanillatweaks", "siphon")))
                && !enchantment.is(Enchantments.SWEEPING_EDGE);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() == ModItems.DARK_ALLOY_INGOT.get();
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (ItemConfig.PhilosophersMacePersist.get() && this.isBroken(stack)) {
            // 1.21 removed the stack-aware attribute override; keep the visible broken state while the component-based replacement is rebuilt.
            tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
        }
    }

}
