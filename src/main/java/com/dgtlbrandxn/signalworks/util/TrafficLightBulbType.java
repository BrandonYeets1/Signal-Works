package com.dgtlbrandxn.signalworks.util;

import com.dgtlbrandxn.signalworks.TrafficControl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Stable bulb IDs copied from the original Traffic Control 1.1.1 enum.
 * The numeric IDs are intentionally preserved for world-data compatibility.
 */
public enum TrafficLightBulbType {
    RED(0, "red"),
    YELLOW(1, "yellow_solid"),
    GREEN(2, "green"),
    RED_ARROW_LEFT(3, "red_arrow_left"),
    YELLOW_ARROW_LEFT(4, "yellow_arrow_left"),
    GREEN_ARROW_LEFT(5, "green_arrow_left"),
    CROSS(6, "cross"),
    DONT_CROSS(7, "dontcross"),
    RED_ARROW_RIGHT(8, "red_arrow_right"),
    YELLOW_ARROW_RIGHT(9, "yellow_arrow_right"),
    GREEN_ARROW_RIGHT(10, "green_arrow_right"),
    NO_RIGHT_TURN(11, "no_right_turn"),
    NO_LEFT_TURN(12, "no_left_turn"),
    STRAIGHT_RED(13, "straight_red"),
    STRAIGHT_YELLOW(14, "straight_yellow"),
    STRAIGHT_GREEN(15, "straight_green"),
    RED_ARROW_U_TURN(16, "red_arrow_u_turn"),
    YELLOW_ARROW_U_TURN(17, "yellow_arrow_u_turn"),
    GREEN_ARROW_U_TURN(18, "green_arrow_u_turn"),
    BUS_STOP(19, "bus_stop"),
    BUS_CAUTION(20, "bus_caution"),
    BUS_GO(21, "bus_go");

    private final int legacyId;
    private final ResourceLocation texture;

    TrafficLightBulbType(int legacyId, String textureName) {
        this.legacyId = legacyId;
        this.texture = ResourceLocation.fromNamespaceAndPath(
                TrafficControl.MOD_ID,
                "textures/block/" + textureName + ".png"
        );
    }

    public int legacyId() {
        return legacyId;
    }

    public ResourceLocation texture() {
        return texture;
    }

    @Nullable
    public static TrafficLightBulbType byLegacyId(int id) {
        for (TrafficLightBulbType value : values()) {
            if (value.legacyId == id) {
                return value;
            }
        }
        return null;
    }
}
