package com.Polarice3.Goety.common.items.magic;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Based on Rat Sack item by @AlexModGuy: <a href="https://github.com/AlexModGuy/Rats/blob/1.20/src/main/java/com/github/alexthe666/rats/server/items/RatSackItem.java">...</a>
 */
public class EsotericTesseract extends Item implements IPersist {
    public static String BIG = "Big";
    public static String LARGE = "Large";
    public static String HUGE = "Huge";
    private static final int DEFAULT_TESSERACT_DURABILITY = 16;

    private static CompoundTag data(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setData(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    public EsotericTesseract() {
        super(new Properties()
                .stacksTo(1)
                .fireResistant()
                // Item properties are created during registry construction before configs load; keep the configured default as the safe bootstrap value.
                .durability(DEFAULT_TESSERACT_DURABILITY + 1)
                .rarity(Rarity.RARE));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.isDamaged(stack);
    }

    public int getBarColor(ItemStack stack) {
        if (this.isBroken(stack)) {
            return 0x800000;
        }
        return super.getBarColor(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (this.isBroken(stack)) {
            return 13;
        }
        return super.getBarWidth(stack);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
            if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                stack.setDamageValue(stack.getMaxDamage() - 1);
                // NeoForge 1.21 changed the break callback to receive the broken item, not the holder entity.
                onBroken.accept(stack.getItem());
            }
            return 0;
        }
        return super.damageItem(stack, amount, entity, onBroken);
    }

    public static boolean isSmall(Mob mob) {
        return mob.getType().is(ModTags.EntityTypes.TESSERACT_SMALL);
    }

    public static boolean isMedium(Mob mob) {
        return mob.getType().is(ModTags.EntityTypes.TESSERACT_MEDIUM);
    }

    public static boolean isLarge(Mob mob) {
        return mob.getType().is(ModTags.EntityTypes.TESSERACT_LARGE);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        if (target instanceof Mob mob && mob.isAlive() && mob instanceof OwnableEntity owned && owned.getOwner() == player && mob.canChangeDimensions(mob.level(), mob.level())) {
            if (stack.getItem() == this && this.isNotBroken(stack)) {
                if (!player.level().isClientSide) {
                    if (getServantsInTesseract(stack) < ItemConfig.TesseractCapacity.get()) {
                        boolean flag = true;
                        if (isLarge(mob)) {
                            if (getServantsInTesseract(stack) > 0) {
                                flag = false;
                            }
                        } else if (isMedium(mob)) {
                            if (getServantsInTesseract(stack) > ItemConfig.TesseractCapacity.get() - 4) {
                                flag = false;
                            }
                        } else if (isSmall(mob)) {
                            if (getServantsInTesseract(stack) > ItemConfig.TesseractCapacity.get() - 2) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            putServantIntoTesseract(stack, mob, getServantsInTesseract(stack) + 1);
                            player.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1.0F, 0.75F);
                            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1.0F, 0.75F));
                            mob.discard();
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getUseDuration(ItemStack p_41454_, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        super.onUseTick(worldIn, livingEntityIn, stack, count);
        if (!worldIn.isClientSide) {
            if (stack.is(this)) {
                if (getServantsInTesseract(stack) > 0) {
                    int time = this.getUseDuration(stack, livingEntityIn) - count;
                    if (time > 0 && time % 20 == 0) {
                        int servantCount = ejectSingleServant(stack, worldIn, livingEntityIn.blockPosition());
                        if (servantCount > 0) {
                            worldIn.playSound(null, livingEntityIn.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, livingEntityIn.getSoundSource(), 1.0F, 1.0F);
                            ItemHelper.hurtAndBreak(stack, servantCount, livingEntityIn);
                        }
                    }
                } else {
                    livingEntityIn.stopUsingItem();
                }
            }
        }
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (getServantsInTesseract(itemstack) > 0) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(itemstack);
        }
        return super.use(worldIn, playerIn, handIn);
    }

    public static void putServantIntoTesseract(ItemStack tesseract, Mob mob, int count) {
        // ItemStack root NBT was removed in 1.21; keep stored servant payloads in CUSTOM_DATA.
        CompoundTag tag = data(tesseract);
        CompoundTag servantTag = new CompoundTag();
        ResourceLocation typesKey = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());

        if (typesKey != null) {
            servantTag.putString("ServantType", typesKey.toString());
            if (mob.isInvulnerable()) {
                servantTag.putBoolean("Invulnerable", true);
            }
            if (mob.isSilent()) {
                servantTag.putBoolean("Silent", mob.isSilent());
            }
            if (mob.isNoGravity()) {
                servantTag.putBoolean("NoGravity", mob.isNoGravity());
            }
            // NeoForge's old per-entity canUpdate toggle is not exposed in 1.21, so no equivalent state is persisted here.

            if (!mob.getTags().isEmpty()) {
                ListTag listtag = new ListTag();

                for(String s : mob.getTags()) {
                    listtag.add(StringTag.valueOf(s));
                }

                servantTag.put("Tags", listtag);
            }

            if (!mob.getPersistentData().isEmpty()) {
                servantTag.put("ForgeData", mob.getPersistentData().copy());
            }
            mob.addAdditionalSaveData(servantTag);
            if (mob.hasCustomName()) {
                servantTag.putString("CustomName", Component.Serializer.toJson(mob.getCustomName(), mob.level().registryAccess()));
            }
            if (isLarge(mob)) {
                servantTag.putBoolean(HUGE, true);
            } else if (isMedium(mob)) {
                servantTag.putBoolean(LARGE, true);
            } else if (isSmall(mob)) {
                servantTag.putBoolean(BIG, true);
            }
            String mainName = "Servant_";
            if (!tag.getAllKeys().isEmpty()) {
                for (int i = 0; i < tag.getAllKeys().size(); ++i) {
                    String name = mainName + count;
                    if (tag.contains(name)) {
                        count += 1;
                    } else {
                        break;
                    }
                }
            }
            String finalName  = mainName + count;
            tag.put(finalName, servantTag);
        }
        setData(tesseract, tag);
    }

    public static int getServantsInTesseract(ItemStack stack) {
        int servantCount = 0;
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = data(stack);
            for (String tagInfo : tag.getAllKeys()) {
                if (tagInfo.contains("Servant")) {
                    CompoundTag servantTag = tag.getCompound(tagInfo);
                    if (servantTag.contains(HUGE)) {
                        servantCount += ItemConfig.TesseractCapacity.get();
                    } else if (servantTag.contains(LARGE)){
                        servantCount += 4;
                    } else if (servantTag.contains(BIG)){
                        servantCount += 2;
                    } else {
                        servantCount++;
                    }
                }
            }
        }
        return servantCount;
    }

    public static int ejectSingleServant(ItemStack stack, Level level, BlockPos pos) {
        int servantCount = 0;
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = data(stack);
            Optional<String> optional = tag.getAllKeys().stream().filter(string -> string.contains("Servant")).findFirst();
            if (optional.isPresent()) {
                String tagInfo = optional.get();
                if (tagInfo.contains("Servant")) {
                    CompoundTag servantTag = tag.getCompound(tagInfo);
                    if (servantTag.contains(HUGE)) {
                        servantCount = ItemConfig.TesseractCapacity.get();
                    } else if (servantTag.contains(LARGE)) {
                        servantCount += 4;
                    } else if (servantTag.contains(BIG)) {
                        servantCount += 2;
                    } else {
                        servantCount++;
                    }
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(servantTag.getString("ServantType")));
                    if (entityType != null) {
                        Entity entity = entityType.create(level);
                        if (entity instanceof Mob servant && entity instanceof OwnableEntity) {
                            if (servantTag.contains("Invulnerable")) {
                                servant.setInvulnerable(servantTag.getBoolean("Invulnerable"));
                            }
                            if (servantTag.contains("Silent")) {
                                servant.setSilent(servantTag.getBoolean("Silent"));
                            }
                            if (servantTag.contains("NoGravity")) {
                                servant.setNoGravity(servantTag.getBoolean("NoGravity"));
                            }
                            if (servantTag.contains("Tags", 9)) {
                                servant.getTags().clear();
                                ListTag listtag3 = servantTag.getList("Tags", 8);
                                int i = Math.min(listtag3.size(), 1024);

                                for(int j = 0; j < i; ++j) {
                                    servant.getTags().add(listtag3.getString(j));
                                }
                            }
                            if (servantTag.contains("ForgeData", 10)) {
                                servant.getPersistentData().merge(servantTag.getCompound("ForgeData"));
                            }

                            servant.readAdditionalSaveData(servantTag);
                            if (!servantTag.getString("CustomName").isEmpty()) {
                                servant.setCustomName(Component.Serializer.fromJson(servantTag.getString("CustomName"), level.registryAccess()));
                            }
                            BlockPos blockPos = BlockFinder.SummonRadius(pos, servant, level, 4);
                            servant.moveTo(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                            if (level instanceof ServerLevel serverLevel) {
                                level.addFreshEntity(servant);
                                if (servant instanceof IServant servant1) {
                                    servant1.setFollowing();
                                }
                                ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.PORTAL, servant);
                            }
                            if (!stack.isEmpty()) {
                                tag.remove(tagInfo);
                                setData(stack, tag);
                            }
                        }
                    }
                }
            }
        }
        return servantCount;
    }

    public static int ejectAllServants(ItemStack stack, Level level, BlockPos pos) {
        int servantCount = 0;
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = data(stack);
            for (String tagInfo : tag.getAllKeys()) {
                if (tagInfo.contains("Servant")) {
                    servantCount++;
                    CompoundTag servantTag = tag.getCompound(tagInfo);
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(servantTag.getString("ServantType")));
                    if (entityType != null) {
                        Entity entity = entityType.create(level);
                        if (entity instanceof Mob servant && entity instanceof OwnableEntity) {
                            if (servantTag.contains("Invulnerable")) {
                                servant.setInvulnerable(servantTag.getBoolean("Invulnerable"));
                            }
                            if (servantTag.contains("Silent")) {
                                servant.setSilent(servantTag.getBoolean("Silent"));
                            }
                            if (servantTag.contains("NoGravity")) {
                                servant.setNoGravity(servantTag.getBoolean("NoGravity"));
                            }
                            if (servantTag.contains("Tags", 9)) {
                                servant.getTags().clear();
                                ListTag listtag3 = servantTag.getList("Tags", 8);
                                int i = Math.min(listtag3.size(), 1024);

                                for(int j = 0; j < i; ++j) {
                                    servant.getTags().add(listtag3.getString(j));
                                }
                            }
                            if (servantTag.contains("ForgeData", 10)) {
                                servant.getPersistentData().merge(servantTag.getCompound("ForgeData"));
                            }

                            servant.readAdditionalSaveData(servantTag);
                            if (!servantTag.getString("CustomName").isEmpty()) {
                                servant.setCustomName(Component.Serializer.fromJson(servantTag.getString("CustomName"), level.registryAccess()));
                            }
                            BlockPos blockPos = pos;
                            if (servantCount > 1) {
                                blockPos = BlockFinder.SummonRadius(pos, servant, level, 3);
                            }
                            servant.moveTo(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                            if (!level.isClientSide()) {
                                level.addFreshEntity(servant);
                                if (servant instanceof IServant servant1) {
                                    servant1.setFollowing();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!stack.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        }
        return servantCount;
    }

    @Override
    public void onDestroyed(ItemEntity entity, DamageSource source) {
        ItemStack stack = entity.getItem();
        if (getServantsInTesseract(stack) > 0) {
            ejectAllServants(stack, entity.level(), entity.blockPosition());
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), entity.getSoundSource(), 1.0F, 1.0F);
        }
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(Tags.Items.ENDER_PEARLS) || super.isValidRepairItem(pToRepair, pRepair);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int servantCount = 0;
        List<String> servantNames = new ArrayList<>();
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = data(stack);
            for (String tagInfo : tag.getAllKeys()) {
                if (tagInfo.contains("Servant")) {
                    CompoundTag servantTag = tag.getCompound(tagInfo);
                    if (servantTag.contains(HUGE)) {
                        servantCount = ItemConfig.TesseractCapacity.get();
                    } else if (servantTag.contains(LARGE)) {
                        servantCount += 4;
                    } else if (servantTag.contains(BIG)) {
                        servantCount += 2;
                    } else {
                        servantCount++;
                    }
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(servantTag.getString("ServantType")));
                    String servantName = Component.translatable("info.goety.servant").toString();
                    if (entityType != null) {
                        servantName = I18n.get(entityType.getDescriptionId());
                    }

                    if (!servantTag.getString("CustomName").isEmpty()) {
                        Component servantNameTag = context.registries() != null ? Component.Serializer.fromJson(servantTag.getString("CustomName"), context.registries()) : null;
                        if (servantNameTag != null) {
                            servantName = servantNameTag.getString();
                        }
                    }
                    servantNames.add(servantName);
                }
            }
        }
        if (this.isBroken(stack)) {
            tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
        }
        if (servantCount <= 0) {
            tooltip.add(Component.translatable("item.goety.tesseract").withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("item.goety.tesseract.empty").withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Component.translatable("item.goety.tesseract.unleash").withStyle(ChatFormatting.DARK_PURPLE));
        }
        tooltip.add(Component.translatable("item.goety.tesseract.contains", servantCount, ItemConfig.TesseractCapacity.get()).withStyle(ChatFormatting.GRAY));
        if (!servantNames.isEmpty()) {
            for (int i = 0; i < servantNames.size(); i++) {
                if (i < 3) {
                    tooltip.add(Component.literal(servantNames.get(i)).withStyle(ChatFormatting.GRAY));
                } else {
                    break;
                }
            }
            if (servantNames.size() > 3) {
                tooltip.add(Component.translatable("item.goety.tesseract.more", servantNames.size() - 3).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
