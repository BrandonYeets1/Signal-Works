# Traffic Control NeoForge 1.21.1 — Milestone 3.4

Version: `2.0.0-alpha.3.4.0`

Milestone 3.4 upgrades the existing `street_sign` registry ID into a functional municipal street-name assembly with a terminal-style editor and dynamic in-world text.

## Editable municipal street signs

Right-click an Editable Municipal Street Sign to open the Municipal Sign Programming Terminal.

Editable fields:

- Primary street name
- Cross-street name
- District / city label
- Block number

Text is stored in the sign block entity, synchronized to clients, and retained across world saves.

## Terminal-style sign editor

The editor matches the Traffic Signal Controller terminal aesthetic and includes:

- four text-entry fields;
- live two-blade preview;
- municipality palette selector;
- uppercase toggle;
- reflective-text toggle;
- double-sided rendering toggle;
- `WRITE TO SIGN` save control.

Pressing Enter also writes the current text fields to the sign.

## Municipal palettes

Included profiles:

- LA Blue
- Classic Green
- Historic Brown
- Black / White

Each profile controls the blade, border, and lettering colors without requiring separate texture files.

## Dynamic two-blade renderer

The sign renders as two perpendicular street-name blades with:

- primary and cross-street names;
- district / city footer;
- optional block number;
- front and rear text when double-sided mode is enabled;
- reflective full-bright lettering when reflective mode is enabled;
- automatic text fitting for long names.

## Rotation

The sign supports sixteen physical orientations. The Signal Adjuster Wrench rotates it in the same 45-degree positive or negative steps used by signal heads and modular streetlights.

## Compatibility

- The existing registry ID remains `trafficcontrol:street_sign`.
- The existing inventory icon is reused.
- No new PNG textures are included.
- Existing placed legacy street-sign blocks may need to be broken and replaced once so Minecraft creates the new block entity.
- The generic `trafficcontrol:sign` catalog block is unchanged.

## Next roadmap target

Milestone 3.5 is prepared for coordinated corridors: controller networking, green-wave offsets, transit priority, and emergency preemption.
