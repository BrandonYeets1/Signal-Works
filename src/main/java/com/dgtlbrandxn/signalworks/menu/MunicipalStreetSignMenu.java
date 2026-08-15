package com.dgtlbrandxn.signalworks.menu;

import com.dgtlbrandxn.signalworks.block.MunicipalSignFont;
import com.dgtlbrandxn.signalworks.block.MunicipalSignShape;
import com.dgtlbrandxn.signalworks.block.MunicipalSignStyle;
import com.dgtlbrandxn.signalworks.block.MunicipalTextSize;
import com.dgtlbrandxn.signalworks.blockentity.MunicipalStreetSignBlockEntity;
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

/** Slotless menu used by the terminal-style municipal street-sign editor. */
public final class MunicipalStreetSignMenu extends AbstractContainerMenu {
    public static final int BUTTON_STYLE_PREV = 0;
    public static final int BUTTON_STYLE_NEXT = 1;
    public static final int BUTTON_UPPERCASE = 2;
    public static final int BUTTON_REFLECTIVE = 3;
    public static final int BUTTON_DOUBLE_SIDED = 4;
    public static final int BUTTON_SHAPE_PREV = 5;
    public static final int BUTTON_SHAPE_NEXT = 6;
    public static final int BUTTON_FONT_PREV = 7;
    public static final int BUTTON_FONT_NEXT = 8;
    public static final int BUTTON_TEXT_SIZE_PREV = 9;
    public static final int BUTTON_TEXT_SIZE_NEXT = 10;
    public static final int BUTTON_BACKLIT = 11;
    public static final int BUTTON_STYLE_SET_BASE = 20;

    public static final int BUTTON_CLEAR_TEXT_BASE = 100;
    public static final int BUTTON_COMMIT_TEXT_BASE = 110;
    private static final int TEXT_PACKET_FLAG = 0x40000000;

    private final ContainerData data;
    private final ContainerLevelAccess access;
    @Nullable
    private final MunicipalStreetSignBlockEntity sign;
    private final StringBuilder[] pendingText = new StringBuilder[MunicipalStreetSignBlockEntity.FIELD_COUNT];

    public MunicipalStreetSignMenu(int containerId, Inventory inventory) {
        this(
                containerId,
                inventory,
                new SimpleContainerData(MunicipalStreetSignBlockEntity.DATA_COUNT),
                ContainerLevelAccess.NULL,
                null
        );
    }

    public MunicipalStreetSignMenu(
            int containerId,
            Inventory inventory,
            MunicipalStreetSignBlockEntity sign
    ) {
        this(
                containerId,
                inventory,
                sign.dataAccess(),
                ContainerLevelAccess.create(sign.getLevel(), sign.getBlockPos()),
                sign
        );
    }

    private MunicipalStreetSignMenu(
            int containerId,
            Inventory inventory,
            ContainerData data,
            ContainerLevelAccess access,
            @Nullable MunicipalStreetSignBlockEntity sign
    ) {
        super(ModMenus.MUNICIPAL_STREET_SIGN.get(), containerId);
        this.data = data;
        this.access = access;
        this.sign = sign;
        checkContainerDataCount(data, MunicipalStreetSignBlockEntity.DATA_COUNT);
        addDataSlots(data);
        for (int field = 0; field < pendingText.length; field++) {
            pendingText[field] = new StringBuilder();
        }
    }

    public MunicipalSignStyle style() {
        return MunicipalSignStyle.byOrdinal(data.get(MunicipalStreetSignBlockEntity.DATA_STYLE));
    }

    public MunicipalSignShape shape() {
        return MunicipalSignShape.byOrdinal(data.get(MunicipalStreetSignBlockEntity.DATA_SHAPE));
    }

    public MunicipalSignFont fontProfile() {
        return MunicipalSignFont.byOrdinal(data.get(MunicipalStreetSignBlockEntity.DATA_FONT));
    }

    public MunicipalTextSize textSize() {
        return MunicipalTextSize.byOrdinal(data.get(MunicipalStreetSignBlockEntity.DATA_TEXT_SIZE));
    }

    /** True after the server has sent the complete initial text payload. */
    public boolean textReady() {
        return data.get(MunicipalStreetSignBlockEntity.DATA_TEXT_READY) != 0;
    }

    public boolean uppercase() {
        return (flags() & MunicipalStreetSignBlockEntity.FLAG_UPPERCASE) != 0;
    }

    public boolean reflective() {
        return (flags() & MunicipalStreetSignBlockEntity.FLAG_REFLECTIVE) != 0;
    }

    public boolean doubleSided() {
        return (flags() & MunicipalStreetSignBlockEntity.FLAG_DOUBLE_SIDED) != 0;
    }

    public boolean backlit() {
        return (flags() & MunicipalStreetSignBlockEntity.FLAG_BACKLIT) != 0;
    }

    public String text(int field) {
        if (field < 0 || field >= MunicipalStreetSignBlockEntity.FIELD_COUNT) {
            return "";
        }
        int base = MunicipalStreetSignBlockEntity.DATA_TEXT_BASE
                + field * MunicipalStreetSignBlockEntity.DATA_TEXT_STRIDE;
        int length = Math.max(0, Math.min(
                MunicipalStreetSignBlockEntity.MAX_TEXT_LENGTH,
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

    private int flags() {
        return data.get(MunicipalStreetSignBlockEntity.DATA_FLAGS);
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
            if (sign == null) {
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
            if (builder.length() > MunicipalStreetSignBlockEntity.MAX_TEXT_LENGTH) {
                builder.setLength(MunicipalStreetSignBlockEntity.MAX_TEXT_LENGTH);
            }
            return true;
        }

        if (id >= BUTTON_CLEAR_TEXT_BASE
                && id < BUTTON_CLEAR_TEXT_BASE + MunicipalStreetSignBlockEntity.FIELD_COUNT) {
            pendingText[id - BUTTON_CLEAR_TEXT_BASE].setLength(0);
            return true;
        }

        if (id >= BUTTON_COMMIT_TEXT_BASE
                && id < BUTTON_COMMIT_TEXT_BASE + MunicipalStreetSignBlockEntity.FIELD_COUNT) {
            if (sign == null) {
                return false;
            }
            int field = id - BUTTON_COMMIT_TEXT_BASE;
            sign.setText(field, pendingText[field].toString());
            broadcastChanges();
            return true;
        }

        if (sign == null) {
            return false;
        }

        if (id >= BUTTON_STYLE_SET_BASE
                && id < BUTTON_STYLE_SET_BASE + MunicipalSignStyle.values().length) {
            sign.setStyleOrdinal(id - BUTTON_STYLE_SET_BASE);
            broadcastChanges();
            return true;
        }

        switch (id) {
            case BUTTON_STYLE_PREV -> sign.cycleStyle(-1);
            case BUTTON_STYLE_NEXT -> sign.cycleStyle(1);
            case BUTTON_UPPERCASE -> sign.toggleUppercase();
            case BUTTON_REFLECTIVE -> sign.toggleReflective();
            case BUTTON_DOUBLE_SIDED -> sign.toggleDoubleSided();
            case BUTTON_SHAPE_PREV -> sign.cycleShape(-1);
            case BUTTON_SHAPE_NEXT -> sign.cycleShape(1);
            case BUTTON_FONT_PREV -> sign.cycleFontProfile(-1);
            case BUTTON_FONT_NEXT -> sign.cycleFontProfile(1);
            case BUTTON_TEXT_SIZE_PREV -> sign.cycleTextSize(-1);
            case BUTTON_TEXT_SIZE_NEXT -> sign.cycleTextSize(1);
            case BUTTON_BACKLIT -> sign.toggleBacklit();
            default -> {
                return false;
            }
        }
        broadcastChanges();
        return true;
    }

    private static void appendCharacter(StringBuilder builder, int character) {
        if (character >= 32 && character <= 126
                && builder.length() < MunicipalStreetSignBlockEntity.MAX_TEXT_LENGTH) {
            builder.append((char) character);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.STREET_SIGN.get());
    }
}
