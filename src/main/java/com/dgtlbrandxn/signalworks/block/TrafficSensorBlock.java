package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Road detector that produces a phase call while a vehicle-sized entity is above it.
 *
 * <p>The legacy lane sensors plus the bus detector describe the requested movement:
 * left, straight, or right. Their horizontal facing determines the approach axis.</p>
 */
public final class TrafficSensorBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<TrafficSensorBlock> CODEC = simpleCodec(TrafficSensorBlock::new);
    public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");

    private static final int SCAN_INTERVAL_TICKS = 5;
    // Same 48x48 model-unit footprint as the road decals: three blocks wide/deep.
    private static final AABB LOCAL_DETECTION_BOX = new AABB(-1.0D, 0.0D, -1.0D, 2.0D, 2.25D, 2.0D);
    private static final VoxelShape SELECTION_SHAPE = box(-16.0D, 0.0D, -16.0D, 32.0D, 0.1D, 32.0D);

    public TrafficSensorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OCCUPIED, false));
    }

    @Override
    protected MapCodec<? extends TrafficSensorBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(OCCUPIED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OCCUPIED);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        detectEntity(level, pos, state, entity);
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        detectEntity(level, pos, state, entity);
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean occupied = hasDetectableEntity(level, pos, this);
        setOccupied(level, pos, state, occupied);
        // Keep scanning even while empty so entities anywhere on the extended 3x3
        // detector surface can create a call without first touching the center block.
        level.scheduleTick(pos, this, SCAN_INTERVAL_TICKS);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SELECTION_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
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
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(OCCUPIED) ? 15 : 0;
    }

    public SignalMovement movement() {
        if (this == ModBlocks.TRAFFIC_SENSOR_LEFT.get()) {
            return SignalMovement.LEFT;
        }
        if (this == ModBlocks.TRAFFIC_SENSOR_RIGHT.get()) {
            return SignalMovement.RIGHT;
        }
        if (this == ModBlocks.TRAFFIC_SENSOR_BUS.get()) {
            return SignalMovement.BUS;
        }
        return SignalMovement.THROUGH;
    }

    private void detectEntity(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && isDetectable(entity)) {
            setOccupied(level, pos, state, true);
            level.scheduleTick(pos, this, 1);
        }
    }

    private static boolean hasDetectableEntity(ServerLevel level, BlockPos pos, TrafficSensorBlock sensor) {
        AABB box = LOCAL_DETECTION_BOX.move(pos);
        List<Entity> entities = level.getEntities((Entity) null, box, sensor::isDetectable);
        return !entities.isEmpty();
    }

    private boolean isDetectable(Entity entity) {
        boolean vehicleSized = entity.isAlive()
                && !entity.isSpectator()
                && !(entity instanceof ItemEntity)
                && !(entity instanceof ExperienceOrb)
                && !(entity instanceof Projectile)
                && (entity.getBbWidth() >= 0.5F || entity.getBbHeight() >= 0.8F);
        if (!vehicleSized) {
            return false;
        }
        if (this != ModBlocks.TRAFFIC_SENSOR_BUS.get()) {
            return true;
        }
        String path = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath().toLowerCase(java.util.Locale.ROOT);
        return entity.getTags().contains("signalworks_bus")
                || entity.getTags().contains("bus")
                || path.contains("bus");
    }

    private static void setOccupied(Level level, BlockPos pos, BlockState state, boolean occupied) {
        if (state.getValue(OCCUPIED) == occupied) {
            return;
        }
        BlockState next = state.setValue(OCCUPIED, occupied);
        level.setBlock(pos, next, Block.UPDATE_ALL);
        level.updateNeighborsAt(pos, next.getBlock());
        level.updateNeighborsAt(pos.below(), next.getBlock());
    }
}
