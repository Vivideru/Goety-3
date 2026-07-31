package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.utils.MobTypeHelper;

import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import com.Polarice3.Goety.utils.GoetyMaceUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Objects;

public class HammerItem extends TieredItem {
    private static final float DEFAULT_BASE_DAMAGE = 5.0F;
    private static final double DEFAULT_ATTACK_SPEED = 0.5D;
    private static final int DEFAULT_DURABILITY = 2500;
    private static final float MACE_DAMAGE_SCALE = 0.85F;
    private static float initialDamage = DEFAULT_BASE_DAMAGE;
    protected final float speed;

    public HammerItem(Tier itemTier) {
        // Item properties and attributes are built during registration before configs load, so use the declared defaults here.
        super(itemTier, new Properties().rarity(Rarity.UNCOMMON).durability(DEFAULT_DURABILITY).attributes(createHammerAttributes(itemTier)));
        this.speed = itemTier.getSpeed() - 2.0F;
    }

    private static ItemAttributeModifiers createHammerAttributes(Tier itemTier) {
        initialDamage = DEFAULT_BASE_DAMAGE + itemTier.getAttackDamageBonus();
        double attackSpeed = 4.0D - DEFAULT_ATTACK_SPEED;
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_DAMAGE_ID, initialDamage - 1.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, com.Polarice3.Goety.utils.ModAttributeUtil.create(BASE_ATTACK_SPEED_ID, -attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public HammerItem(){
        this(Tiers.IRON);
    }

    public static float getInitialDamage() {
        return initialDamage;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        return ConfiguredItemUtil.durability(ItemConfig.HammerDurability, DEFAULT_DURABILITY);
    }

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, EquipmentSlot.MAINHAND);
        GoetyMaceUtil.trySmashEffects(pStack, pTarget, pAttacker);
        if (pAttacker instanceof Player player){
            float f2 = player.getAttackStrengthScale(0.5F);
            if (f2 > 0.9F){
                this.attackMobs(pTarget, player, pStack);
                this.smash(pStack, pTarget, player);
            }
        }
        return true;
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        // Hammers keep their Goety AOE, but 1.21.1 treats them as lighter maces with slightly lower fall-damage scaling.
        return GoetyMaceUtil.getAttackDamageBonus(target, damageSource, MACE_DAMAGE_SCALE);
    }

    public void smash(ItemStack pStack, LivingEntity pTarget, Player player){
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.HAMMER_SWING.get(), player.getSoundSource(), 1.0F, 1.0F);
        if (pTarget.onGround()) {
            player.level().playSound(null, pTarget.getX(), pTarget.getY(), pTarget.getZ(), ModSounds.DIRT_DEBRIS.get(), player.getSoundSource(), 1.0F, 1.0F);
        }
        if (player.level() instanceof ServerLevel serverLevel){
            BlockPos blockPos = BlockPos.containing(pTarget.getX(), pTarget.getY() - 1.0F, pTarget.getZ());
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
            float area = 1.75F;
            area += MobUtil.getItemEnchantmentLevel(player, pStack, ModEnchantments.RADIUS);
            for (int i = 0; i < 8; ++i) {
                ServerParticleUtil.circularParticles(serverLevel, option, pTarget.getX(), pTarget.getY() + 0.25D, pTarget.getZ(), area);
            }
            int color = serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col;
            ColorUtil colorUtil = color == 0 ? ColorUtil.WHITE : new ColorUtil(color);
            serverLevel.sendParticles(new SmashParticleOption(colorUtil, area * 2, 5), pTarget.getX(), pTarget.getY() + 0.25D, pTarget.getZ(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {
        Level level = p_41427_.getLevel();
        BlockPos blockpos = p_41427_.getClickedPos();
        Player player = p_41427_.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        if (player != null) {
            ItemStack itemStack = p_41427_.getItemInHand();
            if (blockstate.is(Tags.Blocks.STORAGE_BLOCKS_IRON)) {
                itemStack.hurtAndBreak(5, player, EquipmentSlot.MAINHAND);
                level.setBlockAndUpdate(blockpos, Blocks.DAMAGED_ANVIL.defaultBlockState());
                level.scheduleTick(blockpos, Blocks.DAMAGED_ANVIL, 2);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(p_41427_);
    }

    public boolean getMineBlocks(Level pLevel, BlockState pState, BlockPos pPos){
        return pState.is(BlockTags.MINEABLE_WITH_PICKAXE)
                && pState.getDestroySpeed(pLevel, pPos) > -1.0F;
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return pState.is(BlockTags.MINEABLE_WITH_PICKAXE) ? this.speed : 1.0F;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(this.getMineBlocks(pLevel, pState, pPos) ? 1 : 2, pEntityLiving, EquipmentSlot.MAINHAND);
        }
        if (this.getMineBlocks(pLevel, pState, pPos)){
            pLevel.playSound((Player) null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.DIRT_DEBRIS.get(), pEntityLiving.getSoundSource(), 1.0F, 1.0F);
            for (BlockPos blockPos : BlockFinder.multiBlockBreak(pEntityLiving, pPos, 1, 1, 1)){
                BlockState blockstate = pLevel.getBlockState(blockPos);
                if (this.getMineBlocks(pLevel, blockstate, blockPos)){
                    if (BlockFinder.breakBlock(pLevel, blockPos, pStack, pEntityLiving)){
                        if (blockstate.getDestroySpeed(pLevel, blockPos) != 0) {
                            pStack.hurtAndBreak(1, pEntityLiving, EquipmentSlot.MAINHAND);
                        }
                    }
                }
            }
        }

        return true;
    }

    public void attackMobs(LivingEntity pTarget, Player pPlayer, ItemStack pStack){
        float f = (float)pPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
        var damageSource = pPlayer.damageSources().playerAttack(pPlayer);
        float f1 = MobUtil.getEnchantedDamage(pPlayer, pPlayer.getMainHandItem(), pTarget, damageSource, f) - f;
        int j = MobUtil.getFireAspect(pPlayer);
        double area = 1.75D;
        area += MobUtil.getItemEnchantmentLevel(pPlayer, pStack, ModEnchantments.RADIUS);
        for (LivingEntity livingentity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pTarget.getBoundingBox().inflate(area, 0.25D, area))) {
            if (livingentity != pPlayer && livingentity != pTarget && !MobUtil.areAllies(pPlayer, livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && livingentity != pPlayer.getVehicle()) {
                livingentity.knockback(0.4F, (double) Mth.sin(pPlayer.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(pPlayer.getYRot() * ((float) Math.PI / 180F))));
                if (livingentity.hurt(damageSource, f + f1)) {
                    if (j > 0) {
                        livingentity.igniteForSeconds(j * 4);
                    }
                    MobUtil.doPostAttackEffects(pPlayer, livingentity, damageSource, pPlayer.getMainHandItem());
                }
            }
        }

        pPlayer.level().playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.HAMMER_IMPACT.get(), pPlayer.getSoundSource(), 1.0F, 1.0F);
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockState) {
        // Minecraft 1.21 asks the stack-aware hook for tool checks, so mirror the hammer's legacy pickaxe behavior.
        return this.isCorrectToolForDrops(blockState);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        ResourceLocation enchantmentId = enchantment.unwrapKey().map(ResourceKey::location).orElse(null);
        // Minecraft 1.21 exposes ranged weapon enchantments as main-hand enchantments, so use item tags plus explicit Goety allowances.
        return (super.supportsEnchantment(stack, enchantment)
                || enchantment.is(ModEnchantments.RADIUS)
                || Objects.equals(enchantmentId, ResourceLocation.fromNamespaceAndPath("vanillatweaks", "siphon")))
                && !enchantment.is(Enchantments.SWEEPING_EDGE);
    }
}
