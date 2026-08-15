# Milestone 2.4.2 Compile Hotfix

This patch fixes the remaining Java compilation error in `RotatablePoleBlock.java`.

## Fix

Added the missing import:

```java
import net.minecraft.world.level.block.Block;
```

This is required by the method signature:

```java
protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
```

The two deprecation messages shown by Gradle are warnings and do not stop the build.

## Build

```bat
gradlew.bat clean build
```

Expected JAR:

```text
build\libs\trafficcontrol-2.0.0-alpha.2.4.2.jar
```
