package com.Polarice3.Goety.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModTradeUtil {
    /**
     * Based on TeamAbnormals' TradeUtil: <a href="https://github.com/team-abnormals/blueprint/blob/1.19.x/src/main/java/com/teamabnormals/blueprint/core/util/TradeUtil.java">...</a>
     */
    public static final int NOVICE = 1;
    public static final int APPRENTICE = 2;
    public static final int JOURNEYMAN = 3;
    public static final int EXPERT = 4;
    public static final int MASTER = 5;

    public static void addVillagerTrades(VillagerTradesEvent event, int profLevel, VillagerTrades.ItemListing... trades) {
        List<VillagerTrades.ItemListing> levelTrades = event.getTrades().get(profLevel);
        // Some compatibility stacks replace trade lists with immutable collections before this event runs.
        if (!(levelTrades instanceof ArrayList<VillagerTrades.ItemListing>)) {
            levelTrades = levelTrades == null ? new ArrayList<>() : new ArrayList<>(levelTrades);
            event.getTrades().put(profLevel, levelTrades);
        }
        for (VillagerTrades.ItemListing trade : trades) {
            levelTrades.add(trade);
        }
    }

    public static void addVillagerTrades(VillagerTradesEvent event, VillagerProfession profession, int profLevel, VillagerTrades.ItemListing... trades) {
        if (event.getType().equals(profession)) {
            addVillagerTrades(event, profLevel, trades);
        }
    }
    /**
     * To Here.
     */

    public static class ItemsForEmeralds implements VillagerTrades.ItemListing {
        private final Item item;
        private final int cost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForEmeralds(ItemLike item, int cost, int maxUses, int villagerXp) {
            this.item = item.asItem();
            this.cost = cost;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = 0.05F;
        }

        public MerchantOffer getOffer(Entity p_219699_, RandomSource p_219700_) {
            return new MerchantOffer(new ItemCost(Items.EMERALD, this.cost), new ItemStack(this.item), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    public static class TreasureMapForEmeralds implements VillagerTrades.ItemListing {
        private final int emeraldCost;
        private final TagKey<Structure> destination;
        private final String displayName;
        private final Holder<MapDecorationType> destinationType;
        private final int maxUses;
        private final int villagerXp;

        public TreasureMapForEmeralds(int emeraldCost, TagKey<Structure> destination, String displayName, Holder<MapDecorationType> mapMarker, int maxUses, int villagerXp) {
            this.emeraldCost = emeraldCost;
            this.destination = destination;
            this.displayName = displayName;
            this.destinationType = mapMarker;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
        }

        @Nullable
        public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
            if (!(entity.level() instanceof ServerLevel serverlevel)) {
                return null;
            } else {
                BlockPos blockpos = serverlevel.findNearestMapStructure(this.destination, entity.blockPosition(), 100, true);
                if (blockpos != null) {
                    ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
                    MapItem.renderBiomePreviewMap(serverlevel, itemstack);
                    MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
                    // ItemStack custom names are stored as data components in 1.21.
                    itemstack.set(DataComponents.CUSTOM_NAME, Component.translatable(this.displayName));
                    return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(new ItemCost(Items.COMPASS)), itemstack, this.maxUses, this.villagerXp, 0.2F);
                } else {
                    return null;
                }
            }
        }
    }
}
