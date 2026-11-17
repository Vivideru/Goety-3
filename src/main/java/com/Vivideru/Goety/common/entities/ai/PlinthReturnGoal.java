package com.Vivideru.Goety.common.entities.ai;

import com.Vivideru.Goety.common.blocks.entities.PlinthBlockEntity;
import com.Vivideru.Goety.common.blocks.entities.PlinthHooks;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/** Walks a golem or Haunted Armor back to its Plinth once it has had no target for a while, then turns it into a statue. */
public class PlinthReturnGoal extends Goal {
    private final Mob mob;
    private int idleTicks;
    private int repathTicks;

    public PlinthReturnGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null || PlinthHooks.isResting(this.mob) || PlinthHooks.stopResting(this.mob) || PlinthHooks.recentlyWoke(this.mob)) {
            this.idleTicks = 0;
            return false;
        }
        PlinthBlockEntity plinth = this.plinth();
        if (plinth == null) {
            return false;
        }
        return ++this.idleTicks >= PlinthHooks.RETURN_DELAY;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getTarget() == null && !PlinthHooks.isResting(this.mob) && !PlinthHooks.stopResting(this.mob) && !PlinthHooks.recentlyWoke(this.mob) && this.plinth() != null;
    }

    @Override
    public void start() {
        this.repathTicks = 0;
        this.moveToPlinth();
    }

    @Override
    public void stop() {
        this.idleTicks = 0;
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        PlinthBlockEntity plinth = this.plinth();
        if (plinth == null) {
            return;
        }
        Vec3 center = plinth.restPosition();
        // Paths end within half the mob's width of their last node on each axis, so wide golems may stop at a corner of the square.
        double reach = Math.max(1.5D, this.mob.getBbWidth() * 0.75D + 0.5D);
        if (this.mob.distanceToSqr(center) <= reach * reach) {
            if (!PlinthHooks.rest(this.mob, plinth)) {
                this.idleTicks = -200;
            }
            return;
        }
        if (--this.repathTicks <= 0) {
            this.moveToPlinth();
        }
    }

    private void moveToPlinth() {
        PlinthBlockEntity plinth = this.plinth();
        if (plinth != null) {
            Vec3 center = plinth.restPosition();
            // Path nodes of wide mobs are the low corner of the blocks they cover, and the mob walks to the middle of those
            // blocks: aim that much short so the golem ends over the middle plate instead of past it.
            double shift = (int) (this.mob.getBbWidth() + 1.0F) * 0.5D - 0.5D;
            this.mob.getNavigation().moveTo(center.x - shift, center.y, center.z - shift, 1.0D);
            this.repathTicks = 40;
        }
    }

    private PlinthBlockEntity plinth() {
        GlobalPos globalPos = PlinthHooks.getPlinth(this.mob);
        if (globalPos == null || globalPos.dimension() != this.mob.level().dimension()) {
            return null;
        }
        PlinthBlockEntity plinth = PlinthHooks.plinthAt(this.mob.level(), globalPos.pos());
        if (plinth == null && this.mob.level().isLoaded(globalPos.pos())) {
            PlinthHooks.clearPlinth(this.mob);
        }
        return plinth;
    }
}
