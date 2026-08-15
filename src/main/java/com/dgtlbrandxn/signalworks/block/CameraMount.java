package com.dgtlbrandxn.signalworks.block;

import net.minecraft.util.StringRepresentable;

/** Physical camera bracket selected from the clicked placement face. */
public enum CameraMount implements StringRepresentable {
    WALL("wall"),
    MAST("mast"),
    HANGING("hanging");

    private final String serializedName;

    CameraMount(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
