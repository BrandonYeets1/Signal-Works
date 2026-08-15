# Traffic Control NeoForge 1.21.1 — Milestone 3.1

## Intersection Linking Foundation

Milestone 3.1 replaces proximity-only controller ownership with explicit, persistent signal links.

### Intersection Engineer Wand

A new **Intersection Engineer Wand** is available immediately after the controller in the Traffic Control creative tab.

Controls:

1. Use the wand on a Traffic Signal Controller to select it.
2. Use the wand in the air to cycle the movement assignment.
3. Use the wand on a signal head to link it to the selected controller.
4. Sneak-use a linked signal to unlink it.
5. Sneak-use the wand in the air to clear the selected controller.
6. Hold the selected wand to display colored link lines.

Movement modes:

- Through
- Protected Left
- Protected Right
- Through + Protected Left
- Pedestrian

Dedicated arrow heads and pedestrian heads automatically normalize to their compatible movement assignment.

### Controller-Owned Signal Groups

Each controller now saves a persistent list of linked signal positions and movement assignments. Each linked signal also stores its owning controller and assigned movement.

Behavior:

- A linked signal ignores unrelated nearby controllers.
- Controllers with explicit links no longer control unlinked nearby signals.
- Signals can be reassigned from one controller to another.
- Stale links are pruned when the controller is opened or visualized.
- Removing a controller releases its loaded linked signals.
- If the selected controller is missing or unloaded, its linked signals remain safely red rather than joining a different controller.

### Visual Link Overlay

While holding a wand with a selected controller, colored particles connect the controller to its loaded signal heads:

- Blue: through
- Green: protected left
- Amber: protected right
- Purple: through + protected left
- White: pedestrian

The overlay is limited to 96 blocks from the selected controller.

### Controller Terminal

The terminal header and `status` command now show the number of linked signal heads. A new `links` command displays the count and reminds the player how to enable the visual overlay.

### Compatibility and fallback

Unlinked signals retain the existing automatic nearby-intersection behavior. An unlinked signal may use a nearby controller only when that controller has no explicit signal links.

### Current foundation limitation

Vehicle phasing is still organized by the signal's east/west or north/south axis. Milestone 3.1 establishes ownership and movement assignments; fully independent approach groups, custom phase ordering, and demand-based calls are planned for the next controller phases.

### Packaging

This incremental patch contains only Milestone 3.1 files. It includes no historical milestone documents, no texture PNGs, and no regenerated inventory icons. The wand item model reuses the existing crossing-relay tuner texture.
