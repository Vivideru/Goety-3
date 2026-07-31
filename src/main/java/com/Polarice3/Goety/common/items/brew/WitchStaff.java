package com.Polarice3.Goety.common.items.brew;

import com.Polarice3.Goety.common.entities.projectiles.ThrownBrew;
import com.Polarice3.Goety.common.items.handler.WitchStaffItemHandler;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WitchStaff extends Item {
    public WitchStaff(Properties p_41383_) {
        super(p_41383_);
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (getThrowBrew(itemstack) != null){
            if (!worldIn.isClientSide) {
                ThrownBrew thrownBrew = new ThrownBrew(worldIn, playerIn);
                thrownBrew.setItem(itemstack);
                float velocity = 0.5F + BrewUtils.getVelocity(itemstack);
                thrownBrew.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), -20.0F, velocity, 1.0F);
                if (worldIn.addFreshEntity(thrownBrew)) {
                    SEHelper.addCooldown(playerIn, this, MathHelper.secondsToTicks(1));
                }
            }

            playerIn.awardStat(Stats.ITEM_USED.get(this));
            if (!playerIn.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
        } else if (getDrinkBrew(itemstack) != null){
            playerIn.startUsingItem(handIn);
        }

        return InteractionResultHolder.consume(itemstack);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        BrewItem drinkBrew = getDrinkBrew(stack);
        if (drinkBrew != null){
            // Mutating the slot stack directly does not notify the 1.21 container component, so extract and reinsert through the handler.
            WitchStaffItemHandler handler = WitchStaffItemHandler.get(stack);
            ItemStack brew = handler.extractItem();
            ItemStack result = drinkBrew.finishUsingItem(brew, worldIn, entityLiving);
            if (!result.isEmpty()) {
                if (result.getItem() instanceof BrewItem) {
                    handler.insertItem(result);
                } else if (entityLiving instanceof Player player) {
                    player.getInventory().add(result);
                } else {
                    entityLiving.spawnAtLocation(result);
                }
            }
        }
        return stack;
    }

    public int getUseDuration(@NotNull ItemStack stack, LivingEntity entity) {
        if (getDrinkBrew(stack) != null) {
            // Minecraft 1.21 passes the using entity when querying duration; delegate to the contained brew with that context.
            return getDrinkBrew(stack).getUseDuration(getBrew(stack), entity);
        } else {
            return 0;
        }
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        if (getDrinkBrew(stack) != null) {
            return getDrinkBrew(stack).getUseAnimation(getBrew(stack));
        }
        return UseAnim.NONE;
    }

    public static ItemStack getBrew(ItemStack itemstack) {
        WitchStaffItemHandler handler = WitchStaffItemHandler.get(itemstack);
        return handler.getSlot();
    }

    public static BrewItem getDrinkBrew(ItemStack itemStack){
        if (getBrew(itemStack) != null && !getBrew(itemStack).isEmpty() && getBrew(itemStack).getItem() instanceof BrewItem brewItem){
            return brewItem;
        } else {
            return null;
        }
    }

    public static ThrowableBrewItem getThrowBrew(ItemStack itemStack){
        if (getBrew(itemStack) != null && !getBrew(itemStack).isEmpty() && getBrew(itemStack).getItem() instanceof ThrowableBrewItem brewItem){
            return brewItem;
        } else {
            return null;
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack) && slotChanged;
    }
}
