package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.runtime.StreetLightIllumination;
import com.dgtlbrandxn.signalworks.runtime.StreetLightRuntime;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
 * JSON-model roadway luminaire supplied through the expanded street-light catalog.
 *
 * <p>The fixture has a day/off and night/on block state. At night, the block's
 * own light value gives an immediate local glow while invisible helper lights
 * spread level-15 illumination below and ahead of the fixture.</p>
 */
public final class StreetLightModelBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<StreetLightModelBlock> CODEC = simpleCodec(StreetLightModelBlock::new);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    private static final int CHECK_INTERVAL = 20;
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(3.0D, 2.0D, 0.0D, 13.0D, 14.0D, 16.0D);
    private static final VoxelShape EAST_WEST_SHAPE = box(0.0D, 2.0D, 3.0D, 16.0D, 14.0D, 13.0D);

    public StreetLightModelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    @Override
    protected MapCodec<? extends StreetLightModelBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        Direction facing = clicked.getAxis() != Direction.Axis.Y
                ? clicked
                : context.getHorizontalDirection().getOpposite();

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(LIT, shouldBeLit(context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level instanceof ServerLevel serverLevel) {
            StreetLightRuntime.register(serverLevel, pos);
            refreshLighting(serverLevel, pos, state);
            serverLevel.scheduleTick(pos, this, CHECK_INTERVAL);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        refreshLighting(level, pos, state);
        level.scheduleTick(pos, this, CHECK_INTERVAL);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        StreetLightRuntime.register(level, pos);
        refreshLighting(level, pos, state);
        level.scheduleTick(pos, this, CHECK_INTERVAL);
    }

    @Override
    protected void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block neighborBlock,
            BlockPos neighborPos,
            boolean movedByPiston
    ) {
        if (level instanceof ServerLevel serverLevel) {
            refreshLighting(serverLevel, pos, state);
            serverLevel.scheduleTick(pos, this, 1);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            StreetLightRuntime.unregister(serverLevel, pos);
            removeLightSources(serverLevel, pos, state.getValue(FACING));
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /** Called by scheduled ticks and the chunk-load runtime index. */
    public void refreshLighting(ServerLevel level, BlockPos pos, BlockState state) {
        boolean lit = shouldBeLit(level, pos);
        BlockState current = state;

        if (state.getValue(LIT) != lit) {
            current = state.setValue(LIT, lit);
            level.setBlock(pos, current, Block.UPDATE_ALL);
        }

        Direction facing = current.getValue(FACING);
        if (lit) {
            ensureLightSources(level, pos, facing);
        } else {
            removeLightSources(level, pos, facing);
        }
    }

    private static boolean shouldBeLit(Level level, BlockPos pos) {
        return level.isNight() && !level.hasNeighborSignal(pos);
    }

    private static void ensureLightSources(Level level, BlockPos pos, Direction facing) {
        StreetLightIllumination.ensure(level, pos, facing.getStepX(), facing.getStepZ());
    }

    private static void removeLightSources(Level level, BlockPos pos, Direction facing) {
        StreetLightIllumination.remove(level, pos, facing.getStepX(), facing.getStepZ());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X
                ? EAST_WEST_SHAPE
                : NORTH_SOUTH_SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
