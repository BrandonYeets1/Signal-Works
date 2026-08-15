# 3.4.13 — Asset Cleanup & Unified Supports

- One block item per pole/mast size now automatically chooses its structural section.
- Grounded vertical placement uses `signal_pole_<size>` with the base flange.
- Additional vertical placements use `signal_pole_<size>_core`.
- The first mast section uses `mast_arm_<size>` with the coupling sleeve.
- Additional mast sections use `mast_arm_<size>_core`.
- Removing a lower/previous section automatically promotes the next section to a base segment.
- Modernized texture roots to `textures/block` and `textures/item`.
- Removed byte-identical `textures/blocks` and `textures/items` duplicates.
- Removed legacy 1.12 recipes stored under `assets`, which modern Minecraft never loads.
- Moved five Blockbench project files out of runtime resources and into `src/development/blockbench`.
- No texture pixels were changed.
