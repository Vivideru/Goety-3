package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.api.items.curios.IActivatable;
import com.Polarice3.Goety.client.inventory.container.EternalCauldronContainer;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.BrewEffects;
import com.Polarice3.Goety.common.items.handler.EternalCauldronItemHandler;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModPotionUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import org.jetbrains.annotations.NotNull;

public class EternalCauldronItem extends SingleStackItem implements IActivatable {

    @Override
    public void activate(Level level, Player player, ItemStack itemStack) {
        if (itemStack.is(this) && !level.isClientSide) {
            if (player.isShiftKeyDown()) {
                this.openContainer((ServerPlayer) player, itemStack);
            } else {
                this.drinkStoredBottle(player, itemStack);
            }
        }
    }

    @NotNull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            this.openContainer((ServerPlayer) playerIn, itemstack);
        }
        return InteractionResultHolder.success(itemstack);
    }

    private void openContainer(ServerPlayer player, ItemStack stack) {
        SimpleMenuProvider provider = new SimpleMenuProvider(
                (id, inventory, playerIn) -> new EternalCauldronContainer(id, inventory, EternalCauldronItemHandler.get(stack), stack), getName(stack));
        player.openMenu(provider);
    }

    private void drinkStoredBottle(Player player, ItemStack itemStack) {
        if (getBottle(itemStack).isEmpty() || SEHelper.isOnCooldown(player, itemStack)) {
            return;
        }
        ItemStack bottle = getBottle(itemStack);
        int duration = MathHelper.secondsToTicks(45);
        int add = 1;
        for(MobEffectInstance mobeffectinstance : ModPotionUtil.getMobEffects(bottle)) {
            BrewEffect brewEffect = BrewEffects.INSTANCE.getBrewEffect(mobeffectinstance.getDescriptionId());
            int amp = 1 + mobeffectinstance.getAmplifier();
            if (brewEffect != null) {
                add += brewEffect.getCapacityExtra() * amp;
            } else if (amp > 1) {
                add += amp - 1;
            }
            if (mobeffectinstance.getEffect().value().isInstantenous()) {
                mobeffectinstance.getEffect().value().applyInstantenousEffect(player, player, player, mobeffectinstance.getAmplifier(), 1.0D);
            } else {
                player.addEffect(new MobEffectInstance(mobeffectinstance));
            }
        }
        for (BrewEffectInstance brewEffectInstance : BrewUtils.getBrewEffects(bottle)){
            brewEffectInstance.getEffect().drinkBlockEffect(player, player, player, brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(bottle));
        }
        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F));
        SEHelper.addCooldown(player, this, duration * add);
    }

    public static ItemStack getBottle(ItemStack itemstack) {
        EternalCauldronItemHandler handler = EternalCauldronItemHandler.get(itemstack);
        return handler.getSlot();
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() && slotChanged;
    }
}
