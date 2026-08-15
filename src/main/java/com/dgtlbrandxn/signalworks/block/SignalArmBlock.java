package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.blockentity.SignalArmBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
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

/**
 * A pole-mounted, bidirectional signal cross-arm with one to five usable head slots.
 *
 * The anchor occupies one block beside a vertical signal pole. Its rendered beam may
 * extend up to four blocks along the local left/right axis, while the block state
 * preserves both wing lengths and the desired signal-facing direction.
 */
public final class SignalArmBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<SignalArmBlock> CODEC = simpleCodec(SignalArmBlock::new);
    public static final IntegerProperty LEFT_LENGTH = IntegerProperty.create("left_length", 0, 4);
    public static final IntegerProperty RIGHT_LENGTH = IntegerProperty.create("right_length", 0, 4);
    public static final BooleanProperty REVERSED = BooleanProperty.create("reversed");
    public static final EnumProperty<SignalArmSize> HEAD_SIZE = EnumProperty.create("head_size", SignalArmSize.class);
    public static final EnumProperty<SignalArmType> ARM_TYPE = EnumProperty.create("arm_type", SignalArmType.class);
    public static final BooleanProperty MULTI = BooleanProperty.create("multi");

    public static final int MAX_SIGNAL_SLOTS = 5;
    public static final int MAX_WING_LENGTH = 4;
    public static final int MAX_HANGER_DROP = 4;

    // Compact bracket selection shapes. The Blockbench model itself extends slightly
    // outside the block like the real back-mounted signal bracket.
    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            box(6.5D, 0.0D, 0.0D, 9.5D, 16.0D, 9.5D),
            box(6.5D, 13.5D, 0.0D, 9.5D, 16.0D, 16.0D)
    );
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 16.0D),
            box(6.5D, 13.5D, 0.0D, 9.5D, 16.0D, 16.0D)
    );
    private static final VoxelShape EAST_SHAPE = Shapes.or(
            box(6.5D, 0.0D, 6.5D, 16.0D, 16.0D, 9.5D),
            box(0.0D, 13.5D, 6.5D, 16.0D, 16.0D, 9.5D)
    );
    private static final VoxelShape WEST_SHAPE = Shapes.or(
            box(0.0D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D),
            box(0.0D, 13.5D, 6.5D, 16.0D, 16.0D, 9.5D)
    );

    public SignalArmBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(LEFT_LENGTH, 0)
                .setValue(RIGHT_LENGTH, 0)
                .setValue(REVERSED, false)
                .setValue(HEAD_SIZE, SignalArmSize.THREE)
                .setValue(ARM_TYPE, SignalArmType.MAST)
                .setValue(MULTI, false));
    }

    @Override
    protected MapCodec<? extends SignalArmBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction facing = clickedFace.getAxis().isHorizontal()
                ? clickedFace
                : context.getHorizontalDirection().getOpposite();
        BlockState placed = defaultBlockState().setValue(FACING, facing);
        return syncAdjacentArms(placed, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos neighborPos
    ) {
        if (!direction.getAxis().isHorizontal()) {
            return state;
        }
        return syncAdjacentArms(state, level, currentPos);
    }

    private static BlockState syncAdjacentArms(BlockState state, LevelAccessor level, BlockPos pos) {
        Direction right = rightDirection(state);
        BlockState leftNeighbor = level.getBlockState(pos.relative(right.getOpposite()));
        BlockState rightNeighbor = level.getBlockState(pos.relative(right));

        boolean multi = state.getValue(MULTI);
        boolean leftConnected = multi && isCompatibleNeighbor(state, leftNeighbor);
        boolean rightConnected = multi && isCompatibleNeighbor(state, rightNeighbor);

        int currentLeft = state.getValue(LEFT_LENGTH);
        int currentRight = state.getValue(RIGHT_LENGTH);

        // Values 2-4 are retained for old adjustable-arm worlds. New compact arms
        // automatically use 0/1 to represent a physically adjacent merged bracket.
        int nextLeft = currentLeft > 1 ? currentLeft : (leftConnected ? 1 : 0);
        int nextRight = currentRight > 1 ? currentRight : (rightConnected ? 1 : 0);
        return state.setValue(LEFT_LENGTH, nextLeft).setValue(RIGHT_LENGTH, nextRight);
    }

    private static boolean isCompatibleNeighbor(BlockState state, BlockState neighbor) {
        return neighbor.getBlock() instanceof SignalArmBlock
                && neighbor.hasProperty(FACING)
                && neighbor.hasProperty(MULTI)
                && neighbor.getValue(MULTI)
                && neighbor.getValue(FACING) == state.getValue(FACING)
                && neighbor.getValue(ARM_TYPE) == state.getValue(ARM_TYPE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEFT_LENGTH, RIGHT_LENGTH, REVERSED, HEAD_SIZE, ARM_TYPE, MULTI);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SignalArmBlockEntity(pos, state);
    }

    public static BlockState cycleHeadSize(BlockState state, int amount) {
        return state.setValue(HEAD_SIZE, state.getValue(HEAD_SIZE).step(amount));
    }

    public static BlockState cycleArmType(BlockState state, int amount) {
        return state.setValue(ARM_TYPE, state.getValue(ARM_TYPE).step(amount));
    }

    public static BlockState toggleMulti(BlockState state) {
        return state.setValue(MULTI, !state.getValue(MULTI));
    }

    public static int slotCount(BlockState state) {
        return 1 + state.getValue(LEFT_LENGTH) + state.getValue(RIGHT_LENGTH);
    }

    public static Direction rightDirection(BlockState state) {
        return state.getValue(FACING).getClockWise();
    }

    public static Direction signalFacing(BlockState state) {
        Direction awayFromPole = state.getValue(FACING);
        return state.getValue(REVERSED) ? awayFromPole : awayFromPole.getOpposite();
    }

    public static int signalRotation(BlockState state) {
        return rotationForDirection(signalFacing(state));
    }

    public static int rotationForDirection(Direction direction) {
        return switch (direction) {
            case SOUTH -> 0;
            case EAST -> 4;
            case NORTH -> 8;
            case WEST -> 12;
            default -> 0;
        };
    }

    public static BlockPos slotPosition(BlockPos armPos, BlockState state, int offset) {
        return armPos.relative(rightDirection(state), offset);
    }

    public static boolean isOffsetEnabled(BlockState state, int offset) {
        if (offset < 0) {
            return -offset <= state.getValue(LEFT_LENGTH);
        }
        return offset <= state.getValue(RIGHT_LENGTH);
    }

    @Nullable
    public static Integer slotOffset(BlockPos armPos, BlockState state, BlockPos targetPos) {
        Direction facing = state.getValue(FACING);
        Direction right = facing.getClockWise();
        int dx = targetPos.getX() - armPos.getX();
        int dz = targetPos.getZ() - armPos.getZ();
        int forwardOffset = dx * facing.getStepX() + dz * facing.getStepZ();
        if (forwardOffset != 0) {
            return null;
        }
        int offset = dx * right.getStepX() + dz * right.getStepZ();
        return isOffsetEnabled(state, offset) ? offset : null;
    }

    /** Locates the nearest enabled virtual arm slot above a prospective or existing signal. */
    @Nullable
    public static ArmMount findArmAbove(Level level, BlockPos signalPos) {
        for (int height = 1; height <= MAX_HANGER_DROP; height++) {
            BlockPos plane = signalPos.above(height);
            for (int radius = 0; radius <= MAX_WING_LENGTH; radius++) {
                ArmMount match = testCandidate(level, signalPos, plane.offset(radius, 0, 0), height);
                if (match != null) return match;
                if (radius > 0) {
                    match = testCandidate(level, signalPos, plane.offset(-radius, 0, 0), height);
                    if (match != null) return match;
                    match = testCandidate(level, signalPos, plane.offset(0, 0, radius), height);
                    if (match != null) return match;
                    match = testCandidate(level, signalPos, plane.offset(0, 0, -radius), height);
                    if (match != null) return match;
                }
            }
        }
        return null;
    }

    @Nullable
    private static ArmMount testCandidate(Level level, BlockPos signalPos, BlockPos armPos, int height) {
        BlockState state = level.getBlockState(armPos);
        if (!(state.getBlock() instanceof SignalArmBlock)) {
            return null;
        }
        Integer offset = slotOffset(armPos, state, signalPos);
        if (offset == null) {
            return null;
        }
        return new ArmMount(armPos.immutable(), state, height, offset, supportColor(level, armPos, state), supportWidth(level, armPos, state));
    }

    public static int supportColor(Level level, BlockPos armPos, BlockState armState) {
        // Adjustable signal arms are standardized black regardless of the pole finish.
        return 0xFF000000 | RotatablePoleBlock.SupportColor.BLACK.rgb();
    }

    public static float supportWidth(Level level, BlockPos armPos, BlockState armState) {
        BlockPos polePos = armPos.relative(armState.getValue(FACING).getOpposite());
        BlockState poleState = level.getBlockState(polePos);
        if (poleState.getBlock() instanceof RotatablePoleBlock support) {
            return (float) support.widthPixels();
        }
        return 4.0F;
    }

    /** Refuses to retract a slot while any block occupies its hanger column. */
    public static boolean removedSlotIsOccupied(Level level, BlockPos armPos, BlockState state, boolean rightWing) {
        int current = state.getValue(rightWing ? RIGHT_LENGTH : LEFT_LENGTH);
        if (current <= 0) {
            return false;
        }
        int offset = rightWing ? current : -current;
        BlockPos slot = slotPosition(armPos, state, offset);
        for (int drop = 1; drop <= MAX_HANGER_DROP; drop++) {
            if (!level.getBlockState(slot.below(drop)).isAir()) {
                return true;
            }
        }
        return false;
    }

    public static boolean extensionSpaceIsClear(Level level, BlockPos armPos, BlockState state, boolean rightWing) {
        int current = state.getValue(rightWing ? RIGHT_LENGTH : LEFT_LENGTH);
        int nextOffset = rightWing ? current + 1 : -(current + 1);
        BlockPos next = slotPosition(armPos, state, nextOffset);
        return level.getBlockState(next).isAir();
    }

    public record ArmMount(
            BlockPos armPos,
            BlockState state,
            int heightBlocks,
            int slotOffset,
            int color,
            float armWidthPixels
    ) {
        public Direction signalFacing() {
            return SignalArmBlock.signalFacing(state);
        }

        public int signalRotation() {
            return SignalArmBlock.signalRotation(state);
        }
    }
}
