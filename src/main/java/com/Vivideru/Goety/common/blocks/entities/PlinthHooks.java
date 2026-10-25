package com.Vivideru.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.entities.IGolem;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.utils.MobUtil;
import com.Vivideru.Goety.common.blocks.PlinthBlock;
import com.Vivideru.Goety.common.capabilities.VivideruAttachments;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

/** Everything that links golems and Haunted Armors to Plinths: binding, resting, waking, mending and the enemy check. */
public final class PlinthHooks {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG_POS = "PlinthPos";
    private static final String TAG_DIM = "PlinthDim";
    private static final String TAG_RESTING = "Resting";
    private static final String TAG_FROZEN = "goety_plinth_frozen";
    public static final float HEAL_SHARE_PER_SECOND = 0.02F;
    public static final float REPAIR_SHARE_PER_SECOND = 0.02F;
    public static final int RETURN_DELAY = 100;

    private PlinthHooks() {
    }

    /** Every Goety golem (servants, Redstone Golems and Monstrosities, hostile golems) and every Haunted Armor. */
    public static boolean canRest(Entity entity) {
        return entity instanceof IGolem || entity instanceof AbstractHauntedArmor;
    }

    /** Side of the square of Plinths a mob needs: 1, 3 or 5 plates wide depending on its width. */
    public static int footprint(Mob mob) {
        float width = mob.getBbWidth();
        return width <= 1.5F ? 1 : width <= 3.0F ? 3 : 5;
    }

    @Nullable
    public static GlobalPos getPlinth(Mob mob) {
        if (!mob.hasData(VivideruAttachments.PLINTH)) {
            return null;
        }
        CompoundTag tag = mob.getData(VivideruAttachments.PLINTH);
        if (!tag.contains(TAG_POS) || !tag.contains(TAG_DIM)) {
            return null;
        }
        ResourceLocation dimension = ResourceLocation.tryParse(tag.getString(TAG_DIM));
        if (dimension == null) {
            return null;
        }
        return GlobalPos.of(ResourceKey.create(Registries.DIMENSION, dimension), BlockPos.of(tag.getLong(TAG_POS)));
    }

    public static void setPlinth(Mob mob, GlobalPos plinth) {
        CompoundTag tag = mob.getData(VivideruAttachments.PLINTH);
        tag.putLong(TAG_POS, plinth.pos().asLong());
        tag.putString(TAG_DIM, plinth.dimension().location().toString());
        tag.putBoolean(TAG_RESTING, false);
        mob.setData(VivideruAttachments.PLINTH, tag);
    }

    public static void clearPlinth(Mob mob) {
        if (mob.hasData(VivideruAttachments.PLINTH)) {
            mob.removeData(VivideruAttachments.PLINTH);
        }
    }

    /** Server side: the mob is a statue on its Plinth. */
    public static boolean isResting(Entity entity) {
        return entity instanceof Mob mob && mob.hasData(VivideruAttachments.PLINTH) && mob.getData(VivideruAttachments.PLINTH).getBoolean(TAG_RESTING);
    }

    private static void setResting(Mob mob, boolean resting) {
        CompoundTag tag = mob.getData(VivideruAttachments.PLINTH);
        tag.putBoolean(TAG_RESTING, resting);
        mob.setData(VivideruAttachments.PLINTH, tag);
    }

    /** Both sides: statues do not tick at all, so they make no sound, no particles and keep their first idle frame. */
    public static boolean isFrozen(Entity entity) {
        if (!(entity instanceof Mob mob) || !canRest(mob) || !mob.isNoAi()) {
            return false;
        }
        return mob.level().isClientSide || isResting(mob);
    }

    /** Client side, every tick for a frozen statue: snap to the position and facing the server sent and rewind the animations once. */
    public static void freezeClient(Mob mob) {
        mob.setPos(mob.lerpTargetX(), mob.lerpTargetY(), mob.lerpTargetZ());
        mob.xo = mob.getX();
        mob.yo = mob.getY();
        mob.zo = mob.getZ();
        float yRot = mob.lerpTargetYRot();
        mob.setYRot(yRot);
        mob.yRotO = yRot;
        mob.yBodyRot = yRot;
        mob.yBodyRotO = yRot;
        mob.setYHeadRot(yRot);
        mob.yHeadRotO = yRot;
        mob.setXRot(0.0F);
        mob.xRotO = 0.0F;
        CompoundTag data = mob.getPersistentData();
        if (!data.getBoolean(TAG_FROZEN)) {
            data.putBoolean(TAG_FROZEN, true);
            mob.tickCount = 0;
            mob.walkAnimation.update(0.0F, 1.0F);
            mob.walkAnimation.setSpeed(0.0F);
        }
    }

    public static void thawClient(Mob mob) {
        CompoundTag data = mob.getPersistentData();
        if (data.getBoolean(TAG_FROZEN)) {
            data.remove(TAG_FROZEN);
        }
    }

    @Nullable
    public static PlinthBlockEntity plinthAt(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.getBlockEntity(pos) instanceof PlinthBlockEntity plinth ? plinth : null;
    }

    /** All the plates of the square under the mob must be Plinths of any kind. */
    public static boolean footprintComplete(Level level, BlockPos center, Mob mob) {
        int half = footprint(mob) / 2;
        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                if (!(level.getBlockState(center.offset(dx, 0, dz)).getBlock() instanceof PlinthBlock)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * The middle plate of a complete square for the mob that contains the given plate, so a Waystone bound to any plate of
     * the square works; the plate itself is preferred, then the nearest middle. Null when no complete square contains it.
     */
    @Nullable
    public static BlockPos squareCenter(Level level, BlockPos plate, Mob mob) {
        int half = footprint(mob) / 2;
        BlockPos best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                BlockPos center = plate.offset(dx, 0, dz);
                int distance = dx * dx + dz * dz;
                if (distance < bestDistance && plinthAt(level, center) != null && footprintComplete(level, center, mob)) {
                    best = center;
                    bestDistance = distance;
                }
            }
        }
        return best;
    }

    /** Waystone used on a servant: if the Waystone points at a Plinth in this dimension, the servant adopts it. */
    public static InteractionResult tryBindToPlinth(ItemStack stack, Player player, LivingEntity entity) {
        if (!(entity instanceof Mob mob) || !canRest(mob) || !(mob instanceof IOwned owned) || owned.getTrueOwner() != player) {
            return InteractionResult.PASS;
        }
        GlobalPos globalPos = WaystoneItem.getPosition(stack);
        if (globalPos == null || globalPos.dimension() != player.level().dimension()) {
            return InteractionResult.PASS;
        }
        PlinthBlockEntity plinth = plinthAt(player.level(), globalPos.pos());
        if (plinth == null) {
            return InteractionResult.PASS;
        }
        BlockPos center = squareCenter(player.level(), globalPos.pos(), mob);
        boolean complete = center != null;
        if (complete) {
            globalPos = GlobalPos.of(globalPos.dimension(), center);
        }
        leaveCurrentPlinth(mob);
        setPlinth(mob, globalPos);
        if (mob instanceof IServant servant) {
            servant.setBoundPos(globalPos.pos());
            servant.setWandering(false);
            servant.setStaying(false);
        }
        player.displayClientMessage(Component.translatable(complete ? "info.goety.plinth.bind" : "info.goety.plinth.too_small", entity.getDisplayName(), footprint(mob)), true);
        player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
        LOGGER.debug("Plinth: {} bound to {} (width {}, square {} complete={})", entity.getName().getString(), globalPos.pos(), mob.getBbWidth(), footprint(mob), complete);
        return InteractionResult.SUCCESS;
    }

    private static void leaveCurrentPlinth(Mob mob) {
        GlobalPos current = getPlinth(mob);
        if (current != null && current.dimension() == mob.level().dimension()) {
            PlinthBlockEntity plinth = plinthAt(mob.level(), current.pos());
            if (plinth != null && mob.getUUID().equals(plinth.getOccupant())) {
                plinth.setOccupant(null);
            }
        }
        if (isResting(mob)) {
            wake(mob, null);
        }
    }

    public static void tickPlinth(ServerLevel level, PlinthBlockEntity plinth, boolean secondTick) {
        BlockPos pos = plinth.getBlockPos();
        Mob occupant = plinth.getOccupantMob();
        if (plinth.getOccupant() != null && occupant == null) {
            Entity stale = level.getEntity(plinth.getOccupant());
            if (stale == null || !stale.isAlive()) {
                plinth.setOccupant(null);
            }
            return;
        }
        if (occupant != null) {
            GlobalPos assigned = getPlinth(occupant);
            if (assigned == null || !assigned.pos().equals(pos) || assigned.dimension() != level.dimension()) {
                plinth.setOccupant(null);
                return;
            }
            if (isResting(occupant)) {
                face(occupant, plinth);
                boolean working = needsMending(occupant);
                if (secondTick && working) {
                    mend(occupant);
                }
                plinth.setCharged(working);
                LivingEntity enemy = findEnemy(level, plinth, occupant);
                if (enemy != null) {
                    wake(occupant, enemy);
                }
            } else {
                plinth.setCharged(false);
            }
            return;
        }
        plinth.setCharged(false);
        adopt(level, plinth);
    }

    private static boolean needsMending(Mob mob) {
        if (mob.getHealth() < mob.getMaxHealth()) {
            return true;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = mob.getItemBySlot(slot);
            if (stack.isDamageableItem() && stack.isDamaged()) {
                return true;
            }
        }
        return false;
    }

    /** One second of rest: 2% of the health and 2% of every worn or held item's durability. */
    private static void mend(Mob mob) {
        if (mob.getHealth() < mob.getMaxHealth()) {
            mob.heal(mob.getMaxHealth() * HEAL_SHARE_PER_SECOND);
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = mob.getItemBySlot(slot);
            if (stack.isDamageableItem() && stack.isDamaged()) {
                int step = Math.max(1, Math.round(stack.getMaxDamage() * REPAIR_SHARE_PER_SECOND));
                stack.setDamageValue(Math.max(0, stack.getDamageValue() - step));
            }
        }
    }

    /** Statues look where the Plinth points; the Rotation Focus can turn the plate and the statue follows. */
    private static void face(Mob mob, PlinthBlockEntity plinth) {
        BlockState state = plinth.getBlockState();
        if (!state.hasProperty(PlinthBlock.FACING)) {
            return;
        }
        float yRot = state.getValue(PlinthBlock.FACING).toYRot();
        if (mob.getYRot() != yRot || mob.yBodyRot != yRot || mob.getYHeadRot() != yRot) {
            setRotation(mob, yRot);
        }
    }

    private static void setRotation(Mob mob, float yRot) {
        mob.setYRot(yRot);
        mob.yRotO = yRot;
        mob.yBodyRot = yRot;
        mob.yBodyRotO = yRot;
        mob.setYHeadRot(yRot);
        mob.yHeadRotO = yRot;
        mob.setXRot(0.0F);
        mob.xRotO = 0.0F;
    }

    /** A free Plinth takes in an idle golem or Haunted Armor standing on it: structure guardians, or servants guarding it. */
    private static void adopt(ServerLevel level, PlinthBlockEntity plinth) {
        BlockPos pos = plinth.getBlockPos();
        // The plates under a resting golem's square belong to it: nothing else rests on top of them.
        if (!level.getEntitiesOfClass(Mob.class, new AABB(pos).deflate(0.1D, 0.0D, 0.1D).expandTowards(0.0D, 3.0D, 0.0D), PlinthHooks::isResting).isEmpty()) {
            return;
        }
        AABB area = new AABB(pos).inflate(0.5D, 0.0D, 0.5D).expandTowards(0.0D, 3.0D, 0.0D);
        for (Mob mob : level.getEntitiesOfClass(Mob.class, area, PlinthHooks::canRest)) {
            if (!mob.isAlive() || mob.getTarget() != null || isResting(mob) || mob instanceof Raider raider && raider.hasActiveRaid()) {
                continue;
            }
            GlobalPos assigned = getPlinth(mob);
            if (assigned != null && !(assigned.pos().equals(pos) && assigned.dimension() == level.dimension())) {
                continue;
            }
            boolean servant = mob instanceof IOwned owned && owned.getTrueOwner() != null;
            if (servant && !(mob instanceof IServant s && s.getBoundPos() != null && s.getBoundPos().equals(pos))) {
                continue;
            }
            // The plate under the middle of the mob takes it, so a golem on a wide square rests where it stands; a mob just
            // off a lone plate still steps onto it.
            BlockPos under = new BlockPos(mob.getBlockX(), pos.getY(), mob.getBlockZ());
            if (!under.equals(pos) && (plinthAt(level, under) != null || !pos.closerToCenterThan(mob.position(), 1.5D))) {
                continue;
            }
            if (rest(mob, plinth)) {
                return;
            }
        }
    }

    /** Puts the mob on the Plinth as a statue. Returns false when the square of Plinths is too small for it. */
    public static boolean rest(Mob mob, PlinthBlockEntity plinth) {
        Level level = plinth.getLevel();
        if (level == null || !footprintComplete(level, plinth.getBlockPos(), mob)) {
            LOGGER.debug("Plinth: {} cannot rest at {}, the square of {} Plinths is incomplete", mob.getName().getString(), plinth.getBlockPos(), footprint(mob));
            return false;
        }
        GlobalPos globalPos = GlobalPos.of(level.dimension(), plinth.getBlockPos());
        GlobalPos assigned = getPlinth(mob);
        if (assigned == null || !assigned.equals(globalPos)) {
            setPlinth(mob, globalPos);
        }
        Vec3 center = plinth.restPosition();
        BlockState state = plinth.getBlockState();
        float yRot = state.hasProperty(PlinthBlock.FACING) ? state.getValue(PlinthBlock.FACING).toYRot() : mob.getYRot();
        mob.getNavigation().stop();
        mob.setDeltaMovement(Vec3.ZERO);
        mob.moveTo(center.x, center.y, center.z, yRot, 0.0F);
        setRotation(mob, yRot);
        if (mob instanceof Summoned summoned) {
            summoned.overrideSetTarget(null);
            summoned.setBoundPos(plinth.getBlockPos());
            summoned.setWandering(false);
            summoned.setStaying(false);
        } else {
            mob.setTarget(null);
        }
        mob.setNoAi(true);
        mob.setSilent(true);
        mob.setPersistenceRequired();
        setResting(mob, true);
        plinth.setOccupant(mob.getUUID());
        level.playSound(null, plinth.getBlockPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 0.8F, 0.6F);
        LOGGER.debug("Plinth: {} now rests at {} facing {}", mob.getName().getString(), plinth.getBlockPos(), yRot);
        return true;
    }

    /** Brings a statue back to life, optionally with a target to attack. */
    public static void wake(Mob mob, @Nullable LivingEntity target) {
        if (!isResting(mob)) {
            if (target != null) {
                setTarget(mob, target);
            }
            return;
        }
        mob.setNoAi(false);
        mob.setSilent(false);
        setResting(mob, false);
        if (target != null) {
            setTarget(mob, target);
        }
        mob.level().playSound(null, mob.blockPosition(), SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8F, 0.7F);
        LOGGER.debug("Plinth: {} wakes up{}", mob.getName().getString(), target != null ? " against " + target.getName().getString() : "");
    }

    private static void setTarget(Mob mob, LivingEntity target) {
        if (mob instanceof Summoned summoned) {
            summoned.overrideSetTarget(target);
        } else {
            mob.setTarget(target);
        }
    }

    @Nullable
    private static LivingEntity findEnemy(ServerLevel level, PlinthBlockEntity plinth, Mob occupant) {
        int radius = plinth.getRadius();
        Vec3 center = plinth.restPosition();
        LivingEntity nearest = null;
        double best = Double.MAX_VALUE;
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, new AABB(plinth.getBlockPos()).inflate(radius), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        for (LivingEntity candidate : candidates) {
            double distance = candidate.distanceToSqr(center);
            if (distance <= radius * (double) radius && distance < best && isEnemy(occupant, candidate)) {
                nearest = candidate;
                best = distance;
            }
        }
        return nearest;
    }

    public static boolean isEnemy(Mob occupant, LivingEntity candidate) {
        if (candidate == occupant || !candidate.isAlive() || isResting(candidate)) {
            return false;
        }
        LivingEntity owner = occupant instanceof IOwned owned ? owned.getTrueOwner() : null;
        if (owner != null) {
            if (candidate == owner || MobUtil.areAllies(owner, candidate) || MobUtil.areAllies(occupant, candidate)) {
                return false;
            }
            if (candidate instanceof Enemy) {
                return true;
            }
            return candidate instanceof Mob mob && mob.getTarget() != null && (mob.getTarget() == owner || MobUtil.areAllies(owner, mob.getTarget()));
        }
        if (candidate instanceof Player) {
            return true;
        }
        if (candidate instanceof IOwned owned && owned.getTrueOwner() instanceof Player) {
            return true;
        }
        return candidate instanceof Mob mob && mob.getTarget() == occupant;
    }

    /** Called for statues that got damaged by something that bypasses invulnerability. */
    public static void onDisturbed(Mob mob) {
        if (isResting(mob)) {
            wake(mob, null);
        }
    }

    public static Direction facingOf(BlockState state) {
        return state.hasProperty(PlinthBlock.FACING) ? state.getValue(PlinthBlock.FACING) : Direction.NORTH;
    }
}
