package com.dgtlbrandxn.signalworks.block;

import net.minecraft.util.StringRepresentable;

/** Movement assignment owned by an explicitly linked traffic controller. */
public enum SignalMovement implements StringRepresentable {
    THROUGH("through"),
    LEFT("left"),
    RIGHT("right"),
    THROUGH_LEFT("through_left"),
    PEDESTRIAN("pedestrian"),
    THROUGH_RIGHT("through_right"),
    U_TURN("u_turn"),
    BUS("bus");

    private final String serializedName;

    SignalMovement(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public SignalMovement next() {
        SignalMovement[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public boolean includesThrough() {
        return this == THROUGH || this == THROUGH_LEFT || this == THROUGH_RIGHT;
    }

    public boolean includesLeft() {
        return this == LEFT || this == THROUGH_LEFT || this == U_TURN;
    }

    public boolean includesRight() {
        return this == RIGHT || this == THROUGH_RIGHT;
    }

    public boolean isTurnOnly() {
        return this == LEFT || this == RIGHT || this == U_TURN;
    }

    public static SignalMovement byName(String name) {
        for (SignalMovement movement : values()) {
            if (movement.serializedName.equalsIgnoreCase(name)) {
                return movement;
            }
        }
        return THROUGH;
    }
}
