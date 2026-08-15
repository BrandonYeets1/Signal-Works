package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Directional Jersey-style concrete barrier with automatic straight-run joins.
 *
 * FACING represents the broad face of the barrier. The physical run axis is
 * perpendicular to that direction, matching how a barrier is normally placed
 * across the player's view. Four connection properties remove the end caps
 * between compatible neighboring segments.
 */
public final class ConcreteBarrierBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<ConcreteBarrierBlock> CODEC = simpleCodec(ConcreteBarrierBlock::new);

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    private static final VoxelShape NORTH_SOUTH_SHAPE = Shapes.or(
            box(5.5D, 0.0D, 0.0D, 10.5D, 16.0D, 16.0D),
            box(1.5D, 0.0D, 0.0D, 5.5D, 5.3D, 16.0D),
            box(10.5D, 0.0D, 0.0D, 14.5D, 5.3D, 16.0D)
    );
    private static final VoxelShape EAST_WEST_SHAPE = Shapes.or(
            box(0.0D, 0.0D, 5.5D, 16.0D, 16.0D, 10.5D),
            box(0.0D, 0.0D, 1.5D, 16.0D, 5.3D, 5.5D),
            box(0.0D, 0.0D, 10.5D, 16.0D, 5.3D, 14.5D)
    );

    public ConcreteBarrierBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false));
    }

    @Override
    protected MapCodec<? extends ConcreteBarrierBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // The broad face points back toward the player; the barrier run is 90 degrees to it.
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();

        boolean northSouthRun = isBarrierOnRunAxis(context.getLevel().getBlockState(pos.north()), Direction.Axis.Z)
                || isBarrierOnRunAxis(context.getLevel().getBlockState(pos.south()), Direction.Axis.Z);
        boolean eastWestRun = isBarrierOnRunAxis(context.getLevel().getBlockState(pos.east()), Direction.Axis.X)
                || isBarrierOnRunAxis(context.getLevel().getBlockState(pos.west()), Direction.Axis.X);

        // Snap to an existing straight run when only one neighboring axis is present.
        if (northSouthRun && !eastWestRun) {
            facing = facing.getAxis() == Direction.Axis.X ? facing : Direction.EAST;
        } else if (eastWestRun && !northSouthRun) {
            facing = facing.getAxis() == Direction.Axis.Z ? facing : Direction.NORTH;
        }

        return updateConnections(defaultBlockState().setValue(FACING, facing), context.getLevel(), pos);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos neighborPos
    ) {
        BooleanProperty property = propertyFor(direction);
        if (property == null) {
            return state;
        }
        return state.setValue(property, connectsTo(state, neighborState, direction));
    }

    private BlockState updateConnections(BlockState state, LevelAccessor level, BlockPos pos) {
        return state
                .setValue(NORTH, connectsTo(state, level.getBlockState(pos.north()), Direction.NORTH))
                .setValue(EAST, connectsTo(state, level.getBlockState(pos.east()), Direction.EAST))
                .setValue(SOUTH, connectsTo(state, level.getBlockState(pos.south()), Direction.SOUTH))
                .setValue(WEST, connectsTo(state, level.getBlockState(pos.west()), Direction.WEST));
    }

    private static boolean connectsTo(BlockState state, BlockState neighborState, Direction direction) {
        if (!(neighborState.getBlock() instanceof ConcreteBarrierBlock)) {
            return false;
        }

        Direction.Axis runAxis = runAxis(state);
        return direction.getAxis() == runAxis && runAxis(neighborState) == runAxis;
    }

    private static boolean isBarrierOnRunAxis(BlockState state, Direction.Axis axis) {
        return state.getBlock() instanceof ConcreteBarrierBlock && runAxis(state) == axis;
    }

    private static Direction.Axis runAxis(BlockState state) {
        Direction.Axis facingAxis = state.getValue(FACING).getAxis();
        return facingAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
    }

    @Nullable
    private static BooleanProperty propertyFor(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> null;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return runAxis(state) == Direction.Axis.X ? EAST_WEST_SHAPE : NORTH_SOUTH_SHAPE;
    }
}
