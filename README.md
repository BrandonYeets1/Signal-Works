# Signal Works Core 3.4.13

## Apply

1. Extract this patch folder anywhere.
2. Open Command Prompt in your Signal Works project root (the folder containing `build.gradle`).
3. Run the patch script by dragging `Apply-SignalWorks-3.4.13.bat` into that window, or copy the patch folder into the project and run the BAT.
4. Build with:

```bat
gradlew.bat clean build
```

The script copies only changed source files, updates `gradle.properties`, preserves Blockbench sources under `src/development/blockbench`, and removes the confirmed duplicate/obsolete runtime folders.
