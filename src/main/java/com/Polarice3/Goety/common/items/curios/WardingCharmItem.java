package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.spells.IronHideSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WardingCharmItem extends SingleStackItem{
    private static final String SOULUSE = "Soul Use";

    private static int getSoulUse(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(SOULUSE);
    }

    private static void setSoulUse(ItemStack stack, int soulUse) {
        // ItemStack root NBT was removed in 1.21; mutate a CUSTOM_DATA copy and write it back.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, compound -> compound.putInt(SOULUSE, soulUse));
    }

    private static int enchantmentLevel(Level level, ItemStack stack, ResourceKey<Enchantment> key) {
        Optional<Holder.Reference<Enchantment>> holder = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(key);
        return holder.map(enchantment -> stack.getItem().getEnchantmentLevel(stack, enchantment)).orElse(0);
    }

    public boolean SoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicRobeItem);
    }

    public boolean SoulCostUp(LivingEntity entityLiving){
        return entityLiving.hasEffect(GoetyEffects.SUMMON_DOWN);
    }

    public int SoulCalculation(LivingEntity entityLiving){
        if (SoulCostUp(entityLiving)){
            int amp = Objects.requireNonNull(entityLiving.getEffect(GoetyEffects.SUMMON_DOWN)).getAmplifier() + 2;
            return new IronHideSpell().defaultSoulCost() * amp;
        } else if (SoulDiscount(entityLiving)){
            return new IronHideSpell().defaultSoulCost() / 2;
        } else {
            return new IronHideSpell().defaultSoulCost();
        }
    }

    public int SoulUse(LivingEntity entityLiving, ItemStack stack){
        if (stack.isEnchanted()){
            return (int) (SoulCalculation(entityLiving) * 2 * SEHelper.soulDiscount(entityLiving));
        } else {
            return (int) (SoulCalculation(entityLiving) * SEHelper.soulDiscount(entityLiving));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            setSoulUse(stack, SoulUse(livingEntity, stack));
            if (!worldIn.isClientSide) {
                if (livingEntity instanceof Player player) {
                    if (!player.hasEffect(GoetyEffects.SOUL_ARMOR)) {
                        if (SEHelper.getSoulsAmount(player, SoulUse(player, stack))) {
                            List<Mob> mobs = worldIn.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(64, 16, 64));
                            Mob hostile = mobs.stream().filter(mob -> mob.getTarget() == player).findFirst().orElse(null);
                            if (hostile != null || player.hurtTime > 0) {
                                SEHelper.decreaseSouls(player, SoulUse(player, stack));
                                SEHelper.sendSEUpdatePacket(player);
                                int enchantment = 0;
                                int duration = 1;
                                if (stack.isEnchanted()) {
                                    enchantment = enchantmentLevel(worldIn, stack, ModEnchantments.POTENCY);
                                    duration += enchantmentLevel(worldIn, stack, ModEnchantments.DURATION);
                                }
                                player.addEffect(new MobEffectInstance(GoetyEffects.SOUL_ARMOR, MathHelper.minutesToTicks(duration), enchantment, false, false, true));
                                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.IRON_HIDE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isEnchantable(ItemStack pStack) {
        return pStack.getCount() == 1;
    }

    public int getEnchantmentValue() {
        return 1;
    }

    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        // 1.21 passes enchantments as registry holders, so compare by resource key instead of raw instances.
        return enchantment.is(ModEnchantments.POTENCY)
                || enchantment.is(ModEnchantments.DURATION);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains(SOULUSE)) {
            int SoulUse = getSoulUse(stack);
            tooltip.add(Component.translatable("info.goety.wand.cost", SoulUse));
        }
    }
}
