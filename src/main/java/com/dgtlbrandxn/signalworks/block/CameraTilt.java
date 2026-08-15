package com.dgtlbrandxn.signalworks.block;

import net.minecraft.util.StringRepresentable;

/** Five practical pan-head elevation presets for traffic cameras. */
public enum CameraTilt implements StringRepresentable {
    DOWN_45("down_45", -45.0D),
    DOWN_22("down_22", -22.5D),
    LEVEL("level", 0.0D),
    UP_22("up_22", 22.5D),
    UP_45("up_45", 45.0D);

    private final String serializedName;
    private final double degrees;

    CameraTilt(String serializedName, double degrees) {
        this.serializedName = serializedName;
        this.degrees = degrees;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public double degrees() {
        return degrees;
    }

    public CameraTilt step(int amount) {
        CameraTilt[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }
}
