package com.dgtlbrandxn.signalworks.menu;

import com.dgtlbrandxn.signalworks.block.SignalMovement;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightControllerBlockEntity;
import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import com.dgtlbrandxn.signalworks.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Controller menu with a one-time two-slot field-kit drawer and synchronized settings. */
public final class TrafficLightControllerMenu extends AbstractContainerMenu {
    public static final int BUTTON_TIMING_DOWN = 0;
    public static final int BUTTON_TIMING_UP = 1;
    public static final int BUTTON_PRIORITY = 2;
    public static final int BUTTON_NIGHT_FLASH = 3;
    public static final int BUTTON_SPEED = 4;
    public static final int BUTTON_FAIL_SAFE = 5;
    public static final int BUTTON_PRIORITY_NORTH = 6;
    public static final int BUTTON_PRIORITY_EAST = 7;
    public static final int BUTTON_PRIORITY_SOUTH = 8;
    public static final int BUTTON_PRIORITY_WEST = 9;

    private static final int BUTTON_ASSIGN_BASE = 1_000;
    private static final int BUTTON_ASSIGN_STRIDE = SignalMovement.values().length;
    private static final int BUTTON_TYPE_BASE = 2_000;
    private static final int BUTTON_TYPE_STRIDE = 2;

    public static final int KIT_SLOT_COUNT = 2;

    private final ContainerData data;
    private final Container kitContainer;
    private final ContainerLevelAccess access;
    @Nullable
    private final TrafficLightControllerBlockEntity controller;

    public TrafficLightControllerMenu(int containerId, Inventory inventory) {
        this(
                containerId,
                inventory,
                new SimpleContainerData(TrafficLightControllerBlockEntity.DATA_COUNT),
                ContainerLevelAccess.NULL,
                new SimpleContainer(KIT_SLOT_COUNT),
                null
        );
    }

    public TrafficLightControllerMenu(
            int containerId,
            Inventory inventory,
            TrafficLightControllerBlockEntity controller
    ) {
        this(
                containerId,
                inventory,
                controller.dataAccess(),
                ContainerLevelAccess.create(controller.getLevel(), controller.getBlockPos()),
                controller,
                controller
        );
    }

    private TrafficLightControllerMenu(
            int containerId,
            Inventory inventory,
            ContainerData data,
            ContainerLevelAccess access,
            Container kitContainer,
            @Nullable TrafficLightControllerBlockEntity controller
    ) {
        super(ModMenus.TRAFFIC_LIGHT_CONTROLLER.get(), containerId);
        this.data = data;
        this.access = access;
        this.kitContainer = kitContainer;
        this.controller = controller;
        checkContainerSize(kitContainer, KIT_SLOT_COUNT);
        kitContainer.startOpen(inventory.player);

        // Cabinet field-kit drawer. These slots are take-only and never restock.
        addSlot(new Slot(kitContainer, 0, 374, 243) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addSlot(new Slot(kitContainer, 1, 396, 243) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });

        checkContainerDataCount(data, TrafficLightControllerBlockEntity.DATA_COUNT);
        addDataSlots(data);
    }

    public int throughGreenTicks() {
        return data.get(TrafficLightControllerBlockEntity.DATA_THROUGH_GREEN);
    }

    public int priorityMode() {
        return data.get(TrafficLightControllerBlockEntity.DATA_TURN_PRIORITY);
    }

    public boolean nightFlashEnabled() {
        return data.get(TrafficLightControllerBlockEntity.DATA_NIGHT_FLASH) != 0;
    }

    public int speedMode() {
        return data.get(TrafficLightControllerBlockEntity.DATA_PHASE_SPEED);
    }

    public boolean failSafeEnabled() {
        return data.get(TrafficLightControllerBlockEntity.DATA_FAIL_SAFE) != 0;
    }

    public int linkedSignalCount() {
        return data.get(TrafficLightControllerBlockEntity.DATA_LINKED_COUNT);
    }

    public int detectorCount() {
        return data.get(TrafficLightControllerBlockEntity.DATA_DETECTOR_COUNT);
    }

    public int xDemandMask() {
        return data.get(TrafficLightControllerBlockEntity.DATA_X_DEMAND);
    }

    public int zDemandMask() {
        return data.get(TrafficLightControllerBlockEntity.DATA_Z_DEMAND);
    }

    public int northPriority() { return data.get(TrafficLightControllerBlockEntity.DATA_NORTH_PRIORITY); }
    public int eastPriority() { return data.get(TrafficLightControllerBlockEntity.DATA_EAST_PRIORITY); }
    public int southPriority() { return data.get(TrafficLightControllerBlockEntity.DATA_SOUTH_PRIORITY); }
    public int westPriority() { return data.get(TrafficLightControllerBlockEntity.DATA_WEST_PRIORITY); }

    public int mapLinkCount() {
        return Math.min(linkedSignalCount(), TrafficLightControllerBlockEntity.MAX_MAP_LINKS);
    }

    public int linkDx(int linkIndex) {
        return linkData(linkIndex, TrafficLightControllerBlockEntity.LINK_FIELD_DX);
    }

    public int linkDy(int linkIndex) {
        return linkData(linkIndex, TrafficLightControllerBlockEntity.LINK_FIELD_DY);
    }

    public int linkDz(int linkIndex) {
        return linkData(linkIndex, TrafficLightControllerBlockEntity.LINK_FIELD_DZ);
    }

    public SignalMovement linkMovement(int linkIndex) {
        int ordinal = linkData(linkIndex, TrafficLightControllerBlockEntity.LINK_FIELD_MOVEMENT);
        SignalMovement[] values = SignalMovement.values();
        return values[Math.max(0, Math.min(values.length - 1, ordinal))];
    }

    public int linkType(int linkIndex) {
        return linkData(linkIndex, TrafficLightControllerBlockEntity.LINK_FIELD_TYPE);
    }

    private int linkData(int linkIndex, int field) {
        if (linkIndex < 0 || linkIndex >= TrafficLightControllerBlockEntity.MAX_MAP_LINKS) {
            return 0;
        }
        return data.get(
                TrafficLightControllerBlockEntity.LINK_DATA_BASE
                        + linkIndex * TrafficLightControllerBlockEntity.LINK_DATA_STRIDE
                        + field
        );
    }

    public static int assignmentButtonId(int linkIndex, SignalMovement movement) {
        return BUTTON_ASSIGN_BASE + linkIndex * BUTTON_ASSIGN_STRIDE + movement.ordinal();
    }

    public static int typeButtonId(int linkIndex, int delta) {
        return BUTTON_TYPE_BASE + linkIndex * BUTTON_TYPE_STRIDE + (delta > 0 ? 1 : 0);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= BUTTON_TYPE_BASE) {
            int encoded = id - BUTTON_TYPE_BASE;
            int linkIndex = encoded / BUTTON_TYPE_STRIDE;
            int delta = encoded % BUTTON_TYPE_STRIDE == 1 ? 1 : -1;
            if (controller == null || linkIndex < 0 || linkIndex >= TrafficLightControllerBlockEntity.MAX_MAP_LINKS) {
                return false;
            }
            boolean changed = controller.setLinkedType(linkIndex, linkType(linkIndex) + delta);
            if (changed) broadcastChanges();
            return changed;
        }

        if (id >= BUTTON_ASSIGN_BASE) {
            int encoded = id - BUTTON_ASSIGN_BASE;
            int linkIndex = encoded / BUTTON_ASSIGN_STRIDE;
            int movementOrdinal = encoded % BUTTON_ASSIGN_STRIDE;
            SignalMovement[] movements = SignalMovement.values();
            if (controller == null
                    || linkIndex < 0
                    || linkIndex >= TrafficLightControllerBlockEntity.MAX_MAP_LINKS
                    || movementOrdinal < 0
                    || movementOrdinal >= movements.length) {
                return false;
            }
            boolean changed = controller.setLinkedMovement(linkIndex, movements[movementOrdinal]);
            if (changed) {
                broadcastChanges();
            }
            return changed;
        }

        switch (id) {
            case BUTTON_TIMING_DOWN -> data.set(
                    TrafficLightControllerBlockEntity.DATA_THROUGH_GREEN,
                    throughGreenTicks() - 100
            );
            case BUTTON_TIMING_UP -> data.set(
                    TrafficLightControllerBlockEntity.DATA_THROUGH_GREEN,
                    throughGreenTicks() + 100
            );
            case BUTTON_PRIORITY -> data.set(
                    TrafficLightControllerBlockEntity.DATA_TURN_PRIORITY,
                    priorityMode() + 1
            );
            case BUTTON_NIGHT_FLASH -> data.set(
                    TrafficLightControllerBlockEntity.DATA_NIGHT_FLASH,
                    nightFlashEnabled() ? 0 : 1
            );
            case BUTTON_SPEED -> data.set(
                    TrafficLightControllerBlockEntity.DATA_PHASE_SPEED,
                    speedMode() + 1
            );
            case BUTTON_FAIL_SAFE -> data.set(
                    TrafficLightControllerBlockEntity.DATA_FAIL_SAFE,
                    failSafeEnabled() ? 0 : 1
            );
            case BUTTON_PRIORITY_NORTH -> data.set(
                    TrafficLightControllerBlockEntity.DATA_NORTH_PRIORITY, northPriority() + 1);
            case BUTTON_PRIORITY_EAST -> data.set(
                    TrafficLightControllerBlockEntity.DATA_EAST_PRIORITY, eastPriority() + 1);
            case BUTTON_PRIORITY_SOUTH -> data.set(
                    TrafficLightControllerBlockEntity.DATA_SOUTH_PRIORITY, southPriority() + 1);
            case BUTTON_PRIORITY_WEST -> data.set(
                    TrafficLightControllerBlockEntity.DATA_WEST_PRIORITY, westPriority() + 1);
            default -> {
                return false;
            }
        }
        broadcastChanges();
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        kitContainer.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.TRAFFIC_LIGHT_CONTROL_BOX.get());
    }
}
