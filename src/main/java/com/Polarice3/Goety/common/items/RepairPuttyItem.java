package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.items.equipment.HammerItem;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RepairPuttyItem extends Item {
    public RepairPuttyItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide && player.getOffhandItem().getItem() instanceof HammerItem && target instanceof Mob mob) {
            boolean repairable = target instanceof IronGolem || target.getType().is(ModTags.EntityTypes.REPAIRABLE);
            boolean safe = mob.getTarget() == null || (mob.getTarget() != player && !MobUtil.areAllies(mob.getTarget(), player));
            if (repairable && (safe || target instanceof IronGolem golem && golem.isPlayerCreated())) {
                mob.heal(25.0F);
                mob.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }
}
