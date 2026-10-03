package com.Vivideru.Goety.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import com.Vivideru.Goety.common.entities.ally.BreezeServant;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BreezeSpell extends SummonSpell {

    public int defaultSoulCost() {
        return SpellConfig.BreezeCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.BreezeDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.BreezeSummonDown.get();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BreezeCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public List<ResourceKey<Enchantment>> acceptedEnchantments() {
        List<ResourceKey<Enchantment>> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY);
        list.add(ModEnchantments.DURATION);
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof BreezeServant;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.BreezeLimit.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += this.durationEnchantmentLevel(caster);
        }
        if (!isShifting(caster)) {
            int count = this.rightStaff(staff) ? 2 : 1;
            for (int i = 0; i < count; ++i) {
                BreezeServant breeze = new BreezeServant(VivideruEntityTypes.BREEZE_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), breeze, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                breeze.setTrueOwner(MobUtil.getSummonOwner(caster));
                breeze.moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(breeze);
                breeze.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                breeze.setPersistenceRequired();
                breeze.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                this.buffSummon(caster, breeze, potency);
                this.SummonSap(caster, breeze);
                this.setTarget(caster, breeze);
                if (worldIn.addFreshEntity(breeze)) {
                    this.uponSummon(worldIn, caster, staff, breeze);
                }
                this.summonAdvancement(caster, breeze);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
