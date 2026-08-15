# Milestone 2.9.4 — Brighter Signals and Fail-Safe Modes

Apply this incremental patch over a project that already contains Milestone 2.9.3.

## Brighter signal bulbs

Active indications now render with three emissive layers:

1. the original full-bright bulb texture;
2. a second emissive copy of the bulb texture, preserving arrow and pedestrian shapes;
3. stronger close and soft outer colored glow layers.

No new or replacement texture PNGs are included.

## Correct nighttime flashing amber

The controller's **Night flashing yellow** option no longer makes every amber indication flash continuously at the same time.

At night, each axis keeps its normal turn-taking sequence:

- the movement's normal green interval becomes **flashing amber**;
- the normal yellow interval becomes **steady amber**;
- the movement enters all-red clearance;
- the other axis begins.

Five-section, doghouse, and dedicated turn heads use their amber arrows during protected-turn stages. Pedestrian heads remain on a steady DON'T WALK indication during this reduced nighttime operation.

## Broken-down fail-safe

The controller GUI now includes **Broken-down fail-safe**.

When enabled:

- all vehicle signal heads show synchronized flashing red;
- dedicated turn heads flash their red arrow;
- pedestrian signals are completely dark;
- normal timing and night-flash behavior are overridden until fail-safe is disabled.

The setting is saved in the controller block entity and synchronized through the existing menu data.

## Build

Run:

```bat
gradlew.bat clean build
```

Expected JAR:

```text
build\libs\trafficcontrol-2.0.0-alpha.2.9.4.jar
```
