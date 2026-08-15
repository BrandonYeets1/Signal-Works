package com.dgtlbrandxn.signalworks.client.render;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.catalog.RoadSignEntry;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Loads custom PNG sign faces and caps oversized artwork to a 1024px master edge. */
public final class RoadSignTextureManager {
    private static final int MAX_EDGE = 1024;
    private static final Map<Path, ResourceLocation> CACHE = new HashMap<>();

    private RoadSignTextureManager() {
    }

    public static ResourceLocation front(RoadSignEntry entry) {
        return texture(entry.builtInFront(), entry.customFront(), entry.id() + "_front");
    }

    public static ResourceLocation back(RoadSignEntry entry) {
        return texture(entry.builtInBack(), entry.customBack(), entry.id() + "_back");
    }

    public static synchronized void clearCustomCache() {
        TextureManager textures = Minecraft.getInstance().getTextureManager();
        CACHE.values().forEach(textures::release);
        CACHE.clear();
    }

    private static ResourceLocation texture(
            @Nullable ResourceLocation builtIn,
            @Nullable Path custom,
            String key
    ) {
        if (custom == null) {
            return builtIn != null ? builtIn : TextureManager.INTENTIONAL_MISSING_TEXTURE;
        }
        Path normalized = custom.toAbsolutePath().normalize();
        ResourceLocation cached = CACHE.get(normalized);
        if (cached != null) {
            return cached;
        }
        ResourceLocation loaded = load(normalized, key);
        CACHE.put(normalized, loaded);
        return loaded;
    }

    private static ResourceLocation load(Path path, String key) {
        try (InputStream stream = Files.newInputStream(path)) {
            NativeImage source = NativeImage.read(stream);
            NativeImage image = cap(source);
            DynamicTexture texture = new DynamicTexture(image);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    TrafficControl.MOD_ID,
                    "dynamic/road_sign/" + Integer.toUnsignedString(key.hashCode(), 36)
            );
            Minecraft.getInstance().getTextureManager().register(id, texture);
            return id;
        } catch (IOException | RuntimeException exception) {
            System.err.println("[Signal Works] Could not load custom sign texture " + path + ": "
                    + exception.getMessage());
            return TextureManager.INTENTIONAL_MISSING_TEXTURE;
        }
    }

    private static NativeImage cap(NativeImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int largest = Math.max(width, height);
        if (largest <= MAX_EDGE) {
            return source;
        }
        float scale = MAX_EDGE / (float) largest;
        int targetWidth = Math.max(1, Math.round(width * scale));
        int targetHeight = Math.max(1, Math.round(height * scale));
        NativeImage resized = new NativeImage(source.format(), targetWidth, targetHeight, true);
        source.resizeSubRectTo(0, 0, width, height, resized);
        source.close();
        return resized;
    }
}
