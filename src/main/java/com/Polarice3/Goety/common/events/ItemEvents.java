package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.items.curios.WitchHatItem;
import com.Polarice3.Goety.common.items.equipment.*;
import com.Polarice3.Goety.common.items.magic.DarkStaff;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.compat.DiscerningEldritchCompat;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import com.Polarice3.Goety.utils.ModPotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = Goety.MOD_ID)
public class ItemEvents {
    // These immutable modifiers are shared instead of rebuilt for every living entity tick.
    private static final AttributeModifier SET_ARMOR = com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("17cb060f-0465-412e-abe7-a9c397b2e548"), "Increase Armor", 4.0D, AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier SET_TOUGHNESS = com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("c3c510ca-76eb-4eb5-9f69-6763b7e40be2"), "Increase Toughness", 4.0D, AttributeModifier.Operation.ADD_VALUE);

    @SubscribeEvent
    public static void PlayerTick(PlayerTickEvent.Post event){
        Player player = event.getEntity();
        if (true) {
            if (ItemHelper.findHelmet(player, ModItems.DARK_HELMET.get())){
                if (ItemConfig.DarkHelmetDarkness.get()) {
                    if (player.getEffect(MobEffects.DARKNESS) != null) {
                        player.removeEffect(MobEffects.DARKNESS);
                    }
                }
                if (ItemConfig.DarkHelmetBlindness.get()) {
                    if (player.getEffect(MobEffects.BLINDNESS) != null) {
                        player.removeEffect(MobEffects.BLINDNESS);
                    }
                }
            }

            Inventory inventory = player.getInventory();

            List<NonNullList<ItemStack>> compartments = ImmutableList.of(inventory.items, inventory.armor, inventory.offhand);

            for (NonNullList<ItemStack> nonnulllist : compartments) {
                for (int i = 0; i < nonnulllist.size(); ++i) {
                    if (!nonnulllist.get(i).isEmpty()) {
                        ItemStack itemStack = nonnulllist.get(i);
                        Item item = itemStack.getItem();
                        if (!(item instanceof IPersist persist) || !persist.isBroken(itemStack)) {
                            if (itemStack.getItem() instanceof ISoulRepair soulRepair) {
                                soulRepair.repairTick(nonnulllist.get(i), player, inventory.selected == i);
                            } else if (itemStack.getItem() instanceof TieredItem tieredItem && tieredItem.getTier() == ModTiers.DARK){
                                ItemHelper.repairTick(itemStack, player, inventory.selected == i);
                            }
                        }
                    }
                }
            }

            if (ItemHelper.armorSet(player, ModArmorMaterials.DARK)){
                if (player.getFoodData().needsFood()){
                    if (player.tickCount % 40 == 0){
                        player.heal(1.0F);
                    }
                }
            }
        }

        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        boolean scythe = player.getMainHandItem().getItem() instanceof DarkScytheItem;

        float increaseAttackSpeed0 = 0.25F;
        AttributeModifier attributemodifier0 = com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("0c091f42-8c6d-4fde-96e9-148115731cbf"), "Two Handed Scythe", increaseAttackSpeed0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        boolean flag0 = scythe && player.getOffhandItem().isEmpty();
        if (attackSpeed != null){
            if (flag0){
                if (!attackSpeed.hasModifier(attributemodifier0.id())){
                    attackSpeed.addPermanentModifier(attributemodifier0);
                }
            } else {
                if (attackSpeed.hasModifier(attributemodifier0.id())){
                    attackSpeed.removeModifier(attributemodifier0.id());
                }
            }
        }

        float increaseAttackSpeed = 0.5F;
        AttributeModifier attributemodifier = com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("d4818bbc-54ed-4ecf-95a3-a15fbf71b31d"), "Scythe Proficiency", increaseAttackSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        boolean flag = CuriosFinder.hasCurio(player, ModItems.GRAVE_GLOVE.get()) && (scythe || player.getMainHandItem().is(ModTags.Items.GRAVE_GLOVE_BOOST));
        if (attackSpeed != null){
            if (flag){
                if (!attackSpeed.hasModifier(attributemodifier.id())){
                    attackSpeed.addPermanentModifier(attributemodifier);
                }
            } else {
                if (attackSpeed.hasModifier(attributemodifier.id())){
                    attackSpeed.removeModifier(attributemodifier.id());
                }
            }
        }

        boolean hammer = player.getMainHandItem().getItem() instanceof HammerItem;

        float increaseAttackSpeed1 = 0.25F;
        AttributeModifier attributemodifier1 = com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("3f0d53a8-f075-4d27-a0b7-a4d923542d4f"), "Two Handed Hammer", increaseAttackSpeed1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        boolean flag1 = hammer && player.getOffhandItem().isEmpty();
        if (attackSpeed != null){
            if (flag1){
                if (!attackSpeed.hasModifier(attributemodifier1.id())){
                    attackSpeed.addPermanentModifier(attributemodifier1);
                }
            } else {
                if (attackSpeed.hasModifier(attributemodifier1.id())){
                    attackSpeed.removeModifier(attributemodifier1.id());
                }
            }
        }

        float increaseAttackSpeed2 = 0.5F;
        AttributeModifier attributemodifier2 = com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("39c01496-8161-4fde-ac2c-0bea379ceb37"), "Hammer Proficiency", increaseAttackSpeed2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        boolean flag2 = CuriosFinder.hasCurio(player, ModItems.THRASH_GLOVE.get()) && (hammer || player.getMainHandItem().is(ModTags.Items.THRASH_GLOVE_BOOST));
        if (attackSpeed != null){
            if (flag2){
                if (!attackSpeed.hasModifier(attributemodifier2.id())){
                    attackSpeed.addPermanentModifier(attributemodifier2);
                }
            } else {
                if (attackSpeed.hasModifier(attributemodifier2.id())){
                    attackSpeed.removeModifier(attributemodifier2.id());
                }
            }
        }

        boolean staff = player.getOffhandItem().getItem() instanceof DarkStaff && ItemConfig.StaffOffhandBuff.get();

        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);

        AttributeModifier attributemodifier3 = com.Polarice3.Goety.utils.ModAttributeUtil.create(UUID.fromString("6dc7952d-11a6-4bf4-954b-b527b35787c6"), "Dark Staff Proficiency", 0.25D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        if (attackDamage != null){
            if (staff){
                if (!attackDamage.hasModifier(attributemodifier3.id())){
                    attackDamage.addPermanentModifier(attributemodifier3);
                }
            } else {
                if (attackDamage.hasModifier(attributemodifier3.id())){
                    attackDamage.removeModifier(attributemodifier3.id());
                }
            }
        }
        if (MobUtil.starAmuletActive(player)){
            player.getAbilities().flying &= player.isCreative();
        }
    }

    @SubscribeEvent
    public static void LivingEffects(EntityTickEvent.Post event){
        // Entity tick events can fire for non-living entities in NeoForge 1.21, so guard the armor modifier logic.
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }
        if (livingEntity != null && livingEntity.isAlive()){
            AttributeModifier attributemodifier = SET_ARMOR;
            AttributeInstance armor = livingEntity.getAttribute(Attributes.ARMOR);
            AttributeModifier attributemodifier1 = SET_TOUGHNESS;
            AttributeInstance toughness = livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armor != null){
                if (ItemHelper.armorSet(livingEntity, ModArmorMaterials.CURSED_KNIGHT) || ItemHelper.armorSet(livingEntity, ModArmorMaterials.CURSED_PALADIN)){
                    if (!armor.hasModifier(attributemodifier.id())){
                        armor.addPermanentModifier(attributemodifier);
                    }
                } else {
                    if (armor.hasModifier(attributemodifier.id())){
                        armor.removeModifier(attributemodifier.id());
                    }
                }
            }
            if (toughness != null){
                if (ItemHelper.armorSet(livingEntity, ModArmorMaterials.CURSED_PALADIN)){
                    if (!toughness.hasModifier(attributemodifier1.id())){
                        toughness.addPermanentModifier(attributemodifier1);
                    }
                } else {
                    if (toughness.hasModifier(attributemodifier1.id())){
                        toughness.removeModifier(attributemodifier1.id());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingIncomingDamageEvent event){
        LivingEntity victim = event.getEntity();
        Entity directEntity = event.getSource().getDirectEntity();
        if (event.getAmount() > 0.0F) {
            if (directEntity instanceof LivingEntity livingAttacker) {
                if (ModDamageSource.physicalAttacks(event.getSource())) {
                    ItemHelper.setItemEffect(livingAttacker.getMainHandItem(), victim);
                    if (livingAttacker.getMainHandItem().getItem() instanceof TieredItem weapon) {
                        if (weapon == ModItems.FANGED_DAGGER.get()){
                            Holder<MobEffect> effect = MobEffects.POISON;
                            if (CuriosFinder.hasWildRobe(livingAttacker)){
                                effect = GoetyEffects.ACID_VENOM;
                            }
                            if (livingAttacker.hasEffect(GoetyEffects.VENOMOUS_HANDS)){
                                EffectsUtil.increaseDuration(victim, effect.value(), 600);
                            } else {
                                victim.addEffect(new MobEffectInstance(effect, 200));
                            }
                        }
                        if (weapon == ModItems.FROZEN_BLADE.get()) {
                            if (victim.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                                event.setAmount(event.getAmount() * 2);
                            }
                        }
                        if (weapon == ModItems.HUNGRY_DAGGER.get()){
                            int soulEat = MobUtil.getEnchantmentLevel(livingAttacker, ModEnchantments.SOUL_EATER) + 1;
                            livingAttacker.heal(event.getAmount() * (0.05F * soulEat));
                        }
                        if (weapon instanceof DarkScytheItem) {
                            victim.playSound(ModSounds.SCYTHE_HIT_MEATY.get());
                        }
                        if (weapon instanceof DeathScytheItem) {
                            if (ItemConfig.DeathScytheSappedDuration.get() > 0) {
                                int seconds = MathHelper.secondsToTicks(ItemConfig.DeathScytheSappedDuration.get());
                                if (!victim.hasEffect(GoetyEffects.SAPPED)) {
                                    victim.addEffect(new MobEffectInstance(GoetyEffects.SAPPED, seconds));
                                    victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                                } else {
                                    if (ItemConfig.DeathScytheSappedChance.get() > 0 && victim.level().random.nextFloat() <= (ItemConfig.DeathScytheSappedChance.get() / 100.0F)) {
                                        EffectsUtil.amplifyEffect(victim, GoetyEffects.SAPPED.get(), seconds);
                                        victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                                    } else {
                                        EffectsUtil.resetDuration(victim, GoetyEffects.SAPPED.get(), seconds);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (victim instanceof Player player) {
            if (CuriosFinder.hasCurio(victim, ModItems.SPITEFUL_BELT.get())) {
                Holder<Enchantment> thorns = victim.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS);
                int a = EnchantmentHelper.getTagEnchantmentLevel(thorns, CuriosFinder.findCurio(victim, ModItems.SPITEFUL_BELT.get()));
                if (SEHelper.getSoulsAmount(player, ItemConfig.SpitefulBeltUseAmount.get() * (a + 1))) {
                    if (!event.getSource().is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS) && !event.getSource().is(DamageTypes.THORNS) && event.getSource().getEntity() instanceof LivingEntity livingentity && livingentity != victim) {
                        livingentity.hurt(livingentity.damageSources().thorns(victim), 2.0F + a);
                        SEHelper.decreaseSouls(player, ItemConfig.SpitefulBeltUseAmount.get() * (a + 1));
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void OnLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        Entity directEntity = event.getSource().getDirectEntity();
        // NeoForge 1.21 splits LivingDamageEvent into phases; Pre is the closest match for this pre-application effect hook.
        if (event.getNewDamage() > 0.0F) {
            if (directEntity instanceof LivingEntity livingAttacker) {
                if (ModDamageSource.physicalAttacks(event.getSource())) {
                    if (livingAttacker.getMainHandItem().getItem() instanceof TieredItem weapon) {
                        if (weapon == ModItems.BLADE_OF_ENDER.get()) {
                            Holder<MobEffect> effect = GoetyEffects.VOID_TOUCHED;
                            int amp = 0;
                            if (!livingAttacker.hasEffect(effect)) {
                                victim.addEffect(new MobEffectInstance(effect, MathHelper.secondsToTicks(5), amp, false, true));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void OnLivingJump(LivingEvent.LivingJumpEvent event){
        if (event.getEntity() instanceof Player player) {
            if (CuriosFinder.hasCurio(player, ModItems.WAYFARERS_BELT.get())){
                float f = 0.625F;
                if (player.hasEffect(MobEffects.JUMP)){
                    f += 0.1F * (float)(player.getEffect(MobEffects.JUMP).getAmplifier() + 1);
                }
                Vec3 vector3d = player.getDeltaMovement();
                player.setDeltaMovement(vector3d.x, f, vector3d.z);
            }
        }

    }

    @SubscribeEvent
    public static void OnLivingFall(LivingFallEvent event){
        if (event.getEntity() instanceof Player player) {
            if (CuriosFinder.hasCurio(player, ModItems.WAYFARERS_BELT.get())){
                event.setDistance(event.getDistance() / 2);
            }
        }
    }

    /*@SubscribeEvent
    public static void usingItemEvents(LivingEntityUseItemEvent.Tick event){
        if (!event.getEntity().level.isClientSide) {
            if (event.getItem().getItem() instanceof IWand && CuriosFinder.hasCurio(event.getEntity(), ModItems.TARGETING_MONOCLE.get())) {
                Entity entity = MobUtil.getSingleTarget(event.getEntity().level, event.getEntity(), 16, 3);
                if (entity instanceof LivingEntity living && !MobUtil.areAllies(entity, event.getEntity())) {
                    event.getEntity().lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(living.getX(), living.getEyeY(), living.getZ()));
                }
            }
        }
    }*/

    @SubscribeEvent
    public static void PlayerInteractBlockEvents(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockHitResult blockHitResult = event.getHitVec();
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        ItemStack itemStack = event.getItemStack();
        // Empty stacks also use the no-effect potion fallback, so require an actual water potion bottle.
        if (itemStack.is(Items.POTION) && ModPotionUtil.getPotion(itemStack) == Potions.WATER){
            if (event.getFace() != Direction.DOWN && blockState.is(ModBlocks.END_SOIL.get())) {
                level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                if (!level.isClientSide) {
                    ServerLevel serverlevel = (ServerLevel)level;

                    for(int i = 0; i < 5; ++i) {
                        serverlevel.sendParticles(ParticleTypes.SPLASH, (double)blockPos.getX() + level.random.nextDouble(), (double)(blockPos.getY() + 1), (double)blockPos.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    }
                }

                level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                level.setBlockAndUpdate(blockPos, ModBlocks.END_MUD.get().defaultBlockState());
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            }
        } else if (itemStack.is(Items.GLASS_BOTTLE)) {
            if (event.getFace() != Direction.DOWN && blockState.is(ModBlocks.END_MUD.get())) {
                level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(itemStack, player, new ItemStack(ModItems.END_MUD_BOTTLE.get())));
                player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                if (!level.isClientSide) {
                    ServerLevel serverlevel = (ServerLevel)level;

                    for(int i = 0; i < 5; ++i) {
                        serverlevel.sendParticles(ParticleTypes.SPLASH, (double)blockPos.getX() + level.random.nextDouble(), (double)(blockPos.getY() + 1), (double)blockPos.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    }
                }

                level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                level.setBlockAndUpdate(blockPos, ModBlocks.END_SOIL.get().defaultBlockState());
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            }
        } else if (itemStack.canPerformAction(ItemAbilities.PICKAXE_DIG)) {
            if (blockState.is(ModBlocks.COBBLED_OMINOUS_STONE_BLOCK.get())) {
                if (blockHitResult.getDirection() != Direction.DOWN) {
                    if (level.isEmptyBlock(blockPos.above())) {
                        BlockState blockstate2 = ModBlocks.COBBLED_OMINOUS_STONE_PATH_BLOCK.get().defaultBlockState();
                        level.playSound(player, blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!level.isClientSide) {
                            level.setBlock(blockPos, blockstate2, 11);
                            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockstate2));
                            if (player != null) {
                                ItemHelper.hurtAndBreak(itemStack, 1, player);
                                player.swing(event.getHand());
                            }
                        }
                        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void UseItemEvent(LivingEntityUseItemEvent.Finish event){
        if (CuriosFinder.hasCurio(event.getEntity(), ModItems.CRONE_HAT.get())){
            if (event.getEntity().level().random.nextFloat() <= 0.25F){
                if (event.getItem().getItem() instanceof PotionItem){
                    event.setResultStack(event.getItem());
                }
            }
            if (event.getEntity().level().random.nextFloat() <= 0.1F){
                if (event.getItem().getItem() instanceof BrewItem){
                    event.setResultStack(event.getItem());
                }
            }
        } else if (CuriosFinder.hasCurio(event.getEntity(), itemStack -> itemStack.getItem() instanceof WitchHatItem)){
            if (event.getEntity().level().random.nextFloat() <= 0.1F){
                if (event.getItem().getItem() instanceof PotionItem){
                    event.setResultStack(event.getItem());
                }
            }
        }
        if (event.getEntity() instanceof Player player) {
            if (MainConfig.WandCoolItemUse.get()) {
                if (!(event.getItem().getItem() instanceof IWand)) {
                    Item main = event.getEntity().getMainHandItem().getItem();
                    Item off = event.getEntity().getOffhandItem().getItem();
                    if (main instanceof IWand) {
                        player.getCooldowns().addCooldown(main, 10);
                    } else if (off instanceof IWand) {
                        player.getCooldowns().addCooldown(off, 10);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void AxeDeath(LivingDeathEvent event){
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        Level world = killed.getCommandSenderWorld();
        if (killer instanceof LivingEntity livingEntity) {
            if (ModDamageSource.physicalAttacks(event.getSource()) && livingEntity.getMainHandItem().getItem() instanceof RampagingAxeItem) {
                MobEffectInstance effectinstance1 = livingEntity.getEffect(GoetyEffects.RAMPAGE);
                if (!livingEntity.hasEffect(GoetyEffects.RAMPAGE)){
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.RAMPAGE, MathHelper.secondsToTicks(ItemConfig.RampagingAxeDuration.get())));
                } else if (effectinstance1 != null){
                    int random = killed.getMaxHealth() >= 20 ? 0 : world.random.nextInt(4);
                    if (effectinstance1.getAmplifier() < 4) {
                        if (random == 0) {
                            EffectsUtil.amplifyEffect(livingEntity, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(ItemConfig.RampagingAxeDuration.get()));
                        }
                    } else {
                        livingEntity.removeEffect(GoetyEffects.RAMPAGE);
                        if (world instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(new ShockwaveParticleOption(), livingEntity.getX(), livingEntity.getY() + 0.5F, livingEntity.getZ(), 0, 0.0D, 0.0D, 0.0D, 0);
                            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 1.0D, 0.0D, 0.0D, 0.5F);
                        }
                        LootingExplosion.Mode lootMode = CuriosFinder.hasWanting(livingEntity) ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
                        ExplosionUtil.lootExplode(world, livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 3.0F, false, Explosion.BlockInteraction.KEEP, lootMode);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void EmptyClickEvents(PlayerInteractEvent.LeftClickEmpty event){
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof DeathScytheItem) {
            DeathScytheItem.emptyClick(itemStack);
        } else if (itemStack.getItem() instanceof BladeOfEnderItem && !player.getCooldowns().isOnCooldown(itemStack.getItem())) {
            BladeOfEnderItem.emptyClick(itemStack);
        }
    }

    @SubscribeEvent
    public static void PlayerAttackEvents(AttackEntityEvent event){
        Player player = event.getEntity();
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() instanceof DeathScytheItem) {
            DeathScytheItem.entityClick(player, player.level());
        } else if (itemStack.getItem() instanceof BladeOfEnderItem) {
            BladeOfEnderItem.entityClick(player, player.level());
        }
    }

    @SubscribeEvent
    public static void InteractEntityEvents(PlayerInteractEvent.EntityInteract event){
        Item item = event.getItemStack().getItem();
        if (item instanceof ReviveServantItem){
            if (SEHelper.isOnCooldown(event.getEntity(), event.getItemStack())){
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
        /*if (item instanceof IWand || item instanceof GoodwillGrimoire || item instanceof GrudgeGrimoire) {
            if (event.getTarget() instanceof Villager villager) {
                InteractionResult result = event.getItemStack().interactLivingEntity(event.getEntity(), villager, event.getHand());
                if (result.consumesAction()) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }*/
    }

    @SubscribeEvent
    public static void GeneralInteractEvents(PlayerInteractEvent.RightClickItem event) {
        DiscerningEldritchCompat.initializeSoulFireScythe(event.getItemStack());
        if (event.getItemStack().is(Items.GLASS_BOTTLE)) {
            InteractionResultHolder<ItemStack> result = ItemHelper.getVoidBottle(event.getEntity(), event.getLevel(), event.getHand());
            if (result.getResult().consumesAction()) {
                event.setCanceled(true);
                event.setCancellationResult(result.getResult());
            }
        }
    }
}
