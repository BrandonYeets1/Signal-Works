package com.dgtlbrandxn.signalworks.blockentity;

import com.dgtlbrandxn.signalworks.block.ConstructionMessageBoardBlock;
import com.dgtlbrandxn.signalworks.block.ConstructionMessageMode;
import com.dgtlbrandxn.signalworks.menu.ConstructionMessageBoardMenu;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/** Persistent three-line message and display program for a portable LED construction board. */
public final class ConstructionMessageBoardBlockEntity extends BlockEntity implements MenuProvider {
    public static final int FIELD_LINE_1 = 0;
    public static final int FIELD_LINE_2 = 1;
    public static final int FIELD_LINE_3 = 2;
    public static final int FIELD_COUNT = 3;
    public static final int MAX_TEXT_LENGTH = 18;

    public static final int DATA_MODE = 0;
    public static final int DATA_FLAGS = 1;
    public static final int DATA_TEXT_BASE = 2;
    public static final int DATA_TEXT_STRIDE = MAX_TEXT_LENGTH + 1;
    public static final int DATA_TEXT_END = DATA_TEXT_BASE + FIELD_COUNT * DATA_TEXT_STRIDE;
    public static final int DATA_TEXT_READY = DATA_TEXT_END;
    public static final int DATA_COUNT = DATA_TEXT_READY + 1;

    public static final int FLAG_LIT = 1;
    public static final int FLAG_DEPLOYED = 2;

    private static final String TAG_LINE_1 = "Line1";
    private static final String TAG_LINE_2 = "Line2";
    private static final String TAG_LINE_3 = "Line3";
    private static final String TAG_MODE = "DisplayMode";

    private String line1 = "ROAD WORK";
    private String line2 = "AHEAD";
    private String line3 = "USE CAUTION";
    private ConstructionMessageMode mode = ConstructionMessageMode.MESSAGE;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == DATA_MODE) {
                return mode.ordinal();
            }
            if (index == DATA_FLAGS) {
                int flags = 0;
                BlockState state = getBlockState();
                if (state.hasProperty(ConstructionMessageBoardBlock.LIT)
                        && state.getValue(ConstructionMessageBoardBlock.LIT)) {
                    flags |= FLAG_LIT;
                }
                if (state.hasProperty(ConstructionMessageBoardBlock.DEPLOYED)
                        && state.getValue(ConstructionMessageBoardBlock.DEPLOYED)) {
                    flags |= FLAG_DEPLOYED;
                }
                return flags;
            }
            if (index >= DATA_TEXT_BASE && index < DATA_TEXT_END) {
                int encoded = index - DATA_TEXT_BASE;
                int field = encoded / DATA_TEXT_STRIDE;
                int within = encoded % DATA_TEXT_STRIDE;
                String value = rawText(field);
                if (within == 0) {
                    return value.length();
                }
                int characterIndex = within - 1;
                return characterIndex < value.length() ? value.charAt(characterIndex) : 0;
            }
            if (index == DATA_TEXT_READY) {
                return 1;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == DATA_MODE) {
                setMode(ConstructionMessageMode.byOrdinal(value));
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public ConstructionMessageBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONSTRUCTION_MESSAGE_BOARD.get(), pos, state);
    }

    public ContainerData dataAccess() {
        return dataAccess;
    }

    public ConstructionMessageMode mode() {
        return mode;
    }

    public String line1() {
        return line1.toUpperCase(Locale.ROOT);
    }

    public String line2() {
        return line2.toUpperCase(Locale.ROOT);
    }

    public String line3() {
        return line3.toUpperCase(Locale.ROOT);
    }

    public String rawText(int field) {
        return switch (field) {
            case FIELD_LINE_1 -> line1;
            case FIELD_LINE_2 -> line2;
            case FIELD_LINE_3 -> line3;
            default -> "";
        };
    }

    public void setText(int field, String value) {
        String clean = sanitize(value);
        switch (field) {
            case FIELD_LINE_1 -> line1 = clean;
            case FIELD_LINE_2 -> line2 = clean;
            case FIELD_LINE_3 -> line3 = clean;
            default -> {
                return;
            }
        }
        sync();
    }

    public void cycleMode(int amount) {
        setMode(mode.step(amount));
    }

    public void setMode(ConstructionMessageMode mode) {
        this.mode = mode;
        sync();
    }

    public void togglePower() {
        updateStateProperty(ConstructionMessageBoardBlock.LIT,
                !getBlockState().getValue(ConstructionMessageBoardBlock.LIT));
    }

    public void toggleDeployed() {
        boolean deployed = !getBlockState().getValue(ConstructionMessageBoardBlock.DEPLOYED);
        BlockState updated = getBlockState().setValue(ConstructionMessageBoardBlock.DEPLOYED, deployed);
        if (!deployed) {
            updated = updated.setValue(ConstructionMessageBoardBlock.LIT, false);
        }
        updateState(updated);
    }

    private void updateStateProperty(net.minecraft.world.level.block.state.properties.BooleanProperty property, boolean value) {
        updateState(getBlockState().setValue(property, value));
    }

    private void updateState(BlockState state) {
        if (level != null) {
            level.setBlock(worldPosition, state, Block.UPDATE_ALL);
        }
        sync();
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder clean = new StringBuilder();
        for (int index = 0; index < value.length() && clean.length() < MAX_TEXT_LENGTH; index++) {
            char character = value.charAt(index);
            if (character >= 32 && character <= 126) {
                clean.append(character);
            }
        }
        return clean.toString().trim();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.trafficcontrol.construction_message_board");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ConstructionMessageBoardMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString(TAG_LINE_1, line1);
        tag.putString(TAG_LINE_2, line2);
        tag.putString(TAG_LINE_3, line3);
        tag.putInt(TAG_MODE, mode.ordinal());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        line1 = sanitize(tag.contains(TAG_LINE_1) ? tag.getString(TAG_LINE_1) : line1);
        line2 = sanitize(tag.contains(TAG_LINE_2) ? tag.getString(TAG_LINE_2) : line2);
        line3 = sanitize(tag.contains(TAG_LINE_3) ? tag.getString(TAG_LINE_3) : line3);
        mode = tag.contains(TAG_MODE)
                ? ConstructionMessageMode.byOrdinal(tag.getInt(TAG_MODE))
                : ConstructionMessageMode.MESSAGE;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
