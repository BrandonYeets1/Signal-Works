# Signal Works Core 3.4.9 — Gradle Build Fix

Replace the `build.gradle` and `gradle.properties` files in the project root with the files in this patch.

The fix:

- restores `mod_artifact_name=signalworks-core`;
- reads archive properties through Gradle Providers with a fallback to `mod_id`;
- configures the Java 21 toolchain in an explicit `java { toolchain { ... } }` block;
- enforces Java 21 bytecode with `options.release = 21`;
- keeps the internal compatibility mod ID `trafficcontrol`.

From the project directory, run:

```bat
gradlew.bat --stop
gradlew.bat clean build
```

The finished mod should be written to:

```text
build\libs\signalworks-core-2.0.0-alpha.3.4.9.jar
```

Use a full JDK 21, not only a JRE. Check with:

```bat
java -version
javac -version
gradlew.bat --version
```
