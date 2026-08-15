# Milestone 2.6.1 Testing Checklist

## Build

```bat
gradlew.bat clean build
```

Expected JAR:

`build\libs\trafficcontrol-2.0.0-alpha.2.6.1.jar`

Remove older Traffic Control alpha JARs before testing.

## Angled signal mounting

1. Place a vertical signal pole.
2. Place a traffic signal in a horizontally adjacent block.
3. Rotate the signal to 45 degrees.
4. Verify a bracket reaches the neighboring pole instead of floating.
5. Repeat at 22.5, 67.5, and cardinal angles.

## Concrete barriers

1. Face north or south and place four barriers left-to-right.
2. Verify they create one continuous east-west run.
3. Verify only the two outside ends remain capped.
4. Face east or west and repeat for a north-south run.
5. Place a new segment beside one end while facing the wrong axis; verify it snaps to the existing run.
6. Confirm the collision box matches the visible direction.

## Mast arms

1. Place a pole and extend a matching mast arm by at least six blocks.
2. Verify the beam is the same width across every straight block boundary.
3. Verify no repeated collar/bulge appears along the straight run.
4. Add a 90-degree branch and a T-junction; verify the conditional branch geometry remains.
