# Milestone 3.2.3 — Combined-Head Red Fix, Road Lighting, and Adjuster Wrench

Apply this incremental patch over Milestone 3.2.2.

## Combined-head red indication

The previous fix still depended on the controller assignment including `THROUGH`. A doghouse or five-section head assigned as `LEFT` therefore illuminated its protected arrow but suppressed its circular red.

Milestone 3.2.3 keys the red indication to the physical head type instead:

- Doghouse protected green: circular red + green left arrow
- Doghouse clearance: circular red + yellow left arrow
- Five-section protected green: circular red + green left arrow
- Five-section clearance: circular red + yellow left arrow
- The behavior works for `LEFT` and `THROUGH_LEFT` assignments
- Night flashing-arrow operation keeps the circular red active as well

## Road-projected streetlight emitters

Invisible level-15 sources are no longer concentrated only a few blocks below the fixture. Each streetlight now searches downward for the roadway and places a five-source pool roughly two blocks above the detected surface:

- directly below the fixture
- two and four blocks forward
- forward-left and forward-right spread points

The downward search supports fixtures mounted as high as 20 blocks above the road. Imported JSON streetlights, modular LED/HPS heads, and the original tall streetlights use the same system.

## Signal Adjuster Wrench

A new `Signal Adjuster Wrench` is available beside the Engineer Wand in the main creative tab.

- Right-click a signal: rotate +45 degrees
- Sneak-right-click a signal: rotate -45 degrees
- Rotation is saved in the existing 16-position block state
- Signal housing, bulbs, mounting hardware, and dynamic pole arm rotate together
- Controller ownership and movement assignment are not changed
- Modular HPS/LED streetlight heads and the original rotatable streetlights are also supported

The item uses a new open-end wrench model built from existing mod textures, so no new texture PNG is included.

## Build

```bat
gradlew.bat clean build
```

Expected JAR:

```text
build\libs\trafficcontrol-2.0.0-alpha.3.2.3.jar
```
