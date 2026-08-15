# Signal Works Core 3.4.11 — USDOT Sign Catalog

## New work

- The legacy `trafficcontrol:sign` registry is now the compatibility-safe Empty Road Sign.
- Placing an Empty Road Sign immediately opens the searchable catalog.
- Right-clicking the placed sign reopens the catalog.
- 907 existing premade sign faces are indexed from the preserved Signal Works sign pack.
- Categories: Squares, Circles, Diamonds, Rectangles, Other, plus manifest-defined custom categories.
- The catalog includes search, paging, live thumbnails, a large preview, selected-state highlighting,
  custom-folder access, and custom-pack reload.
- Signs use reusable galvanized posts, clamps, shape-aware backing plates and textured front/back faces.
- World NBT stores only a stable catalog ID; image bytes are never copied into the save.
- Custom packs load from `config/signalworks/signs/custom/<pack>/manifest.json`.
- PNG artwork is rendered at native quality up to 1024 pixels on its longest edge. Larger custom images
  are capped client-side to 1024 while the original file remains untouched.
- E, T and / remain safe while the catalog search field is active.

## Compatibility

The existing `trafficcontrol:sign` block/item registry IDs are retained. No municipal street-sign,
construction, signal, light, rail, cone, channelizer or barrier IDs were changed.

## Assets

No sign textures were replaced. The existing 907-face catalog is reused as the built-in demonstration
library; Brandon's 1024 USDOT pack can be added later as a custom or bundled catalog without changing code.
