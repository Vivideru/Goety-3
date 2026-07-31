package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

public class TeleportRitual extends Ritual {
    public TeleportRitual(RitualRecipe recipe) {
        super(recipe);
    }

    public boolean isValid(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                           Player castingPlayer, ItemStack activationItem,
                           List<Ingredient> remainingAdditionalIngredients) {
        GlobalPos globalPos = WaystoneItem.getPosition(activationItem);
        return activationItem.getItem() instanceof WaystoneItem
                && globalPos != null
                && WaystoneItem.isSameDimension(castingPlayer, activationItem)
                && this.areAdditionalIngredientsFulfilled(world, darkAltarPos, castingPlayer, remainingAdditionalIngredients);
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);

        GlobalPos globalPos = WaystoneItem.getPosition(activationItem);
        if (globalPos != null) {
            BlockPos blockPos1 = globalPos.pos();
            castingPlayer.teleportTo(blockPos1.getX(), blockPos1.getY() + 1.0F, blockPos1.getZ());

            if (world instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.PORTAL, blockPos.getX() + 0.5,
                        blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 1, 0, 0, 0, 0);
                ModNetwork.sendToALL(new SPlayWorldSoundPacket(BlockPos.containing(castingPlayer.xo, castingPlayer.yo, castingPlayer.zo), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockPos1, SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
            }

            activationItem.shrink(1);
        }
    }
}
