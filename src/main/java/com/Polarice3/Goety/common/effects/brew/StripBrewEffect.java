package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import org.jetbrains.annotations.Nullable;

public class StripBrewEffect extends BrewEffect {
    public StripBrewEffect(int soulCost, int capacityExtra) {
        super("strip", soulCost, capacityExtra, MobEffectCategory.HARMFUL, 0x515151);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        EquipmentSlot[] armorSlots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
        for (int i = 0; i < pAmplifier + 1; ++i){
            EquipmentSlot equipmentSlot = armorSlots[pTarget.getRandom().nextInt(armorSlots.length)];
            ItemStack armor = pTarget.getItemBySlot(equipmentSlot);
            if (pTarget instanceof Mob mob){
                if (MobUtil.getEquipmentDropChance(mob, equipmentSlot) <= 0.0F){
                    armor = ItemStack.EMPTY;
                }
            }
            if (EnchantmentHelper.getItemEnchantmentLevel(pTarget.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE), armor) > 0){
                armor = ItemStack.EMPTY;
            }
            if (!armor.isEmpty()){
                ItemEntity itemEntity = new ItemEntity(pTarget.level(),
                        pTarget.getX() + pTarget.getRandom().nextDouble(),
                        pTarget.getY() + pTarget.getRandom().nextDouble(),
                        pTarget.getZ() + pTarget.getRandom().nextDouble(), armor);
                itemEntity.setPickUpDelay(80);
                ItemStack itemStack = itemEntity.getItem();
                Item item = itemStack.getItem();
                if (pTarget.level().addFreshEntity(itemEntity) || item.hasCustomEntity(itemStack)){
                    pTarget.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                    if (!pTarget.level().isClientSide) {
                        ModNetwork.sendToALL(new SPlayWorldSoundPacket(pTarget.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), 10.0F, 1.5F));
                    }
                    pTarget.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 10.0F, 1.5F);
                }
            }
        }
    }
}
