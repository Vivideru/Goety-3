package com.Vivideru.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Vivideru.Goety.common.entities.neutral.AbstractHurricane;
import com.Vivideru.Goety.common.items.VivideruItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class HurricaneServant extends AbstractHurricane {

    public HurricaneServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    @Override
    public int xpReward() {
        return 20;
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof HurricaneServant;
    }

    @Override
    public void tryKill(Player player) {
        if (this.getKillChance() <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.get(SpellConfig.HurricaneLimit);
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide && this.getTrueOwner() != null && MobsConfig.HurricaneServantCore.get()) {
            ItemStack core = new ItemStack(VivideruItems.HURRICANE_CORE.get());
            ReviveServantItem.setOwnerName(this.getTrueOwner(), core);
            ReviveServantItem.setSummon(this, core);
            // Like the other revival items: it floats up, flies back to the owner and arrives on cooldown.
            FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(), this.getX(), this.getY(), this.getZ());
            flyingItem.setOwner(this.getTrueOwner());
            flyingItem.setItem(core);
            flyingItem.setParticle(ParticleTypes.CLOUD);
            flyingItem.setSecondsCool(ItemConfig.ReviveSecondsCool.get());
            this.level().addFreshEntity(flyingItem);
        }
        super.die(cause);
    }
}
