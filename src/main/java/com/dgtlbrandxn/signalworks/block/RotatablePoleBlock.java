package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Modular support block used by the small, medium and large pole/mast families.
 *
 * It keeps a facing axis for the mast's always-present straight segment and six
 * fence-style connection flags. Pole blocks render a vertical center post plus
 * conditional horizontal branches; mast blocks render a straight core plus
 * conditional collars/branches. Neighbor changes update the flags automatically.
 */
public final class RotatablePoleBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<RotatablePoleBlock> CODEC = simpleCodec(RotatablePoleBlock::new);

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    /** True for the grounded pole foot or the first mast segment attached to a pole. */
    public static final BooleanProperty BASE = BooleanProperty.create("base");
    public static final EnumProperty<SupportColor> COLOR = EnumProperty.create("color", SupportColor.class);

    private final double widthPixels;
    private final boolean mastArm;
    private final VoxelShape poleShape;
    private final VoxelShape mastNorthSouthShape;
    private final VoxelShape mastEastWestShape;
    private final VoxelShape northConnector;
    private final VoxelShape eastConnector;
    private final VoxelShape southConnector;
    private final VoxelShape westConnector;
    private final VoxelShape downConnector;

    public RotatablePoleBlock(BlockBehaviour.Properties properties) {
        this(properties, 4.0D, 0.0D);
    }

    public RotatablePoleBlock(BlockBehaviour.Properties properties, double widthPixels, double armLengthPixels) {
        super(properties);
        this.widthPixels = widthPixels;
        this.mastArm = armLengthPixels > 0.0D;

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(BASE, true)
                .setValue(COLOR, SupportColor.GALVANIZED));

        double min = (16.0D - widthPixels) / 2.0D;
        double max = min + widthPixels;
        double half = widthPixels / 2.0D;
        double low = 8.0D - half;
        double high = 8.0D + half;

        poleShape = box(min, 0.0D, min, max, 16.0D, max);
        mastNorthSouthShape = box(min, low, 0.0D, max, high, 16.0D);
        mastEastWestShape = box(0.0D, low, min, 16.0D, high, max);
        northConnector = box(min, low, 0.0D, max, high, 8.0D);
        eastConnector = box(8.0D, low, min, 16.0D, high, max);
        southConnector = box(min, low, 8.0D, max, high, 16.0D);
        westConnector = box(0.0D, low, min, 8.0D, high, max);
        downConnector = box(min, 0.0D, min, max, 8.0D, max);
    }

    @Override
    protected MapCodec<? extends RotatablePoleBlock> codec() {
        return CODEC;
    }

    public boolean isMastArm() {
        return mastArm;
    }

    public double widthPixels() {
        return widthPixels;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction facing = clickedFace.getAxis() != Direction.Axis.Y
                ? clickedFace
                : context.getHorizontalDirection();

        BlockState state = defaultBlockState().setValue(FACING, facing);
        return updateSegmentType(updateConnections(state, context.getLevel(), context.getClickedPos()),
                context.getLevel(), context.getClickedPos());
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            net.minecraft.world.level.Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        SupportColor requested = SupportColor.fromItem(stack.getItem());
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
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos neighborPos
    ) {
        BooleanProperty property = propertyFor(direction);
        BlockState updated = property == null
                ? state
                : state.setValue(property, connectsTo(state, neighborState, direction));
        return updateSegmentType(updated, level, currentPos);
    }

    /**
     * Selects the visible model without introducing extra block or item registrations.
     * Vertical supports use the flanged model only for the lowest section. Horizontal
     * mast arms use the sleeved model only for the first section attached to a pole;
     * every continuation section uses the matching *_core model.
     */
    private BlockState updateSegmentType(BlockState state, LevelAccessor level, BlockPos pos) {
        boolean baseSegment;
        if (mastArm) {
            Direction rear = state.getValue(FACING).getOpposite();
            BlockState rearState = level.getBlockState(pos.relative(rear));
            baseSegment = !(rearState.getBlock() instanceof RotatablePoleBlock rearSupport
                    && rearSupport.mastArm
                    && rearState.hasProperty(FACING)
                    && rearState.getValue(FACING) == state.getValue(FACING));
        } else {
            BlockState belowState = level.getBlockState(pos.below());
            baseSegment = !(belowState.getBlock() instanceof RotatablePoleBlock belowSupport
                    && !belowSupport.mastArm);
        }
        return state.setValue(BASE, baseSegment);
    }

    private BlockState updateConnections(BlockState state, LevelAccessor level, BlockPos pos) {
        return state
                .setValue(NORTH, connectsTo(state, level.getBlockState(pos.north()), Direction.NORTH))
                .setValue(EAST, connectsTo(state, level.getBlockState(pos.east()), Direction.EAST))
                .setValue(SOUTH, connectsTo(state, level.getBlockState(pos.south()), Direction.SOUTH))
                .setValue(WEST, connectsTo(state, level.getBlockState(pos.west()), Direction.WEST))
                .setValue(UP, connectsTo(state, level.getBlockState(pos.above()), Direction.UP))
                .setValue(DOWN, connectsTo(state, level.getBlockState(pos.below()), Direction.DOWN));
    }

    private boolean connectsTo(BlockState state, BlockState neighborState, Direction direction) {
        if (neighborState.getBlock() instanceof RotatablePoleBlock neighborSupport) {
            if (direction.getAxis() == Direction.Axis.Y) {
                // Vertical stacking is for pole sections; mast sections remain horizontal.
                return !mastArm && !neighborSupport.mastArm;
            }

            // Any support width may transition into another width. The multipart
            // collars hide the seam, while matching widths remain perfectly flush.
            return true;
        }

        if (neighborState.getBlock() instanceof StreetLightHeadBlock
                || neighborState.getBlock() instanceof StreetLightModelBlock) {
            // Roadway fixture heads attach to the end or side of a horizontal mast.
            return mastArm && direction.getAxis() != Direction.Axis.Y;
        }

        if (neighborState.getBlock() instanceof StreetLightArmBlock) {
            // Dedicated streetlight arms clamp to the side of vertical support poles.
            return !mastArm && direction.getAxis() != Direction.Axis.Y;
        }

        if (neighborState.getBlock() instanceof FreewaySignPanelBlock) {
            // Wide guide panels hang directly below the freeway sign mast.
            return mastArm && direction == Direction.DOWN;
        }

        if (isTrafficSignal(neighborState)) {
            SignalMount mount = neighborState.hasProperty(TrafficLightBlock.MOUNT)
                    ? neighborState.getValue(TrafficLightBlock.MOUNT)
                    : SignalMount.AUTO;

            // Mounting hardware is exclusive: an overhead signal connects only
            // downward from a mast; a side signal connects only horizontally to
            // a pole. AUTO keeps compatibility for signals placed before the
            // mount property existed.
            if (mastArm) {
                return direction == Direction.DOWN && mount != SignalMount.SIDE;
            }
            return direction.getAxis() != Direction.Axis.Y && mount != SignalMount.TOP;
        }

        return false;
    }

    private static boolean isTrafficSignal(BlockState state) {
        Block block = state.getBlock();
        return block instanceof TrafficLightBlock
                || block instanceof TrafficLight2Block
                || block instanceof RampMeterSignalBlock
                || block instanceof TrafficLight4Block
                || block instanceof TrafficLight5Block
                || block instanceof TrafficLightDoghouseBlock
                || block instanceof DedicatedTurnSignalBlock
                || block instanceof SignalFlasherBlock;
    }

    @Nullable
    private static BooleanProperty propertyFor(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, BASE, COLOR);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape;
        if (mastArm) {
            shape = state.getValue(FACING).getAxis() == Direction.Axis.X
                    ? mastEastWestShape
                    : mastNorthSouthShape;
        } else {
            shape = poleShape;
        }

        // Pole branches and perpendicular mast branches get real collision.
        if (state.getValue(NORTH) && (!mastArm || state.getValue(FACING).getAxis() != Direction.Axis.Z)) {
            shape = Shapes.or(shape, northConnector);
        }
        if (state.getValue(EAST) && (!mastArm || state.getValue(FACING).getAxis() != Direction.Axis.X)) {
            shape = Shapes.or(shape, eastConnector);
        }
        if (state.getValue(SOUTH) && (!mastArm || state.getValue(FACING).getAxis() != Direction.Axis.Z)) {
            shape = Shapes.or(shape, southConnector);
        }
        if (state.getValue(WEST) && (!mastArm || state.getValue(FACING).getAxis() != Direction.Axis.X)) {
            shape = Shapes.or(shape, westConnector);
        }
        if (mastArm && state.getValue(DOWN)) {
            shape = Shapes.or(shape, downConnector);
        }
        return shape;
    }

    public enum SupportColor implements StringRepresentable {
        GALVANIZED("galvanized", 0xB9C0C5),
        WHITE("white", 0xF9FFFE),
        ORANGE("orange", 0xF9801D),
        MAGENTA("magenta", 0xC74EBD),
        LIGHT_BLUE("light_blue", 0x3AB3DA),
        YELLOW("yellow", 0xFED83D),
        LIME("lime", 0x80C71F),
        PINK("pink", 0xF38BAA),
        GRAY("gray", 0x474F52),
        LIGHT_GRAY("light_gray", 0x9D9D97),
        CYAN("cyan", 0x169C9C),
        PURPLE("purple", 0x8932B8),
        BLUE("blue", 0x3C44AA),
        BROWN("brown", 0x835432),
        GREEN("green", 0x5E7C16),
        RED("red", 0xB02E26),
        BLACK("black", 0x1D1D21),
        /** Kept so existing worlds using the early municipal-green state remain valid. */
        OLD_GREEN("old_green", 0x3E654B);

        private final String serializedName;
        private final int rgb;

        SupportColor(String serializedName, int rgb) {
            this.serializedName = serializedName;
            this.rgb = rgb;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }

        public int rgb() {
            return rgb;
        }

        @Nullable
        public static SupportColor fromItem(Item item) {
            if (item == Items.WHITE_DYE) return WHITE;
            if (item == Items.ORANGE_DYE) return ORANGE;
            if (item == Items.MAGENTA_DYE) return MAGENTA;
            if (item == Items.LIGHT_BLUE_DYE) return LIGHT_BLUE;
            if (item == Items.YELLOW_DYE) return YELLOW;
            if (item == Items.LIME_DYE) return LIME;
            if (item == Items.PINK_DYE) return PINK;
            if (item == Items.GRAY_DYE) return GRAY;
            if (item == Items.LIGHT_GRAY_DYE) return LIGHT_GRAY;
            if (item == Items.CYAN_DYE) return CYAN;
            if (item == Items.PURPLE_DYE) return PURPLE;
            if (item == Items.BLUE_DYE) return BLUE;
            if (item == Items.BROWN_DYE) return BROWN;
            if (item == Items.GREEN_DYE) return GREEN;
            if (item == Items.RED_DYE) return RED;
            if (item == Items.BLACK_DYE) return BLACK;
            return null;
        }
    }


}
