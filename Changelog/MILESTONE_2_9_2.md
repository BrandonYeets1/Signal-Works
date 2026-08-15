# Traffic Control NeoForge 1.21.1 — Milestone 2.9.2

This incremental patch adds automatic day/night operation and separated streetlight illumination.

## Day/night operation

All imported `StreetLightModelBlock` fixtures now:

- switch off during the day;
- switch on at night;
- remain off while receiving redstone power;
- recheck their state once per second.

## Separated bloom and road illumination

The imported fixture block itself now emits zero block light. This prevents the entire metal housing from being treated as the light source.

When lit, the fixture places several invisible level-15 helper lights below and in front of itself. The spread produces wider and stronger road coverage than a single source. Helper lights are removed at sunrise, when redstone powers the fixture, or when the fixture is broken.

## Selective LED emissive model

`classic_street_light_c` now has two visual models:

- `classic_street_light_c.json`: day/off model;
- `classic_street_light_c_on.json`: night/on model.

Only the LED underside face receives NeoForge full-bright face data in the on model. The housing keeps normal lighting. The supplied texture PNG is not included or modified.

The model also repairs its two unresolved `#missing` faces, records the 128×128 Blockbench texture size, and keeps `uvlock` disabled in the blockstate.

## Packaging

- Only this current milestone note is included.
- No historical milestone notes are included.
- No texture PNGs are included.
- No inventory icons are included or regenerated.
