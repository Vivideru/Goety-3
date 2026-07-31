package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.blocks.TallSkullBlock;
import com.Polarice3.Goety.common.blocks.WallTallSkullBlock;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;

public class GraverobberShovelItem extends ShovelItem {
    public GraverobberShovelItem() {
        // In 1.21 the custom properties must declare durability explicitly so the enchanting table treats the shovel as enchantable.
        super(ModTiers.SPECIAL, (new Properties()).rarity(Rarity.UNCOMMON).durability(ModTiers.SPECIAL.getUses()).attributes(DiggerItem.createAttributes(ModTiers.SPECIAL, 1.5F, -3.0F)));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        return ConfiguredItemUtil.durability(ItemConfig.SpecialToolsDurability, ModTiers.SPECIAL.getUses());
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.is(BlockTags.MINEABLE_WITH_SHOVEL)){
            boolean flag = !MobUtil.isShifting(pEntityLiving);
            if (ItemConfig.GraverobberShovelCrouch.get()) {
                flag = MobUtil.isShifting(pEntityLiving);
            }
            if (flag) {
                for (BlockPos blockPos : multiBlockBreak(pEntityLiving, pPos)) {
                    BlockState blockstate = pLevel.getBlockState(blockPos);
                    if (blockstate.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                        if (BlockFinder.breakBlock(pLevel, blockPos, pStack, pEntityLiving)) {
                            if (blockstate.getDestroySpeed(pLevel, blockPos) != 0) {
                                pStack.hurtAndBreak(1, pEntityLiving, EquipmentSlot.MAINHAND);
                            }
                        }
                    }
                }
            }
            if (pLevel.getServer() != null) {
                double d0 = (double) (pLevel.getRandom().nextFloat() * 0.5F) + 0.25D;
                double d1 = (double) (pLevel.getRandom().nextFloat() * 0.5F) + 0.25D;
                double d2 = (double) (pLevel.getRandom().nextFloat() * 0.5F) + 0.25D;
                LootTable loottable = pLevel.getServer().reloadableRegistries().getLootTable(EntityType.SKELETON.getDefaultLootTable());
                if ((pState.is(BlockTags.DIRT) || pState.is(BlockTags.SAND))){
                    if (pLevel.getRandom().nextFloat() <= 0.1F){
                        if (pLevel.getRandom().nextBoolean()){
                            loottable = pLevel.getServer().reloadableRegistries().getLootTable(EntityType.ZOMBIE.getDefaultLootTable());
                        }
                        LootParams.Builder lootcontext$builder = MobUtil.createLootContext(pLevel.damageSources().generic(), pEntityLiving);
                        LootParams ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> {
                            ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX() + d0, pPos.getY() + d1, pPos.getZ() + d2, loot);
                            if (pState.is(BlockTags.SAND)) {
                                if (loot.getFoodProperties(pEntityLiving) != null && loot.getFoodProperties(pEntityLiving).effects().isEmpty()) {
                                    itemEntity.setItem(new ItemStack(Items.BONE));
                                }
                            }
                            pLevel.addFreshEntity(itemEntity);
                        });
                    }
                } else if (pState.is(BlockTags.WITHER_SUMMON_BASE_BLOCKS)){
                    if (pLevel.getRandom().nextFloat() <= 0.1F){
                        LootParams.Builder lootcontext$builder = MobUtil.createLootContext(pLevel.damageSources().generic(), pEntityLiving);
                        LootParams ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> {
                            ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX() + d0, pPos.getY() + d1, pPos.getZ() + d2, loot);
                            pLevel.addFreshEntity(itemEntity);
                        });
                    }
                }
                if (pLevel.getRandom().nextFloat() <= 0.0025F){
                    ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX() + d0, pPos.getY() + d1, pPos.getZ() + d2, new ItemStack(ModItems.GRAVE_DUST.get()));
                    pLevel.addFreshEntity(itemEntity);
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    public static Iterable<BlockPos> multiBlockBreak(LivingEntity livingEntity, BlockPos blockPos){
        BlockHitResult blockHitResult = MobUtil.rayTrace(livingEntity, 10, false);
        Direction direction = blockHitResult.getDirection();
        Direction direction1 = livingEntity.getDirection();
        boolean xRot = direction1.getStepX() == 0;
        boolean zRot = direction1.getStepZ() == 0;
        boolean hasY = direction.getStepY() == 0;
        Vec3i start = new Vec3i(!xRot && !hasY && zRot && direction1.equals(Direction.WEST) ? -1 : 0, 0, xRot && !hasY && !zRot && direction1.equals(Direction.NORTH) ? -1 : 0);
        Vec3i end = new Vec3i(!xRot && !hasY && zRot && direction1.equals(Direction.EAST) ? 1 : 0, hasY ? 1 : 0, xRot && !hasY && !zRot && direction1.equals(Direction.SOUTH) ? 1 : 0);
        return BlockPos.betweenClosed(
                blockPos.offset(start),
                blockPos.offset(end));
    }

    public float getDestroySpeed(ItemStack p_41004_, BlockState p_41005_) {
        if (p_41005_.getBlock() instanceof AbstractSkullBlock || p_41005_.getBlock() instanceof TallSkullBlock || p_41005_.getBlock() instanceof WallTallSkullBlock) {
            return 8.0F;
        } else {
            float speed = super.getDestroySpeed(p_41004_, p_41005_);
            // NeoForge 1.21 uses the TOOL component for mining speed; preserve the tier speed if it falls back to hand speed.
            return speed <= 1.0F && p_41005_.is(BlockTags.MINEABLE_WITH_SHOVEL) ? ModTiers.SPECIAL.getSpeed() : speed;
        }
    }
}
