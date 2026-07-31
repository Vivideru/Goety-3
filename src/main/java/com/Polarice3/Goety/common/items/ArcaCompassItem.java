package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ArcaCompassItem extends Item {
    public static final String TAG_PLAYER = "TrackedPlayer";
    public static final String TAG_PLAYER_NAME = "TrackedPlayerName";
    public static final String TAG_ARCA_POS = "ArcaPos";
    public static final String TAG_ARCA_DIMENSION = "ArcaDimension";
    public static final String TAG_ARCA_TRACKED = "ArcaTracked";

    public ArcaCompassItem() {
        super(new Item.Properties().fireResistant());
    }

    public static CompoundTag tag(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static void updateTag(ItemStack itemStack, Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, updater);
    }

    public static boolean hasPlayer(ItemStack p_40737_) {
        CompoundTag compoundtag = tag(p_40737_);
        return !compoundtag.isEmpty() && compoundtag.contains(TAG_PLAYER);
    }

    public static boolean hasArca(ItemStack p_40737_) {
        CompoundTag compoundtag = tag(p_40737_);
        return !compoundtag.isEmpty() && (compoundtag.contains(TAG_ARCA_DIMENSION) || compoundtag.contains(TAG_ARCA_POS));
    }

    private static Optional<ResourceKey<Level>> getArcaDimension(CompoundTag p_40728_) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get(TAG_ARCA_DIMENSION)).result();
    }

    @Nullable
    public static GlobalPos getArcaPosition(CompoundTag p_220022_) {
        boolean flag = p_220022_.contains(TAG_ARCA_POS);
        boolean flag1 = p_220022_.contains(TAG_ARCA_DIMENSION);
        if (flag && flag1) {
            Optional<ResourceKey<Level>> optional = getArcaDimension(p_220022_);
            if (optional.isPresent()) {
                BlockPos blockpos = NbtUtils.readBlockPos(p_220022_, TAG_ARCA_POS).orElse(null);
                if (blockpos == null) {
                    return null;
                }
                return GlobalPos.of(optional.get(), blockpos);
            }
        }

        return null;
    }

    public boolean isFoil(ItemStack p_40739_) {
        return hasPlayer(p_40739_) || super.isFoil(p_40739_);
    }

    public void inventoryTick(ItemStack p_40720_, Level p_40721_, Entity p_40722_, int p_40723_, boolean p_40724_) {
        if (!p_40721_.isClientSide) {
            // ItemStack custom NBT is stored through data components in 1.21; copy, mutate, then write it back.
            CompoundTag compoundtag = tag(p_40720_);
            if (!p_40722_.isAlive()){
                if (compoundtag.contains(TAG_PLAYER)){
                    compoundtag.remove(TAG_PLAYER);
                }
                if (compoundtag.contains(TAG_PLAYER_NAME)){
                    compoundtag.remove(TAG_PLAYER_NAME);
                }
                if (compoundtag.contains(TAG_ARCA_POS)){
                    compoundtag.remove(TAG_ARCA_POS);
                }
                if (compoundtag.contains(TAG_ARCA_DIMENSION)){
                    compoundtag.remove(TAG_ARCA_DIMENSION);
                }
                if (compoundtag.contains(TAG_ARCA_TRACKED)){
                    compoundtag.remove(TAG_ARCA_TRACKED);
                }
            }
            if (hasPlayer(p_40720_)){
                if (compoundtag.contains(TAG_PLAYER)){
                    Player player = p_40721_.getPlayerByUUID(compoundtag.getUUID(TAG_PLAYER));
                    if (player == null) {
                        return;
                    }
                    if (SEHelper.getArcaBlock(player) != null){
                        if (!compoundtag.contains(TAG_ARCA_POS)){
                            compoundtag.put(TAG_ARCA_POS, NbtUtils.writeBlockPos(SEHelper.getArcaBlock(player)));
                            if (SEHelper.getArcaDimension(player) != null){
                                Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, SEHelper.getArcaDimension(player)).result().ifPresent((p_40731_) -> {
                                    compoundtag.put(TAG_ARCA_DIMENSION, p_40731_);
                                });
                            }
                        }
                    }
                }
                if (hasArca(p_40720_)) {
                    if (compoundtag.contains(TAG_ARCA_TRACKED) && !compoundtag.getBoolean(TAG_ARCA_TRACKED)) {
                        return;
                    }

                    Optional<ResourceKey<Level>> optional = getArcaDimension(compoundtag);
                    if (optional.isPresent() && optional.get() == p_40721_.dimension() && compoundtag.contains(TAG_ARCA_POS)) {
                        BlockPos blockpos = NbtUtils.readBlockPos(compoundtag, TAG_ARCA_POS).orElse(null);
                        if (blockpos == null || !p_40721_.isInWorldBounds(blockpos) || !(p_40721_.getBlockEntity(blockpos) instanceof ArcaBlockEntity)) {
                            compoundtag.remove(TAG_ARCA_POS);
                        }
                    }
                }
            } else if (compoundtag.contains(TAG_PLAYER_NAME)){
                compoundtag.remove(TAG_PLAYER_NAME);
            }
            p_40720_.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundtag));
        }
    }

    public static void addPlayer(Player player, CompoundTag p_40735_) {
        p_40735_.putUUID(TAG_PLAYER, player.getUUID());
        p_40735_.putString(TAG_PLAYER_NAME, player.getDisplayName().getString());
        p_40735_.putBoolean(TAG_ARCA_TRACKED, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = tag(stack);
        if (!tag.isEmpty()) {
            if (tag.contains(TAG_PLAYER_NAME)) {
                tooltip.add(Component.translatable("tooltip.goety.arca_compass_track", tag.getString(TAG_PLAYER_NAME)).withStyle(ChatFormatting.AQUA));
            }
            GlobalPos globalPos = getArcaPosition(tag);
            if (globalPos != null) {
                BlockPos blockPos = globalPos.pos();
                tooltip.add(Component.translatable("tooltip.goety.arca").withStyle(ChatFormatting.GOLD)
                        .append(Component.translatable("tooltip.goety.arcaCoords", blockPos.getX(), blockPos.getY(), blockPos.getZ())));
                if (getArcaDimension(tag).isPresent()){
                    ResourceKey<Level> dimension = getArcaDimension(tag).get();
                    tooltip.add(Component.translatable("tooltip.goety.arcaDimension", dimension.location().toString()));
                }
            }
        } else {
            tooltip.add(Component.translatable("tooltip.goety.arca_compass").withStyle(ChatFormatting.GOLD));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
