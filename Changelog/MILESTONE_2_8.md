# Traffic Control NeoForge 1.21.1 — Milestone 2.8

## Core expansion foundation

Milestone 2.8 adds the first major control-and-enforcement expansion on top of the 2.7.4 source baseline.

### New blocks

- Dedicated Left-Turn Signal
- Dedicated Right-Turn Signal
- Traffic Detection Camera
- Red-Light Enforcement Camera
- LED Lane Sign — No Left Turn
- LED Lane Sign — No Right Turn
- LED Lane Sign — No Through Movement

### Configurable traffic controller

Right-clicking the existing Traffic Light Control Box now opens a slotless settings screen. The controller stores and synchronizes:

- Through-green duration: 2–30 seconds
- Protected-turn priority: left, right, or both
- Night flashing-yellow mode
- Overall phase speed: slow, normal, or fast

Signals search for the nearest controller in range. With no controller nearby, they continue using the normal automatic profile.

### Signal behavior

- Left-turn heads use red, yellow, and green left arrows.
- Right-turn heads use red, yellow, and green right arrows.
- Dedicated heads participate in the existing synchronized intersection clock.
- Five-section and doghouse heads continue to act as protected-left heads.
- Night mode flashes applicable amber indications during the configured nighttime window.

### Cameras and lane-control signs

- Traffic cameras scan the roadway ahead and emit a redstone signal while traffic is detected.
- Red-light cameras scan only while receiving a redstone red-phase input.
- Lane-control LED boxes are illuminated by default and switch off when powered.

### Creative inventory and metadata

- Creative ordering now starts with poles, mast arms, stands, and signal heads.
- Controls, cameras, signs, lighting, road furniture, and railroad equipment follow in grouped order.
- The mod description has been rewritten for the NeoForge port.
- The replaceable mod-loader icon path is `src/main/resources/trafficcontrol.png`.

## Packaging rules applied

- This is an incremental patch for Milestone 2.7.4, not a complete standalone project.
- Historical milestone Markdown files are not included.
- Unchanged texture folders and inventory icon PNGs are not included.
- The only new PNG is the required root mod icon.

## Planned for the next expansion

These requested systems are deliberately queued instead of being shipped half-finished:

- Editable LA-style hanging street-name signs
- Font selection and custom-font support
- Separate traffic-sign catalog/menu
- Yellow reflective and old-green signal backplates
- Utility poles and wire spans
- Additional freeway and regulatory LED sign families

## Build target

- Minecraft 1.21.1
- NeoForge 21.1.240
- Java 21
- Mod version `2.0.0-alpha.2.8`
