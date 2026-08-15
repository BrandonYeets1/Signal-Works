# Milestone 2.7 testing checklist

## Build

```bat
gradlew.bat clean build
```

Expected JAR:

```text
build\libs\trafficcontrol-2.0.0-alpha.2.7.1.jar
```

Remove older Traffic Control alpha JARs before launching.

## Flush traffic-head mount

1. Build a vertical modular pole.
2. Attach a mast arm at the top.
3. Place a standard traffic head one block below the mast segment.
4. Confirm the rear of the head meets the mounting plate rather than floating around the mast centerline.
5. Repeat at 45 degrees.
6. Test a doghouse head and a five-section head; the five-section mast remains two blocks above its anchor.

## Modular streetlight fixture heads

1. Extend a modular mast arm horizontally.
2. Place `M400A2 HPS Fixture` in the block directly beyond the arm end, with its rear facing the arm.
3. Confirm the rear tenon touches the mast.
4. Repeat with the cutoff HPS and LED GCL fixtures.
5. Test all 16 placement rotations.
6. At night, confirm HPS emits level-12 light and LED emits level-15 light from the fixture location.
7. Power each fixture with redstone and confirm it switches off within roughly half a second.

## World-upgrade note

The three HPS/LED registry IDs changed from full-pole renderers to head-only fixtures. Break and replace those blocks when testing an older alpha world. The mod attempts to remove leftover invisible helper-light blocks automatically.

## Icons

The existing icon files were intentionally left unchanged.
