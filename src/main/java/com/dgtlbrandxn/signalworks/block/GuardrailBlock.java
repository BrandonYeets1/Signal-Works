package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.registry.ModBlocks;
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
 * Connectable roadside guardrail family.
 *
 * <p>The existing {@code traffic_rail} registry id is retained as the normal
 * middle section. New end and LED sections participate in the same straight
 * run and automatically snap to a neighboring guardrail axis.</p>
 */
public final class GuardrailBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<GuardrailBlock> CODEC = simpleCodec(GuardrailBlock::new);
    public static final BooleanProperty FRONT = BooleanProperty.create("front");
    public static final BooleanProperty BACK = BooleanProperty.create("back");

    private static final VoxelShape NORTH_SOUTH_SHAPE = Shapes.or(
            box(5.0D, 0.0D, 0.0D, 11.0D, 13.5D, 16.0D),
            box(3.0D, 7.0D, 0.0D, 13.0D, 13.5D, 16.0D)
    );
    private static final VoxelShape EAST_WEST_SHAPE = Shapes.or(
            box(0.0D, 0.0D, 5.0D, 16.0D, 13.5D, 11.0D),
            box(0.0D, 7.0D, 3.0D, 16.0D, 13.5D, 13.0D)
    );

    public GuardrailBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FRONT, false)
                .setValue(BACK, false));
    }

    @Override
    protected MapCodec<? extends GuardrailBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection();

        // Snap to an existing straight guardrail run when possible.
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState neighbor = context.getLevel().getBlockState(pos.relative(direction));
            if (neighbor.getBlock() instanceof GuardrailBlock) {
                facing = neighbor.getValue(FACING).getAxis() == Direction.Axis.X ? Direction.EAST : Direction.NORTH;
                break;
            }
        }

        // A terminal automatically points away from its single neighboring run.
        if (isEndSection()) {
            Direction onlyNeighbor = null;
            int count = 0;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (context.getLevel().getBlockState(pos.relative(direction)).getBlock() instanceof GuardrailBlock) {
                    onlyNeighbor = direction;
                    count++;
                }
            }
            if (count == 1 && onlyNeighbor != null) {
                facing = onlyNeighbor.getOpposite();
            }
        }

        return updateConnections(defaultBlockState().setValue(FACING, facing), context.getLevel(), pos);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (direction.getAxis().isVertical()) {
            return state;
        }
        return updateConnections(state, level, currentPos);
    }

    private BlockState updateConnections(BlockState state, LevelAccessor level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return state
                .setValue(FRONT, connects(state, level.getBlockState(pos.relative(facing))))
                .setValue(BACK, connects(state, level.getBlockState(pos.relative(facing.getOpposite()))));
    }

    private static boolean connects(BlockState ownState, BlockState neighborState) {
        if (!(neighborState.getBlock() instanceof GuardrailBlock)) {
            return false;
        }
        return neighborState.getValue(FACING).getAxis() == ownState.getValue(FACING).getAxis();
    }

    public boolean isEndSection() {
        return this == ModBlocks.GUARDRAIL_END.get();
    }

    public boolean isLedSection() {
        return this == ModBlocks.GUARDRAIL_LED.get();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? NORTH_SOUTH_SHAPE : EAST_WEST_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FRONT, BACK);
    }
}
