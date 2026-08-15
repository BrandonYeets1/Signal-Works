package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.blockentity.TrafficLightBlockEntity;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Shared implementation for dedicated three-section left/right arrow heads. */
public abstract class DedicatedTurnSignalBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE_0 = box(3.0, -5.0, 7.0, 13.0, 16.0, 12.0);
    private static final VoxelShape SHAPE_4 = box(4.0, -5.0, 3.0, 9.0, 16.0, 13.0);
    private static final VoxelShape SHAPE_8 = box(3.0, -5.0, 4.0, 13.0, 16.0, 9.0);
    private static final VoxelShape SHAPE_12 = box(7.0, -5.0, 3.0, 12.0, 16.0, 13.0);
    private static final VoxelShape SHAPE_DIAGONAL = box(3.0, -5.0, 3.0, 13.0, 16.0, 13.0);

    private final boolean rightTurn;

    protected DedicatedTurnSignalBlock(BlockBehaviour.Properties properties, boolean rightTurn) {
        super(properties);
        this.rightTurn = rightTurn;
        registerDefaultState(stateDefinition.any()
                .setValue(TrafficLightBlock.ROTATION, 0)
                .setValue(TrafficLightBlock.LIT, true)
                .setValue(TrafficLightBlock.MOUNT, SignalMount.AUTO));
    }

    public final boolean isRightTurn() {
        return rightTurn;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = TrafficLightBlock.rotationFor(context);
        return defaultBlockState()
                .setValue(TrafficLightBlock.ROTATION, rotation)
                .setValue(TrafficLightBlock.MOUNT, TrafficLightBlock.mountFor(context));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TrafficLightBlock.ROTATION, TrafficLightBlock.LIT, TrafficLightBlock.MOUNT);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(TrafficLightBlock.ROTATION)) {
            case 0 -> SHAPE_0;
            case 4 -> SHAPE_4;
            case 8 -> SHAPE_8;
            case 12 -> SHAPE_12;
            default -> SHAPE_DIAGONAL;
        };
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrafficLightBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        BlockEntityTicker<TrafficLightBlockEntity> ticker = level.isClientSide
                ? TrafficLightBlockEntity::clientTick
                : TrafficLightBlockEntity::serverTick;
        return createTickerHelper(type, ModBlockEntities.TRAFFIC_LIGHT.get(), ticker);
    }

    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> actualType,
            BlockEntityType<E> expectedType,
            BlockEntityTicker<? super E> ticker
    ) {
        return actualType == expectedType ? (BlockEntityTicker<A>) ticker : null;
    }
}
