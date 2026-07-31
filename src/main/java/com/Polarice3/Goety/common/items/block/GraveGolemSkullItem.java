package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

public class GraveGolemSkullItem extends StandingAndWallBlockItem {

    public GraveGolemSkullItem(Properties p_43250_) {
        super(ModBlocks.GRAVE_GOLEM_SKULL_BLOCK.get(), ModBlocks.WALL_GRAVE_GOLEM_SKULL_BLOCK.get(), p_43250_, Direction.DOWN);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return getOwnerID(p_41453_) != null;
    }

    public static void setOwner(@Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            SkullItemData.update(stack, entityTag -> {
                entityTag.putUUID("owner", entity.getUUID());
                entityTag.putString("owner_name", entity.getDisplayName().getString());
            });
        }
    }

    public static void setCustomName(String string, ItemStack stack){
        SkullItemData.update(stack, entityTag -> entityTag.putString("mod_custom_name", string));
    }

    public static String getCustomName(ItemStack stack){
        var entityTag = SkullItemData.tag(stack);
        if (entityTag.contains("mod_custom_name")) {
            return entityTag.getString("mod_custom_name");
        }
        return null;
    }

    @Nullable
    public static UUID getOwnerID(ItemStack stack){
        var entityTag = SkullItemData.tag(stack);
        if (entityTag.contains("owner")) {
            return entityTag.getUUID("owner");
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        var entityTag = SkullItemData.tag(stack);
        if (entityTag.contains("owner_name")) {
            tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + entityTag.getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
        }
        if (entityTag.contains("mod_custom_name")){
            tooltip.add(Component.translatable("tooltip.goety.customName").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + entityTag.getString("mod_custom_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
