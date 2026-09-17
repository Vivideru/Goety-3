package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.ConfiguredItemUtil;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class EeriePickaxeItem extends PickaxeItem {
    public EeriePickaxeItem() {
        // In 1.21 the custom properties must declare durability explicitly so the enchanting table treats the pickaxe as enchantable.
        super(ModTiers.SPECIAL, (new Properties()).rarity(Rarity.UNCOMMON).durability(ModTiers.SPECIAL.getUses()).attributes(createEerieAttributes()));
    }

    private static net.minecraft.world.item.component.ItemAttributeModifiers createEerieAttributes() {
        return DiggerItem.createAttributes(ModTiers.SPECIAL, 1, -2.8F)
                // NeoForge's old BLOCK_REACH attribute is vanilla BLOCK_INTERACTION_RANGE in 1.21.
                .withModifierAdded(Attributes.BLOCK_INTERACTION_RANGE, com.Polarice3.Goety.utils.ModAttributeUtil.create("tool_modifier_eerie_pickaxe_reach", 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Default components are built before common configs are loaded in 1.21, so configurable durability has to be read at stack query time.
        return ConfiguredItemUtil.durability(ItemConfig.SpecialToolsDurability, ModTiers.SPECIAL.getUses());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        float speed = super.getDestroySpeed(stack, blockState);
        // NeoForge 1.21 uses the TOOL component for mining speed; preserve the tier speed if it falls back to hand speed.
        return speed <= 1.0F && blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) ? ModTiers.SPECIAL.getSpeed() : speed;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof Player player) {
            if (isSelected){
                if (worldIn.getRandom().nextFloat() <= 7.5E-4F){
                    int i = 17;
                    BlockPos blockpos = BlockPos.containing(player.getX() + (double)worldIn.getRandom().nextInt(i) - (double)8, player.getEyeY() + (double)worldIn.getRandom().nextInt(i) - (double)8, player.getZ() + (double)worldIn.getRandom().nextInt(i) - (double)8);
                    double d0 = (double)blockpos.getX() + 0.5D;
                    double d1 = (double)blockpos.getY() + 0.5D;
                    double d2 = (double)blockpos.getZ() + 0.5D;
                    double d3 = d0 - player.getX();
                    double d4 = d1 - player.getEyeY();
                    double d5 = d2 - player.getZ();
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                    double d7 = d6 + 2.0D;
                    BlockPos blockPos = BlockPos.containing(player.getX() + d3 / d6 * d7, player.getEyeY() + d4 / d6 * d7, player.getZ() + d5 / d6 * d7);
                    SoundEvent soundEvent = SoundEvents.AMBIENT_CAVE.value();
                    if (worldIn.getRandom().nextFloat() <= 0.01F){
                        soundEvent = SoundEvents.GOAT_SCREAMING_AMBIENT;
                    } else if (worldIn.getRandom().nextFloat() <= 0.05F){
                        soundEvent = SoundEvents.WARDEN_NEARBY_CLOSE;
                    } else if (worldIn.getRandom().nextFloat() <= 0.15F){
                        soundEvent = SoundEvents.SCULK_SHRIEKER_SHRIEK;
                    } else if (worldIn.getRandom().nextFloat() <= 0.25F){
                        soundEvent = SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value();
                        // Open skies occasionally replace the ambient tone with a phantom pass-by.
                        if (worldIn.getRandom().nextBoolean()
                                && (!worldIn.dimensionType().hasSkyLight()
                                || player.getY() >= worldIn.getSeaLevel() && worldIn.canSeeSky(blockpos))) {
                            soundEvent = SoundEvents.PHANTOM_FLAP;
                        }
                    }
                    worldIn.playSound(player, blockPos, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                if (!MobUtil.isShifting(player)){
                    for (Entity entity : worldIn.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(10.0F))){
                        if (entity instanceof ExperienceOrb experienceOrb){
                            experienceOrb.playerTouch(player);
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    //Siphon's redundant on Eerie Pickaxe.
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        ResourceLocation enchantmentId = enchantment.unwrapKey().map(ResourceKey::location).orElse(null);
        return !Objects.equals(enchantmentId, ResourceLocation.fromNamespaceAndPath("vanillatweaks", "siphon"))
                && super.supportsEnchantment(stack, enchantment);
    }
}
