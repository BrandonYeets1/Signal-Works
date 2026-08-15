package com.dgtlbrandxn.signalworks.blockentity;

import com.dgtlbrandxn.signalworks.block.MunicipalSignFont;
import com.dgtlbrandxn.signalworks.block.MunicipalSignShape;
import com.dgtlbrandxn.signalworks.block.MunicipalSignStyle;
import com.dgtlbrandxn.signalworks.block.MunicipalTextSize;
import com.dgtlbrandxn.signalworks.menu.MunicipalStreetSignMenu;
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

/** Persistent editable text and appearance for one municipal street-sign assembly. */
public final class MunicipalStreetSignBlockEntity extends BlockEntity implements MenuProvider {
    public static final int FIELD_PRIMARY = 0;
    public static final int FIELD_CROSS = 1;
    public static final int FIELD_DISTRICT = 2;
    public static final int FIELD_BLOCK = 3;
    public static final int FIELD_COUNT = 4;
    public static final int MAX_TEXT_LENGTH = 24;

    public static final int DATA_STYLE = 0;
    public static final int DATA_FLAGS = 1;
    public static final int DATA_TEXT_BASE = 2;
    public static final int DATA_TEXT_STRIDE = MAX_TEXT_LENGTH + 1;
    public static final int DATA_TEXT_END = DATA_TEXT_BASE + FIELD_COUNT * DATA_TEXT_STRIDE;
    public static final int DATA_SHAPE = DATA_TEXT_END;
    public static final int DATA_FONT = DATA_SHAPE + 1;
    public static final int DATA_TEXT_SIZE = DATA_FONT + 1;
    /** Last data slot; sent after every text character so the client knows initialization is complete. */
    public static final int DATA_TEXT_READY = DATA_TEXT_SIZE + 1;
    public static final int DATA_COUNT = DATA_TEXT_READY + 1;

    public static final int FLAG_UPPERCASE = 1;
    public static final int FLAG_REFLECTIVE = 2;
    public static final int FLAG_DOUBLE_SIDED = 4;
    public static final int FLAG_BACKLIT = 8;

    private static final String TAG_PRIMARY = "PrimaryStreet";
    private static final String TAG_CROSS = "CrossStreet";
    private static final String TAG_DISTRICT = "District";
    private static final String TAG_BLOCK = "BlockNumber";
    private static final String TAG_STYLE = "Style";
    private static final String TAG_UPPERCASE = "Uppercase";
    private static final String TAG_REFLECTIVE = "Reflective";
    private static final String TAG_DOUBLE_SIDED = "DoubleSided";
    private static final String TAG_BACKLIT = "Backlit";
    private static final String TAG_SHAPE = "BladeShape";
    private static final String TAG_FONT = "FontProfile";
    private static final String TAG_TEXT_SIZE = "TextSize";

    private String primaryStreet = "MAIN ST";
    private String crossStreet = "1ST ST";
    private String district = "LOS ANGELES";
    private String blockNumber = "100";
    private MunicipalSignStyle style = MunicipalSignStyle.LA_BLUE;
    private MunicipalSignShape shape = MunicipalSignShape.RECTANGLE;
    private MunicipalSignFont fontProfile = MunicipalSignFont.STANDARD;
    private MunicipalTextSize textSize = MunicipalTextSize.MEDIUM;
    private boolean uppercase = true;
    private boolean reflective = true;
    private boolean doubleSided = true;
    private boolean backlit = false;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == DATA_STYLE) {
                return style.ordinal();
            }
            if (index == DATA_FLAGS) {
                return flags();
            }
            if (index >= DATA_TEXT_BASE && index < DATA_TEXT_END) {
                int encoded = index - DATA_TEXT_BASE;
                int field = encoded / DATA_TEXT_STRIDE;
                int within = encoded % DATA_TEXT_STRIDE;
                String value = text(field);
                if (within == 0) {
                    return value.length();
                }
                int charIndex = within - 1;
                return charIndex < value.length() ? value.charAt(charIndex) : 0;
            }
            if (index == DATA_SHAPE) {
                return shape.ordinal();
            }
            if (index == DATA_FONT) {
                return fontProfile.ordinal();
            }
            if (index == DATA_TEXT_SIZE) {
                return textSize.ordinal();
            }
            if (index == DATA_TEXT_READY) {
                return 1;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == DATA_STYLE) {
                setStyle(MunicipalSignStyle.byOrdinal(value));
            } else if (index == DATA_FLAGS) {
                setFlags(value);
            } else if (index == DATA_SHAPE) {
                setShape(MunicipalSignShape.byOrdinal(value));
            } else if (index == DATA_FONT) {
                setFontProfile(MunicipalSignFont.byOrdinal(value));
            } else if (index == DATA_TEXT_SIZE) {
                setTextSize(MunicipalTextSize.byOrdinal(value));
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public MunicipalStreetSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUNICIPAL_STREET_SIGN.get(), pos, state);
    }

    public ContainerData dataAccess() {
        return dataAccess;
    }

    public String primaryStreet() {
        return displayText(primaryStreet);
    }

    public String crossStreet() {
        return displayText(crossStreet);
    }

    public String district() {
        return displayText(district);
    }

    public String blockNumber() {
        return displayText(blockNumber);
    }

    public String rawText(int field) {
        return text(field);
    }

    public MunicipalSignStyle style() {
        return style;
    }

    public MunicipalSignShape shape() {
        return shape;
    }

    public MunicipalSignFont fontProfile() {
        return fontProfile;
    }

    public MunicipalTextSize textSize() {
        return textSize;
    }

    public boolean uppercase() {
        return uppercase;
    }

    public boolean reflective() {
        return reflective;
    }

    public boolean doubleSided() {
        return doubleSided;
    }

    public boolean backlit() {
        return backlit;
    }

    public void setText(int field, String value) {
        String sanitized = sanitize(value);
        switch (field) {
            case FIELD_PRIMARY -> primaryStreet = sanitized;
            case FIELD_CROSS -> crossStreet = sanitized;
            case FIELD_DISTRICT -> district = sanitized;
            case FIELD_BLOCK -> blockNumber = sanitized;
            default -> {
                return;
            }
        }
        sync();
    }

    public void cycleStyle(int amount) {
        style = style.step(amount);
        sync();
    }

    public void setStyleOrdinal(int ordinal) {
        style = MunicipalSignStyle.byOrdinal(ordinal);
        sync();
    }

    public void cycleShape(int amount) {
        shape = shape.step(amount);
        sync();
    }

    public void cycleFontProfile(int amount) {
        fontProfile = fontProfile.step(amount);
        sync();
    }

    public void cycleTextSize(int amount) {
        textSize = textSize.step(amount);
        sync();
    }

    public void toggleUppercase() {
        uppercase = !uppercase;
        sync();
    }

    public void toggleReflective() {
        reflective = !reflective;
        sync();
    }

    public void toggleDoubleSided() {
        doubleSided = !doubleSided;
        sync();
    }

    public void toggleBacklit() {
        backlit = !backlit;
        sync();
    }

    private String displayText(String text) {
        return uppercase ? text.toUpperCase(java.util.Locale.ROOT) : text;
    }

    private String text(int field) {
        return switch (field) {
            case FIELD_PRIMARY -> primaryStreet;
            case FIELD_CROSS -> crossStreet;
            case FIELD_DISTRICT -> district;
            case FIELD_BLOCK -> blockNumber;
            default -> "";
        };
    }

    private int flags() {
        int flags = 0;
        if (uppercase) {
            flags |= FLAG_UPPERCASE;
        }
        if (reflective) {
            flags |= FLAG_REFLECTIVE;
        }
        if (doubleSided) {
            flags |= FLAG_DOUBLE_SIDED;
        }
        if (backlit) {
            flags |= FLAG_BACKLIT;
        }
        return flags;
    }

    private void setFlags(int flags) {
        uppercase = (flags & FLAG_UPPERCASE) != 0;
        reflective = (flags & FLAG_REFLECTIVE) != 0;
        doubleSided = (flags & FLAG_DOUBLE_SIDED) != 0;
        backlit = (flags & FLAG_BACKLIT) != 0;
        sync();
    }

    private void setStyle(MunicipalSignStyle style) {
        this.style = style;
        sync();
    }

    private void setShape(MunicipalSignShape shape) {
        this.shape = shape;
        sync();
    }

    private void setFontProfile(MunicipalSignFont fontProfile) {
        this.fontProfile = fontProfile;
        sync();
    }

    private void setTextSize(MunicipalTextSize textSize) {
        this.textSize = textSize;
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
        return Component.translatable("menu.trafficcontrol.municipal_sign");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MunicipalStreetSignMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString(TAG_PRIMARY, primaryStreet);
        tag.putString(TAG_CROSS, crossStreet);
        tag.putString(TAG_DISTRICT, district);
        tag.putString(TAG_BLOCK, blockNumber);
        tag.putInt(TAG_STYLE, style.ordinal());
        tag.putBoolean(TAG_UPPERCASE, uppercase);
        tag.putBoolean(TAG_REFLECTIVE, reflective);
        tag.putBoolean(TAG_DOUBLE_SIDED, doubleSided);
        tag.putBoolean(TAG_BACKLIT, backlit);
        tag.putInt(TAG_SHAPE, shape.ordinal());
        tag.putInt(TAG_FONT, fontProfile.ordinal());
        tag.putInt(TAG_TEXT_SIZE, textSize.ordinal());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        primaryStreet = sanitize(tag.contains(TAG_PRIMARY) ? tag.getString(TAG_PRIMARY) : primaryStreet);
        crossStreet = sanitize(tag.contains(TAG_CROSS) ? tag.getString(TAG_CROSS) : crossStreet);
        district = sanitize(tag.contains(TAG_DISTRICT) ? tag.getString(TAG_DISTRICT) : district);
        blockNumber = sanitize(tag.contains(TAG_BLOCK) ? tag.getString(TAG_BLOCK) : blockNumber);
        style = MunicipalSignStyle.byOrdinal(tag.getInt(TAG_STYLE));
        uppercase = !tag.contains(TAG_UPPERCASE) || tag.getBoolean(TAG_UPPERCASE);
        reflective = !tag.contains(TAG_REFLECTIVE) || tag.getBoolean(TAG_REFLECTIVE);
        doubleSided = !tag.contains(TAG_DOUBLE_SIDED) || tag.getBoolean(TAG_DOUBLE_SIDED);
        backlit = tag.contains(TAG_BACKLIT) && tag.getBoolean(TAG_BACKLIT);
        shape = tag.contains(TAG_SHAPE)
                ? MunicipalSignShape.byOrdinal(tag.getInt(TAG_SHAPE))
                : MunicipalSignShape.RECTANGLE;
        fontProfile = tag.contains(TAG_FONT)
                ? MunicipalSignFont.byOrdinal(tag.getInt(TAG_FONT))
                : MunicipalSignFont.STANDARD;
        textSize = tag.contains(TAG_TEXT_SIZE)
                ? MunicipalTextSize.byOrdinal(tag.getInt(TAG_TEXT_SIZE))
                : MunicipalTextSize.MEDIUM;
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
