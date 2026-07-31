package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.client.inventory.container.FocusPackContainer;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

public class FocusPack extends FocusBag {

    @NotNull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            SimpleMenuProvider provider = new SimpleMenuProvider(
                    (id, inventory, player) -> new FocusPackContainer(id, inventory, FocusBagItemHandler.get(itemstack), itemstack), getName(itemstack));
            ((ServerPlayer) playerIn).openMenu(provider);
        }
        return InteractionResultHolder.success(itemstack);
    }

}
