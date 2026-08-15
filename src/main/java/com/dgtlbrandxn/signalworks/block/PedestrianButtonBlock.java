package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.blockentity.TrafficLightBlockEntity;
import com.dgtlbrandxn.signalworks.registry.ModSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** A compact pole/wall-mounted pedestrian call button. */
public final class PedestrianButtonBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<PedestrianButtonBlock> CODEC = simpleCodec(PedestrianButtonBlock::new);
    private static final int SEARCH_RADIUS = 16;

    private static final VoxelShape NORTH_SHAPE = box(5.0D, 2.0D, 0.0D, 11.0D, 15.0D, 3.5D);
    private static final VoxelShape SOUTH_SHAPE = box(5.0D, 2.0D, 12.5D, 11.0D, 15.0D, 16.0D);
    private static final VoxelShape EAST_SHAPE = box(12.5D, 2.0D, 5.0D, 16.0D, 15.0D, 11.0D);
    private static final VoxelShape WEST_SHAPE = box(0.0D, 2.0D, 5.0D, 3.5D, 15.0D, 11.0D);

    public PedestrianButtonBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends PedestrianButtonBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        // FACING stores the support direction (the pole/wall behind the button),
        // not the direction the button face looks. Clicking the south face of a
        // pole places the button south of it, so the support is north.
        Direction facing = clicked.getAxis() != Direction.Axis.Y
                ? clicked.getOpposite()
                : context.getHorizontalDirection();
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        level.playSound(player, pos, ModSounds.PED_BUTTON.get(), SoundSource.BLOCKS, 0.9F, 1.0F);
        if (level instanceof ServerLevel server) {
            TrafficLightBlockEntity nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            for (BlockPos scan : BlockPos.betweenClosed(
                    pos.offset(-SEARCH_RADIUS, -6, -SEARCH_RADIUS),
                    pos.offset(SEARCH_RADIUS, 6, SEARCH_RADIUS))) {
                if (server.getBlockEntity(scan) instanceof TrafficLightBlockEntity signal
                        && signal.isPedestrianSignal()) {
                    double distance = scan.distSqr(pos);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = signal;
                    }
                }
            }

            int requested = 0;
            if (nearest != null) {
                Direction.Axis requestedAxis = nearest.signalAxis();
                BlockPos requestedController = nearest.linkedControllerPos();
                for (BlockPos scan : BlockPos.betweenClosed(
                        pos.offset(-SEARCH_RADIUS, -6, -SEARCH_RADIUS),
                        pos.offset(SEARCH_RADIUS, 6, SEARCH_RADIUS))) {
                    if (!(server.getBlockEntity(scan) instanceof TrafficLightBlockEntity signal)
                            || !signal.isPedestrianSignal()
                            || signal.signalAxis() != requestedAxis) {
                        continue;
                    }
                    BlockPos controller = signal.linkedControllerPos();
                    if (requestedController == null ? controller != null : !requestedController.equals(controller)) {
                        continue;
                    }
                    signal.requestWalk();
                    requested++;
                }
            }
            player.displayClientMessage(Component.translatable(
                    requested > 0
                            ? "message.trafficcontrol.walk_requested"
                            : "message.trafficcontrol.no_ped_signal"), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
