package com.dgtlbrandxn.signalworks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Six-way connectable overhead utility conductor. */
public final class UtilityWireBlock extends Block {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static final VoxelShape CORE = box(7.0D, 7.0D, 7.0D, 9.0D, 9.0D, 9.0D);

    public UtilityWireBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
                .setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateAll(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        return state.setValue(property(direction), connects(neighborState));
    }

    private static boolean connects(BlockState state) {
        return state.getBlock() instanceof UtilityWireBlock
                || state.getBlock() instanceof UtilityPoleBlock
                || state.getBlock() instanceof PoleTransformerBlock
                || state.getBlock() instanceof UtilityGroundWireBlock;
    }

    private static BooleanProperty property(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH; case EAST -> EAST; case SOUTH -> SOUTH; case WEST -> WEST;
            case UP -> UP; case DOWN -> DOWN;
        };
    }

    private static BlockState updateAll(BlockState state, LevelAccessor level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            state = state.setValue(property(direction), connects(level.getBlockState(pos.relative(direction))));
        }
        return state;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, box(7,7,0,9,9,8));
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, box(7,7,8,9,9,16));
        if (state.getValue(WEST)) shape = Shapes.or(shape, box(0,7,7,8,9,9));
        if (state.getValue(EAST)) shape = Shapes.or(shape, box(8,7,7,16,9,9));
        if (state.getValue(DOWN)) shape = Shapes.or(shape, box(7,0,7,9,8,9));
        if (state.getValue(UP)) shape = Shapes.or(shape, box(7,8,7,9,16,9));
        return shape;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }
}
