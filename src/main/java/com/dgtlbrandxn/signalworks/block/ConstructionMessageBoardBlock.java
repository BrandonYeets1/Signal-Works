package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.blockentity.ConstructionMessageBoardBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Portable changeable-message sign trailer with a deployable amber LED display. */
public final class ConstructionMessageBoardBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<ConstructionMessageBoardBlock> CODEC = simpleCodec(ConstructionMessageBoardBlock::new);
    public static final BooleanProperty DEPLOYED = BooleanProperty.create("deployed");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private static final VoxelShape BASE_SHAPE = Block.box(0.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);

    public ConstructionMessageBoardBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(DEPLOYED, true)
                .setValue(LIT, true));
    }

    @Override
    protected MapCodec<? extends ConstructionMessageBoardBlock> codec() {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConstructionMessageBoardBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof MenuProvider provider ? provider : null;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (player.isShiftKeyDown()) {
                boolean deployed = !state.getValue(DEPLOYED);
                BlockState updated = state.setValue(DEPLOYED, deployed);
                if (!deployed) {
                    updated = updated.setValue(LIT, false);
                }
                level.setBlock(pos, updated, Block.UPDATE_ALL);
                player.displayClientMessage(Component.translatable(deployed
                        ? "message.trafficcontrol.message_board_deployed"
                        : "message.trafficcontrol.message_board_stowed"), true);
            } else {
                MenuProvider provider = getMenuProvider(state, level, pos);
                if (provider != null) {
                    serverPlayer.openMenu(provider);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
