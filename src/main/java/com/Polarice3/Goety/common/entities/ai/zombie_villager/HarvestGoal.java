package com.Polarice3.Goety.common.entities.ai.zombie_villager;

import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.SpecialPlantable;

import org.jetbrains.annotations.Nullable;
import java.util.EnumSet;
import java.util.List;

public class HarvestGoal extends Goal {
    public ZombieVillagerServant servant;
    private static final int HARVEST_DURATION = 200;
    public static final float SPEED_MODIFIER = 0.5F;
    @Nullable
    private BlockPos aboveFarmlandPos;
    private long nextOkStartTime;
    private int timeWorkedSoFar;
    private final List<BlockPos> validFarmlandAroundVillager = Lists.newArrayList();

    public HarvestGoal(ZombieVillagerServant servant){
        this.servant = servant;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.servant.getTarget() != null){
            return false;
        } else if (this.servant.getLastHurtByMob() != null){
            return false;
        } else if (this.servant.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        } else {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = this.servant.blockPosition().mutable();
            this.validFarmlandAroundVillager.clear();

            for(int i = -1; i <= 1; ++i) {
                for(int j = -1; j <= 1; ++j) {
                    for(int k = -1; k <= 1; ++k) {
                        blockpos$mutableblockpos.set(this.servant.getX() + (double)i, this.servant.getY() + (double)j, this.servant.getZ() + (double)k);
                        if (this.validPos(blockpos$mutableblockpos, this.servant.level())) {
                            this.validFarmlandAroundVillager.add(new BlockPos(blockpos$mutableblockpos));
                        }
                    }
                }
            }

            this.aboveFarmlandPos = this.getValidFarmland(this.servant.level());
            return this.aboveFarmlandPos != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.timeWorkedSoFar < 200;
    }

    @Override
    public void start() {
        super.start();
        if (this.aboveFarmlandPos != null){
            this.servant.getLookControl().setLookAt(this.aboveFarmlandPos.getCenter());
            this.servant.getNavigation().moveTo(this.aboveFarmlandPos.getCenter().x(), this.aboveFarmlandPos.getCenter().y(), this.aboveFarmlandPos.getCenter().z(), 1.0F);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.servant.getNavigation().stop();
        this.timeWorkedSoFar = 0;
        this.nextOkStartTime = this.servant.level().getGameTime() + 40L;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.aboveFarmlandPos != null){
            if (this.aboveFarmlandPos.closerToCenterThan(this.servant.position(), 2.0D)) {
                if (this.servant.level().getGameTime() > this.nextOkStartTime) {
                    BlockState blockstate = this.servant.level().getBlockState(this.aboveFarmlandPos);
                    Block block = blockstate.getBlock();
                    Block block1 = this.servant.level().getBlockState(this.aboveFarmlandPos.below()).getBlock();
                    if (block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockstate)) {
                        this.servant.level().destroyBlock(this.aboveFarmlandPos, true, this.servant);
                    }

                    if (blockstate.isAir() && block1 instanceof FarmBlock && this.servant.hasFarmSeeds()) {
                        SimpleContainer simplecontainer = this.servant.getInventory();

                        for(int i = 0; i < simplecontainer.getContainerSize(); ++i) {
                            ItemStack itemstack = simplecontainer.getItem(i);
                            boolean flag = false;
                            if (!itemstack.isEmpty() && itemstack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS)) {
                                Item $$11 = itemstack.getItem();
                                if ($$11 instanceof BlockItem blockitem) {
                                    BlockState blockstate1 = blockitem.getBlock().defaultBlockState();
                                    this.servant.level().setBlockAndUpdate(this.aboveFarmlandPos, blockstate1);
                                    this.servant.level().gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(this.servant, blockstate1));
                                    flag = true;
                                } else if (itemstack.getItem() instanceof SpecialPlantable specialPlantable) {
                                    // ZombieVillagerServant is not a vanilla Villager, so preserve the old servant-only planting behavior while using NeoForge's guarded placement hook.
                                    if (specialPlantable.canPlacePlantAtPosition(itemstack, this.servant.level(), this.aboveFarmlandPos, Direction.DOWN)) {
                                        specialPlantable.spawnPlantAtPosition(itemstack, this.servant.level(), this.aboveFarmlandPos, Direction.DOWN);
                                        flag = true;
                                    }
                                }
                            }

                            if (flag) {
                                this.servant.level().playSound((Player)null, (double)this.aboveFarmlandPos.getX(), (double)this.aboveFarmlandPos.getY(), (double)this.aboveFarmlandPos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                                itemstack.shrink(1);
                                if (itemstack.isEmpty()) {
                                    simplecontainer.setItem(i, ItemStack.EMPTY);
                                }
                                break;
                            }
                        }
                    }

                    if (block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockstate)) {
                        this.validFarmlandAroundVillager.remove(this.aboveFarmlandPos);
                        this.aboveFarmlandPos = this.getValidFarmland(this.servant.level());
                        if (this.aboveFarmlandPos != null) {
                            this.nextOkStartTime = this.servant.level().getGameTime() + 20L;
                            this.servant.getLookControl().setLookAt(this.aboveFarmlandPos.getCenter());
                            this.servant.getNavigation().moveTo(this.aboveFarmlandPos.getCenter().x(), this.aboveFarmlandPos.getCenter().y(), this.aboveFarmlandPos.getCenter().z(), 1.0F);
                        }
                    }
                }
            } else {
                this.servant.getLookControl().setLookAt(this.aboveFarmlandPos.getCenter());
                this.servant.getNavigation().moveTo(this.aboveFarmlandPos.getCenter().x(), this.aboveFarmlandPos.getCenter().y(), this.aboveFarmlandPos.getCenter().z(), 1.0F);
            }
        }
        ++this.timeWorkedSoFar;
    }

    @Nullable
    private BlockPos getValidFarmland(Level p_23165_) {
        return this.validFarmlandAroundVillager.isEmpty() ? null : this.validFarmlandAroundVillager.get(p_23165_.getRandom().nextInt(this.validFarmlandAroundVillager.size()));
    }

    private boolean validPos(BlockPos p_23181_, Level p_23182_) {
        BlockState blockstate = p_23182_.getBlockState(p_23181_);
        Block block = blockstate.getBlock();
        Block block1 = p_23182_.getBlockState(p_23181_.below()).getBlock();
        return block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockstate) || blockstate.isAir() && block1 instanceof FarmBlock;
    }
}
