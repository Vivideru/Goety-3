package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.utils.MobTypeHelper;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class DarkScytheItem extends TieredItem {
    private static final float DEFAULT_BASE_DAMAGE = 4.5F;
    private static final double DEFAULT_ATTACK_SPEED = 0.6D;
    private static float initialDamage = DEFAULT_BASE_DAMAGE;

    public DarkScytheItem(Tier itemTier) {
        super(itemTier, new Properties().rarity(Rarity.UNCOMMON).durability(itemTier.getUses()).attributes(createScytheAttributes(itemTier)));
    }

    private static ItemAttributeModifiers createScytheAttributes(Tier itemTier) {
        // Item attributes are built during registration before configs load, so use the declared defaults here.
        initialDamage = DEFAULT_BASE_DAMAGE + itemTier.getAttackDamageBonus();
        double attackSpeed = 4.0D - DEFAULT_ATTACK_SPEED;
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_DAMAGE_ID, initialDamage - 1.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_SPEED_ID, -attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                // NeoForge's old ENTITY_REACH attribute is vanilla ENTITY_INTERACTION_RANGE in 1.21.
                .add(Attributes.ENTITY_INTERACTION_RANGE, com.Polarice3.Goety.utils.ModAttributeUtil.create("item.goety.scythe.reach", 1.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public DarkScytheItem(){
        this(ModTiers.SPECIAL);
    }

    public static float getInitialDamage() {
        return initialDamage;
    }

    protected int getConfiguredMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        return ConfiguredItemUtil.durability(ItemConfig.SpecialToolsDurability, ModTiers.SPECIAL.getUses());
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.getConfiguredMaxDamage(stack);
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
        if (pAttacker instanceof Player player){
            this.attackMobs(pStack, pTarget, player);
        }
        return true;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(this.getMineBlocks(pLevel, pState, pPos) ? 1 : 2, pEntityLiving, EquipmentSlot.MAINHAND);
        }
        if (this.getMineBlocks(pLevel, pState, pPos)){
            pLevel.playSound((Player) null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.SCYTHE_HIT.get(), pEntityLiving.getSoundSource(), 1.0F, 1.0F);
            for (BlockPos blockPos : BlockFinder.multiBlockBreak(pEntityLiving, pPos, 2, 2, 2)){
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

    public void attackMobs(ItemStack pStack, LivingEntity pTarget, Player pPlayer){
        int enchantment = MobUtil.getItemEnchantmentLevel(pPlayer, pStack, ModEnchantments.SOUL_EATER);
        int soulEater = Mth.clamp(enchantment + 1, 1, 10);
        SEHelper.increaseSouls(pPlayer, ItemConfig.DarkScytheSouls.get() * soulEater);

        float f = (float)pPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
        var damageSource = pPlayer.damageSources().playerAttack(pPlayer);
        float f1 = MobUtil.getEnchantedDamage(pPlayer, pPlayer.getMainHandItem(), pTarget, damageSource, f) - f;
        float f2 = pPlayer.getAttackStrengthScale(0.5F);
        f = f * (0.2F + f2 * f2 * 0.8F);
        f1 = f1 * f2;
        f = f + f1;

        if (f > 0.5F || f1 > 0.5F) {
            float f3 = 1.0F + MobUtil.getSweepingDamageRatio(pPlayer) * f;
            int j = MobUtil.getFireAspect(pPlayer);
            double area = 1.0D;
            if (f2 > 0.9F) {
                area = 2.0D;
            }
            for (LivingEntity livingentity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pTarget.getBoundingBox().inflate(area, 0.25D, area))) {
                if (livingentity != pPlayer && livingentity != pTarget && !pPlayer.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && pPlayer.distanceToSqr(livingentity) < 16.0D && livingentity != pPlayer.getVehicle()) {
                    livingentity.knockback(0.4F, (double) Mth.sin(pPlayer.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(pPlayer.getYRot() * ((float) Math.PI / 180F))));
                    if (livingentity.hurt(damageSource, f3)) {
                        if (j > 0) {
                            livingentity.igniteForSeconds(j * 4);
                        }
                        pStack.hurtAndBreak(1, pPlayer, EquipmentSlot.MAINHAND);
                        if (livingentity instanceof IOwned){
                            if (((IOwned) livingentity).getTrueOwner() != pPlayer){
                                SEHelper.increaseSouls(pPlayer, ItemConfig.DarkScytheSouls.get() * soulEater);
                            }
                        } else {
                            SEHelper.increaseSouls(pPlayer, ItemConfig.DarkScytheSouls.get() * soulEater);
                        }
                        MobUtil.doPostAttackEffects(pPlayer, livingentity, damageSource, pPlayer.getMainHandItem());
                    }
                }
            }
        }

        pPlayer.level().playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.SCYTHE_SWING.get(), pPlayer.getSoundSource(), 1.0F, 1.0F);
        pPlayer.sweepAttack();
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
        // Minecraft 1.21 asks the stack-aware hook for tool checks, so mirror the scythe's legacy hoe/cobweb behavior.
        return this.isCorrectToolForDrops(blockState);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        ResourceLocation enchantmentId = enchantment.unwrapKey().map(ResourceKey::location).orElse(null);
        // Minecraft 1.21 exposes ranged weapon enchantments as main-hand enchantments, so use item tags plus explicit Goety allowances.
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
