package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.blockentity.TrafficLightBlockEntity;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Four-section signal with circular red/yellow/green plus a protected-left green arrow. */
public final class TrafficLight4Block extends Block implements EntityBlock {
    public static final MapCodec<TrafficLight4Block> CODEC = simpleCodec(TrafficLight4Block::new);
    public static final IntegerProperty ROTATION = TrafficLightBlock.ROTATION;
    public static final BooleanProperty LIT = TrafficLightBlock.LIT;
    public static final EnumProperty<SignalMount> MOUNT = TrafficLightBlock.MOUNT;

    private static final VoxelShape SHAPE_0 = box(3.0, 0.0, 7.0, 13.0, 32.0, 12.0);
    private static final VoxelShape SHAPE_4 = box(4.0, 0.0, 3.0, 9.0, 32.0, 13.0);
    private static final VoxelShape SHAPE_8 = box(3.0, 0.0, 4.0, 13.0, 32.0, 9.0);
    private static final VoxelShape SHAPE_12 = box(7.0, 0.0, 3.0, 12.0, 32.0, 13.0);
    private static final VoxelShape SHAPE_HALF_DIAGONAL = box(3.2, 0.0, 3.2, 12.8, 32.0, 12.8);
    private static final VoxelShape SHAPE_DIAGONAL = box(4.0, 0.0, 4.0, 12.0, 32.0, 12.0);

    public TrafficLight4Block(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ROTATION, 0).setValue(LIT, true).setValue(MOUNT, SignalMount.AUTO));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = TrafficLightBlock.rotationFor(context);
        return defaultBlockState()
                .setValue(ROTATION, rotation)
                .setValue(MOUNT, TrafficLightBlock.mountFor(context));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, LIT, MOUNT);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(ROTATION)) {
            case 0 -> SHAPE_0;
            case 4 -> SHAPE_4;
            case 8 -> SHAPE_8;
            case 12 -> SHAPE_12;
            case 2, 6, 10, 14 -> SHAPE_HALF_DIAGONAL;
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
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
