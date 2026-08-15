package com.dgtlbrandxn.signalworks.block;

import net.minecraft.util.StringRepresentable;

/** Physical rear-bracket height matched to a 3, 4 or 5 section signal head. */
public enum SignalArmSize implements StringRepresentable {
    THREE("three", 3),
    FOUR("four", 4),
    FIVE("five", 5);

    private final String serializedName;
    private final int bulbs;

    SignalArmSize(String serializedName, int bulbs) {
        this.serializedName = serializedName;
        this.bulbs = bulbs;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public int bulbs() {
        return bulbs;
    }

    public SignalArmSize step(int amount) {
        SignalArmSize[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }
}
