package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreasurePouchItem extends ItemBase {

    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (worldIn instanceof ServerLevel serverLevel) {
            LootTable loottable = worldIn.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ModLootTables.TREASURE_POUCH));
            List<ItemStack> list = loottable.getRandomItems((new LootParams.Builder(serverLevel)).withParameter(LootContextParams.THIS_ENTITY, playerIn).withParameter(LootContextParams.ORIGIN, playerIn.position()).create(LootContextParamSets.GIFT));
            for (ItemStack itemstack1 : list) {
                if (!playerIn.addItem(itemstack1)) {
                    playerIn.drop(itemstack1, false);
                }
            }
            // Only the server should consume the pouch after generating loot; client-side mutation can desync the held stack.
            playerIn.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.0F);
            itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
    }
}
