package com.Polarice3.Goety.common.entities.ally.golem;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class RedstoneMinistrosity extends StoneMinistrosity {
    
    public RedstoneMinistrosity(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.get(AttributesConfig.RedstoneMinistrosityHealth))
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.get(AttributesConfig.RedstoneMinistrosityDamage))
                .add(Attributes.ARMOR, AttributesConfig.get(AttributesConfig.RedstoneMinistrosityArmor))
                .add(Attributes.STEP_HEIGHT, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.get(AttributesConfig.RedstoneMinistrosityFollowRange));
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.get(AttributesConfig.RedstoneMinistrosityHealth));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.get(AttributesConfig.RedstoneMinistrosityArmor));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.get(AttributesConfig.RedstoneMinistrosityDamage));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.get(AttributesConfig.RedstoneMinistrosityFollowRange));
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MINISTROSITY_REDSTONE_IDLE.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.MINISTROSITY_REDSTONE_IDLE.get();
    }

}
