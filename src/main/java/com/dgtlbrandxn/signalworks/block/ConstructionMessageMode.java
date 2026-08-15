package com.dgtlbrandxn.signalworks.block;

/** Display programs supported by the portable construction message board. */
public enum ConstructionMessageMode {
    MESSAGE("Message"),
    LEFT_ARROW("Left Arrow"),
    RIGHT_ARROW("Right Arrow"),
    MERGE_LEFT("Merge Left"),
    MERGE_RIGHT("Merge Right"),
    CAUTION("Caution");

    private final String displayName;

    ConstructionMessageMode(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public ConstructionMessageMode step(int amount) {
        ConstructionMessageMode[] values = values();
        return values[Math.floorMod(ordinal() + amount, values.length)];
    }

    public static ConstructionMessageMode byOrdinal(int ordinal) {
        ConstructionMessageMode[] values = values();
        return values[Math.floorMod(ordinal, values.length)];
    }
}
