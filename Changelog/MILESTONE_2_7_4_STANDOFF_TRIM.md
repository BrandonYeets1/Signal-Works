# Milestone 2.7.4 — Standoff Trim

This hotfix corrects center/front signal mounting hardware.

## Fixed

- The rear support tube no longer extends through the signal housing.
- North, east, south, and west mounts now terminate at the signal model's actual rear surface.
- Positive-direction mounts use the correct rotated rear plane.
- The mounting plate retains a tiny 0.05-pixel overlap to avoid a visible hairline seam without producing visible clipping.
- Existing overhead drop-pipe mounts are unchanged.
- Inventory icon PNGs are unchanged.

## Build

```bat
gradlew.bat clean build
```

Expected output:

```text
build\libs\trafficcontrol-2.0.0-alpha.2.7.4.jar
```
