# Milestone 2.6.1 — Connection Hotfix

This patch addresses the three visual/placement regressions reported after Alpha 2.6.

## 45-degree signal mounting

Signal-head support hardware is now rendered in block/world space instead of being restricted to cardinal signal rotations.

- Signals at 22.5-degree and 45-degree angles can connect to a cardinally adjacent signal pole.
- When more than one pole is adjacent, the renderer prefers the pole nearest the rear of the signal head.
- Existing overhead mast hangers and top-of-pole collars remain active.

## Concrete barrier runs

The barrier's `facing` direction now represents its broad face, while its run axis is perpendicular to that direction.

- Placement faces the broad side toward the player.
- Straight runs form left-to-right across the player's view.
- Neighboring barriers on the same run axis remove their internal end caps.
- Placement beside a single existing run automatically snaps to that run's axis.
- Collision follows the corrected visual axis.

## Smooth mast extensions

Straight mast-arm segments no longer render a coupling collar at every block boundary.

- Matching straight segments form a continuous beam.
- Pole branches, corners, and T-junction branches remain conditional multipart pieces.
- No item icons were regenerated or changed in this patch.
