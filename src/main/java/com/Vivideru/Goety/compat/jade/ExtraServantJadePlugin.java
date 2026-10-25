package com.Vivideru.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.compat.jade.HostileIndicatorProvider;
import com.Polarice3.Goety.compat.jade.ServantModeProvider;
import com.Polarice3.Goety.compat.jade.SummonLifespanProvider;
import com.Polarice3.Goety.compat.jade.SummonOwnerProvider;
import com.Vivideru.Goety.common.entities.extra.ExtraServant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.JadeIds;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/**
 * Jade lines for extra servants. Goety registers its servant providers on its own servant classes, and Jade looks providers
 * up along the superclasses only, so a servant made from another mob never meets them. This provider sits on every mob,
 * does nothing for the others and gives extra servants the owner, hostility, lifespan and stance lines Goety's servants
 * show, each still switched by its own Jade option.
 */
@WailaPlugin
public class ExtraServantJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(Provider.INSTANCE, Mob.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(Provider.INSTANCE, Mob.class);
    }

    public enum Provider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
        INSTANCE;

        private static final String OWNER = "ExtraServantOwner";

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (!(accessor.getEntity() instanceof ExtraServant)) {
                return;
            }
            String owner = accessor.getServerData().getString(OWNER);
            if (!owner.isEmpty() && config.get(SummonOwnerProvider.INSTANCE.getUid())) {
                Component line = Component.translatable("jade.owner", owner);
                if (!tooltip.replace(JadeIds.MC_ANIMAL_OWNER, line)) {
                    tooltip.add(line, JadeIds.MC_ANIMAL_OWNER);
                }
            }
            if (config.get(HostileIndicatorProvider.INSTANCE.getUid())) {
                HostileIndicatorProvider.INSTANCE.appendTooltip(tooltip, accessor, config);
            }
            if (config.get(SummonLifespanProvider.INSTANCE.getUid())) {
                SummonLifespanProvider.INSTANCE.appendTooltip(tooltip, accessor, config);
            }
            if (config.get(ServantModeProvider.INSTANCE.getUid())) {
                ServantModeProvider.INSTANCE.appendTooltip(tooltip, accessor, config);
            }
        }

        @Override
        public void appendServerData(CompoundTag data, EntityAccessor accessor) {
            if (!(accessor.getEntity() instanceof ExtraServant)) {
                return;
            }
            data.putString(OWNER, SummonOwnerProvider.INSTANCE.streamData(accessor));
            HostileIndicatorProvider.INSTANCE.appendServerData(data, accessor);
            SummonLifespanProvider.INSTANCE.appendServerData(data, accessor);
            ServantModeProvider.INSTANCE.appendServerData(data, accessor);
        }

        @Override
        public ResourceLocation getUid() {
            return Goety.location("extra_servant");
        }
    }
}
