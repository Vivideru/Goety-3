package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.items.magic.ISoulContainer;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public class SoulItem extends Item implements ISoulContainer {
    public SoulItem() { super(new Properties().stacksTo(1)); }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        ISoulContainer.setSoulsAmount(stack, 0);
        super.onCraftedBy(stack, level, player);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int souls = ISoulContainer.currentSouls(stack);
        if (souls <= 0 || !SEHelper.getSoulsContainer(player)) return InteractionResultHolder.pass(stack);
        if (level.isClientSide) {
            level.playSound(player, player.blockPosition(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 0.5F, 1.0F);
        } else {
            SEHelper.increaseSouls(player, souls);
            ISoulContainer.setSoulsAmount(stack, 0);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("info.goety.item.souls", ISoulContainer.currentSouls(stack)));
    }
}
