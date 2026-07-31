package com.Polarice3.Goety.client.inventory.container;

import com.Polarice3.Goety.common.blocks.DarkAnvilBlock;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModTags;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class DarkAnvilMenu extends AnvilMenu {
    public int repairItemCountCost;
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();

    public DarkAnvilMenu(int p_39008_, Inventory p_39009_, ContainerLevelAccess p_39010_) {
        super(p_39008_, p_39009_, p_39010_);
        this.addDataSlot(this.cost);
    }

    public DarkAnvilMenu(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(i, inventory, ContainerLevelAccess.NULL);
    }

    public MenuType<?> getType() {
        return ModContainerType.DARK_ANVIL.get();
    }

    protected boolean isValidBlock(BlockState p_39019_) {
        return p_39019_.is(ModTags.Blocks.DARK_ANVILS);
    }

    protected boolean mayPickup(Player p_39023_, boolean p_39024_) {
        return (p_39023_.getAbilities().instabuild || p_39023_.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    protected void onTake(Player p_150474_, ItemStack p_150475_) {
        if (!p_150474_.getAbilities().instabuild) {
            if (MainConfig.DarkAnvilTakePoints.get()){
                p_150474_.giveExperiencePoints(-this.cost.get());
            } else {
                p_150474_.giveExperienceLevels(-this.cost.get());
            }
        }

        float breakChance = net.neoforged.neoforge.common.CommonHooks.onAnvilRepair(p_150474_, p_150475_, DarkAnvilMenu.this.inputSlots.getItem(0), DarkAnvilMenu.this.inputSlots.getItem(1));

        if (breakChance > 0.05F) {
            breakChance = 0.05F;
        }
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemstack = this.inputSlots.getItem(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
                itemstack.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemstack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.cost.set(0);
        float finalBreakChance = breakChance;
        this.access.execute((p_150479_, p_150480_) -> {
            BlockState blockstate = p_150479_.getBlockState(p_150480_);
            if (!p_150474_.getAbilities().instabuild && blockstate.is(ModTags.Blocks.DARK_ANVILS) && p_150474_.getRandom().nextFloat() < finalBreakChance) {
                BlockState blockstate1 = DarkAnvilBlock.damage(blockstate);
                if (blockstate1 == null) {
                    p_150479_.removeBlock(p_150480_, false);
                    for (int i = 0; i < 12; ++i) {
                        if (p_150479_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
                            ItemStack itemStack = new ItemStack(ModItems.DARK_ALLOY_INGOT.get());
                            double d0 = (double) (p_150479_.random.nextFloat() * 0.5F) + 0.25D;
                            double d1 = (double) (p_150479_.random.nextFloat() * 0.5F) + 0.25D;
                            double d2 = (double) (p_150479_.random.nextFloat() * 0.5F) + 0.25D;
                            ItemEntity itementity = new ItemEntity(p_150479_, (double) p_150480_.getX() + d0, (double) p_150480_.getY() + d1, (double) p_150480_.getZ() + d2, itemStack);
                            itementity.setDefaultPickUpDelay();
                            p_150479_.addFreshEntity(itementity);
                        }
                    }
                    p_150479_.levelEvent(1029, p_150480_, 0);
                } else {
                    p_150479_.setBlock(p_150480_, blockstate1, 2);
                    p_150479_.levelEvent(1030, p_150480_, 0);
                }
            } else {
                p_150479_.levelEvent(1030, p_150480_, 0);
            }

        });
    }

    public void createResult() {
        ItemStack itemstack = this.inputSlots.getItem(0);
        this.cost.set(1);
        int i = 0;
        long j = 0L;
        int k = 0;
        if (!itemstack.isEmpty()) {
            if (!net.neoforged.neoforge.common.CommonHooks.onAnvilChange(this, itemstack, this.inputSlots.getItem(1), resultSlots, itemName, j, this.player)) {
                return;
            }
        }
        if (!itemstack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemstack)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getItem(1);
            ItemEnchantments.Mutable itemEnchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemstack1));
            j += (long)itemstack.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0))
                    + (long)itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
            this.repairItemCountCost = 0;
            boolean flag = false;

            // Based on vanilla 1.21 AnvilMenu#createResult; kept local so Dark Anvils can preserve their configurable cost rules.
            if (!itemstack2.isEmpty()) {
                flag = itemstack2.has(DataComponents.STORED_ENCHANTMENTS);
                if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
                    int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    int i3;
                    for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                        int j3 = itemstack1.getDamageValue() - l2;
                        itemstack1.setDamageValue(j3);
                        ++i;
                        l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    }

                    this.repairItemCountCost = i3;
                } else {
                    if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    if (itemstack1.isDamageableItem() && !flag) {
                        int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    ItemEnchantments itemEnchantments2 = EnchantmentHelper.getEnchantmentsForCrafting(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (Entry<Holder<Enchantment>> entry : itemEnchantments2.entrySet()) {
                        Holder<Enchantment> holder = entry.getKey();
                        int i2 = itemEnchantments.getLevel(holder);
                        int j2 = entry.getIntValue();
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        Enchantment enchantment = holder.value();
                        boolean flag1 = itemstack.supportsEnchantment(holder);
                        if (this.player.getAbilities().instabuild) {
                            flag1 = true;
                        }

                        for (Holder<Enchantment> holder1 : itemEnchantments.keySet()) {
                            if (!holder1.equals(holder) && !Enchantment.areCompatible(holder, holder1)) {
                                flag1 = false;
                                ++i;
                            }
                        }

                        if (!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (!MainConfig.DarkAnvilIgnoreMaxLevels.get() && j2 > enchantment.getMaxLevel()) {
                                j2 = enchantment.getMaxLevel();
                            }

                            int maxLevel = Math.max(itemEnchantments.getLevel(holder), itemEnchantments2.getLevel(holder));
                            maxLevel = Math.max(maxLevel, j2);
                            if (maxLevel != j2) {
                                j2 = maxLevel;
                            }

                            itemEnchantments.set(holder, j2);
                            int k3 = enchantment.getAnvilCost();

                            if (flag) {
                                k3 = Math.max(1, k3 / 2);
                            }

                            i += k3 * j2;
                            if (itemstack.getCount() > 1) {
                                i = MainConfig.DarkAnvilRepairCost.get();
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
                if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                    k = 1;
                    i += k;
                    itemstack1.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
                }
            } else if (itemstack.has(DataComponents.CUSTOM_NAME)) {
                k = 1;
                i += k;
                itemstack1.remove(DataComponents.CUSTOM_NAME);
            }
            if (flag && !itemstack1.isBookEnchantable(itemstack2)) {
                itemstack1 = ItemStack.EMPTY;
            }

            this.cost.set((int)Mth.clamp(j + (long)i, 0L, 2147483647L));
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (this.cost.get() > MainConfig.DarkAnvilRepairCost.get()){
                this.cost.set(MainConfig.DarkAnvilRepairCost.get());
            }

            if (k == i && k > 0) {
                this.cost.set(1);
            }

            if (MainConfig.DarkAnvilCap.get() && this.cost.get() >= MainConfig.DarkAnvilRepairCost.get() && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int k2 = itemstack1.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                if (!itemstack2.isEmpty() && k2 < itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0))) {
                    k2 = itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                }

                if (k != i || k == 0) {
                    k2 = calculateIncreasedRepairCost(k2);
                }

                itemstack1.set(DataComponents.REPAIR_COST, k2);
                EnchantmentHelper.setEnchantments(itemstack1, itemEnchantments.toImmutable());
            }

            this.resultSlots.setItem(0, itemstack1);
            this.broadcastChanges();
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        }
    }

    public static int calculateIncreasedRepairCost(int repairCost) {
        return (int)Math.min((long)repairCost * 2L + 1L, 2147483647L);
    }

    public boolean setItemName(String p_288970_) {
        String s = validateName(p_288970_);
        if (s != null && !s.equals(this.itemName)) {
            this.itemName = s;
            if (this.getSlot(2).hasItem()) {
                ItemStack itemstack = this.getSlot(2).getItem();
                if (StringUtil.isBlank(s)) {
                    itemstack.remove(DataComponents.CUSTOM_NAME);
                } else {
                    itemstack.set(DataComponents.CUSTOM_NAME, Component.literal(s));
                }
            }

            this.createResult();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private static String validateName(String p_288995_) {
        String s = StringUtil.filterText(p_288995_);
        return s.length() <= 50 ? s : null;
    }

    public int getCost() {
        return this.cost.get();
    }

    public void setMaximumCost(long value) {
        this.cost.set((int)Mth.clamp(value, 0L, Integer.MAX_VALUE));
    }

}
