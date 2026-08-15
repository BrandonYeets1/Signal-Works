# Milestone 2.9 — Mounting Cleanup and Streetlight Catalog

## Signal mounting fix
Signals suspended from a mast arm above no longer also create side/front standoffs toward a shared vertical pole. This prevents support beams from visually linking neighboring signal heads around multi-arm intersections.

## Pedestrian button remodel
The pedestrian call button is now a directional wall/pole-mounted block. It has no built-in full-height pole, sits against the clicked face, and retains the existing WALK-request behavior.

## Supplied streetlight models
Imported 39 JSON fixture models from the supplied `streetlights.zip` asset pack. They are grouped in a new **Traffic Control — Streetlights** creative tab.

- LED fixtures emit block light level 15.
- Standard fixtures emit block light level 12.
- Dim/low-output fixtures emit block light level 9.
- Redstone power switches each imported fixture off.
- Placement supports the four cardinal directions and connects visually to modular mast arms.

Only newly required streetlight model textures are included in this patch. Existing inventory icon PNG files are untouched.
