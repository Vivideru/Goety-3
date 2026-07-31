package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.fluids.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class EndMudFluidBlock extends LiquidBlock {

    public EndMudFluidBlock() {
        super(ModFluids.END_MUD_FLUID_SOURCE.get(), Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
                .noCollission()
                .strength(100.0F)
                .noLootTable()
                .replaceable()
                .liquid()
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
        entity.fallDistance = 0.0F;
        if (entity instanceof LivingEntity && entity.tickCount % 20 == 0) {
            // Entity.nextStep is private in 1.21, so rate-limit the ambient swim sound by ticks instead of mutating movement internals.
            Vec3 vec3 = entity.getDeltaMovement();
            float f1 = Math.min(1.0F, (float) vec3.length());
            entity.playSound(SoundEvents.GENERIC_SWIM, f1, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
        }
    }
}
