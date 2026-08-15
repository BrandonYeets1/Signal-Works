# Milestone 2.4.1 compile hotfix

Fixes the Java compile error in `RotatablePoleBlock.codec()`.

## Change

The override now returns `MapCodec<? extends RotatablePoleBlock>`, which is covariant with the `HorizontalDirectionalBlock` codec contract in Minecraft 1.21.1.

The deprecation warnings in `ClientModEvents` and the renderer do not stop the build and are left for a later API-cleanup pass.

## Build

```bat
gradlew.bat clean build
```

Expected JAR:

```text
build\libs\trafficcontrol-2.0.0-alpha.2.4.1.jar
```
