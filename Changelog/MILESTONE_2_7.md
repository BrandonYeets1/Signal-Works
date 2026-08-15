# Milestone 2.7 — Flush Signal Mounts and Modular Streetlight Heads

## Traffic-signal mast mounting

Traffic heads placed below a mast arm now use a local, signal-rotated mount assembly:

- width-matched clamp around the mast
- vertical hanger to the head
- short forward neck
- rear mounting plate against the signal shell
- three-pixel forward offset for the body and active bulbs

The mount rotates through the same 16 positions as the signal, so cardinal and 45-degree heads use the same flush geometry.

## Modular roadway fixtures

The following existing registry IDs are now one-block fixture heads instead of complete streetlight poles:

- `street_light_hps_m400a2`
- `street_light_hps_m400a2_cutoff`
- `street_light_led_gcl`

They can be placed beside the end of a modular mast arm or used in a custom support build. Each fixture includes a rear mounting tenon that reaches its block boundary.

### Light output

- standard HPS: block light 12, warm orange full-bright lens
- cutoff HPS: block light 12, flat warm orange full-bright lens
- LED GCL: block light 15, white full-bright lens

Redstone power switches a fixture off. Vanilla block lighting remains monochrome, while the visible lens keeps its HPS or LED color.

The old `street_light_single` and `street_light_double` blocks remain as legacy complete-pole fixtures.

## Inventory icons

No icon texture or item-model files were regenerated in this milestone.
