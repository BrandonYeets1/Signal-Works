# Signal Works Core 3.5.1 — Operations Hotfix

## Controller interface

- Repacked the controller application into a 440×274 layout that remains usable at common 1080p GUI scales.
- Removed the full player inventory from the controller application.
- Kept the persistent two-slot field-kit drawer as a compact take-only panel.
- Shortened and repositioned the home status readout so it does not overlap the drawer.
- Shift-clicking is intentionally disabled in this specialized controller menu; tools are removed with a normal click.

## Signal rendering and optics

- Converted `trafficcontrol:traffic_light_1` into a functional one-section signal head with a steady red indication when unlinked.
- Added a visible LED-module grid and a warmer legacy halogen/HPS hotspot so lamp-technology changes are apparent in-world.
- Technology overlays are limited to circular vehicle lenses and do not cover arrows or pedestrian symbols.
- Moved ramp-meter red and green faces in front of the housing lens plane to prevent clipping and incorrect lens rendering.

## Orientation corrections

- Corrected freeway-guide blockstate rotations so the visible green face follows the block's `facing` property.
- Removed the local horizontal mirror from the construction message-board renderer. Text and directional programs now use the board's facing rotation without reversing glyphs.

## Construction catalog

- The traffic drum item now uses its actual block model in inventory instead of the removed flat icon texture.
- Added `trafficcontrol:road_flare`, a low-profile placeable flare that starts lit, can be extinguished or relit, emits level-7 light, and uses a redstone-torch visual source with red dust/smoke particles.

## Compatibility

- Existing registry IDs and the internal `trafficcontrol` namespace remain unchanged.
- The obsolete drum icon file is deleted by the patch applicator.
- No unrelated texture assets are changed.

## Applying the source patch

1. Extract the patch archive anywhere.
2. Open Command Prompt in the project directory containing `build.gradle`.
3. Run the patch applicator by full path, or copy the patch folder into the project and run:

```bat
Apply-SignalWorks-3.5.1.bat
```

4. Build with Java 21:

```bat
gradlew.bat clean build
```

The applicator is rerunnable and skips a copy when a source file and destination resolve to the same path.
