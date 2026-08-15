package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Adjustable traffic camera with wall and mast brackets and a real directional scan cone.
 * The existing block IDs are retained so old worlds upgrade in place.
 */
public class TrafficCameraBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<TrafficCameraBlock> CODEC = simpleCodec(TrafficCameraBlock::new);
    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");
    public static final EnumProperty<CameraTilt> TILT = EnumProperty.create("tilt", CameraTilt.class);
    public static final EnumProperty<CameraMount> MOUNT = EnumProperty.create("mount", CameraMount.class);

    private static final int SCAN_INTERVAL = 10;
    private static final double SCAN_DISTANCE = 18.0D;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 16.0D);

    public TrafficCameraBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TRIGGERED, false)
                .setValue(TILT, CameraTilt.DOWN_22)
                .setValue(MOUNT, CameraMount.WALL));
    }

    @Override
    protected MapCodec<? extends TrafficCameraBlock> codec() {
        return CODEC;
    }

    protected boolean requiresRedSignal() {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        CameraMount mount = switch (context.getClickedFace()) {
            case UP -> CameraMount.MAST;
            case DOWN -> CameraMount.HANGING;
            default -> CameraMount.WALL;
        };
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(MOUNT, mount);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED, TILT, MOUNT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean enabled = !requiresRedSignal() || level.hasNeighborSignal(pos);
        boolean triggered = enabled && detectTraffic(level, pos, state);
        if (state.getValue(TRIGGERED) != triggered) {
            level.setBlock(pos, state.setValue(TRIGGERED, triggered), Block.UPDATE_ALL);
            level.updateNeighborsAt(pos, this);
        }
        level.scheduleTick(pos, this, SCAN_INTERVAL);
    }

    private static boolean detectTraffic(ServerLevel level, BlockPos pos, BlockState state) {
        CameraMount mount = state.getValue(MOUNT);
        double originOffsetY = mount == CameraMount.MAST ? 0.18D
                : mount == CameraMount.HANGING ? -0.08D : 0.0D;
        Vec3 origin = Vec3.atCenterOf(pos).add(0.0D, originOffsetY, 0.0D);
        Direction facing = state.getValue(FACING);
        double radians = Math.toRadians(state.getValue(TILT).degrees());
        double horizontal = Math.cos(radians);
        Vec3 direction = new Vec3(
                facing.getStepX() * horizontal,
                Math.sin(radians),
                facing.getStepZ() * horizontal
        ).normalize();

        AABB searchBox = new AABB(pos).inflate(SCAN_DISTANCE, SCAN_DISTANCE, SCAN_DISTANCE);
        List<Entity> candidates = level.getEntitiesOfClass(Entity.class, searchBox, entity ->
                entity.isAlive()
                        && !entity.isSpectator()
                        && !(entity instanceof ItemEntity)
                        && !(entity instanceof ExperienceOrb));

        for (Entity entity : candidates) {
            Vec3 delta = entity.getBoundingBox().getCenter().subtract(origin);
            double projection = delta.dot(direction);
            if (projection < 1.0D || projection > SCAN_DISTANCE) {
                continue;
            }
            double perpendicularSquared = Math.max(0.0D, delta.lengthSqr() - projection * projection);
            double coneRadius = 0.8D + projection * 0.16D;
            if (perpendicularSquared <= coneRadius * coneRadius) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(TRIGGERED) ? 15 : 0;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!level.isClientSide) {
            BlockState adjusted;
            Component message;
            if (player.isShiftKeyDown()) {
                Direction facing = state.getValue(FACING).getClockWise();
                adjusted = state.setValue(FACING, facing);
                message = Component.translatable(
                        "message.trafficcontrol.camera_right_click_rotated",
                        facing.getName().toUpperCase(java.util.Locale.ROOT)
                );
            } else {
                CameraTilt tilt = state.getValue(TILT).step(1);
                adjusted = state.setValue(TILT, tilt);
                message = Component.translatable(
                        "message.trafficcontrol.camera_right_click_tilt",
                        tilt.getSerializedName().replace('_', ' ').toUpperCase(java.util.Locale.ROOT)
                );
            }
            level.setBlock(pos, adjusted, Block.UPDATE_ALL);
            player.displayClientMessage(message, true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
