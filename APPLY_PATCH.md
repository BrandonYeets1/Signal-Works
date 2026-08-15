# Apply 3.4.10

1. Back up the project.
2. Extract this patch into the `trafficControl-1.21.1` project root.
3. Allow the included files to overwrite matching files.
4. No files need to be deleted.
5. Run:

```bat
gradlew.bat --stop
gradlew.bat clean build
```

The output is expected at:

```text
build\libs\signalworks-core-2.0.0-alpha.3.4.10.jar
```
