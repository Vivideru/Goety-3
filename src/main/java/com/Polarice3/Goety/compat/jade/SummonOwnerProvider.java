package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.util.CommonProxy;

import java.util.Optional;
import java.util.UUID;

public enum SummonOwnerProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, String> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        String name = this.decodeFromData(accessor).filter(ownerName -> !ownerName.isEmpty()).or(() -> this.resolveOwnerName(accessor)).orElse("");
        if (!name.isEmpty()) {
            Component ownerLine = Component.translatable("jade.owner", name);
            if (!tooltip.replace(this.getUid(), ownerLine)) {
                tooltip.add(ownerLine, this.getUid());
            }
        }
    }

    @Override
    public String streamData(EntityAccessor accessor) {
        return this.resolveOwnerName(accessor).orElse("");
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, String> streamCodec() {
        return ByteBufCodecs.STRING_UTF8.cast();
    }

    private Optional<String> resolveOwnerName(EntityAccessor accessor) {
        Entity entity = accessor.getEntity();
        UUID ownerUUID = null;
        int ownerID = -1;
        LivingEntity resolvedOwner = null;

        if (entity instanceof IOwned owned) {
            resolvedOwner = owned.getMasterOwner();
            ownerUUID = resolvedOwner != null ? resolvedOwner.getUUID() : owned.getOwnerId();
            ownerID = resolvedOwner != null ? resolvedOwner.getId() : owned.getOwnerClientId();
        } else if (entity instanceof OwnableEntity ownable) {
            ownerUUID = ownable.getOwnerUUID();
            if (ownable.getOwner() != null) {
                resolvedOwner = ownable.getOwner();
                ownerID = resolvedOwner.getId();
            }
        }

        if (resolvedOwner instanceof IOwned owned && owned.getMasterOwner() != null) {
            // Summons created by summons should display the effective master owner instead of the intermediate mob.
            resolvedOwner = owned.getMasterOwner();
            ownerUUID = resolvedOwner.getUUID();
            ownerID = resolvedOwner.getId();
        }

        if (resolvedOwner != null) {
            return Optional.of(resolvedOwner.getDisplayName().getString());
        }

        if (ownerUUID != null) {
            String name = CommonProxy.getLastKnownUsername(ownerUUID);
            if (name != null) {
                return Optional.of(name);
            }
            Entity entity1 = EntityFinder.getEntityByUuiD(accessor.getLevel(), ownerUUID);
            if (entity1 == null){
                entity1 = accessor.getLevel().getEntity(ownerID);
            }
            if (entity1 != null){
                return Optional.of(entity1.getDisplayName().getString());
            }
        }

        return Optional.empty();
    }

    @Override
    public ResourceLocation getUid() {
        return JadeIds.MC_ANIMAL_OWNER;
    }
}
