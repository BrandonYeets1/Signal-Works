package com.dgtlbrandxn.signalworks.catalog;

import java.util.Locale;

/** Physical plate families supported by the catalog renderer. */
public enum RoadSignShape {
    SQUARE("Squares", 1.00F, 1.00F),
    CIRCLE("Circles", 1.00F, 1.00F),
    DIAMOND("Diamonds", 1.06F, 1.06F),
    RECTANGLE("Rectangles", 1.28F, 0.82F),
    TRIANGLE("Triangles", 1.08F, 0.98F),
    OTHER("Other", 1.12F, 0.92F);

    private final String defaultCategory;
    private final float width;
    private final float height;

    RoadSignShape(String defaultCategory, float width, float height) {
        this.defaultCategory = defaultCategory;
        this.width = width;
        this.height = height;
    }

    public String defaultCategory() {
        return defaultCategory;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public static RoadSignShape byName(String value) {
        if (value == null) {
            return OTHER;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "square", "squares" -> SQUARE;
            case "circle", "circles", "round" -> CIRCLE;
            case "diamond", "diamonds" -> DIAMOND;
            case "rectangle", "rectangles", "rect" -> RECTANGLE;
            case "triangle", "triangles", "pennant" -> TRIANGLE;
            default -> OTHER;
        };
    }
}
