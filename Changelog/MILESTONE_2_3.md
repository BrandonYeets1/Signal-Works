# Milestone 2.3 — Inventory Icons and Automatic Intersections

Version: `2.0.0-alpha.2.3`

## Inventory icon pass

- Added 52 flat, slot-safe inventory icons under `textures/item/icons`.
- Replaced every registered block item's inventory model with `minecraft:item/generated`.
- Kept the original 3D block models for world rendering.
- Hid the technical `light_source` and `traffic_light_5_upper` blocks from the creative tab.
- Changed the creative-tab icon to the standard traffic signal.

## Ground-hole / face-culling repair

- All unfinished placeholder blocks now use non-occluding block properties.
- Traffic lights and streetlights explicitly return empty visual and occlusion shapes.
- Skylight is allowed to propagate through the renderer anchor block.

This prevents the solid road or sidewalk block underneath a narrow rendered prop from losing its top face.

## Automatic intersection mode

The standard three-section `traffic_light` now automatically participates in a nearby intersection:

1. Place at least two functional signals within 12 blocks.
2. Face at least one signal along the north/south axis.
3. Face at least one signal along the east/west axis.
4. The signals begin cycling without a control box.

The default through sequence is green, yellow, all-red, then the other axis.

## Five-section dedicated-lane signal

`traffic_light_5` is now a functional two-block-tall rendered signal with five active lenses:

1. Red
2. Yellow
3. Green
4. Protected-turn yellow arrow
5. Protected-turn green arrow

When a five-section signal is present on an axis, that axis receives a protected left-turn phase before its normal through phase. During the protected turn, ordinary three-section heads on the same axis remain red.

## First-pass limitations

- The automatic scan is intentionally local and controller-free.
- Intersections within 12 blocks may be treated as one group.
- The base three-section and five-section heads are the functional automatic variants in this milestone.
- Dedicated-lane detection is not yet vehicle-actuated; the protected phase runs every cycle.
- Custom bulb/frame configuration and manual control-box override remain future milestones.
