# Signal Works Core 3.4.5

## Geometry lettering

Municipal sign lettering is now generated as colored 5x7 geometry instead of relying on Minecraft's world-font render layer. This removes the renderer failure that left sign faces blank while preserving text color, size, condensed width, bold lettering, reflectivity, uppercase control, and double-sided signs.

## Mast-arm street signs

A municipal sign placed below either a modular mast arm or an adjustable signal arm automatically becomes a single hanging street-name blade.

- Standard signs use thin aluminum-style blades and straps.
- The new Backlit option uses a deeper illuminated cabinet.
- Hanging direction follows the mast-arm axis automatically.
- Existing post-mounted signs remain crossed two-blade assemblies.

## Camera rework

Both existing camera IDs were upgraded in place.

- Traffic detection cameras use a slim bullet-camera housing.
- Red-light cameras use a larger enforcement housing.
- Top placement selects a mast pedestal; side placement selects a wall bracket.
- Five visible tilt presets are available from 45 degrees down to 45 degrees up.
- Detection now uses a directional cone that follows camera facing and tilt.
- Adjuster use rotates a camera; sneak-use changes its tilt.

No textures, sounds, registry IDs, or existing saved-world identifiers were replaced.
