package com.Vivideru.Goety.common.world.structures.crypt;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.blocks.ISeat;
import com.Polarice3.Goety.common.blocks.ThroneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * The study hall of the crypt keeps a Cairn Necromancer on one of its Royalty thrones. Templates cannot store a mob riding a
 * seat, so the exported necromancer carries a tag instead: once it ticks, it sits on a random free Royalty throne nearby
 * and the tag is dropped. It rises when it finds a target, as any seated mob.
 */
@EventBusSubscriber(modid = Goety.MOD_ID)
public final class CryptThroneSitters {
    public static final String TAG = "goety_crypt_throne";
    /** How far the thrones may be from the necromancer, sideways and up or down. */
    private static final int RANGE = 20;
    private static final int HEIGHT = 6;
    /** Ticks between two tries, and after which a necromancer that found no throne stops looking. */
    private static final int RETRY = 20;
    private static final int GIVE_UP = 600;

    private CryptThroneSitters() {
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide || mob.tickCount % RETRY != 0 || !mob.getTags().contains(TAG)) {
            return;
        }
        if (mob.isPassenger()) {
            mob.removeTag(TAG);
            return;
        }
        Level level = mob.level();
        BlockPos center = mob.blockPosition();
        List<BlockPos> free = new ArrayList<>();
        boolean incomplete = false;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RANGE, -HEIGHT, -RANGE), center.offset(RANGE, HEIGHT, RANGE))) {
            if (!level.isLoaded(pos)) {
                incomplete = true;
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof ThroneBlock throne && state.getValue(ThroneBlock.HALF) == DoubleBlockHalf.LOWER
                    && throne.isThrone(level, pos) && !ISeat.isSeatOccupied(level, pos)) {
                free.add(pos.immutable());
            }
        }
        if (!free.isEmpty()) {
            BlockPos seat = free.get(mob.getRandom().nextInt(free.size()));
            if (level.getBlockState(seat).getBlock() instanceof ThroneBlock throne) {
                mob.moveTo(seat.getX() + 0.5D, seat.getY(), seat.getZ() + 0.5D, mob.getYRot(), mob.getXRot());
                throne.placeSeat(level, seat, mob);
            }
            mob.removeTag(TAG);
        } else if (!incomplete || mob.tickCount >= GIVE_UP) {
            mob.removeTag(TAG);
        }
    }
}
