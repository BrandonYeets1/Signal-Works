package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Thin, dedicated roadway-luminaire arm. The three registered arm blocks use
 * separate editable JSON models, while sharing placement, dye and hitbox logic.
 */
public final class StreetLightArmBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<StreetLightArmBlock> CODEC = simpleCodec(StreetLightArmBlock::new);
    public static final EnumProperty<RotatablePoleBlock.SupportColor> COLOR =
            EnumProperty.create("color", RotatablePoleBlock.SupportColor.class);

    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            box(5.5D, 5.0D, 0.0D, 10.5D, 13.0D, 16.0D),
            box(6.5D, 7.0D, -16.0D, 9.5D, 12.5D, 8.0D));
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            box(5.5D, 5.0D, 0.0D, 10.5D, 13.0D, 16.0D),
            box(6.5D, 7.0D, 8.0D, 9.5D, 12.5D, 32.0D));
    private static final VoxelShape EAST_SHAPE = Shapes.or(
            box(0.0D, 5.0D, 5.5D, 16.0D, 13.0D, 10.5D),
            box(8.0D, 7.0D, 6.5D, 32.0D, 12.5D, 9.5D));
    private static final VoxelShape WEST_SHAPE = Shapes.or(
            box(0.0D, 5.0D, 5.5D, 16.0D, 13.0D, 10.5D),
            box(-16.0D, 7.0D, 6.5D, 8.0D, 12.5D, 9.5D));

    public StreetLightArmBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(COLOR, RotatablePoleBlock.SupportColor.GALVANIZED));
    }

    @Override
    protected MapCodec<? extends StreetLightArmBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        Direction facing = clicked.getAxis().isHorizontal()
                ? clicked
                : context.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        RotatablePoleBlock.SupportColor requested = RotatablePoleBlock.SupportColor.fromItem(stack.getItem());
        if (requested == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (state.getValue(COLOR) == requested) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(COLOR, requested), Block.UPDATE_ALL);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLOR);
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
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
}
