package com.Polarice3.Goety.common.items.revive;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Objects;

public class ReviveServantItem extends Item {

    public ReviveServantItem(Properties p_41383_) {
        super(p_41383_);
    }

    protected static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    protected static void setTag(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    protected static void updateTag(ItemStack stack, java.util.function.Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    public static void setSummon(Entity entity, ItemStack stack) {
        entity.stopRiding();
        entity.ejectPassengers();

        CompoundTag entityTag = new CompoundTag();
        ResourceLocation typesKey = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());

        if (typesKey != null) {
            entityTag.putString("entity", typesKey.toString());
            if (entity.hasCustomName()) {
                entityTag.putString("name", Objects.requireNonNull(entity.getCustomName()).getString());
            }
            entity.save(entityTag);
            CompoundTag itemNBT = tag(stack);
            // ItemStack root NBT was removed in 1.21; keep revive entity data in CUSTOM_DATA.
            itemNBT.put("entity", entityTag);
            setTag(stack, itemNBT);
        }
    }

    public static Entity getSummon(ItemStack stack, Level level) {
        CompoundTag itemTag = tag(stack);

        if (!itemTag.isEmpty()) {
            CompoundTag entityTag = itemTag.getCompound("entity");
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityTag.getString("entity")));
            if (entityType != null) {
                Entity entity = entityType.create(level);
                if (level instanceof ServerLevel && entity != null) {
                    entity.load(entityTag);
                }

                return entity;
            }
        }

        return null;
    }

    public static void setOwnerName(@Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            updateTag(stack, tag -> tag.putString("owner_name", entity.getDisplayName().getString()));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Level level = context.level();
        if (level != null && getSummon(stack, level) != null)  {
            Entity entity = getSummon(stack, level);

            if (entity == null) {
                return;
            }

            CompoundTag itemTag = tag(stack);
            if (!itemTag.isEmpty()) {
                if (itemTag.contains("owner_name")) {
                    tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + itemTag.getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                }
            }

            if (entity.getCustomName() != null) {
                tooltip.add(Component.translatable("tooltip.goety.customName").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("").append(entity.getCustomName()).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
            }
        }
    }
}
