package com.dgtlbrandxn.signalworks.catalog;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Shared deterministic road-sign catalog. Built-in entries come from the original sign pack;
 * user packs are loaded from config/signalworks/signs/custom.
 */
public final class RoadSignCatalog {
    private static final Gson GSON = new Gson();
    private static final String BUILTIN_PATH = "/assets/trafficcontrol/misc/signs.json";
    private static final Path CUSTOM_ROOT = Path.of(System.getProperty("user.dir"),
            "config", "signalworks", "signs", "custom");
    private static volatile List<RoadSignEntry> entries;
    private static volatile Map<String, Integer> indices;
    private static volatile List<String> categories;

    private RoadSignCatalog() {
    }

    public static List<RoadSignEntry> entries() {
        ensureLoaded();
        return entries;
    }

    public static List<String> categories() {
        ensureLoaded();
        return categories;
    }

    public static RoadSignEntry entry(int index) {
        List<RoadSignEntry> values = entries();
        if (values.isEmpty()) {
            throw new IllegalStateException("Signal Works road-sign catalog is empty");
        }
        return values.get(Math.max(0, Math.min(values.size() - 1, index)));
    }

    public static RoadSignEntry entry(String id) {
        int index = indexOf(id);
        return entry(index < 0 ? 0 : index);
    }

    public static int indexOf(String id) {
        ensureLoaded();
        if (id == null) {
            return 0;
        }
        return indices.getOrDefault(id, 0);
    }

    public static Path customRoot() {
        ensureFolders();
        return CUSTOM_ROOT;
    }

    public static synchronized void reload() {
        entries = null;
        indices = null;
        categories = null;
        ensureLoaded();
    }

    private static void ensureLoaded() {
        if (entries != null) {
            return;
        }
        synchronized (RoadSignCatalog.class) {
            if (entries != null) {
                return;
            }
            ensureFolders();
            List<RoadSignEntry> loaded = new ArrayList<>();
            loadBuiltIn(loaded);
            loadCustom(loaded);
            if (loaded.isEmpty()) {
                loaded.add(fallback());
            }

            Map<String, Integer> builtIndices = new LinkedHashMap<>();
            Set<String> builtCategories = new LinkedHashSet<>();
            for (int index = 0; index < loaded.size(); index++) {
                RoadSignEntry entry = loaded.get(index);
                builtIndices.putIfAbsent(entry.id(), index);
                builtCategories.add(entry.category());
            }
            entries = Collections.unmodifiableList(loaded);
            indices = Collections.unmodifiableMap(builtIndices);
            categories = Collections.unmodifiableList(new ArrayList<>(builtCategories));
        }
    }

    private static void loadBuiltIn(List<RoadSignEntry> target) {
        try (InputStream stream = RoadSignCatalog.class.getResourceAsStream(BUILTIN_PATH)) {
            if (stream == null) {
                return;
            }
            JsonObject root = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
            String packId = string(root, "pack_id", "builtin");
            String packName = string(root, "name", "Signal Works Signs");
            Map<String, String> typeNames = readTypes(root.getAsJsonObject("types"));
            JsonArray signs = root.getAsJsonArray("signs");
            if (signs == null) {
                return;
            }
            for (JsonElement element : signs) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject sign = element.getAsJsonObject();
                String type = string(sign, "type", "misc").toLowerCase(Locale.ROOT);
                String front = string(sign, "front", "");
                if (front.isBlank()) {
                    continue;
                }
                String id = "builtin:" + string(sign, "id", type + ":" + front);
                RoadSignShape shape = RoadSignShape.byName(type);
                String category = switch (shape) {
                    case TRIANGLE, OTHER -> "Other";
                    default -> shape.defaultCategory();
                };
                ResourceLocation frontTexture = texture(packId, type, front);
                String backName = string(sign, "back", "back.png");
                ResourceLocation backTexture = texture(packId, type, backName);
                target.add(new RoadSignEntry(
                        id,
                        string(sign, "name", front),
                        category,
                        shape,
                        packName,
                        frontTexture,
                        backTexture,
                        null,
                        null,
                        string(sign, "tooltip", ""),
                        string(sign, "note", "")
                ));
            }
        } catch (Exception exception) {
            System.err.println("[Signal Works] Failed to load built-in road signs: " + exception.getMessage());
        }
    }

    private static void loadCustom(List<RoadSignEntry> target) {
        if (!Files.isDirectory(CUSTOM_ROOT)) {
            return;
        }
        List<Path> manifests = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(CUSTOM_ROOT, 4)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equalsIgnoreCase("manifest.json"))
                    .sorted(Comparator.comparing(path -> path.toAbsolutePath().normalize().toString()))
                    .forEach(manifests::add);
        } catch (IOException exception) {
            return;
        }
        for (Path manifest : manifests) {
            loadCustomManifest(target, manifest);
        }
    }

    private static void loadCustomManifest(List<RoadSignEntry> target, Path manifest) {
        try (InputStream stream = Files.newInputStream(manifest)) {
            JsonObject root = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
            String packId = safeId(string(root, "pack_id", manifest.getParent().getFileName().toString()));
            String packName = string(root, "name", packId);
            Map<String, String> typeNames = readTypes(root.getAsJsonObject("types"));
            JsonArray signs = root.getAsJsonArray("signs");
            if (signs == null) {
                return;
            }
            int serial = 0;
            for (JsonElement element : signs) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject sign = element.getAsJsonObject();
                String type = string(sign, "type", "custom");
                RoadSignShape shape = RoadSignShape.byName(string(sign, "shape", type));
                String category = string(sign, "category",
                        typeNames.getOrDefault(type, shape.defaultCategory()));
                Path front = resolveInside(manifest.getParent(), string(sign, "front", ""));
                if (front == null || !Files.isRegularFile(front)) {
                    serial++;
                    continue;
                }
                Path back = resolveInside(manifest.getParent(), string(sign, "back", ""));
                String rawId = string(sign, "id", Integer.toString(serial));
                target.add(new RoadSignEntry(
                        "custom:" + packId + ":" + safeId(rawId),
                        string(sign, "name", front.getFileName().toString()),
                        category,
                        shape,
                        packName,
                        null,
                        null,
                        front,
                        back != null && Files.isRegularFile(back) ? back : null,
                        string(sign, "tooltip", "Custom sign from " + packName),
                        string(sign, "note", "")
                ));
                serial++;
            }
        } catch (Exception exception) {
            System.err.println("[Signal Works] Failed custom sign manifest " + manifest + ": " + exception.getMessage());
        }
    }

    private static ResourceLocation texture(String packId, String type, String fileName) {
        String clean = fileName.replace('\\', '/');
        return ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID,
                "textures/block/signs/" + packId + "/" + type + "/" + clean);
    }

    private static Map<String, String> readTypes(JsonObject types) {
        Map<String, String> result = new LinkedHashMap<>();
        if (types == null) {
            return result;
        }
        for (Map.Entry<String, JsonElement> entry : types.entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                result.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        return result;
    }

    private static String string(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || !object.get(key).isJsonPrimitive()) {
            return fallback;
        }
        try {
            return object.get(key).getAsString();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static String safeId(String value) {
        String clean = value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]+", "_");
        return clean.isBlank() ? "entry" : clean;
    }

    private static Path resolveInside(Path root, String child) {
        if (child == null || child.isBlank()) {
            return null;
        }
        Path normalizedRoot = root.toAbsolutePath().normalize();
        Path resolved = normalizedRoot.resolve(child).normalize();
        return resolved.startsWith(normalizedRoot) ? resolved : null;
    }

    private static RoadSignEntry fallback() {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                TrafficControl.MOD_ID, "textures/block/signs/signbase.png");
        return new RoadSignEntry("builtin:fallback", "Blank Road Sign", "Other",
                RoadSignShape.OTHER, "Signal Works", texture, null,
                null, null, "No sign catalog was found.", "");
    }

    private static void ensureFolders() {
        try {
            Files.createDirectories(CUSTOM_ROOT);
            Path readme = CUSTOM_ROOT.resolve("README.txt");
            if (!Files.exists(readme)) {
                Files.writeString(readme, customPackReadme(), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW);
            }
        } catch (IOException ignored) {
        }
    }

    private static String customPackReadme() {
        return """
                SIGNAL WORKS CUSTOM ROAD SIGN PACKS

                Create one folder per pack, for example:
                  config/signalworks/signs/custom/my_pack/

                Put 1024px PNG files in that folder and add manifest.json:

                {
                  \"name\": \"My Sign Pack\",
                  \"pack_id\": \"my_signs\",
                  \"types\": { \"california\": \"California Freeway\" },
                  \"signs\": [
                    {
                      \"id\": \"sample\",
                      \"name\": \"Sample Sign\",
                      \"type\": \"california\",
                      \"category\": \"California Freeway\",
                      \"shape\": \"rectangle\",
                      \"front\": \"sample.png\",
                      \"back\": \"sample_back.png\"
                    }
                  ]
                }

                Supported physical shapes: square, circle, diamond, rectangle, triangle, other.
                1024px PNG is the official master quality. Larger custom images are capped to 1024
                by the client texture loader. Multiplayer clients and the server must install the
                same custom pack folders so catalog IDs and artwork match.
                """;
    }
}
