# Milestone 3.2.1 — Map Clustering and Click Hotfix

This incremental patch is applied over Milestone 3.2.

## Fixed node selection

Map clicks are now processed before the vanilla container screen consumes clicks inside the controller GUI. The rendered node coordinates and hitboxes use the same controller-local coordinate system, and the clickable radius is larger than the visible square.

## Two-level intersection map

The controller map now has two views:

- **Overview** — signals that become crowded at a distant zoom level collapse into a numbered group node.
- **Focus** — clicking a group recenters and rescales the map around only that group, then displays the individual signal heads.

The focus renderer also spreads heads that share or nearly share the same top-down coordinate so they remain individually visible and clickable.

Use the new **OVERVIEW** button beside the map tab to return from focus mode.

## Version

`trafficcontrol-2.0.0-alpha.3.2.1.jar`
