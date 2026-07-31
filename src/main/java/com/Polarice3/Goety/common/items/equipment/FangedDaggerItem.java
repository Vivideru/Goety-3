package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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

public class FangedDaggerItem extends TieredItem {
    private final float attackDamage;

    public FangedDaggerItem(Tier tier) {
        // In 1.21 the tier no longer injects durability into these custom properties, and the enchanting table rejects non-damageable items.
        super(tier, (new Properties()).rarity(Rarity.UNCOMMON).durability(tier.getUses()).attributes(createDaggerAttributes(tier)));
        this.attackDamage = tier.getAttackDamageBonus();
    }

    private static ItemAttributeModifiers createDaggerAttributes(Tier tier) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_DAMAGE_ID, tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_SPEED_ID, -1.6F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public FangedDaggerItem() {
        this(ModTiers.SPECIAL);
    }

    public float getDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        int fallback = this.getTier() == ModTiers.DARK ? ModTiers.DARK.getUses() : ModTiers.SPECIAL.getUses();
        return this.getTier() == ModTiers.DARK
                ? ConfiguredItemUtil.durability(ItemConfig.DarkToolsDurability, fallback)
                : ConfiguredItemUtil.durability(ItemConfig.SpecialToolsDurability, fallback);
    }

    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return !p_43294_.isCreative();
    }

    public boolean hurtEnemy(ItemStack p_43278_, LivingEntity p_43279_, LivingEntity p_43280_) {
        p_43278_.hurtAndBreak(1, p_43280_, EquipmentSlot.MAINHAND);
        return true;
    }

    public boolean mineBlock(ItemStack p_43282_, Level p_43283_, BlockState p_43284_, BlockPos p_43285_, LivingEntity p_43286_) {
        if (p_43284_.getDestroySpeed(p_43283_, p_43285_) != 0.0F) {
            p_43282_.hurtAndBreak(2, p_43286_, EquipmentSlot.MAINHAND);
        }

        return true;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        // Minecraft 1.21 exposes ranged weapon enchantments as main-hand enchantments, so use item tags instead of matchingSlot.
        return (super.supportsEnchantment(stack, enchantment)
                || enchantment.is(Enchantments.LOOTING))
                && !enchantment.is(Enchantments.SWEEPING_EDGE);
    }
}
