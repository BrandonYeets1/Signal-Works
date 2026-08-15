# Milestone 2.6 Testing Checklist

## Build

```bat
gradlew.bat clean build
```

Expected JAR:

`build\libs\trafficcontrol-2.0.0-alpha.2.6.jar`

Remove older Traffic Control alpha JARs before testing.

## Concrete barrier

1. Place one barrier while facing north/south.
2. Turn 90 degrees and place another separately; verify it rotates east/west.
3. Create a straight run of at least four barriers.
4. Verify internal end caps disappear and the outside ends remain closed.
5. Place a barrier beside an existing run while facing the wrong axis; verify it snaps to the run when there is only one neighboring axis.
6. Check that the collision shape follows the visual orientation.

## Pole and mast connections

1. Stack three pole blocks of one width.
2. Click a horizontal side of the top pole with the matching mast item.
3. Verify a branch grows from the pole center to the mast and the mast has a coupling collar.
4. Extend the mast with two more segments along the same axis.
5. Add a perpendicular mast at the middle segment; verify a T-junction appears.
6. Test a 90-degree corner.
7. Repeat with small, medium and large widths.
8. Join two different widths and verify the collar masks the transition.

## Signal head

1. Place a standard signal below a mast segment.
2. Verify the existing hanger still reaches the mast centerline.
3. Repeat with the doghouse and five-section signal.
4. Test a signal directly on top of a pole and a cardinal side-mounted signal.

## Report useful screenshots

- One four-segment concrete barrier run
- One pole-to-mast junction from the side
- One mast T-junction viewed from above
- One signal mounted under the mast
