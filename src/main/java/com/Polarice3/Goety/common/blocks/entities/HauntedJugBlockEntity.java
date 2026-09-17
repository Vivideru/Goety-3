package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.BrewCauldronBlock;
import com.Polarice3.Goety.common.blocks.HauntedJugBlock;
import com.Polarice3.Goety.compat.botania.BotaniaIntegration;
import com.Polarice3.Goety.compat.botania.BotaniaLoaded;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.BrewUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HauntedJugBlockEntity extends ModBlockEntity {
    private final FluidTank fluidTank = new FluidTank(Integer.MAX_VALUE) {

        @Override
        public @NotNull FluidStack getFluid() {
            return new FluidStack(Fluids.WATER, this.getCapacity());
        }

        @Override
        public int getFluidAmount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getCapacity() {
            return Integer.MAX_VALUE;
        }

        public CompoundTag writeToNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            nbt.put("Fluid", this.getFluid().save(provider));
            return nbt;
        }

        @NotNull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return this.drain(resource.getAmount(), action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return this.getFluid().copyWithAmount(maxDrain);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return resource.getAmount();
        }
    };
    public int search = 0;

    public HauntedJugBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.HAUNTED_JUG.get(), p_155229_, p_155230_);
    }

    public void tick() {
        if (this.level != null) {
            if (!this.level.isClientSide) {
                if (this.getBlockState().getValue(HauntedJugBlock.ENABLED)) {
                    if (BlockFinder.isPassableBlock(this.level, this.getBlockPos().above())) {
                        ++this.search;
                        int radius = 5;
                        int x = this.search / radius % radius;
                        int y = this.search / radius / radius % (radius - 2);
                        int z = this.search % radius;
                        if (this.search > 1 && x == 0 && y == 0 && z == 0) {
                            this.search = 0;
                        }
                        BlockPos blockPos = this.getBlockPos().offset(x - 2, y - 1, z - 2);
                        BlockState blockState = this.level.getBlockState(blockPos);
                        BlockEntity blockEntity = this.level.getBlockEntity(blockPos);
                        IFluidHandler upwardFluidHandler = this.level.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, Direction.UP);
                        boolean fluidHandler0 = blockEntity != null && !(blockEntity instanceof HauntedJugBlockEntity) && (blockEntity instanceof IFluidHandler || upwardFluidHandler != null) && !blockEntity.getBlockState().getBlock().getDescriptionId().contains("pipe");
                        boolean water = blockState.getBlock() == Blocks.WATER_CAULDRON && blockState.getValue(LayeredCauldronBlock.LEVEL) < 3;
                        boolean vanillaCauldron = blockState.getBlock() == Blocks.CAULDRON || water;
                        // Crafted cauldron outputs consume their stored level per item, so refilling them here would duplicate the result indefinitely.
                        boolean brewCauldron = blockState.getBlock() instanceof BrewCauldronBlock && blockEntity instanceof BrewCauldronBlockEntity cauldronEntity && blockState.getValue(BrewCauldronBlock.LEVEL) < 3 && BrewUtils.isEmpty(cauldronEntity.getBrew()) && cauldronEntity.mode != BrewCauldronBlockEntity.Mode.CRAFTED;
                        AABB aabb = new AABB(blockPos);
                        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, aabb, livingEntity -> livingEntity.isAlive() && (livingEntity.getRemainingFireTicks() > 0 || livingEntity instanceof Axolotl));
                        if (!list.isEmpty()) {
                            LivingEntity livingEntity = list.get(this.level.random.nextInt(list.size()));
                            this.streamWater(blockPos);
                            livingEntity.extinguishFire();
                            if (livingEntity.isSensitiveToWater()) {
                                livingEntity.hurt(livingEntity.damageSources().drown(), 1.0F);
                            }
                            if (livingEntity instanceof Axolotl axolotl) {
                                axolotl.rehydrate();
                            }
                        } else if (fluidHandler0) {
                            IFluidHandler fluidHandler;
                            if (blockEntity instanceof IFluidHandler handler) {
                                fluidHandler = handler;
                            } else {
                                fluidHandler = upwardFluidHandler;
                            }
                            if (fluidHandler != null) {
                                int filled = fluidHandler.fill(new FluidStack(Fluids.WATER, 250), IFluidHandler.FluidAction.EXECUTE);
                                if (filled > 0) {
                                    this.streamWater(blockPos);
                                    this.fluidTank.drain(new FluidStack(Fluids.WATER, filled), IFluidHandler.FluidAction.EXECUTE);
                                    this.markUpdated();
                                }
                            }
                        } else if (brewCauldron) {
                            this.level.setBlock(blockPos, blockState.cycle(BrewCauldronBlock.LEVEL), 2);
                            this.level.updateNeighborsAt(blockPos, blockState.getBlock());
                            this.streamWater(blockPos);
                            this.fluidTank.drain(new FluidStack(Fluids.WATER, 333), IFluidHandler.FluidAction.EXECUTE);
                            this.markUpdated();
                        } else if (vanillaCauldron) {
                            if (water) {
                                this.level.setBlock(blockPos, blockState.cycle(LayeredCauldronBlock.LEVEL), 2);
                            } else if (blockState.getBlock() == Blocks.CAULDRON) {
                                this.level.setBlock(blockPos, Blocks.WATER_CAULDRON.defaultBlockState(), 2);
                            }
                            this.level.updateNeighborsAt(blockPos, blockState.getBlock());
                            this.streamWater(blockPos);
                            this.fluidTank.drain(new FluidStack(Fluids.WATER, 333), IFluidHandler.FluidAction.EXECUTE);
                            this.markUpdated();
                        } else if (BotaniaLoaded.BOTANIA.isLoaded()) {
                            if (BotaniaIntegration.fillApothecary(blockPos, this.level)) {
                                this.level.updateNeighborsAt(blockPos, blockState.getBlock());
                                this.streamWater(blockPos);
                                this.fluidTank.drain(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                                this.markUpdated();
                            }
                        }
                    } else {
                        BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().above());
                        if (blockEntity != null) {
                            IFluidHandler fluidHandler = this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.getBlockPos().above(), Direction.DOWN);
                            if (fluidHandler != null) {
                                int filled = fluidHandler.fill(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                                if (filled > 0) {
                                    this.fluidTank.drain(new FluidStack(Fluids.WATER, filled), IFluidHandler.FluidAction.EXECUTE);
                                    this.markUpdated();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void streamWater(BlockPos target){
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.33F, 1.0F);
            if (this.level instanceof ServerLevel serverLevel) {
                Vec3 vec3 = Vec3.atBottomCenterOf(this.getBlockPos().above());
                Vec3 vec31 = Vec3.atBottomCenterOf(target);
                serverLevel.sendParticles(ModParticleTypes.WATER_STREAM.get(), vec3.x, vec3.y, vec3.z, 0, vec31.x, vec31.y, vec31.z, 1.0F);
            }
            this.level.playSound(null, target, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.33F, 1.0F);
        }
    }

    @Override
    public void readNetwork(CompoundTag compoundNBT) {
        // Fluid NBT uses registry-aware serialization in 1.21.
        if (this.level != null) {
            this.fluidTank.readFromNBT(this.level.registryAccess(), compoundNBT);
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag pCompound) {
        // Fluid NBT uses registry-aware serialization in 1.21.
        return this.level != null ? this.fluidTank.writeToNBT(this.level.registryAccess(), pCompound) : pCompound;
    }

    public FluidTank getFluidTank() {
        return this.fluidTank;
    }
}
