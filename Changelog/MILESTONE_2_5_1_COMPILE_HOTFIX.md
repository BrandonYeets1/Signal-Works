# Milestone 2.5.1 Compile Hotfix

This patch corrects `StreetLightBlockEntityRenderer` after the Milestone 2.5 streetlight variants introduced an invalid BlockEntityRenderer method signature.

## Fixed

- Restored the required six-argument Minecraft 1.21.1 `render(...)` override.
- Removed the accidental `boolean led` and `boolean cutoff` method parameters.
- Kept the renderer's local registry-path detection for LED and cutoff fixtures.
- Resolves all four compile errors reported in `problems-report.html`.

The remaining EventBusSubscriber and deprecated renderer messages are warnings and do not stop compilation.
