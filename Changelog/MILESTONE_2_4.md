# Milestone 2.4 — Synchronized Signals and Lighting

Version: `2.0.0-alpha.2.4`

This milestone expands the functional signal system and adds the first modular support structures.

## Shared intersection clock

Every automatic intersection now reads the same world-game-time master sequence. The sequence length is fixed for both axes, so signals cannot drift merely because one local scan found a protected-turn head and another did not.

A signal still requires a valid nearby two-axis intersection before it leaves its safe red state:

- at least two functional vehicle signal heads
- one north/south-axis signal
- one east/west-axis signal
- within the existing 12-block scan radius and 5-block vertical range

Once recognized, all standard, five-section, doghouse, and pedestrian heads use the same master cycle.

## Pole and mast-arm families

Six modular construction blocks were added:

- Small Signal Pole — 2-pixel width
- Medium Signal Pole — 4-pixel width
- Large Signal Pole — 6-pixel width
- Small Mast Arm — 2-pixel width
- Medium Mast Arm — 4-pixel width
- Large Mast Arm — 6-pixel width

The pieces rotate to the four cardinal directions. They are one-block sections designed to be stacked or extended by the player.

## Functional doghouse signal

`traffic_light_doghouse` is now registered with the shared traffic-light block entity and renderer. Its default five lenses are:

- red circular indication
- yellow circular indication
- green circular indication
- yellow left arrow
- green left arrow

It participates in the protected-turn and through phases like the vertical five-section signal.

## Street-light illumination

Single and double street lights now:

- emit neutral vanilla block light
- create replaceable invisible level-15 light sources near each rendered lamp head
- search nearby air positions when the exact head position is obstructed by leaves or decoration
- turn off when the base receives a redstone signal
- remove their technical light sources when switched off or broken

The base also emits a smaller fallback light level while lit, so the street light still produces some illumination when every candidate position around the lamp head is obstructed.

## Emissive signal lenses

Active traffic-light lenses now have a full-bright colored halo rendered with an emissive translucent layer. Red, amber, and green lenses therefore retain their color and glow in darkness.

Minecraft's vanilla light engine is monochrome, so the world illumination created by the block remains neutral. Shader packs may add colored bloom or spill from the emissive rendered lenses, depending on the shader.

## Three-phase pedestrian signal

`traffic_light_2` is now the functional two-section pedestrian signal:

1. WALK during the matching through-green phase
2. flashing hand during the matching through-yellow phase
3. solid hand during protected turns, all-red, and the crossing traffic's phases

The hand flash uses the same world clock, so pedestrian heads blink together.

## Resource cleanup

This milestone also corrects several inherited legacy model references:

- `traffic_rail` now references the modern vanilla oak-log texture path
- invalid internal face aliases in the Safetran Type 3 and mechanical-bell models were replaced with valid aliases
