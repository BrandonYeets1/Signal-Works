# Milestone 2.2 visual geometry hotfix

This hotfix addresses the two in-game issues reported after Alpha 2.1:

- Traffic-light inactive and flash-off bulb slots are explicitly rendered with the original black bulb texture, removing the transparent/invisible centers.
- `street_light_single` and `street_light_double` are now real 16-direction blocks with block entities and extended block-entity renderers rather than flat `item/generated` placeholder planes.
- The street-light renderer reconstructs the original four-block post, sloped connector, arm, housing, and full-bright lamp surface.
- Street-light inventory icons remain the original 2D item textures.

The invisible light-source placement system and redstone shutoff behavior remain for a later functional milestone. In Alpha 2.2, the lamp surface is full-bright, but it does not yet place invisible level-15 light blocks around the lamp head.
