package com.dgtlbrandxn.signalworks.catalog;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/** One selectable sign face and its physical plate metadata. */
public record RoadSignEntry(
        String id,
        String name,
        String category,
        RoadSignShape shape,
        String packName,
        @Nullable ResourceLocation builtInFront,
        @Nullable ResourceLocation builtInBack,
        @Nullable Path customFront,
        @Nullable Path customBack,
        String tooltip,
        String note
) {
    public boolean custom() {
        return customFront != null;
    }

    public boolean hasBackTexture() {
        return builtInBack != null || customBack != null;
    }
}
