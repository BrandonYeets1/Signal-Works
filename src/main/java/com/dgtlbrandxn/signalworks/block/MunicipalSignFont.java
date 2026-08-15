package com.dgtlbrandxn.signalworks.block;

/** Built-in sign lettering profiles rendered without external font assets. */
public enum MunicipalSignFont {
    STANDARD("STANDARD", 1.0F, false),
    CONDENSED("CONDENSED", 0.82F, false),
    BOLD("BOLD", 1.0F, true);

    private final String displayName;
    private final float widthScale;
    private final boolean bold;

    MunicipalSignFont(String displayName, float widthScale, boolean bold) {
        this.displayName = displayName;
        this.widthScale = widthScale;
        this.bold = bold;
    }

    public String displayName() {
        return displayName;
    }

    public float widthScale() {
        return widthScale;
    }

    public boolean bold() {
        return bold;
    }

    public MunicipalSignFont step(int amount) {
        MunicipalSignFont[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public static MunicipalSignFont byOrdinal(int ordinal) {
        MunicipalSignFont[] values = values();
        return values[Math.max(0, Math.min(values.length - 1, ordinal))];
    }
}
