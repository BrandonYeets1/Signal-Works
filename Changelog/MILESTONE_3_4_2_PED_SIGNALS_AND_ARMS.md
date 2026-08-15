# Signal Works Core 3.4.2 — Pedestrian Signals and Adjustable Arms

## Completed scope

- US/Canada pedestrian legends: white walking person and traffic-orange raised hand
- legacy pedestrian legend option retained for existing designs
- pedestrian style selection added to the Signal Customizer
- one adjustable pole-mounted signal arm with one through five usable signal positions
- independent left and right wing extension
- center control for reversing attached-signal facing
- automatic orientation and top-mount detection for signals placed below enabled arm positions
- support color and collar width inherited from the adjacent modular pole
- retraction protection when a signal or other block occupies the removed hanger column

## Controls

### Adjustable Signal Arm

1. Place the arm against the side of a modular signal pole.
2. Use the Signal Adjuster Wrench on the left or right side to extend that wing.
3. Sneak-use the same side to shorten it.
4. Use the center saddle to switch signal facing toward or away from the pole.
5. Place functional signal heads below any enabled position. Their rotation and overhead mount are selected automatically.

### Pedestrian legend

1. Select **Pedestrian Style** on the Signal Customizer by using it in the air.
2. Use it on a functional pedestrian signal.
3. Cycle between **US/Canada Symbols** and **Legacy Symbols**.

## Asset delta

Only two new textures are introduced:

- `ped_walk_white.png`
- `ped_hand_orange.png`

The arm reuses the existing support texture and mast-arm inventory icon. No existing texture was replaced.

## Compatibility

- internal mod ID remains `trafficcontrol`
- existing registry IDs and namespaces remain unchanged
- pedestrian heads loaded from older saves default to the legacy visual style when no style tag exists
- newly placed pedestrian heads default to US/Canada symbols
