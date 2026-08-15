# Milestone 2.4.4 — Signal Geometry & Support Repair

This cleanup release addresses the regressions visible in the 2.4.3 in-game screenshots.

## Fixed signal models

- Removed unconditional baked mast hangers and clamps from every signal-head JSON model.
- Restored every traffic-light model to Minecraft's supported model-element bounds (`-16` through `32`).
- This repairs the magenta/black missing-model cubes seen on the tall signal variants.
- Hangers and clamps are now rendered only when an actual mast-arm block is detected above the signal.

## Connected support behavior

- Mast-arm geometry now reaches backward into the block it was attached to, rather than extending on the wrong side.
- Clicking a horizontal face places the mast arm facing outward from that face, making pole-to-arm construction predictable.
- A signal directly under a mast arm receives a vertical hanger and a width-matched clamp.
- A signal directly above a modular pole receives a seam-covering coupling collar.
- A cardinally aligned signal beside a modular pole receives a rear mounting bracket.
- Small, medium, and large supports produce appropriately sized collars and clamps.

## Doghouse correction

- Protected-left arrow lenses now render in the left doghouse cabinet.
- Through yellow and green lenses now render in the right cabinet.

## Placement notes

For a mast-mounted head:

1. Build the vertical pole.
2. Click the side of the top pole block with a matching mast-arm item.
3. Place a standard/doghouse/pedestrian head one block below the desired mast segment.
4. Place a five-section head two blocks below the mast segment because its cabinet is two blocks tall.

Existing 2.4.3 mast arms may retain their old facing state. Break and replace those arm blocks once after installing 2.4.4.
