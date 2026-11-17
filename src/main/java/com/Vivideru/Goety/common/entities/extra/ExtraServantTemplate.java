package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Vivideru.Goety.common.entities.util.OwnerCache;
import com.Vivideru.Goety.mixin.MobTargetAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The model of every extra servant. It is never used as it is: for each servant of the config, {@link ExtraServantClasses}
 * copies it with the mob it is made from as superclass, so the copy is that mob plus Goety's servant state and goals. Only
 * methods every PathfinderMob has may be used here, and every super call reaches that mob. Overrides are public so they
 * never narrow a method the mob made public.
 */
public abstract class ExtraServantTemplate extends PathfinderMob implements IServant, ExtraServant, OwnableEntity {
    @Nullable
    private UUID ownerId;
    private int ownerClientId = -1;
    private final OwnerCache ownerCache = new OwnerCache();
    private boolean hostile;
    private boolean natural;
    private boolean hasLifespan;
    private int lifespan;
    private int hasSummonCheck;
    private boolean wandering;
    private boolean staying;
    private boolean upgraded;
    @Nullable
    private BlockPos boundPos;
    private String boundDim = Level.OVERWORLD.location().toString();
    @Nullable
    private BlockPos commandPos;
    @Nullable
    private LivingEntity commandEntity;
    private int commandTick;
    private int noHealTime;
    private int killChance;
    @Nullable
    private LivingEntity priorityTarget;
    @Nullable
    private BlockPos priorityPos;
    private int priorityTime;
    private long ticketTime;
    private boolean renderingAsParent;
    private boolean ticking;
    private boolean converting;
    /** Set during the mob's constructor (by registerGoals), so it has no initializer that would reset it afterwards. */
    @Nullable
    private ExtraServantDefinition definition;

    public ExtraServantTemplate(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        ExtraServantDefinition definition = this.goety$definition();
        if (definition != null && definition.data() != null && !level.isClientSide) {
            // Merged into the mob's own data (not the servant's), so it reads every key it expects; saved servants then load over it.
            CompoundTag tag = new CompoundTag();
            super.addAdditionalSaveData(tag);
            super.readAdditionalSaveData(tag.merge(definition.data()));
        }
    }

    @Override
    @Nullable
    public ExtraServantDefinition goety$definition() {
        if (this.definition == null) {
            this.definition = ExtraServants.definition(super.getType());
        }
        return this.definition;
    }

    @Override
    public void goety$setRenderingAsParent(boolean asParent) {
        this.renderingAsParent = asParent;
    }

    @Override
    public EntityType<?> getType() {
        if (this.renderingAsParent) {
            EntityType<?> parent = ExtraServants.parentType(this.goety$definition());
            if (parent != null) {
                return parent;
            }
        }
        return super.getType();
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        ExtraServantDefinition definition = this.goety$definition();
        if (definition != null) {
            ExtraServantGoals.setUp(this, definition);
        }
    }

    /** Brain mobs read their target from the Brain; with it switched off, the target is the one the goals set. */
    @Override
    @Nullable
    public LivingEntity getTarget() {
        ExtraServantDefinition definition = this.goety$definition();
        return definition != null && definition.replaceBrain() ? ((MobTargetAccessor) this).goety$getGoalTarget() : super.getTarget();
    }

    /** A servant made from a Brain mob runs on goals alone: the Brain, and anything else the mob does here, is skipped. */
    @Override
    public void customServerAiStep() {
        ExtraServantDefinition definition = this.goety$definition();
        if (definition == null || !definition.replaceBrain()) {
            super.customServerAiStep();
        }
    }

    @Override
    public void tick() {
        this.ticking = true;
        try {
            super.tick();
        } finally {
            this.ticking = false;
        }
        if (this.ownerId != null) {
            this.ownedTick();
            this.servantTick();
        }
    }

    /**
     * Some mobs discard themselves while they tick in peaceful instead of despawning (the Foliaath does). A friendly servant
     * stays, as Goety's own do; a conversion still removes the mob it replaces.
     */
    @Override
    public void remove(RemovalReason reason) {
        if (reason == RemovalReason.DISCARDED && this.ticking && !this.converting && !this.isHostile()
                && this.level().getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        super.remove(reason);
    }

    @Override
    @Nullable
    public <T extends Mob> T convertTo(EntityType<T> type, boolean transferInventory) {
        this.converting = true;
        try {
            return super.convertTo(type, transferInventory);
        } finally {
            this.converting = false;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.saveOwnedData(tag);
        this.saveServantData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        ExtraServantDefinition definition = this.goety$definition();
        // A /summon or a spawner gives only a few keys and the mob resets the others: those fall back to the starting data.
        super.readAdditionalSaveData(definition != null && definition.data() != null ? definition.data().copy().merge(tag) : tag);
        this.readOwnedData(tag);
        this.readServantData(tag);
    }

    /** Servants never turn on their owner, the owner's allies or the owner's other servants. */
    @Override
    public boolean canAttack(LivingEntity target) {
        LivingEntity owner = this.getTrueOwner();
        if (owner != null) {
            if (target == owner || MobUtil.areAllies(owner, target)) {
                return false;
            }
            if (target instanceof IOwned owned && owned.getTrueOwner() == owner) {
                return false;
            }
        }
        return super.canAttack(target);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return this.ownerId == null && super.removeWhenFarAway(distance);
    }

    /** Monsters vanish in peaceful; as with Goety's servants, only hostile ones do. */
    @Override
    public boolean shouldDespawnInPeaceful() {
        return this.isHostile() && super.shouldDespawnInPeaceful();
    }

    /**
     * Monster's check for players going to sleep nearby. PathfinderMob has no such method, so this one only takes effect
     * when the mob is a monster: then it overrides it, and only hostile servants keep players awake.
     */
    public boolean isPreventingPlayerRest(Player player) {
        return this.isHostile();
    }

    /** Servants leave items alone; a mob without its Brain could not even let go of what it picked up. */
    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        ExtraServantDefinition definition = this.goety$definition();
        return this.ownerId == null && (definition == null || !definition.replaceBrain()) && super.wantsToPickUp(stack);
    }

    /** The mob's own interactions (a piglin taking gold to barter) need the Brain that was switched off. */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ExtraServantDefinition definition = this.goety$definition();
        return definition != null && definition.replaceBrain() ? InteractionResult.PASS : super.mobInteract(player, hand);
    }

    @Override
    public Component getTypeName() {
        EntityType<?> parent = ExtraServants.parentType(this.goety$definition());
        return parent != null ? Component.translatable("entity.goety.extra_servant", parent.getDescription()) : super.getTypeName();
    }

    @Override
    public ResourceKey<LootTable> getDefaultLootTable() {
        EntityType<?> parent = ExtraServants.parentType(this.goety$definition());
        return parent != null ? parent.getDefaultLootTable() : super.getDefaultLootTable();
    }

    @Override
    @Nullable
    public LivingEntity getTrueOwner() {
        if (this.level().isClientSide) {
            Entity entity = this.ownerClientId < 0 ? null : this.level().getEntity(this.ownerClientId);
            return entity instanceof LivingEntity living && living != this ? living : null;
        }
        return this.ownerId == null ? null : this.ownerCache.get(this.level(), this.ownerId);
    }

    @Override
    @Nullable
    public UUID getOwnerId() {
        return this.ownerId;
    }

    /** OwnableEntity, as Goety's own servants: what the Esoteric Tesseract and other owner checks look for. */
    @Override
    @Nullable
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        return this.getTrueOwner();
    }

    @Override
    public void setOwnerId(@Nullable UUID ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public int getOwnerClientId() {
        return this.ownerClientId;
    }

    @Override
    public void setOwnerClientId(int id) {
        this.ownerClientId = id;
    }

    @Override
    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }

    @Override
    public boolean isHostile() {
        return this.ownerId == null || this.hostile;
    }

    /**
     * As Goety's own check, without its last rule: a servant made from a monster is still a monster to the game, yet it
     * turns hostile only through its owner. Without this, a servant saved as friendly would load as hostile.
     */
    @Override
    public void checkHostility() {
        if (!this.level().isClientSide) {
            LivingEntity owner = this.getTrueOwner();
            if (owner instanceof Enemy || (owner instanceof IOwned owned && owned.isHostile())) {
                this.setHostile(true);
            }
        }
    }

    @Override
    public void setNatural(boolean natural) {
        this.natural = natural;
    }

    @Override
    public boolean isNatural() {
        return this.natural;
    }

    @Override
    public void setHasLifespan(boolean lifespan) {
        this.hasLifespan = lifespan;
    }

    @Override
    public boolean hasLifespan() {
        return this.hasLifespan;
    }

    @Override
    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    @Override
    public int getLifespan() {
        return this.lifespan;
    }

    @Override
    public int getHasSummonCheck() {
        return this.hasSummonCheck;
    }

    @Override
    public void setHasSummonCheck(int count) {
        this.hasSummonCheck = count;
    }

    @Override
    public boolean isWandering() {
        return this.wandering;
    }

    @Override
    public void setWandering(boolean wandering) {
        this.wandering = wandering;
    }

    @Override
    public boolean isStaying() {
        return this.staying && !this.isCommanded();
    }

    @Override
    public void setStaying(boolean staying) {
        this.staying = staying;
    }

    @Override
    public boolean isUpgraded() {
        return this.upgraded;
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    @Override
    @Nullable
    public BlockPos getBoundPos() {
        return this.boundPos;
    }

    @Override
    public void setBoundPos(@Nullable BlockPos pos) {
        this.boundPos = pos;
        if (pos != null) {
            this.setBoundDim(this.level().dimension());
        }
    }

    @Override
    public String getBoundDim() {
        return this.boundDim;
    }

    @Override
    public void setBoundDim(String dimension) {
        this.boundDim = dimension;
    }

    @Override
    public boolean isCommanded() {
        return this.commandPos != null;
    }

    @Override
    @Nullable
    public BlockPos getCommandPos() {
        return this.commandPos;
    }

    @Override
    public void setCommandPos(@Nullable BlockPos pos) {
        this.setCommandPos(pos, true);
    }

    @Override
    public void setCommandPos(@Nullable BlockPos pos, boolean clearEntity) {
        if (clearEntity) {
            this.commandEntity = null;
        }
        this.commandPos = pos;
        this.commandTick = pos == null ? 0 : 200;
    }

    @Override
    public void setCommandPosEntity(@Nullable LivingEntity entity) {
        this.commandEntity = entity;
        if (entity != null) {
            this.setCommandPos(entity.blockPosition(), false);
        }
    }

    @Override
    @Nullable
    public LivingEntity getCommandPosEntity() {
        return this.commandEntity;
    }

    @Override
    public int getCommandTick() {
        return this.commandTick;
    }

    @Override
    public void setCommandTick(int time) {
        this.commandTick = time;
    }

    @Override
    public int getNoHealTime() {
        return this.noHealTime;
    }

    @Override
    public void setNoHealTime(int time) {
        this.noHealTime = time;
    }

    @Override
    public int getKillChance() {
        return this.killChance;
    }

    @Override
    public void setKillChance(int time) {
        this.killChance = time;
    }

    @Override
    public long getTicketTime() {
        return this.ticketTime;
    }

    @Override
    public void setTicketTime(long time) {
        this.ticketTime = time;
    }

    @Override
    @Nullable
    public LivingEntity getPriorityTarget() {
        return this.priorityTarget;
    }

    @Override
    public void setPriorityTarget(@Nullable LivingEntity target) {
        this.priorityTarget = target;
        this.overrideSetTarget(target);
        if (target != null) {
            this.priorityTime = 100;
            this.priorityPos = target.blockPosition();
        }
    }

    @Override
    public int getPriorityTime() {
        return this.priorityTime;
    }

    @Override
    public void setPriorityTime(int time) {
        this.priorityTime = time;
    }

    @Override
    @Nullable
    public BlockPos getPriorityPos() {
        return this.priorityPos;
    }

    @Override
    public void setPriorityPos(@Nullable BlockPos pos) {
        this.priorityPos = pos;
    }

    @Override
    public void overrideSetTarget(@Nullable LivingEntity target) {
        this.setTarget(target);
    }

    @Override
    public void tryKill(Player player) {
        this.hurt(ModDamageSource.getDamageSource(this.level(), ModDamageSource.DISMISSED), Float.MAX_VALUE);
    }
}
