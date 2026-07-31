package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.HellChant;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class InfernalTome extends Item {
    public static String CHANT_TIMES = "Chant Times";

    public InfernalTome() {
        super(new Properties()
                .durability(64)
                .fireResistant()
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entityIn, int p_41407_, boolean p_41408_) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (!tag.contains(CHANT_TIMES)) {
                tag.putInt(CHANT_TIMES, 0);
            }
        });
        super.inventoryTick(stack, level, entityIn, p_41407_, p_41408_);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CustomData.update(DataComponents.CUSTOM_DATA, pStack, compound -> compound.putInt(CHANT_TIMES, 0));
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    public static int getChantTimes(ItemStack pStack){
        CompoundTag tag = pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(CHANT_TIMES)){
            return tag.getInt(CHANT_TIMES);
        }
        return 0;
    }

    public static void setChantTimes(ItemStack pStack, int time){
        CustomData.update(DataComponents.CUSTOM_DATA, pStack, tag -> tag.putInt(CHANT_TIMES, time));
    }

    public static void increaseChantTimes(ItemStack pStack){
        setChantTimes(pStack, getChantTimes(pStack) + 1);
    }

    public static boolean isChanting(ItemStack pStack){
        CompoundTag tag = pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(CHANT_TIMES)){
            return tag.getInt(CHANT_TIMES) > 0;
        }
        return false;
    }

    @Override
    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        super.onUseTick(worldIn, livingEntityIn, stack, count);
        if (stack.getItem() instanceof InfernalTome) {
            int CastTime = stack.getUseDuration(livingEntityIn) - count;
            if (CastTime == 1) {
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), ModSounds.HERETIC_CHANT.get(), SoundSource.PLAYERS, 2.0F, 0.5F);
            }
            if (!worldIn.isClientSide) {
                boolean nether = CuriosFinder.hasNetherRobe(livingEntityIn);
                if (!nether){
                    livingEntityIn.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
                }
                if (count % 10 == 0) {
                    HellChant hellChant = ModEntityType.HELL_CHANT.get().create(worldIn);
                    if (hellChant != null) {
                        hellChant.setExtraDamage(MobUtil.getItemEnchantmentLevel(livingEntityIn, stack, ModEnchantments.POTENCY));
                        hellChant.setBurning(MobUtil.getItemEnchantmentLevel(livingEntityIn, stack, ModEnchantments.BURNING));
                        hellChant.chant(livingEntityIn);
                        worldIn.addFreshEntity(hellChant);
                        ItemHelper.hurtAndBreak(stack, 1, livingEntityIn);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level level, LivingEntity livingEntity) {
        Player player = livingEntity instanceof Player ? (Player)livingEntity : null;

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (pStack.getItem() instanceof InfernalTome) {
                player.getCooldowns().addCooldown(this, 100);
                if (getChantTimes(pStack) != 0) {
                    setChantTimes(pStack, 0);
                }
            }
        }
        return pStack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int useTimeRemaining) {
        if (stack.getItem() instanceof InfernalTome) {
            if (livingEntity instanceof Player player){
                player.getCooldowns().addCooldown(this, 100);
                if (getChantTimes(stack) != 0) {
                    setChantTimes(stack, 0);
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack p_41454_, LivingEntity entity) {
        return 60;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    public InteractionResultHolder<ItemStack> use(Level p_40672_, Player p_40673_, InteractionHand p_40674_) {
        ItemStack itemstack = p_40673_.getItemInHand(p_40674_);
        p_40673_.startUsingItem(p_40674_);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 5;
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return pStack.getCount() == 1;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(ModEnchantments.POTENCY)
                || enchantment.is(ModEnchantments.BURNING)
                || super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() == Items.PAPER || super.isValidRepairItem(pToRepair, pRepair);
    }
}
