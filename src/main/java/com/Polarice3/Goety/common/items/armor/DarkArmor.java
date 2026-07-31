package com.Polarice3.Goety.common.items.armor;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.api.items.armor.ISoulDiscount;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.DarkArmorModel;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.CultistServant;
import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonPillagerServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVindicatorServant;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector3f;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class DarkArmor extends ArmorItem implements ISoulRepair, ISoulDiscount, IPersist {
    private final ArmorItem.Type type;

    public DarkArmor(ArmorItem.Type p_40387_) {
        super(ModArmorMaterials.DARK, p_40387_, ModArmorMaterials.durability(ModArmorMaterials.DARK, p_40387_, ModItems.baseProperties()));
        this.type = p_40387_;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ModArmorMaterials.configuredDurability(this.type, ItemConfig.DarkArmorDurability, 15);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.isDamaged(stack);
    }

    public int getBarColor(ItemStack stack) {
        if (this.isBroken(stack)) {
            return 0x800000;
        }
        return super.getBarColor(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (this.isBroken(stack)) {
            return 13;
        }
        return super.getBarWidth(stack);
    }

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<net.minecraft.world.item.Item> onBroken) {
        if (ItemConfig.DarkArmorPersist.get()) {
            if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
                if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                    stack.setDamageValue(stack.getMaxDamage() - 1);
                    onBroken.accept(this);
                }
                return 0;
            }
        }
        return amount;
    }

    @Override
    public boolean isBroken(ItemStack stack) {
        return IPersist.super.isBroken(stack) && ItemConfig.DarkArmorPersist.get();
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (this.isNotBroken(stack) || !ItemConfig.DarkArmorPersist.get()) {
            // Armor attributes are supplied by item components in 1.21; this stack hook intentionally has no dynamic modifiers.
            return ImmutableMultimap.of();
        } else {
            return ImmutableMultimap.of();
        }
    }

    @Override
    public net.minecraft.resources.ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, net.minecraft.world.item.ArmorMaterial.Layer layer, boolean innerModel) {
        if (slot == EquipmentSlot.LEGS) {
            return Goety.location("textures/models/armor/dark_armor_layer.png");
        } else {
            return Goety.location("textures/models/armor/dark_armor.png");
        }
    }

    public int getSoulDiscount(EquipmentSlot equipmentSlot, ItemStack itemStack){
        if (this.isNotBroken(itemStack)) {
            return 5;
        } else {
            return 0;
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
           public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
               EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
               ModelPart root = modelSet.bakeLayer(equipmentSlot == EquipmentSlot.LEGS ? ModModelLayer.DARK_ARMOR_INNER : ModModelLayer.DARK_ARMOR_OUTER);
               DarkArmorModel model = new DarkArmorModel(root).animate(livingEntity);
               model.hat.visible = equipmentSlot == EquipmentSlot.HEAD;
               model.body.visible = equipmentSlot == EquipmentSlot.CHEST;
               model.rightArm.visible = equipmentSlot == EquipmentSlot.CHEST;
               model.leftArm.visible = equipmentSlot == EquipmentSlot.CHEST;
               model.bottom.visible = equipmentSlot == EquipmentSlot.LEGS;
               model.rightLeg.visible = equipmentSlot == EquipmentSlot.FEET;
               model.leftLeg.visible = equipmentSlot == EquipmentSlot.FEET;

               if (livingEntity instanceof AbstractClientPlayer player){
                   if (player.isModelPartShown(PlayerModelPart.CAPE) && player.getSkin().capeTexture() != null){
                       model.cape.visible = false;
                   }
               }

               if (livingEntity instanceof AbstractVillager
                       || livingEntity instanceof AbstractIllager
                       || livingEntity instanceof AbstractIllagerServant
                       || livingEntity instanceof AbstractBoundIllager
                       || livingEntity instanceof ZombieVillager
                       || livingEntity instanceof ZombieVillagerServant
                       || livingEntity instanceof ZombieVindicatorServant
                       || livingEntity instanceof SkeletonPillagerServant
                       || livingEntity instanceof Witch
                       || livingEntity instanceof Cultist
                       || livingEntity instanceof CultistServant) {
                   model.villager = true;
               }

               model.young = original.young;
               model.crouching = original.crouching;
               model.riding = original.riding;
               model.rightArmPose = original.rightArmPose;
               model.leftArmPose = original.leftArmPose;

               return model;
           }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        int discount = this.getSoulDiscount(this.getEquipmentSlot(stack), stack);
        if (discount > 0) {
            tooltip.add(this.soulDiscountTooltip(stack));
        }
        if (ItemConfig.DarkArmorPersist.get() && this.isBroken(stack)) {
            tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
