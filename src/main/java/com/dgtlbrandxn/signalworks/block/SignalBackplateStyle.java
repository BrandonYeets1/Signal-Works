package com.dgtlbrandxn.signalworks.block;

/** Optional reflective panel rendered behind a signal head. */
public enum SignalBackplateStyle {
    NONE,
    BLACK,
    YELLOW;

    public SignalBackplateStyle step(int amount) {
        SignalBackplateStyle[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public String serializedName() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
