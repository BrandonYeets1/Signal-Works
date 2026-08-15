package com.dgtlbrandxn.signalworks.blockentity;

import com.dgtlbrandxn.signalworks.block.TrafficLight1Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight2Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight4Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight5Block;
import com.dgtlbrandxn.signalworks.block.TrafficLightBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightDoghouseBlock;
import com.dgtlbrandxn.signalworks.block.SpecialThreeSectionSignalBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightUTurnBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightBusBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightStraightArrowBlock;
import com.dgtlbrandxn.signalworks.block.DedicatedTurnSignalBlock;
import com.dgtlbrandxn.signalworks.block.SignalMovement;
import com.dgtlbrandxn.signalworks.block.SignalFlasherBlock;
import com.dgtlbrandxn.signalworks.block.SignalLampTechnology;
import com.dgtlbrandxn.signalworks.block.PedestrianSignalStyle;
import com.dgtlbrandxn.signalworks.block.RampMeterSignalBlock;
import com.dgtlbrandxn.signalworks.block.SignalBackplateStyle;
import com.dgtlbrandxn.signalworks.block.SignalVisorStyle;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import com.dgtlbrandxn.signalworks.util.TrafficLightBulbType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/** Shared state and controller-free timing for all currently functional signal heads. */
public final class TrafficLightBlockEntity extends BlockEntity {
    private static final int TWO_SECTION_BULBS = 2;
    private static final int THREE_SECTION_BULBS = 3;
    private static final int FOUR_SECTION_BULBS = 4;
    private static final int FIVE_SECTION_BULBS = 5;
    private static final int MAX_BULBS = FIVE_SECTION_BULBS;
    private static final int INTERSECTION_RADIUS = 12;
    private static final int INTERSECTION_VERTICAL_RADIUS = 5;
    private static final int GROUP_SCAN_INTERVAL = 10;
    private static final int CONTROLLER_RADIUS = 16;
    private static final int CONTROLLER_VERTICAL_RADIUS = 6;

    // Realistic defaults: 8 s protected turn, 3 s yellow, 1 s turn
    // clearance, 15 s through green, 3 s through yellow and 1.5 s all-red.
    private static final int TURN_GREEN_TICKS = 160;
    private static final int TURN_YELLOW_TICKS = 60;
    private static final int TURN_CLEARANCE_TICKS = 20;
    private static final int THROUGH_GREEN_TICKS = 300;
    private static final int THROUGH_YELLOW_TICKS = 60;
    private static final int ALL_RED_TICKS = 30;
    private static final int PEDESTRIAN_CLEARANCE_TICKS = 100;
    private static final int BUS_GO_TICKS = 80;
    private static final int BUS_CAUTION_TICKS = 40;
    private static final int BUS_CLEARANCE_TICKS = 20;

    private static final String TAG_BULB_TYPES = "bulbTypes";
    private static final String TAG_ACTIVE_PREFIX = "active";
    private static final String TAG_FLASH_PREFIX = "flash";
    private static final String TAG_ALLOW_FLASH_PREFIX = "allowflash";
    private static final String TAG_LINKED_CONTROLLER = "LinkedController";
    private static final String TAG_LINKED_MOVEMENT = "LinkedMovement";
    private static final String TAG_BACKPLATE_STYLE = "BackplateStyle";
    private static final String TAG_VISOR_STYLE = "VisorStyle";
    private static final String TAG_PEDESTRIAN_STYLE = "PedestrianStyle";
    private static final String TAG_LAMP_TECHNOLOGY = "LampTechnology";
    private static final String TAG_WALK_CALL_PENDING = "WalkCallPending";
    private static final String TAG_WALK_CALL_ACTIVE = "WalkCallActive";

    private final TrafficLightBulbType[] bulbs = new TrafficLightBulbType[MAX_BULBS];
    private final boolean[] active = new boolean[MAX_BULBS];
    private final boolean[] flashing = new boolean[MAX_BULBS];
    private final boolean[] allowFlash = new boolean[MAX_BULBS];

    private boolean pigAbove;
    private int pigCheckDelay;
    private long nextGroupScan;
    private IntersectionGroup cachedGroup = IntersectionGroup.NONE;
    private ControllerProfile cachedController = ControllerProfile.DEFAULT;
    private BlockPos cachedAutoControllerPos;
    private boolean walkCallPending;
    private boolean walkCallActive;
    private boolean wasOwnThroughGreen;
    private BlockPos linkedControllerPos;
    private SignalMovement linkedMovement = SignalMovement.THROUGH;
    private boolean linkedControllerAvailable;
    private SignalBackplateStyle backplateStyle = SignalBackplateStyle.NONE;
    private SignalVisorStyle visorStyle = SignalVisorStyle.STANDARD;
    // New placements use modern US/Canada symbols; old saves without this tag load as LEGACY.
    private PedestrianSignalStyle pedestrianSignalStyle = PedestrianSignalStyle.US_CA;
    private SignalLampTechnology lampTechnology = SignalLampTechnology.DEFAULT;

    public TrafficLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRAFFIC_LIGHT.get(), pos, state);
        configureDefaultBulbs(state);
        Arrays.fill(allowFlash, true);
    }

    private void configureDefaultBulbs(BlockState state) {
        Arrays.fill(bulbs, null);
        Arrays.fill(active, false);
        Arrays.fill(flashing, false);

        if (state.getBlock() instanceof SignalFlasherBlock flasher) {
            bulbs[0] = flasher.bulbType();
        } else if (state.getBlock() instanceof TrafficLight1Block) {
            bulbs[0] = TrafficLightBulbType.RED;
        } else if (state.getBlock() instanceof TrafficLight2Block) {
            bulbs[0] = TrafficLightBulbType.DONT_CROSS;
            bulbs[1] = TrafficLightBulbType.CROSS;
        } else if (state.getBlock() instanceof RampMeterSignalBlock) {
            bulbs[0] = TrafficLightBulbType.RED;
            bulbs[1] = TrafficLightBulbType.GREEN;
        } else if (state.getBlock() instanceof TrafficLightBusBlock) {
            bulbs[0] = TrafficLightBulbType.BUS_STOP;
            bulbs[1] = TrafficLightBulbType.BUS_CAUTION;
            bulbs[2] = TrafficLightBulbType.BUS_GO;
        } else if (state.getBlock() instanceof TrafficLightStraightArrowBlock) {
            bulbs[0] = TrafficLightBulbType.STRAIGHT_RED;
            bulbs[1] = TrafficLightBulbType.STRAIGHT_YELLOW;
            bulbs[2] = TrafficLightBulbType.STRAIGHT_GREEN;
        } else if (state.getBlock() instanceof TrafficLightUTurnBlock) {
            bulbs[0] = TrafficLightBulbType.RED_ARROW_U_TURN;
            bulbs[1] = TrafficLightBulbType.YELLOW_ARROW_U_TURN;
            bulbs[2] = TrafficLightBulbType.GREEN_ARROW_U_TURN;
        } else if (state.getBlock() instanceof DedicatedTurnSignalBlock turnSignal) {
            if (turnSignal.isRightTurn()) {
                bulbs[0] = TrafficLightBulbType.RED_ARROW_RIGHT;
                bulbs[1] = TrafficLightBulbType.YELLOW_ARROW_RIGHT;
                bulbs[2] = TrafficLightBulbType.GREEN_ARROW_RIGHT;
            } else {
                bulbs[0] = TrafficLightBulbType.RED_ARROW_LEFT;
                bulbs[1] = TrafficLightBulbType.YELLOW_ARROW_LEFT;
                bulbs[2] = TrafficLightBulbType.GREEN_ARROW_LEFT;
            }
        } else if (state.getBlock() instanceof TrafficLightDoghouseBlock) {
            bulbs[0] = TrafficLightBulbType.RED;
            bulbs[1] = TrafficLightBulbType.YELLOW;
            bulbs[2] = TrafficLightBulbType.GREEN;
            bulbs[3] = TrafficLightBulbType.YELLOW_ARROW_LEFT;
            bulbs[4] = TrafficLightBulbType.GREEN_ARROW_LEFT;
        } else {
            bulbs[0] = TrafficLightBulbType.RED;
            bulbs[1] = TrafficLightBulbType.YELLOW;
            bulbs[2] = TrafficLightBulbType.GREEN;
            if (state.getBlock() instanceof TrafficLight4Block) {
                bulbs[3] = TrafficLightBulbType.GREEN_ARROW_LEFT;
            } else if (state.getBlock() instanceof TrafficLight5Block fiveSection) {
                if (fiveSection.isRightTurn()) {
                    bulbs[3] = TrafficLightBulbType.YELLOW_ARROW_RIGHT;
                    bulbs[4] = TrafficLightBulbType.GREEN_ARROW_RIGHT;
                } else {
                    bulbs[3] = TrafficLightBulbType.YELLOW_ARROW_LEFT;
                    bulbs[4] = TrafficLightBulbType.GREEN_ARROW_LEFT;
                }
            }
        }
        active[0] = true;
    }

    public int getBulbCount() {
        if (isFlasher() || isSingleSection()) {
            return 1;
        }
        if (isPedestrianSignal() || isRampMeterSignal()) {
            return TWO_SECTION_BULBS;
        }
        if (isFourSection()) {
            return FOUR_SECTION_BULBS;
        }
        return isFiveLensSignal() ? FIVE_SECTION_BULBS : THREE_SECTION_BULBS;
    }

    public boolean isFourSection() {
        return getBlockState().getBlock() instanceof TrafficLight4Block;
    }

    public boolean isFiveSection() {
        return getBlockState().getBlock() instanceof TrafficLight5Block;
    }

    public boolean isFiveRightSection() {
        return getBlockState().getBlock() instanceof TrafficLight5Block signal && signal.isRightTurn();
    }

    public boolean isDoghouse() {
        return getBlockState().getBlock() instanceof TrafficLightDoghouseBlock;
    }

    public boolean isDedicatedTurnSignal() {
        return getBlockState().getBlock() instanceof DedicatedTurnSignalBlock;
    }

    public boolean isUTurnSignal() {
        return getBlockState().getBlock() instanceof TrafficLightUTurnBlock;
    }

    public boolean isStraightArrowSignal() {
        return getBlockState().getBlock() instanceof TrafficLightStraightArrowBlock;
    }

    public boolean isBusSignal() {
        return getBlockState().getBlock() instanceof TrafficLightBusBlock;
    }

    public boolean isRightTurnSignal() {
        return (getBlockState().getBlock() instanceof DedicatedTurnSignalBlock signal && signal.isRightTurn())
                || isFiveRightSection();
    }

    @Nullable
    public BlockPos linkedControllerPos() {
        return linkedControllerPos;
    }

    public SignalMovement linkedMovement() {
        return linkedMovement;
    }

    public boolean isExplicitlyLinked() {
        return linkedControllerPos != null;
    }

    public void linkToController(BlockPos controllerPos, SignalMovement movement) {
        linkedControllerPos = controllerPos.immutable();
        linkedMovement = movement;
        nextGroupScan = 0L;
        sync();
    }

    public void clearControllerLink() {
        linkedControllerPos = null;
        linkedMovement = inferredMovement();
        linkedControllerAvailable = false;
        nextGroupScan = 0L;
        sync();
    }

    /** Queues a pedestrian call. It is served only at the next matching through-green boundary. */
    public void requestWalk() {
        if (!isPedestrianSignal() || walkCallPending || walkCallActive) {
            return;
        }
        walkCallPending = true;
        sync();
    }

    /** Compatibility overload for earlier callers; the requested deadline is intentionally ignored. */
    @Deprecated(forRemoval = false)
    public void requestWalk(long ignoredUntilGameTime) {
        requestWalk();
    }

    public boolean isFlasher() {
        return getBlockState().getBlock() instanceof SignalFlasherBlock;
    }

    public boolean isSingleSection() {
        return getBlockState().getBlock() instanceof TrafficLight1Block;
    }

    public boolean isPedestrianSignal() {
        return getBlockState().getBlock() instanceof TrafficLight2Block;
    }

    public boolean isRampMeterSignal() {
        return getBlockState().getBlock() instanceof RampMeterSignalBlock;
    }

    /** Cardinal roadway axis used by controllers and pedestrian-call grouping. */
    public Direction.Axis signalAxis() {
        return getCardinalAxis();
    }

    /** Nearest cardinal direction the signal face is aimed toward. */
    public Direction signalDirection() {
        BlockState state = getBlockState();
        if (!state.hasProperty(TrafficLightBlock.ROTATION)) {
            return Direction.SOUTH;
        }
        int quarter = Math.floorMod((state.getValue(TrafficLightBlock.ROTATION) + 2) / 4, 4);
        return switch (quarter) {
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.WEST;
            default -> Direction.SOUTH;
        };
    }

    /** Copies only appearance settings when the controller swaps the physical head type. */
    public void copyVisualConfigurationFrom(TrafficLightBlockEntity source) {
        if (source == null) return;
        this.backplateStyle = source.backplateStyle;
        this.visorStyle = source.visorStyle;
        this.pedestrianSignalStyle = source.pedestrianSignalStyle;
        this.lampTechnology = source.lampTechnology;
        sync();
    }

    public boolean isFiveLensSignal() {
        return isFiveSection() || isDoghouse();
    }

    public boolean hasDedicatedLane() {
        return isFourSection() || isFiveLensSignal() || isDedicatedTurnSignal();
    }

    public SignalBackplateStyle backplateStyle() {
        return backplateStyle;
    }

    public SignalVisorStyle visorStyle() {
        return visorStyle;
    }

    public PedestrianSignalStyle pedestrianSignalStyle() {
        return pedestrianSignalStyle;
    }

    public SignalLampTechnology lampTechnology() {
        return lampTechnology;
    }

    public SignalLampTechnology cycleLampTechnology(int step) {
        lampTechnology = lampTechnology.step(step);
        sync();
        return lampTechnology;
    }

    public SignalBackplateStyle cycleBackplate(int step) {
        backplateStyle = backplateStyle.step(step);
        sync();
        return backplateStyle;
    }

    public SignalVisorStyle cycleVisor(int step) {
        visorStyle = visorStyle.step(step);
        sync();
        return visorStyle;
    }

    public PedestrianSignalStyle cyclePedestrianStyle(int step) {
        if (!isPedestrianSignal()) {
            return pedestrianSignalStyle;
        }
        pedestrianSignalStyle = pedestrianSignalStyle.step(step);
        sync();
        return pedestrianSignalStyle;
    }

    @Nullable
    public TrafficLightBulbType getBulbType(int slot) {
        checkSlot(slot);
        return bulbs[slot];
    }

    public boolean isActive(int slot) {
        checkSlot(slot);
        return active[slot];
    }

    public boolean isFlashing(int slot) {
        checkSlot(slot);
        return flashing[slot];
    }

    public boolean allowsFlash(int slot) {
        checkSlot(slot);
        return allowFlash[slot];
    }

    public boolean isPigAbove() {
        return pigAbove;
    }

    public boolean isVisible(int slot, float partialTick) {
        if (!isActive(slot)) {
            return false;
        }
        if (!isFlashing(slot)) {
            return true;
        }
        // Operational flashing must remain visible even if an old save or a
        // bulb customization disabled user-controlled flashing. In that case
        // the indication stays steadily illuminated instead of disappearing.
        if (!allowsFlash(slot)) {
            return true;
        }

        // Use a client-local half-second oscillator. Block-entity NBT only has
        // to synchronize the "flashing" flag once; the renderer no longer
        // depends on repeated server packets or a client day-time offset.
        long phase = System.currentTimeMillis() / 500L;
        return (phase & 1L) == 0L;
    }

    public boolean anyActive() {
        for (int slot = 0; slot < getBulbCount(); slot++) {
            if (active[slot]) {
                return true;
            }
        }
        return false;
    }

    public void setBulb(int slot, @Nullable TrafficLightBulbType bulbType) {
        checkSlot(slot);
        bulbs[slot] = bulbType;
        if (bulbType == null) {
            active[slot] = false;
            flashing[slot] = false;
        }
        sync();
    }

    public void setAllowFlash(int slot, boolean value) {
        checkSlot(slot);
        allowFlash[slot] = value;
        sync();
    }

    public void setActive(TrafficLightBulbType bulbType, boolean value, boolean shouldFlash) {
        boolean changed = false;
        for (int slot = 0; slot < getBulbCount(); slot++) {
            if (bulbs[slot] == bulbType && (active[slot] != value || flashing[slot] != shouldFlash)) {
                active[slot] = value;
                flashing[slot] = shouldFlash;
                changed = true;
            }
        }
        if (changed) {
            sync();
        }
    }

    public void powerOff() {
        Arrays.fill(active, false);
        Arrays.fill(flashing, false);
        for (int slot = 0; slot < getBulbCount(); slot++) {
            if (bulbs[slot] == TrafficLightBulbType.DONT_CROSS) {
                active[slot] = true;
            }
        }
        sync();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TrafficLightBlockEntity blockEntity) {
        if (blockEntity.isFlasher()) {
            blockEntity.applyStandaloneFlasher();
            return;
        }
        if (blockEntity.isSingleSection() && !blockEntity.isExplicitlyLinked()) {
            blockEntity.applyStandaloneSingleRed();
            return;
        }
        long gameTime = level.getGameTime();
        if (blockEntity.isRampMeterSignal() && !blockEntity.isExplicitlyLinked()) {
            blockEntity.applyStandaloneRampMeter(gameTime);
            return;
        }
        if (gameTime >= blockEntity.nextGroupScan) {
            blockEntity.refreshControlSource(level, pos);
            blockEntity.nextGroupScan = gameTime + GROUP_SCAN_INTERVAL;
        }

        // An explicitly linked signal never joins a different nearby controller
        // while its selected controller is unloaded or missing.
        if (blockEntity.isExplicitlyLinked() && !blockEntity.linkedControllerAvailable) {
            blockEntity.applyAutomaticState(null);
            return;
        }

        if (blockEntity.cachedController.failSafe()) {
            blockEntity.applyFailSafeFlash();
            return;
        }

        if (!blockEntity.cachedGroup.isIntersection()) {
            blockEntity.applyAutomaticState(null);
            return;
        }

        AutoPhase phase = calculatePhase(gameTime, blockEntity.cachedController);
        blockEntity.noteDetectorService(level, phase);
        if (blockEntity.cachedController.nightFlash() && isNight(level)) {
            blockEntity.applyNightFlashingState(phase);
        } else {
            blockEntity.applyAutomaticState(phase);
        }
    }

    private void noteDetectorService(Level level, @Nullable AutoPhase phase) {
        if (phase == null || phase.activeAxis() != getCardinalAxis()) {
            return;
        }
        SignalMovement serviceMovement;
        int remaining;
        if (phase.stage() == AutoStage.BUS_GO) {
            serviceMovement = SignalMovement.BUS;
            remaining = phase.ticksRemaining()
                    + scaledTicks(BUS_CAUTION_TICKS + BUS_CLEARANCE_TICKS + ALL_RED_TICKS, cachedController.phaseSpeed());
        } else if (phase.stage() == AutoStage.THROUGH_GREEN) {
            serviceMovement = SignalMovement.THROUGH;
            remaining = phase.ticksRemaining()
                    + scaledTicks(THROUGH_YELLOW_TICKS + ALL_RED_TICKS, cachedController.phaseSpeed());
        } else if (phase.stage() == AutoStage.TURN_GREEN) {
            serviceMovement = SignalMovement.LEFT;
            remaining = phase.ticksRemaining()
                    + scaledTicks(TURN_YELLOW_TICKS + TURN_CLEARANCE_TICKS + ALL_RED_TICKS, cachedController.phaseSpeed());
        } else {
            return;
        }

        BlockPos controllerPos = linkedControllerPos != null ? linkedControllerPos : cachedAutoControllerPos;
        if (controllerPos != null && level.isLoaded(controllerPos)
                && level.getBlockEntity(controllerPos) instanceof TrafficLightControllerBlockEntity controller) {
            controller.beginDetectorService(phase.activeAxis(), serviceMovement, remaining);
        }
    }

    private void applyStandaloneFlasher() {
        boolean changed = !active[0] || !flashing[0] || !allowFlash[0];
        active[0] = true;
        flashing[0] = true;
        allowFlash[0] = true;
        for (int slot = 1; slot < MAX_BULBS; slot++) {
            if (active[slot] || flashing[slot]) {
                active[slot] = false;
                flashing[slot] = false;
                changed = true;
            }
        }
        if (changed) {
            sync();
        }
    }


    private void applyStandaloneSingleRed() {
        boolean changed = !active[0] || flashing[0] || !allowFlash[0];
        active[0] = true;
        flashing[0] = false;
        allowFlash[0] = true;
        for (int slot = 1; slot < MAX_BULBS; slot++) {
            if (active[slot] || flashing[slot]) {
                active[slot] = false;
                flashing[slot] = false;
                changed = true;
            }
        }
        if (changed) {
            sync();
        }
    }

    private void applyStandaloneRampMeter(long gameTime) {
        // Typical metering pulse: three seconds red, three quarters of a second green.
        int tick = (int) Math.floorMod(gameTime, 75L);
        boolean green = tick >= 60;
        boolean[] desired = new boolean[MAX_BULBS];
        for (int slot = 0; slot < getBulbCount(); slot++) {
            desired[slot] = bulbs[slot] == (green ? TrafficLightBulbType.GREEN : TrafficLightBulbType.RED);
        }
        applyDesired(desired, null);
    }

    private void refreshControlSource(Level level, BlockPos origin) {
        linkedControllerAvailable = false;
        if (linkedControllerPos != null) {
            if (!level.isLoaded(linkedControllerPos)) {
                cachedGroup = IntersectionGroup.NONE;
                cachedController = ControllerProfile.DEFAULT;
                return;
            }

            if (level.getBlockEntity(linkedControllerPos) instanceof TrafficLightControllerBlockEntity controller
                    && controller.isLinkedSignal(origin)) {
                SignalMovement assigned = controller.movementFor(origin);
                if (assigned != null && assigned != linkedMovement) {
                    linkedMovement = assigned;
                    sync();
                }
                controller.pruneInvalidLinks();
                linkedControllerAvailable = true;
                cachedAutoControllerPos = null;
                cachedGroup = scanLinkedIntersection(level, controller);
                cachedController = profileOf(controller);
                return;
            }

            // The chosen controller is loaded and no longer owns this signal.
            clearControllerLink();
        }

        cachedGroup = scanIntersection(level, origin);
        cachedController = scanController(level, origin);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TrafficLightBlockEntity blockEntity) {
        blockEntity.pigCheckDelay++;
        if (blockEntity.pigCheckDelay < 20) {
            return;
        }

        blockEntity.pigCheckDelay = 0;
        blockEntity.pigAbove = !level.getEntitiesOfClass(
                Pig.class,
                new AABB(pos).inflate(0.75D).expandTowards(0.0D, blockEntity.isFiveSection() ? 3.0D : 2.0D, 0.0D)
        ).isEmpty();
    }

    private IntersectionGroup scanIntersection(Level level, BlockPos origin) {
        boolean hasX = false;
        boolean hasZ = false;
        int vehicleSignalCount = 0;

        BlockPos min = origin.offset(-INTERSECTION_RADIUS, -INTERSECTION_VERTICAL_RADIUS, -INTERSECTION_RADIUS);
        BlockPos max = origin.offset(INTERSECTION_RADIUS, INTERSECTION_VERTICAL_RADIUS, INTERSECTION_RADIUS);
        for (BlockPos scanPos : BlockPos.betweenClosed(min, max)) {
            if (!(level.getBlockEntity(scanPos) instanceof TrafficLightBlockEntity signal)) {
                continue;
            }
            if (signal.isPedestrianSignal() || signal.isRampMeterSignal() || signal.isFlasher() || signal.isExplicitlyLinked()) {
                continue;
            }

            vehicleSignalCount++;
            Direction.Axis axis = signal.getCardinalAxis();
            if (axis == Direction.Axis.X) {
                hasX = true;
            } else {
                hasZ = true;
            }
        }

        return new IntersectionGroup(vehicleSignalCount, hasX, hasZ);
    }

    private IntersectionGroup scanLinkedIntersection(Level level, TrafficLightControllerBlockEntity controller) {
        boolean hasX = false;
        boolean hasZ = false;
        int vehicleSignalCount = 0;

        for (BlockPos signalPos : controller.linkedSignals().keySet()) {
            if (!level.isLoaded(signalPos)
                    || !(level.getBlockEntity(signalPos) instanceof TrafficLightBlockEntity signal)
                    || signal.isPedestrianSignal() || signal.isRampMeterSignal() || signal.isFlasher()) {
                continue;
            }
            vehicleSignalCount++;
            Direction.Axis axis = signal.getCardinalAxis();
            if (axis == Direction.Axis.X) {
                hasX = true;
            } else {
                hasZ = true;
            }
        }
        return new IntersectionGroup(vehicleSignalCount, hasX, hasZ);
    }

    private static ControllerProfile profileOf(TrafficLightControllerBlockEntity controller) {
        TrafficLightControllerBlockEntity.DetectorSnapshot detectors = controller.detectorSnapshot();
        return new ControllerProfile(
                controller.throughGreenTicks(),
                controller.turnPriority(),
                controller.nightFlash(),
                controller.phaseSpeed(),
                controller.failSafe(),
                controller.cycleStartGameTime(),
                detectors.xThroughDemand(),
                detectors.xTurnDemand() && controller.turnMovementAvailable(Direction.Axis.X),
                detectors.zThroughDemand(),
                detectors.zTurnDemand() && controller.turnMovementAvailable(Direction.Axis.Z),
                detectors.xBusDemand() && controller.busMovementAvailable(Direction.Axis.X),
                detectors.zBusDemand() && controller.busMovementAvailable(Direction.Axis.Z),
                controller.approachPriority(Direction.NORTH),
                controller.approachPriority(Direction.EAST),
                controller.approachPriority(Direction.SOUTH),
                controller.approachPriority(Direction.WEST)
        );
    }

    private ControllerProfile scanController(Level level, BlockPos origin) {
        TrafficLightControllerBlockEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        BlockPos min = origin.offset(-CONTROLLER_RADIUS, -CONTROLLER_VERTICAL_RADIUS, -CONTROLLER_RADIUS);
        BlockPos max = origin.offset(CONTROLLER_RADIUS, CONTROLLER_VERTICAL_RADIUS, CONTROLLER_RADIUS);
        for (BlockPos scanPos : BlockPos.betweenClosed(min, max)) {
            if (!(level.getBlockEntity(scanPos) instanceof TrafficLightControllerBlockEntity controller)
                    || controller.linkedSignalCount() > 0) {
                continue;
            }
            double distance = scanPos.distSqr(origin);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = controller;
            }
        }

        cachedAutoControllerPos = nearest == null ? null : nearest.getBlockPos().immutable();
        return nearest == null ? ControllerProfile.DEFAULT : profileOf(nearest);
    }

    private static boolean isNight(Level level) {
        long timeOfDay = Math.floorMod(level.getDayTime(), 24_000L);
        return timeOfDay >= 13_000L && timeOfDay < 23_000L;
    }

    private Direction.Axis getCardinalAxis() {
        BlockState state = getBlockState();
        if (!state.hasProperty(TrafficLightBlock.ROTATION)) {
            return Direction.Axis.Z;
        }

        int rotation = state.getValue(TrafficLightBlock.ROTATION);
        int nearestQuarterTurn = Math.floorMod((rotation + 2) / 4, 4);
        return (nearestQuarterTurn & 1) == 0 ? Direction.Axis.Z : Direction.Axis.X;
    }

    /** Fixed sequence shared by every signal using the same controller profile. */
    @Nullable
    private static AutoPhase calculatePhase(long gameTime, ControllerProfile controller) {
        int zLength = axisCycleLength(controller, Direction.Axis.Z);
        int xLength = axisCycleLength(controller, Direction.Axis.X);
        int totalLength = zLength + xLength;
        if (totalLength <= 0) {
            return null;
        }

        long elapsed = Math.max(0L, gameTime - controller.cycleStartGameTime());
        int cycleTick = (int) Math.floorMod(elapsed, (long) totalLength);
        if (zLength > 0 && cycleTick < zLength) {
            return phaseWithinAxis(Direction.Axis.Z, cycleTick, controller);
        }
        if (xLength <= 0) {
            return phaseWithinAxis(Direction.Axis.Z, Math.floorMod(cycleTick, zLength), controller);
        }
        return phaseWithinAxis(Direction.Axis.X, cycleTick - zLength, controller);
    }

    private static int axisCycleLength(ControllerProfile controller, Direction.Axis axis) {
        boolean turnDemand = controller.turnDemand(axis);
        boolean throughDemand = controller.throughDemand(axis);
        boolean busDemand = controller.busDemand(axis);
        if (!turnDemand && !throughDemand && !busDemand) {
            return 0;
        }

        int length = scaledTicks(ALL_RED_TICKS, controller.phaseSpeed());
        if (busDemand) {
            length += scaledTicks(BUS_GO_TICKS, controller.phaseSpeed());
            length += scaledTicks(BUS_CAUTION_TICKS, controller.phaseSpeed());
            length += scaledTicks(BUS_CLEARANCE_TICKS, controller.phaseSpeed());
        }
        if (turnDemand) {
            length += scaledTicks(weightedTicks(TURN_GREEN_TICKS, controller.axisPriorityScore(axis)), controller.phaseSpeed());
            length += scaledTicks(TURN_YELLOW_TICKS, controller.phaseSpeed());
            length += scaledTicks(TURN_CLEARANCE_TICKS, controller.phaseSpeed());
        }
        if (throughDemand) {
            length += scaledTicks(throughGreenForAxis(controller, axis), controller.phaseSpeed());
            length += scaledTicks(THROUGH_YELLOW_TICKS, controller.phaseSpeed());
        }
        return length;
    }

    private static int throughGreenForAxis(ControllerProfile controller, Direction.Axis axis) {
        Direction.Axis opposing = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        int weighted = weightedTicks(controller.throughGreenTicks(), controller.axisPriorityScore(axis));
        return controller.axisHasDemand(opposing)
                ? weighted
                : Math.min(2_400, weighted * 2);
    }

    private static int weightedTicks(int ticks, int axisPriorityScore) {
        // Two opposing approaches share a phase. Their independent 0/1/2 priorities
        // combine into a pair weight without forcing either direction to disappear.
        return switch (axisPriorityScore) {
            case 0 -> Math.max(1, ticks * 13 / 20); // LESS + LESS = 65%
            case 1 -> Math.max(1, ticks * 4 / 5);   // LESS + NORMAL = 80%
            case 3 -> Math.max(1, ticks * 6 / 5);   // NORMAL + PRIORITY = 120%
            case 4 -> Math.max(1, ticks * 7 / 5);   // PRIORITY + PRIORITY = 140%
            default -> ticks;                       // balanced pair = 100%
        };
    }

    private static AutoPhase phaseWithinAxis(Direction.Axis axis, int tick, ControllerProfile controller) {
        boolean turnDemand = controller.turnDemand(axis);
        boolean throughDemand = controller.throughDemand(axis);

        if (controller.busDemand(axis)) {
            int busGo = scaledTicks(BUS_GO_TICKS, controller.phaseSpeed());
            int busCaution = scaledTicks(BUS_CAUTION_TICKS, controller.phaseSpeed());
            int busClearance = scaledTicks(BUS_CLEARANCE_TICKS, controller.phaseSpeed());
            if (tick < busGo) return new AutoPhase(axis, AutoStage.BUS_GO, busGo - tick);
            tick -= busGo;
            if (tick < busCaution) return new AutoPhase(axis, AutoStage.BUS_CAUTION, busCaution - tick);
            tick -= busCaution;
            if (tick < busClearance) return new AutoPhase(axis, AutoStage.ALL_RED, busClearance - tick);
            tick -= busClearance;
        }

        if (turnDemand) {
            int turnGreen = scaledTicks(weightedTicks(TURN_GREEN_TICKS, controller.axisPriorityScore(axis)), controller.phaseSpeed());
            int turnYellow = scaledTicks(TURN_YELLOW_TICKS, controller.phaseSpeed());
            int turnClearance = scaledTicks(TURN_CLEARANCE_TICKS, controller.phaseSpeed());

            if (tick < turnGreen) {
                return new AutoPhase(axis, AutoStage.TURN_GREEN, turnGreen - tick);
            }
            tick -= turnGreen;
            if (tick < turnYellow) {
                return new AutoPhase(axis, AutoStage.TURN_YELLOW, turnYellow - tick);
            }
            tick -= turnYellow;
            if (tick < turnClearance) {
                return new AutoPhase(axis, AutoStage.ALL_RED, turnClearance - tick);
            }
            tick -= turnClearance;
        }

        if (throughDemand) {
            int throughGreen = scaledTicks(throughGreenForAxis(controller, axis), controller.phaseSpeed());
            int throughYellow = scaledTicks(THROUGH_YELLOW_TICKS, controller.phaseSpeed());
            if (tick < throughGreen) {
                return new AutoPhase(axis, AutoStage.THROUGH_GREEN, throughGreen - tick);
            }
            tick -= throughGreen;
            if (tick < throughYellow) {
                return new AutoPhase(axis, AutoStage.THROUGH_YELLOW, throughYellow - tick);
            }
        }
        return new AutoPhase(axis, AutoStage.ALL_RED, scaledTicks(ALL_RED_TICKS, controller.phaseSpeed()));
    }

    private static int scaledTicks(int ticks, int speedMode) {
        return switch (speedMode) {
            case 0 -> Math.max(1, ticks * 3 / 2); // slow: 150% duration
            case 2 -> Math.max(1, ticks * 2 / 3); // fast: 67% duration
            default -> ticks;
        };
    }

    private void applyAutomaticState(@Nullable AutoPhase phase) {
        if (isBusSignal()) {
            applyBusState(phase);
            return;
        }
        if (isPedestrianSignal()) {
            applyPedestrianState(phase);
            return;
        }

        SignalMovement movement = effectiveMovement();
        Direction.Axis ownAxis = getCardinalAxis();
        boolean ownMovementActive = phase != null && phase.activeAxis() == ownAxis;
        boolean turnPriorityMatches = turnPriorityMatches(movement);
        boolean throughStage = ownMovementActive && phase != null
                && (phase.stage() == AutoStage.THROUGH_GREEN || phase.stage() == AutoStage.THROUGH_YELLOW);
        boolean turnStage = ownMovementActive && phase != null
                && (phase.stage() == AutoStage.TURN_GREEN || phase.stage() == AutoStage.TURN_YELLOW)
                && turnPriorityMatches;

        boolean circularThrough = movement.includesThrough() && !isDedicatedTurnSignal();
        boolean circularTurn = movement.isTurnOnly() && !hasDedicatedLane();
        boolean arrowTurn = hasDedicatedLane() && (movement.includesLeft() || movement.includesRight());

        boolean showCircularGreen = (circularThrough && throughStage && phase.stage() == AutoStage.THROUGH_GREEN)
                || (circularTurn && turnStage && phase.stage() == AutoStage.TURN_GREEN);
        // A four-section protected-left head has no yellow arrow. Its protected
        // phase terminates directly to circular red; the through-yellow lens is
        // reserved exclusively for the straight movement.
        boolean showCircularYellow = (circularThrough && throughStage && phase.stage() == AutoStage.THROUGH_YELLOW)
                || (circularTurn && turnStage && phase.stage() == AutoStage.TURN_YELLOW);
        boolean showTurnGreen = arrowTurn && turnStage && phase.stage() == AutoStage.TURN_GREEN;
        boolean showTurnYellow = arrowTurn && !isFourSection() && turnStage && phase.stage() == AutoStage.TURN_YELLOW;
        boolean showStopIndication = !showCircularGreen && !showCircularYellow && !showTurnGreen && !showTurnYellow;

        // A doghouse, four-section or five-section head represents two movements
        // in one housing. During a protected arrow phase the through movement is
        // still stopped, so its circular red must remain illuminated alongside
        // the green/yellow arrow. Dedicated arrow heads instead use their own red
        // arrow only when the entire turn movement is stopped.
        boolean combinedHead = isFourSection() || isFiveLensSignal();
        boolean showCircularRed = showStopIndication
                || (combinedHead && (showTurnGreen || showTurnYellow));
        boolean showArrowRed = showStopIndication;

        boolean[] desiredActive = new boolean[MAX_BULBS];
        for (int slot = 0; slot < getBulbCount(); slot++) {
            TrafficLightBulbType bulb = bulbs[slot];
            if (bulb == null) {
                continue;
            }

            desiredActive[slot] = switch (bulb) {
                case RED, STRAIGHT_RED -> showCircularRed;
                case RED_ARROW_LEFT, RED_ARROW_RIGHT, RED_ARROW_U_TURN -> showArrowRed;
                case YELLOW, STRAIGHT_YELLOW -> showCircularYellow;
                case GREEN, STRAIGHT_GREEN -> showCircularGreen;
                case YELLOW_ARROW_LEFT, YELLOW_ARROW_RIGHT, YELLOW_ARROW_U_TURN -> showTurnYellow;
                case GREEN_ARROW_LEFT, GREEN_ARROW_RIGHT, GREEN_ARROW_U_TURN -> showTurnGreen;
                case DONT_CROSS -> showStopIndication;
                case CROSS -> showCircularGreen;
                case NO_RIGHT_TURN, NO_LEFT_TURN, BUS_STOP, BUS_CAUTION, BUS_GO -> false;
            };
        }
        applyDesired(desiredActive, null);
    }

    private void applyBusState(@Nullable AutoPhase phase) {
        Direction.Axis ownAxis = getCardinalAxis();
        boolean own = phase != null && phase.activeAxis() == ownAxis;
        boolean go = own && phase.stage() == AutoStage.BUS_GO;
        boolean caution = own && phase.stage() == AutoStage.BUS_CAUTION;
        boolean[] desired = new boolean[MAX_BULBS];
        for (int slot = 0; slot < getBulbCount(); slot++) {
            TrafficLightBulbType bulb = bulbs[slot];
            desired[slot] = switch (bulb) {
                case BUS_GO -> go;
                case BUS_CAUTION -> caution;
                case BUS_STOP -> !go && !caution;
                default -> false;
            };
        }
        applyDesired(desired, null);
    }

    private SignalMovement effectiveMovement() {
        return isExplicitlyLinked() ? linkedMovement : inferredMovement();
    }

    private SignalMovement inferredMovement() {
        if (isPedestrianSignal()) {
            return SignalMovement.PEDESTRIAN;
        }
        if (isBusSignal()) {
            return SignalMovement.BUS;
        }
        if (isUTurnSignal()) {
            return SignalMovement.U_TURN;
        }
        if (isDedicatedTurnSignal()) {
            return isRightTurnSignal() ? SignalMovement.RIGHT : SignalMovement.LEFT;
        }
        if (isFiveRightSection()) {
            return SignalMovement.THROUGH_RIGHT;
        }
        if (isFourSection() || isFiveLensSignal()) {
            return SignalMovement.THROUGH_LEFT;
        }
        return SignalMovement.THROUGH;
    }

    private boolean turnPriorityMatches(SignalMovement movement) {
        int priority = cachedController.turnPriority();
        if (priority == TrafficLightControllerBlockEntity.PRIORITY_BOTH) {
            return movement.includesLeft() || movement.includesRight();
        }
        if (movement.includesRight()) {
            return priority == TrafficLightControllerBlockEntity.PRIORITY_RIGHT;
        }
        return movement.includesLeft() && priority == TrafficLightControllerBlockEntity.PRIORITY_LEFT;
    }

    /**
     * Night operation keeps the normal two-axis sequence, but substitutes a
     * flashing amber indication for each green movement. The movement then
     * receives a steady amber clearance followed by all-red before the other
     * axis begins.
     */
    private void applyNightFlashingState(@Nullable AutoPhase phase) {
        if (isBusSignal()) {
            applyBusState(phase);
            return;
        }
        if (isPedestrianSignal()) {
            boolean[] desired = new boolean[MAX_BULBS];
            boolean[] flash = new boolean[MAX_BULBS];
            for (int slot = 0; slot < getBulbCount(); slot++) {
                if (bulbs[slot] == TrafficLightBulbType.DONT_CROSS) {
                    desired[slot] = true;
                    flash[slot] = true;
                }
            }
            applyDesired(desired, flash);
            return;
        }

        SignalMovement movement = effectiveMovement();
        Direction.Axis ownAxis = getCardinalAxis();
        boolean ownMovementActive = phase != null && phase.activeAxis() == ownAxis;
        boolean priorityMatches = turnPriorityMatches(movement);
        boolean throughStage = ownMovementActive && phase != null
                && (phase.stage() == AutoStage.THROUGH_GREEN || phase.stage() == AutoStage.THROUGH_YELLOW);
        boolean turnStage = ownMovementActive && phase != null
                && (phase.stage() == AutoStage.TURN_GREEN || phase.stage() == AutoStage.TURN_YELLOW)
                && priorityMatches;

        boolean circularThrough = movement.includesThrough() && !isDedicatedTurnSignal();
        boolean circularTurn = movement.isTurnOnly() && !hasDedicatedLane();
        boolean arrowTurn = hasDedicatedLane() && (movement.includesLeft() || movement.includesRight());

        boolean flashingCircular = (circularThrough && throughStage && phase.stage() == AutoStage.THROUGH_GREEN)
                || (circularTurn && turnStage && phase.stage() == AutoStage.TURN_GREEN);
        boolean steadyCircular = (circularThrough && throughStage && phase.stage() == AutoStage.THROUGH_YELLOW)
                || (circularTurn && turnStage && phase.stage() == AutoStage.TURN_YELLOW);
        boolean flashingArrow = arrowTurn && turnStage && phase.stage() == AutoStage.TURN_GREEN;
        boolean steadyArrow = arrowTurn && !isFourSection() && turnStage && phase.stage() == AutoStage.TURN_YELLOW;
        boolean showRed = !flashingCircular && !steadyCircular && !flashingArrow && !steadyArrow;
        boolean combinedHead = isFourSection() || isFiveLensSignal();
        boolean showCircularRed = showRed || (combinedHead && (flashingArrow || steadyArrow));
        boolean showArrowRed = showRed;

        boolean[] desired = new boolean[MAX_BULBS];
        boolean[] flash = new boolean[MAX_BULBS];
        for (int slot = 0; slot < getBulbCount(); slot++) {
            TrafficLightBulbType bulb = bulbs[slot];
            if (bulb == null) {
                continue;
            }

            switch (bulb) {
                case RED, STRAIGHT_RED -> desired[slot] = showCircularRed;
                case RED_ARROW_LEFT, RED_ARROW_RIGHT, RED_ARROW_U_TURN -> desired[slot] = showArrowRed;
                case YELLOW, STRAIGHT_YELLOW -> {
                    desired[slot] = flashingCircular || steadyCircular;
                    flash[slot] = flashingCircular;
                }
                case YELLOW_ARROW_LEFT, YELLOW_ARROW_RIGHT, YELLOW_ARROW_U_TURN -> {
                    desired[slot] = flashingArrow || steadyArrow;
                    flash[slot] = flashingArrow;
                }
                default -> { }
            }
        }
        applyDesired(desired, flash);
    }

    /** Broken-controller fail-safe: synchronized flashing red and flashing pedestrian stop. */
    private void applyFailSafeFlash() {
        boolean[] desired = new boolean[MAX_BULBS];
        boolean[] flash = new boolean[MAX_BULBS];

        for (int slot = 0; slot < getBulbCount(); slot++) {
            TrafficLightBulbType bulb = bulbs[slot];
            boolean failSafeIndication = isPedestrianSignal()
                    ? bulb == TrafficLightBulbType.DONT_CROSS
                    : bulb == TrafficLightBulbType.RED
                            || bulb == TrafficLightBulbType.STRAIGHT_RED
                            || bulb == TrafficLightBulbType.RED_ARROW_LEFT
                            || bulb == TrafficLightBulbType.RED_ARROW_RIGHT
                            || bulb == TrafficLightBulbType.RED_ARROW_U_TURN
                            || bulb == TrafficLightBulbType.BUS_STOP;
            if (failSafeIndication) {
                desired[slot] = true;
                flash[slot] = true;
            }
        }

        applyDesired(desired, flash);
    }

    private void applyPedestrianState(@Nullable AutoPhase phase) {
        Direction.Axis ownAxis = getCardinalAxis();
        boolean ownMovementActive = phase != null && phase.activeAxis() == ownAxis;
        boolean ownThroughGreen = ownMovementActive && phase.stage() == AutoStage.THROUGH_GREEN;

        // Calls pressed during an active green wait for the following cycle.
        // A call is accepted only on the red-to-through-green boundary.
        if (ownThroughGreen && !wasOwnThroughGreen && walkCallPending) {
            walkCallPending = false;
            walkCallActive = true;
        }
        wasOwnThroughGreen = ownThroughGreen;

        boolean clearance = walkCallActive && ownMovementActive
                && ((phase.stage() == AutoStage.THROUGH_GREEN
                        && phase.ticksRemaining() <= scaledTicks(PEDESTRIAN_CLEARANCE_TICKS, cachedController.phaseSpeed()))
                    || phase.stage() == AutoStage.THROUGH_YELLOW);
        boolean walk = walkCallActive && ownThroughGreen && !clearance;
        boolean flashingHand = clearance;

        if (walkCallActive && (!ownMovementActive
                || (phase.stage() != AutoStage.THROUGH_GREEN && phase.stage() != AutoStage.THROUGH_YELLOW))) {
            walkCallActive = false;
        }

        boolean[] desiredActive = new boolean[MAX_BULBS];
        boolean[] desiredFlashing = new boolean[MAX_BULBS];
        for (int slot = 0; slot < getBulbCount(); slot++) {
            TrafficLightBulbType bulb = bulbs[slot];
            if (bulb == TrafficLightBulbType.CROSS) {
                desiredActive[slot] = walk;
            } else if (bulb == TrafficLightBulbType.DONT_CROSS) {
                desiredActive[slot] = !walk;
                desiredFlashing[slot] = flashingHand;
            }
        }
        applyDesired(desiredActive, desiredFlashing);
    }

    private void applyDesired(boolean[] desiredActive, @Nullable boolean[] desiredFlashing) {
        boolean changed = false;
        for (int slot = 0; slot < getBulbCount(); slot++) {
            boolean nextFlash = desiredFlashing != null && desiredFlashing[slot];
            if (active[slot] != desiredActive[slot] || flashing[slot] != nextFlash) {
                active[slot] = desiredActive[slot];
                flashing[slot] = nextFlash;
                changed = true;
            }
        }
        if (changed) {
            sync();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        int[] bulbIds = new int[MAX_BULBS];
        for (int slot = 0; slot < MAX_BULBS; slot++) {
            bulbIds[slot] = bulbs[slot] == null ? -1 : bulbs[slot].legacyId();
            tag.putBoolean(TAG_ACTIVE_PREFIX + slot, active[slot]);
            tag.putBoolean(TAG_FLASH_PREFIX + slot, flashing[slot]);
            tag.putBoolean(TAG_ALLOW_FLASH_PREFIX + slot, allowFlash[slot]);
        }
        tag.putIntArray(TAG_BULB_TYPES, bulbIds);
        tag.putInt(TAG_BACKPLATE_STYLE, backplateStyle.ordinal());
        tag.putInt(TAG_VISOR_STYLE, visorStyle.ordinal());
        tag.putInt(TAG_PEDESTRIAN_STYLE, pedestrianSignalStyle.ordinal());
        tag.putInt(TAG_LAMP_TECHNOLOGY, lampTechnology.ordinal());
        tag.putBoolean(TAG_WALK_CALL_PENDING, walkCallPending);
        tag.putBoolean(TAG_WALK_CALL_ACTIVE, walkCallActive);
        if (linkedControllerPos != null) {
            tag.putLong(TAG_LINKED_CONTROLLER, linkedControllerPos.asLong());
            tag.putString(TAG_LINKED_MOVEMENT, linkedMovement.getSerializedName());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        int[] bulbIds = tag.getIntArray(TAG_BULB_TYPES);
        for (int slot = 0; slot < MAX_BULBS; slot++) {
            if (slot < bulbIds.length) {
                bulbs[slot] = TrafficLightBulbType.byLegacyId(bulbIds[slot]);
            }
            active[slot] = tag.getBoolean(TAG_ACTIVE_PREFIX + slot);
            flashing[slot] = tag.getBoolean(TAG_FLASH_PREFIX + slot);
            allowFlash[slot] = !tag.contains(TAG_ALLOW_FLASH_PREFIX + slot)
                    || tag.getBoolean(TAG_ALLOW_FLASH_PREFIX + slot);
        }

        if ((isFiveLensSignal() && bulbIds.length < FIVE_SECTION_BULBS)
                || (isFourSection() && bulbIds.length < FOUR_SECTION_BULBS)
                || (isRampMeterSignal() && (bulbs[0] == null || bulbs[1] == null))
                || (isSingleSection() && bulbs[0] == null)) {
            configureDefaultBulbs(getBlockState());
        }

        SignalBackplateStyle[] backplates = SignalBackplateStyle.values();
        SignalVisorStyle[] visors = SignalVisorStyle.values();
        backplateStyle = backplates[Math.floorMod(tag.getInt(TAG_BACKPLATE_STYLE), backplates.length)];
        visorStyle = tag.contains(TAG_VISOR_STYLE)
                ? visors[Math.floorMod(tag.getInt(TAG_VISOR_STYLE), visors.length)]
                : SignalVisorStyle.STANDARD;

        PedestrianSignalStyle[] pedestrianStyles = PedestrianSignalStyle.values();
        pedestrianSignalStyle = tag.contains(TAG_PEDESTRIAN_STYLE)
                ? pedestrianStyles[Math.floorMod(tag.getInt(TAG_PEDESTRIAN_STYLE), pedestrianStyles.length)]
                : PedestrianSignalStyle.LEGACY;

        SignalLampTechnology[] lampTechnologies = SignalLampTechnology.values();
        lampTechnology = tag.contains(TAG_LAMP_TECHNOLOGY)
                ? lampTechnologies[Math.floorMod(tag.getInt(TAG_LAMP_TECHNOLOGY), lampTechnologies.length)]
                : SignalLampTechnology.DEFAULT;
        walkCallPending = tag.getBoolean(TAG_WALK_CALL_PENDING);
        walkCallActive = tag.getBoolean(TAG_WALK_CALL_ACTIVE);
        wasOwnThroughGreen = false;

        linkedControllerPos = tag.contains(TAG_LINKED_CONTROLLER)
                ? BlockPos.of(tag.getLong(TAG_LINKED_CONTROLLER))
                : null;
        linkedMovement = tag.contains(TAG_LINKED_MOVEMENT)
                ? SignalMovement.byName(tag.getString(TAG_LINKED_MOVEMENT))
                : inferredMovement();
        linkedControllerAvailable = false;
        nextGroupScan = 0L;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void sync() {
        setChanged();
        if (level == null) {
            return;
        }

        BlockState oldState = getBlockState();
        BlockState newState = oldState;
        boolean lit = anyActive();
        if (oldState.hasProperty(TrafficLightBlock.LIT) && oldState.getValue(TrafficLightBlock.LIT) != lit) {
            newState = oldState.setValue(TrafficLightBlock.LIT, lit);
            level.setBlock(worldPosition, newState, Block.UPDATE_CLIENTS);
        }
        level.sendBlockUpdated(worldPosition, oldState, newState, Block.UPDATE_CLIENTS);
    }

    private static void checkSlot(int slot) {
        if (slot < 0 || slot >= MAX_BULBS) {
            throw new IndexOutOfBoundsException("Traffic-light bulb slot must be 0-" + (MAX_BULBS - 1) + ": " + slot);
        }
    }

    private enum AutoStage {
        BUS_GO,
        BUS_CAUTION,
        TURN_GREEN,
        TURN_YELLOW,
        THROUGH_GREEN,
        THROUGH_YELLOW,
        ALL_RED
    }

    private record AutoPhase(Direction.Axis activeAxis, AutoStage stage, int ticksRemaining) {
    }

    private record ControllerProfile(
            int throughGreenTicks,
            int turnPriority,
            boolean nightFlash,
            int phaseSpeed,
            boolean failSafe,
            long cycleStartGameTime,
            boolean xThroughDemand,
            boolean xTurnDemand,
            boolean zThroughDemand,
            boolean zTurnDemand,
            boolean xBusDemand,
            boolean zBusDemand,
            int northPriority,
            int eastPriority,
            int southPriority,
            int westPriority
    ) {
        private static final ControllerProfile DEFAULT = new ControllerProfile(
                THROUGH_GREEN_TICKS,
                TrafficLightControllerBlockEntity.PRIORITY_LEFT,
                false,
                1,
                false,
                0L,
                true,
                true,
                true,
                true,
                false,
                false,
                TrafficLightControllerBlockEntity.APPROACH_NORMAL,
                TrafficLightControllerBlockEntity.APPROACH_NORMAL,
                TrafficLightControllerBlockEntity.APPROACH_NORMAL,
                TrafficLightControllerBlockEntity.APPROACH_NORMAL
        );

        boolean throughDemand(Direction.Axis axis) {
            return axis == Direction.Axis.X ? xThroughDemand : zThroughDemand;
        }

        boolean turnDemand(Direction.Axis axis) {
            return axis == Direction.Axis.X ? xTurnDemand : zTurnDemand;
        }

        boolean busDemand(Direction.Axis axis) {
            return axis == Direction.Axis.X ? xBusDemand : zBusDemand;
        }

        boolean axisHasDemand(Direction.Axis axis) {
            return throughDemand(axis) || turnDemand(axis) || busDemand(axis);
        }

        int axisPriorityScore(Direction.Axis axis) {
            return axis == Direction.Axis.X
                    ? eastPriority + westPriority
                    : northPriority + southPriority;
        }
    }

    private record IntersectionGroup(
            int signalCount,
            boolean hasX,
            boolean hasZ
    ) {
        private static final IntersectionGroup NONE = new IntersectionGroup(0, false, false);

        boolean isIntersection() {
            return signalCount >= 2 && hasX && hasZ;
        }
    }
}
