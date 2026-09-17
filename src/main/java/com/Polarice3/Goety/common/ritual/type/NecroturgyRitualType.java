package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import com.Polarice3.Goety.common.ritual.Ritual;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class NecroturgyRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.NECROTURGY;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(Items.SCULK);
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, Player pPlayer, BlockPos pPos, Level pLevel) {
        if (!(pLevel.getSkyDarken() >= 4 && pLevel.dimensionType().hasSkyLight())) {
            if (pPlayer != null) {
                pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.night"), true);
            }
            return false;
        }
        return RitualRequirements.getStructures(this.getName(), pPlayer, pPos, pLevel);
    }

    @Override
    public void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem) {
        int range = Ritual.range();
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                for (int z = -range; z <= range; ++z) {
                    BlockPos pos = darkAltarPos.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    if (state.is(ModBlocks.GRAVE_SOIL.get()) && world.getRandom().nextBoolean()) {
                        world.levelEvent(null, 2001, pos, Block.getId(state));
                        world.setBlockAndUpdate(pos, ModBlocks.DARK_DIRT.get().defaultBlockState());
                    }
                }
            }
        }
    }
}
