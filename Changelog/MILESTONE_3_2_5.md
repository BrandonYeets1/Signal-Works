# Milestone 3.2.5 — Selective Emissive Streetlights

This patch integrates the supplied Blockbench emission maps for:

- Classic Streetlight BB — GE M-250A2
- Classic Streetlight C — GreenCobra (GC1)

## Behavior

- Day models remain unchanged and non-emissive.
- Night models keep their normal base texture and add a thin full-bright overlay only over the lamp optic.
- Transparent pixels in the emission maps do not render.
- The fixture housing, arm, photocell, and mounting hardware remain normally shaded.
- Shader bloom can react to the illuminated optic without making the full model glow.
- Existing invisible roadway light emitters remain responsible for actual world illumination.

## Included files

- Updated `classic_street_light_bb_on.json`
- Updated `classic_street_light_c_on.json`
- `prop_streetlight_hps_on_e.png`
- `prop_streetlight_led_on_e.png`
- Current milestone and application notes

No day models, unrelated textures, inventory icons, or historical milestone documents are included.
