# Signal Works Core 3.4.9 — Operations & Lighting

## New changes only

- Replaced packet-sensitive flash timing with a client-local synchronized half-second oscillator.
- Restored night amber, fail-safe red, pedestrian hand, and vintage DONT WALK flashing.
- Fail-safe operation now gives pedestrian heads a flashing stop indication instead of going dark.
- Pedestrian clearance begins during the final five seconds of through green, before vehicle yellow.
- Reworked the pedestrian head into a compact rounded-square LA housing.
- Added a second selectable pedestrian era: vintage `WALK / DONT WALK` text.
- Added a Signal Works field-laptop home screen before the existing terminal and intersection-map applications.
- Expanded streetlight roadway illumination from five elevated emitters to ten pavement-level emitters.

## Assets

Only two new textures are included because the vintage text indications require them:

- `ped_walk_text_white.png`
- `ped_dont_walk_text_orange.png`

No existing texture collection was replaced.
