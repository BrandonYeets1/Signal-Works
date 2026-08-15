# Milestone 3.2 — Controller Intersection Map

## Integrated terminal map

The Traffic Signal Controller now contains two terminal-style tabs:

- Command Terminal
- Intersection Map

The map is drawn directly inside the controller screen. It displays the controller at the center and up to 32 linked signal heads according to their relative world positions.

## Visual movement assignment

Click a signal node, then assign one of the compatible movement roles:

- Through
- Protected left
- Protected right
- Through + protected left
- Pedestrian

Assignments are sent through the existing vanilla menu-button channel and saved by the controller for multiplayer use.

Map node labels:

- 3 — standard three-section head
- 4 — four-section head
- 5 — five-section head
- D — doghouse
- L/R — dedicated arrow head
- P — pedestrian head

## Combination-head defaults

Newly linked doghouse, five-section, and four-section signals default to Through + Left so their protected-left indications work immediately. The map editor can override them to Through-only or Left-only.

## Functional four-section signal

`traffic_light_4` is now a real block-entity signal with:

- circular red
- circular yellow
- circular green
- protected-left green arrow

During protected-left clearance, it uses the circular yellow because the model has no separate yellow arrow.

Break and replace older four-section blocks if they were placed before this milestone.

## Packaging

This incremental patch contains only files changed for Milestone 3.2. No texture PNGs, inventory icons, or historical milestone notes are included.
