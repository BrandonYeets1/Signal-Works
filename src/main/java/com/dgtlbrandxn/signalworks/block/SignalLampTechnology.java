package com.dgtlbrandxn.signalworks.block;

import java.util.Locale;

/** Visual source technology used inside a signal head. */
public enum SignalLampTechnology {
    DEFAULT,
    LED,
    HALOGEN_HPS;

    public SignalLampTechnology step(int amount) {
        SignalLampTechnology[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public String serializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
