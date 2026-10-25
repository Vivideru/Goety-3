package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.function.Predicate;

/** The spell of an extra servant's focus: it summons the servant, with the stats of the focus in the config. */
public class ExtraServantSpell extends SummonSpell {
    private final ExtraServantDefinition definition;
    private final ExtraServantFocusDefinition focus;

    public ExtraServantSpell(ExtraServantDefinition definition, ExtraServantFocusDefinition focus) {
        this.definition = definition;
        this.focus = focus;
    }

    @Override
    public int defaultSoulCost() {
        return this.focus.soulCost();
    }

    @Override
    public int defaultCastDuration() {
        return this.focus.castDuration();
    }

    @Override
    public int defaultSpellCooldown() {
        return this.focus.cooldown();
    }

    @Override
    public int SummonDownDuration() {
        return this.focus.summonDown();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public SpellType getSpellType() {
        return this.focus.spellType();
    }

    @Override
    public List<ResourceKey<Enchantment>> acceptedEnchantments() {
        return List.of(ModEnchantments.POTENCY, ModEnchantments.DURATION);
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        EntityType<?> type = ExtraServants.type(this.definition);
        return livingEntity -> livingEntity.getType() == type;
    }

    @Override
    public int summonLimit() {
        return this.focus.summonLimit();
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        EntityType<?> type = ExtraServants.type(this.definition);
        if (this.isShifting(caster) || type == null) {
            return;
        }
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += this.durationEnchantmentLevel(caster);
        }
        int count = this.focus.staffCounts().getOrDefault(BuiltInRegistries.ITEM.getKey(staff.getItem()), this.focus.summonCount());
        // A staff that calls several at once still stops at the limit.
        count = Math.min(count, this.summonLimit() - this.summoned(worldIn, caster));
        for (int i = 0; i < count; ++i) {
            Entity entity = type.create(worldIn);
            if (!(entity instanceof Mob summoned) || !(entity instanceof IOwned owned)) {
                return;
            }
            BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summoned, worldIn);
            owned.setTrueOwner(MobUtil.getSummonOwner(caster));
            summoned.moveTo(blockPos, caster.getYRot(), 0.0F);
            MobUtil.moveDownToGround(summoned);
            if (this.focus.limitedLife()) {
                owned.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
            }
            summoned.setPersistenceRequired();
            summoned.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null);
            this.buffSummon(caster, summoned, potency);
            this.SummonSap(caster, summoned);
            this.setTarget(caster, summoned);
            if (worldIn.addFreshEntity(summoned)) {
                this.uponSummon(worldIn, caster, staff, summoned);
            }
            this.summonAdvancement(caster, summoned);
        }
        if (this.focus.summonDown() > 0) {
            this.SummonDown(caster);
        }
        this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
    }

    /** The caster's living servants of this kind, counted as the limit check does. */
    private int summoned(ServerLevel worldIn, LivingEntity caster) {
        int count = 0;
        Predicate<LivingEntity> predicate = this.summonPredicate();
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof LivingEntity livingEntity && entity instanceof IOwned owned && predicate.test(livingEntity)
                    && owned.getTrueOwner() == caster && livingEntity.isAlive()) {
                ++count;
            }
        }
        return count;
    }
}
