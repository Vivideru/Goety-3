package com.Vivideru.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.AbstractBeam;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A crystal mounted on any face of a block. Each hit from a Goety spell toggles it; while on it powers the block it sits on,
 * strongly enough for the mechanisms behind a one-block wall.
 */
public class PolarCrystalBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int TOGGLE_COOLDOWN = 10;
    private static final VoxelShape UP = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 7.0D, 13.0D);
    private static final VoxelShape DOWN = Block.box(3.0D, 9.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    private static final VoxelShape NORTH = Block.box(3.0D, 3.0D, 9.0D, 13.0D, 13.0D, 16.0D);
    private static final VoxelShape SOUTH = Block.box(3.0D, 3.0D, 0.0D, 13.0D, 13.0D, 7.0D);
    private static final VoxelShape EAST = Block.box(0.0D, 3.0D, 3.0D, 7.0D, 13.0D, 13.0D);
    private static final VoxelShape WEST = Block.box(9.0D, 3.0D, 3.0D, 16.0D, 13.0D, 13.0D);
    /** What Goety spells hit: most of the block, so a shot or a beam that grazes the crystal still toggles it. */
    private static final VoxelShape SPELL_UP = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 12.0D, 15.0D);
    private static final VoxelShape SPELL_DOWN = Block.box(1.0D, 4.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private static final VoxelShape SPELL_NORTH = Block.box(1.0D, 1.0D, 4.0D, 15.0D, 15.0D, 16.0D);
    private static final VoxelShape SPELL_SOUTH = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 12.0D);
    private static final VoxelShape SPELL_EAST = Block.box(0.0D, 1.0D, 1.0D, 12.0D, 15.0D, 15.0D);
    private static final VoxelShape SPELL_WEST = Block.box(4.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);

    public PolarCrystalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.UP).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> UP;
        };
    }

    /**
     * Spell projectiles and beams trace their hits against the collision shape with themselves as context: they get the
     * larger spell shape, while players and mobs keep bumping into the crystal as it looks.
     */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && isSpell(entityContext.getEntity())) {
            return switch (state.getValue(FACING)) {
                case DOWN -> SPELL_DOWN;
                case NORTH -> SPELL_NORTH;
                case SOUTH -> SPELL_SOUTH;
                case EAST -> SPELL_EAST;
                case WEST -> SPELL_WEST;
                default -> SPELL_UP;
            };
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    private static boolean isSpell(@Nullable Entity entity) {
        return (entity instanceof Projectile || entity instanceof AbstractBeam)
                && BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace().equals(Goety.MOD_ID);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos support = pos.relative(direction.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level, support, direction);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) && state.getValue(FACING) == direction ? 15 : 0;
    }

    /** A spell hit flips the crystal, with a short cooldown so continuous beams count as one hit. */
    public static void hit(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof PolarCrystalBlock crystal) || level.getBlockTicks().hasScheduledTick(pos, crystal)) {
            return;
        }
        boolean powered = !state.getValue(POWERED);
        level.setBlock(pos, state.setValue(POWERED, powered), 3);
        level.scheduleTick(pos, crystal, TOGGLE_COOLDOWN);
        Direction back = state.getValue(FACING).getOpposite();
        level.updateNeighborsAt(pos, crystal);
        level.updateNeighborsAt(pos.relative(back), crystal);
        level.updateNeighborsAt(pos.relative(back, 2), crystal);
        level.playSound(null, pos, powered ? SoundEvents.AMETHYST_BLOCK_CHIME : SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, powered ? 1.2F : 0.8F);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    }

    /** True for a solid block that a glowing crystal sits on: it then passes the strong signal to the blocks around it. */
    public static boolean carriesCrystalSignal(BlockGetter level, BlockState state, BlockPos pos) {
        if (!state.isRedstoneConductor(level, pos)) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            BlockState neighbour = level.getBlockState(pos.relative(direction));
            if (neighbour.getBlock() instanceof PolarCrystalBlock && neighbour.getValue(POWERED) && neighbour.getValue(FACING) == direction) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!movedByPiston && !state.is(newState.getBlock()) && state.getValue(POWERED)) {
            Direction back = state.getValue(FACING).getOpposite();
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.relative(back), this);
            level.updateNeighborsAt(pos.relative(back, 2), this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
