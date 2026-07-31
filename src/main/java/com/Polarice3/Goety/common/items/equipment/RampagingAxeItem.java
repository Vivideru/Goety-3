package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RampagingAxeItem extends AxeItem {
    public RampagingAxeItem() {
        // In 1.21 the custom properties must declare durability explicitly so the enchanting table treats the axe as enchantable.
        super(ModTiers.SPECIAL, (new Properties()).rarity(Rarity.UNCOMMON).durability(ModTiers.SPECIAL.getUses()).attributes(DiggerItem.createAttributes(ModTiers.SPECIAL, 5.0F, -3.0F)));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        return ConfiguredItemUtil.durability(ItemConfig.SpecialToolsDurability, ModTiers.SPECIAL.getUses());
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.is(BlockTags.MINEABLE_WITH_AXE)){
            MobEffectInstance effectinstance1 = pEntityLiving.getEffect(GoetyEffects.RAMPAGE);
            if (!pEntityLiving.hasEffect(GoetyEffects.RAMPAGE)){
                pEntityLiving.addEffect(new MobEffectInstance(GoetyEffects.RAMPAGE, MathHelper.secondsToTicks(10)));
            } else if (effectinstance1 != null){
                if (effectinstance1.getAmplifier() < 4 && pLevel.random.nextFloat() <= 0.25F) {
                    EffectsUtil.amplifyEffect(pEntityLiving, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10));
                } else {
                    EffectsUtil.resetDuration(pEntityLiving, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10));
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        float speed = super.getDestroySpeed(stack, blockState);
        // NeoForge 1.21 uses the TOOL component for mining speed; preserve the tier speed if it falls back to hand speed.
        return speed <= 1.0F && blockState.is(BlockTags.MINEABLE_WITH_AXE) ? ModTiers.SPECIAL.getSpeed() : speed;
    }
}
