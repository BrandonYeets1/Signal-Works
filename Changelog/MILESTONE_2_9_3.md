# Milestone 2.9.3 — Streetlight Activation Fix

This patch repairs imported streetlights that remained dark after applying Milestone 2.9.2.

## Root cause

Milestone 2.9.2 started its day/night loop from `onPlace`. Streetlights already present in a saved world were loaded without receiving that placement callback, so no scheduled tick was created and their `lit` property could remain false indefinitely.

## Changes

- Existing imported streetlights are indexed when their chunks load.
- Loaded fixtures refresh once per second without forcing unloaded chunks to load.
- Newly placed fixtures still start immediately.
- Random ticks provide an additional self-healing path.
- Night detection now uses `Level#isNight()` directly.
- Imported fixtures emit their configured local block light while lit:
  - dim: 9
  - standard/classic: 12
  - LED: 15
- Several invisible level-15 helper lights spread illumination below and ahead of the fixture.
- Helper positions now search nearby air when a pole or mast occupies the ideal location.
- Redstone continues to force a fixture off.
- The selective full-bright LED face for `classic_street_light_c_on.json` remains unchanged.

## Packaging

- Apply over Milestone 2.9.2.
- No textures are included.
- No inventory icons are included.
- No historical milestone notes are included.
