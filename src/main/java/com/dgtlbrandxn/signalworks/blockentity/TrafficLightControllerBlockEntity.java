package com.dgtlbrandxn.signalworks.blockentity;

import com.dgtlbrandxn.signalworks.block.SignalMovement;
import com.dgtlbrandxn.signalworks.block.TrafficLightBlock;
import com.dgtlbrandxn.signalworks.block.TrafficSensorBlock;
import com.dgtlbrandxn.signalworks.menu.TrafficLightControllerMenu;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import com.dgtlbrandxn.signalworks.registry.ModItems;
import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Persistent settings and explicitly linked signal groups for one intersection. */
public final class TrafficLightControllerBlockEntity extends BlockEntity implements MenuProvider, Container {
    public static final int DATA_THROUGH_GREEN = 0;
    public static final int DATA_TURN_PRIORITY = 1;
    public static final int DATA_NIGHT_FLASH = 2;
    public static final int DATA_PHASE_SPEED = 3;
    public static final int DATA_FAIL_SAFE = 4;
    public static final int DATA_LINKED_COUNT = 5;
    public static final int DATA_DETECTOR_COUNT = 6;
    public static final int DATA_X_DEMAND = 7;
    public static final int DATA_Z_DEMAND = 8;
    public static final int DATA_NORTH_PRIORITY = 9;
    public static final int DATA_EAST_PRIORITY = 10;
    public static final int DATA_SOUTH_PRIORITY = 11;
    public static final int DATA_WEST_PRIORITY = 12;

    public static final int APPROACH_LESS = 0;
    public static final int APPROACH_NORMAL = 1;
    public static final int APPROACH_PRIORITY = 2;

    /** Fixed-size vanilla menu data window for the top-down editor. */
    public static final int MAX_MAP_LINKS = 32;
    public static final int LINK_DATA_BASE = 13;
    public static final int LINK_DATA_STRIDE = 5;
    public static final int LINK_FIELD_DX = 0;
    public static final int LINK_FIELD_DY = 1;
    public static final int LINK_FIELD_DZ = 2;
    public static final int LINK_FIELD_MOVEMENT = 3;
    public static final int LINK_FIELD_TYPE = 4;
    public static final int DATA_COUNT = LINK_DATA_BASE + MAX_MAP_LINKS * LINK_DATA_STRIDE;

    public static final int MAP_TYPE_STANDARD = 0;
    public static final int MAP_TYPE_FOUR = 1;
    public static final int MAP_TYPE_FIVE = 2;
    public static final int MAP_TYPE_DOGHOUSE = 3;
    public static final int MAP_TYPE_LEFT = 4;
    public static final int MAP_TYPE_RIGHT = 5;
    public static final int MAP_TYPE_PEDESTRIAN = 6;
    public static final int MAP_TYPE_RAMP_METER = 7;
    public static final int MAP_TYPE_FIVE_RIGHT = 8;
    public static final int MAP_TYPE_STRAIGHT_ARROW = 9;
    public static final int MAP_TYPE_U_TURN = 10;
    public static final int MAP_TYPE_BUS = 11;

    public static final int PRIORITY_LEFT = 0;
    public static final int PRIORITY_RIGHT = 1;
    public static final int PRIORITY_BOTH = 2;

    private static final String TAG_THROUGH_GREEN = "ThroughGreenTicks";
    private static final String TAG_TURN_PRIORITY = "TurnPriority";
    private static final String TAG_NIGHT_FLASH = "NightFlash";
    private static final String TAG_PHASE_SPEED = "PhaseSpeed";
    private static final String TAG_FAIL_SAFE = "FailSafe";
    private static final String TAG_CYCLE_START = "CycleStartGameTime";
    private static final String TAG_LINKED_SIGNALS = "LinkedSignals";
    private static final String TAG_LINK_POS = "Pos";
    private static final String TAG_LINK_MOVEMENT = "Movement";
    private static final String TAG_STARTER_KIT_GENERATED = "StarterKitGenerated";
    private static final String TAG_NORTH_PRIORITY = "NorthPriority";
    private static final String TAG_EAST_PRIORITY = "EastPriority";
    private static final String TAG_SOUTH_PRIORITY = "SouthPriority";
    private static final String TAG_WEST_PRIORITY = "WestPriority";
    private static final int STARTER_KIT_SIZE = 2;

    private static final int DETECTOR_RADIUS = 18;
    private static final int DETECTOR_VERTICAL_RADIUS = 5;

    private int throughGreenTicks = 300;
    private int turnPriority = PRIORITY_LEFT;
    private boolean nightFlash;
    private int phaseSpeed = 1;
    private boolean failSafe;
    private int northPriority = APPROACH_NORMAL;
    private int eastPriority = APPROACH_NORMAL;
    private int southPriority = APPROACH_NORMAL;
    private int westPriority = APPROACH_NORMAL;
    private long cycleStartGameTime;
    private final LinkedHashMap<BlockPos, SignalMovement> linkedSignals = new LinkedHashMap<>();
    private final NonNullList<ItemStack> starterKit = NonNullList.withSize(STARTER_KIT_SIZE, ItemStack.EMPTY);
    private boolean starterKitGenerated;
    private DetectorSnapshot cachedDetectorSnapshot = DetectorSnapshot.DEFAULT;
    private long nextDetectorScanGameTime;

    // Vehicle detector calls are latched until their requested movement actually
    // receives service.  This prevents an occupied detector from dropping the
    // active green the instant the vehicle/player rolls off the loop.
    private static final int CALL_X_THROUGH = 0;
    private static final int CALL_X_TURN = 1;
    private static final int CALL_Z_THROUGH = 2;
    private static final int CALL_Z_TURN = 3;
    private static final int CALL_X_BUS = 4;
    private static final int CALL_Z_BUS = 5;
    private final boolean[] detectorCalls = new boolean[6];
    private final long[] detectorReleaseAt = new long[6];

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_THROUGH_GREEN -> throughGreenTicks;
                case DATA_TURN_PRIORITY -> turnPriority;
                case DATA_NIGHT_FLASH -> nightFlash ? 1 : 0;
                case DATA_PHASE_SPEED -> phaseSpeed;
                case DATA_FAIL_SAFE -> failSafe ? 1 : 0;
                case DATA_LINKED_COUNT -> linkedSignals.size();
                case DATA_DETECTOR_COUNT -> detectorSnapshot().detectorCount();
                case DATA_X_DEMAND -> demandMask(detectorSnapshot(), Direction.Axis.X);
                case DATA_Z_DEMAND -> demandMask(detectorSnapshot(), Direction.Axis.Z);
                case DATA_NORTH_PRIORITY -> northPriority;
                case DATA_EAST_PRIORITY -> eastPriority;
                case DATA_SOUTH_PRIORITY -> southPriority;
                case DATA_WEST_PRIORITY -> westPriority;
                default -> index >= LINK_DATA_BASE && index < DATA_COUNT
                        ? linkedMapData(index)
                        : 0;
            };
        }

        @Override
        public void set(int index, int value) {
            boolean changed = false;
            switch (index) {
                case DATA_THROUGH_GREEN -> {
                    int next = clamp(value, 100, 1_200);
                    changed = throughGreenTicks != next;
                    throughGreenTicks = next;
                }
                case DATA_TURN_PRIORITY -> {
                    int next = Math.floorMod(value, 3);
                    changed = turnPriority != next;
                    turnPriority = next;
                }
                case DATA_NIGHT_FLASH -> {
                    boolean next = value != 0;
                    changed = nightFlash != next;
                    nightFlash = next;
                }
                case DATA_PHASE_SPEED -> {
                    int next = Math.floorMod(value, 3);
                    changed = phaseSpeed != next;
                    phaseSpeed = next;
                }
                case DATA_FAIL_SAFE -> {
                    boolean next = value != 0;
                    changed = failSafe != next;
                    failSafe = next;
                }
                case DATA_NORTH_PRIORITY -> {
                    int next = Math.floorMod(value, 3);
                    changed = northPriority != next;
                    northPriority = next;
                }
                case DATA_EAST_PRIORITY -> {
                    int next = Math.floorMod(value, 3);
                    changed = eastPriority != next;
                    eastPriority = next;
                }
                case DATA_SOUTH_PRIORITY -> {
                    int next = Math.floorMod(value, 3);
                    changed = southPriority != next;
                    southPriority = next;
                }
                case DATA_WEST_PRIORITY -> {
                    int next = Math.floorMod(value, 3);
                    changed = westPriority != next;
                    westPriority = next;
                }
                default -> { }
            }
            if (changed) {
                settingsChanged();
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public TrafficLightControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRAFFIC_LIGHT_CONTROLLER.get(), pos, state);
    }

    public ContainerData dataAccess() {
        return dataAccess;
    }

    public int throughGreenTicks() {
        return throughGreenTicks;
    }

    public int turnPriority() {
        return turnPriority;
    }

    public boolean nightFlash() {
        return nightFlash;
    }

    public int phaseSpeed() {
        return phaseSpeed;
    }

    public boolean failSafe() {
        return failSafe;
    }

    public int approachPriority(Direction direction) {
        return switch (direction) {
            case NORTH -> northPriority;
            case EAST -> eastPriority;
            case SOUTH -> southPriority;
            case WEST -> westPriority;
            default -> APPROACH_NORMAL;
        };
    }

    public int axisPriorityScore(Direction.Axis axis) {
        return axis == Direction.Axis.X
                ? eastPriority + westPriority
                : northPriority + southPriority;
    }

    /** True only when this controller actually owns a protected turn movement on the axis. */
    public boolean turnMovementAvailable(Direction.Axis axis) {
        if (level == null) {
            return false;
        }
        for (Map.Entry<BlockPos, SignalMovement> entry : linkedSignals.entrySet()) {
            if (!entry.getValue().includesLeft() && !entry.getValue().includesRight()) {
                continue;
            }
            BlockPos pos = entry.getKey();
            if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof TrafficLightBlockEntity signal
                    && !signal.isPedestrianSignal() && !signal.isRampMeterSignal()
                    && signal.signalAxis() == axis) {
                return true;
            }
        }
        return false;
    }

    /** True when the controller owns at least one bus-only signal on this axis. */
    public boolean busMovementAvailable(Direction.Axis axis) {
        if (level == null) return false;
        for (Map.Entry<BlockPos, SignalMovement> entry : linkedSignals.entrySet()) {
            if (entry.getValue() != SignalMovement.BUS) continue;
            BlockPos pos = entry.getKey();
            if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof TrafficLightBlockEntity signal
                    && signal.isBusSignal() && signal.signalAxis() == axis) return true;
        }
        return false;
    }

    public long cycleStartGameTime() {
        return cycleStartGameTime;
    }

    public int linkedSignalCount() {
        return linkedSignals.size();
    }

    /**
     * Scans the controller's local road detectors. A movement category with no
     * detector installed remains recalled, preserving the old fixed-time cycle.
     * Once a detector exists for that category, the phase is demand-driven.
     */
    public DetectorSnapshot detectorSnapshot() {
        if (level == null) {
            return DetectorSnapshot.DEFAULT;
        }
        long gameTime = level.getGameTime();
        if (gameTime < nextDetectorScanGameTime) {
            return cachedDetectorSnapshot;
        }

        int count = 0;
        boolean xThroughPresent = false;
        boolean xThroughOccupied = false;
        boolean xTurnPresent = false;
        boolean xTurnOccupied = false;
        boolean zThroughPresent = false;
        boolean zThroughOccupied = false;
        boolean zTurnPresent = false;
        boolean zTurnOccupied = false;
        boolean xBusPresent = false;
        boolean xBusOccupied = false;
        boolean zBusPresent = false;
        boolean zBusOccupied = false;

        BlockPos min = worldPosition.offset(-DETECTOR_RADIUS, -DETECTOR_VERTICAL_RADIUS, -DETECTOR_RADIUS);
        BlockPos max = worldPosition.offset(DETECTOR_RADIUS, DETECTOR_VERTICAL_RADIUS, DETECTOR_RADIUS);
        for (BlockPos scanPos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(scanPos);
            if (!(state.getBlock() instanceof TrafficSensorBlock sensor)) {
                continue;
            }

            count++;
            boolean occupied = state.getValue(TrafficSensorBlock.OCCUPIED);
            Direction.Axis axis = state.getValue(TrafficSensorBlock.FACING).getAxis();
            SignalMovement sensorMovement = sensor.movement();
            boolean bus = sensorMovement == SignalMovement.BUS;
            boolean turn = sensorMovement.includesLeft() || sensorMovement.includesRight();

            if (axis == Direction.Axis.X) {
                if (bus) {
                    xBusPresent = true;
                    xBusOccupied |= occupied;
                } else if (turn) {
                    xTurnPresent = true;
                    xTurnOccupied |= occupied;
                } else {
                    xThroughPresent = true;
                    xThroughOccupied |= occupied;
                }
            } else if (axis == Direction.Axis.Z) {
                if (bus) {
                    zBusPresent = true;
                    zBusOccupied |= occupied;
                } else if (turn) {
                    zTurnPresent = true;
                    zTurnOccupied |= occupied;
                } else {
                    zThroughPresent = true;
                    zThroughOccupied |= occupied;
                }
            }
        }

        refreshDetectorCall(CALL_X_THROUGH, gameTime, xThroughPresent, xThroughOccupied);
        refreshDetectorCall(CALL_X_TURN, gameTime, xTurnPresent, xTurnOccupied);
        refreshDetectorCall(CALL_Z_THROUGH, gameTime, zThroughPresent, zThroughOccupied);
        refreshDetectorCall(CALL_Z_TURN, gameTime, zTurnPresent, zTurnOccupied);
        refreshDetectorCall(CALL_X_BUS, gameTime, xBusPresent, xBusOccupied);
        refreshDetectorCall(CALL_Z_BUS, gameTime, zBusPresent, zBusOccupied);

        cachedDetectorSnapshot = new DetectorSnapshot(
                count,
                !xThroughPresent || detectorCalls[CALL_X_THROUGH],
                !xTurnPresent || detectorCalls[CALL_X_TURN],
                !zThroughPresent || detectorCalls[CALL_Z_THROUGH],
                !zTurnPresent || detectorCalls[CALL_Z_TURN],
                xBusPresent && detectorCalls[CALL_X_BUS],
                zBusPresent && detectorCalls[CALL_Z_BUS]
        );
        nextDetectorScanGameTime = gameTime + 10L;
        return cachedDetectorSnapshot;
    }

    private void refreshDetectorCall(int index, long gameTime, boolean detectorPresent, boolean occupied) {
        if (!detectorPresent) {
            detectorCalls[index] = false;
            detectorReleaseAt[index] = 0L;
            return;
        }

        long releaseAt = detectorReleaseAt[index];
        if (releaseAt > 0L && gameTime >= releaseAt) {
            // The previously latched call has completed its green + clearance.
            // A vehicle still on the loop becomes a fresh call for a later cycle.
            detectorCalls[index] = occupied;
            detectorReleaseAt[index] = 0L;
        } else if (occupied && releaseAt == 0L) {
            detectorCalls[index] = true;
        }
    }

    /**
     * Marks a latched detector call as being served without clearing it mid-phase.
     * The call remains present through the supplied green/yellow/clearance window.
     */
    public void beginDetectorService(Direction.Axis axis, SignalMovement movement, int remainingServiceTicks) {
        if (level == null || level.isClientSide) {
            return;
        }
        int index = detectorCallIndex(axis, movement);
        if (!detectorCalls[index] || detectorReleaseAt[index] > 0L) {
            return;
        }
        detectorReleaseAt[index] = level.getGameTime() + Math.max(1, remainingServiceTicks);
        // Force the next GUI/signal query to observe the latched service state.
        nextDetectorScanGameTime = Math.min(nextDetectorScanGameTime, level.getGameTime());
        setChanged();
    }

    private static int detectorCallIndex(Direction.Axis axis, SignalMovement movement) {
        if (movement == SignalMovement.BUS) return axis == Direction.Axis.X ? CALL_X_BUS : CALL_Z_BUS;
        boolean turnMovement = movement.includesLeft() || movement.includesRight();
        if (axis == Direction.Axis.X) return turnMovement ? CALL_X_TURN : CALL_X_THROUGH;
        return turnMovement ? CALL_Z_TURN : CALL_Z_THROUGH;
    }

    public Map<BlockPos, SignalMovement> linkedSignals() {
        return Collections.unmodifiableMap(linkedSignals);
    }

    public boolean isLinkedSignal(BlockPos signalPos) {
        return linkedSignals.containsKey(signalPos);
    }

    @Nullable
    public SignalMovement movementFor(BlockPos signalPos) {
        return linkedSignals.get(signalPos);
    }

    public boolean linkSignal(BlockPos signalPos, SignalMovement movement) {
        if (level == null || level.isClientSide
                || !(level.getBlockEntity(signalPos) instanceof TrafficLightBlockEntity signal)) {
            return false;
        }

        BlockPos previousController = signal.linkedControllerPos();
        if (previousController != null && !previousController.equals(worldPosition)
                && level.isLoaded(previousController)
                && level.getBlockEntity(previousController) instanceof TrafficLightControllerBlockEntity oldController) {
            oldController.unlinkSignal(signalPos);
        }

        SignalMovement normalized = normalizeMovement(signal, movement);
        SignalMovement previous = linkedSignals.put(signalPos.immutable(), normalized);
        signal.linkToController(worldPosition, normalized);
        if (previous != normalized) {
            linksChanged();
        }
        return true;
    }

    public boolean setLinkedMovement(int linkIndex, SignalMovement requested) {
        Map.Entry<BlockPos, SignalMovement> entry = linkedEntryAt(linkIndex);
        if (entry == null || level == null || level.isClientSide) {
            return false;
        }

        BlockPos signalPos = entry.getKey();
        if (!(level.getBlockEntity(signalPos) instanceof TrafficLightBlockEntity signal)) {
            return false;
        }

        SignalMovement normalized = normalizeMovement(signal, requested);
        if (entry.getValue() == normalized) {
            return true;
        }

        linkedSignals.put(signalPos, normalized);
        signal.linkToController(worldPosition, normalized);
        linksChanged();
        return true;
    }

    /** Replaces a linked signal head in place while preserving orientation, finish and controller ownership. */
    public boolean setLinkedType(int linkIndex, int requestedType) {
        Map.Entry<BlockPos, SignalMovement> entry = linkedEntryAt(linkIndex);
        if (entry == null || level == null || level.isClientSide) {
            return false;
        }
        BlockPos signalPos = entry.getKey();
        if (!(level.getBlockEntity(signalPos) instanceof TrafficLightBlockEntity oldSignal)) {
            return false;
        }

        int targetType = Math.floorMod(requestedType, MAP_TYPE_BUS + 1);
        Block targetBlock = switch (targetType) {
            case MAP_TYPE_FOUR -> ModBlocks.TRAFFIC_LIGHT_4.get();
            case MAP_TYPE_FIVE -> ModBlocks.TRAFFIC_LIGHT_5.get();
            case MAP_TYPE_FIVE_RIGHT -> ModBlocks.TRAFFIC_LIGHT_5_RIGHT.get();
            case MAP_TYPE_STRAIGHT_ARROW -> ModBlocks.TRAFFIC_LIGHT_STRAIGHT_ARROW.get();
            case MAP_TYPE_U_TURN -> ModBlocks.TRAFFIC_LIGHT_U_TURN.get();
            case MAP_TYPE_BUS -> ModBlocks.TRAFFIC_LIGHT_BUS.get();
            case MAP_TYPE_DOGHOUSE -> ModBlocks.TRAFFIC_LIGHT_DOGHOUSE.get();
            case MAP_TYPE_LEFT -> ModBlocks.TRAFFIC_LIGHT_TURN_LEFT.get();
            case MAP_TYPE_RIGHT -> ModBlocks.TRAFFIC_LIGHT_TURN_RIGHT.get();
            case MAP_TYPE_PEDESTRIAN -> ModBlocks.TRAFFIC_LIGHT_2.get();
            case MAP_TYPE_RAMP_METER -> ModBlocks.RAMP_METER_SIGNAL.get();
            default -> ModBlocks.TRAFFIC_LIGHT.get();
        };
        if (oldSignal.getBlockState().getBlock() == targetBlock) {
            return true;
        }

        BlockState oldState = oldSignal.getBlockState();
        BlockState replacement = targetBlock.defaultBlockState();
        if (oldState.hasProperty(TrafficLightBlock.ROTATION) && replacement.hasProperty(TrafficLightBlock.ROTATION)) {
            replacement = replacement.setValue(TrafficLightBlock.ROTATION, oldState.getValue(TrafficLightBlock.ROTATION));
        }
        if (oldState.hasProperty(TrafficLightBlock.MOUNT) && replacement.hasProperty(TrafficLightBlock.MOUNT)) {
            replacement = replacement.setValue(TrafficLightBlock.MOUNT, oldState.getValue(TrafficLightBlock.MOUNT));
        }
        if (replacement.hasProperty(TrafficLightBlock.LIT)) {
            replacement = replacement.setValue(TrafficLightBlock.LIT, true);
        }

        SignalMovement requestedMovement = entry.getValue();
        level.setBlock(signalPos, replacement, Block.UPDATE_ALL);
        if (!(level.getBlockEntity(signalPos) instanceof TrafficLightBlockEntity newSignal)) {
            return false;
        }
        newSignal.copyVisualConfigurationFrom(oldSignal);
        SignalMovement normalized = normalizeMovement(newSignal, requestedMovement);
        linkedSignals.put(signalPos, normalized);
        newSignal.linkToController(worldPosition, normalized);
        linksChanged();
        return true;
    }

    @Nullable
    private Map.Entry<BlockPos, SignalMovement> linkedEntryAt(int index) {
        if (index < 0 || index >= linkedSignals.size() || index >= MAX_MAP_LINKS) {
            return null;
        }
        int current = 0;
        for (Map.Entry<BlockPos, SignalMovement> entry : linkedSignals.entrySet()) {
            if (current++ == index) {
                return entry;
            }
        }
        return null;
    }

    private int linkedMapData(int dataIndex) {
        int relative = dataIndex - LINK_DATA_BASE;
        int linkIndex = relative / LINK_DATA_STRIDE;
        int field = relative % LINK_DATA_STRIDE;
        Map.Entry<BlockPos, SignalMovement> entry = linkedEntryAt(linkIndex);
        if (entry == null) {
            return 0;
        }

        BlockPos signalPos = entry.getKey();
        return switch (field) {
            case LINK_FIELD_DX -> signalPos.getX() - worldPosition.getX();
            case LINK_FIELD_DY -> signalPos.getY() - worldPosition.getY();
            case LINK_FIELD_DZ -> signalPos.getZ() - worldPosition.getZ();
            case LINK_FIELD_MOVEMENT -> entry.getValue().ordinal();
            case LINK_FIELD_TYPE -> mapType(signalPos);
            default -> 0;
        };
    }

    private int mapType(BlockPos signalPos) {
        if (level == null || !level.isLoaded(signalPos)
                || !(level.getBlockEntity(signalPos) instanceof TrafficLightBlockEntity signal)) {
            return MAP_TYPE_STANDARD;
        }
        if (signal.isPedestrianSignal()) {
            return MAP_TYPE_PEDESTRIAN;
        }
        if (signal.isRampMeterSignal()) {
            return MAP_TYPE_RAMP_METER;
        }
        if (signal.isBusSignal()) return MAP_TYPE_BUS;
        if (signal.isStraightArrowSignal()) return MAP_TYPE_STRAIGHT_ARROW;
        if (signal.isUTurnSignal()) return MAP_TYPE_U_TURN;
        if (signal.isDedicatedTurnSignal()) {
            return signal.isRightTurnSignal() ? MAP_TYPE_RIGHT : MAP_TYPE_LEFT;
        }
        if (signal.isDoghouse()) {
            return MAP_TYPE_DOGHOUSE;
        }
        if (signal.isFiveRightSection()) {
            return MAP_TYPE_FIVE_RIGHT;
        }
        if (signal.isFiveSection()) {
            return MAP_TYPE_FIVE;
        }
        if (signal.isFourSection()) {
            return MAP_TYPE_FOUR;
        }
        return MAP_TYPE_STANDARD;
    }

    private static SignalMovement normalizeMovement(TrafficLightBlockEntity signal, SignalMovement requested) {
        if (signal.isPedestrianSignal()) {
            return SignalMovement.PEDESTRIAN;
        }
        if (signal.isRampMeterSignal()) return SignalMovement.THROUGH;
        if (signal.isBusSignal()) return SignalMovement.BUS;
        if (signal.isStraightArrowSignal()) return SignalMovement.THROUGH;
        if (signal.isUTurnSignal()) return SignalMovement.U_TURN;
        if (signal.isDedicatedTurnSignal()) {
            return signal.isRightTurnSignal() ? SignalMovement.RIGHT : SignalMovement.LEFT;
        }
        if (signal.isFiveRightSection()) {
            return switch (requested) {
                case THROUGH_RIGHT -> SignalMovement.THROUGH_RIGHT;
                case RIGHT, THROUGH -> SignalMovement.THROUGH_RIGHT;
                default -> SignalMovement.THROUGH_RIGHT;
            };
        }
        if (signal.isFourSection() || signal.isFiveSection() || signal.isDoghouse()) {
            return switch (requested) {
                case THROUGH, LEFT, THROUGH_LEFT -> requested;
                default -> SignalMovement.THROUGH_LEFT;
            };
        }
        return requested == SignalMovement.PEDESTRIAN
                || requested == SignalMovement.THROUGH_LEFT
                || requested == SignalMovement.THROUGH_RIGHT
                || requested == SignalMovement.U_TURN
                || requested == SignalMovement.BUS
                ? SignalMovement.THROUGH
                : requested;
    }

    public boolean unlinkSignal(BlockPos signalPos) {
        SignalMovement removed = linkedSignals.remove(signalPos);
        if (removed == null) {
            return false;
        }

        if (level != null && !level.isClientSide && level.isLoaded(signalPos)
                && level.getBlockEntity(signalPos) instanceof TrafficLightBlockEntity signal
                && worldPosition.equals(signal.linkedControllerPos())) {
            signal.clearControllerLink();
        }
        linksChanged();
        return true;
    }

    public int clearLinkedSignals() {
        if (linkedSignals.isEmpty()) {
            return 0;
        }
        int count = linkedSignals.size();
        for (BlockPos pos : linkedSignals.keySet()) {
            if (level != null && level.isLoaded(pos)
                    && level.getBlockEntity(pos) instanceof TrafficLightBlockEntity signal
                    && worldPosition.equals(signal.linkedControllerPos())) {
                signal.clearControllerLink();
            }
        }
        linkedSignals.clear();
        linksChanged();
        return count;
    }

    public void pruneInvalidLinks() {
        if (level == null || level.isClientSide || linkedSignals.isEmpty()) {
            return;
        }

        boolean changed = false;
        Iterator<Map.Entry<BlockPos, SignalMovement>> iterator = linkedSignals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, SignalMovement> entry = iterator.next();
            BlockPos signalPos = entry.getKey();
            if (!level.isLoaded(signalPos)) {
                continue;
            }
            if (!(level.getBlockEntity(signalPos) instanceof TrafficLightBlockEntity signal)) {
                iterator.remove();
                changed = true;
                continue;
            }
            BlockPos signalController = signal.linkedControllerPos();
            if (signalController == null) {
                // Restore the signal-side copy after an older save or partial chunk load.
                signal.linkToController(worldPosition, entry.getValue());
            } else if (!worldPosition.equals(signalController)) {
                // The signal was reassigned while this controller was unloaded.
                // Drop the stale entry instead of stealing ownership back.
                iterator.remove();
                changed = true;
            } else if (signal.linkedMovement() != entry.getValue()) {
                signal.linkToController(worldPosition, entry.getValue());
            }
        }
        if (changed) {
            linksChanged();
        }
    }

    private void settingsChanged() {
        if (level != null && !level.isClientSide) {
            cycleStartGameTime = level.getGameTime();
        }
        sync();
    }

    private void linksChanged() {
        sync();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    /** Populates the cabinet drawer once. Emptying it never regenerates the tools. */
    public void ensureStarterKit() {
        if (starterKitGenerated || level == null || level.isClientSide) {
            return;
        }
        starterKitGenerated = true;
        starterKit.set(0, new ItemStack(ModItems.ADJUSTER.get()));
        starterKit.set(1, new ItemStack(ModItems.ENGINEER_WAND.get()));
        setChanged();
    }

    @Override
    public int getContainerSize() {
        return STARTER_KIT_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : starterKit) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return starterKit.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(starterKit, slot, amount);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(starterKit, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        starterKit.set(slot, stack);
        if (stack.getCount() > getMaxStackSize(stack)) {
            stack.setCount(getMaxStackSize(stack));
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public void clearContent() {
        starterKit.clear();
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.trafficcontrol.controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ensureStarterKit();
        return new TrafficLightControllerMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_THROUGH_GREEN, throughGreenTicks);
        tag.putInt(TAG_TURN_PRIORITY, turnPriority);
        tag.putBoolean(TAG_NIGHT_FLASH, nightFlash);
        tag.putInt(TAG_PHASE_SPEED, phaseSpeed);
        tag.putBoolean(TAG_FAIL_SAFE, failSafe);
        tag.putInt(TAG_NORTH_PRIORITY, northPriority);
        tag.putInt(TAG_EAST_PRIORITY, eastPriority);
        tag.putInt(TAG_SOUTH_PRIORITY, southPriority);
        tag.putInt(TAG_WEST_PRIORITY, westPriority);
        tag.putLong(TAG_CYCLE_START, cycleStartGameTime);
        tag.putBoolean(TAG_STARTER_KIT_GENERATED, starterKitGenerated);
        ContainerHelper.saveAllItems(tag, starterKit, registries);

        ListTag links = new ListTag();
        for (Map.Entry<BlockPos, SignalMovement> entry : linkedSignals.entrySet()) {
            CompoundTag link = new CompoundTag();
            link.putLong(TAG_LINK_POS, entry.getKey().asLong());
            link.putString(TAG_LINK_MOVEMENT, entry.getValue().getSerializedName());
            links.add(link);
        }
        tag.put(TAG_LINKED_SIGNALS, links);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_THROUGH_GREEN)) {
            int savedTiming = tag.getInt(TAG_THROUGH_GREEN);
            throughGreenTicks = savedTiming <= 120
                    ? 300
                    : clamp(savedTiming, 100, 1_200);
        }
        turnPriority = Math.floorMod(tag.getInt(TAG_TURN_PRIORITY), 3);
        nightFlash = tag.getBoolean(TAG_NIGHT_FLASH);
        phaseSpeed = tag.contains(TAG_PHASE_SPEED) ? Math.floorMod(tag.getInt(TAG_PHASE_SPEED), 3) : 1;
        failSafe = tag.getBoolean(TAG_FAIL_SAFE);
        northPriority = tag.contains(TAG_NORTH_PRIORITY) ? Math.floorMod(tag.getInt(TAG_NORTH_PRIORITY), 3) : APPROACH_NORMAL;
        eastPriority = tag.contains(TAG_EAST_PRIORITY) ? Math.floorMod(tag.getInt(TAG_EAST_PRIORITY), 3) : APPROACH_NORMAL;
        southPriority = tag.contains(TAG_SOUTH_PRIORITY) ? Math.floorMod(tag.getInt(TAG_SOUTH_PRIORITY), 3) : APPROACH_NORMAL;
        westPriority = tag.contains(TAG_WEST_PRIORITY) ? Math.floorMod(tag.getInt(TAG_WEST_PRIORITY), 3) : APPROACH_NORMAL;
        cycleStartGameTime = tag.contains(TAG_CYCLE_START) ? tag.getLong(TAG_CYCLE_START) : 0L;
        starterKitGenerated = tag.getBoolean(TAG_STARTER_KIT_GENERATED);
        starterKit.clear();
        ContainerHelper.loadAllItems(tag, starterKit, registries);

        linkedSignals.clear();
        ListTag links = tag.getList(TAG_LINKED_SIGNALS, Tag.TAG_COMPOUND);
        for (int index = 0; index < links.size(); index++) {
            CompoundTag link = links.getCompound(index);
            if (link.contains(TAG_LINK_POS)) {
                linkedSignals.put(
                        BlockPos.of(link.getLong(TAG_LINK_POS)),
                        SignalMovement.byName(link.getString(TAG_LINK_MOVEMENT))
                );
            }
        }
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

    private static int demandMask(DetectorSnapshot snapshot, Direction.Axis axis) {
        int mask = 0;
        if (snapshot.throughDemand(axis)) {
            mask |= 1;
        }
        if (snapshot.turnDemand(axis)) {
            mask |= 2;
        }
        if (snapshot.busDemand(axis)) {
            mask |= 4;
        }
        return mask;
    }

    public record DetectorSnapshot(
            int detectorCount,
            boolean xThroughDemand,
            boolean xTurnDemand,
            boolean zThroughDemand,
            boolean zTurnDemand,
            boolean xBusDemand,
            boolean zBusDemand
    ) {
        private static final DetectorSnapshot DEFAULT = new DetectorSnapshot(0, true, true, true, true, false, false);

        public boolean throughDemand(Direction.Axis axis) {
            return axis == Direction.Axis.X ? xThroughDemand : zThroughDemand;
        }

        public boolean turnDemand(Direction.Axis axis) {
            return axis == Direction.Axis.X ? xTurnDemand : zTurnDemand;
        }

        public boolean busDemand(Direction.Axis axis) {
            return axis == Direction.Axis.X ? xBusDemand : zBusDemand;
        }

        public boolean axisHasDemand(Direction.Axis axis) {
            return throughDemand(axis) || turnDemand(axis) || busDemand(axis);
        }
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
