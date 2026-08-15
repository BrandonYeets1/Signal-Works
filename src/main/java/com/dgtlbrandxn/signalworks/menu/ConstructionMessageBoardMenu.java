package com.dgtlbrandxn.signalworks.menu;

import com.dgtlbrandxn.signalworks.block.ConstructionMessageMode;
import com.dgtlbrandxn.signalworks.blockentity.ConstructionMessageBoardBlockEntity;
import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import com.dgtlbrandxn.signalworks.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Slotless editor menu for portable construction message boards. */
public final class ConstructionMessageBoardMenu extends AbstractContainerMenu {
    public static final int BUTTON_MODE_PREV = 0;
    public static final int BUTTON_MODE_NEXT = 1;
    public static final int BUTTON_POWER = 2;
    public static final int BUTTON_DEPLOY = 3;
    public static final int BUTTON_MODE_SET_BASE = 20;
    public static final int BUTTON_CLEAR_TEXT_BASE = 100;
    public static final int BUTTON_COMMIT_TEXT_BASE = 110;
    private static final int TEXT_PACKET_FLAG = 0x40000000;

    private final ContainerData data;
    private final ContainerLevelAccess access;
    @Nullable
    private final ConstructionMessageBoardBlockEntity board;
    private final StringBuilder[] pendingText = new StringBuilder[ConstructionMessageBoardBlockEntity.FIELD_COUNT];

    public ConstructionMessageBoardMenu(int containerId, Inventory inventory) {
        this(containerId, inventory,
                new SimpleContainerData(ConstructionMessageBoardBlockEntity.DATA_COUNT),
                ContainerLevelAccess.NULL, null);
    }

    public ConstructionMessageBoardMenu(
            int containerId,
            Inventory inventory,
            ConstructionMessageBoardBlockEntity board
    ) {
        this(containerId, inventory, board.dataAccess(),
                ContainerLevelAccess.create(board.getLevel(), board.getBlockPos()), board);
    }

    private ConstructionMessageBoardMenu(
            int containerId,
            Inventory inventory,
            ContainerData data,
            ContainerLevelAccess access,
            @Nullable ConstructionMessageBoardBlockEntity board
    ) {
        super(ModMenus.CONSTRUCTION_MESSAGE_BOARD.get(), containerId);
        this.data = data;
        this.access = access;
        this.board = board;
        checkContainerDataCount(data, ConstructionMessageBoardBlockEntity.DATA_COUNT);
        addDataSlots(data);
        for (int index = 0; index < pendingText.length; index++) {
            pendingText[index] = new StringBuilder();
        }
    }

    public ConstructionMessageMode mode() {
        return ConstructionMessageMode.byOrdinal(data.get(ConstructionMessageBoardBlockEntity.DATA_MODE));
    }

    public boolean lit() {
        return (data.get(ConstructionMessageBoardBlockEntity.DATA_FLAGS)
                & ConstructionMessageBoardBlockEntity.FLAG_LIT) != 0;
    }

    public boolean deployed() {
        return (data.get(ConstructionMessageBoardBlockEntity.DATA_FLAGS)
                & ConstructionMessageBoardBlockEntity.FLAG_DEPLOYED) != 0;
    }

    public boolean textReady() {
        return data.get(ConstructionMessageBoardBlockEntity.DATA_TEXT_READY) != 0;
    }

    public String text(int field) {
        if (field < 0 || field >= ConstructionMessageBoardBlockEntity.FIELD_COUNT) {
            return "";
        }
        int base = ConstructionMessageBoardBlockEntity.DATA_TEXT_BASE
                + field * ConstructionMessageBoardBlockEntity.DATA_TEXT_STRIDE;
        int length = Math.max(0, Math.min(
                ConstructionMessageBoardBlockEntity.MAX_TEXT_LENGTH,
                data.get(base)
        ));
        StringBuilder value = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            int character = data.get(base + 1 + index);
            if (character >= 32 && character <= 126) {
                value.append((char) character);
            }
        }
        return value.toString();
    }

    public static int packTextChunk(int field, int chunkIndex, int first, int second, int third) {
        return TEXT_PACKET_FLAG
                | ((field & 0x3) << 28)
                | ((chunkIndex & 0x3F) << 22)
                | ((first & 0x7F) << 15)
                | ((second & 0x7F) << 8)
                | ((third & 0x7F) << 1);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if ((id & TEXT_PACKET_FLAG) != 0) {
            if (board == null) {
                return false;
            }
            int field = (id >>> 28) & 0x3;
            int chunkIndex = (id >>> 22) & 0x3F;
            if (field < 0 || field >= pendingText.length) {
                return false;
            }
            StringBuilder builder = pendingText[field];
            int expectedOffset = chunkIndex * 3;
            while (builder.length() < expectedOffset) {
                builder.append(' ');
            }
            appendCharacter(builder, (id >>> 15) & 0x7F);
            appendCharacter(builder, (id >>> 8) & 0x7F);
            appendCharacter(builder, (id >>> 1) & 0x7F);
            if (builder.length() > ConstructionMessageBoardBlockEntity.MAX_TEXT_LENGTH) {
                builder.setLength(ConstructionMessageBoardBlockEntity.MAX_TEXT_LENGTH);
            }
            return true;
        }

        if (id >= BUTTON_CLEAR_TEXT_BASE
                && id < BUTTON_CLEAR_TEXT_BASE + ConstructionMessageBoardBlockEntity.FIELD_COUNT) {
            pendingText[id - BUTTON_CLEAR_TEXT_BASE].setLength(0);
            return true;
        }

        if (id >= BUTTON_COMMIT_TEXT_BASE
                && id < BUTTON_COMMIT_TEXT_BASE + ConstructionMessageBoardBlockEntity.FIELD_COUNT) {
            if (board == null) {
                return false;
            }
            int field = id - BUTTON_COMMIT_TEXT_BASE;
            board.setText(field, pendingText[field].toString());
            broadcastChanges();
            return true;
        }

        if (board == null) {
            return false;
        }

        if (id >= BUTTON_MODE_SET_BASE
                && id < BUTTON_MODE_SET_BASE + ConstructionMessageMode.values().length) {
            board.setMode(ConstructionMessageMode.byOrdinal(id - BUTTON_MODE_SET_BASE));
            broadcastChanges();
            return true;
        }

        switch (id) {
            case BUTTON_MODE_PREV -> board.cycleMode(-1);
            case BUTTON_MODE_NEXT -> board.cycleMode(1);
            case BUTTON_POWER -> board.togglePower();
            case BUTTON_DEPLOY -> board.toggleDeployed();
            default -> {
                return false;
            }
        }
        broadcastChanges();
        return true;
    }

    private static void appendCharacter(StringBuilder builder, int character) {
        if (character >= 32 && character <= 126
                && builder.length() < ConstructionMessageBoardBlockEntity.MAX_TEXT_LENGTH) {
            builder.append((char) character);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.CONSTRUCTION_MESSAGE_BOARD.get());
    }
}
