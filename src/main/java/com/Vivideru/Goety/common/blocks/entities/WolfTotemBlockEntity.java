package com.Vivideru.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.entities.TrainingBlockEntity;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Vivideru.Goety.common.blocks.WolfTotemBlock;
import com.Vivideru.Goety.common.items.VivideruWolfArmorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WolfTotemBlockEntity extends TrainingBlockEntity {
    private static final TagKey<Block> CRYPT_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath("goety", "crypt_blocks"));
    public static final String SERVANT_LIST = "WolfTotemServants";
    private static final String CREATED_WARG = "CreatedWarg";
    private static final String CREATED_CERBERUS = "CreatedCerberus";
    private static final String HEALTH_BONUS = "VivideruWolfTotemHealthBonus";
    private int rawMeat;
    private int bones;
    private final List<LivingEntity> servants = new ArrayList<>();
    private final List<UUID> uuids = new ArrayList<>();
    private CursedCageBlockEntity cursedCageTile;
    private UUID createdWarg;
    private UUID createdCerberus;

    public WolfTotemBlockEntity(BlockPos pos, BlockState state) {
        super(VivideruBlockEntities.WOLF_TOTEM.get(), pos, state);
        this.trainTimeTotal = 100;
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, TrainingBlockEntity blockEntity) {
        if (level instanceof ServerLevel) {
            this.updateReviveServants();
        }
        super.tick(level, blockPos, blockState, blockEntity);
    }

    @Override
    public void setVariant(ItemStack itemStack, Level level, BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        EntityType<?> entityType = ModEntityType.BLACK_WOLF.get();
        BlockPos spawnPos = blockPos.above();
        if (serverLevel.dimension() == Level.NETHER || serverLevel.getBiome(spawnPos).is(BiomeTags.IS_NETHER)) {
            entityType = ModEntityType.HELLHOUND.get();
        } else if (serverLevel.getBlockState(blockPos.below()).is(CRYPT_BLOCKS)) {
            entityType = ModEntityType.SKELETON_WOLF.get();
        } else if (serverLevel.getBiome(spawnPos).value().coldEnoughToSnow(spawnPos)) {
            entityType = ModEntityType.WINTER_WOLF.get();
        } else if (serverLevel.isThundering() && serverLevel.canSeeSky(spawnPos)) {
            entityType = ModEntityType.STORMHOUND.get();
        }
        this.setEntityType(entityType);
    }

    @Override
    public void startTraining(int amount, ItemStack itemStack) {
        super.startTraining(amount, itemStack);
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), ModSounds.GRAVESTONE_START.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void playSpawnSound() {
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), SoundEvents.WOLF_HOWL, SoundSource.BLOCKS, 0.20F, 0.75F);
        }
    }

    @Override
    public boolean isFuel(ItemStack stack) {
        return isRawMeat(stack) || stack.is(Items.BONE);
    }

    @Override
    public boolean placeItem(ItemStack stack) {
        if (this.level == null || this.level.isClientSide || this.trainAmount >= this.maxTrainAmount()) {
            return false;
        }
        boolean accepted = false;
        if (isRawMeat(stack)) {
            ++this.rawMeat;
            accepted = true;
        } else if (stack.is(Items.BONE)) {
            ++this.bones;
            accepted = true;
        }
        if (!accepted) {
            return false;
        }
        stack.shrink(1);
        if (this.rawMeat > 0 && this.bones > 0) {
            --this.rawMeat;
            --this.bones;
            // A complete offering is one raw meat plus one bone, and it deliberately creates two wolves.
            this.startTraining(2, stack);
        }
        this.markUpdated();
        return true;
    }

    @Override
    public EntityType<?> getTrainMob() {
        return super.getTrainMob();
    }

    @Override
    public void markUpdated() {
        super.markUpdated();
        if (this.level != null && this.getBlockState().hasProperty(WolfTotemBlock.POWERED)) {
            boolean powered = this.isTraining() || this.canOfferRevive();
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(BlockStateProperties.POWERED, powered), 3);
            BlockState upper = this.level.getBlockState(this.getBlockPos().above());
            if (upper.is(this.getBlockState().getBlock()) && upper.hasProperty(WolfTotemBlock.POWERED)) {
                this.level.setBlock(this.getBlockPos().above(), upper.setValue(BlockStateProperties.POWERED, powered), 3);
            }
        }
    }

    @Override
    public int maxTrainAmount() {
        return 10;
    }

    @Override
    public boolean summonLimit() {
        return false;
    }

    public List<LivingEntity> getServants() {
        return this.servants;
    }

    public void addServant(LivingEntity servant) {
        if (!this.uuids.contains(servant.getUUID())) {
            this.uuids.add(servant.getUUID());
        }
    }

    public void removeServant(LivingEntity servant) {
        this.uuids.remove(servant.getUUID());
        this.servants.remove(servant);
    }

    public boolean hasSpace() {
        return this.getServants().size() < MainConfig.OminousIdolLimit.get();
    }

    public boolean hasCreatedWarg() {
        return this.createdWarg != null;
    }

    public void setCreatedWarg(UUID createdWarg) {
        this.createdWarg = createdWarg;
        this.markUpdated();
    }

    public void releaseWarg(UUID wargId) {
        boolean changed = this.uuids.remove(wargId);
        changed |= this.servants.removeIf(servant -> servant.getUUID().equals(wargId));
        if (wargId.equals(this.createdWarg)) {
            // A definitive Warg removal must free both the saved Totem slot and its servant assignment.
            this.createdWarg = null;
            changed = true;
        }
        if (changed) {
            this.markUpdated();
        }
    }

    public boolean hasCreatedCerberus() {
        return this.createdCerberus != null;
    }

    public void setCreatedCerberus(UUID createdCerberus) {
        this.createdCerberus = createdCerberus;
        this.markUpdated();
    }

    public void releaseCerberus(UUID cerberusId) {
        boolean changed = this.uuids.remove(cerberusId);
        changed |= this.servants.removeIf(servant -> servant.getUUID().equals(cerberusId));
        if (cerberusId.equals(this.createdCerberus)) {
            this.createdCerberus = null;
            changed = true;
        }
        if (changed) {
            this.markUpdated();
        }
    }

    public boolean canOfferRevive() {
        return this.checkCage() && !this.getServants().isEmpty();
    }

    public int getSoulEnergy() {
        if (this.checkCage() && this.cursedCageTile != null) {
            return this.cursedCageTile.getSouls();
        }
        return 0;
    }

    public void siphonSoulEnergy(int souls) {
        if (this.checkCage() && this.cursedCageTile != null) {
            this.cursedCageTile.decreaseSouls(souls);
        }
    }

    @Override
    protected void onSummonedEntity(Entity entity, ServerLevel serverLevel) {
        if (entity instanceof LivingEntity living) {
            applyWolfTotemBonus(living);
            if (living instanceof Summoned summoned) {
                // Equipment setup can run before Curios resolves the copied owner, so apply the Ring armor after finalization as well.
                VivideruWolfArmorUtil.equipRingGrantedArmor(summoned);
            }
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag written = super.writeNetwork(tag, provider);
        written.putInt("RawMeat", this.rawMeat);
        written.putInt("Bones", this.bones);
        ListTag list = new ListTag();
        for (UUID uuid : this.uuids) {
            list.add(StringTag.valueOf(uuid.toString()));
        }
        written.put(SERVANT_LIST, list);
        if (this.createdWarg != null) {
            written.putUUID(CREATED_WARG, this.createdWarg);
        }
        if (this.createdCerberus != null) {
            written.putUUID(CREATED_CERBERUS, this.createdCerberus);
        }
        return written;
    }

    @Override
    public void readNetwork(CompoundTag tag, HolderLookup.Provider provider) {
        super.readNetwork(tag, provider);
        this.rawMeat = tag.getInt("RawMeat");
        this.bones = tag.getInt("Bones");
        this.uuids.clear();
        if (tag.contains(SERVANT_LIST)) {
            ListTag list = tag.getList(SERVANT_LIST, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); ++i) {
                this.uuids.add(UUID.fromString(list.getString(i)));
            }
        }
        this.createdWarg = tag.hasUUID(CREATED_WARG) ? tag.getUUID(CREATED_WARG) : null;
        this.createdCerberus = tag.hasUUID(CREATED_CERBERUS) ? tag.getUUID(CREATED_CERBERUS) : null;
    }

    private static boolean isRawMeat(ItemStack stack) {
        return stack.is(ItemTags.MEAT) && stack.getFoodProperties(null) != null && !stack.is(Items.COOKED_BEEF) && !stack.is(Items.COOKED_CHICKEN) && !stack.is(Items.COOKED_COD) && !stack.is(Items.COOKED_MUTTON) && !stack.is(Items.COOKED_PORKCHOP) && !stack.is(Items.COOKED_RABBIT) && !stack.is(Items.COOKED_SALMON);
    }

    public static void applyWolfTotemBonus(LivingEntity entity) {
        if (entity instanceof Summoned) {
            if (entity instanceof BlackWolf blackWolf) {
                // Black Wolf variants already persist ritual health through their own entity NBT flag.
                blackWolf.setRitualSummonedByPlayer(true);
            }
            // ForgeData survives chunk and world reloads, unlike a one-time attribute mutation performed at spawn.
            entity.getPersistentData().putBoolean(HEALTH_BONUS, true);
            restoreWolfTotemBonus(entity, true);
        }
    }

    public static void restoreWolfTotemBonus(LivingEntity entity, boolean healToFull) {
        if (!entity.getPersistentData().getBoolean(HEALTH_BONUS)) {
            return;
        }
        AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            double baseHealth = entity instanceof SkeletonWolf
                    ? AttributesConfig.get(AttributesConfig.SkeletonWolfHealth)
                    : entity instanceof BlackWolf
                    ? AttributesConfig.get(AttributesConfig.BlackWolfHealth)
                    : health.getBaseValue();
            // Reapply the configured value directly so repeated join events cannot multiply the bonus.
            health.setBaseValue(baseHealth * 2.0D);
            if (healToFull) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    private void updateReviveServants() {
        if (!this.uuids.isEmpty()) {
            this.chunkLoadBlock();
            this.uuids.removeIf(uuid -> {
                Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                if (!(entity instanceof LivingEntity living) || !WolfTotemHooks.canUseTotem(living) || living.isRemoved()) {
                    this.markUpdated();
                    return true;
                }
                WolfTotemBlockEntity totem = WolfTotemHooks.getTotem(living);
                if (totem != null && totem != this) {
                    this.servants.remove(living);
                    this.markUpdated();
                    return true;
                }
                if (!this.servants.contains(living)) {
                    this.servants.add(living);
                    this.markUpdated();
                }
                return false;
            });
            this.servants.removeIf(living -> living.isRemoved() || !this.uuids.contains(living.getUUID()));
        } else if (!this.servants.isEmpty()) {
            this.servants.clear();
            this.markUpdated();
        }
    }

    private boolean checkCage() {
        if (this.level != null) {
            BlockPos pos = this.getBlockPos().below();
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity tileentity = this.level.getBlockEntity(pos);
                if (tileentity instanceof CursedCageBlockEntity cage) {
                    this.cursedCageTile = cage;
                    return !cage.getItem().isEmpty();
                }
            }
        }
        this.cursedCageTile = null;
        return false;
    }
}
