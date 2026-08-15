package com.dgtlbrandxn.signalworks.block;

/** Visual legend set used by functional pedestrian signal heads. */
public enum PedestrianSignalStyle {
    US_CA("us_ca"),
    LEGACY("legacy");

    private final String serializedName;

    PedestrianSignalStyle(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }

    public PedestrianSignalStyle step(int amount) {
        PedestrianSignalStyle[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }
}
