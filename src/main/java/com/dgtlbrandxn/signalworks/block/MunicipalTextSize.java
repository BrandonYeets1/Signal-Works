package com.dgtlbrandxn.signalworks.block;

/** User-facing text size presets for municipal signs. */
public enum MunicipalTextSize {
    SMALL("SMALL", 0.86F),
    MEDIUM("MEDIUM", 1.0F),
    LARGE("LARGE", 1.14F);

    private final String displayName;
    private final float scale;

    MunicipalTextSize(String displayName, float scale) {
        this.displayName = displayName;
        this.scale = scale;
    }

    public String displayName() {
        return displayName;
    }

    public float scale() {
        return scale;
    }

    public MunicipalTextSize step(int amount) {
        MunicipalTextSize[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public static MunicipalTextSize byOrdinal(int ordinal) {
        MunicipalTextSize[] values = values();
        return values[Math.max(0, Math.min(values.length - 1, ordinal))];
    }
}
