package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.vehicle.HauntedBroom;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModAttributeUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class HauntedBroomItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private static final String OWNER = "owner";
    private static final String OWNER_NAME = "owner_name";

    public HauntedBroomItem() {
        super(new Item.Properties().stacksTo(1).attributes(createAttributes()));
    }

    private static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 3.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_KNOCKBACK, ModAttributeUtil.create(UUID.fromString("35982898-1981-4c68-8ae3-5ac343403d56"), "item.goety.haunted_broom.knockback", 1.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotIndex, boolean selected) {
        super.inventoryTick(stack, level, entity, slotIndex, selected);
        if (getOwnerID(stack) != null && MobUtil.getItemEnchantmentLevel(entity, stack, ModEnchantments.FEALTY) <= 0) {
            // ItemStack root NBT was removed in 1.21; Fealty ownership markers are kept in CUSTOM_DATA.
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                tag.remove(OWNER);
                tag.remove(OWNER_NAME);
            });
        }
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (state.is(Blocks.COBWEB) && level instanceof ServerLevel serverLevel) {
            state.getBlock().popExperience(serverLevel, pos, 1 + level.getRandom().nextInt(5));
        }
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return state.is(Blocks.COBWEB) ? 15.0F : super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(Blocks.COBWEB);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.getCooldowns().isOnCooldown(this) || SEHelper.isOnCooldown(player, itemStack)) {
                return InteractionResultHolder.pass(itemStack);
            }
            UUID ownerId = getOwnerID(itemStack);
            if (ownerId != null && getOwner(level, ownerId) != player) {
                return InteractionResultHolder.pass(itemStack);
            }
            HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
            if (hitResult.getType() == HitResult.Type.MISS) {
                return InteractionResultHolder.pass(itemStack);
            }
            Vec3 view = player.getViewVector(1.0F);
            List<Entity> entities = level.getEntities(player, player.getBoundingBox().expandTowards(view.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
            if (!entities.isEmpty()) {
                Vec3 eye = player.getEyePosition(1.0F);
                for (Entity entity : entities) {
                    AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (box.contains(eye)) {
                        return InteractionResultHolder.pass(itemStack);
                    }
                }
            }
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                HauntedBroom broom = new HauntedBroom(itemStack, level, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z);
                broom.setYRot(player.getYRot());
                if (MobUtil.getItemEnchantmentLevel(player, itemStack, ModEnchantments.FEALTY) > 0) {
                    broom.setOwner(player);
                }
                int hardy = MobUtil.getItemEnchantmentLevel(player, itemStack, ModEnchantments.HARDY);
                if (hardy > 0) {
                    broom.setDamageThreshold(broom.getDamageThreshold() + hardy * 20);
                }
                if (!level.noCollision(broom, broom.getBoundingBox().inflate(-0.1D))) {
                    return InteractionResultHolder.fail(itemStack);
                }
                level.addFreshEntity(broom);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.success(itemStack);
            }
        }
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSounds.BROOM_SWING.get(), attacker.getSoundSource(), 1.0F, 1.0F);
        target.level().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.BROOM_IMPACT.get(), attacker.getSoundSource(), 1.0F, 1.0F);
        return true;
    }

    public static void setOwner(@Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            // ItemStack root NBT was removed in 1.21; keep broom ownership in CUSTOM_DATA.
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                tag.putUUID(OWNER, entity.getUUID());
                tag.putString(OWNER_NAME, entity.getDisplayName().getString());
            });
        }
    }

    @Nullable
    public static UUID getOwnerID(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains(OWNER) ? tag.getUUID(OWNER) : null;
    }

    @Nullable
    public static Entity getOwner(Level level, UUID uuid) {
        return level instanceof ServerLevel serverLevel ? serverLevel.getEntity(uuid) : null;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        return this.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(ModEnchantments.VELOCITY)
                || enchantment.is(ModEnchantments.BURNING)
                || enchantment.is(ModEnchantments.FEALTY)
                || enchantment.is(ModEnchantments.HARDY);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(OWNER_NAME)) {
            tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY))
                    .append(Component.literal(tag.getString(OWNER_NAME)).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY))));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
