package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.runtime.ConstructionIllumination;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Portable tow-behind floodlight tower with deploy and generator controls. */
public final class ConstructionFloodlightBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<ConstructionFloodlightBlock> CODEC = simpleCodec(ConstructionFloodlightBlock::new);
    public static final BooleanProperty DEPLOYED = BooleanProperty.create("deployed");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private static final VoxelShape BASE_SHAPE = Block.box(1.0D, 0.0D, 2.0D, 15.0D, 12.0D, 14.0D);

    public ConstructionFloodlightBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(DEPLOYED, false)
                .setValue(LIT, false));
    }

    @Override
    protected MapCodec<? extends ConstructionFloodlightBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, DEPLOYED, LIT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE_SHAPE;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide) {
            refreshLighting(level, pos, state);
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        refreshLighting(level, pos, state);
        level.scheduleTick(pos, this, 20);
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
            ConstructionIllumination.remove(level, pos, state.getValue(FACING));
            BlockState updated;
            Component message;
            if (player.isShiftKeyDown()) {
                boolean deployed = !state.getValue(DEPLOYED);
                updated = state.setValue(DEPLOYED, deployed).setValue(LIT, deployed && state.getValue(LIT));
                message = Component.translatable(deployed
                        ? "message.trafficcontrol.floodlight_deployed"
                        : "message.trafficcontrol.floodlight_stowed");
            } else if (!state.getValue(DEPLOYED)) {
                updated = state.setValue(DEPLOYED, true).setValue(LIT, true);
                message = Component.translatable("message.trafficcontrol.floodlight_deployed_on");
            } else {
                boolean lit = !state.getValue(LIT);
                updated = state.setValue(LIT, lit);
                message = Component.translatable(lit
                        ? "message.trafficcontrol.floodlight_on"
                        : "message.trafficcontrol.floodlight_off");
            }
            level.setBlock(pos, updated, Block.UPDATE_ALL);
            refreshLighting(level, pos, updated);
            level.scheduleTick(pos, this, 20);
            player.displayClientMessage(message, true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            ConstructionIllumination.remove(level, pos, state.getValue(FACING));
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static void refreshLighting(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(DEPLOYED) && state.getValue(LIT)) {
            ConstructionIllumination.ensure(level, pos, state.getValue(FACING));
        } else {
            ConstructionIllumination.remove(level, pos, state.getValue(FACING));
        }
    }
}
