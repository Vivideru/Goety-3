package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class RavagerArmorItem extends Item {
   private final int protection;
   private final ResourceLocation texture;

   public RavagerArmorItem(int protection, String material) {
      this(protection, Goety.location("textures/entity/servants/ravager/armor/ravager_armor_" + material + ".png"), new Properties().stacksTo(1));
   }

   public RavagerArmorItem(int protection, String material, Properties properties) {
      this(protection, Goety.location("textures/entity/servants/ravager/armor/ravager_armor_" + material + ".png"), properties);
   }

   public RavagerArmorItem(int protection, ResourceLocation texture, Properties properties) {
      super(properties);
      this.protection = protection;
      this.texture = texture;
   }

   public ResourceLocation getTexture() {
      return texture;
   }

   public int getProtection() {
      return this.protection;
   }

   @Override
   public boolean isEnchantable(ItemStack stack) {
      // These mount armor items are not damageable, so the 1.21 default item check would reject them before their supported enchantments are queried.
      return stack.getCount() == 1;
   }

   @Override
   public int getEnchantmentValue(ItemStack stack) {
      return 1;
   }

   @Override
   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return super.supportsEnchantment(stack, enchantment)
              || enchantment.value().matchingSlot(EquipmentSlot.HEAD)
              || enchantment.value().matchingSlot(EquipmentSlot.CHEST)
              || enchantment.value().matchingSlot(EquipmentSlot.LEGS)
              || enchantment.value().matchingSlot(EquipmentSlot.FEET);
   }
}
