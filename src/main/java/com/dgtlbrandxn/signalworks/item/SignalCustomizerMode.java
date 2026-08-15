package com.dgtlbrandxn.signalworks.item;

/** Editing channel selected on the signal customizer tool. */
public enum SignalCustomizerMode {
    BACKPLATE,
    VISOR,
    MOUNT,
    LAMP,
    PEDESTRIAN,
    ARM_SIZE,
    ARM_TYPE,
    ARM_MULTI;

    public SignalCustomizerMode step(int amount) {
        SignalCustomizerMode[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public String serializedName() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
