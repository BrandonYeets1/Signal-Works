# Signal Works Core 3.4.8 — LA Model Sync & Channelizer Split

## New changes only

- Integrated the supplied LA-style updates for mast arms, signal poles, the five-section signal head, and lane-control signs.
- Replaced the single visible Channelizer with two catalog entries:
  - Orange Channelizer
  - Gray Channelizer
- Removed the old channelizer model and inventory icon.
- Kept `trafficcontrol:channelizer` as a hidden compatibility alias so existing worlds and inventories do not lose blocks; it now renders with the orange model.
- Added only the five textures required by the supplied models.

No controller, signal timing, municipal sign, camera, detection, or saved-world logic was changed.
