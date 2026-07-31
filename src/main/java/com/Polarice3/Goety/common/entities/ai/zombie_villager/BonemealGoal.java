package com.Polarice3.Goety.common.entities.ai.zombie_villager;

import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Optional;

public class BonemealGoal extends Goal {
    public ZombieVillagerServant servant;
    private long nextWorkCycleTime;
    private long lastBonemealingSession;
    private int timeWorkedSoFar;
    private Optional<BlockPos> cropPos = Optional.empty();

    public BonemealGoal(ZombieVillagerServant servant){
        this.servant = servant;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.servant.getTarget() != null){
            return false;
        } else if (!(this.servant.getMainHandItem().isEmpty() || this.servant.getMainHandItem().is(Items.BONE_MEAL) || this.servant.getOffhandItem().isEmpty() || this.servant.getOffhandItem().is(Items.BONE_MEAL))){
            return false;
        } else if (this.servant.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        } else if ((this.lastBonemealingSession == 0L || this.lastBonemealingSession + 160L <= this.servant.level().getGameTime())) {
            if (this.servant.getInventory().countItem(Items.BONE_MEAL) <= 0) {
                return false;
            } else {
                this.cropPos = this.pickNextTarget(this.servant.level(), this.servant);
                return this.cropPos.isPresent();
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.timeWorkedSoFar < 80 && this.cropPos.isPresent();
    }

    public void start() {
        this.setCurrentCropAsTarget(this.servant);
        if (this.servant.getMainHandItem().isEmpty()) {
            this.servant.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
        } else if (this.servant.getOffhandItem().isEmpty()){
            this.servant.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BONE_MEAL));
        }
        this.nextWorkCycleTime = this.servant.level().getGameTime();
        this.timeWorkedSoFar = 0;
    }

    @Override
    public void stop() {
        super.stop();
        if (this.servant.getMainHandItem().is(Items.BONE_MEAL)) {
            this.servant.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        } else if (this.servant.getOffhandItem().is(Items.BONE_MEAL)){
            this.servant.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }
        this.lastBonemealingSession = this.servant.level().getGameTime();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.cropPos.isPresent()) {
            BlockPos blockpos = this.cropPos.get();
            if (blockpos.closerToCenterThan(this.servant.position(), 2.0D)){
                if (this.servant.level().getGameTime() >= this.nextWorkCycleTime) {
                    ItemStack itemstack = ItemStack.EMPTY;
                    SimpleContainer simplecontainer = this.servant.getInventory();
                    int i = simplecontainer.getContainerSize();

                    for (int j = 0; j < i; ++j) {
                        ItemStack itemstack1 = simplecontainer.getItem(j);
                        if (itemstack1.is(Items.BONE_MEAL)) {
                            itemstack = itemstack1;
                            break;
                        }
                    }

                    if (!itemstack.isEmpty() && BoneMealItem.growCrop(itemstack, this.servant.level(), blockpos)) {
                        this.servant.level().levelEvent(1505, blockpos, 0);
                        itemstack.shrink(1);
                        if (this.servant.getMainHandItem().is(Items.BONE_MEAL)) {
                            this.servant.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        } else if (this.servant.getOffhandItem().is(Items.BONE_MEAL)){
                            this.servant.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                        }
                        this.cropPos = this.pickNextTarget(this.servant.level(), this.servant);
                        this.setCurrentCropAsTarget(this.servant);
                        this.nextWorkCycleTime = this.servant.level().getGameTime() + 40L;
                    }

                    ++this.timeWorkedSoFar;
                }
            } else {
                this.servant.getLookControl().setLookAt(blockpos.getCenter());
                this.servant.getNavigation().moveTo(blockpos.getCenter().x(), blockpos.getCenter().y(), blockpos.getCenter().z(), 1.0F);
            }
        }
    }

    private void setCurrentCropAsTarget(ZombieVillagerServant p_24481_) {
        this.cropPos.ifPresent((p_24484_) -> {
            p_24481_.getLookControl().setLookAt(p_24484_.getCenter());
            p_24481_.getNavigation().moveTo(p_24484_.getCenter().x(), p_24484_.getCenter().y(), p_24484_.getCenter().z(), 1.0F);
        });
    }

    private Optional<BlockPos> pickNextTarget(Level p_24493_, ZombieVillagerServant p_24494_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        Optional<BlockPos> optional = Optional.empty();
        int i = 0;

        for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
                for(int l = -1; l <= 1; ++l) {
                    blockpos$mutableblockpos.setWithOffset(p_24494_.blockPosition(), j, k, l);
                    if (this.validPos(blockpos$mutableblockpos, p_24493_)) {
                        ++i;
                        if (p_24493_.random.nextInt(i) == 0) {
                            optional = Optional.of(blockpos$mutableblockpos.immutable());
                        }
                    }
                }
            }
        }

        return optional;
    }

    private boolean validPos(BlockPos p_24486_, Level p_24487_) {
        BlockState blockstate = p_24487_.getBlockState(p_24486_);
        Block block = blockstate.getBlock();
        return block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockstate);
    }
}
