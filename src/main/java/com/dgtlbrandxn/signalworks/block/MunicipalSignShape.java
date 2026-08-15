package com.dgtlbrandxn.signalworks.block;

/** Procedural street-sign blade silhouettes. */
public enum MunicipalSignShape {
    RECTANGLE("RECTANGLE"),
    ROUNDED("ROUNDED"),
    CLIPPED("CLIPPED CORNERS");

    private final String displayName;

    MunicipalSignShape(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public MunicipalSignShape step(int amount) {
        MunicipalSignShape[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public static MunicipalSignShape byOrdinal(int ordinal) {
        MunicipalSignShape[] values = values();
        return values[Math.max(0, Math.min(values.length - 1, ordinal))];
    }
}
