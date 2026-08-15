# Signal Works Core a3.5.7 — New Signal Families

Base: user-provided `src(5).zip`.

## Added

- Dedicated Straight-Arrow Signal
  - three-section red/yellow/green straight-arrow indication
  - fixed `THROUGH` controller movement
- Dedicated U-Turn Signal
  - three-section red/yellow/green U-turn arrows
  - controller movement `U_TURN`
  - intentionally participates in the protected-left/turn phase and left-turn priority
- Bus Signal
  - three-section transit-style white indications:
    - horizontal bar = stop
    - triangle = caution / prepare to stop
    - vertical bar = go
  - independent `BUS` movement and queue-jump stage
- Traffic Sensor (Bus)
  - same 3x3 detector footprint and no-collision behavior as the existing road sensors
  - only calls the BUS movement
  - detects bus-like vehicle entities when either:
    - the entity has tag `signalworks_bus`
    - the entity has tag `bus`
    - its registered entity type path contains `bus`

## Controller integration

- Bus detector calls are latched like the existing vehicle detector calls.
- Bus demand is scheduled ahead of the normal turn/through movement on that axis.
- Bus-only axes are supported; a normal through/turn demand is not required.
- New controller map types: Straight Arrow, U-Turn Arrow, Bus Signal.
- Fixed-purpose signal types cannot be reassigned to an incompatible movement in the controller UI or Engineer Wand.
- U-turns reuse left-turn scheduling/detector service semantics as requested.

## Visual/resource integration

- Added six new lens textures and matching item models.
- Added a BUS-marked detector texture/icon.
- New signal blocks use the existing dynamic traffic-light renderer and housing geometry.
- Existing user models from `src(5)` are preserved byte-for-byte; no existing `models/block/...` file was changed.

## Build version

This archive contains only the `src` tree because `src(5).zip` did not include the Gradle project root.
In the project-level `gradle.properties`, use:

    mod_version=3.5.7-alpha
    mod_artifact_version=a3.5.7

Then build normally with:

    gradlew.bat clean build

## Validation performed here

- 602 JSON files parsed successfully: 0 invalid.
- 27 files added; 14 existing files changed; 0 files removed relative to `src(5)`.
- Existing block-model files from `src(5)` changed: 0.
- Required block/item/creative-tab/block-entity registrations checked for all new signal heads and the bus sensor.
- New bulb texture references checked against files on disk.
- Java sources were parsed by `javac -proc:none`; no Java syntax/exhaustiveness diagnostics were found before expected missing Minecraft/NeoForge dependency errors.
- A full Gradle/NeoForge compile or Minecraft launch was NOT performed because the uploaded archive contains only `src`, not the Gradle wrapper/project dependency setup.
