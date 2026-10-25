package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.api.entities.IOwned;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * The spawn egg of every extra servant: which servant it holds is in its entity data. A plain use spawns the mob without
 * an owner; crouching makes the user its owner, as with Goety's own servant eggs.
 */
public class ExtraServantSpawnEggItem extends Item {
    public ExtraServantSpawnEggItem(Properties properties) {
        super(properties);
    }

    @Nullable
    public static ExtraServantDefinition definition(ItemStack stack) {
        CustomData data = stack.get(DataComponents.ENTITY_DATA);
        if (data == null) {
            return null;
        }
        ResourceLocation id = ResourceLocation.tryParse(data.copyTag().getString("id"));
        return id == null ? null : ExtraServants.definition(BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        ExtraServantDefinition definition = definition(stack);
        EntityType<?> type = definition == null ? null : ExtraServants.type(definition);
        if (type == null) {
            return InteractionResult.FAIL;
        }
        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState state = level.getBlockState(clicked);
        BlockPos pos = state.getCollisionShape(level, clicked).isEmpty() ? clicked : clicked.relative(face);
        Entity entity = type.spawn(serverLevel, stack, context.getPlayer(), pos, MobSpawnType.SPAWN_EGG, true,
                !Objects.equals(clicked, pos) && face == Direction.UP);
        if (entity != null) {
            bind(entity, context.getPlayer());
            stack.shrink(1);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, clicked);
        }
        return InteractionResult.CONSUME;
    }

    private static void bind(Entity entity, @Nullable Player player) {
        if (entity instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
        if (player == null || !player.isCrouching() || !(entity instanceof IOwned owned)) {
            return;
        }
        owned.setNatural(false);
        owned.setHasLifespan(false);
        owned.setHostile(false);
        owned.setTrueOwner(player);
        if (entity instanceof Mob mob) {
            mob.setTarget(null);
            mob.setLastHurtByMob(null);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        EntityType<?> parent = ExtraServants.parentType(definition(stack));
        return parent != null ? Component.translatable("item.goety.extra_servant_spawn_egg", parent.getDescription()) : super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.goety.servant.spawn_egg").withStyle(ChatFormatting.GOLD));
    }
}
