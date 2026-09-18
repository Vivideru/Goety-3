package com.Vivideru.Goety.common.entities.ally;

import com.Polarice3.Goety.api.entities.IBreathing;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.BreathingAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.FireBreathSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemBlockEntity;
import com.Vivideru.Goety.common.blocks.entities.WolfTotemHooks;
import com.Vivideru.Goety.common.world.CerberusTotemData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Cerberus extends Warg implements IBreathing {
    private static final EntityDataAccessor<Boolean> BREATHING = SynchedEntityData.defineId(Cerberus.class, EntityDataSerializers.BOOLEAN);
    private static final int BREATH_POTENCY = 5;
    private static final int BREATH_RANGE = 8;
    private static final int MIN_BREATH_TICKS = 80;
    private static final int MAX_BREATH_TICKS = 100;
    private static final int BREATH_COOLDOWN_TICKS = 300;
    private static final int LOOP_SOUND_INTERVAL_TICKS = 20;
    /** The side heads sit at a fixed 35 degrees in the model, so their flame leaves along the same angle. */
    private static final double HEAD_YAW_SPREAD = Math.toRadians(35.0D);
    /** Muzzle offsets follow the rendered model, which is deliberately larger than the hitbox. */
    private static final double MUZZLE_HEIGHT = 2.2D;
    private static final double MUZZLE_FORWARD = 2.2D;

    private final FireBreathSpell breathSpell = new FireBreathSpell();

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState groundedAnimationState = new AnimationState();
    public final AnimationState jumpAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState fireBreathAnimationState = new AnimationState();

    public Cerberus(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new CerberusBreathGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.STEP_HEIGHT, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.get(AttributesConfig.CerberusHealth))
                .add(Attributes.ARMOR, AttributesConfig.get(AttributesConfig.CerberusArmor))
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.get(AttributesConfig.CerberusDamage));
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.get(AttributesConfig.CerberusHealth));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.get(AttributesConfig.CerberusArmor));
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.get(AttributesConfig.CerberusDamage));
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && armor != null && attack != null) {
            health.setBaseValue(AttributesConfig.get(AttributesConfig.CerberusHealth) * (upgraded ? 1.5D : 1.0D));
            armor.setBaseValue(AttributesConfig.get(AttributesConfig.CerberusArmor) + (upgraded ? 1.0D : 0.0D));
            attack.setBaseValue(AttributesConfig.get(AttributesConfig.CerberusDamage) + (upgraded ? 1.0D : 0.0D));
        }
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BREATHING, false);
    }

    @Override
    public void curseTarget(Entity entity) {
        // The three heads are literally made of Hellhound, so every bite carries the same ignite.
        if (!entity.fireImmune()) {
            entity.igniteForSeconds(6.0F);
        }
    }

    @Override
    public boolean canUseSlot(EquipmentSlot slot) {
        // Cerberus fights with its three mouths and its fire, never a held weapon.
        return slot != EquipmentSlot.MAINHAND && super.canUseSlot(slot);
    }

    @Override
    public boolean isInvisible() {
        // Whatever the source - potion, spell or hidden owner - the hound of the underworld is always seen.
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // Refuse the Saddle before the inherited Warg handling can fit it and swallow the item.
        if (player.getItemInHand(hand).is(Items.SADDLE)) {
            return InteractionResult.PASS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        // No rider, even if a Saddle ever made it on through some other route.
        return false;
    }

    @Override
    public ModMobType getGoetyMobType() {
        // Counting as a Nether mob is what lets Nether Robes mend it, like every other Nether servant.
        return ModMobType.NETHER;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        // Runs the inherited saddle-drop and generic Totem servant-list cleanup before releasing the Cerberus-specific slot.
        super.dropCustomDeathLoot(level, source, recentlyHit);
        this.releaseCerberusTotemSlot(level);
    }

    @Override
    public void dismiss() {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.releaseCerberusTotemSlot(serverLevel);
        }
        super.dismiss();
    }

    private void releaseCerberusTotemSlot(ServerLevel level) {
        WolfTotemBlockEntity totem = WolfTotemHooks.getTotem((LivingEntity) this);
        if (totem != null) {
            totem.releaseCerberus(this.getUUID());
        }
        CerberusTotemData.get(level).unregister(this.getUUID());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.updateCerberusAnimationStates();
            if (this.isBreathing()) {
                this.spawnBreathParticles();
            }
        }
    }

    /**
     * Breath particles belong on the client, like every other Goety breathing caster: always-visible particles
     * survive the client's particle limiter. Each of the three heads spits along its own axis, matching the
     * fixed yaw the model gives them, and starts clear of the hitbox rather than inside the geometry.
     */
    private void spawnBreathParticles() {
        Vec3 look = this.getLookAngle();
        // The muzzles are anchored at head height with a flat forward offset: folding the downward look angle
        // into the spawn point put the flames at ground level, where DragonFlameParticle deletes itself on contact.
        Vec3 flatLook = new Vec3(look.x, 0.0D, look.z);
        Vec3 forwardAxis = flatLook.lengthSqr() < 1.0E-4D ? Vec3.directionFromRotation(0.0F, this.getYRot()) : flatLook.normalize();
        double muzzleY = this.getY() + MUZZLE_HEIGHT;
        double forward = MUZZLE_FORWARD;
        for (int head = -1; head <= 1; ++head) {
            Vec3 placement = rotateAroundY(forwardAxis, head * HEAD_YAW_SPREAD);
            Vec3 direction = rotateAroundY(look, head * HEAD_YAW_SPREAD);
            double muzzleX = this.getX() + placement.x * forward;
            double muzzleZ = this.getZ() + placement.z * forward;
            for (int i = 0; i < 6; ++i) {
                Vec3 spread = new Vec3(this.random.nextDouble() - 0.5D, this.random.nextDouble() - 0.5D, this.random.nextDouble() - 0.5D);
                Vec3 velocity = direction.scale(3.0D).add(spread).normalize().scale(0.6D + this.random.nextDouble() * 0.4D);
                this.level().addAlwaysVisibleParticle(ModParticleTypes.DRAGON_FLAME.get(),
                        muzzleX, muzzleY, muzzleZ, velocity.x, velocity.y, velocity.z);
            }
        }
    }

    private static Vec3 rotateAroundY(Vec3 vector, double radians) {
        if (radians == 0.0D) {
            return vector;
        }
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new Vec3(vector.x * cos - vector.z * sin, vector.y, vector.x * sin + vector.z * cos);
    }

    private SpellStat breathStats() {
        return this.breathSpell.defaultStats().setPotency(BREATH_POTENCY).setRange(BREATH_RANGE);
    }

    @Override
    public boolean isBreathing() {
        return this.entityData.get(BREATHING);
    }

    @Override
    public void setBreathing(boolean flag) {
        this.entityData.set(BREATHING, flag);
    }

    @Override
    public void doBreathing(Entity target) {
        // Same result the Fire Breath spell applies per target, cast at potency 5.
        SpellStat stats = this.breathStats();
        float damage = SpellConfig.FireBreathDamage.get().floatValue() * WandUtil.damageMultiply() + stats.getPotency();
        if (target.hurt(ModDamageSource.fireBreath(this, this), damage)) {
            target.igniteForSeconds(5 * stats.getBurning());
        }
    }

    private void updateCerberusAnimationStates() {
        boolean breathing = this.isBreathing();
        boolean biting = !breathing && this.getAttackTicks() > 0 && this.getAttackType() == ATTACK_BITE;
        boolean airborne = this.tickAirborneAnimation();
        boolean grounded = !breathing && !biting && !airborne;
        setAnimation(this.fireBreathAnimationState, breathing);
        setAnimation(this.biteAnimationState, biting);
        setAnimation(this.jumpAnimationState, !breathing && !biting && airborne);
        setAnimation(this.groundedAnimationState, grounded && this.isSitting());
        setAnimation(this.walkAnimationState, grounded && !this.isSitting() && this.walkAnimation.speed() > 0.05F);
        setAnimation(this.idleAnimationState, grounded && !this.isSitting() && this.walkAnimation.speed() <= 0.05F);
    }

    private void setAnimation(AnimationState state, boolean running) {
        if (running) {
            state.startIfStopped(this.tickCount);
        } else {
            state.stop();
        }
    }

    /**
     * Goety's own breathing attack goal supplies the aiming, body/pitch rotation and per-tick target sweep; this
     * subclass only adds the Fire Breath spell's casting sounds and the cooldown between breaths.
     */
    private class CerberusBreathGoal extends BreathingAttackGoal<Cerberus> {
        private int cooldownTicks;

        private CerberusBreathGoal() {
            super(Cerberus.this, BREATH_RANGE, MAX_BREATH_TICKS, 1.0F);
        }

        @Override
        public boolean canUse() {
            if (this.cooldownTicks > 0) {
                --this.cooldownTicks;
                return false;
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            // A dead target ends the breath immediately and starts the cooldown, rather than spewing at a corpse.
            LivingEntity target = Cerberus.this.getTarget();
            return target != null && target.isAlive() && this.attackTarget == target && super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
            this.durationLeft = MIN_BREATH_TICKS + Cerberus.this.getRandom().nextInt(MAX_BREATH_TICKS - MIN_BREATH_TICKS + 1);
            Cerberus.this.playSound(Cerberus.this.breathSpell.CastingSound(), 2.0F, 1.0F);
        }

        @Override
        public void stop() {
            super.stop();
            this.cooldownTicks = BREATH_COOLDOWN_TICKS;
        }
    }
}
