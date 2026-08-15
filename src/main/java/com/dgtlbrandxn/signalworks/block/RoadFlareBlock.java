package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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

/** Low-profile emergency road flare with redstone-torch-style red emission. */
public final class RoadFlareBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<RoadFlareBlock> CODEC = simpleCodec(RoadFlareBlock::new);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private static final VoxelShape X_SHAPE = Block.box(2.0D, 0.0D, 6.0D, 14.0D, 2.5D, 10.0D);
    private static final VoxelShape Z_SHAPE = Block.box(6.0D, 0.0D, 2.0D, 10.0D, 2.5D, 14.0D);

    public RoadFlareBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, true));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(LIT, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit
    ) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.cycle(LIT), Block.UPDATE_ALL);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.16D;
        double z = pos.getZ() + 0.5D;
        if (random.nextFloat() < 0.85F) {
            level.addParticle(DustParticleOptions.REDSTONE,
                    x + (random.nextDouble() - 0.5D) * 0.18D,
                    y,
                    z + (random.nextDouble() - 0.5D) * 0.18D,
                    0.0D, 0.0D, 0.0D);
        }
        if (random.nextFloat() < 0.18F) {
            level.addParticle(ParticleTypes.SMOKE, x, y + 0.03D, z, 0.0D, 0.015D, 0.0D);
        }
    }
}
