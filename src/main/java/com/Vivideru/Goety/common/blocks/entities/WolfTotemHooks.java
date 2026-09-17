package com.Vivideru.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.Hellhound;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.Stormhound;
import com.Polarice3.Goety.common.entities.ally.WinterWolf;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Vivideru.Goety.common.entities.VivideruEntityTypes;
import com.Vivideru.Goety.common.entities.ally.Cerberus;
import com.Vivideru.Goety.common.entities.ally.Warg;
import com.Vivideru.Goety.common.world.CerberusTotemData;
import com.Vivideru.Goety.common.world.WargTotemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.UUID;

public class WolfTotemHooks {
    public static final int REVIVE_COST = 100;
    private static final double TOTEM_FUSION_RANGE = 8.0D;
    private static final int TOTEM_SEARCH_VERTICAL = 4;
    public static final TagKey<EntityType<?>> WOLF_TOTEM_SERVANTS = TagKey.create(Registries.ENTITY_TYPE, Goety.location("wolf_totem_servants"));

    public static InteractionResult tryLinkToTotem(ItemStackAccess stackAccess, Player player, LivingEntity entity, InteractionHand hand) {
        if (!(entity instanceof IOwned owned) || owned.getTrueOwner() != player || !canUseTotem(entity)) {
            return InteractionResult.PASS;
        }
        if (!stackAccess.isWaystoneBound() || !WaystoneItem.isSameDimension(entity, stackAccess.stack())) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = WaystoneItem.getBlockEntity(stackAccess.stack(), entity.level());
        if (!(blockEntity instanceof WolfTotemBlockEntity totem) || totem.getTrueOwner() != player || !totem.hasSpace()) {
            return InteractionResult.PASS;
        }
        if (entity instanceof Warg && owned.getRevivePos() != null) {
            // Wargs keep their original Totem identity even if the physical block is later removed.
            return InteractionResult.FAIL;
        }
        if (!entity.level().isClientSide) {
            if (entity instanceof Warg && (totem.hasCreatedWarg()
                    || !WargTotemData.get((ServerLevel) entity.level()).canCreate(player.getUUID(), entity.level().dimension(), totem.getBlockPos()))) {
                return InteractionResult.FAIL;
            }
            if (isWargPromotionCandidate(entity) && tryTransformWarg(player, entity, totem, hand)) {
                return InteractionResult.SUCCESS;
            }
            WolfTotemBlockEntity oldTotem = getTotem(owned);
            if (oldTotem != null && oldTotem != totem) {
                oldTotem.removeServant(entity);
                oldTotem.markUpdated();
            }
            BlockPos blockPos = totem.getBlockPos();
            totem.addServant(entity);
            owned.setRevivePos(blockPos);
            owned.setReviveDim(entity.level().dimension());
            if (owned instanceof IServant servant) {
                servant.setWandering(false);
                servant.setStaying(false);
            }
            if (entity instanceof Warg warg && entity.level() instanceof ServerLevel serverLevel) {
                totem.setCreatedWarg(warg.getUUID());
                WargTotemData.get(serverLevel).register(warg.getUUID(), player.getUUID(), serverLevel.dimension(), totem.getBlockPos());
            }
            entity.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
            player.displayClientMessage(Component.translatable("info.goety.servant.guard", entity.getDisplayName()), true);
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean isWargPromotionCandidate(LivingEntity entity) {
        return entity instanceof SkeletonWolf
                || entity instanceof BlackWolf wolf && !(wolf instanceof Hellhound) && !(wolf instanceof Warg);
    }

    private static boolean tryTransformWarg(Player player, LivingEntity wolf,
                                            WolfTotemBlockEntity totem, InteractionHand hand) {
        if (!(wolf.level() instanceof ServerLevel serverLevel) || totem.hasCreatedWarg()) {
            return false;
        }
        UUID ownerId = player.getUUID();
        long nearbyWolves = serverLevel.getEntitiesOfClass(LivingEntity.class,
                        new net.minecraft.world.phys.AABB(totem.getBlockPos()).inflate(8.0D),
                        living -> (living instanceof BlackWolf || living instanceof SkeletonWolf)
                                && !(living instanceof Warg)
                                && living instanceof IOwned nearbyOwned && nearbyOwned.getTrueOwner() == player)
                .size();
        if (nearbyWolves < 4 || !WargTotemData.get(serverLevel).canCreate(ownerId, serverLevel.dimension(), totem.getBlockPos())) {
            return false;
        }
        Warg warg = VivideruEntityTypes.WARG.get().create(serverLevel);
        if (warg == null) {
            return false;
        }
        // Keep the source name before replacing the wolf so the promotion message identifies what changed.
        Component sourceName = wolf.getDisplayName().copy();

        WolfTotemBlockEntity oldTotem = getTotem((LivingEntity) wolf);
        if (oldTotem != null) {
            oldTotem.removeServant(wolf);
            oldTotem.markUpdated();
        }
        ItemStack armor = wolf instanceof BlackWolf blackWolf ? blackWolf.getBodyArmorItem()
                : wolf instanceof SkeletonWolf skeletonWolf ? skeletonWolf.getBodyArmorItem() : ItemStack.EMPTY;
        if (!armor.isEmpty()) {
            wolf.spawnAtLocation(armor.copy());
        }
        warg.moveTo(wolf.getX(), wolf.getY(), wolf.getZ(), wolf.getYRot(), wolf.getXRot());
        warg.setTrueOwner(player);
        warg.setVariant(wolf instanceof SkeletonWolf ? Warg.Variant.SKELETAL
                : wolf instanceof WinterWolf ? Warg.Variant.COLD
                : wolf instanceof Stormhound ? Warg.Variant.MODERATE : Warg.Variant.BLACK);
        if (wolf instanceof Summoned summoned) {
            warg.setUpgraded(summoned.isUpgraded());
            warg.setHostile(summoned.isHostile());
        }
        if (wolf.hasCustomName()) {
            warg.setCustomName(wolf.getCustomName());
            warg.setCustomNameVisible(wolf.isCustomNameVisible());
        }
        warg.setRevivePos(totem.getBlockPos());
        warg.setReviveDim(serverLevel.dimension());
        warg.setWandering(false);
        warg.setStaying(false);
        serverLevel.addFreshEntity(warg);
        totem.addServant(warg);
        totem.setCreatedWarg(warg.getUUID());
        WargTotemData.get(serverLevel).register(warg.getUUID(), ownerId, serverLevel.dimension(), totem.getBlockPos());
        wolf.discard();
        warg.playSound(SoundEvents.WOLF_HOWL, 0.20F, 0.65F);
        player.swing(hand);
        player.displayClientMessage(Component.translatable("info.goety.warg.created", sourceName), true);
        return true;
    }

    /**
     * Three Hellhounds gathered at a Howling Totem fuse into one Cerberus when their owner offers a Netherite
     * Ingot to one of them: the offered Hellhound becomes the Cerberus and the other two are consumed outright,
     * vanishing in a puff of smoke.
     */
    public static boolean tryFuseCerberus(Player player, LivingEntity hellhound, InteractionHand hand) {
        if (!(hellhound.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        WolfTotemBlockEntity totem = findNearbyTotem(serverLevel, player, hellhound.blockPosition());
        if (totem == null || totem.hasCreatedCerberus() || !totem.hasSpace()) {
            return false;
        }
        UUID ownerId = player.getUUID();
        List<Hellhound> nearbyHellhounds = serverLevel.getEntitiesOfClass(Hellhound.class,
                new net.minecraft.world.phys.AABB(totem.getBlockPos()).inflate(TOTEM_FUSION_RANGE),
                living -> living instanceof IOwned nearbyOwned && nearbyOwned.getTrueOwner() == player);
        if (nearbyHellhounds.size() < 3
                || !CerberusTotemData.get(serverLevel).canCreate(ownerId, serverLevel.dimension(), totem.getBlockPos())) {
            return false;
        }
        Cerberus cerberus = VivideruEntityTypes.CERBERUS.get().create(serverLevel);
        if (cerberus == null) {
            return false;
        }
        Component sourceName = hellhound.getDisplayName().copy();

        // Consume two further Hellhounds outright; only the interacted one visibly becomes the Cerberus.
        int consumed = 0;
        for (Hellhound extra : nearbyHellhounds) {
            if (consumed >= 2) {
                break;
            }
            if (extra == hellhound) {
                continue;
            }
            WolfTotemBlockEntity extraTotem = getTotem((LivingEntity) extra);
            if (extraTotem != null) {
                extraTotem.removeServant(extra);
                extraTotem.markUpdated();
            }
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, extra.getX(), extra.getY() + extra.getBbHeight() * 0.5D, extra.getZ(),
                    12, extra.getBbWidth() * 0.5D, extra.getBbHeight() * 0.5D, extra.getBbWidth() * 0.5D, 0.02D);
            extra.discard();
            ++consumed;
        }

        WolfTotemBlockEntity oldTotem = getTotem((LivingEntity) hellhound);
        if (oldTotem != null) {
            oldTotem.removeServant(hellhound);
            oldTotem.markUpdated();
        }
        if (hellhound instanceof BlackWolf blackWolf && !blackWolf.getBodyArmorItem().isEmpty()) {
            hellhound.spawnAtLocation(blackWolf.getBodyArmorItem().copy());
        }
        cerberus.moveTo(hellhound.getX(), hellhound.getY(), hellhound.getZ(), hellhound.getYRot(), hellhound.getXRot());
        cerberus.setTrueOwner(player);
        if (hellhound instanceof Summoned summoned) {
            cerberus.setUpgraded(summoned.isUpgraded());
            cerberus.setHostile(summoned.isHostile());
        }
        if (hellhound.hasCustomName()) {
            cerberus.setCustomName(hellhound.getCustomName());
            cerberus.setCustomNameVisible(hellhound.isCustomNameVisible());
        }
        cerberus.setRevivePos(totem.getBlockPos());
        cerberus.setReviveDim(serverLevel.dimension());
        cerberus.setWandering(false);
        cerberus.setStaying(false);
        serverLevel.addFreshEntity(cerberus);
        totem.addServant(cerberus);
        totem.setCreatedCerberus(cerberus.getUUID());
        CerberusTotemData.get(serverLevel).register(cerberus.getUUID(), ownerId, serverLevel.dimension(), totem.getBlockPos());
        hellhound.discard();
        cerberus.playSound(SoundEvents.WOLF_HOWL, 0.20F, 0.5F);
        player.swing(hand);
        player.displayClientMessage(Component.translatable("info.goety.cerberus.created", sourceName), true);
        return true;
    }

    private static WolfTotemBlockEntity findNearbyTotem(ServerLevel level, Player player, BlockPos origin) {
        int range = (int) TOTEM_FUSION_RANGE;
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-range, -TOTEM_SEARCH_VERTICAL, -range),
                origin.offset(range, TOTEM_SEARCH_VERTICAL, range))) {
            if (level.getBlockEntity(pos) instanceof WolfTotemBlockEntity totem && totem.getTrueOwner() == player) {
                return totem;
            }
        }
        return null;
    }

    public static boolean canRevive(LivingEntity entity, DamageSource damageSource) {
        if (damageSource.is(ModDamageSource.DISMISSED) || !canUseTotem(entity) || entity.hasEffect(com.Polarice3.Goety.common.effects.GoetyEffects.WOUNDED)) {
            return false;
        }
        WolfTotemBlockEntity totem = getTotem(entity);
        return totem != null && entity.level().dimension() == ((IOwned) entity).getReviveLevel() && totem.getSoulEnergy() >= REVIVE_COST;
    }

    public static void onRevive(LivingEntity entity) {
        WolfTotemBlockEntity totem = getTotem(entity);
        if (totem == null) {
            return;
        }
        if (entity instanceof Mob mob) {
            mob.setTarget(null);
        }
        entity.level().broadcastEntityEvent(entity, (byte) 35);
        entity.addEffect(new MobEffectInstance(com.Polarice3.Goety.common.effects.GoetyEffects.WOUNDED, com.Polarice3.Goety.utils.MathHelper.minecraftDayToTicks(1)));
        entity.addEffect(new MobEffectInstance(com.Polarice3.Goety.common.effects.GoetyEffects.CRIPPLED, com.Polarice3.Goety.utils.MathHelper.minutesToTicks(5)));
        totem.siphonSoulEnergy(REVIVE_COST);
    }

    public static boolean isAssignedToWolfTotem(LivingEntity entity) {
        return entity instanceof IOwned owned && getTotem(owned) != null;
    }

    public static boolean canUseTotem(LivingEntity entity) {
        return entity.getType().is(WOLF_TOTEM_SERVANTS)
                || entity instanceof BlackWolf
                || entity instanceof Warg
                || entity instanceof SkeletonWolf
                || entity instanceof BlackBeast;
    }

    public static WolfTotemBlockEntity getTotem(LivingEntity entity) {
        if (entity instanceof IOwned owned) {
            return getTotem(owned);
        }
        return null;
    }

    public static WolfTotemBlockEntity getTotem(IOwned owned) {
        if (!(owned instanceof Entity entity) || owned.getRevivePos() == null || entity.getServer() == null) {
            return null;
        }
        for (Level level : entity.getServer().getAllLevels()) {
            if (level.dimension() == owned.getReviveLevel()) {
                BlockEntity blockEntity = level.getBlockEntity(owned.getRevivePos());
                if (blockEntity instanceof WolfTotemBlockEntity totem && totem.getTrueOwner() == owned.getTrueOwner()) {
                    return totem;
                }
            }
        }
        return null;
    }

    public record ItemStackAccess(net.minecraft.world.item.ItemStack stack) {
        public boolean isWaystoneBound() {
            return this.stack.is(ModItems.WAYSTONE.get()) && WaystoneItem.hasBlock(this.stack);
        }
    }
}
