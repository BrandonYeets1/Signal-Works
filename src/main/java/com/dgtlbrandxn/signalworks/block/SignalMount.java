package com.dgtlbrandxn.signalworks.block;

import net.minecraft.util.StringRepresentable;

/** Remembers how a signal was attached so side and overhead hardware stay exclusive. */
public enum SignalMount implements StringRepresentable {
    AUTO("auto"),
    SIDE("side"),
    TOP("top");

    private final String serializedName;

    SignalMount(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
