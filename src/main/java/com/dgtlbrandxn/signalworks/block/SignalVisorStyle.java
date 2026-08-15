package com.dgtlbrandxn.signalworks.block;

/** Extra visor treatment layered over the signal's built-in standard visor. */
public enum SignalVisorStyle {
    STANDARD,
    TUNNEL;

    public SignalVisorStyle step(int amount) {
        SignalVisorStyle[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public String serializedName() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
