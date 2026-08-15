package com.dgtlbrandxn.signalworks.block;

import net.minecraft.util.StringRepresentable;

/** Interchangeable mounting hardware used behind a traffic signal head. */
public enum SignalArmType implements StringRepresentable {
    MAST("mast"),
    POLE_SMALL("pole_small"),
    POLE_LARGE("pole_large"),
    HANGING("hanging");

    private final String serializedName;

    SignalArmType(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public SignalArmType step(int amount) {
        SignalArmType[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }
}
