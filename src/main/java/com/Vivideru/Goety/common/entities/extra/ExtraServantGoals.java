package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ai.ServantPatrolGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Vivideru.Goety.mixin.NearestAttackableTargetGoalAccessor;
import com.Vivideru.Goety.mixin.RandomStrollGoalAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.ArrayList;
import java.util.List;

/** The goals of an extra servant, set up while the mob is built. */
final class ExtraServantGoals {
    private ExtraServantGoals() {
    }

    static <T extends PathfinderMob & IServant> void setUp(T mob, ExtraServantDefinition definition) {
        if (definition.replaceBrain()) {
            brainless(mob, definition);
        } else {
            onTopOfOwnGoals(mob, definition);
        }
    }

    /**
     * A mob whose Brain is switched off gets a plain servant's goals: it swims, fights with what it holds (a crossbow
     * if it knows how to use one), follows its owner, wanders and looks around. Without an owner it hunts players as the
     * mob would.
     */
    private static <T extends PathfinderMob & IServant> void brainless(T mob, ExtraServantDefinition definition) {
        mob.goalSelector.addGoal(0, new FloatGoal(mob));
        if (mob instanceof CrossbowAttackMob) {
            mob.goalSelector.addGoal(3, crossbowGoal(mob));
        }
        mob.goalSelector.addGoal(3, new HandMeleeGoal(mob));
        if (movement(mob, definition)) {
            mob.goalSelector.addGoal(6, new Summoned.WanderGoal<>(mob, 0.8D));
        }
        mob.goalSelector.addGoal(7, new LookAtPlayerGoal(mob, Player.class, 8.0F));
        mob.goalSelector.addGoal(8, new RandomLookAroundGoal(mob));
        servantTargets(mob);
        mob.targetSelector.addGoal(3, new HurtByTargetGoal(mob));
        mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, Player.class, 10, true, false, player -> mob.isHostile()));
    }

    /**
     * A mob with goals keeps them below the servant's: Goety's targeting and the owner come first, the mob's own fighting
     * stays as it was. Its hunt for players and the goals the config names can be taken away, and its random strolls become
     * Goety's wandering, which keeps still while the servant is told to stay and keeps to the area it guards.
     */
    private static <T extends PathfinderMob & IServant> void onTopOfOwnGoals(T mob, ExtraServantDefinition definition) {
        reorder(mob, mob.goalSelector, definition, false);
        reorder(mob, mob.targetSelector, definition, definition.removePlayerTargets());
        movement(mob, definition);
        servantTargets(mob);
    }

    /** Patrolling and following the owner, unless the config keeps the servant rooted where it was summoned. */
    private static <T extends PathfinderMob & IServant> boolean movement(T mob, ExtraServantDefinition definition) {
        if (!definition.followOwner()) {
            return false;
        }
        mob.goalSelector.addGoal(2, new ServantPatrolGoal<>(mob, mob.getCommandSpeed()));
        mob.goalSelector.addGoal(definition.followPriority(), new Summoned.FollowOwnerGoal<>(mob, mob.getFollowSpeed(), 10.0F, 2.0F));
        return true;
    }

    private static <T extends PathfinderMob & IServant> void servantTargets(T mob) {
        mob.targetSelector.addGoal(1, new SummonTargetGoal(mob));
        mob.targetSelector.addGoal(1, new Owned.OwnerHurtByTargetGoal<>(mob));
        mob.targetSelector.addGoal(2, new Owned.OwnerHurtTargetGoal<>(mob));
    }

    private static <T extends PathfinderMob & IServant> void reorder(T mob, GoalSelector selector, ExtraServantDefinition definition, boolean removePlayerTargets) {
        List<WrappedGoal> goals = new ArrayList<>(selector.getAvailableGoals());
        for (WrappedGoal wrapped : goals) {
            Goal goal = wrapped.getGoal();
            int priority = wrapped.getPriority() + definition.goalShift();
            boolean huntsPlayers = goal instanceof NearestAttackableTargetGoal<?> nearest
                    && Player.class.isAssignableFrom(((NearestAttackableTargetGoalAccessor) nearest).goety$getTargetType());
            // Named by simple class name, or by full name for goals without one (anonymous classes such as EntityFoliaath$1).
            boolean named = definition.removeGoals().contains(goal.getClass().getSimpleName()) || definition.removeGoals().contains(goal.getClass().getName());
            if (named || (removePlayerTargets && huntsPlayers)) {
                selector.removeGoal(goal);
            } else if (goal instanceof RandomStrollGoal stroll) {
                selector.removeGoal(goal);
                selector.addGoal(priority, new Summoned.WanderGoal<>(mob, ((RandomStrollGoalAccessor) stroll).goety$getSpeedModifier()));
            } else if (definition.goalShift() != 0) {
                selector.removeGoal(goal);
                selector.addGoal(priority, goal);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Goal crossbowGoal(Mob mob) {
        return new CrossbowGoal((Mob & CrossbowAttackMob) mob);
    }

    /**
     * The vanilla crossbow goal never makes its mob aggressive: pillagers aim whenever they hold a crossbow, piglins only
     * when their Brain marks them aggressive. With the Brain off, the goal does it, so the loaded crossbow points at the target.
     */
    private static final class CrossbowGoal<T extends Mob & CrossbowAttackMob> extends RangedCrossbowAttackGoal<T> {
        private final T shooter;

        CrossbowGoal(T mob) {
            super(mob, 1.0D, 8.0F);
            this.shooter = mob;
        }

        @Override
        public void start() {
            super.start();
            this.shooter.setAggressive(true);
        }
    }

    /** Melee with whatever the mob holds, unless it holds a bow or a crossbow it shoots with instead. */
    private static final class HandMeleeGoal extends MeleeAttackGoal {
        HandMeleeGoal(PathfinderMob mob) {
            super(mob, 1.0D, true);
        }

        private boolean shoots() {
            return this.mob instanceof CrossbowAttackMob && this.mob.isHolding(stack -> stack.getItem() instanceof ProjectileWeaponItem);
        }

        @Override
        public boolean canUse() {
            return !this.shoots() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.shoots() && super.canContinueToUse();
        }
    }
}
