package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.common.blocks.CursedCageBlock;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * Learned how to make Totem of Souls gain Soul Energy from codes by @Ipsis
 */
public class TotemOfSouls extends Item implements ITotem, ICurioItem {
    public final IntSupplier maxSouls;

    public TotemOfSouls(int maxSouls) {
        this(() -> maxSouls);
    }

    public TotemOfSouls(IntSupplier maxSouls) {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE));
        this.maxSouls = maxSouls;
    }

    public int getMaxSouls(){
        // The configured maximum can only be read after configs have loaded.
        return this.maxSouls.getAsInt();
    }

    public ItemStack getEmptyTotem(){
        ItemStack emptySouls = new ItemStack(this);
        ITotem.setSoulsamount(emptySouls, 0);
        ITotem.setMaxSoulAmount(emptySouls, this.getMaxSouls());
        return emptySouls;
    }

    public ItemStack getFilledTotem(){
        ItemStack maxSouls = new ItemStack(this);
        ITotem.setSoulsamount(maxSouls, this.getMaxSouls());
        ITotem.setMaxSoulAmount(maxSouls, this.getMaxSouls());
        return maxSouls;
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (1.0F - amountColor(stack))/2.0F);
        return Mth.hsvToRgb(f, 1.0F, 1.0F);
    }

    public double amountColor(ItemStack stack){
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            int Soulcount = ITotem.tag(stack).getInt(SOULS_AMOUNT);
            int MaxSouls = ITotem.tag(stack).getInt(MAX_SOUL_AMOUNT);
            return 1.0D - (Soulcount / (double) MaxSouls);
        } else {
            return 1.0D;
        }
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        ITotem.setSoulsamount(pStack, 0);
        ITotem.setMaxSoulAmount(pStack, this.getMaxSouls());
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    @NotNull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        if (container.has(DataComponents.CUSTOM_DATA)) {
            if (ITotem.tag(container).getInt(SOULS_AMOUNT) > ItemConfig.CraftingSouls.get()) {
                ITotem.decreaseSouls(container, ItemConfig.CraftingSouls.get());
                return container;
            } else {
                return new ItemStack(ModItems.SPENT_TOTEM.get());
            }
        } else {
            return new ItemStack(ModItems.SPENT_TOTEM.get());
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        this.setTagTick(stack);
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public static boolean isActivated(ItemStack itemStack){
        return itemStack.has(DataComponents.CUSTOM_DATA);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            int Soulcount = ITotem.tag(stack).getInt(SOULS_AMOUNT);
            int MaxSouls = ITotem.tag(stack).getInt(MAX_SOUL_AMOUNT);
            return Math.round((Soulcount * 13.0F / MaxSouls));
        } else {
            return 0;
        }
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof CursedCageBlock cageBlock && !blockstate.getValue(CursedCageBlock.POWERED)) {
            ItemStack itemstack = pContext.getItemInHand();
            if (!world.isClientSide) {
                cageBlock.setItem(world, blockpos, blockstate, itemstack);
                world.levelEvent(null, 1010, blockpos, Item.getId(this));
            }

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            int Soulcounts = ITotem.tag(stack).getInt(SOULS_AMOUNT);
            int MaxSouls = ITotem.tag(stack).getInt(MAX_SOUL_AMOUNT);
            tooltip.add(Component.translatable("info.goety.totem_of_souls.souls", Soulcounts, MaxSouls));
        }
    }

}
