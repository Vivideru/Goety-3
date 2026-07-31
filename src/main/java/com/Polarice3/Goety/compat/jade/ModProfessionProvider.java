package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.util.CommonProxy;

public enum ModProfessionProvider implements IEntityComponentProvider {
    INSTANCE;

    private static final Component LEVEL_SEPARATOR = Component.literal(" - ");

    private ModProfessionProvider() {
    }

    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        VillagerData data = null;
        if (accessor.getEntity() instanceof ZombieVillagerServant zombieVillagerServant) {
            data = zombieVillagerServant.getVillagerData();
        }

        if (data != null) {
            int level = data.getLevel();
            VillagerProfession profession = data.getProfession();
            MutableComponent component = CommonProxy.getProfessionName(profession);
            if (profession != VillagerProfession.NONE && profession != VillagerProfession.NITWIT && level > 0 && level <= 5) {
                component.append(LEVEL_SEPARATOR).append(Component.translatable("merchant.level." + level));
            }

            tooltip.add(component);
        }
    }

    public ResourceLocation getUid() {
        return Goety.location("villager_profession");
    }
}
