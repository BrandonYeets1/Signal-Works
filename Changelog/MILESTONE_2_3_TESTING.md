# Milestone 2.3 Testing

## Build

```bat
gradlew.bat clean build
```

Expected JAR:

```text
build\libs\trafficcontrol-2.0.0-alpha.2.3.jar
```

Remove all older Traffic Control alpha JARs before testing.

## Inventory test

1. Open the Traffic Control creative tab.
2. Confirm every visible item uses a flat icon that stays inside its slot.
3. Confirm `light_source` and `traffic_light_5_upper` are no longer visible.

## Ground-face test

1. Make a solid stone or concrete platform.
2. Place a traffic light and both streetlight variants on top.
3. Look directly underneath each renderer anchor.
4. The platform's top face should remain visible with no sky-colored square.

## Automatic intersection test

1. Place one standard `traffic_light` facing north or south.
2. Place a second standard `traffic_light` within 12 blocks facing east or west.
3. Wait up to one second for the scan.
4. The two axes should alternate through green, yellow, and all-red phases.
5. Signals that share an axis should show the same phase.

## Dedicated-lane test

1. Replace one standard head with `traffic_light_5`.
2. Keep at least one functional signal on the crossing axis.
3. Watch a complete cycle.
4. The five-section head should show red plus a green left arrow.
5. It should transition to red plus a yellow arrow.
6. After clearance it should display normal through green and yellow.
