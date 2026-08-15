# Milestone 2 Testing Checklist

## Setup

1. Install JDK 21 and confirm `java -version` reports 21.
2. Open this folder as a Gradle project.
3. Run `gradlew.bat runClient` on Windows or `./gradlew runClient` on Linux/macOS.
4. Create a temporary creative-mode world.

## Signal test

1. Open the **Traffic Control** creative tab.
2. Place **Traffic Light (3 Bulb)** while facing several different angles.
3. Confirm the body and illuminated red bulb rotate together.
4. Confirm diagonal placement uses 22.5-degree steps rather than only four cardinal directions.
5. Save and exit the world, then reopen it.
6. Confirm the red/yellow/green configuration and red active state remain.
7. Join from another client or open to LAN when available and confirm the visible state matches.

## Easter egg test

1. Place a pig immediately above or within roughly two blocks above the signal.
2. Wait about one second.
3. Confirm the active bulb face switches to the pig texture.
4. Move the pig away and wait about one second; the normal bulb texture should return.

## Expected alpha limitations

- The signal cannot yet be configured through a GUI.
- The signal does not yet connect visually to horizontal poles.
- Controllers and sensors cannot change its phase yet.
- Other blocks remain nonfunctional placeholders even though their models and registry IDs are present.
- Flashing state exists in code and NBT but is not yet exposed through an in-game controller.

## Reporting a problem

Include:

- `logs/latest.log`
- the complete crash report, if one is generated
- a screenshot of the signal
- the angle at which it was placed
- whether shaders or Sodium/Iris-compatible renderer mods were enabled
