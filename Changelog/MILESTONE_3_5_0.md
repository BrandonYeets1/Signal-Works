# Signal Works Core 3.5.0 — Freeway Operations

## Controller field kit

The traffic-light controller now has a persistent, take-only two-slot cabinet drawer. On the first opening of each controller, it contains one Signal Adjuster Wrench and one Intersection Engineer Wand. The drawer never regenerates after the tools are removed. Contents save with the controller and drop when the cabinet is broken.

## Signal phasing corrections

- Four-section protected-left heads now keep circular red illuminated during the green-arrow phase.
- Their protected-left clearance terminates directly to red. The circular yellow lens is reserved for the straight movement and no longer illuminates after the green arrow.
- Ramp meters are excluded from ordinary intersection discovery and can run standalone or be explicitly linked.

## Pedestrian operation

- Pedestrian buttons play `trafficcontrol:ped_button` from `ped_button.ogg`.
- A press records a pedestrian call instead of changing the active vehicle phase.
- WALK begins only at the next matching through-green boundary.
- Calls made during an active green wait for the next cycle.
- Only pedestrian signals on the matching roadway axis and controller are grouped.
- Clearance uses a flashing hand before returning to a steady stop indication.

## Freeway equipment

- Added the two-lamp `trafficcontrol:ramp_meter_signal` with red/green metering operation.
- Added `trafficcontrol:freeway_guide_sign`, a wide overhead guide-panel foundation.
- Added modular `trafficcontrol:freeway_sign_pole` and `trafficcontrol:freeway_sign_mast` supports.
- Adjustable signal arms now render black.

## Compatibility and corrections

- The obsolete T signal remains registered for existing worlds but is removed from the creative catalog.
- The construction LED message board applies a local screen-space correction so text and directional programs are no longer mirrored.
- Internal namespace and registry compatibility remain `trafficcontrol`.

## Applying the source patch

1. Extract this patch anywhere.
2. Open Command Prompt in the project directory containing `build.gradle`.
3. Run the patch script using its full path, or copy the script folder into the project and run:

```bat
Apply-SignalWorks-3.5.0.bat
```

4. Build with Java 21:

```bat
gradlew.bat clean build
```

The patch uses a separate `payload` directory, making it safe to run from inside the project folder without copying a file onto itself.
