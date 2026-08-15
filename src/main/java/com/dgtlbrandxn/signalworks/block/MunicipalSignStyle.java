package com.dgtlbrandxn.signalworks.block;

/** Built-in municipal street-sign palettes. */
public enum MunicipalSignStyle {
    LA_BLUE("LA BLUE", 0x0B3A68, 0xF4F7FA, 0xF4F7FA),
    CLASSIC_GREEN("CLASSIC GREEN", 0x185C38, 0xF5F8F3, 0xF5F8F3),
    HISTORIC_BROWN("HISTORIC BROWN", 0x5A3522, 0xFFF1C7, 0xFFF1C7),
    BLACK_WHITE("BLACK / WHITE", 0x151719, 0xF7F7F7, 0xF7F7F7);

    private final String displayName;
    private final int backgroundColor;
    private final int textColor;
    private final int borderColor;

    MunicipalSignStyle(String displayName, int backgroundColor, int textColor, int borderColor) {
        this.displayName = displayName;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.borderColor = borderColor;
    }

    public String displayName() {
        return displayName;
    }

    public int backgroundColor() {
        return backgroundColor;
    }

    public int textColor() {
        return textColor;
    }

    public int borderColor() {
        return borderColor;
    }

    public MunicipalSignStyle step(int amount) {
        MunicipalSignStyle[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public static MunicipalSignStyle byOrdinal(int ordinal) {
        MunicipalSignStyle[] values = values();
        return values[Math.max(0, Math.min(values.length - 1, ordinal))];
    }
}
