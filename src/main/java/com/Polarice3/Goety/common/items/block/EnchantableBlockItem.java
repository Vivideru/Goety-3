package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ThroneBlock;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

public class EnchantableBlockItem extends BlockItemBase {

    public EnchantableBlockItem(Block blockIn) {
        super(blockIn);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        // 1.21 passes enchantments as registry holders, so compare by resource key instead of raw instances.
        if (stack.getItem() == ModBlocks.SCULK_DEVOURER.get().asItem()){
            return stack.getCount() == 1
                    && (enchantment.is(ModEnchantments.SOUL_EATER)
                    || enchantment.is(ModEnchantments.RADIUS));
        }
        if (stack.getItem() == ModBlocks.SCULK_CONVERTER.get().asItem()){
            return stack.getCount() == 1 && enchantment.is(ModEnchantments.POTENCY);
        }
        if (stack.getItem() == ModBlocks.SCULK_GROWER.get().asItem()){
            if (MainConfig.SculkGrowerPotency.get()){
                return stack.getCount() == 1 && (enchantment.is(ModEnchantments.POTENCY) || enchantment.is(ModEnchantments.RADIUS));
            } else {
                return stack.getCount() == 1 && enchantment.is(ModEnchantments.RADIUS);
            }
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ThroneBlock) {
            return stack.getCount() == 1 && enchantment.is(ModEnchantments.ROYALTY);
        }
        // Unknown enchantable blocks should not accept every data-driven enchantment just because the stack is singular.
        return super.supportsEnchantment(stack, enchantment);
    }

    public int getMaxStackSize(ItemStack itemStack){
        return itemStack.isEnchanted() ? 1 : super.getMaxStackSize(itemStack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 25;
    }

    public static void setOwner(@Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            // 1.21 stores item custom NBT in the CUSTOM_DATA component instead of the removed root ItemStack tag.
            CustomData.update(DataComponents.CUSTOM_DATA, stack, entityTag -> {
                entityTag.putUUID("owner", entity.getUUID());
                entityTag.putString("owner_name", entity.getDisplayName().getString());
            });
        }
    }

    @Nullable
    public static UUID getOwnerID(ItemStack stack){
        var entityTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (entityTag.contains("owner")) {
            return entityTag.getUUID("owner");
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        var entityTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (entityTag.contains("owner_name")) {
            tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + entityTag.getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
