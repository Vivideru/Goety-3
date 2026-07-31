package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.world.item.component.CustomData;

public class OminousIdolBlockItem extends BlockItemBase{
    public static String ILLAGER_LIST = "illagerList";

    public OminousIdolBlockItem() {
        super(ModBlocks.OMINOUS_IDOL.get());
    }

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void updateTag(ItemStack stack, Consumer<CompoundTag> updater) {
        // ItemStack root NBT was removed in 1.21; mutate a CUSTOM_DATA copy and write it back.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            ListTag listTag = getIllagerList(stack, worldIn);
            if (listTag != null){
                if (listTag.isEmpty()){
                    updateTag(stack, compound -> compound.remove(ILLAGER_LIST));
                }
            }
            if (!getIllagers(stack, worldIn).isEmpty()) {
                for (RaiderServant illagerServant : getIllagers(stack, worldIn)) {
                    if (illagerServant == null || illagerServant.isDeadOrDying()) {
                        removeIllager(stack, illagerServant, worldIn);
                    }
                }
            } else {
                if (tag(stack).contains(ILLAGER_LIST)){
                    updateTag(stack, compound -> compound.remove(ILLAGER_LIST));
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return tag(p_41453_).contains(ILLAGER_LIST);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof OminousIdolBlockItem){
                if (tag(itemstack).contains(ILLAGER_LIST)){
                    updateTag(itemstack, compound -> compound.remove(ILLAGER_LIST));
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static ListTag getIllagerList(ItemStack stack, Level level){
        if (!level.isClientSide) {
            CompoundTag compound = tag(stack);
            if (compound.contains(ILLAGER_LIST)) {
                return compound.getList(ILLAGER_LIST, 8);
            }
        }
        return null;
    }

    public static void removeIllager(ItemStack stack, RaiderServant illagerServant, Level level){
        if (!level.isClientSide) {
            CompoundTag compound = tag(stack);
            List<String> list = new ArrayList<>();
            if (compound.contains(ILLAGER_LIST)) {
                for (int i = 0; i < compound.getList(ILLAGER_LIST, 8).size(); ++i) {
                    list.add(compound.getList(ILLAGER_LIST, 8).getString(i));
                }
            }

            if (list.contains(illagerServant.getStringUUID())) {
                ListTag nbttaglist = compound.contains(ILLAGER_LIST) ? compound.getList(ILLAGER_LIST, 8).copy() : new ListTag();

                nbttaglist.remove(StringTag.valueOf(illagerServant.getStringUUID()));
                updateTag(stack, tag -> {
                    tag.put(ILLAGER_LIST, nbttaglist);
                });
            }
        }
    }

    public static List<RaiderServant> getIllagers(ItemStack stack, Level level){
        List<RaiderServant> illagerServants = new ArrayList<>();
        if (!level.isClientSide && tag(stack).contains(ILLAGER_LIST)){
            ListTag list = tag(stack).getList(ILLAGER_LIST, 8);
            for(int i = 0; i < list.size(); ++i) {
                Entity entity = EntityFinder.getEntityByUuiD(UUID.fromString(list.getString(i)));
                if (entity instanceof RaiderServant servant){
                    illagerServants.add(servant);
                }
            }
        }
        return illagerServants;
    }

    public static void setIllager(ItemStack stack, Player player, RaiderServant illagerServant){
        if (!player.level().isClientSide) {
            CompoundTag compound = tag(stack);
            List<String> list = new ArrayList<>();
            if (compound.contains(ILLAGER_LIST)) {
                for (int i = 0; i < compound.getList(ILLAGER_LIST, 8).size(); ++i) {
                    list.add(compound.getList(ILLAGER_LIST, 8).getString(i));
                }
            }

            if (!list.contains(illagerServant.getStringUUID())) {
                ListTag nbttaglist = compound.contains(ILLAGER_LIST) ? compound.getList(ILLAGER_LIST, 8).copy() : new ListTag();

                nbttaglist.add(StringTag.valueOf(illagerServant.getStringUUID()));
                updateTag(stack, tag -> {
                    tag.put(ILLAGER_LIST, nbttaglist);
                });
            }
        }
    }

    public static void setUUIDs(ItemStack stack, UUID uuid){
        CompoundTag compound = tag(stack);
        List<String> list = new ArrayList<>();
        if (compound.contains(ILLAGER_LIST)) {
            for (int i = 0; i < compound.getList(ILLAGER_LIST, 8).size(); ++i) {
                list.add(compound.getList(ILLAGER_LIST, 8).getString(i));
            }
        }

        if (!list.contains(uuid.toString())) {
            ListTag nbttaglist = compound.contains(ILLAGER_LIST) ? compound.getList(ILLAGER_LIST, 8).copy() : new ListTag();

            nbttaglist.add(StringTag.valueOf(uuid.toString()));
            updateTag(stack, tag -> {
                tag.put(ILLAGER_LIST, nbttaglist);
            });
        }
    }

    public static void clearUUIDs(ItemStack stack) {
        if (tag(stack).contains(ILLAGER_LIST)) {
            updateTag(stack, compound -> compound.remove(ILLAGER_LIST));
        }
    }
}
