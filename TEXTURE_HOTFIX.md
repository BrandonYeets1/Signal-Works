# Milestone 2.1 Texture Hotfix

This hotfix standardizes the original Forge 1.12.2 texture layout for Minecraft 1.21.1.

Changes:
- Added `assets/trafficcontrol/textures/block/` from the legacy `textures/blocks/` tree.
- Added `assets/trafficcontrol/textures/item/` from the legacy `textures/items/` tree.
- Updated 110 model files to use `trafficcontrol:block/...` and `trafficcontrol:item/...`.
- Updated direct renderer texture paths.
- Added `pack.mcmeta` for Minecraft 1.21/1.21.1 resources.
- Retained the legacy folders so no original assets are lost.

Rebuild with `gradlew.bat clean build`.
