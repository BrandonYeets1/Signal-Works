# Milestone 2.4 Testing

## Build

From the extracted project folder:

```bat
gradlew.bat clean build
```

Expected JAR:

```text
build\libs\trafficcontrol-2.0.0-alpha.2.4.jar
```

Remove every older Traffic Control alpha JAR before launching.

## 1. Master synchronization test

1. Build one intersection with at least one north/south head and one east/west head within 12 blocks.
2. Add several more standard, five-section, and doghouse heads on both axes.
3. Wait up to one second for the local group scan.
4. Confirm every head on the same axis changes on the same tick.
5. Build a second valid intersection elsewhere and confirm it follows the same world-time sequence.

Expected result: no per-block phase offset or cumulative drift.

## 2. Doghouse test

1. Place `Traffic Light Doghouse` as one approach of a valid intersection.
2. Watch one complete cycle.
3. Confirm its circular lenses operate during the through phase.
4. Confirm its green and yellow left arrows operate during the protected-turn phase.
5. Confirm conflicting axes stay red.

## 3. Pole and mast-arm test

1. Place Small, Medium, and Large Signal Poles side by side.
2. Stack each pole vertically.
3. Place matching Mast Arm pieces and extend them horizontally.
4. Place the pieces while facing north, east, south, and west.
5. Confirm the model and collision shape follow the cardinal placement direction.

The mast arms are modular one-block segments in this alpha; they do not automatically attach signal heads.

## 4. Street-light emission test

1. Set the time to night.
2. Place one single and one double street light in open air.
3. Confirm light originates near each lamp head and also has a weaker fallback around the base.
4. Repeat under a tree or near decorative blocks; the lamp should search nearby air for its technical light source.
5. Power the street-light base with redstone and confirm the lamp darkens and its light disappears.
6. Break the base and confirm no invisible illumination remains.

## 5. Emissive signal-glow test

1. Test at night with shaders disabled.
2. Confirm active lenses remain fully bright and display a soft matching red, amber, or green halo.
3. Test with Iris and your preferred shader.
4. Check whether that shader adds bloom or colored spill from the emissive layer.

Vanilla block-light propagation remains neutral rather than RGB-colored.

## 6. Pedestrian-signal test

1. Place `Pedestrian Signal (2 Section)` near a valid automatic intersection.
2. Rotate it to the axis it should follow.
3. During that axis's through-green phase, confirm WALK is shown.
4. During through-yellow, confirm the hand blinks.
5. During all other phases, confirm a solid hand is shown.
6. Place multiple pedestrian heads and confirm their blinking is synchronized.

## Failure report

For a compile failure, run:

```bat
gradlew.bat clean build --stacktrace
```

For an in-game failure, include:

```text
.minecraft\logs\latest.log
```

along with a screenshot and the placement directions of the affected signals.
