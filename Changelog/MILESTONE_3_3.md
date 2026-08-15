# Traffic Control NeoForge 1.21.1 — Milestone 3.3

Version: `2.0.0-alpha.3.3.0`

Milestone 3.3 starts the actuated-detection and signal-hardware customization roadmap while repairing the selective streetlight emissive models introduced in 3.2.5.

## Streetlight emissive-angle repair

The GE M-250A2 and GreenCobra GC1 night models now use their normally shaded **off** texture as the fixture housing. A separate, thin underside face uses the emissive map.

This prevents the complete luminaire shell from turning full-white when viewed from the side or rear. Only the lamp optic / LED board is full-bright.

Updated models:

- `classic_street_light_bb_on.json`
- `classic_street_light_c_on.json`

The emissive PNGs from Milestone 3.2.5 are reused and are not duplicated in this patch.

## Functional road detectors

The three existing sensor blocks are now functional:

- Traffic Sensor — Straight
- Traffic Sensor — Left
- Traffic Sensor — Right

A sensor becomes occupied when a vehicle-sized entity travels over it. While occupied it:

- creates a controller phase call;
- outputs redstone level 15;
- keeps checking until the detection area is clear.

Controllers scan for sensors within 18 blocks horizontally and 5 blocks vertically.

### Demand rules

- A movement category with no installed detector remains on normal recall, preserving the existing fixed-time cycle.
- Once a detector is installed for a category, that category runs only when its sensor is occupied.
- Empty turn or through stages can be skipped.
- When only one road axis has demand, its through-green time can extend up to twice the configured duration.
- Left and right sensors currently share the controller's protected-turn call for their axis. Independent left-versus-right phases are a later phase-editor feature.

The controller terminal now reports:

- detector count;
- X-axis demand;
- Z-axis demand.

Terminal command:

```text
detectors
```

## Signal Customizer

A new `Signal Customizer` tool appears beside the Engineer Wand and Adjuster.

### Select a channel

- Right-click air: next channel
- Sneak-right-click air: previous channel

Channels:

- Backplate
- Visor
- Mount

### Apply to a signal

- Right-click signal: next option
- Sneak-right-click signal: previous option

Available options:

### Backplate

- None
- Black
- Reflective yellow

### Visor

- Standard
- Tunnel

`Standard` keeps the signal's existing model visor. `Tunnel` adds extended top and side shielding around every lens.

### Mount

- Automatic
- Side
- Top

The customizer changes visual hardware only. It does not alter controller links, movement roles, timing, or bulb state.

Backplate and visor choices are saved in the signal block entity and synchronized to clients. Mount style uses the existing saved blockstate property.

## Compatibility

- Existing registry IDs are unchanged.
- Existing worlds remain compatible.
- Existing signals default to no added backplate and standard visors.
- Existing sensor IDs are preserved and upgraded in place.
- No new PNG icons or unchanged textures are included.

## Known foundation limitations

- Sensors are discovered by controller proximity rather than explicit wand linking.
- Detection is axis-based; fully independent approaches and left/right turn calls are planned for the visual phase editor.
- This is the first actuated-timing pass and should be tested with simple four-way intersections before large corridor builds.
