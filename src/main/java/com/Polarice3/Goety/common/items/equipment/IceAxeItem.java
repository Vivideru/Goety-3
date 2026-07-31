package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class IceAxeItem extends DiggerItem {
    // The server interaction constant is no longer exposed in 1.21; keep the previous practical reach used by this short-range block check.
    private static final double MAX_ICE_AXE_DISTANCE = 5.0D;
    private final int maxDamage;

    public IceAxeItem(Tier tier, Properties properties) {
        super(tier, BlockTags.ICE, properties.attributes(DiggerItem.createAttributes(tier, 6.0F, -3.1F)));
        this.maxDamage = tier.getUses();
    }

    public IceAxeItem(Tier tier, Properties properties, int maxDamage) {
        super(tier, BlockTags.ICE, properties.durability(maxDamage).attributes(DiggerItem.createAttributes(tier, 6.0F, -3.1F)));
        this.maxDamage = maxDamage;
    }

    public IceAxeItem(Tier tier) {
        // In 1.21 the tier no longer injects durability into these custom properties, and the enchanting table rejects non-damageable items.
        this(tier, new Properties(), tier.getUses());
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        // Custom durability must be reported at stack query time in 1.21 or tier defaults can override special variants.
        return this.maxDamage;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        Player player = context.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        if (player != null) {
            ItemStack itemStack = context.getItemInHand();
            if (direction.getAxis().isHorizontal() && (blockstate.isSolidRender(level, blockpos) || blockstate.is(BlockTags.ICE))) {
                ItemHelper.hurtAndBreak(itemStack, 1, player);
                double yDelta = 0.52D;
                player.swing(context.getHand());
                player.setDeltaMovement(player.getDeltaMovement().x(), yDelta, player.getDeltaMovement().z());
                player.resetFallDistance();
            } else if (player.onGround() && direction == Direction.UP && (blockstate.isSolidRender(level, blockpos) || blockstate.is(BlockTags.ICE))) {
                player.startUsingItem(context.getHand());
            }
        }
        return super.useOn(context);
    }

    public UseAnim getUseAnimation(ItemStack p_273490_) {
        return UseAnim.BOW;
    }

    public int getUseDuration(ItemStack p_272765_) {
        return 72000;
    }

    public void onUseTick(Level p_273467_, LivingEntity p_273619_, ItemStack p_273316_, int p_273101_) {
        if (p_273101_ >= 0 && p_273619_ instanceof Player player && player.onGround()) {
            HitResult hitresult = this.calculateHitResult(p_273619_);
            if (hitresult instanceof BlockHitResult blockhitresult) {
                if (hitresult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = blockhitresult.getBlockPos();
                    BlockState blockState = p_273467_.getBlockState(blockPos);
                    if (blockhitresult.getDirection() == Direction.UP && (blockState.isSolidRender(p_273467_, blockPos) || blockState.is(BlockTags.ICE))) {
                        player.addEffect(new MobEffectInstance(GoetyEffects.TANGLED, 2, 0, false, false));
                    } else {
                        p_273619_.releaseUsingItem();
                    }
                } else {
                    p_273619_.releaseUsingItem();
                }
            } else {
                p_273619_.releaseUsingItem();
            }
        } else {
            p_273619_.releaseUsingItem();
        }
    }

    private HitResult calculateHitResult(LivingEntity p_281264_) {
        return ProjectileUtil.getHitResultOnViewVector(p_281264_, (p_281111_) -> {
            return !p_281111_.isSpectator() && p_281111_.isPickable();
        }, MAX_ICE_AXE_DISTANCE);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;
        ChatFormatting secondary = ChatFormatting.BLUE;
        tooltip.add(Component.translatable("info.goety.ice_axe").withStyle(main));
        tooltip.add(Component.translatable("info.goety.ice_axe.push").withStyle(secondary));
    }
}
