# Signal Works Core — Current Port Status

Current version: `2.0.0-alpha.3.4.2`

## Current completed milestone

### Milestone 3.4.2 — Pedestrian Signals and Adjustable Arms

- modern US/Canada white-walk and orange-hand pedestrian indications
- switchable legacy pedestrian legends through the Signal Customizer
- pole-mounted adjustable signal arm with one through five signal positions
- independent left/right extension and reversible signal facing
- automatic signal orientation and overhead hanger rendering
- adjacent-pole color and width inheritance
- guarded arm retraction when an occupied hanger position would be removed
- two new pedestrian textures; no bulk texture replacement

## Compatibility foundation

- native NeoForge 1.21.1 / Java 21 project
- legacy `trafficcontrol` mod ID, namespaces, registry IDs, and saved-world identifiers preserved
- Signal Works suite creative catalogs retained: Signals, Lights, and RR

## Next development target — Milestone 3.5

- coordinated controller corridors
- configurable cycle lengths, offsets, and synchronization groups
- green-wave progression
- controller-to-controller networking
- transit signal priority
- emergency vehicle preemption
- persistence, diagnostics, and safe fallback behavior

## Validation boundary

Source, resources, metadata, and Java compilation can be validated in this package. Final visual placement,
multiplayer synchronization, collision feel, and saved-world acceptance require a local NeoForge gameplay test.
