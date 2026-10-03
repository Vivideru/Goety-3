package com.Polarice3.Goety.common.events.spell;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.ICancellableEvent;

import org.jetbrains.annotations.Nullable;

public class BlockMagicEvent extends BlockEvent implements ICancellableEvent {
    private ISpell spell;
    @Nullable
    private final Direction direction;
    private final LivingEntity caster;

    public BlockMagicEvent(LevelAccessor level, BlockPos pos, BlockState state, ISpell spell, @Nullable Direction direction, LivingEntity caster) {
        super(level, pos, state);
        this.spell = spell;
        this.direction = direction;
        this.caster = caster;
    }

    public ISpell getSpell() {
        return this.spell;
    }

    public void setSpell(ISpell spell) {
        this.spell = spell;
    }

    @Nullable
    public Direction getDirection() {
        return this.direction;
    }

    public LivingEntity getCaster() {
        return this.caster;
    }
}
