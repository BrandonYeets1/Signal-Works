# Milestone 2.6 — Connected Supports and Directional Barriers

Version: `2.0.0-alpha.2.6`

## Concrete barrier

The concrete barrier is now a dedicated directional block rather than a generic placeholder.

- Four cardinal placement states (`north`, `east`, `south`, `west`)
- Faces toward the placing player
- Snaps to a neighboring straight barrier run when possible
- Fence-style `north/east/south/west` connection properties
- Adjacent same-axis segments remove their touching end caps
- Separate north/south and east/west collision shapes

Build straight runs by placing barriers beside one another. Turn 90 degrees before placement to start a differently oriented run.

## Modular support connections

The six support blocks now use fence-style connection state:

- Small / medium / large signal poles
- Small / medium / large mast arms

Every support state includes:

- `north`
- `east`
- `south`
- `west`
- `up`
- `down`
- mast placement `facing`

Neighbor updates recalculate the connection flags automatically.

### Visual construction

- Pole blocks always render a vertical center post.
- A pole gains a center-to-edge branch when a mast or another support touches one of its sides.
- Mast blocks always render a one-block straight core along their placement axis.
- Mast ends gain coupling collars when connected.
- Perpendicular mast neighbors add branch geometry, allowing corners and T-junctions.
- Small, medium and large pieces may transition into one another; the collars hide the seam.

This uses conditional multipart model pieces rather than spawning a fragile extra world block. Visually, the conditional coupling is the “third connector piece,” but it remains part of the support blockstate.

## Signal-head mounting

The existing conditional signal-head hanger and pole bracket remain active. The renderer now identifies support size and pole/mast type directly from the modular support block, so all three widths remain compatible.

## Tags

A custom block tag is included:

`#trafficcontrol:signal_supports`

The supports are intentionally not added to `#minecraft:fences`; connecting traffic hardware to wooden/nether-brick fences by default would create unwanted geometry. Other mods and datapacks can target the custom tag instead.

## Existing worlds

Break and replace supports and concrete barriers placed by older alpha versions. Their saved blockstates do not contain the new connection properties, so replacement is the safest visual test.
