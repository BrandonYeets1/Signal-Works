# Traffic Control NeoForge 1.21.1 — Milestone 2.9.1

This is an incremental repair patch for a project that already has Milestones 2.8, 2.8.1 and 2.9 applied.

## Mounting rules

Signal placement now records one exclusive mounting mode:

- Clicking a horizontal face places a **side-mounted** signal. It may attach to a neighboring vertical signal pole, but will not generate an overhead hanger.
- Clicking the underside of a mast arm places a **top-mounted** signal. It may generate the overhead drop pipe, but will not generate side hardware toward a pole.
- Signals placed by replacement/commands retain an automatic compatibility mode.

The pole and mast multipart connection logic follows the same rule, preventing top-mounted signals from creating side branches and preventing side-mounted signals from creating mast-drop branches.

Break and replace existing signal heads to assign the new explicit mounting mode.

## Pedestrian button

The button now stores the direction of the supporting pole/wall rather than the clicked face. Clicking a pole face places the button flush against that face and points the button toward the player. Break and replace old buttons.

## Controller and timing

Controller settings now directly drive the server-side phase clock. Changing a setting restarts that controller's cycle, and nearby signals refresh the controller profile every 10 ticks.

Default timings:

- Protected turn green: 8 seconds
- Protected turn yellow: 3 seconds
- Turn clearance: 1 second
- Through green: 15 seconds
- Through yellow: 3 seconds
- All-red: 1.5 seconds

The GUI through-green setting now ranges from 5 to 60 seconds and changes in 5-second steps. Slow mode uses 150% duration; fast mode uses approximately 67% duration. The old 6-second alpha default migrates to 15 seconds.

## Colored supports

Right-click any small, medium or large pole/mast with dye:

- Black Dye: black
- Yellow Dye: safety yellow
- Green Dye: old signal green
- White Dye: white
- Gray or Light Gray Dye: galvanized

Dye is consumed outside Creative mode. Connected brackets and renderer-generated hangers inherit the support's selected color. No new support block IDs or texture PNGs were needed.

## Streetlight model repair

All 39 imported streetlight models now use the cutout render type so transparent pixels in their supplied PNG atlases do not become solid floating planes. Four models with unresolved helper faces now point those faces to the already-existing transparent texture.

No texture PNGs are included in this patch.

## Legacy streetlights

`Street Light Single` and `Street Light Double` are removed from the creative streetlight tab. Their registrations remain for test-world compatibility.

## Packaging

- Only this current milestone note is included.
- No historical milestone files are included.
- No texture PNGs are included.
- No inventory icons are included or regenerated.
