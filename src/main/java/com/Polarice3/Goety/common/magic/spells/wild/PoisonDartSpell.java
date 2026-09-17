package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Vivideru.Goety.common.enchantments.VivideruEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.PoisonQuill;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;

public class PoisonDartSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setVelocity(1.6F);
    }

    public int defaultSoulCost() {
        return SpellConfig.PoisonDartCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.PoisonDartDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.PoisonDartCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<ResourceKey<Enchantment>> acceptedEnchantments() {
        List<ResourceKey<Enchantment>> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY);
        list.add(ModEnchantments.DURATION);
        list.add(ModEnchantments.VELOCITY);
        list.add(VivideruEnchantments.HOMING);
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float velocity = spellStat.getVelocity();
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY, caster) / 3.0F;
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION, caster);
        }
        PoisonQuill poisonQuill = new PoisonQuill(worldIn, caster);
        poisonQuill.setSpear(rightStaff(staff), potency + 1);
        poisonQuill.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
        poisonQuill.setOwner(caster);
        poisonQuill.setExtraDamage(potency);
        poisonQuill.setDuration(duration);
        if (caster.isUnderWater()){
            poisonQuill.setAqua(true);
        }
        worldIn.addFreshEntity(poisonQuill);
        this.playSound(worldIn, caster, ModSounds.POISON_QUILL_VINE_SHOOT.get(), 1.0F, this.projPitch(worldIn.getRandom()));
    }
}
