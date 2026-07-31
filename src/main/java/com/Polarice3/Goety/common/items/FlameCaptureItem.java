package com.Polarice3.Goety.common.items;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Code based off @stal111
 */
public class FlameCaptureItem extends Item {

    public FlameCaptureItem() {
        super(new Properties().stacksTo(1));
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (this.getEntity(stack, level) != null) {
            if (level.getBlockState(pos).getBlock() == ModBlocks.CURSED_CAGE_BLOCK.get()){
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof CursedCageBlockEntity cursedCageBlock){
                    if (!cursedCageBlock.getItem().isEmpty()){
                        return InteractionResult.PASS;
                    } else {
                        if (!level.isClientSide) {
                            level.setBlockAndUpdate(pos, Blocks.SPAWNER.defaultBlockState());
                            BlockEntity blockentity = level.getBlockEntity(pos);
                            if (blockentity instanceof SpawnerBlockEntity) {
                                ((SpawnerBlockEntity) blockentity).getSpawner().setEntityId(this.getEntity(stack, level).getType(), level, level.random, pos);
                            }
                        }
                        this.clearEntity(stack);
                        if (player != null) {
                            player.playSound(ModSounds.FLAME_CAPTURE_RELEASE.get());
                        }
                        stack.shrink(1);

                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            }
        } else {
            if (ItemConfig.FireSpawnCage.get()) {
                if (level.getBlockState(pos).getBlock() == Blocks.SPAWNER) {
                        if (!level.isClientSide()) {
                            BlockEntity blockentity = level.getBlockEntity(pos);
                            if (blockentity instanceof SpawnerBlockEntity) {
                            Entity entity = ((SpawnerBlockEntity) blockentity).getSpawner().getOrCreateDisplayEntity(level, blockentity.getBlockPos());
                            if (entity != null) {
                                this.setEntity(entity, stack);
                                level.destroyBlock(pos, false);
                            }
                        }
                    }
                    if (player != null) {
                        player.playSound(ModSounds.FLAME_CAPTURE_CATCH.get());
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (ItemConfig.FireSpawnCage.get()) {
            Level level = context.level();
            if (level != null && this.getEntity(stack, level) != null) {
                Entity entity = this.getEntity(stack, level);

                if (entity == null) {
                    return;
                }

                MutableComponent textComponent = Component.translatable("tooltip.goety.entity")
                        .append(": ")
                        .append(Component.literal(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())).toString()))
                        .withStyle(ChatFormatting.GREEN);

                tooltip.add(textComponent);
            }
        } else {
            MutableComponent textComponent = Component.translatable("tooltip.goety.disabled")
                    .withStyle(ChatFormatting.DARK_RED);

            tooltip.add(textComponent);
        }
    }

    public static boolean hasEntity(ItemStack itemStack){
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("mob");
    }

    private void setEntity(Entity entity, ItemStack stack) {
        ResourceLocation name = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());

        if (name == null) {
            return;
        }

        // ItemStack root NBT was removed in 1.21; keep the captured entity id in CUSTOM_DATA.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, entityTag -> entityTag.putString("mob", name.toString()));
    }

    private Entity getEntity(ItemStack stack, Level level) {
        CompoundTag itemTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        if (!itemTag.contains("mob")) {
            return null;
        }

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(itemTag.getString("mob")));

        if (entityType == null) {
            return null;
        }

        return entityType.create(level);
    }

    private void clearEntity(ItemStack stack) {
        stack.remove(DataComponents.CUSTOM_DATA);
    }

}
