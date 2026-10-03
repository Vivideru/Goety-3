package com.Vivideru.Goety.common.blocks;

import com.Polarice3.Goety.utils.MobUtil;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import com.Vivideru.Goety.common.entities.hostile.Hurricane;
import com.Vivideru.Goety.common.entities.util.HurricaneCoreSummon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class WhirlingCageBlock extends Block {
    private static final double CORE_HEIGHT = 0.25D;

    public WhirlingCageBlock() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0F, 1200.0F)
                .sound(SoundType.TRIAL_SPAWNER)
                .noOcclusion()
                .isValidSpawn((state, level, pos, type) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!player.isCreative() && level instanceof ServerLevel serverLevel) {
            Hurricane hurricane = new Hurricane(VivideruEntityTypes.HURRICANE.get(), level);
            Vec3 ground = Vec3.atBottomCenterOf(pos);
            hurricane.setPos(ground);
            hurricane.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
            if (MobUtil.validEntity(player)) {
                hurricane.setTarget(player);
            }
            hurricane.setPersistenceRequired();
            level.addFreshEntity(new HurricaneCoreSummon(level, ground.add(0.0D, CORE_HEIGHT, 0.0D), ground.y, hurricane));
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BREEZE_WHIRL, SoundSource.BLOCKS,
                    0.6F, 0.5F + random.nextFloat() * 0.3F, false);
        }
        double angle = random.nextDouble() * Math.PI * 2.0D;
        level.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5D + Math.cos(angle) * 0.3D, pos.getY() + 0.3D + random.nextDouble() * 0.4D,
                pos.getZ() + 0.5D + Math.sin(angle) * 0.3D, -Math.sin(angle) * 0.03D, 0.01D, Math.cos(angle) * 0.03D);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }
}
