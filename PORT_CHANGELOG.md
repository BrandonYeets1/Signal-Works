# Port changelog

## 2.0.0-alpha.2.7.1

- Removed the three-pixel forward translation applied to mast-mounted traffic heads.
- Repositioned the hanger neck and rear mounting plate to the signal shell rear face.
- Inventory icon PNGs and item model references are unchanged.


## 2.0.0-alpha.2.7

- Shifted mast-mounted traffic-signal bodies three pixels forward so their rear shell sits against a real mounting plate.
- Rotated the hanger, neck, and mast clamp with all 16 signal-head angles.
- Converted the M400A2 HPS, M400A2 cutoff, and GreenCobra-style LED registrations into modular one-block fixture heads.
- Added rear tenons that reach the block boundary for flush mast-arm attachment.
- Moved HPS/LED light emission to the fixture block itself: level 12 for HPS and level 15 for LED.
- Preserved redstone shutoff and added cleanup for invisible helper lights left by older alpha worlds.
- Kept the legacy tall single/double streetlight registrations unchanged.
- Did not regenerate or modify inventory icon images.

## 2.0.0-alpha.2.6.1

- Added side-mount brackets for traffic signals at all 16 rotation steps, including 45-degree angles.
- Corrected concrete-barrier run-axis placement and matching collision orientation.
- Corrected barrier end-cap conditions so straight neighboring segments visually merge.
- Removed repeated mast collars from same-axis straight extensions.
- Preserved conditional pole branches, corners, and T-junction geometry.
- Left the existing inventory icon files unchanged.


## 2.0.0-alpha.2.6

- Replaced the concrete barrier placeholder with a four-direction connectable block.
- Added automatic straight-run barrier joins and conditional end caps.
- Reworked all six modular support blocks with fence-style connection properties.
- Added multipart pole branches, mast collars, corners and T-junctions.
- Added `#trafficcontrol:signal_supports` for future compatibility.
- Preserved the existing conditional signal-head mounting renderer.

# 2.0.0-alpha.2.4.4

- Removed unconditional signal-head mast hardware that caused floating plates.
- Restored all traffic-light JSON models to valid Minecraft element bounds, fixing missing-model cubes.
- Added conditional mast hangers, width-matched clamps, pole collars, and side-mount brackets.
- Corrected mast-arm placement direction and rear overlap into adjacent pole blocks.
- Corrected doghouse lens placement so protected-left arrows appear in the left cabinet.

# 2.0.0-alpha.2.4.2

- Fixed missing `net.minecraft.world.level.block.Block` import in `RotatablePoleBlock`.
- Resolves the `cannot find symbol: class Block` Java compilation failure.

# Alpha 2.4.1

- Fixed `RotatablePoleBlock.codec()` return type for Minecraft/NeoForge 1.21.1 compilation.

# Port Changelog

## 2.0.0-alpha.2.4 — Milestone 2.4

- Replaced variable local phase lengths with one fixed world-time master sequence so valid automatic intersections cannot drift.
- Added Small, Medium, and Large Signal Pole blocks.
- Added Small, Medium, and Large Mast Arm blocks with cardinal placement.
- Ported `traffic_light_doghouse` to the functional traffic-light block entity and renderer.
- Ported `traffic_light_2` as a synchronized two-section pedestrian signal with WALK, flashing hand, and solid hand phases.
- Added replaceable invisible level-15 light sources near single and double street-light heads.
- Added nearby-air fallback placement for street lights obstructed by leaves or decoration.
- Added redstone street-light shutoff and technical-light cleanup.
- Added weaker fallback light emission at the street-light base.
- Added full-bright emissive red, amber, and green signal-lens halos.
- Corrected inherited legacy texture and internal face references in several models.
- Expanded the registry and inventory-icon set from 52 to 58 blocks/items.

## 2.0.0-alpha.2.3 — Milestone 2.3

- Added 52 custom flat inventory icons and converted every registered item model to `item/generated`.
- Hid technical helper blocks from the creative tab.
- Added explicit non-occluding visual shapes to traffic lights and streetlights.
- Applied non-occluding properties to unfinished placeholder blocks.
- Added automatic local two-axis intersection cycling.
- Ported `traffic_light_5` as a five-section functional rendered signal.
- Added a protected left-turn arrow phase for axes containing a five-section head.
- Registered the five-section head with the traffic-light block entity and renderer.

## 2.0.0-alpha.2 — Milestone 2

- Added the first functional three-bulb traffic light.
- Added 16-step placement rotation and original collision/selection bounds.
- Added a persistent three-slot traffic-light block entity.
- Preserved all 16 legacy bulb IDs and original NBT field names.
- Added block-entity update synchronization.
- Added exact-angle body and bulb rendering with original textures.
- Restored the original pig-above easter egg.
- Registered the traffic-light block-entity renderer on the client mod bus.
- Replaced active legacy Forge-marker blockstates with safe modern placeholder states.
- Generated matching item models for every registered placeholder block.
- Converted original English block names to modern JSON translations.
- Updated the NeoForge loader range and Gradle wrapper metadata.

## 2.0.0-alpha.1 — Milestone 1

- Created the NeoForge 1.21.1 / Java 21 project foundation.
- Preserved all 52 original block IDs and eight sound IDs.
- Copied the original asset tree.
- Added temporary generic block and BlockItem registrations.

## 2.0.0-alpha.2.2

- Restored black inactive/flash-off bulb faces in the base traffic-light renderer.
- Ported `street_light_single` and `street_light_double` from flat placeholders to 16-direction block-entity-rendered structures.
- Reconstructed the original four-block post, arm, lamp housing, and full-bright lamp surface.
- Preserved the original street-light item icons.

## 2.0.0-alpha.2.4.3
- Extended mast-arm models into adjacent pole centreline.
- Added width-matched pole coupling sleeves.
- Added automatic mast hangers and clamps to signal-head models.

## 2.0.0-alpha.2.5.1
- Fixed the StreetLightBlockEntityRenderer override signature.
- Removed duplicated led/cutoff method parameters that caused four Java compiler errors.


## 2.0.0-alpha.2.7.3
- Added centered front-of-mast signal mounting (`====[signal]====`).
- Added short mast standoff, clamp, and rear signal plate.
- Removed the legacy Horizontal Pole item from crafting and creative inventory while retaining its block registry for world compatibility.
- Added “Temporary traffic pole” tooltip to Stand.
- Left all inventory icon PNGs unchanged.


## 2.0.0-alpha.2.7.4
- Corrected the cardinal rear-surface coordinates used by center/front mast mounts.
- Shortened each standoff tube so it terminates at the outside mounting plate instead of entering the signal housing.
- Moved positive-direction plates to the correct rear side after signal rotation.
- Kept all inventory icon PNGs unchanged.
