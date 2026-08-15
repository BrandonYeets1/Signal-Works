package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.blockentity.TrafficLightBlockEntity;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import com.dgtlbrandxn.signalworks.util.TrafficLightBulbType;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Compact autonomous one-lens warning flasher. */
public abstract class SignalFlasherBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = box(3.0D, 3.0D, 7.0D, 13.0D, 13.0D, 13.0D);
    private final TrafficLightBulbType bulbType;

    protected SignalFlasherBlock(BlockBehaviour.Properties properties, TrafficLightBulbType bulbType) {
        super(properties);
        this.bulbType = bulbType;
        registerDefaultState(stateDefinition.any()
                .setValue(TrafficLightBlock.ROTATION, 0)
                .setValue(TrafficLightBlock.LIT, true)
                .setValue(TrafficLightBlock.MOUNT, SignalMount.AUTO));
    }

    public final TrafficLightBulbType bulbType() {
        return bulbType;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(TrafficLightBlock.ROTATION, TrafficLightBlock.rotationFor(context))
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
        return SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
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
