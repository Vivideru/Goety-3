package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class SickleItem extends TieredItem {
    private static final float DEFAULT_BASE_DAMAGE = 0.5F;
    private static final double DEFAULT_ATTACK_SPEED = 3.0D;
    private static float initialDamage = DEFAULT_BASE_DAMAGE;

    public SickleItem(Tier itemTier) {
        super(itemTier, new Properties().rarity(Rarity.UNCOMMON).durability(itemTier.getUses()).attributes(createSickleAttributes(itemTier)));
    }

    public SickleItem(){
        this(ModTiers.SPECIAL);
    }

    private static ItemAttributeModifiers createSickleAttributes(Tier itemTier) {
        // Item attributes are created during registry setup before configs load, so the runtime config is read from getMaxDamage only.
        initialDamage = DEFAULT_BASE_DAMAGE + itemTier.getAttackDamageBonus();
        double attackSpeed = 4.0D - DEFAULT_ATTACK_SPEED;
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_DAMAGE_ID, initialDamage - 1.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_SPEED_ID, -attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static float getInitialDamage() {
        return initialDamage;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        return com.Polarice3.Goety.utils.ConfiguredItemUtil.durability(ItemConfig.SpecialToolsDurability, ModTiers.SPECIAL.getUses());
    }

    public boolean getMineBlocks(Level pLevel, BlockState pState, BlockPos pPos){
        if (pState.getDestroySpeed(pLevel, pPos) <= -1.0F) {
            return false;
        }
        return pState.is(BlockTags.MINEABLE_WITH_HOE) || pState.is(Blocks.COBWEB) || BlockFinder.isScytheBreak(pState);
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (pState.is(Blocks.COBWEB)) {
            return 15.0F;
        }
        return pState.is(BlockTags.MINEABLE_WITH_HOE) ? 8.0F : 1.0F;
    }

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, EquipmentSlot.MAINHAND);
        return true;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(this.getMineBlocks(pLevel, pState, pPos) ? 1 : 2, pEntityLiving, EquipmentSlot.MAINHAND);
        }
        if (this.getMineBlocks(pLevel, pState, pPos)){
            pLevel.playSound((Player) null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.SCYTHE_HIT.get(), pEntityLiving.getSoundSource(), 1.0F, 1.0F);
            for (BlockPos blockPos : BlockFinder.multiBlockBreak(pEntityLiving, pPos, 1, 1, 1)){
                BlockState blockstate = pLevel.getBlockState(blockPos);
                if (this.getMineBlocks(pLevel, blockstate, blockPos)){
                    if (BlockFinder.breakBlock(pLevel, blockPos, pStack, pEntityLiving)){
                        if (blockstate.getDestroySpeed(pLevel, blockPos) != 0) {
                            pStack.hurtAndBreak(1, pEntityLiving, EquipmentSlot.MAINHAND);
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(BlockTags.MINEABLE_WITH_HOE) || pBlock.is(Blocks.COBWEB);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockState) {
        // Minecraft 1.21 asks the stack-aware hook for tool checks, so mirror the sickle's legacy hoe/cobweb behavior.
        return this.isCorrectToolForDrops(blockState);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        ResourceLocation enchantmentId = enchantment.unwrapKey().map(ResourceKey::location).orElse(null);
        // Minecraft 1.21 exposes enchantment categories through tags, so keep the old weapon/digger allowances via vanilla checks and explicit extras.
        return super.supportsEnchantment(stack, enchantment)
                || enchantment.is(Enchantments.LOOTING)
                || enchantment.is(Enchantments.FORTUNE)
                || Objects.equals(enchantmentId, ResourceLocation.fromNamespaceAndPath("vanillatweaks", "siphon"));
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() == ModItems.PALE_STEEL_INGOT.get() || super.isValidRepairItem(pToRepair, pRepair);
    }
}
